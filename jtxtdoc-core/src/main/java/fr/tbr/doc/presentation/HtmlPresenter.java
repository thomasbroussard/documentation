/**
 *
 */
package fr.tbr.doc.presentation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fr.tbr.helpers.file.FileHelper;
import fr.tbr.helpers.html.HTML_ENTITIES;
import fr.tbr.helpers.xml.XMLHelper;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

/**
 * @author tbrou
 *
 */
public class HtmlPresenter implements Presenter {

	/**
	 *
	 */
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	/**
	 *
	 */
	private static final String HTML_CLASS_LV4 = "lv4";
	/**
	 *
	 */
	private static final String HTML_CLASS_LV3 = "lv3";
	/**
	 *
	 */
	private static final String HTML_CLASS_LV2 = "lv2";

	private static final String HTML_CLASS_LV1 = "lv1";
	/**
	 *
	 */
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Logger LOGGER = LogManager.getLogger(HtmlPresenter.class);

	private enum Stylesheets {
		RESOURCES("jtxtdoc-styles.css"), HIGHLIGHT("default.highlight.css"), CUSTOM("jtxtdoc-global.css");

		private final String resource;

		/**
		 *
		 */
		private Stylesheets(String resource) {
			this.resource = resource;
		}
	}

	@Override
	public byte[] presentContent(String htmlSource) {

		Document document = null;
		try {
			final InputStream is = new ByteArrayInputStream(htmlSource.getBytes(UTF8));
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			final Element body = XMLHelper.runXpathOnDocument("//body", document).get(0);
			final List<Element> bodyChildElements = XMLHelper.getElementsFromNodeList(body.getChildNodes());
			final Element contentDiv = document.createElement("div");
			contentDiv.setAttribute("class", "content");
			for (final Element element : bodyChildElements) {

				contentDiv.appendChild(body.removeChild(element));

			}
			final Element h1 = (Element) contentDiv.getElementsByTagName("h1").item(0);

			final Element headDiv = document.createElement("div");
			headDiv.setAttribute("class", "head");
			final Element header = document.createElement("div");
			header.setAttribute("class", "title");
			headDiv.appendChild(header);
			body.appendChild(headDiv);
			header.setTextContent(h1.getTextContent());
			body.appendChild(contentDiv);
			contentDiv.removeChild(h1);
			generateToc(document);
			importScripts(document);
			importStyleSheets(document);
			drawDiagrams(document);
		} catch (ParserConfigurationException | SAXException | IOException ignored) {
			LOGGER.error("error while generating the presentation", ignored);
		}
		if (document == null) {
			return "".getBytes();
		}
		String string = XMLHelper.documentToString(document, "xhtml");
		// protect script and style cdata sections
		string = string.replaceAll("\\<\\!\\[CDATA\\[", "/*<![CDATA[*/");
		string = string.replaceAll("\\]\\]\\>", "/*]]>*/");

		return string.getBytes(UTF8);

	}

	private void generateBreadCrumbs(Document document) {
		final Element body = (Element) document.getElementsByTagName("body").item(0);
		final Element firstBodyElement = (Element) body.getElementsByTagName("*").item(0);

	}

	/**
	 * Table of content generation This takes a document as a parameter, it should
	 * be well formed HTML.
	 *
	 * @param document
	 */
	protected void generateToc(Document document) {
		int i2 = 0;
		int i3 = 0;
		int i4 = 0;
		int i1 = 0;

		final Element toc = document.createElement("div");
		final NodeList nodeList = document.getElementsByTagName("*");
		final int h1Size = document.getElementsByTagName("h1").getLength();
		final List<Element> elements = XMLHelper.getElementsFromNodeList(nodeList);
		Element titleElement = null;
		for (final Element element : elements) {
			final String tagName = element.getTagName();
			String section = "";
			String className;
			if (HTML_ENTITIES.H1.getEntity().equals(tagName)) {
				if (i1 == 0) {
					titleElement = element;
				}
				++i1;
				if (h1Size > 1) {
					section = String.valueOf(i1);
				} else {
					section = "";
				}

				className = HTML_CLASS_LV1;
				i2 = 0;

			} else if (HTML_ENTITIES.H2.getEntity().equals(tagName)) {
				if (i2 == 0 && titleElement == null) {
					titleElement = element;
				}
				++i2;
				if (h1Size > 1) {
					section = i1 + "-";
				}
				section += String.valueOf(i2);
				className = HTML_CLASS_LV2;
				i3 = 0;

			} else if (HTML_ENTITIES.H3.getEntity().equals(tagName)) {
				++i3;

				if (h1Size > 1) {
					section = i1 + "-";
				}
				section += i2 + "-" + i3;
				className = HTML_CLASS_LV3;
				i4 = 0;

			} else if (HTML_ENTITIES.H4.getEntity().equals(tagName)) {
				++i4;

				if (h1Size > 1) {
					section = i1 + "-";
				}
				section += i2 + "-" + i3 + "-" + i4;
				className = HTML_CLASS_LV4;
			} else {
				continue;
			}
			final String refId = "section-" + section;
			element.setAttribute("id", refId);
			final Element holder = document.createElement(HTML_ENTITIES.DIV.getEntity());
			holder.setAttribute("class", className);
			final Element anchor = document.createElement(HTML_ENTITIES.A.getEntity());
			String formattedSection = section.replaceAll("\\-", ".") + " .";
			if (h1Size == 1 && HTML_ENTITIES.H1.getEntity().equals(tagName)) {
				formattedSection = "";
			}
			anchor.setTextContent(formattedSection + element.getTextContent());
			anchor.setAttribute("href", "#" + refId);
			anchor.setAttribute("class", "scroll");
			toc.appendChild(holder).appendChild(anchor);
		}

		toc.setAttribute("id", "toc");
		final Node content = getContentNode(document);
		final Element title = document.createElement("title");
		if (titleElement != null) {
			title.setTextContent(titleElement.getTextContent());
		}

		final Node head = document.getElementsByTagName(HTML_ENTITIES.HEAD.getEntity()).item(0);
		head.appendChild(title);
		if (i1 <= 1) {
			final Element contentElement = (Element) content;
			contentElement.setAttribute("class", contentElement.getAttribute("class") + " singleTitle");
		}
		content.insertBefore(toc, titleElement);
	}

