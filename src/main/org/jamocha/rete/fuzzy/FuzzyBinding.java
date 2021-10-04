package org.jamocha.rete.fuzzy;

import java.io.Serializable;
import java.util.HashMap;

/**
 * FuzzyBinding is similar to FuzzyJ FuzzyVariable class. Since we call variables bindings,
 * it makes sense to keep the same naming convention. The reason is that Clips called them
 * bindings.
 * 
 * @author peter
 *
 */
public class FuzzyBinding implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String variableName;
	private String unitType; // the user can define what ever type they want for units like C for celsius
	private double[] doubleBoundry = new double[2];
	private HashMap<String, Object> terms = null;

	/**
	 * Default empty constructor
	 */
	public FuzzyBinding() {
		super();
		this.terms = new HashMap<String,Object>();
	}

	public FuzzyBinding(String variableName, double lower, double upper, String unitType) {
		this();
		this.variableName = variableName;
		this.unitType = unitType;
		this.doubleBoundry[0] = lower;
		this.doubleBoundry[1] = upper;
	}
	
	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public double getMinValue() {
		return this.doubleBoundry[0];
	}
	
	public void setMinValue(double val) {
		this.doubleBoundry[0] = val;
	}
	
	public double getMaxValue() {
		return this.doubleBoundry[1];
	}
	
	public void setMaxValue(double max) {
		this.doubleBoundry[1] = max;
	}
	
	public void addTerm(String term, Object value) {
		this.terms.put(term, value);
	}
	
	public Object clone(){
		FuzzyBinding clone = new FuzzyBinding();
		clone.setMaxValue(this.getMaxValue());
		clone.setMinValue(this.getMinValue());
		clone.setUnitType(this.unitType);
		clone.setVariableName(this.variableName);
		clone.terms.putAll(this.terms);
		return clone;
	}
}
