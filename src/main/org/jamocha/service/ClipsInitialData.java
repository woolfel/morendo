package org.jamocha.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import org.jamocha.rete.Constants;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.functions.io.LoadFactsFunction;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The implementation uses load-facts to load the data
 * 
 * @author Peter Lin
 */
public class ClipsInitialData implements InitialData {

	@JsonIgnore
	private transient Logger log = null;
	@JsonIgnore
	private String data = null;
	@JsonIgnore
	private String cacheFile = null;
	private String name = null;
	private String url = null;
	
	public ClipsInitialData() {
		super();
		log = LogFactory.createLogger(ClipsInitialData.class);
	}
	
	public Object getData() {
		return data;
	}

	@JsonIgnore
	public String getDataType() {
		return DEFFACTS;
	}
	
	public void setName(String text) {
		this.name = text;
	}
	
	public String getName() {
		return this.name;
	}

	public void setURL(String text) {
		this.url = text;
	}
	
	public String getURL() {
		return this.url;
	}
	
	/**
	 * the load process needs to be atomic, so that in the event
	 * there is an error, the entire data set is rolled back.
	 */
	public boolean loadData(Rete engine) {
		if (log == null) {
			log = LogFactory.createLogger(ClipsInitialData.class);
		}
		boolean loaded = true;
		try {
			LoadFactsFunction load = (LoadFactsFunction)engine.findFunction(LoadFactsFunction.LOAD);
			Parameter[] parameters = new Parameter[1];
			parameters[0] = new ValueParam(Constants.STRING_TYPE, cacheFile);
			load.executeFunction(engine, parameters);
		} catch (Exception e) {
			loaded = false;
		}
		return loaded;
	}

	public boolean reloadData(Rete engine) {
		engine.clearFacts();
		engine.clearObjects();
		return loadData(engine);
	}

	/**
	 * Method writes the file to a cache directory for backup just in case
	 */
	protected void serializeArrayToDisk() {
		cacheFile = "./cache/data/" + this.name + ".bin";
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(cacheFile);
			ObjectOutputStream output = new ObjectOutputStream(fos);
			output.writeObject(this.data);
			output.close();
			data = null;
		} catch (FileNotFoundException e) {
			log.info(e);
		} catch (IOException e) {
			log.info(e);
		}
	}
}
