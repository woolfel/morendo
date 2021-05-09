/*
 * Copyright 2002-2010 Peter Lin
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

import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.query.QueryBaseJoin;
import org.jamocha.rule.Condition;
import org.jamocha.rule.ObjectCondition;
import org.jamocha.rule.Query;
import org.jamocha.rule.Rule;

public class AndConditionCompiler extends AbstractConditionCompiler {

    ObjectCondition getObjectCondition(Condition condition) {
        return null;
    }

    /**
     * Since a AndCondition has nested conditions, we have to handle this differently
     */
    public void compile(Condition condition, int position, Rule rule,
            boolean alphaMemory) {
    }

    public void compile(Condition condition, int position, Query query) {
    }

    public void compileFirstJoin(Condition condition, Rule rule)
            throws AssertException {
    }

    public void compileFirstJoin(Condition condition, Query query)
    throws AssertException {
}

    public BaseJoin compileJoin(Condition condition, int position, Rule rule,
            Condition previousCond) {
        return null;
    }

    public QueryBaseJoin compileJoin(Condition condition, int position, Query query,
            Condition previousCond) {
        return null;
    }
    
    public void compileSingleCE(Rule rule) throws AssertException {
    }

    public void compileSingleCE(Query query) throws AssertException {
    }
}
