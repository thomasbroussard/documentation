/**
 * 
 */
package fr.tbr.documentation.textile.tests;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.junit.Test;

import fr.tbr.doc.presentation.TextilePresenter;
import fr.tbr.doc.transformer.DocumentMetadata;
import fr.tbr.helpers.file.FileHelper;

/**
 * @author tbrou
 *
 */
public class TestTextile {
	/**
	 * 
	 */
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final Logger LOGGER = LogManager.getLogger(TestTextile.class);

	@Test
	public void testTextileFromFile() throws IOException {
		TextilePresenter presenter = new TextilePresenter();
		String basePath = "src/test/resources";
		String targetPath = "target/";
		File targetDir = new File(targetPath);
		File baseDir = new File(basePath);
		scanForDocumentation(presenter, targetDir, baseDir, baseDir);

	}

	@Test
	public void testTextileIncludeFromFile() throws IOException {
		TextilePresenter presenter = new TextilePresenter();
		processFile(presenter, new File("target/"), new File("src/test/resources"),
				new File("src/test/resources/index.textile"));

		processFile(presenter, new File("target/"), new File("src/test/resources"),
				new File("src/test/resources/generate-cert.textile"));

	}

	@Test
	public void testBreadCrumbs() throws Exception {
		DocumentMetadata metadata = new DocumentMetadata();
		metadata.setDocumentName("Sous-répertoire");
		DocumentMetadata.toFile(new File("target/subdir/metadata.json"), metadata);
		processBreadCrumbs(new File("target/subdir/tomcat-ssl.textile"), new File("target"));
	}

	/**
	 * @param presenter
	 * @param targetDir
	 * @param directory
	 * @throws IOException
	 */
	private void scanForDocumentation(TextilePresenter presenter, File targetDir, File baseDirectory, File directory)
			throws IOException {
		File[] files = directory.listFiles(f -> !f.isDirectory() && f.toString().endsWith(".textile"));
		File[] subDirs = directory.listFiles(f -> f.isDirectory());
		for (File file : files) {
			processFile(presenter, targetDir, baseDirectory, file);
		}
		for (File currentSubDir : subDirs) {
			scanForDocumentation(presenter, targetDir, baseDirectory, currentSubDir);
		}
	}

	/**
	 * @param presenter
	 * @param targetDir
	 * @param baseDirectory
	 * @param file
	 * @throws IOException
	 */
	private String processFile(TextilePresenter presenter, File targetDir, File baseDirectory, File file)
			throws IOException {
		String targetFilePath = targetDir.getAbsolutePath() + File.separator
				+ FileHelper.getRelativePathExt(baseDirectory, file);
		File targetFile = new File(targetFilePath);
		if (!targetFile.getParentFile().exists()) {
			targetFile.mkdirs();
		}

		boolean force = true;

		Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		String metadataOrig = file.getParentFile().toString() + "/metadata.json";
		String metadataDest = targetFile.getParentFile().toString() + "/metadata.json";
		Files.copy(new File(metadataOrig).toPath(), new File(metadataDest).toPath(), StandardCopyOption.REPLACE_EXISTING);
		if (file.getPath().endsWith(".textile")) {
			processInclusions(file, targetFilePath);
			processBreadCrumbs(targetFile, targetDir);
			if (targetFile.exists()) {
				String md5Content = FileHelper.readFile(new File(targetFilePath.replaceAll(".textile", ".md5")),
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
			String presentedFile = presenter.presentFile(targetFile);

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
		String beforeTransformationContent = FileHelper.readFile(new File(targetFilePath), StandardCharsets.UTF_8);
		String replacedContent = "";
		if (beforeTransformationContent.contains("!include=")) {
			// include detected
			Scanner scanner = new Scanner(beforeTransformationContent);
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				String[] split = line.split("\\!include=");
				if (split.length == 1) {
					replacedContent += line + LINE_SEPARATOR;
					continue;
				}
				String includeFilePath = split[1];
				String relativeIncludePath = file.getParentFile().getAbsolutePath() + File.separator + includeFilePath;
				File relativeInclude = new File(relativeIncludePath);
				String includedContent = FileHelper.readFile(relativeInclude, StandardCharsets.UTF_8);
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

		String substract = file.toURI().toString().substring(basePath.toURI().toString().length());
		String[] fileParts = substract.split("/");;

		String breadCrumb = "p(breadcrumb). ";
		int i = 0;

		String absolutePath = basePath.getPath();
		String currentPart = absolutePath;
		while (i < fileParts.length) {
			String relativePath = "";

			DocumentMetadata meta = DocumentMetadata.fromFile(new File(absolutePath + "/metadata.json"));
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

	@Test
	public void testMD5() {
		File file = new File("src/test/resources/index.textile");
		String digest = FileHelper.calculateMD5asHexadecimal(file);
		System.out.println(digest);

	}

	public static String parseByLanguage(MarkupLanguage language, String wikiText) {

		StringWriter writer = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		MarkupParser parser = new MarkupParser(language, builder);
		parser.parse(wikiText);
		return writer.toString();
	}

}
