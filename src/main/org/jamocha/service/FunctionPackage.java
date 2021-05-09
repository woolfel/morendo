package org.jamocha.service;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * FunctionPackage encapsulates one or more functions and morendo
 * FunctionPackage into a logical group. The reason for this is
 * to make it easier to group functions together for deployment.
 * 
 * @author Peter Lin
 */
public class FunctionPackage {
	
	private String[] classNames;
	private String URL;
	
	public FunctionPackage() {
		super();
	}

	/**
	 * The fully qualified class names for the FunctionGroups
	 * @return
	 */
	public String[] getClassNames() {
		return classNames;
	}

	public void setClassNames(String[] className) {
		this.classNames = className;
	}

	/**
	 * The URL to the jar file containing the FunctionGroup and
	 * Function classes.
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
}
