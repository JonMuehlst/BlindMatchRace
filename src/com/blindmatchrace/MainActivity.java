package com.blindmatchrace;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.blindmatchrace.classes.C;
import com.blindmatchrace.classes.GetBuoysTask;
import com.blindmatchrace.classes.GetSailorsTask;
import com.blindmatchrace.classes.SendDataHThread;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

/**
 * Main activity. Shows a google map with the sailors, buoys and current position.
 * 
 */
public class MainActivity extends FragmentActivity implements LocationListener, OnInitListener {

	// < JonM :
		private TextToSpeech mTts;
	    // This code can be any value you want, its just a checksum.
	    private static final int MY_DATA_CHECK_CODE = 1234;
	// / >
	
	// Application variables.
	private String user = "", pass = "", event = "", fullUserName = "";
	private LocationManager locationManager;
	private Circle[] buoyRadiuses = new Circle[C.MAX_BUOYS];
	private MediaPlayer buoyBeep;
	private boolean disableLocation = false;

	// Views.
	private Marker currentPosition;
	private List<Marker> sailorMarkers = new ArrayList<Marker>();
	private GoogleMap googleMap;
	private TextView tvLat, tvLng, tvUser, tvSpeed, tvDirection, tvEvent;
	Handler handler = new Handler();
	Runnable r = new Runnable() {
		
		@Override
		public void run() {
			runnableGo();
			
		}
	};

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Disables lock-screen and keeps screen on.
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// < JonM: onClick listener for buttonSpeak
		// Fire off an intent to check if a TTS engine is installed
	        Intent checkIntent = new Intent();
	        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
	        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
			
			Button buttonSpeak = (Button) findViewById(R.id.buttonSpeak);
			buttonSpeak.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
			        // grab the contents of the text box.
			        
