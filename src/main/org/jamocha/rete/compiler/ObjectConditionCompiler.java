package org.jamocha.rete.compiler;

import org.jamocha.rete.BaseAlpha;
import org.jamocha.rete.BaseAlpha2;
import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.BaseNode;
import org.jamocha.rete.Binding;
import org.jamocha.rete.CompileEvent;
import org.jamocha.rete.DefaultQueryCompiler;
import org.jamocha.rete.DefaultRuleCompiler;
import org.jamocha.rete.GraphQueryCompiler;
import org.jamocha.rete.HashedEqBNode;
import org.jamocha.rete.HashedEqNJoin;
import org.jamocha.rete.HashedNotEqBNode;
import org.jamocha.rete.HashedNotEqNJoin;
import org.jamocha.rete.LIANode;
import org.jamocha.rete.NotJoin;
import org.jamocha.rete.NotJoinFrst;
import org.jamocha.rete.ObjectTypeNode;
import org.jamocha.rete.PredicateBNode;
import org.jamocha.rete.QueryCompiler;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.Template;
import org.jamocha.rete.ZJBetaNode;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.query.QueryBaseAlpha;
import org.jamocha.rete.query.QueryBaseAlphaCondition;
import org.jamocha.rete.query.QueryBaseJoin;
import org.jamocha.rete.query.QueryFuncJoin;
import org.jamocha.rete.query.QueryHashedEqJoin;
import org.jamocha.rete.query.QueryHashedEqNot;
import org.jamocha.rete.query.QueryHashedNeqJoin;
import org.jamocha.rete.query.QueryHashedNeqNot;
import org.jamocha.rete.query.QueryLIANode;
import org.jamocha.rete.query.QueryNotJoin;
import org.jamocha.rete.query.QueryNotJoinFrst;
import org.jamocha.rete.query.QueryObjTypeNode;
import org.jamocha.rete.query.QueryParameterNode;
import org.jamocha.rete.query.QueryZeroJoin;
import org.jamocha.rule.AndLiteralConstraint;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.Defquery;
import org.jamocha.rule.GraphQuery;
import org.jamocha.rule.LiteralConstraint;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.OrLiteralConstraint;
import org.jamocha.rule.PredicateConstraint;
import org.jamocha.rule.Query;
import org.jamocha.rule.Rule;

/**
 * 
 * @author HouZhanbin
 * Oct 12, 2007 9:42:15 AM
 *
 */
public class ObjectConditionCompiler extends AbstractConditionCompiler{
	
	public ObjectConditionCompiler(RuleCompiler ruleCompiler){
		this.ruleCompiler = (DefaultRuleCompiler)ruleCompiler;
	}
	
	public ObjectConditionCompiler(QueryCompiler queryCompiler){
		this.queryCompiler = (DefaultQueryCompiler)queryCompiler;
	}
	
	public ObjectConditionCompiler(GraphQueryCompiler queryCompiler) {
		this.graphCompiler = queryCompiler;
	}
	
	/**
	 * Compile a single ObjectCondition and create the alphaNodes and/or Bindings
	 */
	public void compile(Condition condition, int position, Rule util, boolean alphaMemory) {
		ObjectCondition cond = (ObjectCondition)condition;
        ObjectTypeNode otn = ruleCompiler.findObjectTypeNode(cond.getTemplateName());
        if (otn != null) {
            BaseAlpha first = null;
            BaseAlpha previous = null;
            BaseAlpha current = null;
            Template templ = cond.getTemplate();

            Constraint[] constrs = cond.getConstraints();
            for (int idx=0; idx < constrs.length; idx++) {
                Constraint cnstr = constrs[idx];
                if (cnstr instanceof LiteralConstraint) {
                    current = 
                    	ruleCompiler.compileConstraint((LiteralConstraint)cnstr, templ, util);
                } else if (cnstr instanceof AndLiteralConstraint) {
                    current = 
                    	ruleCompiler.compileConstraint((AndLiteralConstraint)cnstr, templ, util);
                } else if (cnstr instanceof OrLiteralConstraint) {
                    current = 
                    	ruleCompiler.compileConstraint((OrLiteralConstraint)cnstr, templ, util);
                } else if (cnstr instanceof BoundConstraint) {
                    BoundConstraint bc = (BoundConstraint)cnstr;
                    BaseAlpha2 ifn =
                        ruleCompiler.compileConstraint(bc, templ, util, position);
                    if (ifn != null) {
                        current = ifn;
                    }
                } else if (cnstr instanceof PredicateConstraint) {
                	PredicateConstraint pcon = (PredicateConstraint)cnstr;
                    current = 
                    	ruleCompiler.compileConstraint(pcon, templ, util, position);
                    if (pcon.isPredicateJoin()) {
                    	cond.setHasPredicateJoin(true);
                    }
                }
                // we add the node to the previous
                if (first == null) {
                	if (current != null) {
                        first = current;
                        previous = current;
                	}
                } else if (current != null && current != previous){
                    try {
                        previous.addSuccessorNode(current,ruleCompiler.getEngine(),ruleCompiler.getMemory());
                        // now set the previous to current
                        previous = current;
                    } catch (AssertException e) {
                        // send an event
                    }
                }
            }
            if (first != null) {
            	attachAlphaNode(otn,first,cond);
            }
        }
		
	}
	
