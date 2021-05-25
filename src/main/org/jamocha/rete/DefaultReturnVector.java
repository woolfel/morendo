/*
 * Copyright 2002-2008 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author Peter Lin
 *
 * Basic implementation of ReturnVector used by functions to return
 * the results.
 */
public class DefaultReturnVector implements ReturnVector {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("rawtypes")
	protected Vector items = new Vector(2);


	/**
	 * 
	 */
	public DefaultReturnVector() {
		super();
	}
    
    public void clear() {
        this.getItems().clear();
    }

	/**
	 * Current implementation returns the size of the Vector
	 */
	public int size() {
		return this.getItems().size();
	}

	/**
	 * the implementation returns itself, since ReturnVector extends
	 * Iterator interface.
	 */
	@SuppressWarnings("rawtypes")
	public Iterator getIterator() {
		return getItems().iterator();
	}

	/**
	 * Return the first item in the vector
	 * @return
	 */
	public ReturnValue firstReturnValue() {
		return (ReturnValue) this.getItems().get(0);
	}

	@SuppressWarnings("unchecked")
	public void addReturnValue(ReturnValue val) {
		getItems().add(val);
	}
	
	@SuppressWarnings("rawtypes")
	public String toString() {
		Iterator itr = getIterator();
		StringBuilder sb = new StringBuilder();
		while (itr.hasNext()) {
			ReturnValue rval = (ReturnValue) itr.next();
			sb.append(rval.getStringValue()).append('\n');
		}
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public Vector getItems() {
		return items;
	}
}
