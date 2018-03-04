/**
 * Ce fichier est la propriété de Thomas BROUSSARD
 * Code application :
 * Composant :
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

import fr.tbr.helpers.file.FileHelper;

/**
 * <h3>Description</h3>
 * <p>This class allows to ...</p>
 *
 * <h3>Usage</h3>
 * <p>This class should be used as follows:
 *   <pre><code>${type_name} instance = new ${type_name}();</code></pre>
 * </p>
 *
 * @since $${version}
 * @see See also $${link}
 * @author ${user}
 *
 * ${tags}
 */
public class GenericTextToHtml {

	private static final Logger LOGGER = LogManager.getLogger(MarkdownToHtml.class);

	/**
	 *
	 * @param wikiText
	 * @return
	 */
	public String parse(String wikiText, MarkupLanguage mkLanguage) {

		final StringWriter writer = new StringWriter();
		final HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		final MarkupParser parser = new MarkupParser(mkLanguage, builder);
		parser.parse(wikiText);
		return writer.toString();
	}

	/**
	 *
	 * @param wikiTextFile
	 * @return
	 */
	public String parse(File wikiTextFile, MarkupLanguage mkLanguage) {

		final StringWriter writer = new StringWriter();
		final HtmlDocumentBuilder builder = new HtmlDocumentBuilder(writer);
		final MarkupParser parser = new MarkupParser(mkLanguage, builder);
		String content = null;
		try {
			content = FileHelper.readFile(wikiTextFile, Charset.forName("UTF-8"));
		} catch (final Exception e) {
			LOGGER.error("error while trying to read content from file", e);
		}
		if (content == null) {
			return "";
		}
		parser.parse(content);
		return writer.toString();
	}

}
