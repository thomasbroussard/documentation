/**
 *
 */
package fr.tbr.doc.transformer;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author tbrou
 *
 */
public class DocumentMetadata {

	private static final Logger LOGGER = LogManager.getLogger(DocumentMetadata.class);


	private String documentName;
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
			return mapper.readValue(file, DocumentMetadata.class);
		} catch (final IOException e) {
			LOGGER.error("error", e);
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
			LOGGER.error("error", e);
		}
	}

	/**
	 *
	 */
	public DocumentMetadata() {
		// default constructor
	}

	/**
	 * @return the files
	 */
	public Map<String, String> getFiles() {
		return files;
	}

	/**
	 * @param files
	 *            the files to set
	 */
	public void setFiles(Map<String, String> files) {
		this.files = files;
	}




}
