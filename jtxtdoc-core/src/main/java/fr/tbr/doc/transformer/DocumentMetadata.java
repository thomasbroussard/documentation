/**
 *
 */
package fr.tbr.doc.transformer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
	private Map<String, String> files;
	private boolean active = true;


	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @param string
	 */
	public static DocumentMetadata fromFile(File file) {
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final DocumentMetadata metadata = mapper.readValue(file, DocumentMetadata.class);
			return metadata;
		} catch (final IOException e) {
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
		final ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(file, metadata);
		} catch (final IOException e) {
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

	/**
	 * @return the files
	 */
	public Map<String, String> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(Map<String, String> files) {
		this.files = files;
	}




}
