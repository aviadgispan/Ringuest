package com.ringchash.dodot.aviad.ringuest;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by AVIAD on 12/22/2014.
 * mangage what ring is need to be set in the ringtone maneger
 */
public class AdsSummaryManager {
    AdsSummaryObject[] _arr;
    Context _context;

    public AdsSummaryManager(AdsSummaryObject[] arr, Context con) {
        // array of all rings
        this._arr = arr;
        // the context need
        this._context = con;
    }

    /**
     * constructor
     * @param arr the arry that represent the ring as we get from server
     * @param con the context for dealing to have the ability to save to shared preferance
     */
    public AdsSummaryManager(String[] arr, Context con) {
        if(arr!=null){
            AdsSummaryObject[] sol=new AdsSummaryObject[arr.length];
            boolean valid=true;
            for(int i=0;i<sol.length;i++){
                sol[i]=getAdsSummaryObject(arr[i]);
                if(sol[i]==null){
                    valid=false;
                }
            }
            if(valid){
                this._arr=sol;
            }else{
                int counter=0;
                for(int i=0;i<sol.length;i++){
                    if(sol[i]==null){
                      counter++;
                    }
                }
                this._arr=new AdsSummaryObject[sol.length-counter];
                counter=0;
                for(int i=0;i<sol.length;i++){
                    if(sol[i]!=null){
                        this._arr[counter]=sol[i];
                        counter++;
                    }
                }
            }

        }
        this._context = con;

    }

    /**
     * clean the adsSummaryObject from what is not in the string array (file name)
     * @param arr array that represented the file name
     * @return AdsSummaryObject[] according the file name array
     */
    public AdsSummaryObject[] getAdsSummaryObjectArrAccordingToFileName(String[] arr){
         if(this._arr==null||arr==null||this._arr.length==0){
             return null;
         }
        int counter=0;
        for(int i=0;i<arr.length;i++){
            if(isExist(arr[i])){
                counter++;
            }
        }
        AdsSummaryObject[] sol=new AdsSummaryObject[counter];
        counter=0;
        for(int i=0;i<arr.length;i++){
            if(isExist(arr[i])){
                sol[counter]=getAdsObjectByName(arr[i]);
                counter++;
            }
        }

        return sol;
    }

    /**
     * is exist file name the in arr
     * @param fileName the ask file name
     * @return true if file name is exist
     */
    public boolean isExist(String fileName){
        if(this._arr==null){
            return false;
        }
        for(int i=0;i<this._arr.length;i++){
            if(this._arr[i]._fileName!=null){
                if(this._arr[i]._fileName.equals(fileName)){
                    return true;
                }
            }

        }
        return false;
    } ;

    /**
     * get the AdsSummaryObject according to file name
     * @param fileName the ask file name
     * @return the AdsSummaryObject according to file name
     */
    public AdsSummaryObject getAdsObjectByName(String fileName){
        if(this._arr==null){
            return null;
        }
        for(int i=0;i<this._arr.length;i++){
            if(this._arr[i]._fileName!=null){
                if(this._arr[i]._fileName.equals(fileName)){
                    return this._arr[i];
                }
            }

        }
        return null;
    } ;

    /**
     * get AdsSummaryObject according to the String from the server
     * split by | char
     * @param str ths string from server
     * @return the ask str
     */
    private AdsSummaryObject getAdsSummaryObject(String str) {
        if(str==null||str.length()==0){
            return null;
        }

        String[] arr=str.split("\\|");
        if(arr.length==22){
            return new AdsSummaryObject(arr[0],arr[1],arr[2],arr[3],arr[4],arr[5],arr[6],arr[7],arr[8]
            ,arr[9],arr[10],arr[11],arr[12],arr[13],arr[14],arr[15],arr[16],arr[17],arr[18],arr[19],arr[20],arr[21]);
        }

        return null;
    };

