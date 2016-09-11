/**
 * 
 */
package fr.tbr.doc.textile;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

import fr.tbr.helpers.file.FileHelper;

/**
 * @author tbrou
 *
 */
public class TextileToHtml {
	
	private static final Logger LOGGER = LogManager.getLogger("HelloWorld");
	private static final MarkupLanguage textileLanguage = new TextileLanguage();
	
	public String parse(String wikiText) {

		StringWriter writer = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		MarkupParser parser = new MarkupParser(textileLanguage, builder);
		parser.parse(wikiText);
		return writer.toString();
	}

	
	public String parse(File wikiTextFile) {

		StringWriter writer = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		MarkupParser parser = new MarkupParser(textileLanguage, builder);
		String content = null;
		try{
			content = FileHelper.readFile(wikiTextFile, Charset.forName("UTF-8"));
		}catch(Exception e){
			LOGGER.error("error whil trying to read content from file", e);
		}
		if (content == null){
			return "";
		}
		parser.parse(content);
		return writer.toString();
	}

	
}
