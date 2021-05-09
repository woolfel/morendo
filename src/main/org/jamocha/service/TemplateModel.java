package org.jamocha.service;

import java.net.MalformedURLException;
import java.net.URL;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.functions.io.BatchFunction;

/**
 * Template model defines the deftemplates for a rule application
 * configuration.
 * 
 * @author Peter Lin
 */
public class TemplateModel implements Model {
	
	private String contents;
	private String URL;
	
	public TemplateModel() {
		super();
	}

	/**
	 * The contents of the file to pass to Jamocha's parser.
	 * @return
	 */
	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	/**
	 * The URL to get the file or contents
	 * @return
	 */
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
	
	/**
	 * Current implementation uses BatchFunction to load the deftemplate model. It assumes
	 * the URL has all the deftemplate declarations.
	 */
	public void loadModel(Rete engine) {
		Function batch = engine.findFunction(BatchFunction.BATCH);
		batch.executeFunction(engine, new Parameter[]{new ValueParam(Constants.STRING_TYPE,this.URL)});
	}
}
