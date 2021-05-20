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
package org.jamocha.rete.functions;

import java.util.ArrayList;
import java.util.Iterator;

import org.jamocha.rete.DefaultWM;
import org.jamocha.rete.util.NodeComparator;

/**
 * A base class with methods that perform common operations like
 * getting the nodes and sorting them by node id.
 * 
 * @author Peter Lin
 */
public abstract class BaseMatchFunction {
    
    private static NodeComparator compare = new NodeComparator();

    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected ArrayList getSortedAlphaNodes(DefaultWM wm) {
        ArrayList alphaNodes = new ArrayList();
        Iterator itr = wm.getAllAlphaMemories().keySet().iterator();
        while (itr.hasNext()) {
            alphaNodes.add(itr.next());
        }
        java.util.Collections.sort(alphaNodes, compare);
        return alphaNodes;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected ArrayList getSortedBetaNodes(DefaultWM wm) {
        ArrayList betaNodes = new ArrayList();
        Iterator itr = wm.getAllBetaLeftMemories().keySet().iterator();
        while (itr.hasNext()) {
            Object n = itr.next();
            if (!betaNodes.contains(n)) {
                betaNodes.add(n);
            }
        }
        itr = wm.getAllBetaRightMemories().keySet().iterator();
        while (itr.hasNext()) {
            Object n = itr.next();
            if (!betaNodes.contains(n)) {
                betaNodes.add(n);
            }
        }
        java.util.Collections.sort(betaNodes, compare);
        return betaNodes;
    }
}
