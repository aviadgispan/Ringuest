package com.ringchash.dodot.aviad.ringuest;

import java.util.Comparator;

/**
 * Created by AVIAD on 12/21/2014.
 */
public class EventSQlite  {

    public int _adsID;
    public long _adsTime;
    public String _gps;

    public EventSQlite(int adsId, String gps, long time) {
        this._adsID = adsId;
        this._gps = gps;
        this._adsTime = time;
    }
    public static Comparator<EventSQlite> EventSQliteComparator = new Comparator<EventSQlite>() {



        @Override
        public int compare(EventSQlite a, EventSQlite b) {
            if (a._adsID > b._adsID) {
                return 1;
            } else {
                if (a._adsID == b._adsID) {
                    if(a._adsTime>b._adsID){
                        return 1;
                    }else{
                        if(a._adsTime==b._adsID){
                            return 0;
                        }else{
                            return -1;
                        }
                    }

                } else {

                    return -1;
                }
            }
        }
    };

};
