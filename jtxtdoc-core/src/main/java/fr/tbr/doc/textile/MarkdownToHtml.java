/**
 *
 */
package fr.tbr.doc.textile;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.markdown.core.MarkdownLanguage;

/**
 * @author tbrou
 *
 */
public class MarkdownToHtml {

	private static final Logger LOGGER = LogManager.getLogger(MarkdownToHtml.class);
	private static final MarkupLanguage markdown = new MarkdownLanguage();

	private static final GenericTextToHtml genericParser = new GenericTextToHtml();

	/**
	 *
	 * @param wikiText
	 * @return
	 */
	public String parse(String wikiText) {
		return genericParser.parse(wikiText, markdown);
	}


	/**
	 *
	 * @param wikiTextFile
	 * @return
	 */
	public String parse(File wikiTextFile) {
		return genericParser.parse(wikiTextFile, markdown);
	}


}
