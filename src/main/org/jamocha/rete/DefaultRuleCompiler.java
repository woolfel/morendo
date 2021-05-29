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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rule.*;

/**
 * @author Peter Lin
 *
 * DefaultRuleCompiler is a basic implementation. It does not handle logical patterns,
 * or demorgan's theorem. Writing deeply nested logical statements is generally a bad
 * idea and leads to complexity. One day it may be supported, but for now the recommendation
 * is to write separate rules rather than having a NOTCE nest complex logical patterns
 */
public class DefaultRuleCompiler implements RuleCompiler {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WorkingMemory memory = null;
    private Rete engine = null;
	private Map<Template, ObjectTypeNode> inputnodes = null;
    private Module currentMod = null;
    
	private ArrayList<CompilerListener> listener = new ArrayList<CompilerListener>();
    protected boolean validate = true;
    protected TemplateValidation tval = null;
    
    public static final String FUNCTION_NOT_FOUND = 
        Messages.getString("CompilerProperties.function.not.found"); //$NON-NLS-1$
    public static final String INVALID_FUNCTION = 
        Messages.getString("CompilerProperties.invalid.function"); //$NON-NLS-1$
    public static final String ASSERT_ON_PROPOGATE = 
        Messages.getString("CompilerProperties.assert.on.add"); //$NON-NLS-1$
    
    protected Logger log = LogFactory.createLogger(DefaultRuleCompiler.class);

    
	public DefaultRuleCompiler(Rete engine, Map<Template, ObjectTypeNode> inputNodes) {
		super();
        this.engine = engine;
        this.inputnodes = inputNodes;
        this.tval = new TemplateValidation(engine);
	}
	
	public void setValidateRule(boolean valid) {
		this.validate = valid;
	}
	
	public boolean getValidateRule() {
		return this.validate;
	}

