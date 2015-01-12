package com.ringchash.dodot.aviad.ringchash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by AVIAD on 12/23/2014.
 */
public class ManagerService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +10000, ConfigAppData.UPDATE_INTERVAL, pendingIntent);


        Toast.makeText(this,"on Create",Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"on Start",Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"on Destroy",Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}
