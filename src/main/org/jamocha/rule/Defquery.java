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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.BaseNode;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Binding2;
import org.jamocha.rete.Parameter;

import org.jamocha.rete.WorkingMemory;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.query.QueryBaseJoin;
import org.jamocha.rete.query.QueryBaseNot;
import org.jamocha.rete.query.QueryFuncAlphaNode;
import org.jamocha.rete.query.QueryParameterNode;
import org.jamocha.rete.query.QueryResultNode;
import org.jamocha.rete.query.QueryRootNode;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;

/**
 * @author Peter Lin
 *
 * A basic implementation of the Rule interface
 */
public class Defquery implements Query, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String name = null;
    @SuppressWarnings("rawtypes")
	protected ArrayList conditions = null;
	@SuppressWarnings("rawtypes")
	protected ArrayList joins = null;
	@SuppressWarnings("rawtypes")
	protected ArrayList notJoins = null;
	@SuppressWarnings("rawtypes")
	protected Map variables = new HashMap();
    protected boolean auto = false;
    /**
     * by default noAgenda is false
     */
    protected String version = "";
    @SuppressWarnings("rawtypes")
	protected Map bindValues = new HashMap();
    @SuppressWarnings("rawtypes")
	protected LinkedHashMap bindings = new LinkedHashMap();
    protected String comment = "";

    protected QueryRootNode queryRoot = null;
    protected QueryResultNode resultNode = null;
	/**
	 * We use LinkedHashMap to keep the parameters in the order
	 * they were declared.
	 */
    @SuppressWarnings("rawtypes")
	protected Map queryParameterNodeMap = new LinkedHashMap();
	
    /**
     * by default watch is off
     */
    protected boolean watch = false;
    protected long elapsedTime = 0;
    protected long startTime = 0;
    
	/**
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public Defquery() {
		super();
        conditions = new ArrayList();
        joins = new ArrayList();
        notJoins = new ArrayList();
	}

    public Defquery(String name) {
        this();
        setName(name);
    }
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
        this.name = name;
	}

	public boolean getWatch() {
		return watch;
	}

	public void setWatch(boolean watch) {
        this.watch = watch;
	}
    
    public String getComment() {
    	return this.comment;
    }
    
    public void setComment(String text) {
    	this.comment = text.substring(1,text.length() -1);
    }

    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(String ver) {
    	if (ver != null) {
            this.version = ver;
    	}
    }

	@SuppressWarnings("unchecked")
	public void addCondition(Condition cond) {
        conditions.add(cond);
	}

	@SuppressWarnings("unchecked")
	public Condition[] getConditions() {
        Condition[] cond = new Condition[conditions.size()];
        conditions.toArray(cond);
		return cond;
	}

    /**
     * add join nodes to the rule
     */
    @SuppressWarnings("unchecked")
	public void addJoinNode(QueryBaseJoin node) {
    	if (node instanceof QueryBaseNot) {
    		this.notJoins.add(node);
    	} else {
            this.joins.add(node);
    	}
    }

    /**
     * get the array of join nodes
     */
    @SuppressWarnings("rawtypes")
	public List getJoins() {
        return this.joins;
    }
    
    @SuppressWarnings("unchecked")
	public void addNotNode(QueryBaseNot node) {
    	this.notJoins.add(node);
    }
    
    @SuppressWarnings("rawtypes")
	public List getNotNodes() {
    	return this.notJoins;
    }
    
    public BaseNode getLastNode() {
        if (this.joins.size() > 0) {
            return (BaseNode)this.joins.get(this.joins.size() - 1);
        } else if (conditions.size() > 0) {
            // this means there's only 1 ConditionalElement, so the conditions
            // only has 1 element. in all other cases, there will be atleast
            // 1 join node
            Condition c = (Condition)this.conditions.get(0);
            if (c instanceof ObjectCondition) {
                return ((ObjectCondition)c).getLastNode();
            } else if (c instanceof TestCondition) {
                return ((TestCondition)c).getTestNode();
            }
            return null;
        } else {
        	return null;
        }
    }
    
	/**
	 * Method will only add the binding if it doesn't already
	 * exist.
	 * @param bind
	 */
	@SuppressWarnings("unchecked")
	public void addBinding(String key, Binding bind) {
		if (!this.bindings.containsKey(key)) {
			this.bindings.put(key,bind);
		}
	}
	
	/**
	 * Return the Binding matching the variable name
	 * @param varName
	 * @return
	 */
	public Binding getBinding(String varName) {
		return (Binding)this.bindings.get(varName);
	}

	/**
	 * Get a copy of the Binding using the variable name
	 * @param varName
	 * @return
	 */
	public Binding copyBinding(String varName) {
		Binding b = getBinding(varName);
		if (b != null) {
			Binding b2 = (Binding)b.clone();
			return b2;
		} else {
			return null;
		}
	}
	
	public Binding copyPredicateBinding(String varName, int operator) {
		Binding2 b = (Binding2)getBinding(varName);
		if (b != null) {
			Binding2 b2 = new Binding2(operator);
			b2.setLeftRow(b.getLeftRow());
			b2.setLeftIndex(b.getLeftIndex());
            b2.setVarName(b.getVarName());
            b2.setQueryValue(b.getQueryValue());
            b2.setRightIndex(b.getRightIndex());
			return b2;
		} else {
			return null;
		}
	}
	
	/**
	 * The method will return the Bindings in the order they
	 * were added to the utility.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Iterator getBindingIterator() {
		return this.bindings.values().iterator();
	}
	
	/**
	 * Returns the number of unique bindings. If a binding is
	 * used multiple times to join several facts, it is only
	 * counted once.
	 * @return
	 */
	public int getBindingCount() {
		return this.bindings.size();
	}
	
	@SuppressWarnings("unchecked")
	public void addQueryParameterNode(QueryParameterNode parameterNode) {
		if (this.queryParameterNodeMap.containsKey(parameterNode.getParameterName())) {
			// only set the node if the parameter was declared
			this.queryParameterNodeMap.put(parameterNode.getParameterName(), parameterNode);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void addQueryFuncNode(QueryFuncAlphaNode funcNode) {
		if (this.queryParameterNodeMap.containsKey(funcNode.getParameterName())) {
			this.queryParameterNodeMap.put(funcNode.getParameterName(), funcNode);
		}
	}
	
	public void resolveTemplates(Rete engine) {
        this.resolveConditionTemplates(engine, this.getConditions());
	}
    
    private void resolveConditionTemplates(Rete engine, Condition[] cnds) {
        for (int idx=0; idx < cnds.length; idx++) {
            Condition cnd = cnds[idx];
            if (cnd instanceof ObjectCondition) {
                ObjectCondition oc = (ObjectCondition)cnd;
                Template dft = engine.findTemplate(oc.getTemplateName());
                if (dft != null) {
                    oc.setTemplate(dft);
                }
            } else if (cnd instanceof ExistCondition) {
                ExistCondition exc = (ExistCondition) cnd;
                Template dft = engine.findTemplate(exc.getTemplateName());
                if (dft != null) {
                    exc.setTemplate(dft);
                }
            } else if (cnd instanceof TemporalCondition) {
                TemporalCondition tempc = (TemporalCondition)cnd;
                Template dft = engine.findTemplate(tempc.getTemplateName());
                if (dft != null) {
                    tempc.setTemplate(dft);
                }
            } else if (cnd instanceof AndCondition) {
                resolveConditionTemplates(engine, ((AndCondition)cnd).getConditions() );
            }
        }
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setQueryParameters(List props) {
		Iterator itr = props.iterator();
		while (itr.hasNext()) {
			String var = (String)itr.next();
			if (var.startsWith("?")) {
				String variable = var.substring(1);
				this.variables.put(variable, variable);
				this.queryParameterNodeMap.put(variable, null);
			}
		}
	}
	
	/**
	 * This method is only used by clone(Rete) method.
	 * @param variables
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setQueryParameters(Map variables) {
		Iterator itr = variables.keySet().iterator();
		while (itr.hasNext()) {
			String variable = (String)itr.next();
			this.variables.put(variable, variable);
			this.queryParameterNodeMap.put(variable, null);
		}
	}
	
	public boolean isQueryParameter(String name) {
		return this.variables.containsKey(name);
	}
	
	public static long getDateTime(String date) {
		if (date != null && date.length() > 0) {
			try {
				java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("mm/dd/yyyy HH:mm");
				return df.parse(date).getTime();
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		} else {
			return 0;
		}
	}
	
	public String toPPString() {
		StringBuffer buf = new StringBuffer();
		return buf.toString();
	}

	@SuppressWarnings("rawtypes")
	public void clear() {
		Iterator itr = this.conditions.iterator();
		while (itr.hasNext()) {
			Condition cond = (Condition)itr.next();
			cond.clear();
		}
		this.joins.clear();
	}
	
	/**
	 * Each query should use a clone of the defquery.
	 */
	public Defquery clone(Rete engine) {
		Defquery clone = new Defquery(this.name);
		clone.setQueryParameters(this.queryParameterNodeMap);
		clone.bindings = this.bindings;
		clone.comment = this.comment;
		clone.conditions = this.conditions;
		clone.name = this.name;
		clone.watch = this.watch;
		clone.queryRoot = this.queryRoot.clone(engine, clone);
		return clone;
	}

	public void setQueryNetwork(QueryRootNode queryRoot) {
		this.queryRoot = queryRoot;
	}
	
	public QueryRootNode getQueryRootNode() {
		return this.queryRoot;
	}

	public void setQueryResultNode(QueryResultNode queryResultNode) {
		this.resultNode = queryResultNode;
	}
	
	public long getElapsedTime() {
		return this.elapsedTime;
	}
	
	public void setElapsedTime(long time) {
		this.elapsedTime = time;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List executeQuery(Rete engine, WorkingMemory memory, Parameter[] parameters) {
		if (watch) {
			startTime = System.currentTimeMillis();
		}
		try {
			ArrayList params = new ArrayList(this.queryParameterNodeMap.values());
			for (int i=0; i < parameters.length; i++) {
				Object node = params.get(i);
				if (node instanceof QueryParameterNode) {
					QueryParameterNode pnode = (QueryParameterNode)node;
					pnode.setQueryParameterValue(parameters[i].getValue());
				} else if (node instanceof QueryFuncAlphaNode) {
					QueryFuncAlphaNode pnode = (QueryFuncAlphaNode)node;
					pnode.setQueryParameterValue(parameters[i].getValue());
				}
			}
			this.queryRoot.assertObject(null, engine, memory);
			// now iterate over the NOTCE nodes and execute the query
			// they should be in the order it was defined.
			for (int i=0; i < this.notJoins.size(); i++) {
				((QueryBaseNot)this.notJoins.get(i)).executeJoin(engine, memory);
			}
		} catch (AssertException e) {
		}
		if (watch) {
			elapsedTime = System.currentTimeMillis() - startTime;
			engine.setQueryTime(this.name, this.elapsedTime);
		}
		return this.resultNode.getResults();
	}
}
