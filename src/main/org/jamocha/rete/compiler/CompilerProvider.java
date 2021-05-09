package org.jamocha.rete.compiler;

import org.jamocha.rete.GraphQueryCompiler;
import org.jamocha.rete.QueryCompiler;
import org.jamocha.rete.RuleCompiler;

/**
 * 
 * @author HouZhanbin
 * Oct 12, 2007 10:24:19 AM
 *
 */
public class CompilerProvider {
	
	private static CompilerProvider compilerProvider;
	
	public static ConditionCompiler objectConditionCompiler;
	
	public static ConditionCompiler existConditionCompiler;
    
	public static ConditionCompiler temporalConditionCompiler;
    
	public static ConditionCompiler testConditionCompiler;
    
    public static ConditionCompiler andConditionCompiler;
    
    public static ConditionCompiler cubeQueryConditionCompiler;
    
    public static ConditionCompiler onlyConditionCompiler;
    
    public static ConditionCompiler multipleConditionCompiler;
	
	public static CompilerProvider getInstance(RuleCompiler ruleCompiler){
		
		if(compilerProvider==null){
			objectConditionCompiler = new ObjectConditionCompiler(ruleCompiler);
			existConditionCompiler = new ExistConditionCompiler(objectConditionCompiler);
            temporalConditionCompiler = new TemporalConditionCompiler(ruleCompiler);
            testConditionCompiler = new TestConditionCompiler(ruleCompiler);
            andConditionCompiler = new AndConditionCompiler();
            cubeQueryConditionCompiler = new CubeQueryConditionCompiler(ruleCompiler);
			onlyConditionCompiler = new OnlyConditionCompiler(objectConditionCompiler);
			multipleConditionCompiler = new MultipleConditionCompiler(objectConditionCompiler);
			compilerProvider = new CompilerProvider();
		}
		return compilerProvider;
	}

	public static CompilerProvider getInstance(QueryCompiler queryCompiler){
		
		if(compilerProvider==null){
			objectConditionCompiler = new ObjectConditionCompiler(queryCompiler);
			existConditionCompiler = new ExistConditionCompiler(objectConditionCompiler);
            temporalConditionCompiler = new TemporalConditionCompiler(queryCompiler);
            testConditionCompiler = new TestConditionCompiler(queryCompiler);
            andConditionCompiler = new AndConditionCompiler();
            cubeQueryConditionCompiler = new CubeQueryConditionCompiler(queryCompiler);
			onlyConditionCompiler = new OnlyConditionCompiler(objectConditionCompiler);
			multipleConditionCompiler = new MultipleConditionCompiler(objectConditionCompiler);
			compilerProvider = new CompilerProvider();
		}
		return compilerProvider;
	}
	
	public static CompilerProvider getInstance(GraphQueryCompiler queryCompiler){
		
		if(compilerProvider==null){
			objectConditionCompiler = new ObjectConditionCompiler(queryCompiler);
			existConditionCompiler = new ExistConditionCompiler(objectConditionCompiler);
            temporalConditionCompiler = new TemporalConditionCompiler(queryCompiler);
            testConditionCompiler = new TestConditionCompiler(queryCompiler);
            andConditionCompiler = new AndConditionCompiler();
			compilerProvider = new CompilerProvider();
		}
		return compilerProvider;
	}
}
