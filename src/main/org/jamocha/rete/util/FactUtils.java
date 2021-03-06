/*
 * Copyright 2002-2006 Peter Lin
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
package org.jamocha.rete.util;

import java.util.Arrays;
import java.util.List;

/**
 * @author Peter Lin
 * 
 * the class contains utilities for doing things like sortig the facts for
 * printing.
 */
public class FactUtils {

	public static final FactComparator COMPARATOR = new FactComparator();
	public static final FactTemplateComparator TEMPLATECOMP = 
		new FactTemplateComparator();
    
    private static long counter = 0;
	
	public FactUtils() {
		super();
	}

	public static Object[] sortFacts(List<?> facts) {
		Object[] sorted = facts.toArray();
		Arrays.sort(sorted,COMPARATOR);
		return sorted;
	}
	
	public static Object[] sortFactsByTemplate(List<?> facts) {
		Object[] sorted = facts.toArray();
		Arrays.sort(sorted,TEMPLATECOMP);
		return sorted;
	}
    
    /**
     * method return the next timestamp which is just a counter
     * @return
     */
    public static long nextTimeStamp() {
        return ++counter;
    }
    
    public static void reset() {
        counter = 0;
    }
}
