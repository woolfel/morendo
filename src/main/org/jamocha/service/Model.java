package org.jamocha.service;

import java.net.URL;

/**
 * Model classes are responsible for loading and declaring the classes used
 * by the rule application. The rule application will create an instance and
 * call loadModel(Rete).
 * 
 * @author Peter Lin
 */
public interface Model {
	String getURL();
	void setURL(String url);
	
	URL getURLObject();
	
	void loadModel(org.jamocha.rete.Rete engine);
}
