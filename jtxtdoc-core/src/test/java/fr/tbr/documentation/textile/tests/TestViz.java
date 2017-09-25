/**
 *
 */
package fr.tbr.documentation.textile.tests;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import net.sourceforge.plantuml.SourceFileReader;
import net.sourceforge.plantuml.SourceStringReader;



/**
 * @author tbrou
 *
 */
public class TestViz {
	private static final Logger LOGGER = LogManager.getLogger(TestViz.class);


	@Test
	public void testSvgFromPlantUML() throws IOException {

		String source = "@startuml\n";
		source += "Bob -> Alice : hello\n";
		source += "@enduml\n";

		final SourceStringReader reader = new SourceStringReader(source);
		final String desc = reader.generateImage(new File("exampleFromPlant.png"));
		// Return a null string if no generation


	}
	@Test
	public void testSvgFromPlantUMLActivity() throws IOException {

		final SourceFileReader reader = new SourceFileReader(new File("src/test/resources/activity.puml"));

		LOGGER.debug(reader.getGeneratedImages().get(0).getPngFile());
		// Return a null string if no generation


	}

}
