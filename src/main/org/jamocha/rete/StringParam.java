/*
 * Copyright 2002-2008 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

/**
 * @author Peter Lin
 *
 * Value parameter is meant for values. It extends AbstractParam, which provides
 * implementation for the convienance methods that convert the value to
 * primitive types.
 */
public class StringParam extends AbstractParam {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected int valueType;

	protected String value = null;

	/**
	 * 
	 */
	public StringParam(int vtype, String value) {
		super();
		this.valueType = vtype;
		this.value = value;
	}

	/**
	 * The value types are defined in woolfel.engine.rete.Constants
	 */
	public int getValueType() {
		return this.valueType;
	}

	/**
	 * Method will return the value as on Object. This means primitive
	 * values are wrapped in their Object equivalent.
	 */
	public Object getValue() {
		return this.value;
	}

    /**
     * String parameters do not need to do any lookup, so it just
     * returns the value.
     */
    public Object getValue(Rete engine, int valueType) {
        return this.value;
    }
    
	/**
	 * implementation sets the value to null and the type to Object
	 */
	public void reset() {
		this.value = null;
		this.valueType = Constants.OBJECT_TYPE;
	}
}
