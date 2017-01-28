/**
 * 
 */
package fr.tbr.documentation.textile.tests;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.junit.Test;

import fr.tbr.doc.presentation.TextilePresenter;
import fr.tbr.helpers.file.FileHelper;

/**
 * @author tbrou
 *
 */
public class TestTextile {
	private static final Logger LOGGER = LogManager.getLogger(TestTextile.class);

	@Test
	public void testTextileFromFile() throws IOException {
		TextilePresenter presenter = new TextilePresenter();
		String baseDir = "src/test/resources";
		File targetDir = new File("/target/");
		File directory = new File(baseDir);
		scanForDocumentation(presenter, targetDir, directory,directory);
		
	}

	/**
	 * @param presenter
	 * @param targetDir
	 * @param directory
	 * @throws IOException
	 */
	private void scanForDocumentation(TextilePresenter presenter, File targetDir, File baseDirectory, File directory) throws IOException {
		File[] files = directory.listFiles(f -> f.getPath().endsWith(".textile"));
		File[] subDirs = directory.listFiles(f -> f.isDirectory());
		for (File file : files){
			String targetFilePath = targetDir.getAbsolutePath() + File.separator + getRelativePath(file, baseDirectory);
			File targetFile = new File(targetFilePath);
			if (! targetFile.getParentFile().exists()){
				targetFile.mkdirs();
			}
			
			Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			targetFilePath = targetFilePath.replaceAll(".textile", ".html");
			LOGGER.debug(targetFilePath);
			String presentedFile;
			if (file.getName().contains("index.textile")){
				presentedFile = presenter.presentFile(file);
			}else{
				presentedFile = presenter.presentFile(file);
			}
			FileHelper.writeToFile(targetFilePath, presentedFile);
		}
		for (File file : subDirs){
			scanForDocumentation(presenter, targetDir, baseDirectory, file);
		}
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

	private static String createTextileHelloWorld() {
		StringBuilder sb = new StringBuilder();
		sb.append("h1. Heading 1\n");
		sb.append("\n");
		sb.append("Hello World!\n");
		sb.append("\n");
		sb.append("* Lorem\n");
		sb.append("* Ipsum\n");
		sb.append("\n");
		sb.append("This is *Textile* language.\n");
		return sb.toString();
	}

}
