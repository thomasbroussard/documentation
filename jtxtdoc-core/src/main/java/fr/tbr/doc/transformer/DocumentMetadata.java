/**
 * 
 */
package fr.tbr.doc.transformer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author tbrou
 *
 */
public class DocumentMetadata {
	
	
	private String documentName;
	private DocumentMetadata parent;



	private List<String> tags;
	private DocumentMetadata children;
	
	
	/**
	 * @param string
	 */
	public static DocumentMetadata fromFile(File file) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			DocumentMetadata metadata = mapper.readValue(file, DocumentMetadata.class);
			return metadata;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @return the documentName
	 */
	public String getDocumentName() {
		return documentName;
	}

	/**
	 * @param documentName the documentName to set
	 */
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public static void toFile(File file, DocumentMetadata metadata){
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(file, metadata);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public DocumentMetadata() {
		// TODO Auto-generated constructor stub
	}
	

	

}