    public void setWorkingMemory(WorkingMemory wm) {
        this.memory = wm;
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
     * 3. create the terminal node and attach to the last
     * join node.
     * 
     * This means the rule compiler takes a 2 pass approach to
     * compiling rules. At the start of the method, it sets 3
     * attributes to null: prevCE, prevJoinNode, joinNode.
     * Those attributes are used by the compile join methods,
     * so it's important to set it to null at the start. If
     * we don't the next rule won't compile correctly.
     */
	public boolean addRule(Rule rule) {
		rule.resolveTemplates(engine);
        rule.getComplexity().calculateComplexity();
		if (!this.validate || (this.validate && this.tval.analyze(rule) == Analysis.VALIDATION_PASSED)) {
            // we have to set the attributes to null, before we start compiling a rule.

            // we've set the attributes to null, so we can compile now!!
            
			if (rule.getConditions() != null && rule.getConditions().length > 0) {
                // we check the name of the rule to see if it is for a specific
                // module. if it is, we have to add it to that module
                this.setModule(rule);
	            try {
	                Condition[] conds = this.getRuleConditions(rule);
	                // first we create the constraints, before creating the Conditional
	                // elements which include joins
                    // we use a counter and only increment it to make sure the
                    // row index of the bindings are accurate. this makes it simpler
                    // for the rule compiler and compileJoins is cleaner and does
                    // less work.
                    int counter = 0;
	                for (int idx=0; idx < conds.length; idx++) {
	                    Condition con = conds[idx];
	                    // compile object conditions
	                    //implement in the ObjectConditionCompiler.compile or ExistConditionCompiler.compile
	                    con.getCompiler(this).compile(con, counter, rule, rule.getRememberMatch());
	                    
                        if ((con instanceof ObjectCondition) && (!((ObjectCondition)con).getNegated())) {
                        	if (!(con instanceof ExistCondition) && !(con instanceof MultipleCondition)) {
                                counter++;
                        	}
                        }
	                }
                    // now we compile the joins
	                compileJoins(rule,conds);
	                
	                BaseNode last = rule.getLastNode();
	                TerminalNode tnode = createTerminalNode(rule);

	                attachTerminalNode(last,tnode);
	                // compile the actions
	                compileActions(rule,rule.getActions());
	                // compile the modification actions
	                compileActions(rule,rule.getModificationActions());
	                // now we pass the bindings to the rule, so that actions can
	                // resolve the bindings
	                
	                // now we add the rule to the module
	                currentMod.addRule(rule);
	                CompileEvent ce = new CompileEvent(rule,CompileEvent.ADD_RULE_EVENT);
	                ce.setRule(rule);
                    ce.setMessage("Complexity: " + rule.getComplexity().getValue());
	                this.notifyListener(ce);
	                return true;
	            } catch (AssertException e) {
	                CompileEvent ce = new CompileEvent(rule,CompileEvent.INVALID_RULE);
	                ce.setMessage(Messages.getString("RuleCompiler.assert.error")); //$NON-NLS-1$
	                this.notifyListener(ce);
	                log.debug(e);
	                return false;
	            }
	        } else if (rule.getConditions().length == 0){
                this.setModule(rule);
                // the rule has no LHS, this means it only has actions
                BaseNode last = this.inputnodes.get(engine.initFact);
                TerminalNode tnode = createTerminalNode(rule);
                compileActions(rule,rule.getActions());
                attachTerminalNode(last,tnode);
                // now we add the rule to the module
                currentMod.addRule(rule);
                CompileEvent ce = new CompileEvent(rule,CompileEvent.ADD_RULE_EVENT);
                ce.setRule(rule);
                this.notifyListener(ce);
                return true;
            }
            return false;
		} else {
			// we need to print out a message saying the rule was not valid
			Summary error = this.tval.getErrors();
			engine.writeMessage("Rule " + rule.getName() + " was not added. ", Constants.DEFAULT_OUTPUT); //$NON-NLS-1$ //$NON-NLS-2$
			engine.writeMessage(error.getMessage(), Constants.DEFAULT_OUTPUT);
			Summary warn = this.tval.getWarnings();
			engine.writeMessage(warn.getMessage(), Constants.DEFAULT_OUTPUT);
            return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Condition[] getRuleConditions(Rule rule) {
        Condition[] conditions = rule.getConditions();
        ArrayList<Condition> conditionList = new ArrayList<Condition>();
        boolean hasAnd = false;
        for (int idx=0; idx < conditions.length; idx++) {
            if (conditions[idx] instanceof AndCondition) {
                AndCondition and = (AndCondition)conditions[idx];
                conditionList.addAll((Collection<? extends Condition>) and.getNestedConditionalElement());
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
    
    public void setModule(Rule rule) {
        // we check the name of the rule to see if it is for a specific
        // module. if it is, we have to add it to that module
        if (rule.getName().indexOf("::") > 0) { //$NON-NLS-1$
            String text = rule.getName();
            String[] sp = text.split("::"); //$NON-NLS-1$
            rule.setName(sp[1]);
            String modName = sp[0].toUpperCase();
            currentMod = engine.findModule(modName);
            if (currentMod == null) {
                engine.addModule(modName,false);
                currentMod = engine.findModule(modName);
            }
        } else {
            currentMod = engine.getCurrentFocus();
        }
        rule.setModule(currentMod);
    }
    
	/**
	 * The method is responsible for creating the right terminal node based on the
	 * settings for the rule.
	 * @param rl
	 * @return
	 */
	protected TerminalNode createTerminalNode(Rule rl) {
		if (rl.getModificationActions() != null && rl.getModificationActions().length > 0) {
			MLTerminalNode tnode = new MLTerminalNode(engine.nextNodeId(), rl);
			tnode.setNoAgenda(rl.getNoAgenda());
			return tnode;
		} else if (rl.getNoAgenda() && rl.getExpirationDate() == 0) {
            return new NoAgendaTNode(engine.nextNodeId(),rl);
        } else if (rl.getNoAgenda() && rl.getExpirationDate() > 0) {
        	return new NoAgendaTNode2(engine.nextNodeId(),rl);
        } else if (rl.getExpirationDate() > 0) {
        	return new TerminalNode3(engine.nextNodeId(),rl);
        } else {
            return new TerminalNode2(engine.nextNodeId(),rl);
        }
	}
	
	/**
     * implementation uses the deftemplate for the HashMap key and the
     * node for the value. If the node already exists in the HashMap,
     * or the key already exists, the compiler will not add it to the
     * network.
	 */
	public void addObjectTypeNode(ObjectTypeNode node) {
        if (!this.inputnodes.containsKey(node.getDeftemplate())) {
            this.inputnodes.put(node.getDeftemplate(),node);
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
	public void removeObjectTypeNode(ObjectTypeNode node) {
		this.inputnodes.remove(node.getDeftemplate());
		node.clear(this.memory);
		node.clearSuccessors();
	}

	/**
     * if the ObjectTypeNode does not exist, the method will return null.
	 */
	public ObjectTypeNode getObjectTypeNode(Template template) {
		return this.inputnodes.get(template);
	}
    
    /**
     * 
     * @param templateName
     * @return
     */
	public ObjectTypeNode findObjectTypeNode(String templateName) {
        Iterator<Template> itr = this.inputnodes.keySet().iterator();
        Template tmpl = null;
        while (itr.hasNext()) {
            tmpl = itr.next();
            if (tmpl.getName().equals(templateName)) {
                break;
            }
        }
        if (tmpl != null) {
            return this.inputnodes.get(tmpl);
        } else {
        	log.debug(Messages.getString("RuleCompiler.deftemplate.error")); //$NON-NLS-1$
            return null;
        }
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

	public WorkingMemory getMemory() {
		return memory;
	}

	/**
     * method compiles a literalConstraint
     * @param cnstr
     * @param templ
     * @param rule
     * @param current
     * @return
     */
    public BaseAlpha2 compileConstraint(LiteralConstraint cnstr,
            Template templ, Rule rule) {
        BaseAlpha2 current = null;
        if (templ.getSlot(cnstr.getName()) != null) {
            Slot sl = (Slot) templ.getSlot(cnstr.getName()).clone();
            Object sval = ConversionUtils.convert(sl.getValueType(), cnstr
                    .getValue());
            sl.value = sval;
            if (rule.getRememberMatch()) {
                current = new AlphaNode(engine.nextNodeId());
            } else {
                current = new NoMemANode(engine.nextNodeId());
            }
            current.setSlot(sl);
            if (cnstr.getNegated()) {
                current.setOperator(Constants.NOTEQUAL);
            } else {
                current.setOperator(Constants.EQUAL);
            }
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
    public BaseAlpha2 compileConstraint(AndLiteralConstraint cnstr,
            Template templ, Rule rule) {
        BaseAlpha2 current = null;
        if (templ.getSlot(cnstr.getName()) != null) {
            Slot2 sl = new Slot2(cnstr.getName());
            sl.setId( templ.getColumnIndex(cnstr.getName()));
            Object sval = cnstr.getValue();
            sl.setValue(sval);
            if (rule.getRememberMatch()) {
                current = new AlphaNodeAnd(engine.nextNodeId());
            } else {
                current = new NoMemAnd(engine.nextNodeId());
            }
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
    public BaseAlpha2 compileConstraint(OrLiteralConstraint cnstr,
            Template templ, Rule rule) {
        BaseAlpha2 current = null;
        if (templ.getSlot(cnstr.getName()) != null) {
            Slot2 sl = new Slot2(cnstr.getName());
            sl.setId( templ.getColumnIndex(cnstr.getName()));
            Object sval = cnstr.getValue();
            sl.setValue(sval);
            if (rule.getRememberMatch()) {
                current = new AlphaNodeOr(engine.nextNodeId());
            } else {
                current = new NoMemOr(engine.nextNodeId());
            }
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
    public BaseAlpha2 compileConstraint(BoundConstraint cnstr,
            Template templ, Rule rule, int position) {
        BaseAlpha2 current = null;
        if (rule.getBinding(cnstr.getVariableName()) == null) {
            // if the HashMap doesn't already contain the binding, we create
            // a new one
            if (cnstr.getIsObjectBinding()) {
                Binding bind = new Binding();
                bind.setVarName( cnstr.getVariableName() );
                bind.setLeftRow(position);
                bind.setLeftIndex( -1 );
                bind.setIsObjectVar(true);
                rule.addBinding(cnstr.getVariableName(),bind);
            } else {
                Binding bind = new Binding();
                bind.setVarName( cnstr.getVariableName() );
                bind.setLeftRow(position);
                bind.setLeftIndex( templ.getSlot(cnstr.getName()).getId() );
                bind.setRowDeclared(position);
                cnstr.setFirstDeclaration(true);
                // we only add the binding to the rule if it is bindable
                // by that we mean the action of the rule can access the
                // variable
                if (cnstr.getBindableConstraint()) {
                    rule.addBinding(cnstr.getVariableName(),bind);
                }
            }
        }
        // need to enhance this to handle multiple
        if (cnstr.hasIntraFactJoin()) {
            IntraFactNode ifnode = new IntraFactNode(engine.nextNodeId());
            BoundConstraint first = cnstr.getFirstIFJ();
            Binding rightbind = ((Binding)rule.getBinding((String)first.getValue()));
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
	public BaseAlpha compileConstraint(PredicateConstraint cnstr,
            Template templ, Rule rule, int position) {
        BaseAlpha current = null;
        // for now we expect the user to write the predicate in this
        // way (> ?bind value), where the binding is first. this
        // needs to be updated so that we look at the order of the
        // parameters and set the node appropriately

        // we only create an AlphaNode if the predicate isn't
        // joining 2 bindings.
        if (!cnstr.isPredicateJoin()) {
            if (ConversionUtils.isPredicateOperatorCode(cnstr.getFunctionName())) {
            	BaseAlpha2 node;
                int oprCode = ConversionUtils.getOperatorCode(cnstr.getFunctionName());
                if (cnstr.reverseOperator()) {
                	oprCode = ConversionUtils.getOppositeOperatorCode(oprCode);
                }
                Slot sl = (Slot)templ.getSlot(cnstr.getName()).clone();
                Object sval = 
                    ConversionUtils.convert(sl.getValueType(),cnstr.getValue());
                sl.value = sval;
                // create the alphaNode
                if (rule.getRememberMatch()) {
                    if (oprCode == Constants.EQUAL) {
                        node = new AlphaNode(engine.nextNodeId());
                    } else {
                        node = new NumericAlphaNode(engine.nextNodeId());
                    }
                } else {
                    node = new NoMemANode(engine.nextNodeId());
                }
                current = node;
                node.setSlot(sl);
                node.setOperator(oprCode);
                node.incrementUseCount();
                // we increment the node use count when when create a new
                // AlphaNode for the LiteralConstraint
                templ.getSlot(sl.getId()).incrementNodeCount();
            } else {
                // the function isn't a built in predicate function that
                // returns boolean true/false. We look up the function
                Function f = engine.findFunction(cnstr.getFunctionName());
                if (f != null) {
                    // we create the alphaNode if a function is found and
                    // the return type is either boolean primitive or object
                    if (f.getReturnType() == Constants.BOOLEAN_PRIM_TYPE || 
                        f.getReturnType() == Constants.BOOLEAN_OBJECT) {

                    	Parameter[] parameters = new Parameter[cnstr.getParameters().size()];
                    	parameters = (Parameter[])cnstr.getParameters().toArray(parameters);
                    	// configure the parameters
                    	compileParameters(parameters, cnstr, engine, templ, rule);

                    	AlphaNodePredConstr node = new AlphaNodePredConstr(engine.nextNodeId(), f, parameters);
                    	node.slot = (Slot)templ.getSlot(cnstr.getName());
                    	node.incrementUseCount();
                    	current = node;
                    } else {
                        // the function doesn't return boolean, so we have to notify
                        // the listeners the condition is not valid
                        CompileEvent ce = 
                            new CompileEvent(this,CompileEvent.FUNCTION_INVALID);
                        ce.setMessage(INVALID_FUNCTION + " " + f.getName() + " " + f.getReturnType()); //$NON-NLS-1$
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
        }
        Binding bind = new Binding();
        bind.setVarName( cnstr.getVariableName() );
        bind.setLeftRow(position);
        bind.setLeftIndex( templ.getSlot(cnstr.getName()).getId() );
        bind.setRowDeclared(position);
        // we only add the binding to the map if it doesn't already exist
        if (rule.getBinding(cnstr.getVariableName()) == null) {
            rule.addBinding(cnstr.getVariableName(),bind);
        }
        return current;
    }
    
    public void compileParameters(Parameter[] parameters, PredicateConstraint constraint, Rete engine, Template template, Rule rule) {
    	for (int px=0; px < parameters.length; px++) {
    		if (parameters[px] instanceof BoundParam) {
    			BoundParam bp = (BoundParam)parameters[px];
    			bp.setColumn(template.getSlot(constraint.getName()).getId());
    			bp.setRow(0);
    		} else if (parameters[px] instanceof FunctionParam2) {
    			FunctionParam2 fp = (FunctionParam2)parameters[px];
    			fp.configure(engine, rule);
    		}
    	}
    }
    
    public void compileJoins(Rule rule, Condition[] conds)
    throws AssertException
    {
       BaseJoin prevJoinNode = null;
       BaseJoin joinNode = null;
       Condition prevCE = null;
        // only if there's more than 1 condition do we attempt to 
        // create the join nodes. A rule with just 1 condition has
        // no joins
        if (conds.length > 1) {
            // previous Condition
            prevCE = conds[0];
            //this.compileFirstJoin(engine, memory); moved to the ConditionCompiler.compileFirstJoin method
            prevCE.getCompiler(this).compileFirstJoin(prevCE, rule);
            
            
            // now compile the remaining conditions
            for (int idx=1; idx < conds.length; idx++) {
                Condition cdt = conds[idx];

                joinNode = cdt.getCompiler(this).compileJoin(cdt, idx, rule, prevCE);
                cdt.getCompiler(this).connectJoinNode(prevCE, cdt, prevJoinNode, joinNode);
                
                // now we set the previous node to current
                prevCE = cdt;
                prevJoinNode = joinNode;
                rule.addJoinNode(joinNode);
            }
        } else if (conds.length == 1){
        	conds[0].getCompiler(this).compileSingleCE(rule);
        }
    }
    

    
    /**
     * The method is responsible for compiling the string form of the
     * actions to the equivalent functions.
     * @param rule
     * @param acts
     * @param util
     */
    protected void compileActions(Rule rule, Action[] acts) {
    	for (int idx=0; idx < acts.length; idx++) {
            Action atn = acts[idx];
            atn.configure(engine, rule);
        }
    }
    
    protected void attachTerminalNode(BaseNode last, TerminalNode terminal) {
        if (last != null && terminal != null) {
            try {
                if (last instanceof BaseJoin) {
                    ((BaseJoin)last).addSuccessorNode(terminal,engine,memory);
                } else if (last instanceof BaseAlpha) {
                    ((BaseAlpha)last).addSuccessorNode(terminal,engine,memory);
                }
            } catch (AssertException e) {
                
            }
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
            ((BaseAlpha)last).addSuccessorNode(join,engine,memory);
        } else if (last instanceof BaseJoin) {
            ((BaseJoin)last).addSuccessorNode(join,engine,memory);
        }
    }
    
    /**
     * method will find the first LeftInputAdapter node for the
     * ObjectTypeNode. There should only be one that is a direct
     * successor.
     * @param otn
     * @return
     */
    public LIANode findLIANode(ObjectTypeNode otn) {
        LIANode node = null;
        if (otn.getSuccessorNodes() != null && otn.getSuccessorNodes().length > 0) {
            Object[] nodes = (Object[])otn.getSuccessorNodes();
            for (int idx=0; idx < nodes.length; idx++) {
                if (nodes[idx] instanceof LIANode) {
                    node = (LIANode)nodes[idx];
                    break;
                }
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

	public Map<Template, ObjectTypeNode> getInputnodes() {
		return inputnodes;
	}
}
