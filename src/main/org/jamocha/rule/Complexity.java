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
 * @author Peter Lin
 *
 * Complexity is a basic interface for rule complexity. The complexity 
 * could be calculated when the rule is compiled, or dynamically at runtime.
 * We make it an interface so that I can play with different algorithms
 * for calculating complexity.
 */
public interface Complexity {
	/**
	 * when the rule engine creates an instance of Defrule,
	 * it will pass itself to the complexity instance.
	 * @param rule
	 */
	void setRule(Rule rule);
    int getValue();
    /**
     * concrete class should implement the logic that calculates
     * the complexity for the rule.
     */
    void calculateComplexity();
}
