package com.ringchash.dodot.aviad.ringchash;

/**
 * Created by AVIAD on 1/1/2015.
 */
public class ConfigAppData {
    ////////////////////SERVER DATA //////////////////////
// /   http://ec2-54-93-56-200.eu-central-1.compute.amazonaws.com:8080
    public static final String URL_SERVER="http://10.0.0.5:8080";
    public static final String UPDATE_ADS_DATA_HISTORY_FROM_USER=URL_SERVER+"/updateAdsData";
    public static final String UPDATE_ADS_DATA_FROM_SERVER=URL_SERVER+"/getMyAdsData";
    public static final String UPDATE_USER_FROM_SERVER=URL_SERVER+"/updateUserData";
    public static final String ADS_SUMMARY_MANEGER ="ADS_SUMMARY_MANEGER";
    ////////////////////USER DATA ///////////////////////
    /*
     user data in shared preferences
     */
    public static final String USER_NAME="USER_NAME";

    public static final String USER_PHONE_NUMBER="USER_PHONE_NUMBER";

    /*
     0:female
     1: male ,
     -1 : unknown
     */
    public static final String USER_FB_ID="USER_FB_ID";
    public static final String USER_GENDER="USER_GENDER";
    public static final String USER_EMAIL="USER_EMAIL";
    public static final String USER_BIRTH_DAY="USER_BIRTH_DAY";
    public static final String USER_BIRTH_MONTH="USER_BIRTH_MONTH";
    public static final String USER_BIRTH_YEAR="USER_BIRTH_YEAR";
    public static final String ADS_LIST_IN_DOWNLOAD_PROGRESS="ADS_LIST_IN_DOWNLOAD_PROGRESS";
    public static final String USER_NUMBER="USER_NUMBER";
    //only if managed
    public static final String LAST_UPDATE="LAST_UPDATE";

    public static final String LAST_UPDATE_SERVER_DATA_FROM_USER="LAST_UPDATE_SERVER_FROM_USER";
    public static final String LAST_UPDATE_FILE="LAST_UPDATE_FILE";

    ///  RINGTONE MANAGER DATA
    public static final String DEFAULT_RINGTONE_NAME="DEFAULT_RINGTONE_NAME";
    public static final String IS_ADS_RINGONE_PLAY="IS_ADS_RINGONE_PLAY";
    public static final String CUTTENT_ADS_RINGTONE="CUTTENT_ADS_RINGTONE";
    //// USER DATA
    public static final String USER_ID="USER_ID";

    ////////enum
    public static final int UPDATE_RINGTONE=0;
    public static final int UPDATE_SERVER_DATA=1;
    public static final int UPDATE_FILE=2;

    ///////////Alarm maneger
    private static final int SECOND=1000;
    public static final int MINUTE=60*SECOND;
    public static final int HOUR=60*MINUTE;
    public static final long UPDATE_INTERVAL=MINUTE*2;
    public static final long UPDATE_FILE_INTERVAL=UPDATE_INTERVAL*2;
    public static final long UPDATE_SERVER_DATA_INTERVAL=UPDATE_INTERVAL*5;


    public static final String CUPON_URL="https://s3-eu-west-1.amazonaws.com/matos/cupon.png";
}