	public void compile(Condition condition, int position, Query query) {
		if (query instanceof GraphQuery) {
			compile(condition, position, (GraphQuery)query);
		} else {
			ObjectCondition cond = (ObjectCondition)condition;
	        QueryObjTypeNode queryOTN = queryCompiler.findQueryObjTypeNode(cond.getTemplate());
	        
	        if (queryOTN != null) {
	        	QueryBaseAlpha first = null;
	            QueryBaseAlpha previous = null;
	            QueryBaseAlpha current = null;
	            Template templ = cond.getTemplate();

	            Constraint[] constrs = cond.getConstraints();
	            for (int idx=0; idx < constrs.length; idx++) {
	                Constraint cnstr = constrs[idx];
	                if (cnstr instanceof LiteralConstraint) {
	                    current = 
	                    	queryCompiler.compileConstraint((LiteralConstraint)cnstr, templ, query);
	                } else if (cnstr instanceof AndLiteralConstraint) {
	                    current = 
	                    	queryCompiler.compileConstraint((AndLiteralConstraint)cnstr, templ, query);
	                } else if (cnstr instanceof OrLiteralConstraint) {
	                    current = 
	                    	queryCompiler.compileConstraint((OrLiteralConstraint)cnstr, templ, query);
	                } else if (cnstr instanceof BoundConstraint) {
	                	current =
	                		queryCompiler.compileConstraint((BoundConstraint)cnstr, templ, query, position);
	                } else if (cnstr instanceof PredicateConstraint) {
	                	PredicateConstraint pcon = (PredicateConstraint)cnstr;
	                    current = 
	                    	queryCompiler.compileConstraint(pcon, templ, query, position);
	                }
	                // we add the node to the previous
	                if (first == null) {
	                	if (current != null) {
	                        first = current;
	                        previous = current;
	                	}
	                } else if (current != null && current != previous){
	                    try {
	                        previous.addSuccessorNode(current,queryCompiler.getEngine(),null);
	                        // now set the previous to current
	                        previous = current;
	                    } catch (AssertException e) {
	                        // send an event
	                    }
	                }
	            }
	            if (first != null) {
	            	try {
	            		// Note: This differs from rules, since we do not share nodes for queries.
						queryOTN.addSuccessorNode(first,queryCompiler.getEngine(),null);
						cond.addNewAlphaNodes(first);
					} catch (AssertException e) {
					}
	            }
	        }
		}
	}
	
