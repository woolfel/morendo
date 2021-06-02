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
package org.jamocha.rule;

/**
 * ComplexityFactory makes it easier to configure the rule engine
 * to use different implementations of complexity calculation.
 * When the rule engine starts, it should get the Class object and
 * set the factory appropriately.
 * 
 * @author Peter Lin
 */
public class ComplexityFactory {
	private static Class<?> complexityClazz = null;
	
	public static void setComplexityClass(Class<?> clzz) {
		complexityClazz = clzz;
	}
	
	public static Complexity newInstance() {
		if (complexityClazz == null) {
			return new DefaultComplexity();
		} else {
			Complexity c = null;
			try {
				c = (Complexity)complexityClazz.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				
			}
			return c;
		}
	}
}
