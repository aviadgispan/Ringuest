package com.ringchash.dodot.aviad.ringchash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
        Button continueBtn=(Button)findViewById(R.id.finishRegister);
        updateData();
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData();
                if(!isNeedToRegister()){

                    Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.HELLO");
                    startActivity(intent);
                    String str = getResources().getString(R.string.all_details_are_in);
                    makeToast(str);

                }else{

                    String str = getResources().getString(R.string.need_to_fill_all_details);

                    makeToast(str);
                }

            };
        });
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
        ImageButton backButton=(ImageButton)findViewById(R.id.back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNeedToRegister()){
                    Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.HELLO");
                    startActivity(intent);
                }


            };
        });

     //   saveDataOfUser(this,"Aviad Gispan",1,"aviadgispan@gmail.com",23,9,1984);
    }
    public void makeToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_LONG).show();
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

    public void saveData(){
        EditText nameEdit = (EditText) findViewById(R.id.nameEdit);
        String name=nameEdit.getText().toString();
        EditText emailEdit = (EditText) findViewById(R.id.emailText);

        String email=emailEdit.getText().toString();
        DatePicker date=(DatePicker)findViewById(R.id.datePicker);
        int day=date.getDayOfMonth();
        int month=date.getMonth();
        int year=date.getYear();
        int gender=-1;

        if(_male==false&&_female==false){
            return;
        }
        if(_female==true&&_male==false){
            gender=0;
        }
        if(_female==false&&_male==true){
            gender=1;
        }

        saveDataOfUser(this,name,gender, email, day,month,year);
    }
    public boolean updateData(){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        boolean isExistAllData=true;
        String name=sp.getString(ConfigAppData.USER_NAME,null);
        if(name!=null&&name.length()>0){
            EditText nameText = (EditText) findViewById(R.id.nameEdit);
            nameText.setText(name, TextView.BufferType.EDITABLE);

        }else{
            isExistAllData=false;
        }
        String email=sp.getString(ConfigAppData.USER_NAME,null);
        if(email!=null&&email.length()>0){
            EditText emailText = (EditText) findViewById(R.id.emailText);
            emailText.setText(email, TextView.BufferType.EDITABLE);
        }else{
            isExistAllData=false;
        }
        int gender=sp.getInt(ConfigAppData.USER_GENDER,-1);
        if(gender!=-1){
            if(gender==0){
                setGender(true);
            }else{
                setGender(false);
            }
        }else{
            isExistAllData=false;
        }
        int year=sp.getInt(ConfigAppData.USER_BIRTH_YEAR,-1);
        int month=sp.getInt(ConfigAppData.USER_BIRTH_MONTH,-1);
        int day=sp.getInt(ConfigAppData.USER_BIRTH_MONTH,-1);
        if(year==-1||month==-1||day==-1){
            isExistAllData=false;
        }else{
            DatePicker date=(DatePicker)findViewById(R.id.datePicker);
            date.updateDate(year, month, day);

        }

return isExistAllData;



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
