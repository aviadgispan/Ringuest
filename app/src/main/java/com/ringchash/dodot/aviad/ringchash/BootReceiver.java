package com.ringchash.dodot.aviad.ringchash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by AVIAD on 1/5/2015.
 */
public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
        boolean firstRun=sp.getBoolean(ConfigAppData.FIRST_RUN,true);
        if(!firstRun){
            context.startService(new Intent(context,ManagerService.class));
        }

    }
}
