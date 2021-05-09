package org.jamocha.nn;

import java.io.Serializable;

/**
 * <p>
 * A couple of important considerations for implementing a generic matrix library for
 * the rule engine. The first one is which float should we use? Based on the latest
 * research in 2020, BFloat16 is the best choice, but Java doesn't have a primitive
 * bfloat16 implementation. There are libraries that use JNI to read/write bfloat16
 * using native C++ libraries. While this makes sense for full-featured neural net
 * frameworks, it doesn't match the needs of the rule engine.</p>
 * <p>
 * Given bfloat16 requires using an external Java library with native C++ library
 * bindings, that isn't desirable at the moment. I looked at DeepLearning4Java ND4J
 * library to see how they handle matrix. Their design supports tensorflow, spark
 * and other formats. To serialize and deserialize data, they have a conversion class
 * that maps things back and forth. Rather than reproduce DL4J, it's better to use
 * it directly with a function. The purpose of embedding a simple NN in the rule
 * engine is to try out interesting new combinations and experiment.</p>
 * <p>
 * Another important decision with matrix is deciding if float is sufficient, or
 * do we really need to use double. DL4J and other java NN engines use double. The
 * main concern with using double is memory pressure, garbage collection and memory
 * fragmentation. 95 percent of the performance improvements in Jamocha is reducing
 * all three. Using double has a non-trivial impact on performance. The big question
 * is this "can we get away with using float instead of double?"</p>
 * <p>
 * Java 9 support Vector math operations with Intel FMA https://richardstartin.github.io/posts/autovectorised-fma-in-jdk10
 * </p>
 * <p>
 * definitely should use FMAt to take advantage of better performance.
 * </p>
 * 
 * @author peter
 *
 */
public class Matrix implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// to be memory efficient, always initialize with zero cols and rows
	private float[][] fmatrix = new float[0][0];

	public Matrix() {
	}

}
