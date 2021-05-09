/*
 * Copyright 2002-2009 Jamocha
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

import java.io.Serializable;

/**
 * @author Peter Lin
 *
 * Template defines the methods to access an object, which is the
 * equivalent of un-ordered facts. It defines all the necessary
 * methods for Deftemplate.
 */
public interface Template extends Serializable, Print {
	/**
	 * The name of the template may be the fully qualified
	 * class name, or an alias.
	 * @return
	 */
	String getName();

	/**
	 * templates may have 1 or more slots. A slot is a named
	 * column with a specific type of value.
	 * @return
	 */
	int getNumberOfSlots();

	/**
	 * Return an array of all the slots.
	 * @return
	 */
	BaseSlot[] getAllSlots();

	/**
	 * clones the slots to create facts
	 * @return
	 */
	BaseSlot[] cloneAllSlots();
	
	/**
	 * Return the slot with the String name
	 * @return
	 */
	BaseSlot getSlot(String name);

	/**
	 * Get the Slot at the given column id
	 * @param column
	 * @return
	 */
	BaseSlot getSlot(int column);

	/**
	 * Returns the number of slots used by rules
	 * @return
	 */
	int getSlotsUsed();

	/**
	 * Get the column index with the given name
	 * @param name
	 * @return
	 */
	int getColumnIndex(String name);

	/**
	 * if watch is set to true, the rule engine will pass events
	 * when the fact traverses the network.
	 * @return
	 */
	boolean getWatch();

	/**
	 * Set the watch flag
	 * @param watch
	 */
	void setWatch(boolean watch);

	/**
	 * 
	 * @param data
	 * @param id
	 * @return
	 */
	Fact createFact(Object data, Defclass clazz, long id);

	/**
	 * method for creating a temporal deffact for the given object
	 * @param data
	 * @param clazz
	 * @param id
	 * @return
	 */
	Fact createTemporalFact(Object data, Defclass clazz, long id);
	
	/**
	 * If a template has a parent, the method should
	 * return the parent, otherwise it should return
	 * null
	 * @return
	 */
	Template getParent();

	/**
	 * set the parent template
	 * @param parent
	 */
	void setParent(Template parent);

	/**
	 * @return
	 */
	String getClassName();
	
	/**
	 * If the template is currently in use, we should not remove it
	 * until all the dependent rules are removed first.
	 * @return
	 */
	boolean inUse();

	/**
	 * the temporal distance of the template in milliseoncds
	 * @return
	 */
	int getTemporalDistance();
	
	/**
	 * set the temporal distance in milliseoncds
	 * @param distnace
	 */
	void setTemporalDistance(int distnace);
	
	/**
	 * 
	 * @return
	 */
	String toString();

	void incrementColumnUseCount(String name);
	
	boolean getNonTemporalRules();
	
	void setNonTemporalRules(boolean value);
}
