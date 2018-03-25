/**
 *
 */
package fr.tbr.doc.transformer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.tbr.doc.parsing.Parser;
import fr.tbr.doc.presentation.Presenter;
import fr.tbr.helpers.file.FileHelper;

/**
 * @author tbrou
 *
 */
public class DocumentationSourceProcessor {

	/**
	 *
	 */
	private static final String METADATA_JSON = "/metadata.json";

	private static final Logger LOGGER = LogManager.getLogger(DocumentationSourceProcessor.class);

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private final File baseDirectory;
	private final File targetDir;

	private Presenter presenter;
	private Parser parser;

	private boolean force;

	private boolean processBreadCrumbs;

	private String[] sourceExtension;

	/**
	 * @param targetDir2
	 * @param baseDir
	 */
	public DocumentationSourceProcessor(File targetDir, File baseDir) {
		baseDirectory = baseDir;
		this.targetDir = targetDir;
	}

	public void process() throws IOException {

		scanForDocumentation(presenter, targetDir, baseDirectory, baseDirectory, force);
	}

	/**
	 * @param presenter
	 * @param targetDir
	 * @param directory
	 * @throws IOException
	 */
	private void scanForDocumentation(Presenter presenter, File targetDir, File baseDirectory, File directory,
			boolean force) throws IOException {
		final File[] files = directory.listFiles(f -> !f.isDirectory());
		final File[] subDirs = directory.listFiles(f -> f.isDirectory());
		for (final File file : files) {
			processFile(presenter, targetDir, baseDirectory, file);
		}
		for (final File currentSubDir : subDirs) {
			scanForDocumentation(presenter, targetDir, baseDirectory, currentSubDir, force);
		}
	}

	/**
	 * @param presenter
	 * @param targetDir
	 * @param baseDirectory
	 * @param file
	 * @throws IOException
	 */
	private String processFile(Presenter presenter, File targetDir, File baseDirectory, File file) throws IOException {
		String targetFilePath = targetDir.getAbsolutePath() + File.separator
				+ FileHelper.getRelativePathExt(baseDirectory, file);
		final File targetFile = new File(targetFilePath);
		if (!targetFile.getParentFile().exists()) {
			targetFile.mkdirs();
		}

		Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		final String metadataOrig = file.getParentFile().toString() + METADATA_JSON;
		final String metadataDest = targetFile.getParentFile().toString() + METADATA_JSON;

		final File metadataFile = new File(metadataOrig);
		if (metadataFile.exists()) {
			Files.copy(metadataFile.toPath(), new File(metadataDest).toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		if (file.getPath().endsWith(".textile")) {
			processInclusions(file, targetFilePath);
			if (processBreadCrumbs) {
				processBreadCrumbs(targetFile, targetDir);
			}
			final String calculateMD5asHexadecimal = FileHelper.calculateMD5asHexadecimal(targetFile);
			if (targetFile.exists()) {
				final String md5Content = FileHelper.readFile(
						new File(targetFilePath.replaceAll(presenter.getSourceExtension(), ".md5")),
						StandardCharsets.UTF_8);
				final boolean noChange = calculateMD5asHexadecimal.equals(md5Content);
				if (md5Content != null && !force && noChange) {
					LOGGER.info("skipping file {}, no modification since last construction", targetFilePath);
					return targetFilePath;
				}
			}
			for (final String ext : sourceExtension) {
				targetFilePath = targetFilePath.replaceAll("\\." + ext, "." + presenter.getTargetExtension());

			}
			LOGGER.debug(targetFilePath);
			final String rawContent = parser.parse(targetFile);
			final byte[] presentedFile = presenter.presentContent(rawContent);

			FileHelper.writeToFile(targetFilePath, presentedFile);
			FileHelper.writeToFile(targetFilePath.replaceAll(presenter.getTargetExtension(), "md5"),
					calculateMD5asHexadecimal);

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
		final StringBuilder replacedContent = new StringBuilder();
		if (beforeTransformationContent.contains("!include=")) {
			// include detected
			final Scanner scanner = new Scanner(beforeTransformationContent);
			while (scanner.hasNext()) {
				final String line = scanner.nextLine();
				final String[] split = line.split("\\!include=");
				if (split.length == 1) {
					replacedContent.append(line);
					replacedContent.append(LINE_SEPARATOR);
					continue;
				}
				final String includeFilePath = split[1];
				final String relativeIncludePath = file.getParentFile().getAbsolutePath() + File.separator
						+ includeFilePath;
				final File relativeInclude = new File(relativeIncludePath);
				final String includedContent = FileHelper.readFile(relativeInclude, StandardCharsets.UTF_8);
				replacedContent.append(includedContent);
			}
			scanner.close();
			FileHelper.writeToFile(targetFilePath, replacedContent.toString());
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

		final StringBuilder breadCrumb = new StringBuilder("p(breadcrumb). ");
		int i = 0;

		final StringBuilder absolutePath = new StringBuilder(basePath.getPath());
		String currentPart = "";
		while (i < fileParts.length - 1) {
			absolutePath.append("/" + fileParts[i]);
			currentPart = fileParts[i];
			final StringBuilder relativePath = new StringBuilder();
			DocumentMetadata meta;
			meta = getMetaData(fileParts[i], absolutePath.toString());
			for (int j = 0; j < fileParts.length - 1 - i; j++) {
				relativePath.append("../");
			}
			if (meta.isActive()) {
				breadCrumb.append("\"" + meta.getDocumentName() + "\":" + relativePath + currentPart + " > ");
			} else {
				breadCrumb.append(meta.getDocumentName() + " > ");
			}

			i++;
		}
		final String filePart = fileParts[fileParts.length - 1];
		final DocumentMetadata metadata = getMetaData(filePart, file.getParentFile().getAbsolutePath());
		final Map<String, String> files = metadata.getFiles();
		String displayedFileName = filePart;
		if (files != null) {
			displayedFileName = files.get(filePart);
		}
		if (filePart != null) {
			breadCrumb.append(displayedFileName == null ? filePart.split("\\.")[0] : displayedFileName);
		}
		beforeTransformationContent += LINE_SEPARATOR + breadCrumb;
		FileHelper.writeToFile(file.getAbsolutePath(), beforeTransformationContent);

	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	private DocumentMetadata getMetaData(String filePart, String absolutePath) {
		DocumentMetadata meta;
		final File metadataFile = new File(absolutePath + METADATA_JSON);
		if (!metadataFile.exists()) {
			meta = new DocumentMetadata();
			meta.setDocumentName(filePart);
		} else {
			meta = DocumentMetadata.fromFile(metadataFile);
		}
		return meta;
	}

	/**
	 * @return the processBreadCrumbs
	 */
	public boolean isProcessBreadCrumbs() {
		return processBreadCrumbs;
	}

	/**
	 * @param processBreadCrumbs
	 *            the processBreadCrumbs to set
	 */
	public void setProcessBreadCrumbs(boolean processBreadCrumbs) {
		this.processBreadCrumbs = processBreadCrumbs;
	}

	/**
	 * @return the force
	 */
	public boolean isForce() {
		return force;
	}

	/**
	 * @param force
	 *            the force to set
	 */
	public void setForce(boolean force) {
		this.force = force;
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	public void setParser(Parser parser) {
		this.parser = parser;
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	public void setSourceExtensions(String... extensions) {
		sourceExtension = extensions;
	}

}
