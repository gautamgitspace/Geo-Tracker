/**
 //  MBP111.0138.B16
 //  Geo Tracker v1.2 ~ with SQLite database functionality
 //	 Geo Tracker v1.3 ~ with Export to CSV functionality
 //  Geo Tracker v1.4 ~ reverse engineers coordinates into addresses
 //  System Serial: C02P4SP9G3QH
 //  Created by Abhishek Gautam on 4/04/2016
 //  agautam2@buffalo.edu
 //  University at Buffalo, The State University of New York.
 //  Copyright © 2016 Gautam. All rights reserved.
 */


package com.example.gpstracking;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


public class GPSTracker extends Service implements LocationListener
{

	private final Context mContext;

	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	boolean useNetwork = false;
	public Geocoder geocoder;

	Location location;
	double latitude;
	double longitude;
	String locality;
	String adminArea;
	String countryCode;
	String throughFare;

	private static final long distance = 10;
	private static final long updateInterval = 30000;
	static final String TAG = "[GPS-DEBUG]";
	protected LocationManager locationManager;
	protected LocationListener locationListener;

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
			Log.v(TAG, "trying to get location from Wi-Fi or Cellular Towers");
			geocoder = new Geocoder(mContext);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, updateInterval, distance, this);
			Log.v(TAG, "NETWORK BASED");
			if(locationManager==null)
			{
				Log.v(TAG, "location manager returned null");
			}
			if (locationManager != null)
			{
				location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location != null)
				{
					latitude = location.getLatitude();
					longitude = location.getLongitude();
					Log.v(TAG, "LAT: " + Double.toString(latitude));
					Log.v(TAG, "LONG: " + Double.toString(longitude));

					try {
						if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
						{
							Log.v(TAG,"Attempting to resolve address");
							List<Address> locationList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
							Log.v(TAG,locationList.get(0).toString());
							if(locationList.get(0).getLocality()!=null)
							{
								locality = locationList.get(0).getLocality();
								Log.v("[LOCALITY]", locality);
							}
							if(locationList.get(0).getAdminArea()!=null)
							{
								adminArea = locationList.get(0).getAdminArea();
								Log.v("[ADMIN AREA]", adminArea);
							}
							if(locationList.get(0).getCountryName()!=null)
							{
								countryCode = locationList.get(0).getCountryName();
								Log.v("[COUNTRY]", countryCode);
							}
							if(locationList.get(0).getThoroughfare()!=null)
							{
								throughFare = locationList.get(0).getThoroughfare();
								Log.v("[COUNTRY]", throughFare);
							}
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

					ContentValues contentValues = new ContentValues();
					DBHandler dbHandler = new DBHandler(mContext);
					SQLiteDatabase sqLiteDatabase = dbHandler.getWritableDatabase();
					contentValues.put("LAT",latitude);
					contentValues.put("LONG",longitude);
					contentValues.put("NETWORK_PROVIDER", "NETWORK");
					contentValues.put("LOCALITY",throughFare);
					contentValues.put("CITY",locality);
					contentValues.put("STATE",adminArea);
					contentValues.put("COUNTRY",countryCode);
					sqLiteDatabase.insert("footRecords", null, contentValues);
					sqLiteDatabase.close();
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
		geocoder = new Geocoder(mContext);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateInterval, distance, this);
		Log.v(TAG, "GPS BASED");
		if (locationManager != null)
		{
			Log.v(TAG, "GPS BASED 1");
			location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null)
			{
				Log.v(TAG, "GPS BASED 2");
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				Log.v(TAG, "LAT: " + Double.toString(latitude));
				Log.v(TAG, "LONG: " + Double.toString(longitude));

				try {
					if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
					{
						Log.v(TAG,"Attempting to resolve address");
						List<Address> locationList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
						if(locationList.get(0).getLocality()!=null)
						{
							locality = locationList.get(0).getLocality();
							Log.v("[LOCALITY]", locality);
						}
						if(locationList.get(0).getAdminArea()!=null)
						{
							adminArea = locationList.get(0).getAdminArea();
							Log.v("[ADMIN AREA]", adminArea);
						}
						if(locationList.get(0).getCountryName()!=null)
						{
							countryCode = locationList.get(0).getCountryName();
							Log.v("[COUNTRY]", countryCode);
						}
						if(locationList.get(0).getThoroughfare()!=null)
						{
							throughFare = locationList.get(0).getThoroughfare();
							Log.v("[THROUGH FARE]", throughFare);
						}
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

					ContentValues contentValues = new ContentValues();
					DBHandler dbHandler = new DBHandler(mContext);
					SQLiteDatabase sqLiteDatabase = dbHandler.getWritableDatabase();
					contentValues.put("LAT",latitude);
					contentValues.put("LONG",longitude);
					contentValues.put("NETWORK_PROVIDER", "GPS");
					contentValues.put("LOCALITY",throughFare);
					contentValues.put("CITY",locality);
					contentValues.put("STATE",adminArea);
					contentValues.put("COUNTRY",countryCode);
					sqLiteDatabase.insert("footRecords", null, contentValues);
					sqLiteDatabase.close();

			}
			if(location==null)
			{
				Log.v(TAG, "Location Returned NULL");
			}
		}
		if(locationManager==null)
		{
			Log.v(TAG, "Location Manager Returned NULL");
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

				if (isNetworkEnabled)
				{
					Log.v(TAG,"Network Based Location Services are ENABLED");
				}
				if(!isNetworkEnabled)
				{
					Log.v(TAG,"isProviderEnabled returned FALSE for Provider Type: NETWORK");
				}
				//Check for GPS
				if (isGPSEnabled)
				{
					this.canGetLocation = true;
					Log.v(TAG, "GPS Based Location Services are ENABLED");
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

	public void showSettingsAlert()
	{
		Location location;
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle("GPS disabled");
		alertDialog.setMessage("Do you want to enable it now in settings?");
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});

		alertDialog.setNegativeButton("No. Use Network", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				getLocationByNetwork();
				Toast.makeText(mContext, "You are at - " + throughFare + ", " + locality + ", " + adminArea + ", " + countryCode + "\n" +
						"Latitude: " + latitude + "\nLongitude: " + longitude,  Toast.LENGTH_LONG).show();
				// Toast.makeText(mContext, "You are at - \nLatitude: " + latitude + "\nLongitude: " + longitude +"\n" + throughFare+ ", " + locality + ", " + adminArea + ", " + countryCode,  Toast.LENGTH_LONG).show();
				//Toast.makeText(mContext, "You are at - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

			}
		});

		alertDialog.show();
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
	public String getLocality()
	{
		return locality;
	}
	public String getCountryCode()
	{
		return countryCode;
	}
	public String getAdminArea()
	{
		return adminArea;
	}
	public String getThroughFare()
	{
		return throughFare;
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
