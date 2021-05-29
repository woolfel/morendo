/*
 * Copyright 2002-2010 Jamocha
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
package org.jamocha.rete.compiler;

import java.util.List;

import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.Binding;
import org.jamocha.rete.Binding2;
import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.ConversionUtils;
import org.jamocha.rete.DefaultQueryCompiler;
import org.jamocha.rete.DefaultRuleCompiler;
import org.jamocha.rete.Function;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.GraphQueryCompiler;
import org.jamocha.rete.ObjectTypeNode;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.QueryCompiler;
import org.jamocha.rete.Rete;
import org.jamocha.rete.RuleCompiler;
import org.jamocha.rete.Template;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.query.QueryBaseJoin;
import org.jamocha.rete.query.QueryObjTypeNode;
import org.jamocha.rule.BoundConstraint;
import org.jamocha.rule.Condition;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.PredicateConstraint;
import org.jamocha.rule.Query;
import org.jamocha.rule.Rule;
/**
 * 
 * @author HouZhanbin
 * @author Peter Lin
 * Oct 16, 2007 2:22:07 PM
 *
 */
public abstract class AbstractConditionCompiler implements ConditionCompiler{
	
	protected DefaultRuleCompiler ruleCompiler;
	protected DefaultQueryCompiler queryCompiler;
	protected GraphQueryCompiler graphCompiler;
	
	public final RuleCompiler getRuleCompiler(){
		return this.ruleCompiler;
	}
	
	public final QueryCompiler getQueryCompiler() {
		return this.queryCompiler;
	}
	
	public final GraphQueryCompiler getGraphCompiler() {
		return this.graphCompiler;
	}
	
	/**
	 * The first step is to connect the exist join to the parent on the left side. 
	 * The second step is to connect it to the parent on the right. For the right 
     * side, if the objectCondition doesn't have any nodes, we attach it to the 
     * objectType node.
	 */
	public final void connectJoinNode(Condition previousCondition,Condition condition, BaseJoin previousJoinNode, BaseJoin joinNode) throws AssertException {
        if (previousJoinNode != null) {
        	ruleCompiler.attachJoinNode(previousJoinNode,(BaseJoin)joinNode);
        } else {
        	ruleCompiler.attachJoinNode(previousCondition.getLastNode(),(BaseJoin)joinNode);
        }
        // next we have to add the ExistJoin for the right side, which should be either
        // an alphaNode or the objectTypeNode
        ObjectCondition oc = getObjectCondition(condition);
        ObjectTypeNode otn = ruleCompiler.findObjectTypeNode(oc.getTemplateName());
        
        if (oc.getNodes().size() > 0) {
        	ruleCompiler.attachJoinNode(oc.getLastNode(),(BaseJoin)joinNode);
        } else {
            otn.addSuccessorNode(joinNode,ruleCompiler.getEngine(),ruleCompiler.getEngine().getWorkingMemory());
        }
	}
	
	public final void connectJoinNode(Condition previousCondition, Condition condition, QueryBaseJoin previousJoinNode, QueryBaseJoin joinNode) throws AssertException {
        if (previousJoinNode != null) {
        	queryCompiler.attachJoinNode(previousJoinNode,(QueryBaseJoin)joinNode);
        } else {
        	queryCompiler.attachJoinNode(previousCondition.getLastNode(),(QueryBaseJoin)joinNode);
        }
        // next we have to add the ExistJoin for the right side, which should be either
        // an alphaNode or the objectTypeNode
        ObjectCondition oc = getObjectCondition(condition);
        Template template = queryCompiler.getEngine().findTemplate(oc.getTemplateName());
        QueryObjTypeNode qotn = queryCompiler.findQueryObjTypeNode(template);
        
        if (oc.getNodes().size() > 0) {
        	queryCompiler.attachJoinNode(oc.getLastNode(),(QueryBaseJoin)joinNode);
        } else {
            qotn.addSuccessorNode(joinNode,queryCompiler.getEngine(),queryCompiler.getEngine().getWorkingMemory());
        }
	}
	
