/**
 *
 */
package fr.tbr.documentation.textile.tests;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.junit.Test;

import fr.tbr.doc.parsing.GenericMarkupToHtml;
import fr.tbr.doc.presentation.HtmlPresenter;
import fr.tbr.doc.presentation.PDFPresenter;
import fr.tbr.doc.transformer.DocumentationSourceProcessor;
import fr.tbr.helpers.file.FileHelper;

/**
 * @author tbrou
 *
 */
public class TestTextile {
	/**
	 *
	 */

	private static final Logger LOGGER = LogManager.getLogger(TestTextile.class);


	@Test
	public void processTutorials() throws IOException {
		final String basePath = "src/test/resources";
		final String targetPath = "target";
		final File targetDir = new File(targetPath);
		final File baseDir = new File(basePath);
		final DocumentationSourceProcessor processor = new DocumentationSourceProcessor(targetDir, baseDir);
		processor.setForce(true);
		processor.setProcessBreadCrumbs(true);
		processor.setParser(new GenericMarkupToHtml(new TextileLanguage()));
		processor.setPresenter(new HtmlPresenter());
		processor.setSourceExtensions("textile");
		processor.process();
	}

	@Test
	public void testPDFFromHtmlFile() throws IOException {
		final String basePath = "src/test/resources";
		final String targetPath = "target";
		final File targetDir = new File(targetPath);
		final File baseDir = new File(basePath);
		final DocumentationSourceProcessor processor = new DocumentationSourceProcessor(targetDir, baseDir);
		processor.setForce(true);
		processor.setProcessBreadCrumbs(false);
		processor.setParser(new GenericMarkupToHtml(new TextileLanguage()));
		processor.setPresenter(new PDFPresenter());
		processor.setSourceExtensions("textile");
		processor.process();
	}





	@Test
	public void testMD5() {
		final File file = new File("src/test/resources/index.textile");
		final String digest = FileHelper.calculateMD5asHexadecimal(file);
		System.out.println(digest);

	}

}
