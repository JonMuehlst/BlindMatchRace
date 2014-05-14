package com.blindmatchrace.test;

import com.blindmatchrace.classes.SendDataHThread;

import junit.framework.TestCase;

public class SendDataHThreadTest extends TestCase {

	public static final int TIMEOUT = 2500;
	
	public SendDataHThreadTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testHttpConnSendData() throws Exception{
		 SendDataHThread SDHT = new SendDataHThread("test");
		 SDHT.setLat("32.103758");
		 SDHT.setLng("35.2046"); 
		 SDHT.setSpeed("0");
		 SDHT.setBearing("0"); 
		 SDHT.setFullUserName("test");
		 SDHT.setEvent("1234");
		 SDHT.run();
		 if(SDHT.exception != null){
			throw SDHT.exception;
		}
		 
	}
}