	public void compile(Condition condition, int position, GraphQuery query) {
		ObjectCondition cond = (ObjectCondition)condition;
        QueryObjTypeNode queryOTN = graphCompiler.findQueryObjTypeNode(cond.getTemplate());
        if (queryOTN != null) {
        	QueryBaseAlpha first = null;
            QueryBaseAlpha previous = null;
            QueryBaseAlpha current = null;
            Template templ = cond.getTemplate();

            Constraint[] constrs = cond.getConstraints();
            for (int idx=0; idx < constrs.length; idx++) {
                Constraint cnstr = constrs[idx];
                if (cnstr instanceof LiteralConstraint) {
                    current = 
                   		graphCompiler.compileConstraint((LiteralConstraint)cnstr, templ, query);
                } else if (cnstr instanceof AndLiteralConstraint) {
                    current = 
                   		graphCompiler.compileConstraint((AndLiteralConstraint)cnstr, templ, query);
                } else if (cnstr instanceof OrLiteralConstraint) {
                    current = 
                   		graphCompiler.compileConstraint((OrLiteralConstraint)cnstr, templ, query);
                } else if (cnstr instanceof BoundConstraint) {
                	current =
               			graphCompiler.compileConstraint((BoundConstraint)cnstr, templ, query, position);
                } else if (cnstr instanceof PredicateConstraint) {
                	PredicateConstraint pcon = (PredicateConstraint)cnstr;
                    current = 
                    	graphCompiler.compileConstraint(pcon, templ, query, position);
                }
                // we add the node to the previous
                if (first == null) {
                	if (current != null) {
                        first = current;
                        previous = current;
                	}
                } else if (current != null && current != previous){
                    try {
                    	if (current instanceof QueryParameterNode) {
                    		String pname = ((QueryParameterNode)current).getParameterName();
                    		if (query.isQueryParameter(pname)) {
                                previous.addSuccessorNode(current,graphCompiler.getEngine(),null);
                                previous = current;
                    		}
                    	} else {
                            previous.addSuccessorNode(current,graphCompiler.getEngine(),null);
                            previous = current;
                    	}
                    } catch (AssertException e) {
                        // send an event
                    }
                }
            }
            if (first != null && !(first instanceof QueryParameterNode)) {
            	try {
            		// Note: This differs from rules, since we do not share nodes for queries.
					queryOTN.addSuccessorNode(first,graphCompiler.getEngine(),null);
					cond.addNewAlphaNodes(first);
				} catch (AssertException e) {
				}
            }
        }
	}	
	
    /**
     * For now just attach the node and don't bother with node sharing
     * @param existing - an existing node in the network. it may be
     * an ObjectTypeNode or AlphaNode
     * @param alpha
     */
    public void attachAlphaNode(BaseAlpha existing, BaseAlpha alpha, Condition cond) {
        if (alpha != null) {
            try {
                BaseAlpha share = null;
                share = shareAlphaNode(existing,alpha);
                if (share == null) {
                    existing.addSuccessorNode(alpha,ruleCompiler.getEngine(),ruleCompiler.getMemory());
                    // if the node isn't shared, we add the node to the Condition
                    // object the node belongs to.
                    cond.addNewAlphaNodes(alpha);
                } else if (existing != alpha) {
                    // the node is shared, so instead of adding the new node,
                    // we add the existing node
                	share.incrementUseCount();
                    cond.addNode(share);
                    ruleCompiler.getMemory().removeAlphaMemory(alpha);
                    if (alpha.successorCount() == 1 &&
                            alpha.getSuccessorNodes()[0] instanceof BaseAlpha) {
                        // get the next node from the new AlphaNode
                        BaseAlpha nnext = (BaseAlpha)alpha.getSuccessorNodes()[0];
                        attachAlphaNode(share,nnext,cond);
                    }
                }
            } catch (AssertException e) {
                // send an event with the correct error
                CompileEvent ce = new CompileEvent(this,CompileEvent.ADD_NODE_ERROR);
                ce.setMessage(alpha.toPPString());
                ruleCompiler.notifyListener(ce);
            }
        }
    }

    /**
     * Implementation will get the hashString from each node and compare them
     * @param otn
     * @param alpha
     * @return
     */
    private BaseAlpha shareAlphaNode(BaseAlpha existing, BaseAlpha alpha) {
        Object[] scc = existing.getSuccessorNodes();
        for (int idx=0; idx < scc.length; idx++) {
            Object next = scc[idx];
            if (next instanceof BaseAlpha) {
                BaseAlpha baseAlpha = (BaseAlpha)next;
                if (baseAlpha.hashString().equals(alpha.hashString())) {
                    return baseAlpha;
                }
            }
        }
        return null;
    }

