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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import org.jamocha.model.Edge;
import org.jamocha.model.Graph;
import org.jamocha.model.Node;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.query.GraphResultNode;
import org.jamocha.rete.query.QueryAlphaNode;
import org.jamocha.rete.query.QueryAndAlphaNode;
import org.jamocha.rete.query.QueryBaseAlpha;
import org.jamocha.rete.query.QueryBaseAlphaCondition;
import org.jamocha.rete.query.QueryBaseJoin;
import org.jamocha.rete.query.QueryFuncAlphaNode;
import org.jamocha.rete.query.QueryIntraFactNode;
import org.jamocha.rete.query.QueryLIANode;
import org.jamocha.rete.query.QueryObjTypeNode;
import org.jamocha.rete.query.QueryOrAlphaNode;
import org.jamocha.rete.query.QueryParameterNode;
import org.jamocha.rete.query.QueryRootNode;
import org.jamocha.rule.*;

/**
 * @author Peter Lin
 *
 * GraphQueryCompiler handles compiling graph queries, which are different
 * from defqueries. Graph queries perform pattern matching on nodes and
 * edges. It makes it easy to query graph data based on the label and type.
 */
public class GraphQueryCompiler implements QueryCompiler {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private Rete engine = null;
   	private Map<Template, QueryObjTypeNode> objectTypeNodesMap = null;
    
    private ArrayList<CompilerListener> listener = new ArrayList<CompilerListener>();
    protected boolean validate = true;
    protected TemplateValidation tval = null;
    
    public static final String FUNCTION_NOT_FOUND = 
        Messages.getString("CompilerProperties.function.not.found"); //$NON-NLS-1$
    public static final String INVALID_FUNCTION = 
        Messages.getString("CompilerProperties.invalid.function"); //$NON-NLS-1$
    
    protected Logger log = LogFactory.createLogger(GraphQueryCompiler.class);
    protected GraphQuery currentQuery = null;

    /**
     * The query compiler needs a reference to the Map containing all the ObjectTypeNodes
     * @param engine
     * @param inputNodes
     */
	public GraphQueryCompiler(Rete engine) {
		super();
        this.engine = engine;
        this.objectTypeNodesMap = new HashMap<Template, QueryObjTypeNode>();
        this.tval = new TemplateValidation(engine);
	}

	protected void setupObjectNodes() {
		if (this.objectTypeNodesMap.size() == 0) {
			Deftemplate gdt = this.engine.findDeftemplate(Graph.class);
			QueryObjTypeNode qotn1 = this.currentQuery.getQueryRootNode().findQueryObjTypeNode(gdt);
			if (qotn1 != null) {
				this.objectTypeNodesMap.put(qotn1.getDeftemplate(), qotn1);
			}
			Deftemplate ndt = this.engine.findDeftemplate(Node.class);
			QueryObjTypeNode qotn2 = this.currentQuery.getQueryRootNode().findQueryObjTypeNode(ndt);
			if (qotn2 != null) {
				this.objectTypeNodesMap.put(qotn2.getDeftemplate(), qotn2);
			}
			Deftemplate edt = this.engine.findDeftemplate(Edge.class);
			QueryObjTypeNode qotn3 = this.currentQuery.getQueryRootNode().findQueryObjTypeNode(edt);
			if (qotn3 != null) {
				this.objectTypeNodesMap.put(qotn3.getDeftemplate(), qotn3);
			}
		}
	}
	
	public void setValidateQuery(boolean valid) {
		this.validate = valid;
	}
	
	public boolean getValidateQuery() {
		return this.validate;
	}

