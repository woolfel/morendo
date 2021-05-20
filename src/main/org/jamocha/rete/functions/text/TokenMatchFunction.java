package org.jamocha.rete.functions.text;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import org.jamocha.rete.BoundParam;
import org.jamocha.rete.Constants;
import org.jamocha.rete.DefaultReturnValue;
import org.jamocha.rete.DefaultReturnVector;
import org.jamocha.rete.Function;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;
import org.jamocha.rete.ValueParam;

/**
 * <p>
 * Function takes a string and Set<String> of tokens. It will return the total
 * match count. For example, say you want to see if the response in a chat
 * has words related to a topic.</p>
 * <p>the text of their response is the first parameter. The second parameter
 * is a Set<String> of the language specific tokens for the topic. The function
 * will count how many matches are in the chat response</p>
 * 
 * @author peter
 *
 */
public class TokenMatchFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TOKENMATCH = "token-match";

	public TokenMatchFunction() {
	}

	public int getReturnType() {
		return Constants.INTEGER_OBJECT;
	}

	@SuppressWarnings("unchecked")
	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Integer total = 0;
		if (params != null && params.length == 2) {
			try {
				String rawText = params[1].getStringValue();
				rawText = rawText.replaceAll("[/./,/!/?/)/(/:/`/^/]]", "");
				BoundParam bp = (BoundParam) params[0];
				Object resolvedValue = null;
				if (bp.isObjectBinding()) {
					resolvedValue = bp.getValue(engine, Constants.OBJECT_TYPE);
				} else {
					resolvedValue = bp.getValue();
				}
				if (resolvedValue instanceof Set) {
					Set<String> stop = (Set<String>)resolvedValue;
					Iterator<String> itr = stop.iterator();
					while (itr.hasNext()) {
						String word = itr.next();
						for (int i=0; i < rawText.length(); i++) {
							int idof = rawText.substring(i).indexOf(word);
							if (idof > -1) {
								total++;
								i = idof + word.length();
							} else {
								break;
							}
						}
					}
				} else if (resolvedValue instanceof String[]) {
					String[] stop = (String[])resolvedValue;
					for (int s=0; s < stop.length; s++) {
						for (int i=0; i < rawText.length(); i++) {
							if (rawText.substring(i).indexOf(stop[s]) > -1) {
								total++;
								i = i + stop[s].length();
							}
						}
					}
				}
			} catch (Exception e) {
				//
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.INTEGER_OBJECT,
				total);
		ret.addReturnValue(rv);
		return ret;
	}
	
	public String getName() {
		return TOKENMATCH;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getParameter() {
		return new Class[]{ValueParam.class,BoundParam.class,ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			return buf.toString();
		} else {
			return "(stop-word <set> <string>)";
		}
	}
}
