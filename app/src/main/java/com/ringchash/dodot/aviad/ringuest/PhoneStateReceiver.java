package com.ringchash.dodot.aviad.ringuest;

/**
 * Created by AVIAD on 12/28/2014.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

//Automatically called when State Change is Detected because this Receiver is Registered for PHONE_STATE intent filter in AndroidManifest.xml
public class PhoneStateReceiver extends BroadcastReceiver {

    TelephonyManager manager;
    PhoneStateMonitor phoneStateListener;
    static boolean isAlreadyListening = false;

    //This Method automatically Executed when Phone State Change is Detected
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        phoneStateListener = new PhoneStateMonitor(context);//Creating the Object of Listener
        manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);//Getting the Telephony Service Object
        if (!isAlreadyListening)//Checking Listener is Not Registered with Telephony Services
        {
            manager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);//Registering the Listener with Telephony to listen the State Change
            Toast.makeText(context, "Start", Toast.LENGTH_LONG).show();
            isAlreadyListening = true;  //setting true to indicate that Listener is listening the Phone State
        }

    }

}