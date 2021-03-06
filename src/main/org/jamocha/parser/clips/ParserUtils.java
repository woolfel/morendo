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
package org.jamocha.parser.clips;

public class ParserUtils {

    /**
     * convienant method to get string literal
     * 
     * @param text
     * @return
     */
    public static String getStringLiteral(String text) {
        StringBuffer buf = new StringBuffer();
        int len = text.length() - 1;
        boolean escaping = false;
        for (int i = 1; i < len; i++) {
            char ch = text.charAt(i);
            if (escaping) {
                buf.append(ch);
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * convennt utility method to escape string literals
     * 
     * @param text
     * @return
     */
    public static String escapeStringLiteral(String text) {
        StringBuilder buffer = new StringBuilder();
        for (char chr : text.toCharArray()) {
            if (chr == '"' || chr == '\\') {
                buffer.append('\\');
            }
            buffer.append(chr);
        }
        return buffer.toString();
    }

}
