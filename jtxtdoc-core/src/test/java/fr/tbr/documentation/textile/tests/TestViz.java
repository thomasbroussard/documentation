/**
 * 
 */
package fr.tbr.documentation.textile.tests;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Factory;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Serializer;
import net.sourceforge.plantuml.SourceFileReader;
import net.sourceforge.plantuml.SourceStringReader;



/**
 * @author tbrou
 *
 */
public class TestViz {
	private static final Logger LOGGER = LogManager.getLogger(TestViz.class);

	@Test
	public void testSvgFromDot() throws IOException {
		Graph g = Factory.graph("example").directed().node(Factory.node("a").link(Factory.node("b")));
		String dotString = new Serializer(g).serialize();
		LOGGER.debug(dotString);
		Graphviz.fromString(dotString).createSvg();
		
	}
	@Test
	public void testSvgFromPlantUML() throws IOException {
		
		String source = "@startuml\n";
		source += "Bob -> Alice : hello\n";
		source += "@enduml\n";

		SourceStringReader reader = new SourceStringReader(source);
		String desc = reader.generateImage(new File("exampleFromPlant.png"));
		// Return a null string if no generation
		
		
	}
	@Test
	public void testSvgFromPlantUMLActivity() throws IOException {
		
		SourceFileReader reader = new SourceFileReader(new File("src/test/resources/activity.puml"));
		
		LOGGER.debug(reader.getGeneratedImages().get(0).getPngFile());
		// Return a null string if no generation
		
		
	}

}
