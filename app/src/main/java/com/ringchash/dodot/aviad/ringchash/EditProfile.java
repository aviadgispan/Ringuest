package com.ringchash.dodot.aviad.ringchash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
        // USER_PHONE_NUMBER
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        if(sp.getString(ConfigAppData.USER_PHONE_NUMBER,null)==null){
            TelephonyManager tel=(TelephonyManager)getSystemService(Context.TELECOM_SERVICE);


            if(tel!=null&&tel.getLine1Number()!=null){
                String str=tel.getLine1Number().toString();
                SharedPreferences.Editor edit=sp.edit();
                edit.putString(ConfigAppData.USER_PHONE_NUMBER,str);
                edit.commit();
            }
        }




        DatePicker dp = (DatePicker)
                findViewById(R.id.datePicker);
        dp.setCalendarViewShown(false);
        ImageButton btnMale=(ImageButton)findViewById(R.id.buttonMale);
        ImageButton btnFemale=(ImageButton)findViewById(R.id.buttonFemale);
        Button continueBtn=(Button)findViewById(R.id.finishRegister);
        updateData();

        int[] arrOfTextId={R.id.profile_edit_header,R.id.name_text,R.id.birth_text,R.id.gender_text,R.id.email_textView};

        ///nameEdit emailText
        int[] arrOfEditId={R.id.nameEdit,R.id.emailText};

        Typeface tfAlef;
        tfAlef = Typeface.createFromAsset(getAssets(), "fonts/alef.ttf");

        for(int i=0;i<arrOfTextId.length;i++){
            TextView t=(TextView)findViewById(arrOfTextId[i]);
            t.setTypeface(tfAlef);
        }
        for(int i=0;i<arrOfEditId.length;i++){
            EditText t=(EditText)findViewById(arrOfEditId[i]);
            t.setTypeface(tfAlef);
        }
        continueBtn.setTypeface(tfAlef);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveData();
                if(!isNeedToRegister()){
                    startRunIfNeed();
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
    public void startRunIfNeed(){
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstRun=sp.getBoolean(ConfigAppData.FIRST_RUN,true);
        if(firstRun){
            SharedPreferences.Editor edit=sp.edit();
            edit.putBoolean(ConfigAppData.FIRST_RUN,false);
            edit.commit();
            this.startService(new Intent(this,ManagerService.class));
        }
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
        if(email==null||email.length()==0||name==null||name.length()==0){
            return;
        }
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
        if(name!=null&&name.length()>0){

            edit.putString(ConfigAppData.USER_NAME,name);
        }

        edit.putInt(ConfigAppData.USER_GENDER,gender);
        if(email!=null&&email.length()>0){
            edit.putString(ConfigAppData.USER_NAME,email);
        }
        edit.putInt(ConfigAppData.USER_BIRTH_YEAR,year);
        edit.putInt(ConfigAppData.USER_BIRTH_MONTH,month);
        edit.putInt(ConfigAppData.USER_BIRTH_DAY,day);
        edit.commit();
        int id=sp.getInt(ConfigAppData.USER_ID,-1);
        String fb=sp.getString(ConfigAppData.USER_FB_ID,"");
        String phoneNumber=sp.getString(ConfigAppData.USER_PHONE_NUMBER,"");

        updateUserData(c,id,name,gender, email,day, month,year,fb,phoneNumber);

    }
    public static void updateUserData(final Context c,int userId,String name,int gender,String email,int day,int month,int year,String fb,String phoneNumber){
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(ConfigAppData.UPDATE_USER_FROM_SERVER);
        final List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(9);
        nameValuePair.add(new BasicNameValuePair("userID",userId+""));
        nameValuePair.add(new BasicNameValuePair("name",name));
        nameValuePair.add(new BasicNameValuePair("gender",gender+""));
        nameValuePair.add(new BasicNameValuePair("email",email));
        nameValuePair.add(new BasicNameValuePair("day",day+""));
        nameValuePair.add(new BasicNameValuePair("phoneNumber",phoneNumber));
        nameValuePair.add(new BasicNameValuePair("year",year+""));
        nameValuePair.add(new BasicNameValuePair("month",month+""));
        nameValuePair.add(new BasicNameValuePair("fbId",fb));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

                } catch (UnsupportedEncodingException e)
                {
                    e.printStackTrace();
                }
                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    // write response to log
                    //	String str=response.getEntity().toString();
                    HttpEntity entity = response.getEntity();
                    //str=EntityUtils.getContentMimeType(entity);
                    String inputLine ;
                    String str="";
                    BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                    try {
                        while ((inputLine = in.readLine()) != null) {
                            str=str+inputLine;

                        }
                        // AnsFromServer
                        Gson gson = new Gson();
                        AnsFromServer ans = gson.fromJson(str, AnsFromServer.class);
                        if(ans.id>-1){
                            SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(c);
                            int id=sp.getInt(ConfigAppData.USER_ID,-1);
                            if(id==-1){
                                SharedPreferences.Editor edit=sp.edit();
                                edit.putInt(ConfigAppData.USER_ID,ans.id);
                                edit.commit();
                            }
                        }
                        Log.d("from server ans :", ans.id+"");
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                } catch (ClientProtocolException e) {
                    // Log exception
                    e.printStackTrace();
                } catch (IOException e) {
                    // Log exception
                    e.printStackTrace();
                }




            }
        });
        t.start();


    }

}