					String text = "Speed " + tvSpeed.getText().toString() + 
								  " Direction " + 
								  tvDirection.getText().toString();
					if(text != null)
				        mTts.speak(text,
				                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
				                null);
					}
			});
		// JonM /> 

		initialize();
		

		handler.postDelayed(r, 10000);
		
		
	}
	private void runnableGo(){
		double speed =  Double.parseDouble(tvSpeed.getText().toString());
		speed = Math.round(speed * 100);
	    speed = speed / 100;
		String speedtext = "Speed " +  speed;
		String directiontext = "Direction " + tvDirection.getText().toString();
		if(speedtext != null) mTts.speak(speedtext + " " + directiontext, TextToSpeech.QUEUE_FLUSH,  null);

		handler.postDelayed(r, 10000);
	}
	/**
	 * Initialize components.
	 */
	private void initialize() {
		// The user name, password and event number connected to the application.
		user = getIntent().getStringExtra(C.USER_NAME);
		pass = getIntent().getStringExtra(C.USER_PASS);
		event = getIntent().getStringExtra(C.EVENT_NUM);
		fullUserName = user + "_" + pass + "_" + event;

		// Initialize location ability.
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		String provider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(provider, C.MIN_TIME, C.MIN_DISTANCE, this);

		// Initialize map.
		FragmentManager fm = getSupportFragmentManager();
		googleMap = ((SupportMapFragment) fm.findFragmentById(R.id.map)).getMap();

		// Adds location button in the top-right screen.
		googleMap.setMyLocationEnabled(true);

		// Focus the camera on the latLng location.
		LatLng latLng = new LatLng(32.056286, 34.824598);
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, C.ZOOM_LEVEL);
		googleMap.animateCamera(cameraUpdate);

		// Initializing TextViews.
		tvLat = (TextView) findViewById(R.id.tvLat);
		tvLng = (TextView) findViewById(R.id.tvLng);
		tvSpeed = (TextView) findViewById(R.id.tvSpeed);
		tvDirection = (TextView) findViewById(R.id.tvDirection);
		tvUser = (TextView) findViewById(R.id.tvUser);
		tvEvent = (TextView) findViewById(R.id.tvEvent);
		tvUser.setText(user.substring(6));
		tvEvent.setText(event);

		// Loads the buoy warning beep sound.
		buoyBeep = MediaPlayer.create(this, R.raw.buoy_warning_beep);

		// AsyncTask for getting the buoy's locations from DB and adding them to the google map.
		GetBuoysTask getBuoys = new GetBuoysTask("GetBuoysTask", googleMap, buoyRadiuses, event);
		getBuoys.execute(C.URL_CLIENTS_TABLE);

		// AsyncTask for getting the sailor's locations from DB and adding them to the google map.
		GetSailorsTask getSailors = new GetSailorsTask("GetSailorsTask", googleMap, sailorMarkers, fullUserName, event);
		getSailors.execute(C.URL_CLIENTS_TABLE);
		
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		// Disables the location changed code.
		disableLocation = true;
		finish();
	}

	@Override
	public void onLocationChanged(Location location) {
		if (!disableLocation) {
			// HandlerThread for sending the current location to DB.
			SendDataHThread thread = new SendDataHThread("SendGPS");
			thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);

			String lat = new DecimalFormat("##.######").format(location.getLatitude());
			String lng = new DecimalFormat("##.######").format(location.getLongitude());
			String speed = "" + location.getSpeed();
			String bearing = "" + location.getBearing();

			thread.setFullUserName(fullUserName);
			thread.setLat(lat);
			thread.setLng(lng);
			thread.setSpeed(speed);
			thread.setBearing(bearing);
			thread.setEvent(event);

			thread.start();

			// AsyncTask for getting the sailor's locations from DB and adding them to the google map.
			GetSailorsTask getSailors = new GetSailorsTask("GetSailorsTask", googleMap, sailorMarkers, fullUserName, event);
			getSailors.execute(C.URL_CLIENTS_TABLE);

			// Updates TextViews in layout.
			tvLat.setText(lat);
			tvLng.setText(lng);
			tvSpeed.setText(speed);
			tvDirection.setText(bearing);

			// Adds currentPosition marker to the google map.
			LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
			if (currentPosition != null) {
				currentPosition.remove();
			}
			currentPosition = googleMap.addMarker(new MarkerOptions().position(latLng).title("Current Position").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_sailor_user_low)));

			// Focus the camera on the currentPosition marker.
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, C.ZOOM_LEVEL);
			googleMap.animateCamera(cameraUpdate);

			// Checks if the user is near a buoy by measuring the distance between the currentPosition marker and the radius around the buoys. If near then a beep sound is made.
			float[] distance = new float[2];
			for (int i = 0; i < buoyRadiuses.length; i++) {
				if (buoyRadiuses[i] != null) {
					Location.distanceBetween(currentPosition.getPosition().latitude, currentPosition.getPosition().longitude, buoyRadiuses[i].getCenter().latitude, buoyRadiuses[i].getCenter().longitude, distance);
					if (distance[0] < buoyRadiuses[i].getRadius()) {
						buoyBeep.start();
						Toast.makeText(this, "Near a buoy! move away!", Toast.LENGTH_LONG).show();
						break;
					}
				}
			}

			
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	// < JonM
		public void onActivityResult(int requestCode, int resultCode, Intent data)
	    {
	        if (requestCode == MY_DATA_CHECK_CODE)
	        {
	            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
	            {
	                // success, create the TTS instance
	            	mTts = new TextToSpeech(this, this);
	            }
	            else
	            {
	                // missing data, install it
	                Intent installIntent = new Intent();
	                installIntent.setAction(
	                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	                startActivity(installIntent);
	            }
	        }
	    }
	// / >
	
	// < JonM
	@Override
    public void onDestroy()
    {
        // Don't forget to shutdown!
        if (mTts != null)
        {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }
	// / >

	// < JonM
	@Override
	public void onInit(int arg0) {
		// TODO Auto-generated method stub
		
	}
	// / >

}
