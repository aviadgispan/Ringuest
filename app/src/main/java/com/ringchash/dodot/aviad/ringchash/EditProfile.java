package com.ringchash.dodot.aviad.ringchash;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;

/**
 * Created by AVIAD on 1/1/2015.
 */
public class EditProfile extends Activity {
    boolean _male=false;
    boolean _female=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        DatePicker dp = (DatePicker)
                findViewById(R.id.datePicker);
        dp.setCalendarViewShown(false);
        ImageButton btnMale=(ImageButton)findViewById(R.id.buttonMale);
        ImageButton btnFemale=(ImageButton)findViewById(R.id.buttonFemale);
        btnMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGender(true);

            };
        });
        btnFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGender(false);
            };
        });
     //   saveDataOfUser(this,"Aviad Gispan",1,"aviadgispan@gmail.com",23,9,1984);
    }
    public void setGender(boolean isMale){
        if(isMale){
            _male=true;
            _female=false;
            ImageButton btnMale=(ImageButton)findViewById(R.id.buttonMale);
            ImageButton btnFemale=(ImageButton)findViewById(R.id.buttonFemale);
            btnMale.setImageResource(R.drawable.man_chosen);
            btnFemale.setImageResource(R.drawable.woman);
        }else{
            _male=false;
            _female=true;
            ImageButton btnMale=(ImageButton)findViewById(R.id.buttonMale);
            ImageButton btnFemale=(ImageButton)findViewById(R.id.buttonFemale);
            btnMale.setImageResource(R.drawable.man);
            btnFemale.setImageResource(R.drawable.woman_chosen);
        }
    }
    /**
     * save data user in preference, gender (0 if female, if male 1 , other (unknown -1))
     */
    public static  void saveDataOfUser(Context c,String name,int gender,String email,int day,int month,int year){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor edit=sp.edit();
        edit.putString(ConfigAppData.USER_NAME,name);
        edit.putInt(ConfigAppData.USER_GENDER,gender);
        edit.putString(ConfigAppData.USER_EMAIL,email);
        edit.putInt(ConfigAppData.USER_BIRTH_YEAR,year);
        edit.putInt(ConfigAppData.USER_BIRTH_MONTH,month);
        edit.putInt(ConfigAppData.USER_BIRTH_DAY,day);
        edit.commit();
    }
}
