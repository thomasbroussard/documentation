/**
 *
 */
package fr.tbr.helpers.xml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author tbrou
 *
 */
public class XMLHelper {

	private static final Logger LOGGER = LogManager.getLogger(XMLHelper.class);


	public static List<Element> getElementsFromNodeList(NodeList nodeList){
		final List<Element> elements = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength() ; i++){
			final Node item = nodeList.item(i);
			if (item instanceof Element){
				elements.add((Element) item);
			}
		}
		return elements;
	}

	public static String documentToString(Document document){
		return documentToString(document, "xml");
	}

	public static String documentToString(Document document, String method) {
		final TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			final StringWriter writer = new StringWriter();
			// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, method);
			transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "script");
			transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "style");
			// transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
			// "2");
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.getBuffer().toString();
		} catch (final TransformerException e) {
			LOGGER.error("error while transforming the output", e);
		}
		return "";
	}


	public static List<Element> runXpathOnDocument(String xpathExpr, Document document){
		final XPathFactory xpathFactory = XPathFactory.newInstance();
		javax.xml.xpath.XPathExpression expr;
		try {
			expr = xpathFactory.newXPath().compile(xpathExpr);
			final Object xpathEval = expr.evaluate(document, XPathConstants.NODESET);
			return getElementsFromNodeList((NodeList) xpathEval);
		} catch (final XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<>();
	}

}
