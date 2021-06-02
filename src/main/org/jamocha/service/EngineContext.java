/*
 * Copyright 2002-2010 Peter Lin
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

package org.jamocha.service;

import java.util.List;

import org.jamocha.rete.Fact;
import org.jamocha.rete.Rete;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * The purpose of an EngineContext is to encapsulate a rule
 * engine instance so that when the request or application
 * is done, it returns the engine instance back to the
 * application pool.
 * 
 * @author Peter Lin
 */
public interface EngineContext {
	/**
	 * Close the context and return the engine back to the pool.
	 */
	void close();
	
	/**
	 * Resets the the EngineContext by removing all the objects
	 * and asserting them again. It is equivalent to Rete.reset().
	 */
	void reset();

	/**
	 * Add a java object to the rule engine
	 * @param data
	 * @param isStatic
	 * @param isShadowed
	 */
	void assertObject(Fact data, boolean isStatic, boolean isShadowed) throws AssertException;

	/**
	 * Add a list of java objects to the rule engine
	 * @param data
	 * @param isStatic
	 * @param isShadowed
	 */
	void asssertObjects(List<Fact> data, boolean isStatic, boolean isShadowed) throws AssertException;

	/**
	 * Remove the object from the rule engine
	 * @param data
	 */
	void removeObject(Object data) throws RetractException;

	/**
	 * Remove the objects in the list from the rule engine
	 * @param data
	 */
	void removeObjects(List<?> data) throws RetractException;

	/**
	 * Tell the rule engine an object has been modified
	 * @param data
	 */
	void modifyObject(Object data) throws AssertException, RetractException;

	/**
	 * Tell the rule engine of the objects that have been modified
	 * @param data
	 */
	void modifyObjects(List<?> data) throws AssertException, RetractException;

	/**
	 * Returns a list of the current Java objects in memory.
	 * @return
	 */
	List<?> getObjects();
	
	/**
	 * Executes the rules
	 */
	void executeRules();
	
	/**
	 * Returns the name of the rule applications.
	 * @return
	 */
	String getApplicationName();
	
	/**
	 * In some cases, like embedding the engine in another application, the user
	 * may want direct access to it. This functionality is for expert users and
	 * situations where the rule engine is embedded. For normal applications,
	 * do not use this method.
	 * @return
	 */
	Rete getRuleEngine();
}
