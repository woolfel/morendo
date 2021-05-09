package org.jamocha.service;

/**
 * The concrete class implementing the interface should know how to
 * get the data and load it. This way, the RuleApplication class
 * doesn't have to know how to handle different types of data or
 * what should get asserted. This is especially important in the
 * case of big object graphs.
 * 
 * @author Peter Lin
 */
public interface InitialData {
	public static final String DEFFACTS = "deffacts";
	public static final String OBJECTS = "objects";
	public static final String DATASET = "dataset";
	public static final String JSON = "json";
	
	void setName(String name);
	String getName();
	
	String getDataType();
	
	/**
	 * The data could be a list of objects, raw text or some
	 * other form.
	 * @return
	 */
	Object getData();
	boolean loadData(org.jamocha.rete.Rete engine);
	boolean reloadData(org.jamocha.rete.Rete engine);
	
}
