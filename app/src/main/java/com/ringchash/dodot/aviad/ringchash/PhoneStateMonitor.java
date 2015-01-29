package com.ringchash.dodot.aviad.ringchash;

/**
 * Created by AVIAD on 12/28/2014.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

public class PhoneStateMonitor extends PhoneStateListener {
    Context context;
    long startRing;
    public static final int MIN_RING_TIME_MIL=4000;
    public static final int MAX_RING_TIME_MIL=180000;
    public static final double VALID_VOLUME=0.5;
    public PhoneStateMonitor(Context context) {
        super();
        // TODO Auto-generated constructor stub
        this.context=context;

    }

    //This Method Automatically called when changes is detected in Phone State
    public void onCallStateChanged(int state, String incomingNumber) {
        // TODO Auto-generated method stub

        super.onCallStateChanged(state, incomingNumber);

        switch(state)
        {
            case TelephonyManager.CALL_STATE_IDLE:    //Phone is in Idle State

                handleCall(false);
                break;
            case TelephonyManager.CALL_STATE_RINGING:  //Phone is Ringing

                AudioManager mAudioManager = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
                double volRing = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
                double volMax= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                if(volRing/volMax>=VALID_VOLUME){
                    startRing=System.currentTimeMillis();
                }else{
                    startRing=0;
                }
//                String theRing=" Id app ring : "+getAdsIdAccordingToCurrentRing();
//                Toast.makeText(context, "Phone State is RINGING  "+volRing+", "+theRing, Toast.LENGTH_LONG).show();

                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:  //Call is Received
                handleCall(true);


                break;
        }
    }
    public void updateAdsHistoryManagerUntilGettingCash(int idApp,long current){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this.context);
        String adsHistoryManagerUntilGettingCashString=sp.getString(ConfigAppData.ADS_HISTORY_MANAGER_UNTIL_GETTING_CASH,null);
        AdsHistoryManagerUntilGettingCash a;
        if(adsHistoryManagerUntilGettingCashString==null){
            a=new AdsHistoryManagerUntilGettingCash();
            a.update(idApp,current);

        }else{
            Gson gson=new Gson();
            a = gson.fromJson(adsHistoryManagerUntilGettingCashString, AdsHistoryManagerUntilGettingCash.class);
            if(a==null){
                a=new AdsHistoryManagerUntilGettingCash();
                a.update(idApp,current);
            }else{
                if(!a.update(idApp,current)){
                    return;
                }
            }

        }
        SharedPreferences.Editor edit = sp.edit();
        String str = new Gson().toJson(a);
        edit.putString(ConfigAppData.ADS_HISTORY_MANAGER_UNTIL_GETTING_CASH, str);
        edit.commit();
    }
    public void handleCall(boolean receive){
        long current=System.currentTimeMillis();
        long timeRing=current-startRing;

        if(MIN_RING_TIME_MIL<=timeRing&&MAX_RING_TIME_MIL>=timeRing){
//            if(!receive){
//                Toast.makeText(context, "Phone State is IDLE Time : "+timeRing, Toast.LENGTH_LONG).show();
//            }else{
//                Toast.makeText(context, "Call State is OFFHOOK : "+timeRing, Toast.LENGTH_LONG).show();
//            }
            int idApp=getAdsIdAccordingToCurrentRing(this.context);
            if(idApp>=0){
                updateEventAds(idApp);
                updateAdsHistoryManagerUntilGettingCash(idApp,current);
                SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
                int counterRing=sp.getInt(ConfigAppData.COUNTER_ALL_RING,0);
                int counterRingUnPaid=sp.getInt(ConfigAppData.COUNTER_ALL_RING_THAT_UNPAID,0);
                counterRing++;
                counterRingUnPaid++;
                SharedPreferences.Editor edit = sp.edit();
                edit.putInt(ConfigAppData.COUNTER_ALL_RING,counterRing);
                edit.putInt(ConfigAppData.COUNTER_ALL_RING_THAT_UNPAID,counterRingUnPaid);
                edit.commit();
                // COUNTER_ALL_RING
            }

        }else{
            Toast.makeText(context, "Phone State is IDLE Time : but to short"+timeRing, Toast.LENGTH_LONG).show();
        }
        startRing=0;
    }
    public static int getAdsIdAccordingToCurrentRing(Context context){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
        String adsSummaryManagerString = sp.getString(EventAds.Ads_summary_Manager, null);
        Gson gson = new Gson();
        AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
        Uri uriDefualt= RingtoneManager.getActualDefaultRingtoneUri(context,RingtoneManager.TYPE_RINGTONE);
        if(adsSummaryManager._arr==null){
            return -1;
        }else{
            for(int i=0;i<adsSummaryManager._arr.length;i++){
                if(adsSummaryManager._arr[i]._uri!=null){

                    if(uriDefualt.toString().equals(adsSummaryManager._arr[i]._uri.toString())){

                            return adsSummaryManager._arr[i]._id;
                    }
                }

            }
        }

        return -1;
    };
    public void updateEventAds(int id){
        LocationManager locationManager;
        locationManager = (LocationManager)context.getSystemService(context.LOCATION_SERVICE);
        Location l=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        double x=l.getLatitude();
        double y=l.getLongitude();
        String gps=x+","+y;
        DatabaseOperations db=new DatabaseOperations(context);
        db.updateAdsEvent(db,gps,id);

        // String str= new Gson().toJson(target);
    }

}