package com.ringchash.dodot.aviad.ringchash;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by AVIAD on 12/22/2014.
 */
public class AdsSummaryObject {
    int _id;

    String _companyName;
    boolean _isOn = true;
    int _sumOfMaxTime;

    double[][] _validGeoPlace;
    // all the geo point [[latitude,longitude,R],[latitude,longitude,R]
    // that  not valid
    double[][] _notValidGeoPlace;

    long _endCampaign;
    long _startCampaign;

    boolean _ringtone;
    boolean _alarmClock;
    boolean _notification;
    String _pathToSound;
    int _maxIn24;
    String _uri;
    int _priority;
    int[][] _validTime;
    boolean _valid = true;
    String _fileName;
    boolean _onlyWifi;
    boolean _userCanSeeDownload;
    String _titleDownload;
    String _descriptionDownLoad;

    public AdsSummaryObject(String id, String fileName, String priority, String compenyName, String sumOfMaxTime, String maxIn24, String validGeoPlace, String notValidGeoPlace,
                            String startCampaign, String endCampaign, String validTime
            , String pathToSound, String ringtone, String notification, String alarmClock, String onlyWifi, String userCanSeeDownload, String titleDownload, String descriptionDownLoad) {

        if (isNumberInt(maxIn24)) {
            this._maxIn24 = Integer.parseInt(maxIn24);
        } else {
            this._valid = false;
        }
        _uri = null;
        if (isNumberInt(id)) {
            this._id = Integer.parseInt(id);
        } else {
            this._valid = false;
        }
        this._titleDownload = titleDownload;
        this._descriptionDownLoad = descriptionDownLoad;
        if (fileName != null && fileName.length() > 0) {
            this._fileName = fileName;
        } else {
            this._valid = false;
        }
        if (isNumberInt(priority)) {
            this._priority = Integer.parseInt(priority);
        } else {
            this._valid = false;
        }
        _validTime = getValidTimeInDay(validTime);
        this._pathToSound = pathToSound;
        if (notification != null && notification.length() > 0) {
            if (notification.charAt(0) == 'T' || notification.charAt(0) == 't') {
                this._notification = true;
            } else {
                this._notification = false;
            }
        } else {
            this._valid = false;
        }
        if (alarmClock != null && alarmClock.length() > 0) {
            if (alarmClock.charAt(0) == 'T' || alarmClock.charAt(0) == 't') {
                this._alarmClock = true;
            } else {
                this._alarmClock = false;
            }
        } else {
            this._valid = false;
        }


        if (onlyWifi != null && onlyWifi.length() > 0) {
            if (onlyWifi.charAt(0) == 'T' || onlyWifi.charAt(0) == 't') {
                this._onlyWifi = true;
            } else {
                this._onlyWifi = false;
            }
        } else {
            this._valid = false;
        }

        if (userCanSeeDownload != null && userCanSeeDownload.length() > 0) {
            if (userCanSeeDownload.charAt(0) == 'T' || userCanSeeDownload.charAt(0) == 't') {
                this._userCanSeeDownload = true;
            } else {
                this._userCanSeeDownload = false;
            }
        } else {
            this._valid = false;
        }

        if (ringtone != null && ringtone.length() > 0) {
            if (ringtone.charAt(0) == 'T' || ringtone.charAt(0) == 't') {
                this._ringtone = true;
            } else {
                this._ringtone = false;
            }
        } else {
            this._valid = false;
        }
        if (isNumberInt(endCampaign)) {
            this._endCampaign = Long.parseLong(endCampaign);
        } else {
            this._valid = false;
        }

        if (isNumberInt(startCampaign)) {
            this._startCampaign = Long.parseLong(startCampaign);
        } else {
            this._valid = false;
        }
        this._companyName = compenyName;
        if (isNumberInt(sumOfMaxTime)) {
            this._sumOfMaxTime = Integer.parseInt(sumOfMaxTime);
        } else {
            this._valid = false;
        }


        this._validGeoPlace = this.getGeoFromArr(validGeoPlace);
        if (this._validGeoPlace == null) {
            this._valid = false;
        }
        this._notValidGeoPlace = this.getGeoFromArr(notValidGeoPlace);


    }

    public static boolean isNumberDouble(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char k = str.charAt(i);
            if (!(k == '0' || k == '1' || k == '2' || k == '3' || k == '4' || k == '5' || k == '6' || k == '7' || k == '8' || k == '9' || k == '.')) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumberInt(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char k = str.charAt(i);
            if (!(k == '0' || k == '1' || k == '2' || k == '3' || k == '4' || k == '5' || k == '6' || k == '7' || k == '8' || k == '9')) {
                return false;
            }
        }
        return true;
    }

    public static int[][] getValidTimeInDay(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        String[] temp = str.split("#");
        int[][] sol = new int[temp.length][2];
        for (int i = 0; i < sol.length; i++) {
            String[] inner = temp[i].split("-");
            if (inner == null || inner.length != 2 || !isNumberInt(inner[0]) || !isNumberInt(inner[1])) {
                return null;
            } else {
                sol[i][0] = Integer.parseInt(inner[0]);
                sol[i][1] = Integer.parseInt(inner[1]);
            }
        }
        return sol;
    }

