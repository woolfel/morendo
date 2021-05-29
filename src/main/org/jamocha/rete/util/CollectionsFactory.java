/*
 * Copyright 2002-2007 Peter Lin
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Peter Lin
 *
 * The purpose of this factory is to centralize the creation of ArrayList,
 * List, Collection, Set and Map data structures. This is done so that
 * we can easily drop in some other implementation, like Tangosol's
 * Coherence product, which uses distributed HashMaps.
 */
public class CollectionsFactory {
    
	protected static CollectionsFactory factory = null;
	
	protected CollectionsFactory() {
	}
	
	public static void init() {
		factory = new CollectionsFactory();
	}
	
    @SuppressWarnings("rawtypes")
	public static Map newAlphaMemoryMap(String name) {
        return new HashMap();
    }
    
    @SuppressWarnings("rawtypes")
	public static Map newLinkedHashmap(String name) {
        return new LinkedHashMap();
    }
    
    @SuppressWarnings("rawtypes")
	public static Map newBetaMemoryMap(String name) {
        return new HashMap();
    }
    
    @SuppressWarnings("rawtypes")
	public static Map newTerminalMap() {
    	return new HashMap();
    }
    
    @SuppressWarnings("rawtypes")
	public static Map newClusterableMap(String name) {
        return new HashMap();
    }
    
    @SuppressWarnings("rawtypes")
	public static Map newMap() {
    	return new HashMap();
    }
    
    @SuppressWarnings("rawtypes")
	public static Map newHashMap() {
    	return new HashMap();
    }
    
    @SuppressWarnings("rawtypes")
	public static Map newNodeMap(String name) {
        return new HashMap();
    }
    
    /**
     * the sole purpose of this method is to return a Map that is not
     * clustered. The other methods will return a map, but depending
     * on the settings, they may return a Map that is hooked into a
     * JCache compliant product like Tangosol's Coherence.
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static Map localMap() {
    	return new HashMap();
    }
    
    @SuppressWarnings("rawtypes")
	public static java.util.Map javaHashMap() {
    	return new java.util.HashMap();
    }
    
    public static org.jamocha.rete.util.Map getCustomMap() {
    	return new org.jamocha.rete.util.HashMap();
    }
}
