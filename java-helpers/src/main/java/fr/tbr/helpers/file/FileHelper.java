/**
 *
 */
package fr.tbr.helpers.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

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
		final byte[] encoded = Files.readAllBytes(Paths.get(path));
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
			final byte[] encoded = Files.readAllBytes(file.toPath());
			return new String(encoded, encoding);
		} catch (final Exception e) {
			LOGGER.warn("error while reading file" );
			LOGGER.trace("error", e);
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
			final InputStream is = callingClass.getResourceAsStream(classPathLocation);
			return StringHelper.toString(is);
		} catch (final Exception e) {
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

	/**
	 * @param formattedHtml
	 * @throws IOException
	 */
	public static void writeToFile(String targetPath, byte[] text) throws IOException {
		Files.write(Paths.get(targetPath), text, StandardOpenOption.CREATE);

	}

	/**
	 * Calculates an MD5 checkSum
	 * 
	 * @param file
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static String calculateMD5asHexadecimal(File file){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (final NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try (InputStream is = Files.newInputStream(file.toPath());
				DigestInputStream dis = new DigestInputStream(is, md)) {
			final Scanner scanner = new Scanner(dis);
			while (scanner.hasNext()){
				scanner.next();
			}
			scanner.close();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final byte[] bytes = md.digest();
		final BigInteger bi = new BigInteger(1, bytes);
		final String digest = String.format("%0" + (bytes.length << 1) + "X", bi);
		return digest;
	}


	/**
	 * Computes the path for a file relative to a given base, or fails if the only shared
	 * directory is the root and the absolute form is better.
	 *
	 * @param base File that is the base for the result
	 * @param name File to be "relativized"
	 * @return the relative name
	 * @throws IOException if files have no common sub-directories, i.e. at best share the
	 *                     root prefix "/" or "C:\"
	 */

	public static String getRelativePathExt(File base, File name) throws IOException  {
		final File parent = base.getParentFile();

		if (parent == null) {
			throw new IOException("No common directory");
		}

		final String bpath = base.toURI().toString();
		final String fpath = name.toURI().toString();

		if (fpath.startsWith(bpath)) {
			return fpath.substring(bpath.length());
		} else {
			return ".." + File.separator + getRelativePathExt(parent, name);
		}
	}
}
