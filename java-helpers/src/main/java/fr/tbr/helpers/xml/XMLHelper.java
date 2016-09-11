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
		List<Element> elements = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength() ; i++){
			Node item = nodeList.item(i);
			if (item instanceof Element){
				elements.add((Element) item);
			}
		}
		return elements;
	}
	
	public static String documentToString(Document document){
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			StringWriter writer = new StringWriter();
			//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			//transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			return writer.getBuffer().toString();
		} catch (TransformerException e) {
			LOGGER.error("error while transforming the output", e);
		}
		return "";
	}
}
