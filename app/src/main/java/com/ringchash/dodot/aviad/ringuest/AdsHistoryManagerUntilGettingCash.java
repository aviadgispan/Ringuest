package com.ringchash.dodot.aviad.ringuest;

/**
 * Created by AVIAD on 1/28/2015.
 *
 */

public class AdsHistoryManagerUntilGettingCash {
    AdsHistoryUnitUntilGettingCash[] _arr;
    public AdsHistoryManagerUntilGettingCash(){

    }
    public boolean update(int id,long time){
        if(this._arr==null){
            _arr=new AdsHistoryUnitUntilGettingCash[1];
            _arr[0]=new AdsHistoryUnitUntilGettingCash(id,time);
            return true;
        }else{

            for(int i=0;i<_arr.length;i++){
                if(_arr[i]._id==id){
                    if(this._arr[i]._last<time){
                        this._arr[i]._last=time;
                        return true;
                    }
                    return false;
                }
            }
            AdsHistoryUnitUntilGettingCash[] fixArr=new AdsHistoryUnitUntilGettingCash[_arr.length+1];
            for(int i=0;i<_arr.length;i++){
                fixArr[i]=_arr[i];
            }
            _arr=fixArr;
            _arr[_arr.length-1]=new AdsHistoryUnitUntilGettingCash(id,time);
            return true;
        }
    }

    public boolean clear(long time){
        if(this._arr==null){
            return false;
        }
        int counter=0;
        for(int i=0;i<_arr.length;i++){
            if(_arr[i]._last>time){
                counter++;
            }
        }
        if(counter==this._arr.length){
            return false;
        }else{
            if(counter==0){
                _arr=null;
                return true;
            }
            AdsHistoryUnitUntilGettingCash[] fixArr=new AdsHistoryUnitUntilGettingCash[counter];
            counter=0;
            for(int i=0;i<_arr.length;i++){
                if(_arr[i]._last>time){
                    fixArr[counter]=_arr[i];
                    counter++;
                }
            }
            _arr=fixArr;
            return true;


        }

    }
    public String getAllRelevantAdsFromTime(){
        if(_arr==null){
            return null;
        }
        String str="";
        int counter=0;
        for(int i=0;i<this._arr.length;i++){

                if(counter>0){
                    str=str+","+this._arr[i]._id;
                }else{
                    str=str+this._arr[i]._id;
                }
                counter++;

        }
        if(counter==0){
            return null;
        }else{
            return str;
        }

    }

}

