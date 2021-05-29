/*
 * Copyright 2002-2009 Peter Lin
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
import java.util.HashMap;
import java.util.Map;

import org.jamocha.rete.*;
import org.jamocha.rete.functions.BaseMatchFunction;

/**
 * @author Peter Lin
 * 
 * MatchesFunction will print out all partial matches including alpha and 
 * beta nodes.
 */
public class MatchesFunction extends BaseMatchFunction implements Function, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String MATCHES = "matches";
    
	/**
	 * 
	 */
	public MatchesFunction() {
		super();
	}

	public int getReturnType() {
        return Constants.RETURN_VOID_TYPE;
	}

	/**
	 * If the function is called without any parameters, it prints out
	 * all the memories. if parameters are passed, the output will be
	 * filtered.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		HashMap filter = new HashMap();
		if (params != null && params.length > 0) {
			// now we populate the filter
			for (int idx=0; idx < params.length; idx++) {
				if (params[idx] instanceof ValueParam) {
					filter.put( ((ValueParam)params[idx]).getStringValue(),null);
				} else if (params[idx] instanceof BoundParam) {
					// for now, BoundParam is not supported
				}
			}
		}
        this.printMemoriesByNodes(engine, filter);
		return new DefaultReturnVector();
	}

    @SuppressWarnings("rawtypes")
	protected void printMemoriesByNodes(Rete engine, HashMap filter) {
        DefaultWM wm = (DefaultWM)engine.getWorkingMemory();
        ArrayList alphaNodes = this.getSortedAlphaNodes(wm);
        for (int idx=0; idx < alphaNodes.size(); idx++) {
            this.printAlphaNodeInfo((BaseAlpha)alphaNodes.get(idx), engine, wm);
        }
        
        // now get the beta nodes into a list and sort it
        ArrayList betaNodes = this.getSortedBetaNodes(wm);
        
        for (int idx=0; idx < betaNodes.size(); idx++) {
            this.printBetaNodeInfo((BaseJoin)betaNodes.get(idx), engine, wm);
        }
    }
    
    @SuppressWarnings("rawtypes")
	protected void printMemoryForRule(Rete engine, HashMap filter) {
    }
    
    protected void printAlphaNodeInfo(BaseAlpha alphaNode, Rete engine, DefaultWM wm) {
        if (!(alphaNode instanceof ObjectTypeNode) && !(alphaNode instanceof LIANode) && !(alphaNode instanceof IFLIANode)) {
            StringBuffer buf = new StringBuffer();
            buf.append(alphaNode.toPPString());
            AlphaMemory alphaMem = (AlphaMemory)wm.getAlphaMemory(alphaNode);
            buf.append(" - Total Memories: " + alphaMem.size());
            buf.append(Constants.LINEBREAK);
            engine.writeMessage(buf.toString());
        }
    }
    
    @SuppressWarnings("rawtypes")
	protected void printBetaNodeInfo(BaseJoin betaNode, Rete engine, DefaultWM wm) {
        StringBuffer buf = new StringBuffer();
        buf.append(betaNode.toPPString());
        Map lmem = (Map)wm.getBetaLeftMemory(betaNode);
        Object rmem = wm.getBetaRightMemory(betaNode);
        buf.append(" - left memory count:" + lmem.size());
        // we need to check which type of node it is
        if (betaNode instanceof HashedEqBNode || betaNode instanceof HashedEqNJoin) {
            HashedAlphaMemoryImpl haMem = (HashedAlphaMemoryImpl)rmem;
            buf.append(" / right memory count:" + haMem.size());
        } else if (betaNode instanceof HashedNotEqNJoin || betaNode instanceof HashedNotEqBNode) {
            HashedNeqAlphaMemory haneqMem = (HashedNeqAlphaMemory)rmem;
            buf.append(" / right memory count:" + haneqMem.size());
        } else if (betaNode instanceof ExistJoin || betaNode instanceof NotJoin) {
            Map rmMem = (Map)rmem;
            buf.append(" / right memory count:" + rmMem.size());
        }
        buf.append(Constants.LINEBREAK);
        engine.writeMessage(buf.toString());
    }
    
	public String getName() {
		return MATCHES;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[] {String[].class};
	}

	public String toPPString(Parameter[] params, int indents) {
		return "(matches)\n" +
			"Function description:\n" +
			"\tPrints out a summary of the facts for each node.";
	}
}
