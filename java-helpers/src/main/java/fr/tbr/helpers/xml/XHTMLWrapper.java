/**
 * Ce fichier est la propriété de Thomas BROUSSARD
 * Code application :
 * Composant :
 */
package fr.tbr.helpers.xml;

import java.nio.charset.Charset;
import java.util.List;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <h3>Description</h3>
 * <p>
 * This class allows to ...
 * </p>
 *
 * <h3>Usage</h3>
 * <p>
 * This class should be used as follows:
 *
 * <pre>
 * <code>${type_name} instance = new ${type_name}();</code>
 * </pre>
 * </p>
 *
 * @since $${version}
 * @see See also $${link}
 * @author ${user}
 *
 *         ${tags}
 */
public class XHTMLWrapper {

	/**
	 *
	 */
	private final Document document;

	/**
	 * @param document
	 */
	public XHTMLWrapper(Document document) {
		this.document = document;
	}

	/**
	 * @param document
	 */
	public XHTMLWrapper(String string) {
		document = XMLHelper.stringToDocument(string);
	}

	/**
	 * @param document
	 */
	public XHTMLWrapper(String string, Charset charset) {
		document = XMLHelper.stringToDocument(string, charset);
	}

	public Element getBody() {

		return (Element) document.getElementsByTagName("body").item(0);
	}

	public Element getHead() {
		return (Element) document.getElementsByTagName("head").item(0);
	}

	public void createCssLink(String url) {
		// attributes : rel="stylesheet" type="text/css" href="theme.css"
		final Element link = document.createElement("link");
		getHead().appendChild(link);
		link.setAttribute("rel", "stylesheet");
		link.setAttribute("type", "text/css");
		link.setAttribute("href", url);

	}

	public void createScriptFromUrl(String url) {
		// attributes : src ="%url%", type="text/javascript"
		final Element script = document.createElement("script");

		getBody().appendChild(script);
		script.setAttribute("type", "text/javascript");
		script.setAttribute("src", url);
	}

	public void createScriptFromContent(String content) {
		// attributes : src ="%url%", type="text/javascript"
		final Element script = document.createElement("script");
		getBody().appendChild(script);
		script.setAttribute("type", "text/javascript");
		if (content.contains("\"</script>\"")) {
			// protect against </script> closing tag within content
			content = content.replaceAll("\"\\<\\/script\\>\"", "\"</scri\" + \"pt\"");
		}
		// escape content with cdata tag
		final CDATASection section = document.createCDATASection(content);
		script.appendChild(section);
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	public Element createDiv(Element parent, String id, String... classes) {

		final Element div = document.createElement("div");
		if (parent != null) {
			parent.appendChild(div);
		}

		div.setAttribute("id", id);
		handleClasses(div, classes);
		return div;
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	private void handleClasses(final Element div, String... classes) {
		String classesAsString = "";
		if (classes != null) {
			for (final String cssClass : classes) {
				classesAsString += cssClass + " ";
			}
			div.setAttribute("class", classesAsString);
		}
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	public Element createAnchor(Element parent, String id, String href, String textContent, String... classes) {
		final Element elt = createAndAppend(parent, "a");
		handleId(id, elt);
		elt.setAttribute("href", href);
		elt.setTextContent(textContent);
		handleClasses(elt, classes);
		return elt;
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	private void handleId(String id, final Element elt) {
		if (id != null && !id.isEmpty()) {
			elt.setAttribute("id", id);
		}
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	private Element createAndAppend(Element parent, String tagName) {
		final Element elt = document.createElement(tagName);
		if (parent != null) {
			parent.appendChild(elt);
		}
		return elt;
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	public Element createTitle(String textContent) {
		final Element title = createAndAppend(getHead(), "title");
		title.setTextContent(textContent);
		return title;
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	public List<Element> getAllElements() {
		return XMLHelper.getElementsFromNodeList(getDocument().getElementsByTagName("*"));
	}

	public List<Element> getElementsByTagName(String tagName) {
		return XMLHelper.getElementsFromNodeList(getDocument().getElementsByTagName(tagName));
	}

	public String toXhtmlString() {
		String asString = XMLHelper.documentToString(document, "xhtml");
		// protect script and style cdata sections
		asString = asString.replaceAll("\\<\\!\\[CDATA\\[", "/*<![CDATA[*/");
		asString = asString.replaceAll("\\]\\]\\>", "/*]]>*/");
		return asString;
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	public Element addInlineStyle(String string) {
		final Element elt = createAndAppend(getHead(), "style");
		final CDATASection section = document.createCDATASection(string);
		elt.appendChild(section);
		return elt;
	}

	/**
	 * <h3>Description</h3>
	 * <p>
	 * This methods allows to ...
	 * </p>
	 *
	 * <h3>Usage</h3>
	 * <p>
	 * It should be used as follows :
	 *
	 * <pre>
	 * <code> ${enclosing_type} sample;
	 *
	 * //...
	 *
	 * sample.${enclosing_method}();
	 *</code>
	 * </pre>
	 * </p>
	 *
	 * @since $${version}
	 * @see Voir aussi $${link}
	 * @author ${user}
	 *
	 *         ${tags}
	 */
	public Element createEmbeddedImage(Element parent, String string) {
		final Element img = createAndAppend(parent, "img");
		img.setAttribute("src", string);
		return img;
	}

}
