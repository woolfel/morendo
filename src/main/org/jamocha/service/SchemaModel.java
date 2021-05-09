package org.jamocha.service;

import java.net.MalformedURLException;
import java.net.URL;

import org.jamocha.rete.Rete;

/**
 * Schema model is defined by XML Schema. Concrete classes will need
 * to either compiled the schema or get a precompiled jar file for
 * the schema.
 * 
 * @author Peter Lin
 */
public class SchemaModel implements Model {
	private String URL;
	private String contents;
	
	public SchemaModel() {
		super();
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String url) {
		URL = url;
	}

	public URL getURLObject() {
		try {
			return new URL(this.URL);
		} catch (MalformedURLException e) {
			return null;
		}
	}
	
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}
	
	/**
	 * TODO - finish implementing me
	 */
	public void loadModel(Rete engine) {
		
	}
}
