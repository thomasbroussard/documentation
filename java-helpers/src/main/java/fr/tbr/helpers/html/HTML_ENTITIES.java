/**
 * 
 */
package fr.tbr.helpers.html;

/**
 * An enumeration to define classical HTML entities
 * @author tbrou
 *
 */
public enum HTML_ENTITIES {
	H1("h1"),
	H2("h2"),
	H3("h3"),
	H4("h4"),
	BODY("body"),
	A("a"),
	DIV("div"),
	HEAD("head"), 
	STYLE("style"), 
	SCRIPT("script"),
	
	
	;
	
	
	private String entity;
	/**
	 * 
	 */
	private HTML_ENTITIES(String entity) {
		this.entity = entity;
	}
	/**
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}
	
	
	


}
