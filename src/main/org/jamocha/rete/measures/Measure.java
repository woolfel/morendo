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
package org.jamocha.rete.measures;

/**
 * Measures are used by the engine to perform additional filtering. For example, we might
 * want to calculate and aggregate from the matching data. We might also want to get the
 * top 5 or bottom 10 of the dataset.
 * 
 * @author Peter Lin
 */
public interface Measure {
	String getMeasureName();
	String getDescription();
}
