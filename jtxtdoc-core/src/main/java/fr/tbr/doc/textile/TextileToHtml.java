/**
 *
 */
package fr.tbr.doc.textile;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * @author tbrou
 *
 */
public class TextileToHtml {

	private static final Logger LOGGER = LogManager.getLogger(TextileToHtml.class);
	private static final MarkupLanguage textileLanguage = new TextileLanguage();
	private static final GenericTextToHtml genericParser = new GenericTextToHtml();


	/**
	 *
	 * @param wikiText
	 * @return
	 */
	public String parse(String wikiText) {

		return genericParser.parse(wikiText, textileLanguage);
	}


	/**
	 *
	 * @param wikiTextFile
	 * @return
	 */
	public String parse(File wikiTextFile) {
		return genericParser.parse(wikiTextFile, textileLanguage);
	}


}
