package fr.tbr.helpers.string;
import java.io.InputStream;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author tbrou
 *
 */
public class StringHelper{
	
	public static String toString(InputStream inputStream){
		Scanner scanner = new Scanner(inputStream);
		Scanner s = scanner.useDelimiter("\\A");
		String result = s.hasNext() ? s.next() : "";
		scanner.close();
		s.close();
		return result;
	}

}
