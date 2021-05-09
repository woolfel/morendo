package org.jamocha.rule.util;

import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Constraint;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.ObjectCondition;

public class PartialMatchCalculation {

	public PartialMatchCalculation() {
		super();
	}

	public long calculatePartialMatchCost(Rete engine, Defrule r) {
        Condition[] conditions = r.getConditions();
        // first we add the size for the object type nodes
        long cost = 1;
        long totalcost = 1;
        Template template = null;
        // the first step is to add the number of successors for the object type nodes
        for (int idx=0; idx < conditions.length; idx++) {
            if (conditions[idx] instanceof ObjectCondition) {
            	cost = 1;
                ObjectCondition oc = (ObjectCondition)conditions[idx];
                template = oc.getTemplate();
                Constraint[] constraints = oc.getConstraints();
                for (int c=0; c < constraints.length; c++) {
                    long distinctCount = template.getSlot(constraints[c].getName()).getDistinctCount();
                    if (distinctCount > 0) {
                    	cost = cost * distinctCount;
                    }
                }
                oc.setPartialMatchCount(cost);
                totalcost = totalcost * cost;
            }
        }
		return totalcost;
	}
}
