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
public class HtmlPresenter {

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
	/**
	 * 
	 */
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Logger LOGGER = LogManager.getLogger(HtmlPresenter.class);

	private enum Stylesheets{
		ressources("jtxtdoc-styles.css"),
		highlight("default.highlight.css"),
		custom("jtxtdoc-global.css");
		
		
		private String resource;
		/**
		 * 
		 */
		private Stylesheets(String resource) {
			this.resource = resource;
		}
	}
	
	
	public String present(String htmlSource) {
		
		Document document = null;
		try {
			InputStream is = new ByteArrayInputStream( htmlSource.getBytes( UTF8 ) );
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			generateToc(document);
			importScripts(document);
			importStyleSheets(document);
			drawDiagrams(document);
		} catch (ParserConfigurationException | SAXException |	IOException ignored) {
			LOGGER.error("error while generating the presentation", ignored);
		}
		if (document == null){
			return "";
		}
		return XMLHelper.documentToString(document);

	}
	
	
	/**
	 * Table of content generation
	 * This takes a document as a parameter, it should be well formed HTML.
	 * @param document
	 */
	private void generateToc(Document document){
		int i2 = 0, i3 = 0, i4 = 0;
		
		Element toc = document.createElement("div");
		NodeList nodeList = document.getElementsByTagName("*");
		List<Element> elements = XMLHelper.getElementsFromNodeList(nodeList);
		Element titleElement = null;
		for (Element element : elements) {
			String tagName = element.getTagName();
			String section;
			String className;
			if (HTML_ENTITIES.H2.getEntity().equals(tagName)) {
				if (i2 == 0){
					titleElement = element;
				}
				++i2;
				section = String.valueOf(i2); 
				className = HTML_CLASS_LV2;

			} else if (HTML_ENTITIES.H3.getEntity().equals(tagName)) {
				++i3;
				section = i2 + "." + i3;
				className = HTML_CLASS_LV3;

			} else if (HTML_ENTITIES.H4.getEntity().equals(tagName)) {
				++i4;
				section = i2 + "." + i3 + "." + i4;
				className = HTML_CLASS_LV4;
			} else {
				continue;
			}
			String refId = "section-"+section;
			element.setAttribute("id", refId);
			Element holder = document.createElement(HTML_ENTITIES.DIV.getEntity());
			holder.setAttribute("class", className);
			Element anchor = document.createElement(HTML_ENTITIES.A.getEntity());
			anchor.setTextContent(section +". " + element.getTextContent());
			anchor.setAttribute("href", "#"+refId);
			toc.appendChild(holder).appendChild(anchor);
		}
		toc.setAttribute("id", "toc");
		Node body = document.getElementsByTagName(HTML_ENTITIES.BODY.getEntity()).item(0);
		body.insertBefore(toc, titleElement);
	}


	/**
	 * @param document
	 */
	private void importStyleSheets(Document document) {
		Node head = document.getElementsByTagName(HTML_ENTITIES.HEAD.getEntity()).item(0);
		 

		Element style = document.createElement("style");
		String styleContent= "";
		for (Stylesheets stylesheet : Stylesheets.values()){
			String string = FileHelper.readFile(new File(stylesheet.resource), UTF8);
			if (string == null || "".equals(string)){
				string = FileHelper.readFileFromClasspath(HtmlPresenter.class, "/" + stylesheet.resource, UTF8);
			}
			styleContent += LINE_SEPARATOR + string;
		}
		try {
			Document newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader("<style>"+ styleContent +"</style>")));
			Element newElement = newDoc.getDocumentElement();
			head.appendChild(document.importNode(newElement, true));
		} catch (SAXException | IOException | ParserConfigurationException e) {
		}
		
	}

	/**
	 * @param document
	 */
	private void importScripts(Document document) {
		Node body = document.getElementsByTagName("body").item(0);
		Element script = document.createElement("script");
		script.setAttribute("src","http://cdnjs.cloudflare.com/ajax/libs/highlight.js/8.7/highlight.min.js");
		script.setAttribute("type","text/javascript" );
		script.setTextContent(" ");
		body.appendChild(script);
		script = document.createElement("script");
		script.setTextContent("hljs.initHighlightingOnLoad();");
		script.setAttribute("type","text/javascript" );
		body.appendChild(script);
	}
	
	private void drawDiagrams(Document document){
		List<Element> codeBlocks = XMLHelper.getElementsFromNodeList(document.getElementsByTagName("code"));
		for (Element element : codeBlocks){
			String textContent = element.getTextContent();
			if (textContent.startsWith("@startuml")){
				SourceStringReader reader = new SourceStringReader(textContent);
				final ByteArrayOutputStream os = new ByteArrayOutputStream();
				// Write the first image to "os"
				try {
					String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
					os.close();

					// The XML is stored into svg
					final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
					Document newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(svg)));
					Element newElement = newDoc.getDocumentElement();
					Node importedNode = document.importNode(newElement, true);
					Node body = document.getElementsByTagName("body").item(0);
					Element object = document.createElement("object");
					object.appendChild(importedNode);
					body.insertBefore(object,element.getParentNode());
					body.removeChild(element.getParentNode());
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}
	}

}
