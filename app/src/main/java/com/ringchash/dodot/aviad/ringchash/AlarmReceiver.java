package com.ringchash.dodot.aviad.ringchash;

/**
 * Created by AVIAD on 12/23/2014.
 */

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    Context _context;
//    public  AlarmReceiver(){
//
//    }
//    public  AlarmReceiver(Context c){
//        _context=c;
//    }
    @Override
    public void onReceive(Context context, Intent intent) {
        _context = context;
        // For our recurring task, we'll just display a message
        Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();
        switch (getStatus(context)) {
            case ConfigAppData.UPDATE_FILE:
                updateFile(context);
                Toast.makeText(context, "I'm running (update file)", Toast.LENGTH_SHORT).show();
                break;
            // from user...
            case ConfigAppData.UPDATE_SERVER_DATA:
                updateServerDataHistory(context);
                Toast.makeText(context, "I'm running (update Server data)", Toast.LENGTH_SHORT).show();
                break;
            case ConfigAppData.UPDATE_RINGTONE:
                setAdsRingtone();
                Toast.makeText(context, "I'm running (update ringtone)", Toast.LENGTH_SHORT).show();
                break;
        }

    }
    public void updateFile() {
        updateFile(_context);
    }
    public void updateFile(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong(ConfigAppData.LAST_UPDATE_FILE, System.currentTimeMillis());
        edit.commit();
        getAdsAndUpdateFileFromServer(context);


    }

    public int getStatus(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long lastUpdateFile = sp.getLong(ConfigAppData.LAST_UPDATE_FILE, 0);
        long currentTime = System.currentTimeMillis();
        if (lastUpdateFile + ConfigAppData.UPDATE_FILE_INTERVAL <= currentTime) {
            return ConfigAppData.UPDATE_FILE;
        } else {
            long lastUpdateServerData = sp.getLong(ConfigAppData.LAST_UPDATE_SERVER_DATA_FROM_USER, 0);
            if (lastUpdateServerData + ConfigAppData.UPDATE_SERVER_DATA_INTERVAL <= currentTime) {
                return ConfigAppData.UPDATE_SERVER_DATA;
            }
        }
        return ConfigAppData.UPDATE_RINGTONE;
    }

    public void updateRingtone(Context context) {
//        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(context);
//        String adsSummaryManagerString=sp.getString(EventAds.Ads_summary_Manager,null);
//        if(adsSummaryManagerString!=null){
//            Gson gson = new Gson();
//            AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
//
//
//            LocationManager locationManager;
//            locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
//            Location l=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
//            double lat1=l.getLatitude();
//            double lon1=l.getLongitude();
//            AdsSummaryObject[] allRelevant=adsSummaryManager.getAllRelevant(lat1, lon1);
//
//
//
//        }

    }

    public void getAdsAndUpdateFileFromServer(Context context) {

        /// get the data of user age gender & gps for the server.
        int year;
        int gender;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        //gender 0 if female , 1 if male if unknown 0
        gender = sp.getInt(ConfigAppData.USER_GENDER, -1);
        year = sp.getInt(ConfigAppData.USER_BIRTH_YEAR, -1);
        Gson gson = new Gson();

        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);
        String allMyAdsId = "";
        if (adsSummaryManagerString != null) {
            AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
            if (adsSummaryManager._arr != null) {
                for (int i = 0; i < adsSummaryManager._arr.length; i++) {
                    if (i > 0) {
                        allMyAdsId = allMyAdsId + "&" + adsSummaryManager._arr[i]._id;
                    } else {
                        allMyAdsId = allMyAdsId + adsSummaryManager._arr[i]._id;
                    }

                }
            }
        }


        Calendar c = Calendar.getInstance(Locale.getDefault());
        // user age
        int age = c.get(Calendar.YEAR) - year;
        // get his gps point
        LocationManager locationManager;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location l = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        double lat = l.getLatitude();
        double lon = l.getLongitude();
        /// start sending to the server
        getAdsDataFromServerAccordingToUserData(age, gender, lat, lon, allMyAdsId);

    }

    private static String[] getAllDownloadListInProgress(Context context) {
        //ADS_LIST_IN_DOWNLOAD_PROGRESS
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String downloadDataString = sp.getString(ConfigAppData.ADS_LIST_IN_DOWNLOAD_PROGRESS, null);

        Gson gson = new Gson();


        if (downloadDataString != null) {
            DownLoadData downloadData = gson.fromJson(downloadDataString, DownLoadData.class);
            return downloadData._downloadList;
        }
        return null;
    }

    private static String[] getAllFileNameNotToDownLoad(Context context) {
        File[] fileFromRingCash = getAllFileNameInRingCash();
        if (fileFromRingCash == null || fileFromRingCash.length == 0) {
            return getAllDownloadListInProgress(context);
        } else {
            String[] inProgress = getAllDownloadListInProgress(context);
            int size = 0;
            if (inProgress != null) {
                size = inProgress.length;
            }
            String[] fileToDownload = new String[fileFromRingCash.length + size];
            for (int i = 0; i < fileFromRingCash.length; i++) {
                fileToDownload[i] = fileFromRingCash[i].getName();
            }

            for (int i = 0; i < size; i++) {
                fileToDownload[i + fileFromRingCash.length] = inProgress[i];
            }
            return removeAllDuplicate(fileToDownload);

        }

    }

    ;

    private void getAdsDataFromServerAccordingToUserData(int age, int gender, double lat, double lon, String idList) {
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(ConfigAppData.UPDATE_ADS_DATA_FROM_SERVER);
        final List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(5);
        nameValuePair.add(new BasicNameValuePair("userAge", age + ""));
        nameValuePair.add(new BasicNameValuePair("userGender", gender + ""));
        nameValuePair.add(new BasicNameValuePair("userLat", lat + ""));
        nameValuePair.add(new BasicNameValuePair("userLon", lon + ""));
        nameValuePair.add(new BasicNameValuePair("userIdList", idList));
        //Encoding POST data
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    // write response to log
                    //	String str=response.getEntity().toString();
                    HttpEntity entity = response.getEntity();
                    //str=EntityUtils.getContentMimeType(entity);
                    String inputLine;
                    String str = "";
                    BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                    try {
                        while ((inputLine = in.readLine()) != null) {
                            str = str + inputLine;
                        }
                        in.close();
                        updateDataFromServer(_context, str);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } catch (ClientProtocolException e) {
                    // Log exception
                    e.printStackTrace();
                } catch (IOException e) {
                    // Log exception
                    e.printStackTrace();
                }


            }
        });
        t.start();

    }

    private void updateDataFromServer(Context context, String data) {
        if (data == null || data.length() == 0) {
            return;
        }
        Gson gson = new Gson();
        DataUpdateFromServer dataUpdateFromServer = gson.fromJson(data, DataUpdateFromServer.class);
        if (dataUpdateFromServer == null) {
            return;
        }
        if (dataUpdateFromServer.updateAds != null && dataUpdateFromServer.updateAds.length > 0) {
            updateAddFromString(dataUpdateFromServer.updateAds);
        }
        if (dataUpdateFromServer.toRemove != null && dataUpdateFromServer.toRemove.length > 0) {
            removeAllThatNotRelevant(dataUpdateFromServer.toRemove);
        }
        removeOldFile();
        DownloadAction(_context);
    }

    private static String[] getAllFileNameToDownload(Context context) {
        String[] allFileNameNotToDownLoad = getAllFileNameNotToDownLoad(context);

        String[] fromAdsManager = getAllFileNameInAdsManager(context);
        if (fromAdsManager == null || fromAdsManager.length == 0) {
            return null;
        }
        if (allFileNameNotToDownLoad == null || allFileNameNotToDownLoad.length == 0) {
            return fromAdsManager;
        } else {
            int counter = 0;
            for (int i = 0; i < fromAdsManager.length; i++) {
                if (!isStringInArrString(fromAdsManager[i], allFileNameNotToDownLoad)) {
                    counter++;
                }
            }
            String[] fileNameToDownload = new String[counter];
            counter = 0;
            for (int i = 0; i < fromAdsManager.length; i++) {
                if (!isStringInArrString(fromAdsManager[i], allFileNameNotToDownLoad)) {
                    fileNameToDownload[counter] = fromAdsManager[i];
                    counter++;
                }
            }
            return fileNameToDownload;
        }


    }

    ;

    private static boolean isStringInArrString(String string, String[] stringArr) {
        if (stringArr == null || string == null) {
            return false;
        } else {
            for (int i = 0; i < stringArr.length; i++) {
                if (string.equals(stringArr[i])) {
                    return true;
                }
            }
            return false;
        }
    }

    ;

    private static void DownloadAction(Context context) {
        String[] allToDownLoad = getAllFileNameToDownload(context);
        if (allToDownLoad == null || allToDownLoad.length == 0) {
            return;
        }
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);
        if (adsSummaryManagerString != null) {
            AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
            if (adsSummaryManager != null) {
                adsSummaryManager._context = context;
                AdsSummaryObject[] adsSummaryArr = adsSummaryManager.getAdsSummaryObjectArrAccordingToFileName(allToDownLoad);
                if (adsSummaryArr != null && adsSummaryArr.length > 0) {
                    if (isDownloadManagerAvailable(context)) {
                        for (int i = 0; i < adsSummaryArr.length; i++) {
                            if (adsSummaryArr[i] != null) {
                                downloadFileStaticVersion(context, adsSummaryArr[i]._pathToSound, adsSummaryArr[i]._fileName, adsSummaryArr[i]._onlyWifi, adsSummaryArr[i]._userCanSeeDownload, adsSummaryArr[i]._titleDownload, adsSummaryArr[i]._descriptionDownLoad);

                            }

                        }
                    }
                }
            }
        }


    }

    ;

    public static void downloadFileStaticVersion(Context context, String url, String fileName, boolean onlyWifi, boolean userCanSeeDownload, String title, String description) {
        if (url == null || url.length() == 0 || fileName == null || fileName.length() == 0) {
            return;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);
        request.setDescription(description);
        if (onlyWifi) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        }
        if (userCanSeeDownload) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        ///Environment.DIRECTORY_DOWNLOADS
        request.setDestinationInExternalPublicDir("/RingCashFolder", fileName);
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(context, "External SD card not mounted", Toast.LENGTH_LONG).show();
        }
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        updateListInDownLoad(context, fileName);
        manager.enqueue(request);
        // DownloadManager manger=(DownloadManager) getActivity().


    }

    private static void updateListInDownLoad(Context context, String fileName) {
        //ADS_LIST_IN_DOWNLOAD_PROGRESS
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String downloadDataString = sp.getString(ConfigAppData.ADS_LIST_IN_DOWNLOAD_PROGRESS, null);

        Gson gson = new Gson();

        DownLoadData downloadData;
        if (downloadDataString != null) {
            downloadData = gson.fromJson(downloadDataString, DownLoadData.class);
            String[] fileNameArr = downloadData._downloadList;
            for (int i = 0; i < fileNameArr.length; i++) {
                if (fileNameArr[i].equals(fileName)) {
                    return;
                }

            }
            String[] arr = new String[fileNameArr.length + 1];
            for (int i = 0; i < fileNameArr.length; i++) {
                arr[i] = fileNameArr[i];
            }
            arr[arr.length - 1] = fileName;
            downloadData._downloadList = arr;
        } else {
            downloadData = new DownLoadData();
            downloadData._downloadList = new String[1];
            downloadData._downloadList[0] = fileName;

        }
        String str = new Gson().toJson(downloadData);
        Log.d("Gson : ", str);

        SharedPreferences.Editor edit = sp.edit();
        edit.putString(ConfigAppData.ADS_LIST_IN_DOWNLOAD_PROGRESS, str);
        edit.commit();

    }

    public static boolean isDownloadManagerAvailable(Context context) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void removeOldFile() {
        File[] arr = getAllFileNameToRemove(_context);
        if (arr == null || arr.length == 0) {
            Log.d("FILR m : ", "no old file founded");
            return;
        }
        for (int i = 0; i < arr.length; i++) {
            String name = arr[i].getName();
            Log.d("FILE  " + i + " : ", arr[i].getName());

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(arr[i].getAbsolutePath());
            _context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + arr[i].getAbsolutePath() + "\"", null);


            boolean isS = arr[i].delete();
        }
    }

    private static File[] getAllFileNameToRemove(Context context) {
        File[] fileInSdCard = getAllFileNameInRingCash();
        String[] fileNameInAddManager = getAllFileNameInAdsManager(context);
        int counter = 0;
        for (int i = 0; i < fileInSdCard.length; i++) {
            if (!isStringInArrString(fileInSdCard[i].getName(), fileNameInAddManager)) {
                counter++;
            }
        }
        File[] fileToRemove = new File[counter];
        if (counter > 0) {
            counter = 0;
            for (int i = 0; i < fileInSdCard.length; i++) {
                if (!isStringInArrString(fileInSdCard[i].getName(), fileNameInAddManager)) {
                    fileToRemove[counter] = fileInSdCard[i];
                    counter++;
                }
            }
        }

        return fileToRemove;
    }

    ;

    protected static File[] getAllFileNameInRingCash() {
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "RingCashFolder");
        int counter = 0;
        for (File f : yourDir.listFiles()) {
            if (f.isFile()) {
                counter++;
            }

        }
        File[] arr = new File[counter];
        counter = 0;
        for (File f : yourDir.listFiles()) {
            if (f.isFile()) {
                arr[counter] = f;
                counter++;
            }

        }
        return arr;
    }

    ;

    private static String[] getAllFileNameInAdsManager(Context context) {
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);

        AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
        if (adsSummaryManager == null) {
            return null;
        }
        adsSummaryManager._context = context;
        String[] arr = new String[adsSummaryManager._arr.length];
        for (int i = 0; i < adsSummaryManager._arr.length; i++) {
            arr[i] = adsSummaryManager._arr[i]._fileName;
        }

        return removeAllDuplicate(arr);
    }

    ;

    public static String[] removeAllDuplicate(String[] arr) {
        if (arr == null) {
            return null;
        }
        String[] next = removeOneDuplicate(arr);

        while (next.length < arr.length) {
            arr = next;
            next = removeOneDuplicate(arr);
        }
        return next;
    }

    private static String[] removeOneDuplicate(String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            int indexDup = getDuplicateIndexOf(arr, i);
            if (indexDup != -1) {
                String[] sol = new String[arr.length - 1];
                int counter = 0;
                for (int j = 0; j < arr.length; j++) {
                    if (indexDup != j) {
                        sol[counter] = arr[j];
                        counter++;
                    }
                }
                return sol;
            }
        }
        return arr;
    }

    private static int getDuplicateIndexOf(String[] arr, int index) {
        if (arr == null) {
            return -1;
        }
        for (int i = 0; i < arr.length; i++) {
            if (i != index) {
                if (arr[i].equals(arr[index])) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void updateAddFromString(String[] adsArr) {
        if (adsArr == null || adsArr.length == 0) {
            return;
        } else {
            Gson gson = new Gson();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
            String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);
            // ads is not in shared preferences
            if (adsSummaryManagerString == null) {
                AdsSummaryManager adsSummaryManager = new AdsSummaryManager(adsArr, _context);
                adsSummaryManager._context = null;
                // save to shared preferences
                String str = new Gson().toJson(adsSummaryManager);
                Log.d("Gson : ", str);

                SharedPreferences.Editor edit = sp.edit();
                edit.putString(ConfigAppData.ADS_SUMMARY_MANEGER, str);
                edit.commit();
            } else {
                // ads is in shared preferences
                AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
                adsSummaryManager._context = _context;
                adsSummaryManager.addAll(adsArr);
                adsSummaryManager._context = null;
                // save to shared preferences
                String str = new Gson().toJson(adsSummaryManager);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(ConfigAppData.ADS_SUMMARY_MANEGER, str);
                edit.commit();
            }
        }

    }

    private void removeAllThatNotRelevant(String[] arrToRemoveString) {
        if (arrToRemoveString == null || arrToRemoveString.length == 0) {
            return;
        }
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);

        AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
        adsSummaryManager._context = _context;
        int counter = 0;
        for (int i = 0; i < arrToRemoveString.length; i++) {
            if (AdsSummaryObject.isNumberDouble(arrToRemoveString[i])) {
                counter++;
            }
        }
        int[] idArr = new int[counter];
        counter = 0;
        for (int i = 0; i < arrToRemoveString.length; i++) {
            if (AdsSummaryObject.isNumberDouble(arrToRemoveString[i])) {
                idArr[counter] = Integer.parseInt(arrToRemoveString[i]);
                counter++;
            }
        }
        adsSummaryManager.removeAll(idArr);
        adsSummaryManager._context = null;
        String str = new Gson().toJson(adsSummaryManager);
        Log.d("Gson : ", str);

        SharedPreferences.Editor edit = sp.edit();
        edit.putString(ConfigAppData.ADS_SUMMARY_MANEGER, str);
        edit.commit();
    }

    /////////////// UPDATE EVENT HISTORY IN SERVER
    public void updateServerDataHistory(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong(ConfigAppData.LAST_UPDATE_SERVER_DATA_FROM_USER, System.currentTimeMillis());
        edit.commit();
        long user_id = getUserId();
        getDataFromServerAfterTime(user_id);
    }

    private long getUserId() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
        return sp.getLong(ConfigAppData.USER_ID, -1);
    }

    ;
