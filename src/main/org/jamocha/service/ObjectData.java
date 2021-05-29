package org.jamocha.service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectData implements InitialData {

	@JsonIgnore
	private transient Logger log = LogFactory.createLogger(ObjectData.class);
	private String name;
	private transient List<Object> data = null;
	private String url;
	@JsonIgnore
	private static ObjectMapper mapper = new ObjectMapper();
	
	public ObjectData() {
	}

	public Object getData() {
		return data;
	}

	public void setData(List<Object> data) {
		this.data = data;
	}
	
	public List<Object> getObjectList() {
		return this.data;
	}
	
	@JsonIgnore
	public String getDataType() {
		return OBJECTS;
	}

	public String getName() {
		return name;
	}

	@JsonIgnore
	public boolean loadData(Rete engine) {
		if (data == null) {
			// the data is null, try to load it from the URL
			this.loadFromURL();
		}
		if (data != null) {
			try {
				engine.assertObjects(data);
				return true;
			} catch (AssertException e) {
				log.info(e);
				return false;
			}
		}
		return false;
	}

	@JsonIgnore
	public boolean reloadData(Rete engine) {
		if (data != null) {
			for (int idx=0; idx < data.size(); idx++) {
				try {
					engine.retractObject(data.get(idx));
				} catch (RetractException e) {
					return false;
				}
			}
			try {
				engine.assertObjects(data);
				return true;
			} catch (AssertException e) {
				log.info(e);
				return false;
			}
		}
		return false;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@SuppressWarnings("unchecked")
	@JsonIgnore
	private void loadFromURL() {
		if (this.url != null) {
			this.data = ObjectData.loadObjectData(this.url);
		}
	}
	
	/**
	 * Convienant static method for reading XML to a List of java
	 * objects. The method checks to see if the URL begins with
	 * http:// and handle it appropriately.
	 * @param url
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@JsonIgnore
	public static List loadObjectData(String url) {
		Reader reader;
		try {
			if (url.startsWith("http://")) {
				try {
					URL urlObject = new URL(url);
					InputStream input = urlObject.openStream();
					List data = mapper.readValue(input, List.class);
					return data;
				} catch (MalformedURLException e) {
					Logger log = LogFactory.createLogger(ObjectData.class);
					log.fatal(e);
				} catch (IOException e) {
					Logger log = LogFactory.createLogger(ObjectData.class);
					log.fatal(e);
				}
			} else {
				reader = new FileReader(url);
				List data = mapper.readValue(reader, List.class);
				return data;
			}
		} catch (Exception e) {
			Logger log = LogFactory.createLogger(ObjectData.class);
			log.fatal(e);
		}
		return null;
	}
	
	/**
	 * Convienant static method for saving a List of java objects to XML
	 * format using XStream.
	 * @param filename
	 * @param data
	 */
	@SuppressWarnings("rawtypes")
	@JsonIgnore
	public static void saveObjectData(String filename, List data) {
		FileWriter writer;
		try {
			writer = new FileWriter(filename);
			mapper.writeValue(writer, data);
			writer.close();
		} catch (IOException e) {
			Logger log = LogFactory.createLogger(ObjectData.class);
			log.fatal(e);
		}
	}
}