    public AdsSummaryManager(Context con) {
        String id = "1";
        String companyName = "CocaCola";
        String sumOfMaxTime = "1000";
        String maxIn24 = "6";
        this._context = con;
        String validGeoPlace = "31.7653693&35.2098312&2#29.7653693&32.2098312&1000";
        String notValidGeoPlace = "";
        String endCampaign = "1519455580000";
        String startCampaign = "1419282780000";
        String validTime = "0-24";
        String priorety = "8";
        String pathToSound = "/dwd";
        String ringtone = "TRUE";
        String fileName = "CocaCola";
        String notification = "false";
        String alarmClock = "false";


        String onlyWifi="false";
        String userCanSeeDownload="true";
        String titleDownload="title";
        String descriptionDownLoad="description";
        String dayes="1#2#4";
        String interval="1234";
        String pricePerRing="5";
        AdsSummaryObject obj0 = new AdsSummaryObject("1", fileName, priorety, companyName, sumOfMaxTime, maxIn24, validGeoPlace, notValidGeoPlace, startCampaign, endCampaign, validTime, pathToSound, ringtone, notification, alarmClock,onlyWifi,userCanSeeDownload,titleDownload,descriptionDownLoad,dayes,interval,pricePerRing);
        AdsSummaryObject obj1 = new AdsSummaryObject("2", fileName, "10"/*priorety*/, "AV", sumOfMaxTime, maxIn24, validGeoPlace, notValidGeoPlace, startCampaign, endCampaign, validTime, pathToSound, ringtone, notification, alarmClock,onlyWifi,userCanSeeDownload,titleDownload,descriptionDownLoad,dayes,interval,pricePerRing);
        AdsSummaryObject obj2 = new AdsSummaryObject("3", fileName, "2"/*priorety*/, "DSDS", sumOfMaxTime, maxIn24, validGeoPlace, notValidGeoPlace, startCampaign, endCampaign, validTime, pathToSound, ringtone, notification, alarmClock,onlyWifi,userCanSeeDownload,titleDownload,descriptionDownLoad,dayes,interval,pricePerRing);
        AdsSummaryObject[] arr = new AdsSummaryObject[3];
        arr[0] = obj0;
        arr[1] = obj1;
        arr[2] = obj2;
        this._arr = arr;
    }

    /**
     * add AdsSummaryObject
     * @param arr the arr to add
     */
    public void addAll(AdsSummaryObject[] arr){
        if(arr==null){
            return;
        }
        for(int i=0;i<arr.length;i++){
            add(arr[i]);
        }
    }

    /**
     * remove according to id
     * @param idArr the id arr
     */
    public void removeAll(int[] idArr){
        if(this._arr==null||this._arr.length==0){
            return;
        }
        for(int i=0;i<idArr.length;i++){
            remove(idArr[i]);
        }
    }

    /**
     * remove by id
     * @param id id of what we want to remove
     */
    public void remove(int id){
        if(this._arr==null||this._arr.length==0){
            return;
        }
        int index=-1;
        for(int i=0;index==-1&&i<this._arr.length;i++){
            if(this._arr[i]._id==id){
                index=i;
            }
        }
        if(index==-1){
            return;
        }else{
            AdsSummaryObject[] temp=new AdsSummaryObject[this._arr.length-1];
            int counter=0;
            for(int i=0;i<this._arr.length;i++){
                if(i!=index){
                    temp[counter]=this._arr[i];
                    counter++;
                }
            }
            this._arr=temp;
        }
    }

    /**
     * add al from the string from the server
     * split by char |
     * @param arr the arry that represent the ring as we get from server
     */
    public void addAll(String[] arr){
        if(arr==null){
            return;
        }
        for(int i=0;i<arr.length;i++){
            add(getAdsSummaryObject(arr[i]));
        }
    }

    /**
     * add object to the manager
     * @param a the candidate to add
     */
    public void add(AdsSummaryObject a){
        if(a==null){
            return;
        }
        if(this._arr==null){
            this._arr=new AdsSummaryObject[1];
            this._arr[0]=a;
        }else{
            int index=getIndex(a);

            // if ads in not ads manager
            if(index==-1){

                AdsSummaryObject[] temp=new AdsSummaryObject[this._arr.length+1];
                for(int i=0;i<this._arr.length;i++){
                    temp[i]=this._arr[i];
                }
                temp[temp.length-1]=a;
                this._arr=temp;
            }else{
                // if ads in ads manager
                String uri=this._arr[index]._uri;
                boolean inOn=this._arr[index]._isOn;
                this._arr[index]=a;
                this._arr[index]._uri=uri;
                this._arr[index]._isOn=inOn;


            }
        }
    }

