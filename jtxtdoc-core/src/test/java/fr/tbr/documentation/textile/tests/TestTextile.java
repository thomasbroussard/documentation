/**
 * 
 */
package fr.tbr.documentation.textile.tests;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.junit.Test;

/**
 * @author tbrou
 *
 */
public class TestTextile {
	public static final String NAME_TEXTILE = "Textile";

	@Test
	public void testTextile() {
		System.out.println(parseByLanguage(new TextileLanguage(), createTextileHelloWorld()));
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
