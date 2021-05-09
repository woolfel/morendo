package org.jamocha.rete.functions.messaging;

import java.util.ArrayList;
import java.util.List;

import org.jamocha.rete.FunctionGroup;
import org.jamocha.rete.Rete;

public class MessagingFunctions implements FunctionGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList funcs = new ArrayList();
	
	public MessagingFunctions() {
	}

	public String getName() {
		return MessagingFunctions.class.getSimpleName();
	}

	public List listFunctions() {
		return funcs;
	}

	public void loadFunctions(Rete engine) {
		try {
			CloseMessagingClientFunction closeClient = new CloseMessagingClientFunction();
			engine.declareFunction(closeClient);
			funcs.add(closeClient);
			CreateMessageClientFunction createClient = new CreateMessageClientFunction();
			engine.declareFunction(createClient);
			funcs.add(createClient);
			RegisterContentHandlerFunction register = new RegisterContentHandlerFunction();
			engine.declareFunction(register);
			funcs.add(register);
			PrintContentHandlersFunction printch = new PrintContentHandlersFunction();
			engine.declareFunction(printch);
			funcs.add(printch);
			PrintMessageClientsFunction printclients = new PrintMessageClientsFunction();
			engine.declareFunction(printclients);
			funcs.add(printclients);
			SendMessageFunction sendMsg = new SendMessageFunction();
			engine.declareFunction(sendMsg);
			funcs.add(sendMsg);
		} catch (Exception e) {
			
		}
	}

}
