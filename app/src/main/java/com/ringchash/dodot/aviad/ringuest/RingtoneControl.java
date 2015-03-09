package com.ringchash.dodot.aviad.ringuest;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;


import com.google.gson.Gson;

import java.io.File;

/**
 * Created by AVIAD on 12/24/2014.
 */
public class RingtoneControl {
    Context _context;
    public static final String DEFAULT_RINGTONE = "DEFAULT_RINGTONE";
    public static final String DEFAULT_RINGTONE_URL = "https://s3-eu-west-1.amazonaws.com/matos/default_ring_cash.ogg";

    public RingtoneControl(Context context) {
        _context = context;
    }


    public void getReaDefaultRingtone() {
        updateRingtone();
    //    return null;
    };

    /**
     * get the ask file
     * @param filename the name of file
     * @return the ask file
     */
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
    };

    /**
     * update if ure exist
     * @param uri the asked uri
     * @return true if seceded
     */
    public boolean updateWithUri(Uri uri) {
        if (uri == null) {
            return false;
        }
        if (RingtoneManager.getActualDefaultRingtoneUri(_context, RingtoneManager.TYPE_RINGTONE).equals(uri)) {
            return true;
        } else {
            Ringtone ringtone = RingtoneManager.getRingtone(_context, uri);
            if (ringtone == null) {
                return false;
            }

            RingtoneManager.setActualDefaultRingtoneUri(
                    _context,
                    RingtoneManager.TYPE_RINGTONE,
                    uri
            );
            return true;
        }

    }

    /**
     * can update ringtone with file
     * @param fileName the ask file name
     * @return true if succeed
     */
    public boolean canUpdateWithFile(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return false;
        } else {
            File f = getFileByName(fileName);
            if (f != null) {
                return true;
            }
        }
        return false;
    } ;

    public void updateRingtone() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
        String adsSummaryManagerString = sp.getString(EventAds.Ads_summary_Manager, null);
        if (adsSummaryManagerString != null) {
            Gson gson = new Gson();
            AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
            adsSummaryManager._context = this._context;

            LocationManager locationManager;
            locationManager = (LocationManager)_context.getSystemService(_context.LOCATION_SERVICE);

            Location l;
            LocationListener ll = new LocationListner();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
            if(locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  ll);
            }
            if(locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER)){
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,  ll);
            }

            if(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!=null){
                l=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            }else{

                if(locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER)&&locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!=null){
                    l=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }else{
                    l=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if(l==null){
                        l=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                    if(l==null){
                        l=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    }

                }
            }


            double lat1=l.getLatitude();
            double lon1=l.getLongitude();
            AdsSummaryObject[] allRelevant = adsSummaryManager.getAllRelevant(lat1, lon1);
            boolean updateRingtone = false;
            if (allRelevant != null && allRelevant.length > 0) {
                for (int i = 0; i < allRelevant.length && !updateRingtone; i++) {
                    if (allRelevant[i] != null) {
                        boolean isUpdateWithUri;
                        if (allRelevant[i]._uri == null || allRelevant[i]._uri.length() == 0) {
                            isUpdateWithUri = false;
                        } else {
                            isUpdateWithUri = updateWithUri(Uri.parse(allRelevant[i]._uri));
                        }

                        if (isUpdateWithUri) {
                            updateRingtone = true;

                        } else {
                            if (canUpdateWithFile(allRelevant[i]._fileName)) {
                                File f = getFileByName(allRelevant[i]._fileName);
                                if (f != null) {
                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
                                    values.put(MediaStore.MediaColumns.TITLE, allRelevant[i]._fileName);

                                    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");
                                    values.put(MediaStore.Audio.Media.ARTIST, allRelevant[i]._companyName);
                                    // update ringtone ... do all uri in manager share preference...
//                                    Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
//                                    Uri newUri = _context.getContentResolver().insert(uri, values);
//                                    RingtoneManager.setActualDefaultRingtoneUri(
//                                            _context,
//                                            RingtoneManager.TYPE_RINGTONE,
//                                            newUri
//                                    );


                                    Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
                                    _context.getContentResolver().delete(
                                            uri,
                                            MediaStore.MediaColumns.DATA + "=\""
                                                    + f.getAbsolutePath() + "\"", null);
                                    Uri newUri = _context.getContentResolver().insert(uri, values);

                                    RingtoneManager.setActualDefaultRingtoneUri(
                                            _context, RingtoneManager.TYPE_RINGTONE,
                                            newUri);


                                    if (adsSummaryManager._arr != null) {
                                        adsSummaryManager._context = null;

                                        int appId = allRelevant[i]._id;
                                        for (int k = 0; k < adsSummaryManager._arr.length; k++) {
                                            if (adsSummaryManager._arr[k]._id == appId) {
                                                adsSummaryManager._arr[k]._uri = newUri.toString();
                                                k = adsSummaryManager._arr.length;
                                            }
                                        }
                                        SharedPreferences.Editor edit = sp.edit();
                                        String str = new Gson().toJson(adsSummaryManager);
                                        edit.putString(EventAds.Ads_summary_Manager, str);
                                        edit.commit();
                                        adsSummaryManager._context = this._context;
                                    }


                                    updateRingtone = true;
                                }

                            }
                        }
                    }


                }
                //return allRelevant[0];
            } else {
                int adsRingId = PhoneStateMonitor.getAdsIdAccordingToCurrentRing(this._context);

                if (adsRingId >= 0) {

                    String uriDefault = sp.getString(DEFAULT_RINGTONE, null);
                    if (uriDefault != null) {
                        if (updateWithUri(Uri.parse(uriDefault))) {
                        } else {
                            updateRingtoneDefaultIfNotExist();
                        }
                    } else {
                        updateRingtoneDefaultIfNotExist();
                    }


                } else {
                    String uriDefault = sp.getString(DEFAULT_RINGTONE, null);

                    // if uri null then DEFAULT_RINGTONE is not update to the default,
                    Uri u = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

                    if (uriDefault == null) {

                        if (u != null) {
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString(DEFAULT_RINGTONE, u.toString());
                            edit.commit();
                        }
                    } else {

                    }
                }
                // return to defult
            }


        }

    }

    public void updateRingtoneDefaultIfNotExist() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
        File k = new File("/RingCashFolder/default_ring_cash", "default_ring_cash.ogg");
        if (k == null) {
            boolean onlyWifi = false;
            boolean userCanSeeDownload = false;
            Start.downloadFileStaticVersion(_context, DEFAULT_RINGTONE_URL, "default_ring_cash", onlyWifi, userCanSeeDownload, "", "");

        }
        k = new File("/RingCashFolder/default_ring_cash", "default_ring_cash.ogg");
        if (k != null) {
            Uri u = Uri.fromFile(k);
            if (updateWithUri(u)) {
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(DEFAULT_RINGTONE, u.toString());
                edit.commit();
            }
        }
    }

    private class LocationListner implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("LOCATION CHANGED", location.getLatitude() + "");
                Log.d("LOCATION CHANGED", location.getLongitude() + "");

            }
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
