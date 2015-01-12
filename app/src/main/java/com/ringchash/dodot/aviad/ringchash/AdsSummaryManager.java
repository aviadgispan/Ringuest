package com.ringchash.dodot.aviad.ringchash;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by AVIAD on 12/22/2014.
 */
public class AdsSummaryManager {
    AdsSummaryObject[] _arr;
    Context _context;

    public AdsSummaryManager(AdsSummaryObject[] arr, Context con) {
        this._arr = arr;
        this._context = con;
    }

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
    private AdsSummaryObject getAdsSummaryObject(String str) {
        if(str==null||str.length()==0){
            return null;
        }

        String[] arr=str.split("\\|");
        if(arr.length==19){
            return new AdsSummaryObject(arr[0],arr[1],arr[2],arr[3],arr[4],arr[5],arr[6],arr[7],arr[8]
            ,arr[9],arr[10],arr[11],arr[12],arr[13],arr[14],arr[15],arr[16],arr[17],arr[18]);
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
        AdsSummaryObject obj0 = new AdsSummaryObject("1", fileName, priorety, companyName, sumOfMaxTime, maxIn24, validGeoPlace, notValidGeoPlace, startCampaign, endCampaign, validTime, pathToSound, ringtone, notification, alarmClock,onlyWifi,userCanSeeDownload,titleDownload,descriptionDownLoad);
        AdsSummaryObject obj1 = new AdsSummaryObject("2", fileName, "10"/*priorety*/, "AV", sumOfMaxTime, maxIn24, validGeoPlace, notValidGeoPlace, startCampaign, endCampaign, validTime, pathToSound, ringtone, notification, alarmClock,onlyWifi,userCanSeeDownload,titleDownload,descriptionDownLoad);
        AdsSummaryObject obj2 = new AdsSummaryObject("3", fileName, "2"/*priorety*/, "DSDS", sumOfMaxTime, maxIn24, validGeoPlace, notValidGeoPlace, startCampaign, endCampaign, validTime, pathToSound, ringtone, notification, alarmClock,onlyWifi,userCanSeeDownload,titleDownload,descriptionDownLoad);
        AdsSummaryObject[] arr = new AdsSummaryObject[3];
        arr[0] = obj0;
        arr[1] = obj1;
        arr[2] = obj2;
        this._arr = arr;
    }
    public void addAll(AdsSummaryObject[] arr){
        if(arr==null){
            return;
        }
        for(int i=0;i<arr.length;i++){
            add(arr[i]);
        }
    }
    public void removeAll(int[] idArr){
        if(this._arr==null||this._arr.length==0){
            return;
        }
        for(int i=0;i<idArr.length;i++){
            remove(idArr[i]);
        }
    }
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
    public void addAll(String[] arr){
        if(arr==null){
            return;
        }
        for(int i=0;i<arr.length;i++){
            add(getAdsSummaryObject(arr[i]));
        }
    }
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
    public AdsSummaryObject getRelevant(){
        LocationManager locationManager;
        locationManager = (LocationManager)_context.getSystemService(_context.LOCATION_SERVICE);
        Location l=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        double lat=l.getLatitude();
        double lon=l.getLongitude();
        AdsSummaryObject[] arr= getAllRelevant(lat,lon);
        if(arr==null||arr.length==0){
            return null;
        }
        return arr[0];
    }
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
    public AdsSummaryObject[] getAllRelevant(double lat1, double lon1) {
        if (this._arr == null) {
            return null;
        }
        boolean[] boolArr = new boolean[this._arr.length];
        int counter = 0;
        for (int i = 0; i < this._arr.length; i++) {
            boolean isOn = this._arr[i]._isOn;
            boolean valid = this._arr[i]._valid;
            boolean isInCampaignHours = this._arr[i].isInCampaignTimeHours();
            boolean isInCampaignTime = this._arr[i].isInCampaignTime();
            boolean isInBoundaries = this._arr[i].isBoundaries(lat1, lon1);
            boolean isFinishDownload=this._arr[i].isFinishDownload();
            boolean isCounterLessThenMaxIn24 = this._arr[i].counterLessThenMaxIn24(this._context);
            if (valid && isOn && isInCampaignHours && isInCampaignTime && isInBoundaries && isCounterLessThenMaxIn24&&isFinishDownload) {
                boolArr[i] = true;
                counter++;
            } else {
                boolArr[i] = false;
                ;
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
}