    /**
     * Here is a description of the compilation algorithm.
     * 1. iterate over the conditional elements
     *   i. generate the alpha nodes
     *     a. literal constraints generate alpha node
     *     b. predicate constaints that compare against a literal generate alpha node
     *   ii. calculate the bindings
     *     a. each binding has a rowId
     *     b. NOT and EXIST CE do not increment the rowId
     * 2. iterate over the conditional elements
     *   i. generate the beta nodes
     *   ii. attach the Left Input adapater nodes
     *   iii. attach the join nodes to the alpha nodes
     * 3. create the QueryResultNode and attach it to the last node.
     * 
     * This means the query compiler takes a 2 pass approach to
     * compiling rules. At the start of the method, it sets 3
     * attributes to null: prevCE, prevJoinNode, joinNode.
     * Those attributes are used by the compile join methods,
     * so it's important to set it to null at the start. If
     * we don't the next rule won't compile correctly.
     */
	public boolean addQuery(Query query) {
		query.resolveTemplates(engine);
		this.currentQuery = (GraphQuery)query;
		QueryRootNode queryRoot = engine.getRootNode().createQueryRoot(engine);
		currentQuery.setQueryNetwork(queryRoot);
        this.setupObjectNodes();
		if (this.currentQuery.getConditions() != null && this.currentQuery.getConditions().length > 0) {
			try {
				Condition[] conds = this.getRuleConditions(this.currentQuery);
				int counter = 0;
				for (int idx = 0; idx < conds.length; idx++) {
					Condition con = conds[idx];
					con.getCompiler(this).compile(con, counter, this.currentQuery);
					if ((con instanceof ObjectCondition) && (!((ObjectCondition) con).getNegated())) {
						counter++;
					}
				}
				compileJoins(this.currentQuery, conds);
				BaseNode last = this.currentQuery.getLastNode();
				GraphResultNode resultNode = new GraphResultNode(engine.nextNodeId());
				last.addSuccessorNode(resultNode, engine, null);
				currentQuery.setQueryResultNode(resultNode);
				engine.declareGraphQuery(this.currentQuery);
				this.currentQuery = null;
				return true;
			} catch (AssertException e) {
				CompileEvent ce = new CompileEvent(this.currentQuery, CompileEvent.INVALID_RULE);
				ce.setMessage(Messages.getString("RuleCompiler.assert.error"));
				this.notifyListener(ce);
				log.debug(e);
				this.currentQuery = null;
				return false;
			}
		}
		return false;
	}

	public Condition[] getRuleConditions(Query query) {
        Condition[] conditions = query.getConditions();
        ArrayList<Object> conditionList = new ArrayList<Object>();
        boolean hasAnd = false;
        for (int idx=0; idx < conditions.length; idx++) {
            if (conditions[idx] instanceof AndCondition) {
                AndCondition and = (AndCondition)conditions[idx];
                conditionList.addAll(and.getNestedConditionalElement());
                hasAnd = true;
            } else {
                conditionList.add(conditions[idx]);
            }
        }
        if (hasAnd) {
            // we create a new array of conditions from the ArrayList
            Condition[] newlist = new Condition[conditionList.size()];
            conditions = conditionList.toArray(newlist);
        }
        return conditions;
    }
    
	/**
     * implementation uses the deftemplate for the HashMap key and the
     * node for the value. If the node already exists in the HashMap,
     * or the key already exists, the compiler will not add it to the
     * network.
	 */
	public void addObjectTypeNode(QueryObjTypeNode node) {
        if (!this.objectTypeNodesMap.containsKey(node.getDeftemplate())) {
            this.objectTypeNodesMap.put(node.getDeftemplate(),node);
        }
        // if it is the objectTypeNode for InitialFact, we go ahead and create
        // the Left Input Adapter node for it
        if (node.getDeftemplate() instanceof InitialFact) {
            try {
                IFLIANode lian = new IFLIANode(this.engine.nextNodeId());
                node.addSuccessorNode(lian, engine, engine.workingMem);
            } catch (AssertException e) {
                
            }
        }
	}

	/**
     * Method will remove the ObjectTypeNode and call clear on it.
	 */
	public void removeObjectTypeNode(QueryObjTypeNode node) {
		this.objectTypeNodesMap.remove(node.getDeftemplate());
		node.removeAllSuccessors();
	}

	/**
     * if the ObjectTypeNode does not exist, the method will return null.
	 */
	public QueryObjTypeNode getObjectTypeNode(Template template) {
		return this.objectTypeNodesMap.get(template);
	}
    
    /**
     * 
     * @param templateName
     * @return
     */
    public QueryObjTypeNode findObjectTypeNode(String templateName) {
        Iterator<Template> itr = this.objectTypeNodesMap.keySet().iterator();
        Template tmpl = null;
        while (itr.hasNext()) {
            tmpl = itr.next();
            if (tmpl.getName().equals(templateName)) {
                break;
            }
        }
        if (tmpl != null) {
            return this.objectTypeNodesMap.get(tmpl);
        } else {
        	log.debug(Messages.getString("RuleCompiler.deftemplate.error")); //$NON-NLS-1$
            return null;
        }
    }

    public QueryObjTypeNode findQueryObjTypeNode(Template template) {
    	return this.currentQuery.getQueryRootNode().findQueryObjTypeNode(template);
    }
    
	/**
     * Implementation will check to see if the 
	 * @see org.jamocha.rete.RuleCompiler#addListener(org.jamocha.rete.CompilerListener)
	 */
	public void addListener(CompilerListener listener) {
        if (!this.listener.contains(listener)) {
            this.listener.add(listener);
        }
	}

