package org.jamocha.service;

import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import woolfel.examples.model.Account;

public class JsonDataTest extends TestCase {

	public JsonDataTest() {
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testLoadJson() {
		JSONData jdata = new JSONData();
		jdata.setName("org.jamocha.examples.model.Account");
		jdata.setUrl("./samples/configuration/accounts.json");
		
		jdata.setData(jdata.loadJsonData(jdata.getUrl(), Account.class));
		
		assertNotNull(jdata.getData());
		System.out.println("count: " + ((List)jdata.getData()).size());
	}
	
	public static void main(String[] args) {
	}

}