    /**
     * find the ask index of ads in the  ads manager
     * @param a the ads
     * @return the ask index if not found return -1
     */
    public int getIndex(AdsSummaryObject a){
      if(this._arr==null){
          return -1;
      }
        for(int i=0;i<this._arr.length;i++){
            if(this._arr[i]._id==a._id){
                return i;
            }
        }
      return -1;
    }

    /**
     * get the relevent ring according ti the data(location)
     * first in list
     * @return the ask object
     */
    public AdsSummaryObject getRelevant(){
        LocationManager locationManager;
        locationManager = (LocationManager)_context.getSystemService(_context.LOCATION_SERVICE);

        Location l;
        LocationListener ll = new LocationListner();
       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
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


        double lat=l.getLatitude();
        double lon=l.getLongitude();
        AdsSummaryObject[] arr= getAllRelevant(lat,lon);
        if(arr==null||arr.length==0){
            return null;
        }
        return arr[0];
    }

    /**
     * update uri in the maneger adding uri to the adsSummaryObject with
     * the ask filename
     * @param fileName represernt the object that we wqant to add to him uri
     * @param uri ask uri to add
     * @return true if seccsed
     */
    public boolean updateUriWithFileName(String fileName,String uri){
        if(this._arr==null){
            return false;
        }
        boolean update=false;
        for(int i=0;i<this._arr.length;i++){
            if(this._arr[i]._fileName!=null){
                if(this._arr[i]._fileName.equals(fileName)){
                    update=true;
                    this._arr[i]._uri=uri;
                }
            }
        }
        return update;
    }

    /**
     * get the relavant ring according to user data(loication gender age time hour etc..)
     * @param lat1 latitude
     * @param lon1 longitude
     * @return the AdsSummaryObject[] of the ring that fit to time
     */
    public AdsSummaryObject[] getAllRelevant(double lat1, double lon1) {
        if (this._arr == null) {
            return null;
        }
        boolean[] boolArr = new boolean[this._arr.length];
        int counter = 0;
        for (int i = 0; i < this._arr.length; i++) {
            boolean isOn = this._arr[i]._isOn;
            boolean valid = this._arr[i]._valid;
            boolean isValidtimeFromLastRing=this._arr[i].isValidtimeFromLastRing(this._context);
            boolean isValidDay=this._arr[i].isInValidDay();
            boolean isInCampaignHours = this._arr[i].isInCampaignTimeHours();
            boolean isInCampaignTime = this._arr[i].isInCampaignTime();
            boolean isInBoundaries = this._arr[i].isBoundaries(lat1, lon1);
            boolean isFinishDownload=this._arr[i].isFinishDownload();
            boolean isCounterLessThenMaxIn24 = this._arr[i].counterLessThenMaxIn24(this._context);
            boolean isEverFinished=this._arr[i].counterAdsIdEver(this._context);
            if (valid && isOn && isInCampaignHours &&isValidtimeFromLastRing&&isEverFinished&&isValidDay&& isInCampaignTime && isInBoundaries && isCounterLessThenMaxIn24&&isFinishDownload) {
                boolArr[i] = true;
                counter++;
            } else {
                boolArr[i] = false;

            }
        }
        if (counter == 0) {
            return null;
        }
        AdsSummaryObject[] sol = new AdsSummaryObject[counter];
        counter = 0;
        for (int i = 0; i < boolArr.length; i++) {
            if (boolArr[i]) {
                sol[counter] = this._arr[i];
                counter++;
            }
        }
        if (sol.length == 1) {
            return sol;
        }
        for (int i = 0; i < sol.length - 1; i++) {
            for (int j = i + 1; j < sol.length; j++) {
                if (sol[i]._priority < sol[j]._priority) {
                    sol = swap(sol, i, j);
                }
            }
        }

        return sol;

    }

    public static AdsSummaryObject[] swap(AdsSummaryObject[] arr, int a, int b) {
        AdsSummaryObject temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
        return arr;
    }

    /**
     * listenr for place
     */
    private class LocationListner implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
//            if (location != null) {
////                Log.d("LOCATION CHANGED", location.getLatitude() + "");
////                Log.d("LOCATION CHANGED", location.getLongitude() + "");
//
//            }
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
