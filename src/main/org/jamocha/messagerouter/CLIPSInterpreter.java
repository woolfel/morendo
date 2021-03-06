/**
  * Copyright 2006-2009 Alexander Wilden, Christoph Emonds, Sebastian Reinartz
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
package org.jamocha.messagerouter;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Function;
import org.jamocha.rete.Rete;
import org.jamocha.rete.ReturnVector;

public class CLIPSInterpreter {

	private Rete engine;

	public CLIPSInterpreter(Rete engine) {
		this.engine = engine;
	}

	public ReturnVector executeCommand(Object command) {
		ReturnVector result = null;
		if (command instanceof Function) {
			result = ((Function) command)
					.executeFunction(engine, null);
		} else {
			throw new RuntimeException(command.toString().trim() + " is not a recognized command" + Constants.LINEBREAK);
		}
		return result;
	}
}
