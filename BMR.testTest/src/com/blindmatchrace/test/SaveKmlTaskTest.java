package com.blindmatchrace.test;

import junit.framework.TestCase;
import com.blindmatchrace.classes.*;
import android.test.mock.MockContext;

public class SaveKmlTaskTest extends TestCase {

	public SaveKmlTaskTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSaveKmlTask(){
		MockContext mContext = new MockContext();
		SaveKmlTask SKT = new SaveKmlTask(mContext, "testKML1", "Sailoruser2004_2004_2004", 1);
		SaveKmlTask SKT2 = new SaveKmlTask(mContext, "testKML2", "Sailoruser2004_2004_2004", 2);
		SKT.execute(C.URL_HISTORY_TABLE, C.URL_CLIENTS_TABLE);
		SKT2.execute(C.URL_HISTORY_TABLE, C.URL_CLIENTS_TABLE);
	}

}
