/*
 * Copyright 2002-2006 Peter Lin
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
package org.jamocha.rule.util;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.Defclass;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.ValueParam;
import org.jamocha.rule.*;

/**
 * @author Peter Lin
 * 
 * The class will generate the trigger facts for a single rule. The purpose of this is to make
 * it easier to test a rule. Since a rule knows what conditions it needs, it makes sense to
 * generate the trigger facts instead of doing it manually.
 */
public class GenerateFacts {
	public GenerateFacts() {
		super();
	}
	
	public static ArrayList<Object> generateFacts(Rule rule, Rete engine) {
		ArrayList<Object> facts = new ArrayList<Object>();
		if (rule != null) {
			Condition[] conditions = rule.getConditions();
			for (int idx=0; idx < conditions.length; idx++) {
				Condition c = conditions[idx];
				if (c instanceof ObjectCondition) {
					ObjectCondition oc = (ObjectCondition)c;
					Deftemplate tpl = (Deftemplate)engine.findTemplate(oc.getTemplateName());
					if (tpl.getClassName() != null) {
						Object data = generateJavaFacts(oc,tpl,engine);
						facts.add(data);
					} else {
						Object data = generateDeffact(oc,tpl,engine);
						facts.add(data);
					}
				} else if (c instanceof TestCondition) {
					
				}
			}
		}
		return facts;
	}
	
	/**
	 * The method uses Defclass, Class, Deftemplate and Rete to create a new
	 * instance of the java object. Once the instance is created, the method
	 * uses Defclass to look up the write method and calls it with the
	 * appropriate value.
	 * @param cond
	 * @param templ
	 * @param engine
	 * @return
	 */
	public static Object generateJavaFacts(ObjectCondition cond, Deftemplate templ, Rete engine) {
		try {
			Class<?> theclz = Class.forName(templ.getClassName());
			Defclass dfc = engine.findDefclass(theclz);
			Object data = theclz.getDeclaredConstructor().newInstance();
			Constraint[] cnstr = cond.getConstraints();
			for (int idx=0; idx < cnstr.length; idx++) {
				Constraint cn = cnstr[idx];
				if (cn instanceof LiteralConstraint) {
					java.lang.reflect.Method meth = dfc.getWriteMethod(cn.getName());
					meth.invoke(data, new Object[]{cn.getValue()});
				}
			}
			// for now the method doesn't inspect the bindings
			// later on it needs to be added
			
			return data;
		} catch (ClassNotFoundException e) {
			return null;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		}
	}
	
	/**
	 * 
	 * @param cond
	 * @param templ
	 * @param engine
	 * @return
	 */
	public static Object generateDeffact(ObjectCondition cond, Deftemplate templ, Rete engine) {
		ArrayList<Slot> list = new ArrayList<Slot>();
		Constraint[] cnstr = cond.getConstraints();
		for (int idx=0; idx < cnstr.length; idx++) {
			Constraint cn = cnstr[idx];
			if (cn instanceof LiteralConstraint) {
				Slot s = new Slot(cn.getName(),cn.getValue());
				list.add(s);
			} else if (cn instanceof PredicateConstraint) {
				PredicateConstraint pc = (PredicateConstraint)cn;
				Object val = generatePredicateValue(pc);
				Slot s = new Slot(cn.getName(),val);
				list.add(s);
			} else if (cn instanceof BoundConstraint) {
				// for now we do the simple thing and just set
				// any bound slots to 1
				Slot s = new Slot(cn.getName(), Integer.valueOf(1));
				list.add(s);
			}
		}
		Fact f = templ.createFact(list,engine.nextFactId());
		return f;
	}
    
    public static String parseModuleName(Rule rule, Rete engine) {
        if (rule.getName().indexOf("::") > 0) {
            String text = rule.getName();
            String[] sp = text.split("::");
            return sp[0].toUpperCase();
        }
        return null;
    }
    
	public static Object generatePredicateValue(PredicateConstraint pc) {
    	String fname = pc.getFunctionName();
    	Object value = null;
		Parameter p = null;
		// first find the literal value
		List<?> prms = pc.getParameters();
		for (int idx=0; idx < prms.size(); idx++) {
			if (prms.get(idx) instanceof ValueParam) {
				p = (Parameter)pc.getParameters().get(1);
			}
		}
    	if (fname.equals(">") || fname.equals(">=")) {
    		value = p.getBigDecimalValue().add( new BigDecimal(1));
    	} else if (fname.equals("<") || fname.equals("<=")) {
    		value = p.getBigDecimalValue().subtract( new BigDecimal(1));
    	}
    	return value;
    }
}
