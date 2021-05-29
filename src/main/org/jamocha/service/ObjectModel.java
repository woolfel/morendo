package org.jamocha.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jamocha.rete.Defclass;
import org.jamocha.rete.Rete;

/**
 * Object model is only for compiled jar files containing
 * the classes that need to be declared with defclass.
 * 
 * @author Peter Lin
 */
public class ObjectModel implements Model {
	
	private String URL = null;
	private List<?> classList = null;
	private RuleApplication ruleApp;
	
	public ObjectModel() {
		super();
	}

	public void setRuleApplication(RuleApplication app) {
		this.ruleApp = app;
	}
	
	public String getURL() {
		return URL;
	}

	/**
	 * The URL for the jar file is optional and is only used
	 * if the jar isn't already in the classpath.
	 * @param url
	 */
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
	
	public List<?> getClassList() {
		return classList;
	}

	/**
	 * The classes that implement jamocha's FunctionGroup
	 * interface.
	 * @param classList
	 */
	public void setClassList(List<?> classList) {
		this.classList = classList;
	}
	
	public void loadModel(Rete engine) {
		for (int idx=0; idx < classList.size(); idx++) {
			String clazzname = (String)classList.get(idx);
			Class<?> clzz = ruleApp.findClass(clazzname);
			if (clzz != null) {
				engine.declareObject(clzz);
				Defclass defclass = engine.findDefclass(clzz);
				defclass.loadMacros(engine.getCurrentFocus().getModuleClassLoader());
			}
		}
	}
}
