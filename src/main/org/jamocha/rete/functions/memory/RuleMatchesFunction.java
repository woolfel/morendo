package org.jamocha.rete.functions.memory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.BaseAlpha;
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
import org.jamocha.rete.Index;
import org.jamocha.rete.LIANode;
import org.jamocha.rete.NotJoin;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;
import org.jamocha.rete.functions.BaseMatchFunction;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Defrule;

public class RuleMatchesFunction extends BaseMatchFunction implements Function, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String RULE_MATCHES = "rule-matches";
    
    public RuleMatchesFunction() {
    }

    	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
        if (params != null && params.length > 0) {
            ArrayList<Defrule> rules = new ArrayList<Defrule>();
            for (int idx=0; idx < params.length; idx++) {
                if (params[idx] instanceof ValueParam) {
                    String name = params[idx].getStringValue();
                    Defrule r = (Defrule)engine.getCurrentFocus().findRule(name);
                    if (r != null && !rules.contains(r)) {
                        rules.add(r);
                    }
                }
            }
            DefaultWM wm = (DefaultWM)engine.getWorkingMemory();
            // iterate over the rules
            for (int idx=0; idx < rules.size(); idx++) {
                this.printRuleMemories(engine, rules.get(idx), wm);
            }
        }
        return new DefaultReturnVector();
    }

    protected void printRuleMemories(Rete engine, Defrule rule, DefaultWM wm) {
        StringBuffer buf = new StringBuffer();
        buf.append(rule.getName() + Constants.LINEBREAK);
        Condition[] conditions = rule.getConditions();
        for (int idx=0; idx < conditions.length; idx++) {
            Condition c = conditions[idx];
            List<?> nodes = c.getNodes();
            Iterator<?> itr = nodes.iterator();
            while (itr.hasNext()) {
                BaseAlpha n = (BaseAlpha)itr.next();
                if ( !(n instanceof LIANode) ) {
                    Map<?, ?> rmem = (Map<?, ?>)wm.getBetaRightMemory(n);
                    buf.append(n.toPPString() + " - right memories:" + rmem.size() + Constants.LINEBREAK);
                    Iterator<?> memItr = rmem.keySet().iterator();
                    while (memItr.hasNext()) {
                        Fact f = (Fact)memItr.next();
                        buf.append("\t" + f.toFactString() + Constants.LINEBREAK);
                    }
                }
            }
        }
        List<?> betaNodes = rule.getJoins();
        Iterator<?> bnItr = betaNodes.iterator();
        while (bnItr.hasNext()) {
            BaseJoin betaNode = (BaseJoin)bnItr.next();
            buf.append(betaNode.toPPString() + Constants.LINEBREAK);
            Map<?, ?> lmem = (Map<?, ?>)wm.getBetaLeftMemory(betaNode);
            Object rmem = wm.getBetaRightMemory(betaNode);
            if (lmem.size() > 0) {
                buf.append(" - left memories:" + Constants.LINEBREAK);
                Iterator<?> leftItr = lmem.keySet().iterator();
                while (leftItr.hasNext()) {
                    Index mem = (Index)leftItr.next();
                    buf.append("\t" + mem.toPPString() + Constants.LINEBREAK);
                }
            }
            
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
                Map<?, ?> rmMem = (Map<?, ?>)rmem;
                Iterator<?> itr = rmMem.keySet().iterator();
                while (itr.hasNext()) {
                    Fact f = (Fact)itr.next();
                    buf.append("\t" + f.toFactString() + Constants.LINEBREAK);
                }
            }
            buf.append(Constants.LINEBREAK);
        }
        engine.writeMessage(buf.toString());
    }
    
    public String getName() {
        return RULE_MATCHES;
    }

	public Class<?>[] getParameter() {
        return new Class[] {String[].class};
    }

    public int getReturnType() {
        return Constants.RETURN_VOID_TYPE;
    }

    public String toPPString(Parameter[] params, int indents) {
        return "(rule-matches <Rule name>)" +
        "Function description:\n" +
        "\tPrints out the memories for a rule.";
    }

}
