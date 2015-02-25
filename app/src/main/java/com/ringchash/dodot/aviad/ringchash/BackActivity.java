package com.ringchash.dodot.aviad.ringchash;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.List;
import java.util.Locale;

/**
 * Created by AVIAD on 1/1/2015.
 */
public class BackActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.back);
        Button btnGetAdsFromSrever = (Button) findViewById(R.id.updateDataFromServer);
        Button btnClearData = (Button) findViewById(R.id.clearData);
        Button btnPrintAdsManegerData = (Button) findViewById(R.id.printAdsManegerData);

        btnPrintAdsManegerData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printManagerData();
            }

            ;
        });
        btnClearData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                removeOldFile();


            }

            ;
        });


        btnGetAdsFromSrever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAdsDataFromServer();

            }

            ;
        });

    }

    public void getAdsDataFromServer() {

        /// get the data of user age gender & gps for the server.
        int year;
        int gender;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
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
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
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

        double lat = l.getLatitude();
        double lon = l.getLongitude();
        /// start sending to the server
        getAdsDataFromServerAccordingToUserData(age, gender, lat, lon, allMyAdsId);

    }

    private void removeAllThatNotRelevant(String[] arrToRemoveString) {
        if (arrToRemoveString == null || arrToRemoveString.length == 0) {
            return;
        }
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);

        AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
        adsSummaryManager._context = this;
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

    private void updateDataFromServer(String data) {
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
        DownloadAction(this);
    }

    private void updateAddFromString(String[] adsArr) {
        if (adsArr == null || adsArr.length == 0) {
            return;
        } else {
            Gson gson = new Gson();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);
            // ads is not in shared preferences
            if (adsSummaryManagerString == null) {
                AdsSummaryManager adsSummaryManager = new AdsSummaryManager(adsArr, this);
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
                adsSummaryManager._context = this;
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
                        updateDataFromServer(str);

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

    private static File[] getAllFileNameInRingCash() {
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

    public void removeOldFile() {
        File[] arr = getAllFileNameToRemove(this);
        if (arr == null || arr.length == 0) {
            Log.d("FILR m : ", "no old file founded");
            return;
        }
        for (int i = 0; i < arr.length; i++) {
            String name = arr[i].getName();
            Log.d("FILE  " + i + " : ", arr[i].getName());
            boolean isS = arr[i].delete();
        }
    }

    protected static File[] getAllFileNameToRemove(Context context) {
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
    };



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
    private static void updateListInDownLoad(Context context,String fileName) {
        //ADS_LIST_IN_DOWNLOAD_PROGRESS
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String downloadDataString = sp.getString(ConfigAppData.ADS_LIST_IN_DOWNLOAD_PROGRESS, null);

        Gson gson = new Gson();

        DownLoadData downloadData;
        if (downloadDataString != null) {
            downloadData = gson.fromJson(downloadDataString, DownLoadData.class);
           String[] fileNameArr=downloadData._downloadList;
           for(int i=0;i<fileNameArr.length;i++){
               if(fileNameArr[i].equals(fileName)){
                   return;
               }

           }
            String[] arr=new String[fileNameArr.length+1];
            for(int i=0;i<fileNameArr.length;i++){
                arr[i]=fileNameArr[i];
            }
            arr[arr.length-1]=fileName;
            downloadData._downloadList=arr;
        }else{
            downloadData=new DownLoadData();
            downloadData._downloadList=new String[1];
            downloadData._downloadList[0]=fileName;

        }
        String str = new Gson().toJson(downloadData);
        Log.d("Gson : ", str);

        SharedPreferences.Editor edit = sp.edit();
        edit.putString(ConfigAppData.ADS_LIST_IN_DOWNLOAD_PROGRESS, str);
        edit.commit();

    }
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
        updateListInDownLoad(context,fileName);
        manager.enqueue(request);
        // DownloadManager manger=(DownloadManager) getActivity().


    }

    public void printManagerData() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);
        Log.d("ads maneger ", adsSummaryManagerString);
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
