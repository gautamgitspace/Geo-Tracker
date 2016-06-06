package com.example.gpstracking;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;


public class TrackActivity extends Activity
{
	
	Button track;
	GPSTracker gps;
	static final String TAG = "[GPS-DEBUG]";
	Context context;

	public void exportDatabase()
	{
		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state))
		{
			Toast.makeText(this, "MEDIA MOUNT ERROR!", Toast.LENGTH_LONG).show();
		}
		else
		{
			File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			if (!exportDir.exists())
			{
				exportDir.mkdirs();
				Log.v(TAG, "Directory made");
			}

			File file = new File(exportDir, "GeoData.csv") ;
			PrintWriter printWriter = null;
			try
			{
				file.createNewFile();
				printWriter = new PrintWriter(new FileWriter(file));
				DBHandler dbHandler = new DBHandler(getApplicationContext());
				SQLiteDatabase sqLiteDatabase = dbHandler.getReadableDatabase();
				Cursor curCSV = sqLiteDatabase.rawQuery("select * from footRecords", null);
				printWriter.println("Latitude,Longitude");
				while(curCSV.moveToNext())
				{
					Double latitude = curCSV.getDouble(curCSV.getColumnIndex("LAT"));
					Double longitude = curCSV.getDouble(curCSV.getColumnIndex("LONG"));

					String record = latitude + "," + longitude;
					Log.v(TAG, "attempting to write to file");
					printWriter.println(record);
					Log.v(TAG, "data written to file");
				}
				curCSV.close();
				sqLiteDatabase.close();
			}

			catch(Exception exc)
			{
				exc.printStackTrace();
				Toast.makeText(this, "ERROR!", Toast.LENGTH_LONG).show();
			}
			finally
			{
				if(printWriter != null) printWriter.close();
			}

			//If there are no errors, return true.
			Toast.makeText(this, "DB Exported to CSV file!", Toast.LENGTH_LONG).show();
		}
	}

	private void deleteDB()
	{
		boolean result = this.deleteDatabase("mainTuple");
		if (result==true)
		{
			Toast.makeText(this, "DB Deleted!", Toast.LENGTH_LONG).show();
		}
	}
	private void exportDB()
	{
		File sd = Environment.getExternalStorageDirectory();
		File data = Environment.getDataDirectory();
		FileChannel source = null;
		FileChannel destination = null;
		String currentDBPath = "/data/" + "com.example.gpstracking" + "/databases/" + "mainTuple";
		String backupDBPath = "mainTuple";
		File currentDB = new File(data, currentDBPath);
		File backupDB = new File(sd, backupDBPath);
		if (currentDB.exists())
		{
			try
			{
				source = new FileInputStream(currentDB).getChannel();
				destination = new FileOutputStream(backupDB).getChannel();
				destination.transferFrom(source, 0, source.size());
				source.close();
				destination.close();
				Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		track = (Button) findViewById(R.id.button1);
		track.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg1)
			{
				deleteDB();
			}
		});

		track = (Button) findViewById(R.id.button2);
		track.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg1)
			{
				exportDB();
			}
		});

		track = (Button) findViewById(R.id.button3);
		track.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg1)
			{
				exportDatabase();
			}
		});

		ImageButton imageButton = (ImageButton) findViewById(R.id.btnShowLocation);
		imageButton.setOnClickListener(new View.OnClickListener()
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
		        	Toast.makeText(getApplicationContext(), "You are at - \nLatitude: " + latitude + "\nLongitude: " + longitude, Toast.LENGTH_LONG).show();
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