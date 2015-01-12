package com.ringchash.dodot.aviad.ringchash;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.io.File;

/**
 * Created by AVIAD on 1/6/2015.
 */
public class RingtoneTest extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ringtone_test);
        Button buttonRingtoneRemove = (Button) findViewById(R.id.removeRingtone);
        Button btnsaveDefaultRingtone = (Button) findViewById(R.id.saveDefultRingtone);
        Button btnprintRingtone = (Button) findViewById(R.id.printAllRingtoneData);
        Button ButtonLoadringtonedefault = (Button) findViewById(R.id.loadringtonedefault);

        Button btnupdateUserRingtone= (Button) findViewById(R.id.updateUserRingtone);
        Button btnupdateRingtone1= (Button) findViewById(R.id.updateRingtone1);
        Button updatefileButton= (Button) findViewById(R.id.fileUpdate2);
        Button regiserButton=(Button)findViewById(R.id.register1);
        regiserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserData();
            };
        });
        updatefileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateFile();

            };
        });

        btnupdateRingtone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setAdsRingtone();

            };
        });
        btnupdateUserRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setUserRingtone();

            };
        });

        ButtonLoadringtonedefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setUserRingtone();


            }

            ;
        });
        btnprintRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                printAllRingtone();

            }

            ;
        });
        btnsaveDefaultRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // saveTheDefaultRingtone();
                // setDefaultRingtone();

            }

            ;
        });
        buttonRingtoneRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                removeRingtone();

            }

            ;
        });


    }
    public void updateUserData(){

        EditProfile.saveDataOfUser(this,"Aviad Gispan",1,"aviadgispan@gmail.com",23,9,1984);
    }
    public void removeRingtone() {

    }
    public void updateFile(){
//        AlarmReceiver a=new AlarmReceiver(this);
//        a.updateFile();
    }

    public void saveTheDefaultRingtone() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Uri u = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
        String name = u.toString();
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(ConfigAppData.DEFAULT_RINGTONE_NAME, name);
        edit.commit();
        // "DEFAULT_RINGTONE_NAME
    }

    ;

    public void setUserRingtone() {


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        // IS_USER_RINGONE_PLAY

        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(ConfigAppData.IS_ADS_RINGONE_PLAY, false);

        edit.commit();
        String uri = sp.getString(ConfigAppData.DEFAULT_RINGTONE_NAME, null);
        if (uri != null) {
            RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, Uri.parse(uri));
        } else {

            // is removed or null...
        }
    }

    public AdsSummaryObject getAdsRingtone() {

        return null;
    }

    public void setAdsRingtone() {
        updateDefaultRingtoneValue();
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);
        // ads is not in shared preferences
        if (adsSummaryManagerString == null) {
            setUserRingtone();
            return;

        }
        AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
        adsSummaryManager._context = this;
        AdsSummaryObject target = adsSummaryManager.getRelevant();
        if (target == null || target._fileName == null || target._fileName.length() == 0) {
            setUserRingtone();
            return;
        }
        if (target._uri == null) {
            String urlNewRingtone=updateRingtoneByFileName(target);
            updateUriInAdsManager(target._fileName,urlNewRingtone );

        } else {
            Uri u=Uri.parse(target._uri);
            Ringtone r=RingtoneManager.getRingtone(this,u);
            if(r==null){
                String urlNewRingtone=updateRingtoneByFileName(target);
                updateUriInAdsManager(target._fileName,urlNewRingtone );
            }else{
                RingtoneManager.setActualDefaultRingtoneUri(
                        this, RingtoneManager.TYPE_RINGTONE,
                        u);
                updateInSPCurrentRingtone(target._uri);
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean(ConfigAppData.IS_ADS_RINGONE_PLAY, true);
                edit.commit();
            }
        }

    }
    public void updateInSPCurrentRingtone(String uri){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(ConfigAppData.CUTTENT_ADS_RINGTONE,uri);
        edit.commit();

    }
    public void updateDefaultRingtoneValue(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isInRingAdsStatus=sp.getBoolean(ConfigAppData.IS_ADS_RINGONE_PLAY,false);
        if(isInRingAdsStatus){

            Uri u=RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_RINGTONE);
            String currentAds=sp.getString(ConfigAppData.CUTTENT_ADS_RINGTONE,null);
            if(currentAds!=null&&u!=null){
                // ringtone was change
                if(!currentAds.equals(u.toString())){
                  if(!isUrlInAdsManager(u.toString())){
                      SharedPreferences.Editor edit = sp.edit();
                      edit.putString(ConfigAppData.DEFAULT_RINGTONE_NAME,u.toString());
                      edit.commit();
                  }
               }
            }
        }else{
            Uri u=RingtoneManager.getActualDefaultRingtoneUri(this,RingtoneManager.TYPE_RINGTONE);
            String cur=u.toString();
            String def=sp.getString(ConfigAppData.DEFAULT_RINGTONE_NAME,null);
            if(def==null){
                if(cur!=null){
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString(ConfigAppData.DEFAULT_RINGTONE_NAME,cur);
                    edit.commit();
                }
            }else{
                if(!def.equals(cur)){
                    if(!isUrlInAdsManager(cur)){
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString(ConfigAppData.DEFAULT_RINGTONE_NAME,cur);
                        edit.commit();
                    }
                }
            }
        }
    }
    public boolean isUrlInAdsManager(String url){
      return false;
    };
    public String updateRingtoneByFileName(AdsSummaryObject obj) {
        File f = getFileByName(obj._fileName);
        if (f == null) {

            return null;
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
            values.put(MediaStore.MediaColumns.TITLE, obj._fileName);

            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");
            values.put(MediaStore.Audio.Media.ARTIST, obj._companyName);

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
            this.getContentResolver().delete(
                    uri,
                    MediaStore.MediaColumns.DATA + "=\""
                            + f.getAbsolutePath() + "\"", null);
            Uri newUri = this.getContentResolver().insert(uri, values);

            RingtoneManager.setActualDefaultRingtoneUri(
                    this, RingtoneManager.TYPE_RINGTONE,
                    newUri);
            updateInSPCurrentRingtone(newUri.toString());
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean(ConfigAppData.IS_ADS_RINGONE_PLAY, true);
            edit.commit();
            return newUri.toString();

        }
    }

    public void updateUriInAdsManager(String fileName, String uri) {
        if (uri == null || fileName == null || fileName.length() == 0 || uri.length() == 0) {
            return;
        }
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);
        // ads is not in shared preferences
        if (adsSummaryManagerString != null) {
            AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
            adsSummaryManager._context = this;
            adsSummaryManager.updateUriWithFileName(fileName, uri);
            adsSummaryManager._context = null;
            SharedPreferences.Editor edit = sp.edit();
            String str = new Gson().toJson(adsSummaryManager);
            edit.putString(ConfigAppData.ADS_SUMMARY_MANEGER, str);
            edit.commit();


        }
    }

    public static File getFileByName(String filename) {
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "RingCashFolder");
        for (File f : yourDir.listFiles()) {
            if (f.isFile())


            {
                if (filename.equals(f.getName())) {
                    return f;
                }


            }

        }


        return null;
    }

    ;

    //
//    public void setDefaultRingtone(){
//        Log.d("Path ringtone","bla vbla");
//        Uri u=RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
//        String s=u.toString();
//
//        Log.d("Path ringtone",s);
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        String name=sp.getString(ConfigAppData.DEFAULT_RINGTONE_NAME,null);
//        if(name!=null){
//
//
//        }
//    }
    public void printAllRingtone() {
        RingtoneManager r = new RingtoneManager(this);
        Cursor c = r.getCursor();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String name = c.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            String uri = c.getString(RingtoneManager.URI_COLUMN_INDEX);
            Log.d("Ringtone " + " : = ", name + " , " + uri);
            c.moveToNext();
        }

    }

    ;
}
