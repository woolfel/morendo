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
package org.jamocha.rete.compiler;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.BaseAlpha;
import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.BaseSlot;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Binding2;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.Cube;
import org.jamocha.rete.CubeBinding;
import org.jamocha.rete.CubeDimension;
import org.jamocha.rete.CubeQueryBNode;
import org.jamocha.rete.CubeTemplate;
import org.jamocha.rete.DefaultQueryCompiler;
import org.jamocha.rete.DefaultRuleCompiler;
import org.jamocha.rete.DimensionSlot;
import org.jamocha.rete.GraphQueryCompiler;
import org.jamocha.rete.QueryCompiler;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.Template;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.query.QueryBaseJoin;
import org.jamocha.rete.query.QueryCubeQueryJoin;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.CubeQueryCondition;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.PredicateConstraint;
import org.jamocha.rule.Query;
import org.jamocha.rule.Rule;

/**
 * 
 * @author Peter Lin
 *
 */
public class CubeQueryConditionCompiler extends AbstractConditionCompiler{
	
	public CubeQueryConditionCompiler(RuleCompiler ruleCompiler){
		this.ruleCompiler = (DefaultRuleCompiler)ruleCompiler;
	}
	
	public CubeQueryConditionCompiler(QueryCompiler queryCompiler){
		this.queryCompiler = (DefaultQueryCompiler)queryCompiler;
	}
	
	public CubeQueryConditionCompiler(GraphQueryCompiler queryCompiler){
		this.graphCompiler = queryCompiler;
	}
	
	/**
	 * CubeQuery conditional elements will never have alpha nodes. The method
	 * is not implemented
	 */
	public void compile(Condition condition, int position, Rule rule, boolean alphaMemory) {
		ObjectCondition cond = (ObjectCondition)condition;
        CubeTemplate templ = (CubeTemplate)cond.getTemplate();
        Constraint[] constrs = cond.getConstraints();
        for (int idx=0; idx < constrs.length; idx++) {
            Constraint cnstr = constrs[idx];
            if (cnstr instanceof BoundConstraint) {
                BoundConstraint bc = (BoundConstraint)cnstr;
                ruleCompiler.compileConstraint(bc, templ, rule, position);
            } else if (cnstr instanceof PredicateConstraint) {
            	if (rule.getBinding(((PredicateConstraint) cnstr).getVariableName()) == null) {
                	PredicateConstraint pc = (PredicateConstraint)cnstr;
                	int operator = ConversionUtils.getOperatorCode(pc.getFunctionName());
                	Binding2 bind = new Binding2(operator);
                	bind.setRightIndex(templ.getColumnIndex(pc.getName()));
                	bind.setVarName(pc.getVariableName());
                	Object val = pc.getValue();
                	if (val instanceof BoundParam) {
                		BoundParam bp = (BoundParam)val;
                		Binding bd = rule.getBinding(bp.getVariableName());
                		bind.setLeftRow(bd.getLeftRow());
                		bind.setLeftIndex(bd.getLeftIndex());
                	} else {
                    	bind.setQueryValue(pc.getValue());
                	}
                	rule.addBinding(pc.getVariableName(), bind);
            	}
            }
        }
	}

	public void compile(Condition condition, int position, Query query) {
		ObjectCondition cond = (ObjectCondition)condition;
        CubeTemplate templ = (CubeTemplate)cond.getTemplate();
        Constraint[] constrs = cond.getConstraints();
        for (int idx=0; idx < constrs.length; idx++) {
            Constraint cnstr = constrs[idx];
            if (cnstr instanceof BoundConstraint) {
                BoundConstraint bc = (BoundConstraint)cnstr;
                queryCompiler.compileConstraint(bc, templ, query, position);
            } else if (cnstr instanceof PredicateConstraint) {
            	if (query.getBinding(((PredicateConstraint) cnstr).getVariableName()) == null) {
                	PredicateConstraint pc = (PredicateConstraint)cnstr;
                	int operator = ConversionUtils.getOperatorCode(pc.getFunctionName());
                	Binding2 bind = new Binding2(operator);
                	bind.setRightIndex(templ.getColumnIndex(pc.getName()));
                	bind.setVarName(pc.getVariableName());
                	Object val = pc.getValue();
                	if (val instanceof BoundParam) {
                		BoundParam bp = (BoundParam)val;
                		Binding bd = query.getBinding(bp.getVariableName());
                		bind.setLeftRow(bd.getLeftRow());
                		bind.setLeftIndex(bd.getLeftIndex());
                	} else {
                    	bind.setQueryValue(pc.getValue());
                	}
                	query.addBinding(pc.getVariableName(), bind);
            	}
            }
        }
	}

