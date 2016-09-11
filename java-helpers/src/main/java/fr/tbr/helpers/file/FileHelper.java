/**
 * 
 */
package fr.tbr.helpers.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.tbr.helpers.string.StringHelper;

/**
 * @author tbrou
 *
 */
public class FileHelper {
	private static final Logger LOGGER = LogManager.getLogger(FileHelper.class); 

	/**
	 * gets the content of a file path
	 * 
	 * @param path
	 *            the path of the file to be read
	 * @param encoding
	 *            the encoding (UTF-8 by default)
	 * @return a string composed of the file content
	 * @throws IOException
	 */
	public static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

	/**
	 * gets the content of a file from a File instance
	 * 
	 * @param file
	 *            the file to be read
	 * @param encoding
	 *            the encoding (UTF-8 by default)
	 * @return a string composed of the file content
	 * @throws IOException
	 */
	public static String readFile(File file, Charset encoding) {
		try {
			byte[] encoded = Files.readAllBytes(file.toPath());
			return new String(encoded, encoding);
		} catch (Exception e) {
			LOGGER.error("error while reading file" , e);
		}
		return "";
	}

	

	/**
	 * gets the content of a file from a File instance
	 * 
	 * @param file
	 *            the file to be read
	 * @param encoding
	 *            the encoding (UTF-8 by default)
	 * @return a string composed of the file content
	 * @throws IOException
	 */
	public static String readFileFromClasspath(Class<?> callingClass, String classPathLocation, Charset encoding) {
		try {
			InputStream is = callingClass.getResourceAsStream(classPathLocation);
			return StringHelper.toString(is);
		} catch (Exception e) {
			LOGGER.error("error while reading file" , e);
		}
		return "";
	}

	/**
	 * @param formattedHtml
	 * @throws IOException 
	 */
	public static void writeToFile(String targetPath, String text) throws IOException {
		Files.write(Paths.get(targetPath), text.getBytes(Charset.forName("UTF-8")), StandardOpenOption.CREATE);
		
	}

}
