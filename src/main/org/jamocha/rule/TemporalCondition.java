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
package org.jamocha.rule;

import java.util.List;

import org.jamocha.rete.*;
import org.jamocha.rete.compiler.CompilerProvider;
import org.jamocha.rete.compiler.ConditionCompiler;

/**
 * @author Peter Lin
 *
 * TemporalCondition extends AbstractCondition and adds 2 additional
 * attributes: relativeTime and varname. Since all temporal nodes have
 * to have both, we make it easier to set and get.
 */
public class TemporalCondition extends ObjectCondition {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String varname = null;
    protected int relativeTime = 0;
    protected int intervalTime = 0;
    protected String function = null;
    protected Parameter[] parameters = null;
    
	/**
	 * 
	 */
	public TemporalCondition() {
		super();
	}
    
    public String getVariableName() {
        return this.varname;
    }
    
    public void setVariableName(String name) {
        this.varname = name;
    }
    
    public void setRelativeTime(int time) {
        this.relativeTime = time;
    }
    
    public int getRelativeTime() {
        return this.relativeTime;
    }
    

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(int intervalTime) {
        this.intervalTime = intervalTime;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }
    
	public void addFunction(List<?> list) {
        if (list.size() > 0) {
            Object[] array = list.toArray();
            ValueParam vp = (ValueParam)array[0];
            setFunction(vp.getStringValue());
            Parameter[] params = new Parameter[array.length -1];
            for (int idx=1; idx < array.length; idx++) {
                params[idx - 1] = (Parameter)array[idx];
            }
            setParameters(params);
        }
    }
    
    /**
     * TODO - currently we don't need it and it isn't implemented.
     * should finish implementing it.
     */
	public boolean compare(Condition cond) {
		return false;
	}

    /**
     * The current implementation expects the deffact or object binding
     * constriant to be first.
     */
    public String toPPString() {
    	StringBuffer buf = new StringBuffer();
    	int start = 0;
    	// this is a hack, but it keeps the code simple for spacing
    	// default indent for CE is 2 spaces
    	String pad = "  ";
        buf.append(pad + "(temporal" + Constants.LINEBREAK);
        if (negated) {
            buf.append(pad + pad + "(relative-time " + this.relativeTime + ")" + Constants.LINEBREAK);
        } else {
            buf.append(pad + pad + "?" + this.varname + Constants.LINEBREAK);
            buf.append(pad + pad + "(relative-time " + this.relativeTime + ")" + Constants.LINEBREAK);
        }
        if (this.negated) {
            buf.append(pad + pad + "(not" + Constants.LINEBREAK);
            pad = "      ";
        } else {
            pad = "    ";
        }
    	buf.append(pad + "(" + this.templateName + Constants.LINEBREAK);
    	for (int idx=start; idx < this.constraints.size(); idx++) {
    		Constraint cnstr = (Constraint)this.constraints.get(idx);
            if (this.negated) {
                buf.append("    " + cnstr.toPPString());
            } else {
                buf.append("  " + cnstr.toPPString());
            }
    	}
        if (this.negated) {
            buf.append(pad + ")" + Constants.LINEBREAK);
        }
        pad = "  ";
        buf.append(pad + pad + ")" + Constants.LINEBREAK);
        buf.append(pad + ")" + Constants.LINEBREAK);
    	return buf.toString();
    }

	public ConditionCompiler getCompiler(RuleCompiler ruleCompiler) {
		CompilerProvider.getInstance(ruleCompiler);
		return CompilerProvider.temporalConditionCompiler;
	}
}
