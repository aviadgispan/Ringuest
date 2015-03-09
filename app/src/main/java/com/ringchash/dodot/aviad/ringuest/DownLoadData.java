package com.ringchash.dodot.aviad.ringuest;

/**
 * Created by AVIAD on 1/5/2015.
 */
public class DownLoadData {
    String[] _downloadList;
    public void add(String fileName){
        if(this._downloadList==null){
            this._downloadList=new String[1];
            this._downloadList[0]=fileName;
        }else{
            for(int i=0;i<this._downloadList.length;i++){
                if(this._downloadList[i].equals(fileName)){
                    return;
                }
            }
            String[] arr=new String[this._downloadList.length+1];
            for(int i=0;i<this._downloadList.length;i++){
                arr[i]=this._downloadList[i];
            }
            arr[arr.length-1]=fileName;
            this._downloadList=arr;
        }
    }
    public void remove(String fileName){
        if(this._downloadList==null){
            return;
        }
        int counter=0;
        for(int i=0;i<this._downloadList.length;i++) {
            if (fileName.equals(this._downloadList[i])) {
                counter++;
            }
        }
        if(counter==0){
            return ;
        }
        String[] newList=new String[this._downloadList.length-counter];
        counter=0;
        for(int i=0;i<this._downloadList.length;i++){
            if (!fileName.equals(this._downloadList[i])) {
               newList[counter]=this._downloadList[i];
                counter++;
            }
        }

    }
}
