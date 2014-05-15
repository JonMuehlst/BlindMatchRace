package com.blindmatchrace.test;

import android.app.Activity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.blindmatchrace.*;

import java.util.ArrayList;
import java.util.List;

import com.blindmatchrace.classes.C;
import com.blindmatchrace.classes.GetBuoysTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

import android.support.v4.app.*;

public class GetBuoysTaskTest extends ActivityInstrumentationTestCase2<MainActivity> {

	Activity activity;
	GoogleMap gmap;
	Circle[] buoyRadiuses;
	
	public GetBuoysTaskTest() {
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
	
	public void testGetBuoysTask() throws Exception{
		
		FragmentManager fm = getActivity().getSupportFragmentManager();
		gmap = ((SupportMapFragment) fm.findFragmentById(com.blindmatchrace.R.id.map)).getMap();
		
        buoyRadiuses = new Circle[C.MAX_BUOYS];
		
		GetBuoysTask getBuoys = new GetBuoysTask("GetBuoysTask",  gmap, buoyRadiuses, "1234");
		getBuoys.execute(C.URL_CLIENTS_TABLE);
		
		if(getBuoys.getException() != null){
			throw getBuoys.getException();
		}
		
	}

	

}
