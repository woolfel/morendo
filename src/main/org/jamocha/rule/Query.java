/*
 * Copyright 2002-2010 Jamocha
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
package org.jamocha.rule;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.jamocha.rete.BaseNode;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.query.QueryBaseJoin;
import org.jamocha.rete.query.QueryBaseNot;

/**
 * @author Peter Lin
 *
 * The query interface defines the attributes for a query class. The main
 * difference between a rule and query is that queries do not have actions.
 * Instead, it simply gathers the results and returns an ArrayList. Queries
 * also do not have the same properties as a rule.
 */
public interface Query extends Serializable {
    /**
     * if users want to give a rule a comment, the method will return it.
     * otherwise it should return zero length string
     * @return
     */
    String getComment();
    /**
     * set the comment of the rule. it should be a descriptive comment
     * about what the rule does.
     * @param text
     */
    void setComment(String text);
    /**
     * get the name of the rule
     * @return
     */
    String getName();
    /**
     * set the name of the rule
     * @param name
     */
    void setName(String name);
    /**
     * the version of the rule
     * @return
     */
    String getVersion();
    /**
     * set the version of the rule
     * @param ver
     */
    void setVersion(String ver);
    /**
     * watch is used for debugging
     * @return
     */
    boolean getWatch();
    /**
     * to debug a rule, set the watch to true
     * @param watch
     */
    void setWatch(boolean watch);
    
    void addCondition(Condition cond);
    Condition[] getConditions();
    void addJoinNode(QueryBaseJoin node);
    @SuppressWarnings("rawtypes")
	List getJoins();
    void addNotNode(QueryBaseNot node);
    @SuppressWarnings("rawtypes")
	List getNotNodes();
    /**
     * The method should return the last node in the rule, not counting
     * the terminal node.
     * @return
     */
    BaseNode getLastNode();
    /**
     * Add a new binding to the rule with the variable as the key
     * @param key
     * @param bind
     */
    void addBinding(String key, Binding bind);
    /**
     * Get the Binding object for the given key
     * @param varName
     * @return
     */
    Binding getBinding(String varName);
    /**
     * utility method for copying bindings
     * @param varName
     * @return
     */
    Binding copyBinding(String varName);
    /**
     * utility method for copying predicate bindings
     * @param varName
     * @param operator
     * @return
     */
    Binding copyPredicateBinding(String varName, int operator);
    /**
     * Get a iterator to the Binding objects
     * @return
     */
    @SuppressWarnings("rawtypes")
	Iterator getBindingIterator();
    /**
     * Get a count of the Binding
     * @return
     */
    int getBindingCount();
    /**
     * this method needs to be called before rule compilation begins. It
     * avoids doing multiple lookups for the corresponding template.
     * @param engine
     */
    void resolveTemplates(Rete engine);
    /**
     * set the variables for the query
     * @param variables
     */
    @SuppressWarnings("rawtypes")
	void setQueryParameters(List variables);
    /**
     * Return a pretty print formatted string for the rule.
     * @return
     */
    String toPPString();
    /**
     * returns the time it took to execute the query.
     * @return
     */
    long getElapsedTime();
    /**
     * Method will execute the query and return a list of results.
     * @param engine
     * @param memory
     * @return
     */
    @SuppressWarnings("rawtypes")
	List executeQuery(Rete engine, WorkingMemory memory, Parameter[] parameters);
}
