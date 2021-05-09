package org.jamocha.rete.macro;

import org.jamocha.rete.macro.ReadMacro;
import org.jamocha.rete.macro.WriteMacro;

/**
 * PropertyMacro has a reference to the ReadMacro and WriteMacro
 * for a single bean property. The purpose is to encapsulate the
 * macros and make it easier to use.
 * 
 * @author Peter Lin
 */
public class PropertyMacros {
	private ReadMacro readMacro;
	private WriteMacro writeMacro;
	
	public PropertyMacros() {}

	public ReadMacro getReadMacro() {
		return readMacro;
	}

	public void setReadMacro(ReadMacro readMacro) {
		this.readMacro = readMacro;
	}

	public WriteMacro getWriteMacro() {
		return writeMacro;
	}

	public void setWriteMacro(WriteMacro writeMacro) {
		this.writeMacro = writeMacro;
	}
}