	public final void connectJoinNode(Condition previousCondition, Condition condition, QueryBaseJoin previousJoinNode, QueryBaseJoin joinNode, GraphQueryCompiler compiler) throws AssertException {
        if (previousJoinNode != null) {
        	compiler.attachJoinNode(previousJoinNode,(QueryBaseJoin)joinNode);
        } else {
        	compiler.attachJoinNode(previousCondition.getLastNode(),(QueryBaseJoin)joinNode);
        }
        // next we have to add the ExistJoin for the right side, which should be either
        // an alphaNode or the objectTypeNode
        ObjectCondition oc = getObjectCondition(condition);
        Template template = compiler.getEngine().findTemplate(oc.getTemplateName());
        QueryObjTypeNode qotn = compiler.findQueryObjTypeNode(template);
        
        if (oc.getNodes().size() > 0) {
        	compiler.attachJoinNode(oc.getLastNode(),(QueryBaseJoin)joinNode);
        } else {
            qotn.addSuccessorNode(joinNode,compiler.getEngine(),compiler.getEngine().getWorkingMemory());
        }
	}
	
	abstract ObjectCondition getObjectCondition(Condition condition);
	
	/**
	 * the paramList should be clean and 
	 * other codes surrounding this method in subclass may be removed into this method.
	 * Houzhanbin 10/16/2007
	 * @param condition
	 * @param rule
	 * @param Constraints
	 * @param position
	 * @param hasNotEqual
	 * @param hasPredicateJoin
	 * @return
	 */
	final Binding[] getBindings(Condition condition, Rule rule, int position) {
		ObjectCondition oc = getObjectCondition(condition);
		List<?> Constraints = oc.getBindConstraints();
		Template tmpl = oc.getTemplate();
		Binding[] binds = new Binding[Constraints.size()];
		for (int idz = 0; idz < Constraints.size(); idz++) {
			Object cst = Constraints.get(idz);
			if (cst instanceof BoundConstraint) {
				BoundConstraint bc = (BoundConstraint) cst;
				Binding cpy = rule.copyBinding(bc.getVariableName());
				if (cpy.getLeftRow() >= position) {
					binds = new Binding[0];
					break;
				} else {
					binds[idz] = cpy;
					int rinx = tmpl.getColumnIndex(bc.getName());
					// we increment the count to make sure the
					// template isn't removed if it is being used
					tmpl.incrementColumnUseCount(bc.getName());
					binds[idz].setRightIndex(rinx);
					binds[idz].setNegated(bc.getNegated());
					if (bc.getNegated()) {
						oc.setHasNotEqual(true);
					}
				}
			} else if (cst instanceof PredicateConstraint) {
				PredicateConstraint pc = (PredicateConstraint) cst;
				if (pc.getValue() instanceof BoundParam) {
					oc.setHasPredicateJoin(true);
					BoundParam bpm = (BoundParam) pc.getValue();
					String var = bpm.getVariableName();
					int op = ConversionUtils.getOperatorCode(pc
							.getFunctionName());
					// check and make sure the function isn't user defined
					if (op != Constants.USERDEFINED) {
						// if the first binding in the function is from the
						// object type
						// we reverse the operator
						if (pc.getParameters().get(0) != bpm)
							op = ConversionUtils.getOppositeOperatorCode(op);
						binds[idz] = rule.copyPredicateBinding(var, op);
						((Binding2) binds[idz]).setRightVariable(pc
								.getVariableName());
					} else {
						Binding2 b2 = (Binding2)rule.copyPredicateBinding(var, op);
						binds[idz] = b2;
						b2.setFunction( ruleCompiler.getEngine().findFunction(pc.getFunctionName()));
						Parameter[] params = new Parameter[pc.getParameters().size()];
						params = (Parameter[]) pc.getParameters().toArray(params);
						b2.setParams(params);
						for (int px = 0; px < params.length; px++) {
							if (params[px] instanceof FunctionParam2) {
								configureNestedFunctionParam(
										(FunctionParam2) params[px], ruleCompiler
												.getEngine(), rule);
							} else if (params[px] instanceof BoundParam) {
								BoundParam bp = (BoundParam)params[px];
								Binding binding = rule.getBinding(bp.getVariableName());
								bp.setRow(binding.getLeftRow());
								bp.setColumn(binding.getLeftIndex());
							}
						}
					}
					binds[idz].setPredJoin(true);
					int rinx = tmpl.getColumnIndex(pc.getName());
					// we increment the count to make sure the
					// template isn't removed if it is being used
					tmpl.incrementColumnUseCount(pc.getName());
					binds[idz].setRightIndex(rinx);
				} else if (pc.getFunctionName() != null) {
					// this means there is a nested function
					Binding2 bind2 = new Binding2(Constants.EQUAL);
					binds[idz] = bind2;
					Function f = ruleCompiler.getEngine().findFunction(
							pc.getFunctionName());
					bind2.setFunction(f);
					bind2.setPredJoin(true);
					Parameter[] params = new Parameter[pc.getParameters()
							.size()];
					params = (Parameter[]) pc.getParameters().toArray(params);
					bind2.setParams(params);
					for (int px = 0; px < params.length; px++) {
						if (params[px] instanceof FunctionParam2) {
							configureNestedFunctionParam(
									(FunctionParam2) params[px], ruleCompiler
											.getEngine(), rule);
						}
					}
				}
			}
		}
		return binds;
	}
	
