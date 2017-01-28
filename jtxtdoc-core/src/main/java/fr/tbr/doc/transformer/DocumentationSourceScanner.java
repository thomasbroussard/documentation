/**
 * 
 */
package fr.tbr.doc.transformer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author tbrou
 *
 */
public class DocumentationSourceScanner {

	
	
	
	public List<DocumentMetadata> scanForDocumentation(String rootPath){
		List<DocumentMetadata> result = new ArrayList<>();
		if (rootPath == null){
			return result;
		}
		File file = new File(rootPath);
		if (! file.exists()){
			return result;
		}
		
		List<File> documents = Arrays.asList(file.listFiles(f -> !file.isDirectory()));
		
		return result;
	}
	
	private List<DocumentMetadata> buildMetadataForLevel(String relativeRoot, int depth){
		
		return new ArrayList<DocumentMetadata>();
	}
}
