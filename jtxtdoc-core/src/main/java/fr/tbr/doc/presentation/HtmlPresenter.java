/**
 *
 */
package fr.tbr.doc.presentation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import fr.tbr.helpers.file.FileHelper;
import fr.tbr.helpers.html.HTML_ENTITIES;
import fr.tbr.helpers.xml.XHTMLWrapper;
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
	private static final String CLASS = "class";
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

	private final Set<String> stylesheetUrls = new LinkedHashSet<>();
	private final Set<String> stylesheetContents = new LinkedHashSet<>();
	private final Set<String> scriptUrls = new LinkedHashSet<>();
	private final Set<String> scriptContents = new LinkedHashSet<>();
	protected XHTMLWrapper xhtml;

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

		xhtml = new XHTMLWrapper(htmlSource);
		final Element body = xhtml.getBody();

		final Element contentDiv = xhtml.createDiv(null, "content", "content");
		XMLHelper.moveChildren(body, contentDiv);

		final Element h1 = (Element) contentDiv.getElementsByTagName("h1").item(0);
		final Element headDiv = xhtml.createDiv(body, "headDiv", "head");
		final Element header = xhtml.createDiv(headDiv, "titleDiv", "title");
		if (h1 != null) {
			final String textContent = h1.getTextContent();
			header.setTextContent(textContent);
			xhtml.createTitle(textContent);
		}

		body.appendChild(contentDiv);
		contentDiv.removeChild(h1);
		generateToc();
		importScripts();
		importStyleSheets();
		drawDiagrams();
		processImages();

		final String string = xhtml.toXhtmlString();


		return string.getBytes(UTF8);

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
	private void processImages() {
		final List<Element> images = xhtml.getElementsByTagName("img");
		for (final Element elt : images) {
			final Document document = xhtml.getDocument();
			final Element figure = document.createElement("figure");

			final String title = elt.getAttribute("title");
			final Element body = xhtml.getBody();
			body.appendChild(figure);
			elt.getParentNode().replaceChild(figure, elt);
			figure.appendChild(elt);
			if (title != null && !"".equals(title)) {
				final Element caption = document.createElement("figcaption");
				caption.setTextContent(title);
				figure.appendChild(caption);
			}




		}

	}

	/**
	 * Table of content generation This takes a document as a parameter, it should
	 * be well formed HTML.
	 *
	 * @param document
	 */
	protected void generateToc() {
		int i2 = 0;
		int i3 = 0;
		int i4 = 0;
		int i1 = 0;

		final Element toc = xhtml.createDiv(null, "toc", "toc");
		final int h1Size = xhtml.getElementsByTagName("h1").size();
		final List<Element> elements = xhtml.getAllElements();
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
			final Element holder = xhtml.createDiv(toc, "menuHolder");
			holder.setAttribute(CLASS, className);

			String formattedSection = section.replaceAll("\\-", ".") + ". ";
			if (h1Size == 1 && HTML_ENTITIES.H1.getEntity().equals(tagName)) {
				formattedSection = "";
			}
			xhtml.createAnchor(holder, "anchor-" + refId, "#" +refId,
					formattedSection + element.getTextContent(), "scroll");

		}



		final Element content = getContentNode(xhtml.getDocument());
		if (i1 <= 1) {
			content.setAttribute(CLASS, content.getAttribute(CLASS) + " singleTitle");
		}
		content.insertBefore(toc, titleElement);
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to get the "content" element, the one which contains all
	 * the presentation
	 * </p>
	 * ${tags}
	 */
	private Element getContentNode(Document document) {
		return XMLHelper.runXpathOnDocument("//div[contains(@class,'content')]", document).get(0);
	}

	/**
	 */
	private void importStyleSheets() {
		final StringBuilder styleContent = new StringBuilder();
		// no configured content, fall back to default
		if (stylesheetContents.isEmpty() && stylesheetUrls.isEmpty()) {
			for (final Stylesheets stylesheet : Stylesheets.values()) {
				String string = FileHelper.readFile(new File(stylesheet.resource), UTF8);
				if (string == null || "".equals(string)) {
					string = FileHelper.readFileFromClasspath(HtmlPresenter.class, "/" + stylesheet.resource, UTF8);
				}
				styleContent.append(LINE_SEPARATOR);
				styleContent.append(string);
			}
		} else {
			for (final String stylesheet : stylesheetContents) {
				styleContent.append(stylesheet);
				styleContent.append(LINE_SEPARATOR);
			}
			for (final String stylesheetUrl : stylesheetUrls) {
				xhtml.createCssLink(stylesheetUrl);
			}
		}
		xhtml.addInlineStyle(styleContent.toString());

	}

	private void importScripts() {
		final StringBuilder scriptContent = new StringBuilder();
		// no configured content, fall back to default
		if (scriptContents.isEmpty() && scriptUrls.isEmpty()) {
			scriptContent.append(LINE_SEPARATOR);
			scriptContent.append(FileHelper.readFileFromClasspath(HtmlPresenter.class, "/script.js", UTF8));
			scriptContent.append(LINE_SEPARATOR);

		} else {
			for (final String script : scriptContents) {
				scriptContent.append(script);
				scriptContent.append(LINE_SEPARATOR);
			}
			for (final String stylesheetUrl : scriptUrls) {
				xhtml.createScriptFromUrl(stylesheetUrl);
			}
		}
		xhtml.createScriptFromContent(scriptContent.toString());

	}


	protected void drawDiagrams() {
		final List<Element> codeBlocks = xhtml.getElementsByTagName("code");
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
					final Document document = xhtml.getDocument();
					final Node importedNode = document.importNode(newElement, true);
					final Node contentNode = getContentNode(document);
					final Element object = document.createElement("object");
					final String classes = ((Element) element.getParentNode()).getAttribute("class");
					final Element figure = document.createElement("figure");
					final Element body = xhtml.getBody();
					body.appendChild(figure);
					figure.appendChild(object);
					if (classes != null && !classes.isEmpty()) {
						final Element caption = document.createElement("figcaption");
						caption.setTextContent(classes);
						figure.appendChild(caption);
					}


					object.appendChild(importedNode);
					contentNode.insertBefore(figure, element.getParentNode());
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
	 * (non-Javadoc) !
	 * @see fr.tbr.doc.presentation.Presenter#getSourceExtension()
	 */
	@Override
	public String getSourceExtension() {
		return "html";
	}

	public void addScriptFromContent(String content) {
		scriptContents.add(content);
	}

	public void addScriptFromUrl(String resource) {
		scriptUrls.add(resource);
	}

	public void addScriptFromContent(File contentFile) {
		scriptContents
		.add(FileHelper.readFile(contentFile, StandardCharsets.UTF_8));
	}

	public void addStyleFromContent(String content) {
		stylesheetContents.add(content);
	}

	public void addStyleFromContent(File contentFile) {
		stylesheetContents
		.add(FileHelper.readFile(contentFile, StandardCharsets.UTF_8));
	}

	public void addStyleFromUrl(String resource) {
		stylesheetUrls.add(resource);
	}

}
