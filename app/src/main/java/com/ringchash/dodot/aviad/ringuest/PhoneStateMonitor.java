package com.ringchash.dodot.aviad.ringuest;

/**
 * Created by AVIAD on 12/28/2014.
 */
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;

/**
 * the class that listen to the ring
 */
public class PhoneStateMonitor extends PhoneStateListener {
    Context context;
    long startRing;
    // min time of ads that need to be for counting
    public static final int MIN_RING_TIME_MIL=4000;
    // max time of ads that need to be for counting
    public static final int MAX_RING_TIME_MIL=180000;
    // 0-1 the volume of the device 0 is couner when is not volume
    // 1 counting only whe the device in max volume
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
                int takbulRing=sp.getInt(ConfigAppData.TAKBUL_ALL_RING,0);
                int takbulRingUnPaid=sp.getInt(ConfigAppData.TAKBUL_ALL_RING_THAT_UNPAID,0);
                int takbule= getTakbulForRing(this.context,idApp);
                takbulRing=takbulRing+takbule;
                takbulRingUnPaid=takbulRingUnPaid+takbule;
                counterRing++;
                counterRingUnPaid++;
                SharedPreferences.Editor edit = sp.edit();
                edit.putInt(ConfigAppData.COUNTER_ALL_RING,counterRing);
                edit.putInt(ConfigAppData.COUNTER_ALL_RING_THAT_UNPAID,counterRingUnPaid);
                edit.putInt(ConfigAppData.TAKBUL_ALL_RING_THAT_UNPAID,takbulRingUnPaid);
                edit.putInt(ConfigAppData.TAKBUL_ALL_RING,takbulRing);
                edit.commit();
                // COUNTER_ALL_RING
                setAdsRingtone();

            }

        }else{
         //   Toast.makeText(context, "Phone State is IDLE Time : but to short"+timeRing, Toast.LENGTH_LONG).show();
        }
        startRing=0;
    }
    public static int getTakbulForRing(Context context,int id){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
        String adsSummaryManagerString = sp.getString(EventAds.Ads_summary_Manager, null);
        if(adsSummaryManagerString==null){
            return (int)ConfigAppData.PAY_FOR_RING*100;
        }
        Gson gson = new Gson();
        AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
        if(adsSummaryManager==null||adsSummaryManager._arr==null){
            return (int)(ConfigAppData.PAY_FOR_RING*100);
        }else{
            for(int i=0;i<adsSummaryManager._arr.length;i++){
                if(adsSummaryManager._arr[i]._id==id){
                    return adsSummaryManager._arr[i]._pricePerRing;
                }
            }
            return (int)(ConfigAppData.PAY_FOR_RING*100);
        }
    }
    public static int getAdsIdAccordingToCurrentRing(Context context){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
        String adsSummaryManagerString = sp.getString(EventAds.Ads_summary_Manager, null);
        if(adsSummaryManagerString==null){
            return -1;
        }
        Gson gson = new Gson();
        AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
        Uri uriDefualt= RingtoneManager.getActualDefaultRingtoneUri(context,RingtoneManager.TYPE_RINGTONE);
        if(adsSummaryManager==null||adsSummaryManager._arr==null){
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
    public void setAdsRingtone() {
        updateDefaultRingtoneValue();
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);
        // ads is not in shared preferences
        if (adsSummaryManagerString == null) {
            setUserRingtone();
            return;

        }
        AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
        adsSummaryManager._context = context;
        AdsSummaryObject target = adsSummaryManager.getRelevant();
        if (target == null || target._fileName == null || target._fileName.length() == 0) {
            setUserRingtone();
            return;
        }
        if (target._uri == null) {
            String urlNewRingtone = updateRingtoneByFileName(target);
            updateUriInAdsManager(target._fileName, urlNewRingtone);

        } else {
            Uri u = Uri.parse(target._uri);
            Ringtone r = RingtoneManager.getRingtone(context, u);
            if (r == null) {
                String urlNewRingtone = updateRingtoneByFileName(target);
                updateUriInAdsManager(target._fileName, urlNewRingtone);
            } else {
                RingtoneManager.setActualDefaultRingtoneUri(
                        context, RingtoneManager.TYPE_RINGTONE,
                        u);
                updateInSPCurrentRingtone(target._uri);
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean(ConfigAppData.IS_ADS_RINGONE_PLAY, true);
                edit.commit();
            }
        }

    }
    public void setUserRingtone() {


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        // IS_USER_RINGTONE_PLAY

        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(ConfigAppData.IS_ADS_RINGONE_PLAY, false);

        edit.commit();
        String uri = sp.getString(ConfigAppData.DEFAULT_RINGTONE_NAME, null);
        if (uri != null) {
            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, Uri.parse(uri));
        } else {

            // is removed or null...
        }
    }
    public String updateRingtoneByFileName(AdsSummaryObject obj) {
        File f = AlarmReceiver.getFileByName(obj._fileName);
        if (f == null) {

            return null;
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
            values.put(MediaStore.MediaColumns.TITLE, obj._fileName);

            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");
            values.put(MediaStore.Audio.Media.ARTIST, obj._companyName);

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
            context.getContentResolver().delete(
                    uri,
                    MediaStore.MediaColumns.DATA + "=\""
                            + f.getAbsolutePath() + "\"", null);
            Uri newUri = context.getContentResolver().insert(uri, values);

            RingtoneManager.setActualDefaultRingtoneUri(
                    context, RingtoneManager.TYPE_RINGTONE,
                    newUri);
            updateInSPCurrentRingtone(newUri.toString());
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean(ConfigAppData.IS_ADS_RINGONE_PLAY, true);
            edit.commit();
            return newUri.toString();

        }
    }
    public void updateInSPCurrentRingtone(String uri){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(ConfigAppData.CUTTENT_ADS_RINGTONE,uri);
        edit.commit();

    }
    public void updateDefaultRingtoneValue() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isInRingAdsStatus = sp.getBoolean(ConfigAppData.IS_ADS_RINGONE_PLAY, false);
        if (isInRingAdsStatus) {

            Uri u = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
            String currentAds = sp.getString(ConfigAppData.CUTTENT_ADS_RINGTONE, null);
            if (currentAds != null && u != null) {
                // ringtone was change
                if (!currentAds.equals(u.toString())) {
                    if (!isUrlInAdsManager(u.toString())) {
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString(ConfigAppData.DEFAULT_RINGTONE_NAME, u.toString());
                        edit.commit();
                    }
                }
            }
        } else {
            Uri u = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
            String cur = u.toString();
            String def = sp.getString(ConfigAppData.DEFAULT_RINGTONE_NAME, null);
            if (def == null) {
                if (cur != null) {
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString(ConfigAppData.DEFAULT_RINGTONE_NAME, cur);
                    edit.commit();
                }
            } else {
                if (!def.equals(cur)) {
                    if (!isUrlInAdsManager(cur)) {
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString(ConfigAppData.DEFAULT_RINGTONE_NAME, cur);
                        edit.commit();
                    }
                }
            }
        }
    }
    public boolean isUrlInAdsManager(String url){
        return false;
    };
    public void updateUriInAdsManager(String fileName, String uri) {
        if (uri == null || fileName == null || fileName.length() == 0 || uri.length() == 0) {
            return;
        }
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);
        // ads is not in shared preferences
        if (adsSummaryManagerString != null) {
            AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
            adsSummaryManager._context = context;
            adsSummaryManager.updateUriWithFileName(fileName, uri);
            adsSummaryManager._context = null;
            SharedPreferences.Editor edit = sp.edit();
            String str = new Gson().toJson(adsSummaryManager);
            edit.putString(ConfigAppData.ADS_SUMMARY_MANEGER, str);
            edit.commit();


        }
    }
}