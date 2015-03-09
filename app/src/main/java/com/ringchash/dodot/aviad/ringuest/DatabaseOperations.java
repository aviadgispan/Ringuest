package com.ringchash.dodot.aviad.ringuest;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.ringchash.dodot.aviad.ringuest.TableHistoryData.TableHistoryInfo;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


/**
 * Created by AVIAD on 12/18/2014.
 */
public class DatabaseOperations extends SQLiteOpenHelper {
    public static final int database_version = 2;
    public String CREATE_QUERY = "CREATE TABLE " + TableHistoryInfo.TABLE_NAME +
            " (" + TableHistoryInfo.ID_ADS + " INTEGER," + TableHistoryInfo.GPS_ADS + " TEXT," +
            TableHistoryInfo.TIME_ADS + " INTEGER);";

    public DatabaseOperations(Context context) {
        super(context, TableHistoryInfo.TABLE_NAME, null, database_version);

    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL(CREATE_QUERY);
        Log.d("DB operation", "Data Base Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public int getDataForTheLast24hour(DatabaseOperations dop,int appID){
        Calendar c = Calendar.getInstance(Locale.getDefault());
        int sec=60;
        int mil=1000;
        int min=60;
        int hour=24;

        SQLiteDatabase SQ=dop.getReadableDatabase();
        long timeBefore24=c.getTimeInMillis()-sec*mil*min*hour;

        Cursor mCount= SQ.rawQuery("select count(*) from "+TableHistoryInfo.TABLE_NAME+" where "+TableHistoryInfo.ID_ADS+"="+appID+" and " +TableHistoryInfo.TIME_ADS+" > "+timeBefore24+"", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        Log.d("COUNT ",count+"");
        SQ.close();
        return count;
    };
    public boolean isRingMilAgo(DatabaseOperations dop,int appID,long mil){
        Calendar c = Calendar.getInstance(Locale.getDefault());
        SQLiteDatabase SQ=dop.getReadableDatabase();
        long timeBefore=c.getTimeInMillis()-mil;
        Cursor mCount= SQ.rawQuery("select count(*) from "+TableHistoryInfo.TABLE_NAME+" where "+TableHistoryInfo.ID_ADS+"="+appID+" and " +TableHistoryInfo.TIME_ADS+" > "+timeBefore+"", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        Log.d("COUNT ",count+"");
        SQ.close();
        if(count>0){
            return true;
        }else{
            return false;
        }
    };

    public int getDataForTheAppEver(DatabaseOperations dop,int appID){
       // Calendar c = Calendar.getInstance(Locale.getDefault());


        SQLiteDatabase SQ=dop.getReadableDatabase();


        Cursor mCount= SQ.rawQuery("select count(*) from "+TableHistoryInfo.TABLE_NAME+" where "+TableHistoryInfo.ID_ADS+"="+appID, null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();
        Log.d("COUNT ",count+"");
        SQ.close();
        return count;
    };


    public ArrayList<EventSQlite> getAllAfterTime(DatabaseOperations dop,long time){
        SQLiteDatabase SQ=dop.getReadableDatabase();
        String[] tableCol={TableHistoryInfo.ID_ADS,TableHistoryInfo.GPS_ADS,TableHistoryInfo.TIME_ADS};

        String timeString=""+time;
        String[] whereArgs =  {timeString};
        String whereClause = TableHistoryInfo.TIME_ADS+" > "+timeString;
         Cursor cur=SQ.query(TableHistoryInfo.TABLE_NAME,tableCol,whereClause,null,null,null,null);
         cur.moveToFirst();
        int counter=0;

        ArrayList<EventSQlite> arr=new ArrayList<EventSQlite>();
        long maxTime=-1;
        while(!cur.isAfterLast()){
            int indexGps=cur.getColumnIndex(TableHistoryInfo.GPS_ADS);
            int indexID_Ads=cur.getColumnIndex(TableHistoryInfo.ID_ADS);
            int indexTime_Ads=cur.getColumnIndex(TableHistoryInfo.TIME_ADS);
            String gpsData=cur.getString(indexGps);
            int ID_Ads=cur.getInt(indexID_Ads);
            long timeAds=cur.getLong(indexTime_Ads);
            if(timeAds>maxTime){
                maxTime=timeAds;
            }

            Log.d("db counter ",counter+" "+gpsData);
            counter++;
            arr.add(new EventSQlite(ID_Ads,gpsData,timeAds ));
            cur.moveToNext();
        }
        SQ.close();

        return arr;
    }

    public void dropTable(DatabaseOperations dop){
        SQLiteDatabase SQ=dop.getWritableDatabase();
        SQ.execSQL("DROP TABLE "+TableHistoryInfo.TABLE_NAME);
        //DROP TABLE table_name
    }
    public void updateAdsEvent(DatabaseOperations dop,String gps,int id){
        SQLiteDatabase SQ=dop.getWritableDatabase();
        ContentValues cv= new ContentValues();

        cv.put(TableHistoryInfo.ID_ADS,id);
        cv.put(TableHistoryInfo.GPS_ADS,gps);
        Calendar c = Calendar.getInstance(Locale.getDefault());
        cv.put(TableHistoryInfo.TIME_ADS, c.getTimeInMillis());
        long k=SQ.insert(TableHistoryInfo.TABLE_NAME,null,cv);
        Log.d("DB operation","insert new Ads event to db");
        SQ.close();
    }


}
