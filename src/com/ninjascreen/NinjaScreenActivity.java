package com.ninjascreen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

public class NinjaScreenActivity extends Activity {
	private static final String TAG = "NinjaScreen";
	
	// BT's MAC on Arduino
	private static final String DEVICE_ADDRESS = "00:06:66:08:E7:60";
	
	private ArduinoReceiver arduino = new ArduinoReceiver(); 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
	    				Log.d(TAG, "Right signal: " + peices[0].equals("1") + ", Left signal: " + peices[1].equals("1") + ", High beams: " + peices[2].equals("1"));
	    			}
	    		}
	    	}
		}
    }
}