	public void compileFirstJoin(Condition condition, Rule rule) throws AssertException{
        ObjectCondition cond = (ObjectCondition) condition;
        ObjectTypeNode otn = ruleCompiler.findObjectTypeNode(cond.getTemplateName());
        // the LeftInputAdapterNode is the first node to propogate to
        // the first joinNode of the rule
        LIANode node = new LIANode(ruleCompiler.getEngine().nextNodeId());
        // if the condition doesn't have any nodes, we want to add it to
        // the objectType node if one doesn't already exist.
        // otherwise we add it to the last AlphaNode
        if (cond.getNodes().size() == 0) {
            // try to find the existing LIANode for the given ObjectTypeNode
            // if we don't do this, we end up with multiple LIANodes
            // descending directly from the ObjectTypeNode
            LIANode existingLIANode = ruleCompiler.findLIANode(otn);
            if (existingLIANode == null) {
                otn.addSuccessorNode(node, ruleCompiler.getEngine(), ruleCompiler.getMemory());
                cond.addNode(node);
            } else {
                existingLIANode.incrementUseCount();
                cond.addNode(existingLIANode);
            }
        } else {
            // add the LeftInputAdapterNode to the last alphaNode
            // In the case of node sharing, the LIANode could be the last
            // alphaNode, so we have to check and only add the node to 
            // the condition if it isn't a LIANode
            BaseAlpha old = (BaseAlpha) cond.getLastNode();
            //if the last node of condition has a LIANode successor,
            //the LIANode should be shared with the new CE followed by another CE.
            // Houzhanbin,10/16/2007
            	BaseNode[] successors=(BaseNode[])old.getSuccessorNodes();
                for(int i=0;i<successors.length;i++){
                	if(successors[i] instanceof LIANode){
                		cond.addNode(successors[i]);
                		return;
                	}
                }

            if (!(old instanceof LIANode)) {
                old.addSuccessorNode(node, ruleCompiler.getEngine(), ruleCompiler.getMemory());
                cond.addNode(node);
            }
        }
	}
	
	public void compileFirstJoin(Condition condition, Query query) throws AssertException{
		if (query instanceof GraphQuery) {
			compileFirstJoin(condition,(GraphQuery)query);
		} else {
			Defquery dquery = (Defquery)query;
	        ObjectCondition cond = (ObjectCondition) condition;
	        Template template = this.queryCompiler.getEngine().findTemplate(cond.getTemplateName());
	        QueryObjTypeNode queryotn = dquery.getQueryRootNode().findQueryObjTypeNode(template);

	        QueryLIANode node = new QueryLIANode(queryCompiler.getEngine().nextNodeId());
	        // if the condition doesn't have any nodes, we want to add it to
	        // the objectType node if one doesn't already exist.
	        // otherwise we add it to the last AlphaNode
	        if (cond.getNodes().size() == 0) {
	            // try to find the existing LIANode for the given ObjectTypeNode
	            // if we don't do this, we end up with multiple LIANodes
	            // descending directly from the ObjectTypeNode
	        	QueryLIANode existingLIANode = queryCompiler.findQueryLIANode(queryotn);
	            if (existingLIANode == null) {
	            	queryotn.addSuccessorNode(node, queryCompiler.getEngine(), null);
	                cond.addNode(node);
	            } else {
	                existingLIANode.incrementUseCount();
	                cond.addNode(existingLIANode);
	            }
			} else {
				// add the LeftInputAdapterNode to the last alphaNode
				// In the case of node sharing, the LIANode could be the last
				// alphaNode, so we have to check and only add the node to
				// the condition if it isn't a LIANode
				QueryBaseAlphaCondition old = (QueryBaseAlphaCondition) cond.getLastNode();
				// if the last node of condition has a LIANode successor,
				// the LIANode should be shared with the new CE followed by another CE.
				// Houzhanbin,10/16/2007
				BaseNode[] successors = (BaseNode[]) old.getSuccessorNodes();
				for (int i = 0; i < successors.length; i++) {
					if (successors[i] instanceof LIANode) {
						cond.addNode(successors[i]);
						return;
					}
				}
				old.addSuccessorNode(node, queryCompiler.getEngine(), null);
				cond.addNode(node);
			}
		}
	}
	
	public void compileFirstJoin(Condition condition, GraphQuery query) throws AssertException {
        ObjectCondition cond = (ObjectCondition) condition;
        Template template = this.graphCompiler.getEngine().findTemplate(cond.getTemplateName());
        QueryObjTypeNode queryotn = this.graphCompiler.findQueryObjTypeNode(template);

        QueryLIANode node = new QueryLIANode(graphCompiler.getEngine().nextNodeId());
        if (cond.getNodes().size() == 0) {
        	
        	QueryLIANode existingLIANode = graphCompiler.findQueryLIANode(queryotn);
            if (existingLIANode == null) {
            	queryotn.addSuccessorNode(node, graphCompiler.getEngine(), null);
                cond.addNode(node);
            } else {
                existingLIANode.incrementUseCount();
                cond.addNode(existingLIANode);
            }
		} else {
			// add the LeftInputAdapterNode to the last alphaNode
			// In the case of node sharing, the LIANode could be the last
			// alphaNode, so we have to check and only add the node to
			// the condition if it isn't a LIANode
			QueryBaseAlphaCondition old = (QueryBaseAlphaCondition) cond.getLastNode();
			// if the last node of condition has a LIANode successor,
			// the LIANode should be shared with the new CE followed by another CE.
			// Houzhanbin,10/16/2007
			BaseNode[] successors = (BaseNode[]) old.getSuccessorNodes();
			for (int i = 0; i < successors.length; i++) {
				if (successors[i] instanceof LIANode) {
					cond.addNode(successors[i]);
					return;
				}
			}
			old.addSuccessorNode(node, graphCompiler.getEngine(), null);
			cond.addNode(node);
		}
	}
	
