package com.ringchash.dodot.aviad.ringchash;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by AVIAD on 1/5/2015.
 */
public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,ManagerService.class));
    }
}
