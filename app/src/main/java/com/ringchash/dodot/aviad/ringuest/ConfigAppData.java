package com.ringchash.dodot.aviad.ringuest;

/**
 * Created by AVIAD on 1/1/2015.
 */
public class ConfigAppData {
    ////////////////////SERVER DATA //////////////////////
// /  "http://10.0.0.2:8080"
    /**
     * the url of the server...
     */
    public static final String URL_SERVER="http://matosapp.info:8080";

    // public static final String URL_SERVER="http://10.0.0.2:8080";
    /**
     * makeing strings for server
     */
    public static final String UPDATE_ASK_FOR_MONEY=URL_SERVER+"/updateAskForMoney";
    public static final String UPDATE_ADS_DATA_HISTORY_FROM_USER=URL_SERVER+"/updateAdsData";
    public static final String UPDATE_ADS_DATA_FROM_SERVER=URL_SERVER+"/getMyAdsData";
    public static final String UPDATE_USER_FROM_SERVER=URL_SERVER+"/updateUserData";
    /**
     * the String that represent the maneger of all rings...
     */
    public static final String ADS_SUMMARY_MANEGER ="ADS_SUMMARY_MANEGER";
    ////////////////////USER DATA ///////////////////////
    /*
     user data in shared preferences
     */
    public static final String USER_NAME="USER_NAME";

    public static final String USER_LAST_NAME="USER_LAST_NAME";
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


    public static final String ADS_HISTORY_MANAGER_UNTIL_GETTING_CASH="ADS_HISTORY_MANAGER_UNTIL_GETTING_CASH";

    ////////enum
    public static final int UPDATE_RINGTONE=0;
    public static final int UPDATE_SERVER_DATA=1;
    public static final int UPDATE_FILE=2;

    ///////////Alarm maneger
    /**
     * define the time interval for the service that running and manege of stuff
     */
    private static final int SECOND=1000;
    public static final int MINUTE=60*SECOND;
    public static final int HOUR=60*MINUTE;
    public static final long UPDATE_INTERVAL=MINUTE*2;
    public static final long UPDATE_FILE_INTERVAL=UPDATE_INTERVAL*15;
    public static final long UPDATE_SERVER_DATA_INTERVAL=UPDATE_INTERVAL*27;
    /**
     * url of the data that store at S3
     */
    public static String CUPON_URL="https://s3.eu-central-1.amazonaws.com/ringuest/kopon/cupon.png";
    public static String TERMS_URL="https://s3.eu-central-1.amazonaws.com/ringuest/mor.../askem-mistaaa.html";
    /**
     * the define of the first data. get fixed if need by the server. in the update action  from the server
     */
    public static double PAY_FOR_RING=0.05;
    public static int MIN_FOR_GET_CASH=20;

    public static final String CUPON_URL_SP="CUPON_URL";
    public static final String TERMS_URL_SP="TERMS_URL_SP";
    public static final String PAY_FOR_RING_SP="PAY_FOR_RING_SP";
    public static final String MIN_FOR_GET_CASH_SP="MIN_FOR_GET_CASH_SP";
    /**
     * counter ring with out the value
     */
    public static final String COUNTER_ALL_RING="COUNTER_ALL_RING";
    public static final String COUNTER_ALL_RING_THAT_UNPAID="COUNTER_ALL_RING_THAT_UNPAID";
    /**
     * counter of all rings ever tue summarize is for each ring his value
     */
    public static final String TAKBUL_ALL_RING="TAKBUL_ALL_RING";
    /**
     * counter of all rings unpaid tue summarize is for each ring his value
     */
    public static final String TAKBUL_ALL_RING_THAT_UNPAID="TAKBUL_ALL_RING_THAT_UNPAID";




    public static final String FIRST_RUN="FIRST_RUN";

}