	final Binding[] getBindings(Condition condition, Query query, int position) {
		ObjectCondition oc = getObjectCondition(condition);
		List<?> Constraints = oc.getBindConstraints();
		Template tmpl = oc.getTemplate();
		Binding[] binds = new Binding[Constraints.size()];
		for (int idz = 0; idz < Constraints.size(); idz++) {
			Object cst = Constraints.get(idz);
			if (cst instanceof BoundConstraint) {
				BoundConstraint bc = (BoundConstraint) cst;
				Binding cpy = query.copyBinding(bc.getVariableName());
				if (cpy.getLeftRow() >= position) {
					binds = new Binding[0];
					break;
				} else {
					binds[idz] = cpy;
					int rinx = tmpl.getColumnIndex(bc.getName());
					// we increment the count to make sure the
					// template isn't removed if it is being used
					tmpl.incrementColumnUseCount(bc.getName());
					binds[idz].setRightIndex(rinx);
					binds[idz].setNegated(bc.getNegated());
					if (bc.getNegated()) {
						oc.setHasNotEqual(true);
					}
				}
			} else if (cst instanceof PredicateConstraint) {
				PredicateConstraint pc = (PredicateConstraint) cst;
				if (pc.getValue() instanceof BoundParam) {
					oc.setHasPredicateJoin(true);
					BoundParam bpm = (BoundParam) pc.getValue();
					String var = bpm.getVariableName();
					int op = ConversionUtils.getOperatorCode(pc
							.getFunctionName());
					// check and make sure the function isn't user defined
					if (op != Constants.USERDEFINED) {
						// if the first binding in the function is from the
						// object type
						// we reverse the operator
						if (pc.getParameters().get(0) != bpm)
							op = ConversionUtils.getOppositeOperatorCode(op);
						binds[idz] = query.copyPredicateBinding(var, op);
						((Binding2) binds[idz]).setRightVariable(pc
								.getVariableName());
					} else {
						binds[idz] = query.copyPredicateBinding(var, op);
						((Binding2)binds[idz]).setFunction( queryCompiler.getEngine().findFunction(pc.getFunctionName()));
					}
					binds[idz].setPredJoin(true);
					int rinx = tmpl.getColumnIndex(pc.getName());
					// we increment the count to make sure the
					// template isn't removed if it is being used
					tmpl.incrementColumnUseCount(pc.getName());
					binds[idz].setRightIndex(rinx);
				} else if (pc.getFunctionName() != null) {
					// this means there is a nested function
					Binding2 bind2 = new Binding2(Constants.EQUAL);
					binds[idz] = bind2;
					Function f = queryCompiler.getEngine().findFunction(
							pc.getFunctionName());
					bind2.setFunction(f);
					bind2.setPredJoin(true);
					Parameter[] params = new Parameter[pc.getParameters()
							.size()];
					params = (Parameter[]) pc.getParameters().toArray(params);
					bind2.setParams(params);
					for (int px = 0; px < params.length; px++) {
						if (params[px] instanceof FunctionParam2) {
							configureNestedFunctionParam(
									(FunctionParam2) params[px], queryCompiler.getEngine(), query);
						}
					}
				}
			}
		}
		return binds;
	}
	
	protected void configureNestedFunctionParam(FunctionParam2 functionParam, Rete engine, Rule util) {
		functionParam.configure(engine, util);
	}
	
	protected void configureNestedFunctionParam(FunctionParam2 functionParam, Rete engine, Query util) {
		functionParam.configure(engine, util);
	}
}
