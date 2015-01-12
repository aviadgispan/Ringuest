package com.ringchash.dodot.aviad.ringchash;

import android.provider.BaseColumns;

/**
 * Created by AVIAD on 12/18/2014.
 */
public class TableHistoryData {
public TableHistoryData(){

}
public static abstract class TableHistoryInfo implements BaseColumns{
    public static final String ID_ADS="id_ads";
    public static final String GPS_ADS="gps_ads";
    public static final String TIME_ADS ="time_ads";
    public static final String DATABASE_NAME ="ring_cash";
    public static final String TABLE_NAME ="ads_history";


}
}
