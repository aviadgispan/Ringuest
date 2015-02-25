package com.ringchash.dodot.aviad.ringchash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by AVIAD on 1/10/2015.
 */
public class Splash extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String termsUrl=sp.getString(ConfigAppData.TERMS_URL_SP,null);
        String cuponUrl=sp.getString(ConfigAppData.CUPON_URL_SP,null);
        int agurotPerRing=sp.getInt(ConfigAppData.PAY_FOR_RING_SP,-1);
        int sumOfMinMoneyBeforePaid=sp.getInt(ConfigAppData.MIN_FOR_GET_CASH_SP,-1);

        ConfigAppData.PAY_FOR_RING=0.05;
        ConfigAppData.MIN_FOR_GET_CASH=20;
        if(termsUrl!=null){
            ConfigAppData.TERMS_URL=termsUrl;
        }
        if(cuponUrl!=null){
            ConfigAppData.CUPON_URL=cuponUrl;
            Toast.makeText(this,cuponUrl,Toast.LENGTH_LONG).show();
        }
        if(agurotPerRing!=-1){
            ConfigAppData.PAY_FOR_RING=(double)agurotPerRing/100;

        }

        if(sumOfMinMoneyBeforePaid!=-1){
            ConfigAppData.MIN_FOR_GET_CASH=sumOfMinMoneyBeforePaid;
        }

        final Thread logoTimer = new Thread() {
            public void run() {
                try {
                    String intentString = "";
                    if (isNeedToRegister()) {

                        intentString = "com.ringchash.dodot.aviad.ringchash.LOGIN";
                    } else {
                        intentString = "com.ringchash.dodot.aviad.ringchash.HELLO";
                    }
                    sleep(2000);
                    Intent intent = new Intent(intentString);
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }


            }
        };
        logoTimer.start();


    }

    public boolean isNeedToRegister() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isExistAllData = true;
        String name = sp.getString(ConfigAppData.USER_NAME, null);
        if (!(name != null && name.length() > 0)) {
            isExistAllData = false;

        }
        String email = sp.getString(ConfigAppData.USER_NAME, null);
        if (!(email != null && email.length() > 0)) {
            isExistAllData = false;

        }
        String lastName = sp.getString(ConfigAppData.USER_LAST_NAME, null);
        if (!(lastName != null && lastName.length() > 0)) {
            isExistAllData = false;

        }
        int gender = sp.getInt(ConfigAppData.USER_GENDER, -1);
        if (gender == -1) {

            isExistAllData = false;
        }
        int year = sp.getInt(ConfigAppData.USER_BIRTH_YEAR, -1);
        int month = sp.getInt(ConfigAppData.USER_BIRTH_MONTH, -1);
        int day = sp.getInt(ConfigAppData.USER_BIRTH_MONTH, -1);
        if (year == -1 || month == -1 || day == -1) {
            isExistAllData = false;
        }
        return !isExistAllData;
    }
}