	/**
	 * method compiles ObjectConditions, which include NOTCE
	 */
	public BaseJoin compileJoin(Condition condition, int position, Rule rule, Condition previousCond) {
		
        Binding[] binds = getBindings(condition,rule,position);
        ObjectCondition oc = (ObjectCondition)condition;
        BaseJoin joinNode = null;
        //deal with the CE which is not NOT CE.
        if ( !oc.getNegated() ) {
            if (binds.length > 0 && oc.isHasPredicateJoin()) {
                joinNode = new PredicateBNode(ruleCompiler.getEngine().nextNodeId());
            } else if (binds.length > 0 && oc.isHasNotEqual()) {
            	joinNode = new HashedNotEqBNode(ruleCompiler.getEngine().nextNodeId());
            } else if (binds.length > 0) {
            	joinNode = new HashedEqBNode(ruleCompiler.getEngine().nextNodeId());
            } else if (binds.length == 0) {
                joinNode = new ZJBetaNode(ruleCompiler.getEngine().nextNodeId());
            }
        }
        
        //deal with the CE which is NOT CE.
        if(oc.getNegated()){
            if (binds.length > 0 && oc.isHasPredicateJoin()) {
                joinNode = new NotJoin(ruleCompiler.getEngine().nextNodeId());
            } else if (oc.isHasNotEqual()) {
                joinNode = new HashedNotEqNJoin(ruleCompiler.getEngine().nextNodeId());
            } else {
                joinNode = new HashedEqNJoin(ruleCompiler.getEngine().nextNodeId());
            }
        }
        
        joinNode.setBindings(binds);
		return joinNode;
	}

	public QueryBaseJoin compileJoin(Condition condition, int position, Query query, Condition previousCond) {
		if (query instanceof GraphQuery) {
			return compileJoin(condition, position, (GraphQuery)query, previousCond);
		} else {
	        Binding[] binds = getBindings(condition,query,position);
	        ObjectCondition oc = (ObjectCondition)condition;
	        QueryBaseJoin joinNode = null;
	        //deal with the CE which is not NOT CE.
	        if ( !oc.getNegated() ) {
	            if (binds.length > 0 && oc.isHasPredicateJoin()) {
	                joinNode = new QueryFuncJoin(queryCompiler.getEngine().nextNodeId());
	            } else if (binds.length > 0 && oc.isHasNotEqual()) {
	            	joinNode = new QueryHashedNeqJoin(queryCompiler.getEngine().nextNodeId());
	            } else if (binds.length > 0) {
	            	joinNode = new QueryHashedEqJoin(queryCompiler.getEngine().nextNodeId());
	            } else if (binds.length == 0) {
	                joinNode = new QueryZeroJoin(queryCompiler.getEngine().nextNodeId());
	            }
	        }
	        
	        //deal with the CE which is NOT CE.
	        if(oc.getNegated()){
	            if (binds.length > 0 && oc.isHasPredicateJoin()) {
	                joinNode = new QueryNotJoin(queryCompiler.getEngine().nextNodeId());
	            } else if (oc.isHasNotEqual()) {
	                joinNode = new QueryHashedNeqNot(queryCompiler.getEngine().nextNodeId());
	            } else {
	                joinNode = new QueryHashedEqNot(queryCompiler.getEngine().nextNodeId());
	            }
	        }
	        
	        joinNode.setBindings(binds);
			return joinNode;
		}
	}
	
