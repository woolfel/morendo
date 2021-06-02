package org.jamocha.rete.functions.text;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

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
 * Function will parse a string and return a Map<String,Integer> after it
 * has filtered out the stopwords. The parameters it takes is raw text and
 * array of stop words.
 * 
 * @author peter
 *
 */
public class StopwordFunction implements Function, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String STOPWORD = "stop-word";

	public StopwordFunction() {
	}

	public int getReturnType() {
		return Constants.OBJECT_TYPE;
	}

	public ReturnVector executeFunction(Rete engine, Parameter[] params) {
		Map<String,Integer> wordcount = new HashMap<String,Integer>();
		if (params != null && params.length == 2) {
			try {
				String rawText = params[0].getStringValue();
				BoundParam bp = (BoundParam) params[1];
				Object resolvedValue = bp.getValue(engine, Constants.OBJECT_TYPE);
				if (resolvedValue instanceof String[]) {
					Set<String> stop = this.read((String[]) resolvedValue);
					StringTokenizer toke = new StringTokenizer(rawText);
					while (toke.hasMoreTokens()) {
						String t = toke.nextToken();
						t = t.replaceAll("[/./,/!/?/)/(/:/`/^/]]", "");
						if (!stop.contains(t)) {
							Integer c = wordcount.get(t);
							if (c == null) {
								c = 0;
							}
							c++;
							wordcount.put(t, c);
						}
					}
				}
			} catch (Exception e) {
				//
			}
		}
		DefaultReturnVector ret = new DefaultReturnVector();
		DefaultReturnValue rv = new DefaultReturnValue(Constants.OBJECT_TYPE,
				wordcount);
		ret.addReturnValue(rv);
		return ret;
	}
	
	private Set<String> read(String[] words) {
		Set<String> wordset = new HashSet<String>();
		for (String s: words) {
			wordset.add(s);
		}
		return wordset;
	}

	public String getName() {
		return STOPWORD;
	}

	public Class<?>[] getParameter() {
		return new Class[]{ValueParam.class,BoundParam.class,ValueParam.class};
	}

	public String toPPString(Parameter[] params, int indents) {
		if (params != null && params.length > 0) {
			StringBuffer buf = new StringBuffer();
			return buf.toString();
		} else {
			return "(stop-word <string> <array>)";
		}
	}
}