	/**
	 * Since there's never going to be an alpha node, we won't ever need to attach them.
	 * @param existing
	 * @param alpha
	 * @param cond
	 */
    protected void attachAlphaNode(BaseAlpha existing, BaseAlpha alpha, Condition cond) {
    }

	public void compileFirstJoin(Condition condition, Rule rule) throws AssertException{
	}
	
	public void compileFirstJoin(Condition condition, Query query) throws AssertException{
	}
	
	/**
	 * method compiles ObjectConditions
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BaseJoin compileJoin(Condition condition, int position, Rule rule, Condition previousCond) {
		ArrayList dbindings = new ArrayList();
		ArrayList mbindings = new ArrayList();
        getCubeBindings(condition,rule,position,dbindings,mbindings);
        ObjectCondition oc = (ObjectCondition)condition;
        BaseJoin joinNode = null;
        //deal with the CE which is not NOT CE.
        if ( !oc.getNegated() ) {
        	joinNode = new CubeQueryBNode(ruleCompiler.getEngine().nextNodeId());
        }
        // first set the bindings for the left input
        Binding[] binds = getLeftBindings(condition,rule,position);
        Binding[] nbinds = getNumericBindings(condition,rule,position);

        // now set the cube bindings
        CubeBinding[] dmnBindings = new CubeBinding[dbindings.size()];
        dmnBindings = (CubeBinding[])dbindings.toArray(dmnBindings);
        CubeBinding[] msrBindings = new CubeBinding[mbindings.size()];
        msrBindings = (CubeBinding[])mbindings.toArray(msrBindings);
        // set all the bindings
        joinNode.setBindings(binds);
        ((CubeQueryBNode)joinNode).setNumericBindings(nbinds);
        ((CubeQueryBNode)joinNode).setDimensionBindings(dmnBindings);
        ((CubeQueryBNode)joinNode).setMeasureBindings(msrBindings);
		return joinNode;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public QueryBaseJoin compileJoin(Condition condition, int position, Query rule, Condition previousCond) {
		ArrayList dbindings = new ArrayList();
		ArrayList mbindings = new ArrayList();
        getCubeBindings(condition,rule,position,dbindings,mbindings);
        ObjectCondition oc = (ObjectCondition)condition;
        QueryBaseJoin joinNode = null;
        //deal with the CE which is not NOT CE.
        if ( !oc.getNegated() ) {
        	joinNode = new QueryCubeQueryJoin(queryCompiler.getEngine().nextNodeId());
        }
        // first set the bindings for the left input
        Binding[] binds = getLeftBindings(condition,rule,position);
        Binding[] nbinds = getNumericBindings(condition,rule,position);

        // now set the cube bindings
        CubeBinding[] dmnBindings = new CubeBinding[dbindings.size()];
        dmnBindings = (CubeBinding[])dbindings.toArray(dmnBindings);
        CubeBinding[] msrBindings = new CubeBinding[mbindings.size()];
        msrBindings = (CubeBinding[])mbindings.toArray(msrBindings);
        // set all the bindings
        joinNode.setBindings(binds);
        ((QueryCubeQueryJoin)joinNode).setNumericBindings(nbinds);
        ((QueryCubeQueryJoin)joinNode).setDimensionBindings(dmnBindings);
        ((QueryCubeQueryJoin)joinNode).setMeasureBindings(msrBindings);
		return joinNode;
	}

	@Override
	ObjectCondition getObjectCondition(Condition condition) {
		return (ObjectCondition)condition;
	}

	/**
	 * A rule that uses a Cube will always have 2 or more Conditions. this method is not
	 * implemented, since it does not apply
	 */
	public void compileSingleCE(Rule rule) throws AssertException{
	}