    public static double[][] getGeoFromArr(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        String[] dataPointArr = str.split("#");
        String[][] dataGeoSplitInString = new String[dataPointArr.length][];

        for (int i = 0; i < dataPointArr.length; i++) {
            dataGeoSplitInString[i] = dataPointArr[i].split("&");
        }
        for (int i = 0; i < dataGeoSplitInString.length; i++) {
            if (dataGeoSplitInString[i] == null || dataGeoSplitInString[i].length != 3) {
                return null;
            }
        }
        double[][] dataGeoSplitInDouble = new double[dataGeoSplitInString.length][3];
        for (int i = 0; i < dataGeoSplitInDouble.length; i++) {
            for (int j = 0; j < 3; j++) {
                if (!isNumberDouble(dataGeoSplitInString[i][j])) {
                    return null;
                } else {
                    dataGeoSplitInDouble[i][j] = Double.parseDouble(dataGeoSplitInString[i][j]);
                }
            }
        }

        return dataGeoSplitInDouble;
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2,
                                  double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = deg2rad(lat2 - lat1);
        double lonDistance = deg2rad(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public boolean isBoundaries(double lat1, double lon1) {
        if (this._validGeoPlace == null) {
            return false;
        }
        boolean inBoundaries = false;
        for (int i = 0; i < this._validGeoPlace.length && !inBoundaries; i++) {
            if (this._validGeoPlace[i] != null && this._validGeoPlace[i].length == 3) {
                if (distance(lat1, lon1, this._validGeoPlace[i][0], this._validGeoPlace[i][1], 0, 0) <= this._validGeoPlace[i][2] * 1000) {
                    inBoundaries = true;
                }
            }
        }
        if (!inBoundaries) {
            return false;
        }
        if (this._notValidGeoPlace == null || this._notValidGeoPlace.length == 0) {
            return true;
        }
        for (int i = 0; i < this._notValidGeoPlace.length; i++) {
            if (this._notValidGeoPlace[i] != null && this._notValidGeoPlace[i].length == 3) {
                if (distance(lat1, lon1, this._notValidGeoPlace[i][0], this._notValidGeoPlace[i][1], 0, 0) <= this._notValidGeoPlace[i][2] * 1000) {
                    return false;
                }
            }
        }
        return true;

    }

    public boolean isFinishDownload() {
        if (this._fileName == null || this._fileName.length() == 0) {
            this._valid = false;
            return false;
        }
        File[] fileArr = AlarmReceiver.getAllFileNameInRingCash();
        if (fileArr == null || fileArr.length == 0) {
            return false;
        }
        for (int i = 0; i < fileArr.length; i++) {
            if (fileArr[i].getName().equals(this._fileName)) {
                return true;
            }
        }
        return false;
    }

    ;

    public boolean theCampaignIsOver() {
        Calendar c = Calendar.getInstance(Locale.getDefault());

        return c.getTimeInMillis() > this._endCampaign;
    }

    public boolean isInCampaignTime() {

        if (theCampaignIsOver()) {
            return false;
        }
        Calendar c = Calendar.getInstance(Locale.getDefault());

        return c.getTimeInMillis() >= this._startCampaign;
    }

    public boolean counterLessThenMaxIn24(Context ctx) {
        Context t = ctx;
        DatabaseOperations db = new DatabaseOperations(t);
        int timeToday = db.getDataForTheLast24hour(db, this._id);
        return this._maxIn24 > timeToday;
    }

    ;

    public boolean isInCampaignTimeHours() {
        boolean isTrue = false;
        if (this._validTime == null) {
            return true;
        } else {
            Calendar c = Calendar.getInstance(Locale.getDefault());
            int currentHour = c.get(Calendar.HOUR_OF_DAY);

            for (int i = 0; i < this._validTime.length; i++) {
                int from;
                int to;
                if (this._validTime[i][0] < this._validTime[i][1]) {
                    from = this._validTime[i][0];
                    to = this._validTime[i][1];
                } else {
                    from = this._validTime[i][1];
                    to = this._validTime[i][0];
                }
                if (from <= currentHour && to >= currentHour) return true;


            }
        }
        return false;


    }


    /**
     * if the ads is relavent to user acoording to this data
     *
     * @param lat1   latitude of the user
     * @param lon1   longitude of the user
     * @param male   true if we know that the user is male
     * @param female true if we know that the user is female
     *               if we don't know the gender male=false,female =false
     * @param age    the age of the user
     * @return if is relevant ads.
     */
    public boolean isRelevantAds(double lat1, double lon1, boolean male, boolean female, int age) {
        if (!this._valid) {
            return false;
        }
        if (this.theCampaignIsOver()) {
            this._valid = false;
            return false;
        }

        return this.isBoundaries(lat1, lon1);

    }
}
