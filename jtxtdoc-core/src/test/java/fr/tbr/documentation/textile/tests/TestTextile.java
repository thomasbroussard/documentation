/**
 * 
 */
package fr.tbr.documentation.textile.tests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	public void testBreadCrumbs() throws Exception{
		DocumentMetadata metadata = new DocumentMetadata();
		metadata.setDocumentName("Sous-répertoire");
		DocumentMetadata.toFile(new File("target/subdir/metadata.json"), metadata );
		processBreadCrumbs(new File("target/subdir/tomcat-ssl.textile"),
				new File("target"));
	}

	/**
	 * @param presenter
	 * @param targetDir
	 * @param directory
	 * @throws IOException
	 */
	private void scanForDocumentation(TextilePresenter presenter, File targetDir, File baseDirectory, File directory)
			throws IOException {
		File[] files = directory.listFiles(f -> !f.isDirectory());
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
		String targetFilePath = targetDir.getAbsolutePath() + File.separator + getRelativePath(file, baseDirectory);
		File targetFile = new File(targetFilePath);
		if (!targetFile.getParentFile().exists()) {
			targetFile.mkdirs();
		}

		boolean force = true;

		Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		if (file.getPath().endsWith(".textile")) {
			processInclusions(file, targetFilePath);
			if (targetFile.exists()){
				String md5Content = FileHelper.readFile(new File(targetFilePath.replaceAll(".textile", ".md5")), StandardCharsets.UTF_8);
				if (md5Content != null && ! force){
					if (md5Content.equals(calculateMD5asHexadecimal(targetFile))){
						LOGGER.info("skipping file {}, no modification since last construction", targetFilePath);
						return targetFilePath;
					}
				}
			}
			targetFilePath = targetFilePath.replaceAll(".textile", ".html");
			LOGGER.debug(targetFilePath);
			String presentedFile = presenter.presentFile(targetFile);
			
			FileHelper.writeToFile(targetFilePath, presentedFile);
			FileHelper.writeToFile(targetFilePath.replaceAll(".html", ".md5"), calculateMD5asHexadecimal(targetFile));

		}
		return targetFilePath;
	}

	/**
	 * @param file
	 * @param targetFilePath
	 * @throws IOException
	 */
	private void processInclusions(File file, String targetFilePath) throws IOException {
		String beforeTransformationContent =  FileHelper.readFile(new File(targetFilePath), StandardCharsets.UTF_8);
		String replacedContent = "";
		if (beforeTransformationContent.contains("!include=")){
			//include detected
			Scanner scanner = new Scanner(beforeTransformationContent);
			while (scanner.hasNext()){
				String line = scanner.nextLine();
				String[] split = line.split("\\!include=");
				if (split.length == 1){
					replacedContent+=line + LINE_SEPARATOR;
					continue;
				}
				String includeFilePath = split[1];
				String relativeIncludePath = file.getParentFile().getAbsolutePath() + File.separator + includeFilePath;
				File relativeInclude = new File(relativeIncludePath);
				String includedContent = FileHelper.readFile(relativeInclude, StandardCharsets.UTF_8);
				replacedContent  += includedContent;
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
		String beforeTransformationContent =  FileHelper.readFile(file, StandardCharsets.UTF_8);


		String substract = file.toURI().toString().substring(basePath.toURI().toString().length());
		String[] fileParts = substract.split("/");
		List<String> filePartsAsList = new ArrayList<String>(Arrays.asList(fileParts));
		filePartsAsList.add(0, basePath.toString());
		fileParts = filePartsAsList.toArray(new String[filePartsAsList.size()]);
		
		String breadCrumb = "p.breadcrumb ";
		int i = 0;
		
		String absolutePath = basePath.getAbsolutePath().toString();

		
		while (i < fileParts.length - 1){
			String relativePath = "";
			DocumentMetadata meta =  DocumentMetadata.fromFile(new File(absolutePath + "/metadata.json"));
			for (int j = 0 ; j < fileParts.length - i; j++){
				relativePath += "../";
			}
			breadCrumb += "\"" + meta.getDocumentName() + "\":"+ relativePath + "/" + fileParts[i]+ " > ";
			absolutePath += "/" + fileParts[i];
			i++;
		}
		
		breadCrumb += fileParts[fileParts.length - 1];
		beforeTransformationContent += LINE_SEPARATOR + breadCrumb;
		FileHelper.writeToFile(file.getAbsolutePath(), beforeTransformationContent);

	}

	/**
	 * Computes the path for a file relative to a given base, or fails if the only shared 
	 * directory is the root and the absolute form is better.
	 * 
	 * @param base File that is the base for the result
	 * @param name File to be "relativized"
	 * @return the relative name
	 * @throws IOException if files have no common sub-directories, i.e. at best share the
	 *                     root prefix "/" or "C:\"
	 */

	public static String getRelativePathExt(File base, File name) throws IOException  {
	    File parent = base.getParentFile();

	    if (parent == null) {
	        throw new IOException("No common directory");
	    }

	    String bpath = base.getCanonicalPath();
	    String fpath = name.getCanonicalPath();

	    if (fpath.startsWith(bpath)) {
	        return fpath.substring(bpath.length() + 1);
	    } else {
	        return (".." + File.separator + getRelativePathExt(parent, name));
	    }
	}
	
	@Test
	public void testMD5() {
		File file = new File("src/test/resources/index.textile");
		String digest = calculateMD5asHexadecimal(file);


		System.out.println(digest);

	}

	/**
	 * @param file
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	private String calculateMD5asHexadecimal(File file){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try (InputStream is = Files.newInputStream(file.toPath());
				DigestInputStream dis = new DigestInputStream(is, md)) {
			Scanner scanner = new Scanner(dis);
			while (scanner.hasNext()){
				scanner.next();
			}
			scanner.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] bytes = md.digest();
		BigInteger bi = new BigInteger(1, bytes);
		String digest = String.format("%0" + (bytes.length << 1) + "X", bi);
		return digest;
	}

	// returns null if file isn't relative to folder
	public static String getRelativePath(File file, File folder) {
		String filePath = file.getAbsolutePath();
		String folderPath = folder.getAbsolutePath();
		if (filePath.startsWith(folderPath)) {
			return filePath.substring(folderPath.length() + 1);
		} else {
			return null;
		}
	}

	public static String parseByLanguage(MarkupLanguage language, String wikiText) {

		StringWriter writer = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		MarkupParser parser = new MarkupParser(language, builder);
		parser.parse(wikiText);
		return writer.toString();
	}

}
