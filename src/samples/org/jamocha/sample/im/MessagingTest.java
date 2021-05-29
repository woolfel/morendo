package org.jamocha.sample.im;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jamocha.rete.Rete;

import junit.framework.TestCase;

public class MessagingTest extends TestCase {
	private Rete engine = null;

	public MessagingTest() {
	}

	@SuppressWarnings("rawtypes")
	public void testMessageFilter() {
		this.engine = setupEngine();
		if (this.engine == null) {
			fail();
		} else {
			try {
				Message msg = createSpamMessage();
				engine.assertObject(msg,"Message",false,true);
				engine.fire();
				System.out.println(msg.getMessageStatus());

				List facts = engine.getDeffacts();
				System.out.println("number of facts - " + facts.size());
				
				Message msg2 = createSimpleMessage();
				engine.assertObject(msg2,"Message",false,true);
				engine.fire();
				System.out.println(msg2.getMessageStatus());
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}
		}
	}
	
	public Rete setupEngine() {
		if (this.engine == null) {
			this.engine = new Rete();
		}
		String ruleset = "./samples/InstantMessaging/instant_messaging.clp";
		// we load the rules
		engine.loadRuleset(ruleset);
		User u = createUser();
		BuddyList buddy = createBuddies();
		BlockList block = createBlock();
		// now assert the facts
		try {
			engine.assertObject(u, "User", false, true);
			engine.assertObject(buddy,"BuddyList",false,true);
			engine.assertObject(block,"BlockList",false,true);
			return engine;
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		return null;
	}
	
	public User createUser() {
		User usr = new User();
		usr.setCity("Boston");
		usr.setCountry("US");
		usr.setFirstName("John");
		Inet4Address ip = null;
		usr.setIpAddress(ip);
		usr.setLastName("Doe");
		usr.setPostalCode("010101");
		usr.setPublicProfile(false);
		usr.setStreet1("100 Massachusetts Ave");
		usr.setUserId("john.doe");
		return usr;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BuddyList createBuddies() {
		BuddyList buddies = new BuddyList();
		buddies.setUserId("john.doe");
		ArrayList buds = new ArrayList();
		buds.add("jane.doe");
		buds.add("mike.doe");
		buds.add("howard.doe");
		buds.add("bestbuds");
		buddies.setBuddies(buds);
		return buddies;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BlockList createBlock() {
		BlockList block = new BlockList();
		block.setUserId("john.doe");
		ArrayList bl = new ArrayList();
		bl.add("hot.tomalie");
		bl.add("george.bush");
		bl.add("dick.cheney");
		block.setBlocked(bl);
		return block;
	}
	
	public Message createSimpleMessage() {
		Message msg = new Message();
		msg.setReceiverId("john.doe");
		msg.setMessageStatus(Message.QUEUED);
		msg.setSenderId("jane.doe");
		msg.setSendTime(Calendar.getInstance());
		msg.setText("hello john");
		return msg;
	}
	
	public Message createSpamMessage() {
		Message msg = new Message();
		msg.setReceiverId("john.doe");
		msg.setMessageStatus(Message.QUEUED);
		msg.setSenderId("dick.cheney");
		msg.setSendTime(Calendar.getInstance());
		msg.setText("hello john");
		return msg;
	}
}
