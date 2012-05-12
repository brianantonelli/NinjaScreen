package com.ninjascreen;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class NinjaScreenActivity extends Activity {
	private static final String TAG = "NinjaScreen";
	
	// BT's MAC on Arduino
	private static final String DEVICE_ADDRESS = "00:06:66:08:E7:60";
	private ArduinoReceiver arduino = new ArduinoReceiver(); 
	
	// View bindings
	private TextView rightSignal;
	private TextView leftSignal;
	private TextView highBeams;
	private TextView neutral;
	private TextView lowFuel;
	private TextView tach;
	private TextView fuelInj;
	private TextView oil;
	private TextView waterTemp;
	private TextView speedo;
	private TextView clock;
	
	// GPS / Speedometer Stuff
    private LocationManager lm;
    private LocationListener locationListener;
    private Integer data_points = 2; // how many data points to calculate for
    private Double[][] positions;
    private Long[] times;

	// Guages idea: http://stackoverflow.com/questions/6156674/android-gauge-animation-question
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Associate the view bindings
        rightSignal = (TextView) findViewById(R.id.rightSignal);
        leftSignal = (TextView) findViewById(R.id.leftSignal);
        highBeams = (TextView) findViewById(R.id.highBeams);
        neutral = (TextView) findViewById(R.id.neutral);
        lowFuel = (TextView) findViewById(R.id.lowFuel);
        tach = (TextView) findViewById(R.id.tach);
        fuelInj = (TextView) findViewById(R.id.fuelInj);
        oil = (TextView) findViewById(R.id.oil);
        waterTemp = (TextView) findViewById(R.id.waterTemp);
        speedo = (TextView) findViewById(R.id.speedo);
        clock = (TextView) findViewById(R.id.clock);
        
        // Kick off the GPS / Speedo stuff
        // two arrays for position and time.
        positions = new Double[data_points][2];
        times = new Long[data_points];
        
        // keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // use the LocationManager class to obtain GPS locations
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);    
        locationListener = new MyLocationListener();
    }

    @Override
    public void onStart(){
    	super.onStart();
    	
    	// Register our receiver so we can receive broadcasts
    	Log.d(TAG, "Registering receiver");
    	registerReceiver(arduino, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));
    	registerReceiver(arduino, new IntentFilter(AmarinoIntent.ACTION_CONNECTED));
    	
    	// Connect to the device
    	Log.d(TAG, "Connecting to Arduino");
    	Amarino.connect(this, DEVICE_ADDRESS);
    	
    	//Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'l', 0);
    	
    	// Setup full screen view
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    @Override
    public void onPause() {
    	// Turn off GPS tracking when in the background - conserve battery!
        lm.removeUpdates(locationListener);
        super.onPause();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Restart GPS tracking when resuming from the background
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	
    	// Always disconnect the Arduino when exiting the app
    	Log.d(TAG, "Disconnecting from Arduino");
    	Amarino.disconnect(this, DEVICE_ADDRESS);
    	
    	
    	// And un-register
    	Log.d(TAG, "Unregistering receiver");
    	unregisterReceiver(arduino);
    }
    
    // Arduino Receiver Implementation -- handles responses from Arduino
    public class ArduinoReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "*-rx-*");
	    	String data = null;
	    	final String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
	    	
	    	Log.d(TAG, "Received Event From: " + address);
	    	
	    	final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
			
	    	if(dataType == AmarinoIntent.STRING_EXTRA){
	    		data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
	    		
	    		if(data != null){
	    			Log.d(NinjaScreenActivity.TAG, "Data is = " + data);
	    			if(data.charAt(0) != '_' || data.charAt(data.length()-1) != '~'){
	    				Log.e(TAG, "First or last special char missing! Trash data!");
	    			}
	    			else{
	    				String[] peices = data.substring(1, data.length()-1).split("\\|");
	    				if(peices.length != 9){
	    					Log.e(TAG, "Wrong amount of peices! Expected 9 but got: " + peices.length);
	    					return;
	    				}
	    				rightSignal.setText("Right Turn Signal: " + ("1".equals(peices[0]) ? "on" : "off"));
	    				leftSignal.setText("Left Turn Signal: " + ("1".equals(peices[1]) ? "on" : "off"));
	    				highBeams.setText("High Beams: " + ("1".equals(peices[2]) ? "on" : "off"));
	    				neutral.setText("Neutral: " + ("1".equals(peices[3]) ? "engaged" : "disengaged"));
	    				lowFuel.setText("Low Fuel: " + ("1".equals(peices[4]) ? "yes!!!" : "nope"));
	    				tach.setText("Tachometer: " + peices[5]);
	    				fuelInj.setText("Fuel Injectors: " + ("1".equals(peices[6]) ? "trouble!!!" : "okay"));
	    				oil.setText("Oil: " + ("1".equals(peices[7]) ? "trouble!!!" : "okay"));
	    				waterTemp.setText("Water Temperature: " + ("1".equals(peices[8]) ? "trouble!!!" : "okay"));
	    				clock.setText(new SimpleDateFormat("hh:mm aaa").format(new Date()));
	    			}
	    		}
	    	}
		}
    }
    
    // Location listener, used for GPS tracking for speedometer
    private class MyLocationListener implements LocationListener {
        Integer counter = 0;
        
        public void onLocationChanged(Location loc) {   
            if (loc != null) {
                String speed_string;
                Double d1;
                Long t1;
                Double speed = 0.0;
                d1 = 0.0;
                t1 = 0l;
        
                positions[counter][0] = loc.getLatitude();
                positions[counter][1] = loc.getLongitude();
                times[counter] = loc.getTime();
                
                if (loc.hasSpeed()) {
                    speed = loc.getSpeed() * 1.0; // need to * 1.0 to get into a double for some reason...
                }
                else {
                    try {
                        // get the distance and time between the current position, and the previous position.
                        // using (counter - 1) % data_points doesn't wrap properly
                        d1 = distance(positions[counter][0], positions[counter][1], positions[(counter+(data_points - 1)) % data_points][0], positions[(counter + (data_points -1)) %data_points][1]);
                        t1 = times[counter] - times[(counter + (data_points - 1)) % data_points];
                    } 
                    catch (NullPointerException e) {
                        //all good, just not enough data yet.
                    }
                    speed = d1 / t1; // m/s
                }                
                counter = (counter + 1) % data_points;
                
                // convert from m/s to specified units
//              speed = speed * 3.6d; // kmph
                speed = speed * 2.23693629d; // mph
//              speed = speed * 1.94384449d; // knots

                // update display
                speedo.setText(speed + " mph");
            }
        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            Log.i(getResources().getString(R.string.app_name), "provider disabled : " + provider);
        }

 
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            Log.i(getResources().getString(R.string.app_name), "provider enabled : " + provider);
        }

   
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
            Log.i(getResources().getString(R.string.app_name), "status changed : " + extras.toString());
        }
        
        // private functions       
        private double distance(double lat1, double lon1, double lat2, double lon2) {
	        // haversine great circle distance approximation, returns meters
	        double theta = lon1 - lon2;
	        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	        dist = Math.acos(dist);
	        dist = rad2deg(dist);
	        dist = dist * 60; // 60 nautical miles per degree of seperation
	        dist = dist * 1852; // 1852 meters per nautical mile  
	        
	        return (dist);
        }
	
	    private double deg2rad(double deg) {
	      return (deg * Math.PI / 180.0);
	    }
	
	    private double rad2deg(double rad) {
	      return (rad * 180.0 / Math.PI);
	    }               
    }

}