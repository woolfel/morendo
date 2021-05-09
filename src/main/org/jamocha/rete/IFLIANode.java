/*
 * Copyright 2002-2008 Peter Lin
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
package org.jamocha.rete;

import org.jamocha.rete.exception.AssertException;
import org.jamocha.rete.exception.RetractException;

/**
 * IFLIANode is a special Left-Input Adapater node which has alpha memory.
 * LIANode has no memory to make the engine more efficient, but the left
 * input adapter for the InitialFact needs the memory to make sure rules
 * that start with NOTCE work properly. If we don't, the user has to 
 * execute (reset) function, so the rule will fire correctly.
 * @author woolfel
 *
 */
public class IFLIANode extends LIANode {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IFLIANode(int id) {
        super(id);
    }

    /**
     * the implementation just propogates the assert down the network
     */
    public void assertFact(Fact fact, Rete engine, WorkingMemory mem) 
    throws AssertException
    {
        AlphaMemory alpha = (AlphaMemory) mem.getAlphaMemory(this);
        alpha.addPartialMatch(fact);
        propogateAssert(fact, engine, mem);
    }
    
    /**
     * Retract simply propogates it down the network
     */
    public void retractFact(Fact fact, Rete engine, WorkingMemory mem)
    throws RetractException
    {
        AlphaMemory alpha = (AlphaMemory)mem.getAlphaMemory(this);
        if (alpha.removePartialMatch(fact) != null) {
            propogateRetract(fact, engine, mem);
        }
    }
}
