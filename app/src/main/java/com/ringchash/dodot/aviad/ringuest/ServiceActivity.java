package com.ringchash.dodot.aviad.ringuest;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by AVIAD on 12/23/2014.
 */
public class ServiceActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_ads);
        Button buttonStart=(Button)findViewById(R.id.startServiceButton);
        Button buttonStop=(Button)findViewById(R.id.stopServiceButton);
        Button stopAlarmButton=(Button)findViewById(R.id.stopAlarm);
        Button buttonGetDefaultRingtone=(Button)findViewById(R.id.ringtonegetDefulte);
        buttonGetDefaultRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDefRingtone();


            }

            ;
        });
        stopAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopAlarm();


            }

            ;
        });
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startManagerService();


            }

            ;
        });
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopManagerService();
            };
        });
    }
    public void startManagerService(){
        Intent intent = new Intent();
        intent.setClass(this,ManagerService.class);
        startService(intent);
    }

    /**
     * update the default ringtone
     */
    public void getDefRingtone(){
        RingtoneControl r=new RingtoneControl(this);
        r.getReaDefaultRingtone();
    }
    public void stopManagerService(){
        Intent intent = new Intent();
        intent.setClass(this,ManagerService.class);
        stopService(intent);
    }
    public void stopAlarm(){
        Intent intentstop = new Intent(this,AlarmReceiver.class);
        PendingIntent senderstop = PendingIntent.getBroadcast(this,
                0, intentstop, 0);
        AlarmManager alarmManagerstop = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManagerstop.cancel(senderstop);
    }
}
