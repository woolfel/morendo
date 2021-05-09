/*
 * Copyright 2002-2010 Peter Lin
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
package org.jamocha.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import org.jamocha.rete.Rete;

/**
 * Basic implementation for handling text message to the rule engine.
 * The handler will use Rete.build(String) method to evaluate the
 * text, which means it can do pretty much anything.
 * 
 * @author Peter Lin
 * 
 */
public class TextHandler implements ContentHandler {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Logger log = LogFactory.createLogger(TextHandler.class);

	protected String[] types = new String[]{MessageConstants.TEXT_MSG};
	protected Message last = null;
	/**
	 * 
	 */
	public TextHandler() {
		super();
	}

	public String[] getMessageTypes() {
		return types;
	}

	/**
	 * Method only handles text message and uses the batch function
	 */
	public void processMessage(Message msg, Rete engine, MessageClient client) {
		if (msg != last && msg instanceof TextMessage){
			TextMessage txtmsg = (TextMessage)msg;
			try {
				engine.build(txtmsg.getText());
			} catch (JMSException e) {
				log.info(e);
			}
		}
	}

}
