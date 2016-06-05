/**
 //  MBP111.0138.B16
 //  System Serial: C02P4SP9G3QH
 //  Created by Abhishek Gautam on 4/04/2016
 //  agautam2@buffalo.edu
 //  University at Buffalo, The State University of New York.
 //  Copyright © 2016 Gautam. All rights reserved.
 */


package com.example.gpstracking;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class GPSTracker extends Service implements LocationListener
{

	private final Context mContext;

	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	boolean useNetwork = false;

	Location location;
	double latitude;
	double longitude;

	private static final long distance = 10;
	private static final long updateInterval = 1000 * 60 * 1;
	static final String TAG = "[GPS-DEBUG]";
	protected LocationManager locationManager;

	public GPSTracker(Context context)
	{
		this.mContext = context;
		Log.v(TAG, "calling getLocation function");
		getLocationParameters();
	}

	public Location getLocationByNetwork()
	{
		try
		{
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, updateInterval, distance, this);
			Log.v(TAG, "NETWORK BASED");
			if (locationManager != null)
			{
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location != null)
				{
					latitude = location.getLatitude();
					longitude = location.getLongitude();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return location;
	}
	public Location getLocationByGPS()
	{
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateInterval, distance, this);
		Log.v(TAG, "GPS BASED");
		if (locationManager != null)
		{
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null)
			{
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				Log.v(TAG, "LAT: " + Double.toString(latitude));
				Log.v(TAG, "LONG: " + Double.toString(longitude));
			}
		}

		return location;
	}

	public Location getLocationParameters()
	{
		try
		{
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled)
			{
				Log.v(TAG, "No Network Coverage or GPS disabled");
			}
			else
			{
				//Check for Network
				if (isNetworkEnabled)
				{
					Log.v(TAG,"Network Based Location Services are ENABLED");
				}
				if(!isNetworkEnabled)
				{
					Log.v(TAG,"isProviderEnabled returned FALSE");
				}
				//Check for GPS
				if (isGPSEnabled)
				{
					this.canGetLocation = true;
					Log.v(TAG, "GPS Based Location Services are ENABLED");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return location;
	}

	//FORCE STOP GPS
	public void stopUsingGPS()
	{
		if(locationManager != null)
		{
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	public boolean canGetLocation()
	{
		return this.canGetLocation;
	}

	public boolean showSettingsAlert()
	{

		Location location;
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle("GPS disabled");
		alertDialog.setMessage("Do you want to enable it now in settings?");
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		alertDialog.setNegativeButton("No. Use Network", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which)
			{
				getLocationByNetwork();
				useNetwork=true;
			}
		});

		alertDialog.show();
		return useNetwork;
	}

	public double getLatitude()
	{
		if(location != null)
		{
			latitude = location.getLatitude();
		}
		return latitude;
	}

	public double getLongitude()
	{
		if(location != null)
		{
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	@Override
	public void onLocationChanged(Location location)
	{
		//TODO
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}





}