// fddsa
    public void getDataFromServerAfterTime(long userId) {
        DatabaseOperations db = new DatabaseOperations(_context);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
        long maxTime = -1;

        long lastUpdate = sp.getLong(ConfigAppData.LAST_UPDATE, 0);
        Log.d("LAST UPDATE: ", "" + lastUpdate);
        ArrayList<EventSQlite> arr = db.getAllAfterTime(db, lastUpdate);
        if (arr == null || arr.size() == 0) {
            Log.d("SP :=> ", "no new was found form: " + lastUpdate);
            return;
        }
        Log.d("SP :=>SIZE OF Arr ", "Size " + arr.size());
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i)._adsTime > maxTime) {
                maxTime = arr.get(i)._adsTime;
            }
        }
        Collections.sort(arr, EventSQlite.EventSQliteComparator);
        int sizeOfStringArr = 1;
        if (arr.size() > 1) {
            for (int i = 1; i < arr.size(); i++) {
                if (arr.get(i)._adsID != arr.get(i - 1)._adsID) {
                    sizeOfStringArr++;
                }
            }
        }
        String[][] updateArr = new String[sizeOfStringArr][2];
        int[] idArr = new int[sizeOfStringArr];
        idArr[0] = arr.get(0)._adsID;
        if (arr.size() > 1) {
            int counter = 1;
            for (int i = 1; i < arr.size(); i++) {
                if (arr.get(i)._adsID != arr.get(i - 1)._adsID) {
                    idArr[counter] = arr.get(i)._adsID;
                    counter++;
                }
            }
        }
        int counterInnerDataPerApp = 0;
        for (int k = 0; k < idArr.length; k++) {
            updateArr[k][0] = idArr[k] + "";
            updateArr[k][1] = "";
            counterInnerDataPerApp = 0;
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i)._adsID == idArr[k]) {
                    String regex = "#";
                    if (counterInnerDataPerApp == 0) {
                        regex = "";
                    }
                    updateArr[k][1] = updateArr[k][1] + regex + arr.get(i)._adsTime + "&" + arr.get(i)._gps;
                    counterInnerDataPerApp++;
                }
            }
        }
        String dataToServer = "";
        for (int i = 0; i < idArr.length; i++) {
            if (i > 0) {
                dataToServer = "@" + dataToServer;
            }
            dataToServer = dataToServer + updateArr[i][0] + "$" + updateArr[i][1];
        }


        saveAllEventToServer(userId, dataToServer);
        // if there was a call to update( save to shared preference)
        // the time of the last update
        if (maxTime >= 0) {

            SharedPreferences.Editor edit = sp.edit();
            edit.putLong(ConfigAppData.LAST_UPDATE, maxTime);
            edit.commit();
            Log.d("new UPDATE: ", "" + maxTime);

        }
    }

    public void saveAllEventToServer(long userId, String data) {
        // TODO Auto-generated method stub


        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(ConfigAppData.UPDATE_ADS_DATA_HISTORY_FROM_USER);
        final List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("userID", userId + ""));
        nameValuePair.add(new BasicNameValuePair("data", data));
        //Encoding POST data
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    // write response to log
                    //	String str=response.getEntity().toString();
                    HttpEntity entity = response.getEntity();
                    //str=EntityUtils.getContentMimeType(entity);
                    String inputLine;
                    String str = "";
                    BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                    try {
                        while ((inputLine = in.readLine()) != null) {
                            str = str + inputLine;
                        }
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } catch (ClientProtocolException e) {
                    // Log exception
                    e.printStackTrace();
                } catch (IOException e) {
                    // Log exception
                    e.printStackTrace();
                }


            }
        });
        t.start();
    }

    //// update ringtone....
    public void updateDefaultRingtoneValue() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
        boolean isInRingAdsStatus = sp.getBoolean(ConfigAppData.IS_ADS_RINGONE_PLAY, false);
        if (isInRingAdsStatus) {

            Uri u = RingtoneManager.getActualDefaultRingtoneUri(_context, RingtoneManager.TYPE_RINGTONE);
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
            Uri u = RingtoneManager.getActualDefaultRingtoneUri(_context, RingtoneManager.TYPE_RINGTONE);
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

    public void setAdsRingtone() {
        updateDefaultRingtoneValue();
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);
        // ads is not in shared preferences
        if (adsSummaryManagerString == null) {
            setUserRingtone();
            return;

        }
        AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
        adsSummaryManager._context = _context;
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
            Ringtone r = RingtoneManager.getRingtone(_context, u);
            if (r == null) {
                String urlNewRingtone = updateRingtoneByFileName(target);
                updateUriInAdsManager(target._fileName, urlNewRingtone);
            } else {
                RingtoneManager.setActualDefaultRingtoneUri(
                        _context, RingtoneManager.TYPE_RINGTONE,
                        u);
                updateInSPCurrentRingtone(target._uri);
                SharedPreferences.Editor edit = sp.edit();
                edit.putBoolean(ConfigAppData.IS_ADS_RINGONE_PLAY, true);
                edit.commit();
            }
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
            _context.getContentResolver().delete(
                    uri,
                    MediaStore.MediaColumns.DATA + "=\""
                            + f.getAbsolutePath() + "\"", null);
            Uri newUri = _context.getContentResolver().insert(uri, values);

            RingtoneManager.setActualDefaultRingtoneUri(
                    _context, RingtoneManager.TYPE_RINGTONE,
                    newUri);
            updateInSPCurrentRingtone(newUri.toString());
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean(ConfigAppData.IS_ADS_RINGONE_PLAY, true);
            edit.commit();
            return newUri.toString();

        }
    }
    public void updateInSPCurrentRingtone(String uri){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(ConfigAppData.CUTTENT_ADS_RINGTONE,uri);
        edit.commit();

    }
    public void setUserRingtone() {


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
        // IS_USER_RINGTONE_PLAY

        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(ConfigAppData.IS_ADS_RINGONE_PLAY, false);

        edit.commit();
        String uri = sp.getString(ConfigAppData.DEFAULT_RINGTONE_NAME, null);
        if (uri != null) {
            RingtoneManager.setActualDefaultRingtoneUri(_context, RingtoneManager.TYPE_RINGTONE, Uri.parse(uri));
        } else {

            // is removed or null...
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_context);
        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);
        // ads is not in shared preferences
        if (adsSummaryManagerString != null) {
            AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
            adsSummaryManager._context = _context;
            adsSummaryManager.updateUriWithFileName(fileName, uri);
            adsSummaryManager._context = null;
            SharedPreferences.Editor edit = sp.edit();
            String str = new Gson().toJson(adsSummaryManager);
            edit.putString(ConfigAppData.ADS_SUMMARY_MANEGER, str);
            edit.commit();


        }
    }

}