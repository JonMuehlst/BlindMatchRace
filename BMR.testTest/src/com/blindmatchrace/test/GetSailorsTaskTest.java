package com.blindmatchrace.test;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.blindmatchrace.*;
import java.util.ArrayList;
import java.util.List;

import com.blindmatchrace.classes.C;
import com.blindmatchrace.classes.GetSailorsTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import android.support.v4.app.*;

public class GetSailorsTaskTest extends ActivityInstrumentationTestCase2<MainActivity> {

	Activity activity;
	GoogleMap gmap;
	List<Marker> sailorMarkers;
	
	public GetSailorsTaskTest() {
		super(MainActivity.class);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.putExtra(C.USER_NAME, "Sailortest");
		intent.putExtra(C.USER_PASS, "1234");
		intent.putExtra(C.EVENT_NUM, "1234");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setActivityIntent(intent);
        
        activity = getActivity();
        
    }
	
	public void testPreConditions() {
        assertNotNull(activity);
        
	}
	
	public void testGetSailorsTask() throws Exception{
		
		FragmentManager fm = getActivity().getSupportFragmentManager();
		gmap = ((SupportMapFragment) fm.findFragmentById(com.blindmatchrace.R.id.map)).getMap();
		
        sailorMarkers = new ArrayList<Marker>();
		
		GetSailorsTask getSailors = new GetSailorsTask("GetSailorsTask",  gmap, sailorMarkers, "Sailoruser2004_2004_2004", "1234");
		getSailors.execute(C.URL_CLIENTS_TABLE);
		
		if(getSailors.getException() != null){
			throw getSailors.getException();
		}
		
	}

	

}
