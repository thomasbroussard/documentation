/**
 * 
 */
package fr.tbr.documentation.textile.tests;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.junit.Test;

import fr.tbr.doc.presentation.HtmlPresenter;
import fr.tbr.doc.textile.TextileToHtml;
import fr.tbr.helpers.file.FileHelper;

/**
 * @author tbrou
 *
 */
public class TestTextile {
	private static final Logger LOGGER = LogManager.getLogger(TestTextile.class);

	@Test
	public void testTextileFromFile() throws IOException {
		LOGGER.debug("beginning transformation from file");
		TextileToHtml parser = new TextileToHtml();
		Date beginDate = new Date();
		String rawHtml = parser.parse(new File("src/test/resources/tomcat-ssl.textile"));
		LOGGER.debug("transformation achieved, took : {} ms", (new Date()).getTime() - beginDate.getTime());
//		LOGGER.info(rawHtml);
		HtmlPresenter presenter = new HtmlPresenter();
		String formattedHtml = presenter.present(rawHtml);
//		LOGGER.info(formattedHtml);
		FileHelper.writeToFile("target/essai.html", formattedHtml);
		
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
