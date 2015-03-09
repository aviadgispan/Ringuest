package com.ringchash.dodot.aviad.ringuest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AVIAD on 12/18/2014.
 *
 */
public class EventAds extends Activity {
    Context ctx=this;
    public static final String last_update="LAST_UPDATE";
    public static final String Ads_summary_Manager ="ADS_SUMMARY_MANEGER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_ads);

        Button eventButton=(Button)findViewById(R.id.eventAdd);
        Button receiveButton=(Button)findViewById(R.id.recive);
        Button spSaveManagerButton=(Button)findViewById(R.id.spManeger);
        Button countLast24Button=(Button)findViewById(R.id.countLast24);
        countLast24Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseOperations db=new DatabaseOperations(ctx);
                EditText idAds = (EditText) findViewById(R.id.idAds);
                int id_ads = Integer.parseInt(idAds.getText().toString());
                db.getDataForTheLast24hour(db,id_ads);

            };
        });
        spSaveManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdsSummaryManager a = new AdsSummaryManager(ctx);
                saveAdsSummaryManeger(a);

            }

            ;
        });



        receiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText idAds = (EditText) findViewById(R.id.idAds);
                int id_ads = Integer.parseInt(idAds.getText().toString());
                getDataFromServerAfterTime(1984);
                //    dtopTableFromServer();
                //updateEventAds(id_ads);
            }

            ;
        });
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText idAds = (EditText) findViewById(R.id.idAds);
                int id_ads = Integer.parseInt(idAds.getText().toString());
              //  getDataFromServerAfterTime(100);
            //    dtopTableFromServer();
               updateEventAds(id_ads);
                //saveAllEventToServer(23,"das");
            }

            ;
        });
    }
    public void saveAdsSummaryManeger(AdsSummaryManager a){
        a._context=null;
        String str = new Gson().toJson(a);
        Log.d("Gson : ",str);
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit=sp.edit();
        edit.putString(Ads_summary_Manager, str);
        edit.commit();
    }
    public void dtopTableFromServer(){
        DatabaseOperations db=new DatabaseOperations(ctx);
        db.dropTable(db);
    }
    public void getDataFromServerAfterTime(int userId){
        DatabaseOperations db=new DatabaseOperations(ctx);
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        long maxTime=-1;

        long lastUpdate=sp.getLong(last_update,0);
        Log.d("LAST UPDATE: ", ""+lastUpdate);
        ArrayList<EventSQlite> arr=db.getAllAfterTime(db,lastUpdate);
        if(arr==null||arr.size()==0){
            Log.d("SP :=> ", "no new was found form: "+lastUpdate);
            return;
        }
        Log.d("SP :=>SIZE OF Arr ", "Size "+arr.size());
        for(int i=0;i<arr.size();i++){
            if(arr.get(i)._adsTime>maxTime){
                maxTime=arr.get(i)._adsTime;
            }
        }
        Collections.sort(arr,EventSQlite.EventSQliteComparator);
        int sizeOfStringArr=1;
        if(arr.size()>1){
            for(int i=1;i<arr.size();i++){
                if(arr.get(i)._adsID!=arr.get(i-1)._adsID){
                    sizeOfStringArr++;
                }
            }
        }
        String[][] updateArr=new String[sizeOfStringArr][2];
        int[] idArr=new int[sizeOfStringArr];
        idArr[0]=arr.get(0)._adsID;
        if(arr.size()>1){
            int counter=1;
            for(int i=1;i<arr.size();i++){
                if(arr.get(i)._adsID!=arr.get(i-1)._adsID){
                    idArr[counter]=arr.get(i)._adsID;
                    counter++;
                }
            }
        }
        int counterInnerDataPerApp=0;
        for(int k=0;k<idArr.length;k++){
            updateArr[k][0]=idArr[k]+"";
            updateArr[k][1]="";
            counterInnerDataPerApp=0;
            for(int i=0;i<arr.size();i++){
                if(arr.get(i)._adsID==idArr[k]){
                    String regex="#";
                    if(counterInnerDataPerApp==0){
                        regex="";
                    }
                    updateArr[k][1]=updateArr[k][1]+regex+arr.get(i)._adsTime+"&"+arr.get(i)._gps;
                    counterInnerDataPerApp++;
                }
            }
        }
        String dataToServer="";
        for(int i=0;i<idArr.length;i++){
            if(i>0){
                dataToServer="@"+dataToServer;
            }
            dataToServer=dataToServer+updateArr[i][0]+"$"+updateArr[i][1];
        }


        saveAllEventToServer(userId,dataToServer);
                  // if there was a call to update( save to shared preference)
                // the time of the last update
        if(maxTime>=0){

            SharedPreferences.Editor edit=sp.edit();
            edit.putLong(last_update,maxTime);
            edit.commit();


        }
    }

    public void updateEventAds(int id){


        LocationManager locationManager;
        locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location l;

        LocationListener ll = new LocationListner();

        if(locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  ll);
        }
        if(locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,  ll);
        }
        if(locationManager.isProviderEnabled( LocationManager.PASSIVE_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0,  ll);
        }

        if(locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
            l=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }else{
            if(locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER)){
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


        if(l==null){
            return;
        }

        double x=l.getLatitude();
        double y=l.getLongitude();
        String gps=x+","+y;
        DatabaseOperations db=new DatabaseOperations(ctx);
        db.updateAdsEvent(db,gps,id);

       // String str= new Gson().toJson(target);
    }

    public void saveAllEventToServer(long userId,String data){
        // TODO Auto-generated method stub


        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(ConfigAppData.UPDATE_ADS_DATA_HISTORY_FROM_USER);
        final List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("userID",userId+""));
        nameValuePair.add(new BasicNameValuePair("data",data));
        //Encoding POST data
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    // write response to log
                    //	String str=response.getEntity().toString();
                    HttpEntity entity = response.getEntity();
                    //str=EntityUtils.getContentMimeType(entity);
                    String inputLine ;
                    String str="";
                    BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                    try {
                        while ((inputLine = in.readLine()) != null) {
                            str=str+inputLine;
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
    private class LocationListner implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
//                Log.d("LOCATION CHANGED", location.getLatitude() + "");
//                Log.d("LOCATION CHANGED", location.getLongitude() + "");

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
