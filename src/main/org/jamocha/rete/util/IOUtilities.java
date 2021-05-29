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

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.jamocha.rete.Fact;

/**
 * IOUtilities contains some commonly used static methods for saving and
 * loading files.
 * @author Peter
 */
public class IOUtilities {
	
	@SuppressWarnings("rawtypes")
	public static boolean saveFacts(List facts, String output) {
		try {
			FileWriter writer = new FileWriter(output);
			java.util.Iterator itr = facts.iterator();
			while (itr.hasNext()) {
				Fact f = (Fact)itr.next();
				writer.write(f.toFactString());
			}
			writer.flush();
			writer.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
