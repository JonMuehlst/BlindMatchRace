package com.blindmatchrace.test;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import junit.framework.TestCase;

import com.blindmatchrace.classes.*;
import com.blindmatchrace.modules.JsonReader;

public class JsonReaderTest extends TestCase {

	public JsonReaderTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testReadJsonFromURL(){
		JSONObject jsonObj = null;
		try {
			jsonObj = JsonReader.readJsonFromUrl(C.URL_HISTORY_TABLE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(jsonObj);
	}
	

}