	/**
	 * A rule that uses a Cube will always have 2 or more Conditions. this method is not
	 * implemented, since it does not apply
	 */
	public void compileSingleCE(Query query) throws AssertException{
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Binding[] getLeftBindings(Condition condition, Rule rule, int position) {
		CubeQueryCondition cqcond = (CubeQueryCondition)getObjectCondition(condition);
	       List Constraints = cqcond.getQueryConstraints();
	       Template tmpl = cqcond.getTemplate();
	       ArrayList bindlist = new ArrayList();
        for (int idz=0; idz < Constraints.size(); idz++) {
            Object cst = Constraints.get(idz);
            if (cst instanceof BoundConstraint) {
                BoundConstraint bc = (BoundConstraint)cst;
                Binding cpy = rule.copyBinding(bc.getVariableName());
                if (cpy != null) {
                    if (cpy.getLeftRow() >= position) {
                        break;
                    } else {
                    	bindlist.add(cpy);
                        int rinx = tmpl.getColumnIndex(bc.getName());
                        // we increment the count to make sure the
                        // template isn't removed if it is being used
                        tmpl.incrementColumnUseCount(bc.getName());
                        cpy.setRightIndex(rinx);
                        cpy.setNegated(bc.getNegated());
                        if (bc.getNegated()) {
                        	cqcond.setHasNotEqual(true);
                        }
                    }
                }
            }
        }
        Binding[] binds = new Binding[bindlist.size()];
        binds = (Binding[])bindlist.toArray(binds);
        return binds;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Binding[] getLeftBindings(Condition condition, Query query, int position) {
		CubeQueryCondition cqcond = (CubeQueryCondition)getObjectCondition(condition);
	       List Constraints = cqcond.getQueryConstraints();
	       Template tmpl = cqcond.getTemplate();
	       ArrayList bindlist = new ArrayList();
        for (int idz=0; idz < Constraints.size(); idz++) {
            Object cst = Constraints.get(idz);
            if (cst instanceof BoundConstraint) {
                BoundConstraint bc = (BoundConstraint)cst;
                Binding cpy = query.copyBinding(bc.getVariableName());
                if (cpy != null) {
                    if (cpy.getLeftRow() >= position) {
                        break;
                    } else {
                    	bindlist.add(cpy);
                        int rinx = tmpl.getColumnIndex(bc.getName());
                        // we increment the count to make sure the
                        // template isn't removed if it is being used
                        tmpl.incrementColumnUseCount(bc.getName());
                        cpy.setRightIndex(rinx);
                        cpy.setNegated(bc.getNegated());
                        if (bc.getNegated()) {
                        	cqcond.setHasNotEqual(true);
                        }
                    }
                }
            }
        }
        Binding[] binds = new Binding[bindlist.size()];
        binds = (Binding[])bindlist.toArray(binds);
        return binds;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Binding[] getNumericBindings(Condition condition, Rule rule, int position) {
		CubeQueryCondition cqcond = (CubeQueryCondition)getObjectCondition(condition);
		List Constraints = cqcond.getQueryConstraints();
		Template tmpl = cqcond.getTemplate();
		ArrayList bindlist = new ArrayList();
		for (int idz=0; idz < Constraints.size(); idz++) {
			Object cst = Constraints.get(idz);
	   		if (cst instanceof PredicateConstraint) {
	   	    	PredicateConstraint pc = (PredicateConstraint)cst;
	   	    	int operator = ConversionUtils.getOperatorCode(pc.getFunctionName());
                if (pc.reverseOperator()) {
                	operator = ConversionUtils.getOppositeOperatorCode(operator);
                }
	   	    	Binding cpy = rule.copyPredicateBinding(pc.getVariableName(), operator);
	   	        int rinx = tmpl.getColumnIndex(pc.getName());
	   	        tmpl.incrementColumnUseCount(pc.getName());
	   	        cpy.setRightIndex(rinx);
	   	    	bindlist.add(cpy);
	   	    	BaseSlot bslot = tmpl.getSlot(pc.getName());
	   	    	if (bslot instanceof DimensionSlot) {
	   	    		((DimensionSlot)bslot).getDimension().setAutoIndex(true);
	   	    	}
	   		}
		}
		Binding[] binds = new Binding[bindlist.size()];
		binds = (Binding[])bindlist.toArray(binds);
		return binds;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Binding[] getNumericBindings(Condition condition, Query query, int position) {
		CubeQueryCondition cqcond = (CubeQueryCondition)getObjectCondition(condition);
		List Constraints = cqcond.getQueryConstraints();
		Template tmpl = cqcond.getTemplate();
		ArrayList bindlist = new ArrayList();
		for (int idz=0; idz < Constraints.size(); idz++) {
			Object cst = Constraints.get(idz);
	   		if (cst instanceof PredicateConstraint) {
	   	    	PredicateConstraint pc = (PredicateConstraint)cst;
	   	    	int operator = ConversionUtils.getOperatorCode(pc.getFunctionName());
                if (pc.reverseOperator()) {
                	operator = ConversionUtils.getOppositeOperatorCode(operator);
                }
	   	    	Binding cpy = query.copyPredicateBinding(pc.getVariableName(), operator);
	   	        int rinx = tmpl.getColumnIndex(pc.getName());
	   	        tmpl.incrementColumnUseCount(pc.getName());
	   	        cpy.setRightIndex(rinx);
	   	    	bindlist.add(cpy);
	   	    	BaseSlot bslot = tmpl.getSlot(pc.getName());
	   	    	if (bslot instanceof DimensionSlot) {
	   	    		((DimensionSlot)bslot).getDimension().setAutoIndex(true);
	   	    	}
	   		}
		}
		Binding[] binds = new Binding[bindlist.size()];
		binds = (Binding[])bindlist.toArray(binds);
		return binds;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getCubeBindings(Condition condition, Rule rule, int position, ArrayList dimensionBinding, ArrayList measureBinding) {
		CubeQueryCondition cqcondition = (CubeQueryCondition)condition;
		Cube cube = this.ruleCompiler.getEngine().getCube(cqcondition.getTemplateName());
		Constraint[] constraints = cqcondition.getConstraints();
		for (int idx=0; idx < constraints.length; idx++) {
			if (constraints[idx] instanceof BoundConstraint) {
				BoundConstraint bc = (BoundConstraint)constraints[idx];
				CubeDimension dimen = cube.getDimension(bc.getName());
				if (dimen != null) {
					CubeBinding cbind = (CubeBinding)dimen.getBinding();
					if (cbind.isMeasure()) {
						measureBinding.add(cbind);
					} else {
						dimensionBinding.add(cbind);
						// we set the auto index to true
						CubeDimension dimension = cube.getDimension(bc.getName());
						if (dimension != null) {
							dimension.setAutoIndex(true);
						}
					}
				} else if (bc.getIsObjectBinding()) {
					// we ignore it, since object bindings are neither dimensions or measures
				} else {
					// it's a measure
					CubeBinding cbind = (CubeBinding)cube.getBindingBySlot(bc.getName());
					measureBinding.add(cbind);
				}
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getCubeBindings(Condition condition, Query query, int position, ArrayList dimensionBinding, ArrayList measureBinding) {
		CubeQueryCondition cqcondition = (CubeQueryCondition)condition;
		Cube cube = this.queryCompiler.getEngine().getCube(cqcondition.getTemplateName());
		Constraint[] constraints = cqcondition.getConstraints();
		for (int idx=0; idx < constraints.length; idx++) {
			if (constraints[idx] instanceof BoundConstraint) {
				BoundConstraint bc = (BoundConstraint)constraints[idx];
				CubeDimension dimen = cube.getDimension(bc.getName());
				if (dimen != null) {
					CubeBinding cbind = (CubeBinding)dimen.getBinding();
					if (cbind.isMeasure()) {
						measureBinding.add(cbind);
					} else {
						dimensionBinding.add(cbind);
						// we set the auto index to true
						CubeDimension dimension = cube.getDimension(bc.getName());
						if (dimension != null) {
							dimension.setAutoIndex(true);
						}
					}
				} else {
					// it's a measure
					CubeBinding cbind = (CubeBinding)cube.getBindingBySlot(bc.getName());
					measureBinding.add(cbind);
				}
			}
		}
	}
}