	public QueryBaseJoin compileJoin(Condition condition, int position, GraphQuery query, Condition previousCond) {
        Binding[] binds = getBindings(condition,query,position);
        ObjectCondition oc = (ObjectCondition)condition;
        QueryBaseJoin joinNode = null;
        //deal with the CE which is not NOT CE.
        if ( !oc.getNegated() ) {
            if (binds.length > 0 && oc.isHasPredicateJoin()) {
                joinNode = new QueryFuncJoin(graphCompiler.getEngine().nextNodeId());
            } else if (binds.length > 0 && oc.isHasNotEqual()) {
            	joinNode = new QueryHashedNeqJoin(graphCompiler.getEngine().nextNodeId());
            } else if (binds.length > 0) {
            	joinNode = new QueryHashedEqJoin(graphCompiler.getEngine().nextNodeId());
            } else if (binds.length == 0) {
                joinNode = new QueryZeroJoin(graphCompiler.getEngine().nextNodeId());
            }
        }
        
        //deal with the CE which is NOT CE.
        if(oc.getNegated()){
            if (binds.length > 0 && oc.isHasPredicateJoin()) {
                joinNode = new QueryNotJoin(graphCompiler.getEngine().nextNodeId());
            } else if (oc.isHasNotEqual()) {
                joinNode = new QueryHashedNeqNot(graphCompiler.getEngine().nextNodeId());
            } else {
                joinNode = new QueryHashedEqNot(graphCompiler.getEngine().nextNodeId());
            }
        }
        
        joinNode.setBindings(binds);
		return joinNode;
	}

	@Override
	ObjectCondition getObjectCondition(Condition condition) {
		return (ObjectCondition)condition;
	}

	public void compileSingleCE(Rule rule) throws AssertException{
		Condition[] conds=rule.getConditions();
		ObjectCondition oc = (ObjectCondition)conds[0];
		if (oc.getNegated()) {
			// the ObjectCondition is negated, so we need to
			// handle it appropriate. This means we need to
			// add a LIANode to _IntialFact and attach a NOTNode
			// to the LIANode.
			ObjectTypeNode otn = (ObjectTypeNode)this.ruleCompiler.getInputnodes().get(ruleCompiler.getEngine().getInitFact());
			LIANode lianode = ruleCompiler.findLIANode(otn);
			NotJoinFrst njoin = new NotJoinFrst(ruleCompiler.getEngine().nextNodeId());
			njoin.setBindings(new Binding[0]);
			lianode.addSuccessorNode(njoin,ruleCompiler.getEngine(),ruleCompiler.getMemory());
            // add the join to the rule object
            rule.addJoinNode(njoin);
            oc.getLastNode().addSuccessorNode(njoin,ruleCompiler.getEngine(), ruleCompiler.getMemory());
		} else if (oc.getNodes().size() == 0){
            // this means the rule has a binding, but no conditions
            ObjectTypeNode otn = ruleCompiler.findObjectTypeNode(oc.getTemplateName());
            LIANode lianode = new LIANode(ruleCompiler.getEngine().nextNodeId());
            otn.addSuccessorNode(lianode, ruleCompiler.getEngine(), ruleCompiler.getMemory());
            rule.getConditions()[0].addNode(lianode);
        }
		
	}

	public void compileSingleCE(Query query) throws AssertException{
		Condition[] conds=query.getConditions();
		ObjectCondition oc = (ObjectCondition)conds[0];
		Defquery dquery = (Defquery)query;
		if (oc.getNegated()) {
	        Template template = this.queryCompiler.getEngine().findTemplate(queryCompiler.getEngine().getInitFact().getName());
	        QueryObjTypeNode queryotn = dquery.getQueryRootNode().findQueryObjTypeNode(template);
			QueryLIANode querylianode = queryCompiler.findQueryLIANode(queryotn);
			
			QueryNotJoinFrst njoin = new QueryNotJoinFrst(queryCompiler.getEngine().nextNodeId());
			njoin.setBindings(new Binding[0]);
			querylianode.addSuccessorNode(njoin,queryCompiler.getEngine(), null);
            // add the join to the rule object
            query.addJoinNode(njoin);
            oc.getLastNode().addSuccessorNode(njoin,queryCompiler.getEngine(), null);
		} else if (oc.getNodes().size() == 0){
	        Template template = this.queryCompiler.getEngine().findTemplate(queryCompiler.getEngine().getInitFact().getName());
	        QueryObjTypeNode queryotn = dquery.getQueryRootNode().findQueryObjTypeNode(template);
            // this means the rule has a binding, but no conditions
            QueryLIANode lianode = new QueryLIANode(queryCompiler.getEngine().nextNodeId());
            queryotn.addSuccessorNode(lianode, queryCompiler.getEngine(), null);
            query.getConditions()[0].addNode(lianode);
        } else {
        	
        }
	}
}
