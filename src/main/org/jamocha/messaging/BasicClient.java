/*
 * Copyright 2002-2010 Jamocha
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

import java.io.Serializable;
import java.lang.Thread;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jamocha.logging.LogFactory;
import org.jamocha.logging.Logger;
import org.jamocha.rete.Rete;

/**
 * 
 * @author Peter Lin
 *
 */
public class BasicClient implements MessageClient {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Rete ENGINE = null;
	private Context CTX = null;
	private TopicConnection CONN = null;
	private Topic TOPIC = null;
	private TopicSession SESSION = null;
	private TopicSubscriber SUBSCRIBER = null;
	private TopicPublisher PUBLISHER = null;
	private String URL = null;
	private String JNDI = null;
	private String FACTORYNAME = null;
	private String USER = null;
	private String PASSWORD = null;
	private String TOPICNAME = null;
	private String CLIENTNAME = null;
    private Thread CLIENTTHREAD = null;
    
    protected Logger log = LogFactory.createLogger(BasicClient.class);

    /**
	 * 
	 */
	public BasicClient() {
		super();
	}

	public BasicClient(String url, String initctxfactory,
			String factory, String topic, String user, String pwd, String name) {
		this.URL = url;
		this.JNDI = initctxfactory;
		this.FACTORYNAME = factory;
		this.USER = user;
		this.TOPICNAME = topic;
		this.PASSWORD = pwd;
		this.CLIENTNAME = name;
		init();
	}
	
	public void init() {
		Properties prop = new Properties();
		prop.put(Context.INITIAL_CONTEXT_FACTORY, this.JNDI);
		prop.put(Context.PROVIDER_URL, this.URL);
		prop.put(Context.SECURITY_PRINCIPAL,this.USER);
		prop.put(Context.SECURITY_CREDENTIALS,this.PASSWORD);
		try {
			this.CTX = new InitialContext(prop);
		} catch (NamingException e) {
			log.warn(e);
		}
		this.connect();
		try {
			this.SESSION = CONN.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
		
			if (this.TOPIC == null) {
				this.TOPIC = this.SESSION.createTopic(this.TOPICNAME);
			}
			
			this.SUBSCRIBER = this.SESSION.createSubscriber(TOPIC, null, true);
			this.SUBSCRIBER.setMessageListener(this);
			this.PUBLISHER = this.SESSION.createPublisher(TOPIC);
		} catch (JMSException e){
			log.warn("error creating session, publisher and subscriber" + e);
		}
		this.start();
		while (!this.CLIENTTHREAD.isAlive()){
			// to make sure the thread is alive
		}
	}
	
	public void close() {
		try {
			this.PUBLISHER.close();
			this.SUBSCRIBER.close();
			this.SESSION.unsubscribe(TOPIC.getTopicName());
			this.SESSION.close();
			this.CONN.stop();
			this.CONN.close();
			this.CTX.close();
			this.SUBSCRIBER = null;
			this.PUBLISHER = null;
			this.CONN = null;
			this.TOPIC = null;
			this.ENGINE = null;
			this.CLIENTTHREAD.interrupt();
			this.CLIENTTHREAD = null;
		} catch (Exception e){
			log.warn(e);
		} 
	}

	public void connect() {
		try {
			log.info("factoryname: " + this.FACTORYNAME);
			Object tcf = this.CTX.lookup(this.FACTORYNAME);
			TopicConnectionFactory factory = null;
			if (tcf != null) {
				factory = (TopicConnectionFactory)tcf;
			}
			this.CONN = factory.createTopicConnection();
			log.info("created TopicConnection");
			log.info("try to create topic " + this.TOPICNAME);
			this.TOPIC = (Topic) this.CTX.lookup(this.TOPICNAME);
			log.info("created topic " + this.TOPICNAME);
		} catch (JMSException e){
			log.warn(e);
		} catch (NamingException e){
			log.warn(e);
		} catch (Exception e) {
			log.warn(e);
		}
	}

	public String getConnectionFactory() {
		return this.FACTORYNAME;
	}

	public String getInitialContextFactory() {
		return this.JNDI;
	}

	public String getName() {
		return this.CLIENTNAME;
	}

	public String getProviderURL() {
		return this.URL;
	}

	public String getSecurityCredentials() {
		return this.PASSWORD;
	}

	public String getSecurityPrinciple() {
		return this.USER;
	}

	public String getTopic() {
		return this.TOPICNAME;
	}

	public void publish(Serializable message) {
		try {
			if (message instanceof String){
				// if the message is a string, we create a Text message
				// and publish it.
				TextMessage resp = this.SESSION.createTextMessage((String)message);
				resp.setJMSType(MessageConstants.TEXT_MSG);
				this.PUBLISHER.publish(resp);
            } else if (message instanceof ObjectMessage){
            	((ObjectMessage)message).setJMSType(MessageConstants.OBJECT_MSG);
                this.PUBLISHER.publish((Message)message);
			} else {
				Message resp = this.SESSION.createObjectMessage(message);
				resp.setJMSType(MessageConstants.TEXT_MSG);
				this.PUBLISHER.publish(resp);
			}
		} catch (JMSException e){
			log.warn(e);
		}
	}

	public void publishObject(Object body) {
        try {
            if (body instanceof ObjectMessage) {
                this.PUBLISHER.publish((Message)body);
			} else {
				ObjectMessage resp = this.SESSION.createObjectMessage();
				resp.setObject((Serializable) body);
				this.PUBLISHER.publish(resp);
			}
        } catch (JMSException e){
            log.warn(e);
        }
	}

	public void setConnectionFactory(String name) {
		this.FACTORYNAME = name;
	}

	public void setInitialContextFactory(String ctxname) {
		this.JNDI = ctxname;
	}

	public void setName(String name) {
		this.CLIENTNAME = name;
	}

	public void setProviderURL(String url) {
		this.URL = url;
	}

	public void setSecurityCredentials(String pwd) {
		this.PASSWORD = pwd;
	}

	public void setSecurityPrinciple(String username) {
		this.USER = username;
	}

	public void setTopic(String topic) {
		this.TOPICNAME = topic;
	}

	public void setRete(Rete engine) {
		this.ENGINE = engine;
	}

	public void start(){
		try {
			CONN.start();
			this.CLIENTTHREAD = new Thread(this);
			this.CLIENTTHREAD.start();
		} catch (JMSException e) {
			log.warn(e);
		}
	}
	
	public void onMessage(Message msg) {
		try {
			if (msg != null && msg.getJMSType() != null) {
				ContentHandler handler = this.getHandler(msg);
				// we expect the message to be an object message and not a
				// simple
				// TextMessage. If it's not an ObjectMessage, we ignore it.
				if (msg instanceof ObjectMessage && handler != null) {
					ObjectMessage omsg = (ObjectMessage) msg;
					handler.processMessage(omsg, this.ENGINE, this);
				} else if (msg instanceof TextMessage && handler != null) {
					TextMessage tmsg = (TextMessage) msg;
					handler.processMessage(tmsg, this.ENGINE, this);
					log.info(tmsg.getText());
				}
			}
		} catch (JMSException e) {
			System.err.println("Communication error: " + e.getMessage());
		}
	}

	public void run() {
	}
	
	/**
	 * Method looks in the Content handler registry to find the appropriate
	 * type of handler
	 * @param msg
	 * @return
	 */
	public ContentHandler getHandler(Message msg){
		try {
			return ContentHandlerRegistry.findHandler(msg.getJMSType());
		} catch (JMSException e){
			return null;
		}
	}
}
