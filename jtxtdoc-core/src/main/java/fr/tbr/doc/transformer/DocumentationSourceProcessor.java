/**
 *
 */
package fr.tbr.doc.transformer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.tbr.doc.presentation.Presenter;
import fr.tbr.helpers.file.FileHelper;

/**
 * @author tbrou
 *
 */
public class DocumentationSourceProcessor {

	private static final Logger LOGGER = LogManager.getLogger(DocumentationSourceProcessor.class);

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");



	private final File baseDirectory;
	private final File targetDir;


	/**
	 * @param targetDir2
	 * @param baseDir
	 */
	public DocumentationSourceProcessor(File targetDir, File baseDir) {
		baseDirectory = baseDir;
		this.targetDir = targetDir;
	}

	public void process(Presenter presenter, boolean force) throws IOException {

		scanForDocumentation(presenter, targetDir, baseDirectory, baseDirectory, force);
	}



	/**
	 * @param presenter
	 * @param targetDir
	 * @param directory
	 * @throws IOException
	 */
	private void scanForDocumentation(Presenter presenter, File targetDir, File baseDirectory, File directory,
			boolean force)
					throws IOException {
		final File[] files = directory.listFiles(f -> !f.isDirectory());
		final File[] subDirs = directory.listFiles(f -> f.isDirectory());
		for (final File file : files) {
			processFile(presenter, targetDir, baseDirectory, file, force);
		}
		for (final File currentSubDir : subDirs) {
			scanForDocumentation(presenter, targetDir, baseDirectory, currentSubDir, force);
			final String targetFilePath = targetDir.getAbsolutePath() + File.separator
					+ FileHelper.getRelativePathExt(baseDirectory, currentSubDir);
			// Files.move(currentSubDir.toPath(), new File(targetFilePath).toPath(),
			// StandardCopyOption.REPLACE_EXISTING);
		}
	}

	/**
	 * @param presenter
	 * @param targetDir
	 * @param baseDirectory
	 * @param file
	 * @throws IOException
	 */
	private String processFile(Presenter presenter, File targetDir, File baseDirectory, File file, boolean force)
			throws IOException {
		String targetFilePath = targetDir.getAbsolutePath() + File.separator
				+ FileHelper.getRelativePathExt(baseDirectory, file);
		final File targetFile = new File(targetFilePath);
		if (!targetFile.getParentFile().exists()) {
			targetFile.mkdirs();
		}

		Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		final String metadataOrig = file.getParentFile().toString() + "/metadata.json";
		final String metadataDest = targetFile.getParentFile().toString() + "/metadata.json";

		final File metadataFile = new File(metadataOrig);
		if (metadataFile.exists()) {
			Files.copy(metadataFile.toPath(), new File(metadataDest).toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		if (file.getPath().endsWith(".textile")) {
			processInclusions(file, targetFilePath);
			processBreadCrumbs(targetFile, targetDir);
			if (targetFile.exists()) {
				final String md5Content = FileHelper.readFile(new File(targetFilePath.replaceAll(".textile", ".md5")),
						StandardCharsets.UTF_8);
				if (md5Content != null && !force) {
					if (md5Content.equals(FileHelper.calculateMD5asHexadecimal(targetFile))) {
						LOGGER.info("skipping file {}, no modification since last construction", targetFilePath);
						return targetFilePath;
					}
				}
			}
			targetFilePath = targetFilePath.replaceAll(".textile", ".html");
			LOGGER.debug(targetFilePath);
			final String presentedFile = presenter.presentFile(targetFile);

			FileHelper.writeToFile(targetFilePath, presentedFile);
			FileHelper.writeToFile(targetFilePath.replaceAll(".html", ".md5"),
					FileHelper.calculateMD5asHexadecimal(targetFile));

		}
		return targetFilePath;
	}

	/**
	 * @param file
	 * @param targetFilePath
	 * @throws IOException
	 */
	private void processInclusions(File file, String targetFilePath) throws IOException {
		final String beforeTransformationContent = FileHelper.readFile(new File(targetFilePath),
				StandardCharsets.UTF_8);
		String replacedContent = "";
		if (beforeTransformationContent.contains("!include=")) {
			// include detected
			final Scanner scanner = new Scanner(beforeTransformationContent);
			while (scanner.hasNext()) {
				final String line = scanner.nextLine();
				final String[] split = line.split("\\!include=");
				if (split.length == 1) {
					replacedContent += line + LINE_SEPARATOR;
					continue;
				}
				final String includeFilePath = split[1];
				final String relativeIncludePath = file.getParentFile().getAbsolutePath() + File.separator
						+ includeFilePath;
				final File relativeInclude = new File(relativeIncludePath);
				final String includedContent = FileHelper.readFile(relativeInclude, StandardCharsets.UTF_8);
				replacedContent += includedContent;
			}
			scanner.close();
			FileHelper.writeToFile(targetFilePath, replacedContent);
		}
	}

	/**
	 * @param file
	 * @param targetFilePath
	 * @throws IOException
	 */
	private void processBreadCrumbs(File file, File basePath) throws IOException {
		String beforeTransformationContent = FileHelper.readFile(file, StandardCharsets.UTF_8);

		final String substract = file.toURI().toString().substring(basePath.toURI().toString().length());
		final String[] fileParts = substract.split("/");
		;

		String breadCrumb = "p(breadcrumb). ";
		int i = 0;

		String absolutePath = basePath.getPath();
		String currentPart = absolutePath;
		while (i < fileParts.length) {
			String relativePath = "";
			DocumentMetadata meta;
			final File metadataFile = new File(absolutePath + "/metadata.json");
			if (!metadataFile.exists()) {
				meta = new DocumentMetadata();
				meta.setDocumentName(fileParts[i]);
			} else {
				meta = DocumentMetadata.fromFile(metadataFile);
			}
			for (int j = 0; j < fileParts.length - i; j++) {
				relativePath += "../";
			}
			breadCrumb += "\"" + meta.getDocumentName() + "\":" + relativePath + currentPart + " > ";

			absolutePath += "/" + fileParts[i];
			currentPart = fileParts[i];

			i++;
		}

		breadCrumb += fileParts[fileParts.length - 1];
		beforeTransformationContent += LINE_SEPARATOR + breadCrumb;
		FileHelper.writeToFile(file.getAbsolutePath(), beforeTransformationContent);

	}

}