	/* (non-Javadoc)
	 * @see woolfel.engine.rete.RuleCompiler#removeListener(woolfel.engine.rete.CompilerListener)
	 */
	public void removeListener(CompilerListener listener) {
        this.listener.remove(listener);
	}
  

    public Rete getEngine() {
		return engine;
	}

	/**
     * method compiles a literalConstraint
     * @param cnstr
     * @param templ
     * @param rule
     * @param current
     * @return
     */
    public QueryBaseAlpha compileConstraint(LiteralConstraint cnstr,
            Template templ, Query query) {
        QueryBaseAlphaCondition current = null;
        if (templ.getSlot(cnstr.getName()) != null) {
            Slot sl = (Slot) templ.getSlot(cnstr.getName()).clone();
            Object sval = ConversionUtils.convert(sl.getValueType(), cnstr
                    .getValue());
            sl.value = sval;
            current = new QueryAlphaNode(engine.nextNodeId());
            
            current.setSlot(sl);
            current.setOperator(Constants.EQUAL);
            current.incrementUseCount();
            // we increment the node use count when when create a new
            // AlphaNode for the LiteralConstraint
            templ.getSlot(sl.getId()).incrementNodeCount();
        }
        return current;
    }
    
    /**
     * method compiles AndLiteralConstraint into alpha nodes
     * @param cnstr
     * @param templ
     * @param rule
     * @param current
     * @return
     */
    public QueryBaseAlpha compileConstraint(AndLiteralConstraint cnstr,
            Template templ, Query query) {
    	QueryBaseAlphaCondition current = null;
        if (templ.getSlot(cnstr.getName()) != null) {
            Slot2 sl = new Slot2(cnstr.getName());
            sl.setId( templ.getColumnIndex(cnstr.getName()));
            Object sval = cnstr.getValue();
            sl.setValue(sval);
            current = new QueryAndAlphaNode(engine.nextNodeId());
            
            current.setSlot(sl);
            current.incrementUseCount();
            // we increment the node use count when when create a new
            // AlphaNode for the LiteralConstraint
            templ.getSlot(sl.getId()).incrementNodeCount();
        }
        return current;
    }
    
    /**
     * method compiles OrLiteralConstraint into alpha nodes
     * @param cnstr
     * @param templ
     * @param rule
     * @param current
     * @return
     */
    public QueryBaseAlpha compileConstraint(OrLiteralConstraint cnstr,
            Template templ, Query query) {
    	QueryBaseAlphaCondition current = null;
        if (templ.getSlot(cnstr.getName()) != null) {
            Slot2 sl = new Slot2(cnstr.getName());
            sl.setId( templ.getColumnIndex(cnstr.getName()));
            Object sval = cnstr.getValue();
            sl.setValue(sval);
            current = new QueryOrAlphaNode(engine.nextNodeId());
            
            current.setSlot(sl);
            current.incrementUseCount();
            // we increment the node use count when when create a new
            // AlphaNode for the LiteralConstraint
            templ.getSlot(sl.getId()).incrementNodeCount();
        }
        return current;
    }
    
    /**
     * method creates Bindings from the bound constraint and adds them to
     * the Rule.
     * @param cnstr
     * @param templ
     * @param rule
     * @param position
     * @return
     */
    public QueryBaseAlpha compileConstraint(BoundConstraint cnstr,
            Template templ, Query query, int position) {
    	QueryBaseAlphaCondition current = new QueryParameterNode(engine.nextNodeId());
        if (query.getBinding(cnstr.getVariableName()) == null) {
            // if the HashMap doesn't already contain the binding, we create
            // a new one
            if (cnstr.getIsObjectBinding()) {
                Binding bind = new Binding();
                bind.setVarName( cnstr.getVariableName() );
                bind.setLeftRow(position);
                bind.setLeftIndex( -1 );
                bind.setIsObjectVar(true);
                query.addBinding(cnstr.getVariableName(),bind);
            } else {
                Binding bind = new Binding();
                bind.setVarName( cnstr.getVariableName() );
                bind.setLeftRow(position);
                bind.setLeftIndex( templ.getSlot(cnstr.getName()).getId() );
                bind.setRowDeclared(position);
                cnstr.setFirstDeclaration(true);
                query.addBinding(cnstr.getVariableName(),bind);
            }
        }
        // need to enhance this to handle multiple
        if (cnstr.hasIntraFactJoin()) {
            QueryIntraFactNode ifnode = new QueryIntraFactNode(engine.nextNodeId());
            BoundConstraint first = cnstr.getFirstIFJ();
            Binding rightbind = ((Binding)query.getBinding((String)first.getValue()));
            Slot left = (Slot) templ.getSlot(cnstr.getName()).clone();
            Slot right = (Slot) templ.getSlot(rightbind.getLeftIndex()).clone();
            ifnode.setSlot(left);
            ifnode.setRightSlot(right);
            ifnode.incrementUseCount();
            if (first.getNegated()) {
                ifnode.setOperator(Constants.NOTEQUAL);
            } else {
                ifnode.setOperator(Constants.EQUAL);
            }
            current = ifnode;
        } else {
            QueryParameterNode qpn = (QueryParameterNode)current;
            qpn.setParameterName(cnstr.getVariableName());
            Slot slot = (Slot)templ.getSlot(cnstr.getName());
            qpn.setSlot(slot);
            ((Defquery)query).addQueryParameterNode(qpn);
        }
        return current;
    }
    
