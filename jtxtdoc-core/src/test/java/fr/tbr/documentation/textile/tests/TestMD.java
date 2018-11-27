/**
 *
 */
package fr.tbr.documentation.textile.tests;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.mylyn.wikitext.markdown.core.MarkdownLanguage;
import org.junit.Test;

import fr.tbr.doc.parsing.GenericMarkupToHtml;
import fr.tbr.doc.presentation.HtmlPresenter;
import fr.tbr.doc.transformer.DocumentationSourceProcessor;

/**
 * @author tbrou
 *
 */
public class TestMD {
	/**
	 *
	 */

	private static final Logger LOGGER = LogManager.getLogger(TestMD.class);


	@Test
	public void processTutorials() throws IOException {
		final String basePath = "src/test/resources";
		final String targetPath = "target";
		final File targetDir = new File(targetPath);
		final File baseDir = new File(basePath);
		final DocumentationSourceProcessor processor = new DocumentationSourceProcessor(targetDir, baseDir);
		processor.setForce(false);
		processor.setProcessBreadCrumbs(false);
		final GenericMarkupToHtml parser = new GenericMarkupToHtml(new MarkdownLanguage());
		processor.setParser(parser);
		final HtmlPresenter presenter = new HtmlPresenter();
		presenter.addScriptFromContent(new File("src/test/resources/script.js"));
		presenter.addScriptFromContent(new File("src/test/resources/highlight.min.js"));

		presenter.addStyleFromContent(new File("src/test/resources/jtxtdoc-styles.css"));
		presenter.addStyleFromContent(new File("src/test/resources/default.highlight.css"));
		presenter.addStyleFromContent(new File("src/test/resources/jtxtdoc-global.css"));

		presenter.addStyleFromUrl("style.css");
		processor.setPresenter(presenter);

		processor.setSourceExtensions("md");
		processor.process();
	}



}