	/**
	 * <h3>Description</h3>
	 * <p>This methods allows to ...</p>
	 *
	 * <h3>Usage</h3>
	 * <p>It should be used as follows :
	 *
	 * <pre><code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code></pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 * ${tags}
	 */
	private Element getContentNode(Document document) {
		return XMLHelper.runXpathOnDocument("//div[contains(@class,'content')]", document).get(0);
	}

	/**
	 * @param document
	 */
	private void importStyleSheets(Document document) {
		final Node head = document.getElementsByTagName(HTML_ENTITIES.HEAD.getEntity()).item(0);

		final StringBuilder styleContent = new StringBuilder();
		for (final Stylesheets stylesheet : Stylesheets.values()) {
			String string = FileHelper.readFile(new File(stylesheet.resource), UTF8);
			if (string == null || "".equals(string)) {
				string = FileHelper.readFileFromClasspath(HtmlPresenter.class, "/" + stylesheet.resource, UTF8);
			}
			styleContent.append(LINE_SEPARATOR);
			styleContent.append(string);
		}
		importContent(document, head, styleContent.toString(), HTML_ENTITIES.STYLE.getEntity());

	}

	/**
	 * @param document
	 * @param target
	 * @param content,
	 *            String type
	 */
	private Element importContent(Document document, Node target, String content, String type) {
		try {
			final Document newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader("<" + type + ">" + content + "</" + type + ">")));
			final Element newElement = newDoc.getDocumentElement();
			final Node importedNode = document.importNode(newElement, true);
			target.appendChild(importedNode);
			return (Element) importedNode;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			LOGGER.error("error occured while importing content", e);
		}
		return null;
	}

	/**
	 * @param document
	 */
	private void importScripts(Document document) {
		final Node body = document.getElementsByTagName("body").item(0);
		final Element script = document.createElement("script");
		script.setAttribute("src", "http://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.7/highlight.min.js");
		script.setAttribute("type", "text/javascript");
		script.setTextContent(" ");
		body.appendChild(script);
		final String content = "<![CDATA[ " + LINE_SEPARATOR
				+ FileHelper.readFileFromClasspath(HtmlPresenter.class, "/script.js", UTF8) + LINE_SEPARATOR + "]]>";
		importContent(document, body, content, HTML_ENTITIES.SCRIPT.getEntity());
	}


	protected void drawDiagrams(Document document) {
		final List<Element> codeBlocks = XMLHelper.getElementsFromNodeList(document.getElementsByTagName("code"));
		for (final Element element : codeBlocks) {
			final String textContent = element.getTextContent();
			if (textContent.startsWith("@startuml")) {
				final SourceStringReader reader = new SourceStringReader(textContent);
				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				// Write the first image to "os"
				try {
					reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
					os.close();
					// The XML is stored into svg
					final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
					final Document newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
							.parse(new InputSource(new StringReader(svg)));
					final Element newElement = newDoc.getDocumentElement();
					final Node importedNode = document.importNode(newElement, true);
					final Node contentNode = getContentNode(document);
					final Element object = document.createElement("object");
					object.appendChild(importedNode);
					contentNode.insertBefore(object, element.getParentNode());
					contentNode.removeChild(element.getParentNode());
				} catch (final IOException | SAXException | ParserConfigurationException e) {
					LOGGER.error("error", e);
				}

			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.tbr.doc.presentation.Presenter#presentFile(java.io.File)
	 */
	@Override
	public byte[] presentContent(File file) {
		return presentContent(FileHelper.readFile(file, StandardCharsets.UTF_8));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.tbr.doc.presentation.Presenter#getExtension()
	 */
	@Override
	public String getTargetExtension() {
		return "html";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see fr.tbr.doc.presentation.Presenter#getSourceExtension()
	 */
	@Override
	public String getSourceExtension() {
		return "html";
	}

}