    /**
     * 
     * @param cnstr
     * @param templ
     * @param rule
     * @param position
     * @return
     */
	public QueryBaseAlpha compileConstraint(PredicateConstraint cnstr,
            Template templ, Query query, int position) {
    	QueryBaseAlphaCondition current = null;
        // Queries are different than rules in that the value will be
    	// set when the query is executed.

    	if (ConversionUtils.isPredicateOperatorCode(cnstr.getFunctionName())) {
            int oprCode = ConversionUtils.getOperatorCode(cnstr.getFunctionName());
            if (cnstr.reverseOperator()) {
            	oprCode = ConversionUtils.getOppositeOperatorCode(oprCode);
            }
            Slot sl = (Slot)templ.getSlot(cnstr.getName()).clone();
            QueryParameterNode node = new QueryParameterNode(engine.nextNodeId());
            current = node;
            node.setSlot(sl);
            node.setOperator(oprCode);
            // get the Parameter that is the variable declared for the query
            String variable = null;
            List<?> params = cnstr.getParameters();
            for (int i=0; i < params.size(); i++) {
            	BoundParam p = (BoundParam)params.get(i);
            	String var = p.getVariableName();
            	if ( ((Defquery)query).isQueryParameter(var) ) {
            		variable = var;
            		break;
            	}
            }
            node.setParameterName(variable);
            ((Defquery)query).addQueryParameterNode(node);
    	} else {
            Function f = engine.findFunction(cnstr.getFunctionName());
            if (f != null) {
                // we create the alphaNode if a function is found and
                // the return type is either boolean primitive or object
                if (f.getReturnType() == Constants.BOOLEAN_PRIM_TYPE || 
                    f.getReturnType() == Constants.BOOLEAN_OBJECT) {

                	Parameter[] parameters = new Parameter[cnstr.getParameters().size()];
                	parameters = (Parameter[])cnstr.getParameters().toArray(parameters);
                	// configure the parameters
                	compileParameters(parameters, cnstr, engine, templ, query);
                	Slot pslot = (Slot)templ.getSlot(cnstr.getName());

                	QueryFuncAlphaNode node = new QueryFuncAlphaNode(engine.nextNodeId(), f, parameters, pslot);
                	node.incrementUseCount();
                	node.setParameterName(cnstr.getVariableName());
                	((Defquery)query).addQueryFuncNode(node);
                	current = node;
                } else {
                    // the function doesn't return boolean, so we have to notify
                    // the listeners the condition is not valid
                    CompileEvent ce = 
                        new CompileEvent(this,CompileEvent.FUNCTION_INVALID);
                    ce.setMessage(INVALID_FUNCTION + " " + f.getReturnType()); //$NON-NLS-1$
                    this.notifyListener(ce);
                }
            } else {
                // we need to notify listeners the function wasn't found
                CompileEvent ce = 
                    new CompileEvent(this,CompileEvent.FUNCTION_NOT_FOUND);
                // ce.setMessage(FUNCTION_NOT_FOUND + " " + f.getReturnType()); //$NON-NLS-1$
                ce.setMessage(FUNCTION_NOT_FOUND + " Null return type"); // TODO
                this.notifyListener(ce);
            }
    	}
        
        Binding bind = new Binding();
        bind.setVarName( cnstr.getVariableName() );
        bind.setLeftRow(position);
        bind.setLeftIndex( templ.getSlot(cnstr.getName()).getId() );
        bind.setRowDeclared(position);
        // we only add the binding to the map if it doesn't already exist
        if (query.getBinding(cnstr.getVariableName()) == null) {
            query.addBinding(cnstr.getVariableName(),bind);
        }
        return current;
    }
    
