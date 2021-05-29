package org.jamocha.rete.functions.temporal;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;
import org.jamocha.rule.AbstractCondition;
import org.jamocha.rule.Condition;
import org.jamocha.rule.Defrule;
import org.jamocha.rule.TemporalCondition;

/**
 * Temporal Calculation is an utility for calculating the temporal distance for any given
 * deftemplate in a ruleset. The number of rules doesn't matter for the calculation.
 * 
 * @author Peter Lin
 */
public class TemporalCalculation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TemporalCalculation() {
	}

	/**
	 * <code>
	 * The calculation implements the algorithm I provided in the blog
	 * and in the temporal logic extension paper.
	 * Hashtable ht = new Hashtable
	 *	foreach rule in the ruleset
	 *	  int td = -1 // in the beginning we assume no temporal pattern
	 *	  foreach condition element in the rule
	 *	    if the deftemplate isn't in the hashtable
	 *	      add the deftemplate to the hashtable
	 *	    if the condition is temporal
	 *	      td += temporal value
	 *	      if td > 0 and deftemplate temporal value &lt; td
	 *	        set the deftemplate temporal value = td
	 *	      end if
	 *	    end if
	 *	  end foreach
	 *	  if td equals -1
	 *	    foreach condition in the rule
	 *	      set deftemplate used by non-temporal rule to true
	 *	    end foreach
	 *	  end if
	 *	end foreach
	 * </code>
	 * @param engine
	 * @param rules
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean calcuateDistance(Rete engine, Collection rules) {
		// iterate over the rules and look at each conditional element
		Iterator itr = rules.iterator();
		while (itr.hasNext()) {
			Defrule rule = (Defrule)itr.next();
			Condition[] conditions = rule.getConditions();
			int distance = -1;
			for (int idx=0; idx < conditions.length; idx++) {
				AbstractCondition condition = (AbstractCondition)conditions[idx];
				Template template = condition.getTemplate();
				if (condition instanceof TemporalCondition) {
					TemporalCondition temporal = (TemporalCondition)condition;
					// if the distance is -1, it's the first temporal node
					// so we set the distance to zero so the calculation is accurate
					if (distance == -1) {
						distance = 0;
					}
					// we add the time as we iterate over the conditions
					// this means the facts will stay in the network longer
					if (temporal.getRelativeTime() > 0) {
						distance += (temporal.getRelativeTime() * 1000);
					} else {
						distance += (temporal.getIntervalTime() * 1000);
					}
					if (distance > 0 && distance > template.getTemporalDistance()) {
						// we set the absolute distance for the template to a greater value
						template.setTemporalDistance(distance);
					}
				}
			}
			// this means the templates used in the rule are used by non-temporal rules
			// in that case, it means we can't calculate the absolute temporal distance
			if (distance == -1) {
				for (int idx=0; idx < conditions.length; idx++) {
					AbstractCondition condition = (AbstractCondition)conditions[idx];
					Template template = condition.getTemplate();
					template.setNonTemporalRules(true);
				}
			}
		}
		return true;
	}
}
