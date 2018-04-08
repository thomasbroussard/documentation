/**
 * Ce fichier est la propriété de Thomas BROUSSARD
 * Code application :
 * Composant :
 */
package fr.tbr.documentation.textile.tests;

import java.io.File;
import java.io.IOException;

import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

import fr.tbr.doc.parsing.GenericMarkupToHtml;
import fr.tbr.doc.presentation.HtmlPresenter;
import fr.tbr.doc.transformer.DocumentationSourceProcessor;

/**
 * <h3>Description</h3>
 * <p>This class allows to ...</p>
 *
 * <h3>Usage</h3>
 * <p>This class should be used as follows:
 *   <pre><code>${type_name} instance = new ${type_name}();</code></pre>
 * </p>
 *
 * @since $${version}
 * @see See also $${link}
 * @author ${user}
 *
 * ${tags}
 */
public class Launcher {

	public static void main(String[] args) throws IOException {
		final String basePath = "src/test/resources";
		final String targetPath = "target";
		final File targetDir = new File(targetPath);
		final File baseDir = new File(basePath);
		final DocumentationSourceProcessor processor = new DocumentationSourceProcessor(targetDir, baseDir);
		processor.setForce(false);
		processor.setProcessBreadCrumbs(true);
		final GenericMarkupToHtml parser = new GenericMarkupToHtml(new TextileLanguage());
		processor.setParser(parser);
		final HtmlPresenter presenter = new HtmlPresenter();
		presenter.addScriptFromContent(new File("src/test/resources/highlight.min.js"));
		presenter.addScriptFromContent(new File("src/test/resources/script.js"));

		presenter.addStyleFromContent(new File("src/test/resources/default.highlight.css"));
		presenter.addStyleFromContent(new File("src/test/resources/jtxtdoc-styles.css"));
		presenter.addStyleFromContent(new File("src/test/resources/jtxtdoc-global.css"));
		presenter.addStyleFromUrl("style.css");
		processor.setPresenter(presenter);

		processor.setSourceExtensions("textile");
		processor.process();
	}
}
