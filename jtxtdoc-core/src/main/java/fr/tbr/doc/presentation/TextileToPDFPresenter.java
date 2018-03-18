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
public class TextileToPDFPresenter implements Presenter {

	private static final Logger LOGGER = LogManager.getLogger(TextileToPDFPresenter.class);


	private final TextileToHtml parser = new TextileToHtml();
	private final PDFPresenter presenter = new PDFPresenter();

	@Override
	public byte[] presentFile(File file) {
		final Date beginDate = new Date();
		LOGGER.debug("beginning transformation from file");
		final String html = parser.parse(file);

		final byte[] formattedHtml = presenter.presentAsByte(html);
		LOGGER.debug("transformation achieved, took : {} ms", new Date().getTime() - beginDate.getTime());
		return formattedHtml;

	}

	@Override
	public String getTargetExtension() {
		return "pdf";
	}

	@Override
	public String getSourceExtension() {
		return "textile";
	}

}
