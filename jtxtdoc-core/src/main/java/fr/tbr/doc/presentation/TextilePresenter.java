/**
 *
 */
package fr.tbr.doc.presentation;

import java.io.File;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.tbr.doc.textile.TextileToHtml;

/**
 * @author tbrou
 *
 */
public class TextilePresenter implements Presenter {

	private static final Logger LOGGER = LogManager.getLogger(TextilePresenter.class);


	private final TextileToHtml parser = new TextileToHtml();
	private final HtmlPresenter presenter = new HtmlPresenter();

	@Override
	public String presentFile(File file){
		final Date beginDate = new Date();
		LOGGER.debug("beginning transformation from file");
		final String html = parser.parse(file);

		final String formattedHtml = presenter.present(html);
		LOGGER.debug("transformation achieved, took : {} ms", new Date().getTime() - beginDate.getTime());
		return formattedHtml;

	}

}
