/**
 *
 */
package fr.tbr.documentation.textile.tests;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import fr.tbr.doc.presentation.TextilePresenter;
import fr.tbr.doc.transformer.DocumentationSourceProcessor;
import fr.tbr.helpers.file.FileHelper;

/**
 * @author tbrou
 *
 */
public class TestTextile {
	/**
	 *
	 */

	private static final Logger LOGGER = LogManager.getLogger(TestTextile.class);

	@Test
	public void testTextileFromFile() throws IOException {
		final TextilePresenter presenter = new TextilePresenter();
		final String basePath = "src/test/resources";
		final String targetPath = "target/";
		final File targetDir = new File(targetPath);
		final File baseDir = new File(basePath);
		final DocumentationSourceProcessor processor = new DocumentationSourceProcessor(targetDir, baseDir);
		processor.process(presenter, true);

	}



	@Test
	public void testMD5() {
		final File file = new File("src/test/resources/index.textile");
		final String digest = FileHelper.calculateMD5asHexadecimal(file);
		System.out.println(digest);

	}

}
