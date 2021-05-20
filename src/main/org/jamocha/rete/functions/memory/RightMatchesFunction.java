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
package org.jamocha.rete.functions.memory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.jamocha.rete.BaseJoin;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.DefaultWM;
import org.jamocha.rete.ExistJoin;
import org.jamocha.rete.Fact;
import org.jamocha.rete.Function;
import org.jamocha.rete.HashedAlphaMemoryImpl;
import org.jamocha.rete.HashedEqBNode;
import org.jamocha.rete.HashedEqNJoin;
import org.jamocha.rete.HashedNeqAlphaMemory;
import org.jamocha.rete.HashedNotEqBNode;
import org.jamocha.rete.HashedNotEqNJoin;
import org.jamocha.rete.NotJoin;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ZJBetaNode;
import org.jamocha.rete.functions.BaseMatchFunction;


/**
 * @author Peter Lin
 * The purpose of RightMatches is to print out the facts in the right
 * side of BetaNodes. It isn't the same as matches function. Unlike
 * matches, RightMatches prints out all the facts on the right side
 * and doesn't show which facts it matches on the left.
 */
public class RightMatchesFunction extends BaseMatchFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String RIGHT_MATCHES = "right-matches";
	

	public RightMatchesFunction() {
		super();
	}

	public int getReturnType() {
		return Constants.RETURN_VOID_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
        this.printRightMatches(engine);
		return new DefaultReturnVector();
	}

    @SuppressWarnings("rawtypes")
	protected void printRightMatches(Rete engine) {
        DefaultWM wm = (DefaultWM)engine.getWorkingMemory();
        ArrayList betaNodes = this.getSortedBetaNodes(wm);
        for (int idx=0; idx < betaNodes.size(); idx++) {
            BaseJoin join = (BaseJoin)betaNodes.get(idx);
            if (!(join instanceof ZJBetaNode)) {
                this.printBetaNodeDetailInfo(join, engine, wm);
            }
        }
    }
    
    @SuppressWarnings("rawtypes")
	public void printBetaNodeDetailInfo(BaseJoin betaNode, Rete engine, DefaultWM wm) {
        StringBuffer buf = new StringBuffer();
        buf.append(betaNode.toPPString());
        Map lmem = (Map)wm.getBetaLeftMemory(betaNode);
        Object rmem = wm.getBetaRightMemory(betaNode);
        buf.append(" - right memories:" + Constants.LINEBREAK);
        // now iterate over the right memories
        if (betaNode instanceof HashedEqBNode || betaNode instanceof HashedEqNJoin) {
            HashedAlphaMemoryImpl haMem = (HashedAlphaMemoryImpl)rmem;
            Object[] facts = haMem.iterateAll();
            for (int idx=0; idx < facts.length; idx++) {
                Fact f = (Fact)facts[idx];
                buf.append("\t" + f.toFactString() + Constants.LINEBREAK);
            }
        } else if (betaNode instanceof HashedNotEqNJoin || betaNode instanceof HashedNotEqBNode) {
            HashedNeqAlphaMemory haneqMem = (HashedNeqAlphaMemory)rmem;
            Object[] facts = haneqMem.iterateAll();
            for (int idx=0; idx < facts.length; idx++) {
                Fact f = (Fact)facts[idx];
                buf.append("\t" + f.toFactString() + Constants.LINEBREAK);
            }
        } else if (betaNode instanceof ExistJoin || betaNode instanceof NotJoin) {
            Map rmMem = (Map)rmem;
            Iterator itr = rmMem.keySet().iterator();
            while (itr.hasNext()) {
                Fact f = (Fact)itr.next();
                buf.append("\t" + f.toFactString() + Constants.LINEBREAK);
            }
        }
        buf.append(Constants.LINEBREAK);
        engine.writeMessage(buf.toString());
        
    }
    
	public String getName() {
		return RIGHT_MATCHES;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[0];
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(right-matches)\n" +
				"Function description:\n" +
				"\tPrints out the facts in the right side of BetaNodes,\n" +
				"\tand does not show which facts it matches on the left.";
	}

}
