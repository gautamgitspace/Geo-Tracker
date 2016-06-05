package com.example.gpstracking;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TrackActivity extends Activity
{
	
	Button track;
	GPSTracker gps;
	static final String TAG = "[GPS-DEBUG]";

	
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        track = (Button) findViewById(R.id.btnShowLocation);
		track.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
		        gps = new GPSTracker(TrackActivity.this);
		        if(gps.canGetLocation())
				{
					gps.getLocationByGPS();
					double latitude = gps.getLatitude();
					double longitude = gps.getLongitude();
		        	// \n is for new line
		        	Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();	
		        }
				else
				{
		        	//CASE - when GPS is disabled on device. Prompt user to enable GPS or user Network instead
					Log.v(TAG, "No GPS services were found, fail-over to NETWORK");
		        	gps.showSettingsAlert();

		        }
				
			}
		});
    }
    
}