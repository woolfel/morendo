/*
 * Copyright 2002-2009 Jamocha
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

/**
 * The default implementation of complexity calculation is straight forward.
 * Here is the algorithm.
 * <ul><li> add 1 for each conditional element</li>
 * <li> add 1 for literal or predicate constraints</li>
 * <li> add 2 for each binding</li>
 * <li> add 2 for negated ce or existential ce</li>
 * <li> add 2 for test ce</li></ul>
 * 
 * @author Peter Lin
 */
public class DefaultComplexity implements Complexity {

    private Rule rule = null;
    private int complexityValue = 0;
    
	public DefaultComplexity() {
	}
    
	public void setRule(Rule r) {
		this.rule = r;
	}
	
	public int getValue() {
		return complexityValue;
	}

	public void calculateComplexity() {
        if (this.rule != null) {
            Condition[] conditions = this.rule.getConditions();
            for (int idx=0; idx < conditions.length; idx++) {
                this.complexityValue++;
                Condition cond = conditions[idx];
                if (cond instanceof ObjectCondition) {
                    ObjectCondition oc = (ObjectCondition)cond;
                    Constraint[] constraints = oc.getConstraints();
                    int bindingCount = 0;
                    for (int idc=0; idc < constraints.length; idc++) {
                    	int count = 0;
                        if (constraints[idc] instanceof AndLiteralConstraint ||
                        		constraints[idc] instanceof OrLiteralConstraint ||
                        		constraints[idc] instanceof PredicateConstraint) {
                            count = count + 1;
                        }
                        if (constraints[idc] instanceof BoundConstraint) {
                        	count = count + 1;
                        }
                    	count++;
                        this.complexityValue = count + bindingCount + this.complexityValue;
                    }
                    if (oc.negated || oc instanceof ExistCondition) {
                        this.complexityValue = this.complexityValue + 2;
                    }
                } else if (cond instanceof TestCondition) {
                	this.complexityValue = this.complexityValue + 2;
                }
            }
        }
	}
}
