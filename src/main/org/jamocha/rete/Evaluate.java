/*
 * Copyright 2002-2008 Peter Lin
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
package org.jamocha.rete;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Peter Lin
 *
 * The purpose of Evaluate is similar to the Evaluatn in CLIPS. The class
 * constains static methods for evaluating two values
 */
public class Evaluate {

    /**
     * evaluate is responsible for evaluating two values. The left value
     * is the value in the slot. The right value is the value of the object
     * instance to match against.
     * @param operator
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluate(int operator, Object left, Object right){
        boolean eval = false;
        switch (operator){
            case Constants.EQUAL:
                eval = evaluateEqual(left,right);
                break;
            case Constants.NOTEQUAL:
                eval = evaluateNotEqual(left,right);
                break;
            case Constants.LESS:
                eval = evaluateLess(left,right);
                break;
            case Constants.LESSEQUAL:
                eval = evaluateLessEqual(left,right);
                break;
            case Constants.GREATER:
                eval = evaluateGreater(left,right);
                break;
            case Constants.GREATEREQUAL:
                eval = evaluateGreaterEqual(left,right);
                break;
            case Constants.NILL:
                eval = evaluateNull(left,right);
                break;
        }
        return eval;
    }
    
    /**
     * evaluate if two values are equal. If they are equal
     * return true. otherwise return false.
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluateEqual(Object left, Object right){
    	if (left.equals(Constants.NIL_SYMBOL)) {
    		return right == null;
    	} else if (left instanceof String){
            return evaluateStringEqual((String)left,right);
    	} else if (left instanceof Boolean) {
    		return evaluateBooleanEqual((Boolean)left,right);
    	} else if (left instanceof Double) {
    		return evaluateDoubleEqual((Double)left,right);
    	} else if (left instanceof Integer) {
    		return evaluateIntegerEqual((Integer)left,right);
    	} else if (left instanceof Short) {
    		return evaluateShortEqual((Short)left,right);
    	} else if (left instanceof Float) {
    		return evaluateFloatEqual((Float)left,right);
    	} else if (left instanceof Long) {
    		return evaluateLongEqual((Long)left,right);
    	} else if (left instanceof BigDecimal) {
    		return evaluateBigDecimalEqual((BigDecimal)left,right);
    	} else if (left instanceof Date) {
    		return evaluateDateEqual((Date)left,right);
        } else if (left instanceof Object && right instanceof Object) {
        	return left.equals(right);
        } else {
    		return false;
    	}
    }

    /**
     * evaluate if two values are equal when left is a string and right
     * is some object.
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluateStringEqual(String left, Object right) {
    	if (right instanceof Boolean) {
    		return left.equals(right.toString());
    	} else {
    		return left.equals(right);
    	}
    }
    
    /**
     * evaluate Boolean values against each other. If the right is a String,
     * the method will attempt to create a new Boolean object and evaluate.
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluateBooleanEqual(Boolean left, Object right) {
    	if (right instanceof Boolean) {
    		return left.equals(right);
    	} else if (right instanceof String) {
    		return left.toString().equals(right);
    	} else {
    		return false;
    	}
    }
    
    public static boolean evaluateIntegerEqual(Integer left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() == ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() == ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() == ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() == ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() == ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() == ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return left.toString().equals(right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateShortEqual(Short left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() == ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() == ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() == ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() == ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() == ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() == ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return left.toString().equals(right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateFloatEqual(Float left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() == ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() == ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() == ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() == ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() == ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return left.toString().equals(right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLongEqual(Long left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() == ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() == ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() == ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() == ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() == ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() == ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return left.toString().equals(right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateDoubleEqual(Double left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() == ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() == ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() == ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() == ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() == ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() == ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return left.toString().equals(right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateBigDecimalEqual(BigDecimal left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() == ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() == ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() == ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() == ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() == ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() == ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return left.toString().equals(right);
        } else {
            return false;
        }
    }

    public static boolean evaluateDateEqual(Date left, Object right) {
        if (right instanceof Date) {
            return left.getTime() == ((Date)right).getTime();
        } else if (right instanceof Long) {
            return left.getTime() == ((Long)right).longValue();
        } else if (right instanceof BigDecimal) {
            return left.getTime() == ((BigDecimal)right).longValue();
        } else {
            return false;
        }
    }
    
    /**
     * evaluate if two values are not equal. If they are not
     * equal, return true. Otherwise return false.
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluateNotEqual(Object left, Object right){
        if (left instanceof String){
            return !left.equals(right);
        } else if (left instanceof Boolean) {
        	return evaluateBooleanNotEqual((Boolean)left,right);
        } else if (left instanceof Integer) {
        	return evaluateIntegerNotEqual((Integer)left,right);
        } else if (left instanceof Short) {
        	return evaluateShortNotEqual((Short)left,right);
        } else if (left instanceof Float) {
        	return evaluateFloatNotEqual((Float)left,right);
        } else if (left instanceof Long) {
        	return evaluateLongNotEqual((Long)left,right);
        } else if (left instanceof Double) {
        	return evaluateDoubleNotEqual((Double)left,right);
        } else if (left instanceof BigDecimal) {
        	return evaluateBigDecimalNotEqual((BigDecimal)left,right);
        } else if (left instanceof Date) {
        	return evaluateDateNotEqual((Date)left, right);
        } else if (left instanceof Object && right instanceof Object) {
        	return !left.equals(right);
        } else {
            return false;
        }
    }
    
    /**
     * evaluate Boolean values against each other. If the right is a String,
     * the method will attempt to create a new Boolean object and evaluate.
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluateBooleanNotEqual(Boolean left, Object right) {
    	if (right instanceof Boolean) {
    		return !left.equals(right);
    	} else if (right instanceof String) {
    		Boolean b = new Boolean((String)right);
    		return !left.equals(b);
    	} else {
    		return false;
    	}
    }
    
    public static boolean evaluateIntegerNotEqual(Integer left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() != ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() != ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() != ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() != ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() != ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() != ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return !left.toString().equals(right);
        } else {
            return false;
        }
    }
   
    public static boolean evaluateShortNotEqual(Short left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() != ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() != ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() != ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() != ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() != ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() != ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return !left.toString().equals(right);
        } else {
            return false;
        }
    }
   
    public static boolean evaluateFloatNotEqual(Float left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() != ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() != ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() != ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() != ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() != ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() != ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return !left.toString().equals(right);
        } else {
            return false;
        }
    }
   
    public static boolean evaluateLongNotEqual(Long left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() != ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() != ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() != ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() != ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() != ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() != ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return !left.toString().equals(right);
        } else {
            return false;
        }
    }
   
    public static boolean evaluateDoubleNotEqual(Double left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() != ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() != ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() != ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() != ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() != ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() != ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return !left.toString().equals(right);
        } else {
            return false;
        }
    }
   
    public static boolean evaluateBigDecimalNotEqual(BigDecimal left, Object right) {
        if (right instanceof Double) {
            return left.doubleValue() != ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() != ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() != ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() != ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() != ((Long)right).doubleValue();
        } else if (right instanceof BigDecimal) {
            return left.doubleValue() != ((BigDecimal)right).doubleValue();
        } else if (right instanceof String) {
        	return !left.toString().equals(right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateDateNotEqual(Date left, Object right) {
        if (right instanceof Date) {
            return left.getTime() != ((Date)right).getTime();
        } else if (right instanceof Long) {
            return left.getTime() != ((Long)right).longValue();
        } else if (right instanceof BigDecimal) {
            return left.getTime() != ((BigDecimal)right).longValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLess(Object left, Object right){
        if (left instanceof BigDecimal){
            return evaluateBigDecimalLess((BigDecimal)left,right);
        } else if (left instanceof Integer){
            return evaluateIntegerLess((Integer)left,right);
        } else if (left instanceof Short){
            return evaluateShortLess((Short)left,right);
        } else if (left instanceof Long){
            return evaluateLongLess((Long)left,right);
        } else if (left instanceof Float){
            return evaluateFloatLess((Float)left,right);
        } else if (left instanceof Double){
            return evaluateDoubleLess((Double)left,right);
        } else if (left instanceof Date) {
        	return evaluateDateLess((Date)left, right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLessEqual(Object left, Object right){
        if (left instanceof BigDecimal){
            return evaluateBigDecimalLessEqual((BigDecimal)left,right);
        } else if (left instanceof Integer){
            return evaluateIntegerLessEqual((Integer)left,right);
        } else if (left instanceof Short){
            return evaluateShortLessEqual((Short)left,right);
        } else if (left instanceof Long){
            return evaluateLongLessEqual((Long)left,right);
        } else if (left instanceof Float){
            return evaluateFloatLessEqual((Float)left,right);
        } else if (left instanceof Double){
            return evaluateDoubleLessEqual((Double)left,right);
        } else if (left instanceof Date) {
        	return evaluateDateLessEqual((Date)left, right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreater(Object left, Object right){
        if (left instanceof BigDecimal){
            return evaluateBigDecimalGreater((BigDecimal)left,right);
        } else if (left instanceof Integer){
            return evaluateIntegerGreater((Integer)left,right);
        } else if (left instanceof Short){
            return evaluateShortGreater((Short)left,right);
        } else if (left instanceof Long){
            return evaluateLongGreater((Long)left,right);
        } else if (left instanceof Float){
            return evaluateFloatGreater((Float)left,right);
        } else if (left instanceof Double){
            return evaluateDoubleGreater((Double)left,right);
        } else if (left instanceof Date) {
        	return evaluateDateGreater((Date)left, right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateGreaterEqual(Object left, Object right){
        if (left instanceof BigDecimal){
            return evaluateBigDecimalGreaterEqual((BigDecimal)left,right);
        } else if (left instanceof Integer){
            return evaluateIntegerGreaterEqual((Integer)left,right);
        } else if (left instanceof Short){
            return evaluateShortGreaterEqual((Short)left,right);
        } else if (left instanceof Long){
            return evaluateLongGreaterEqual((Long)left,right);
        } else if (left instanceof Float){
            return evaluateFloatGreaterEqual((Float)left,right);
        } else if (left instanceof Double){
            return evaluateDoubleGreaterEqual((Double)left,right);
        } else if (left instanceof Date) {
        	return evaluateDateGreaterEqual((Date)left, right);
        } else {
            return false;
        }
    }
    
    public static boolean evaluateBigDecimalGreaterEqual(BigDecimal left, Object right) {
        if (right instanceof BigDecimal){
            return left.doubleValue() >= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Integer){
            return left.intValue() >= ((Integer)right).intValue();
        } else if (right instanceof Short){
            return left.shortValue() >= ((Short)right).shortValue();
        } else if (right instanceof Long){
            return left.longValue() >= ((Long)right).longValue();
        } else if (right instanceof Float){
            return left.floatValue() >= ((Float)right).floatValue();
        } else if (right instanceof Double){
            return left.doubleValue() >= ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    /**
     * In the case of checking if an object's attribute is null,
     * we only check the right.
     * @param left
     * @param right
     * @return
     */
    public static boolean evaluateNull(Object left, Object right){
        if (right == null){
            return true;
        } else {
            return false;
        }
    }
    
