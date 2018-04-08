/**
 *
 */
package fr.tbr.doc.presentation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xhtmlrenderer.pdf.ITextRenderer;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

/**
 * @author tbrou
 *
 */
public class PDFPresenter extends HtmlPresenter {

	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Logger LOGGER = LogManager.getLogger(PDFPresenter.class);

	public byte[] presentAsByte(String htmlSource) {
		try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			String result = new String(super.presentContent(htmlSource), UTF8);
			result = result.replaceAll("\"toc\"", "tocPDF");
			final ITextRenderer renderer = new ITextRenderer();

			renderer.setDocumentFromString(result);
			renderer.layout();

			renderer.createPDF(baos);
			return baos.toByteArray();
		} catch (final Exception e) {
			LOGGER.error("rendering failed", e);
		}
		return new byte[0];

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.tbr.doc.presentation.HtmlPresenter#presentContent(java.lang.String)
	 */
	@Override
	public byte[] presentContent(String htmlSource) {
		return presentAsByte(htmlSource);
	}

	@Override
	protected void generateToc() {
		/* it is necessary to not generate toc in this subclass */
	}

	@Override
	protected void drawDiagrams() {
		final List<Element> codeBlocks = xhtml.getElementsByTagName("code");
		for (final Element element : codeBlocks) {
			final String textContent = element.getTextContent();
			if (textContent.startsWith("@startuml")) {
				final SourceStringReader reader = new SourceStringReader(textContent);
				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				// Write the first image to "os"
				try {
					reader.generateImage(os, new FileFormatOption(FileFormat.PNG));
					os.close();

					final String encode = Base64.getEncoder().encodeToString(os.toByteArray());
					// The XML is stored into svg

					final Element object = xhtml.createEmbeddedImage(null, "data:image/png;base64, " + encode);

					final Node body = xhtml.getBody();
					body.insertBefore(object, element.getParentNode());
					body.removeChild(element.getParentNode());
				} catch (final IOException e) {
					LOGGER.error("error occured", e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.tbr.doc.presentation.HtmlPresenter#getExtension()
	 */
	@Override
	public String getTargetExtension() {
		return "pdf";
	}

}
