/*
 * Copyright 2002-2010 Peter Lin
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

package org.jamocha.messaging;

import java.util.HashMap;
import java.util.Iterator;

/**
 * ContentHandler registry is used to register content handler to
 * jms message types. 
 * 
 * @author Peter Lin
 *
 */
public class ContentHandlerRegistry {
	
	private static HashMap<String, Object> REGISTRY = new HashMap<String, Object>();
	
	/**
	 * Method will register an instance of content handler using the
	 * jmsType as the key.
	 * @param jmsType
	 * @param instance
	 */
	public static void registerHandler(String jmsType, Object instance){
		if (jmsType != null && instance != null){
			if (!REGISTRY.containsKey(jmsType)){
				REGISTRY.put(jmsType,instance);
			}
		}
	}

	/**
	 * Method will return an instance of the content handler. If none
	 * exists, the method return null.
	 * @param jmsType
	 * @return
	 */
	public static ContentHandler findHandler(String jmsType){
		ContentHandler handler = null;
		handler = (ContentHandler)REGISTRY.get(jmsType);
		return handler;
	}
	
	public static Iterator<String> keyIterator() {
		return REGISTRY.keySet().iterator();
	}
}