    /// ------- Integer comparison methods ------- ///
    public static boolean evaluateIntegerLess(Integer left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() < ((BigDecimal)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.intValue() < ((Integer)right).intValue();
        } else if (right instanceof Short) {
            return left.intValue() < ((Short)right).intValue();
        } else if (right instanceof Long) {
            return left.longValue() < ((Long)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() < ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() < ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateIntegerLessEqual(Integer left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.intValue() <= ((Integer)right).intValue();
        } else if (right instanceof Short) {
            return left.intValue() <= ((Short)right).intValue();
        } else if (right instanceof Long) {
            return left.longValue() <= ((Long)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() <= ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() <= ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateIntegerGreater(Integer left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() > ((BigDecimal)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.intValue() > ((Integer)right).intValue();
        } else if (right instanceof Short) {
            return left.intValue() > ((Short)right).intValue();
        } else if (right instanceof Long) {
            return left.longValue() > ((Long)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() > ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() > ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateIntegerGreaterEqual(Integer left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() >= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.intValue() >= ((Integer)right).intValue();
        } else if (right instanceof Short) {
            return left.intValue() >= ((Short)right).intValue();
        } else if (right instanceof Long) {
            return left.longValue() >= ((Long)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() >= ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() >= ((Double)right).doubleValue();
        } else {
            return false;
        }
    }

    /// ------- Short comparison methods ------- ///
    public static boolean evaluateShortLess(Short left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() < ((BigDecimal)right).doubleValue();
        } else if (right instanceof Short) {
            return left.shortValue() < ((Short)right).shortValue();
        } else if (right instanceof Integer) {
            return left.intValue() < ((Integer)right).intValue();
        } else if (right instanceof Float) {
            return left.floatValue() < ((Float)right).floatValue();
        } else if (right instanceof Long) {
            return left.longValue() < ((Long)right).longValue();
        } else if (right instanceof Double) {
            return left.doubleValue() < ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateShortLessEqual(Short left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Short) {
            return left.shortValue() <= ((Short)right).shortValue();
        } else if (right instanceof Integer) {
            return left.intValue() <= ((Integer)right).intValue();
        } else if (right instanceof Float) {
            return left.floatValue() <= ((Float)right).floatValue();
        } else if (right instanceof Long) {
            return left.longValue() <= ((Long)right).longValue();
        } else if (right instanceof Double) {
            return left.doubleValue() <= ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateShortGreater(Short left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() > ((BigDecimal)right).doubleValue();
        } else if (right instanceof Short) {
            return left.shortValue() > ((Short)right).shortValue();
        } else if (right instanceof Integer) {
            return left.intValue() > ((Integer)right).intValue();
        } else if (right instanceof Float) {
            return left.floatValue() > ((Float)right).floatValue();
        } else if (right instanceof Long) {
            return left.longValue() > ((Long)right).longValue();
        } else if (right instanceof Double) {
            return left.doubleValue() > ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateShortGreaterEqual(Short left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() >= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Short) {
            return left.shortValue() >= ((Short)right).shortValue();
        } else if (right instanceof Integer) {
            return left.intValue() >= ((Integer)right).intValue();
        } else if (right instanceof Float) {
            return left.floatValue() >= ((Float)right).floatValue();
        } else if (right instanceof Long) {
            return left.longValue() >= ((Long)right).longValue();
        } else if (right instanceof Double) {
            return left.doubleValue() >= ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    /// ------- Long comparison methods ------- ///
    public static boolean evaluateLongLess(Long left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() < ((BigDecimal)right).doubleValue();
        } else if (right instanceof Long) {
            return left.longValue() < ((Long)right).longValue();
        } else if (right instanceof Integer) {
            return left.longValue() < ((Integer)right).longValue();
        } else if (right instanceof Short) {
            return left.longValue() < ((Short)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() < ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() < ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLongLessEqual(Long left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Long) {
            return left.longValue() <= ((Long)right).longValue();
        } else if (right instanceof Integer) {
            return left.longValue() <= ((Integer)right).longValue();
        } else if (right instanceof Short) {
            return left.longValue() <= ((Short)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() <= ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() <= ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLongGreater(Long left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() > ((BigDecimal)right).doubleValue();
        } else if (right instanceof Long) {
            return left.longValue() > ((Long)right).longValue();
        } else if (right instanceof Integer) {
            return left.longValue() > ((Integer)right).longValue();
        } else if (right instanceof Short) {
            return left.longValue() > ((Short)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() > ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() > ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateLongGreaterEqual(Long left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() >= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Long) {
            return left.longValue() >= ((Long)right).longValue();
        } else if (right instanceof Integer) {
            return left.longValue() >= ((Integer)right).longValue();
        } else if (right instanceof Short) {
            return left.longValue() >= ((Short)right).longValue();
        } else if (right instanceof Float) {
            return left.floatValue() >= ((Float)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() >= ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    /// ------- Float comparison methods ------- ///
    public static boolean evaluateFloatLess(Float left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() < ((BigDecimal)right).doubleValue();
        } else if (right instanceof Float) {
            return left.floatValue() < ((Float)right).floatValue();
        } else if (right instanceof Integer) {
            return left.floatValue() < ((Integer)right).floatValue();
        } else if (right instanceof Short) {
            return left.floatValue() < ((Short)right).floatValue();
        } else if (right instanceof Long) {
            return left.floatValue() < ((Long)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() < ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateFloatLessEqual(Float left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Float) {
            return left.floatValue() <= ((Float)right).floatValue();
        } else if (right instanceof Integer) {
            return left.floatValue() <= ((Integer)right).floatValue();
        } else if (right instanceof Short) {
            return left.floatValue() <= ((Short)right).floatValue();
        } else if (right instanceof Long) {
            return left.floatValue() <= ((Long)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() <= ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateFloatGreater(Float left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() > ((BigDecimal)right).doubleValue();
        } else if (right instanceof Float) {
            return left.floatValue() > ((Float)right).floatValue();
        } else if (right instanceof Integer) {
            return left.floatValue() > ((Integer)right).floatValue();
        } else if (right instanceof Short) {
            return left.floatValue() > ((Short)right).floatValue();
        } else if (right instanceof Long) {
            return left.floatValue() > ((Long)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() > ((Double)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateFloatGreaterEqual(Float left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() >= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Float) {
            return left.floatValue() >= ((Float)right).floatValue();
        } else if (right instanceof Integer) {
            return left.floatValue() >= ((Integer)right).floatValue();
        } else if (right instanceof Short) {
            return left.floatValue() >= ((Short)right).floatValue();
        } else if (right instanceof Long) {
            return left.floatValue() >= ((Long)right).floatValue();
        } else if (right instanceof Double) {
            return left.doubleValue() >= ((Double)right).doubleValue();
        } else {
            return false;
        }
    }

    /// ------- Double comparison methods ------- ///
    public static boolean evaluateDoubleLess(Double left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() < ((BigDecimal)right).doubleValue();
        } else if (right instanceof Double) {
            return left.doubleValue() < ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() < ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() < ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() < ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() < ((Long)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateBigDecimalLess(BigDecimal left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() < ((BigDecimal)right).doubleValue();
        } else if (right instanceof Double) {
            return left.doubleValue() < ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() < ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() < ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() < ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() < ((Long)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateDoubleLessEqual(Double left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Double) {
            return left.doubleValue() <= ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() <= ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() <= ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() <= ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() <= ((Long)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateBigDecimalLessEqual(BigDecimal left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() <= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Double) {
            return left.doubleValue() <= ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() <= ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() <= ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() <= ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() <= ((Long)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateBigDecimalGreater(BigDecimal left, Object right) {
        if (right instanceof BigDecimal) {
            return left.doubleValue() > ((BigDecimal)right).doubleValue();
        } else if (right instanceof Double) {
            return left.doubleValue() > ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() > ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() > ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() > ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() > ((Long)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateDoubleGreater(Double left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() > ((BigDecimal)right).doubleValue();
        } else if (right instanceof Double) {
            return left.doubleValue() > ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() > ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() > ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() > ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() > ((Long)right).doubleValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateDoubleGreaterEqual(Double left, Object right){
        if (right instanceof BigDecimal) {
            return left.doubleValue() >= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Double) {
            return left.doubleValue() >= ((Double)right).doubleValue();
        } else if (right instanceof Integer) {
            return left.doubleValue() >= ((Integer)right).doubleValue();
        } else if (right instanceof Short) {
            return left.doubleValue() >= ((Short)right).doubleValue();
        } else if (right instanceof Float) {
            return left.doubleValue() >= ((Float)right).doubleValue();
        } else if (right instanceof Long) {
            return left.doubleValue() >= ((Long)right).doubleValue();
        } else {
            return false;
        }
    }
 
    /// ------- Date comparison methods ------- ///
    public static boolean evaluateDateLess(Date left, Object right){
    	if (right instanceof Date) {
    		return left.getTime() < ((Date)right).getTime();
    	} else if (right instanceof BigDecimal) {
            return left.getTime() < ((BigDecimal)right).doubleValue();
        } else if (right instanceof Long) {
            return left.getTime() < ((Long)right).longValue();
        } else if (right instanceof BigInteger) {
            return left.getTime() < ((BigInteger)right).intValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateDateLessEqual(Date left, Object right){
    	if (right instanceof Date) {
    		return left.getTime() <= ((Date)right).getTime();
    	} else if (right instanceof BigDecimal) {
            return left.getTime() <= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Long) {
            return left.getTime() <= ((Long)right).longValue();
        } else if (right instanceof BigInteger) {
            return left.getTime() <= ((BigInteger)right).intValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateDateGreater(Date left, Object right){
    	if (right instanceof Date) {
    		return left.getTime() > ((Date)right).getTime();
    	} else if (right instanceof BigDecimal) {
            return left.getTime() > ((BigDecimal)right).doubleValue();
        } else if (right instanceof Long) {
            return left.getTime() > ((Long)right).longValue();
        } else if (right instanceof BigInteger) {
            return left.getTime() > ((BigInteger)right).intValue();
        } else {
            return false;
        }
    }
    
    public static boolean evaluateDateGreaterEqual(Date left, Object right){
    	if (right instanceof Date) {
    		return left.getTime() >= ((Date)right).getTime();
    	} else if (right instanceof BigDecimal) {
            return left.getTime() >= ((BigDecimal)right).doubleValue();
        } else if (right instanceof Long) {
            return left.getTime() >= ((Long)right).longValue();
        } else if (right instanceof BigInteger) {
            return left.getTime() >= ((BigInteger)right).intValue();
        } else {
            return false;
        }
    }
    
    public static boolean factsEqual(Fact[] left, Fact[] right) {
        if (left == right) {
            return true;
        }
        int length = left.length;
        if (length != right.length) {
            return false;
        }
        for (int i=0; i<length; i++) {
            Object o1 = left[i];
            Object o2 = right[i];
            if (!(o1==null ? o2==null : o1 == o2) )
                return false;
        }
        return true;
    }
}
