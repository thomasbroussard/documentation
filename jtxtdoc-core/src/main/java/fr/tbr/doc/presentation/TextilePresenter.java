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
public class TextilePresenter {
	
	private static final Logger LOGGER = LogManager.getLogger(TextilePresenter.class);

	
	private TextileToHtml parser = new TextileToHtml();
	private HtmlPresenter presenter = new HtmlPresenter();
	
	public String presentFile(File file){
		Date beginDate = new Date();
		LOGGER.debug("beginning transformation from file");
		String html = parser.parse(file);
		
		String formattedHtml = presenter.present(html);
		LOGGER.debug("transformation achieved, took : {} ms", (new Date()).getTime() - beginDate.getTime());
		return formattedHtml;
		
	}

}