    public void compileParameters(Parameter[] parameters, PredicateConstraint constraint, Rete engine, Template template, Query query) {
    	for (int px=0; px < parameters.length; px++) {
    		if (parameters[px] instanceof BoundParam) {
    			BoundParam bp = (BoundParam)parameters[px];
    			bp.setColumn(template.getSlot(constraint.getName()).getId());
    			bp.setRow(0);
    		} else if (parameters[px] instanceof FunctionParam2) {
    			FunctionParam2 fp = (FunctionParam2)parameters[px];
    			fp.configure(engine, query);
    		}
    	}
    }
    
    public void compileJoins(GraphQuery query, Condition[] conds)
    throws AssertException
    {
       QueryBaseJoin prevJoinNode = null;
       QueryBaseJoin joinNode = null;
       Condition prevCE = null;
        // only if there's more than 1 condition do we attempt to 
        // create the join nodes. A rule with just 1 condition has
        // no joins
        if (conds.length > 1) {
            // previous Condition
            prevCE = conds[0];
            prevCE.getCompiler(this).compileFirstJoin(prevCE, query);
            
            
            // now compile the remaining conditions
            for (int idx=1; idx < conds.length; idx++) {
                Condition cdt = conds[idx];

                joinNode = cdt.getCompiler(this).compileJoin(cdt, idx, query, prevCE);
                // we call a different method than defaultquerycompiler
                cdt.getCompiler(this).connectJoinNode(prevCE, cdt, prevJoinNode, joinNode, this);
                
                if (idx == 1) {
                	prevCE.getLastNode().addSuccessorNode(joinNode, this.engine, this.engine.getWorkingMemory());
                }
                // now we set the previous node to current
                prevCE = cdt;
                prevJoinNode = joinNode;
                query.addJoinNode(joinNode);
            }
        } else if (conds.length == 1){
        	conds[0].getCompiler(this).compileSingleCE(query);
        }
    }
    
    /**
     * Method will attach a new JoinNode to an ancestor node. An ancestor
     * could be LIANode, AlphaNode or BetaNode.
     * @param last
     * @param join
     * @throws AssertException
     */
    public void attachJoinNode(BaseNode last, BaseJoin join) 
    throws AssertException
    {
        if (last instanceof BaseAlpha) {
            ((BaseAlpha)last).addSuccessorNode(join,engine,null);
        } else if (last instanceof BaseJoin) {
            ((BaseJoin)last).addSuccessorNode(join,engine,null);
        }
    }
    
    public void attachJoinNode(BaseNode last, QueryBaseJoin join) 
    throws AssertException
    {
        if (last instanceof QueryBaseAlpha) {
            ((QueryBaseAlpha)last).addSuccessorNode(join,engine,null);
        } else if (last instanceof QueryBaseJoin) {
            ((QueryBaseJoin)last).addSuccessorNode(join,engine,null);
        }
    }
    
    /**
     * method will find the first LeftInputAdapter node for the
     * ObjectTypeNode. There should only be one that is a direct
     * successor.
     * @param otn
     * @return
     */
    public QueryLIANode findQueryLIANode(QueryObjTypeNode otn) {
    	QueryLIANode node = null;
        if (otn.getSuccessorNodes() != null && otn.getSuccessorNodes().length > 0) {
            Object[] nodes = (Object[])otn.getSuccessorNodes();
            for (int idx=0; idx < nodes.length; idx++) {
                if (nodes[idx] instanceof QueryLIANode) {
                    node = (QueryLIANode)nodes[idx];
                    break;
                }
            }
        } else if (otn.getSuccessorNodes().length == 0) {
        	node = new QueryLIANode(this.engine.nextNodeId());
        	try {
            	otn.addSuccessorNode(node, engine, null);
        	} catch (AssertException e) {
        		// do nothing
        	}
        }
        return node;
    }
    
    
    public void fireErrorEvent(Object reason) {
        
    }

    /**
     * basic method iterates over the listeners and passes the event, checking
     * what kind of event it is and calling the appropriate method.
     * @param event
     */
    public void notifyListener(CompileEvent event) {
        Iterator<CompilerListener> itr = this.listener.iterator();
        //engine.writeMessage(event.getMessage());
        while (itr.hasNext()) {
            CompilerListener listen = itr.next();
            int etype = event.getEventType();
            if (etype == CompileEvent.ADD_RULE_EVENT) {
                listen.ruleAdded(event);
            } else if (etype == CompileEvent.REMOVE_RULE_EVENT) {
                listen.ruleRemoved(event);
            } else {
                listen.compileError(event);
            }
        }
    }

	public Map<Template, QueryObjTypeNode> getObjectTypeNodeMap() {
		return objectTypeNodesMap;
	}
}
