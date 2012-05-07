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
	
	// TODO: Change to my BT's MAC and ensure device is added to Anduino
	private static final String DEVICE_ADDRESS = "00:00:00:00:00:00";
	
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
    	registerReceiver(arduino, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));
    	
    	// Connect to the device
    	Amarino.connect(this, DEVICE_ADDRESS);
    }
    
    @Override
    public void onStop(){
    	super.onStop();
    	
    	// Always disconnect the Arduino when exiting the app
    	Amarino.disconnect(this, DEVICE_ADDRESS);
    	
    	// And un-register
    	unregisterReceiver(arduino);
    }
    
    public class ArduinoReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
	    	String data = null;
	    	final String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);
	    	final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);
			
	    	if(dataType == AmarinoIntent.STRING_EXTRA){
	    		data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);
	    		
	    		if(data != null){
	    			Log.d(NinjaScreenActivity.TAG, "Data is = " + data);
	    		}
	    	}
		}
    }
}