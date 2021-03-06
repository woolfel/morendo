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

/**
 * @author Peter Lin
 *
 * ProfileStats is used to collect statistics about the runtime.
 */
public class ProfileStats {

	public static long assertTime = 0;
	public static long retractTime = 0;
	public static long rmActivation = 0;
	public static long addActivation = 0;
	public static int addcount = 0;
	public static int rmcount = 0;
	public static long fireTime = 0;
	public static long averageCubeQueryTime = 0;
	public static long indexTime = 0;

	protected static long fstart = 0;
	protected static long fend = 0;
	
	protected static long assertstart = 0;
	protected static long assertend = 0;
	
	protected static long retractstart = 0;
	protected static long retractend = 0;
	
	protected static long addstart = 0;
	protected static long addend = 0;
	
	protected static long rmstart = 0;
	protected static long rmend = 0;
	
	protected static long cubeQueryStart = 0;
	protected static long cubeQueryEnd = 0;
	protected static long queryCount = 0;
	protected static long cubeQueryTotal = 0;
	
	protected static long indexStart = 0;
	protected static long indexEnd = 0;
	
	public ProfileStats() {
		super();
	}

	public static void resetStats() {
		assertTime = 0;
		retractTime = 0;
		rmActivation = 0;
		addActivation = 0;
		fireTime = 0;
	}
	
	/**
	 * method should be called when Rete.fire is called or simply
	 * turn on profiling in Rete.
	 */
	public static void startFire() {
		fstart = System.currentTimeMillis();
	}

	/**
	 * endFire will automatically calculate the elapsed time
	 * and add it to the total fire time. if the start fire 
	 * timestamp is zero, the elapsed time will not be
	 * calculated.
	 */
	public static void endFire() {
		fend = System.currentTimeMillis();
		if (fstart > 0) {
			addFireET(fend - fstart);
		}
	}

	/**
	 * Add a long time to the total fire time
	 * @param time
	 */
	public static void addFireET(long time) {
		fireTime += time;
	}

	/**
	 * method should be called before assert is called or turn
	 * profiling in Rete.
	 */
	public static void startAssert() {
		assertstart = System.currentTimeMillis();
	}
	
	/**
	 * method will automatically calculate the elapsed time and
	 * add it to the total assert time. if the start assert
	 * timestamp is zero, elpased time will not be calculated
	 * and added.
	 */
	public static void endAssert() {
		assertend = System.currentTimeMillis();
		if (assertstart > 0) {
			addAssertET(assertend - assertstart);
		}
	}
	
	/**
	 * add elapsted time to assert total time
	 * @param time
	 */
	public static void addAssertET(long time) {
		assertTime += time;
	}
	
	/**
	 * the method should be called before retract is called or
	 * turn of profiling in the Rete class.
	 */
	public static void startRetract() {
		retractstart = System.currentTimeMillis();
	}

	/**
	 * method will calculate the elapsed time and add it to the
	 * total retract time. if the start retract timestamp is zero
	 * the elapsed time will not be calculated.
	 */
	public static void endRetract() {
		retractend = System.currentTimeMillis();
		if (retractstart > 0) {
			addRetractET(retractend - retractstart);
		}
	}
	
	/**
	 * add elapsed time to retract total
	 * @param time
	 */
	public static void addRetractET(long time) {
		retractTime += time;
	}
	
	public static void startAddActivation() {
		addstart = System.currentTimeMillis();
	}
	
	public static void endAddActivation() {
		addend = System.currentTimeMillis();
		if (addstart > 0) {
			addAddActivationET(addend - addstart);
			addcount++;
		}
	}
	
	public static void addAddActivationET(long time) {
		addActivation += time;
	}
	
	public static void startRemoveActivation() {
		rmstart = System.currentTimeMillis();
	}
	
	public static void endRemoveActivation() {
		rmend = System.currentTimeMillis();
		if (rmstart > 0) {
			addRemoveActivationET(rmend - rmstart);
			rmcount++;
		}
	}
	
	public static void addRemoveActivationET(long time) {
		rmActivation += time;
	}
	
	public static void startCubeQuery() {
		cubeQueryStart = System.currentTimeMillis();
	}
	
	public static void endCubeQuery() {
		cubeQueryEnd = System.currentTimeMillis();
		queryCount++;
		calculateAverageCubeQuery();
	}
	
	public static void calculateAverageCubeQuery() {
		cubeQueryTotal += cubeQueryEnd - cubeQueryStart;
		averageCubeQueryTime = cubeQueryTotal / queryCount;
	}
	
	public static void startCubeIndex() {
		indexStart = System.currentTimeMillis();
	}
	
	public static void endCubeIndex() {
		indexEnd = System.currentTimeMillis();
		indexTime += indexEnd - indexStart;
	}
	
	public static void reset() {
		assertTime = 0;
		retractTime = 0;
		rmActivation = 0;
		addActivation = 0;
		fireTime = 0;
		fstart = 0;
		fend = 0;
		assertstart = 0;
		assertend = 0;
		retractstart = 0;
		retractend = 0;
		addstart = 0;
		addend = 0;
		rmstart = 0;
		rmend = 0;
		addcount = 0;
		rmcount = 0;
		cubeQueryStart = 0;
		cubeQueryEnd = 0;
		averageCubeQueryTime = 0;
		queryCount = 0;
		indexTime = 0;
	}
}
