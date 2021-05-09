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
package org.jamocha.rete;

import java.io.Serializable;

import org.jamocha.rete.query.QueryObjTypeNode;
import org.jamocha.rule.Query;

/**
 * @author Peter Lin
 *
 * The purpose of a QueryCompiler is to generate a RETE network
 * from the query. Note the network is separate from the RETE
 * network and doesn't use terminal node. Instead it ends with
 * a QueryNode, which gathers the results.
 */
public interface QueryCompiler extends Serializable {
	/**
	 * for the runtime, the default should be false. For the development
	 * the setting should be set to true.
	 * @param validate
	 */
	void setValidateQuery(boolean validate);
	/**
	 * return whether the rule compiler is set to validate the rule
	 * before compiling it.
	 * @return
	 */
	boolean getValidateQuery();
	/**
	 * A rule can be added dynamically at runtime to an existing
	 * engine. If the engine wasn't able to add the rule, it
	 * will throw an exception.
	 * @param rule
	 */
	boolean addQuery(Query rule);

	/**
	 * Add a new ObjectTypeNode to the network
	 * @param node
	 */
	void addObjectTypeNode(QueryObjTypeNode node);

	/**
	 * Remove an ObjectTypeNode from the network. This should be
	 * when the rule engine isn't running. When an ObjectTypeNode
	 * is removed, all nodes and rules using the ObjectTypeNode
	 * need to be removed.
	 * @param node
	 */
	void removeObjectTypeNode(QueryObjTypeNode node);

	/**
	 * Look up the ObjectTypeNode using the Template
	 * @param template
	 * @return
	 */
	QueryObjTypeNode getObjectTypeNode(Template template);

    /**
     * find the object type node using the string name
     * @param templateName
     * @return
     */
    QueryObjTypeNode findObjectTypeNode(String templateName);
    
	/**
	 * If an user wants to listen to the various events in the compiler,
	 * add a listener and then handle the events accordingly.
	 * @param listener
	 */
	void addListener(CompilerListener listener);

	/**
	 * Remove a listener from the compiler.
	 * @param listener
	 */
	void removeListener(CompilerListener listener);
}
