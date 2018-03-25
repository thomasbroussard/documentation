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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.pdf.ITextRenderer;

import fr.tbr.helpers.xml.XMLHelper;
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
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");


	public byte[] presentAsByte(String htmlSource) {
		try {
			String result = new String(super.presentContent(htmlSource), UTF8);
			result = result.replaceAll("\"toc\"", "tocPDF");
			final ITextRenderer renderer = new ITextRenderer();

			renderer.setDocumentFromString(result);
			renderer.layout();
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
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
	protected void generateToc(Document document) {
		super.generateToc(document);
		final NodeList elementsByTagName = document.getElementsByTagName("div");
		final Element toc = (Element) elementsByTagName.item(0);
		toc.getParentNode().removeChild(toc);
	}

	@Override
	protected void drawDiagrams(Document document) {
		final List<Element> codeBlocks = XMLHelper.getElementsFromNodeList(document.getElementsByTagName("code"));
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

					final Element object = document.createElement("img");
					object.setAttribute("src", "data:image/png;base64, " + encode);
					final Node body = document.getElementsByTagName("body").item(0);
					body.insertBefore(object, element.getParentNode());
					body.removeChild(element.getParentNode());
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
