/**
 * Copyright 2006-2010 Alexander Wilden, Christoph Emonds, Sebastian Reinartz
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jamocha.messagerouter.MessageEvent;
import org.jamocha.messagerouter.MessageRouter;
import org.jamocha.messagerouter.StreamChannel;

public class Shell {

	public static final String CHANNELNAME = "Shell";

	private MessageRouter router;

	private StreamChannel channel;

	public Shell(Rete engine) {
		router = engine.getMessageRouter();
		channel = router.openChannel(CHANNELNAME, System.in);
		engine.getMessageRouter().setCurrentChannelId(channel.getChannelId());
	}

    /**
     * run is the main method for the shell.
     */
	public void run() {
		List<MessageEvent> msgEvents = new ArrayList<MessageEvent>();
		boolean printPrompt = false;
		System.out.println(Constants.PROJECT_MESSAGE);
		System.out.println(Constants.SHELL_MESSAGE);
		System.out.print(Constants.SHELL_PROMPT);

		while (true) {
			channel.fillEventList(msgEvents);
			if (!msgEvents.isEmpty()) {
				for (MessageEvent event : msgEvents) {
					if (event.getType() == MessageEvent.PARSE_ERROR
							|| event.getType() == MessageEvent.ERROR
							|| event.getType() == MessageEvent.RESULT) {
						printPrompt = true;
					}
					if( event.getType() == MessageEvent.ERROR) {
						System.out.println(exceptionToString((Exception)event.getMessage()).trim());
					}
					if (event.getType() != MessageEvent.COMMAND && !event.getMessage().toString().equals("")) {
						if(event.getMessage() instanceof DefaultReturnVector) {
							DefaultReturnVector rv = (DefaultReturnVector) event.getMessage();
							if (rv.getItems().size() > 0) {
								ReturnValue rval = (ReturnValue) rv.getItems().firstElement();
								if ((rval.getValueType() == Constants.ARRAY_TYPE) || 
										(rval.getValueType() == Constants.LIST_TYPE))  {
									System.out.print(Arrays.toString((Object[])rval.getValue()) 
											+ System.getProperty("line.separator"));
								}  else	System.out.print(event.getMessage().toString());
							}
						}
						else System.out.print(event.getMessage().toString());
					}
	
						 
				}
				msgEvents.clear();
				if (printPrompt) {
					System.out.print(Constants.SHELL_PROMPT);
				}
				printPrompt = false;
			} else {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Converts an Exception to a String namely turns the StackTrace to
	 * a String.
	 * 
	 * @param exception
	 *            The Exception
	 * @return A nice String representation of the Exception
	 */
	private String exceptionToString(Exception exception) {
		StringBuilder res = new StringBuilder();
		StackTraceElement[] str = exception.getStackTrace();
		for (int i = 0; i < str.length; ++i) {
			res.append(str[i] + System.getProperty("line.separator"));
		}
		return res.toString();
	}

}
