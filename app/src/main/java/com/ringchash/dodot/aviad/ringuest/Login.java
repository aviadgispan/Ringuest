package com.ringchash.dodot.aviad.ringuest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by AVIAD on 2/8/2015.
 */
public class Login extends Activity{
    boolean term_is_fine=false;
    boolean  isFetching = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
//        String APP_ID=getString(R.string.app_id);
//       fb=new Facebook(APP_ID);
        removeOldFile();
        TextView terms=(TextView)findViewById(R.id.terms_web);
//        String link="<a href=\"http://www.google.com\">Google</a>";
//        terms.setText( Html.fromHtml(link));
        String str=getResources().getString(R.string.text_terms_of_use2);

        String text = "<a href=\""+ConfigAppData.TERMS_URL+"\">"+str+"</a>";
        terms.setMovementMethod(LinkMovementMethod.getInstance());
        terms.setText(Html.fromHtml(text));
         // terms.setMovementMethod(LinkMovementMethod.getInstance());
        updateTerm();
        ImageButton termOfUser=(ImageButton)findViewById(R.id.term_of_use);
        ImageButton connect_user=(ImageButton)findViewById(R.id.connect_as_user);
      ImageButton connect_fb=(ImageButton)findViewById(R.id.connect_as_fb);



        termOfUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                term_is_fine=!term_is_fine;
                updateTerm();

            };
        });
        connect_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(term_is_fine){

                    startActivity(new Intent("com.ringchash.dodot.aviad.ringchash.EDIT_PROFILE"));
                }else{
                    termToast();
                }
            };
        });
        connect_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (term_is_fine) {
                    performFacebookLogin();
                } else {
                    termToast();
                }
            }

            ;
        });



    }
    public void updateTerm(){
        ImageButton termOfUser=(ImageButton)findViewById(R.id.term_of_use);
        if(term_is_fine){
            termOfUser.setImageResource(R.drawable.license_v);
        }else{
            termOfUser.setImageResource(R.drawable.license);
        }
    }
    public void termToast(){
        Toast.makeText(this,getString(R.string.need_user_consent),Toast.LENGTH_SHORT).show();
    }
    private void onClickLogin() {
        Session.openActiveSession(this, true, new Session.StatusCallback() {

            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    List<String> permissions = session.getPermissions();
                    Session.NewPermissionsRequest newPermissionsRequest = new         Session.NewPermissionsRequest(Login.this, Arrays.asList("email"));
                    session.requestNewReadPermissions(newPermissionsRequest);
                    // make request to the /me API
                    Request.newMeRequest(session, new Request.GraphUserCallback() {

                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {


                                String str="Hello " + user.getName() +"!";
                              //  makeToast(str);

                            }
                        }
                    }).executeAsync();
                }
            }
        });
    }

    public void makeToast(String str){
        Toast.makeText(this,str,Toast.LENGTH_LONG).show();
    }


    private void performFacebookLogin()
    {
        Log.d("FACEBOOK", "performFacebookLogin");

        String[] arr={"email","user_birthday"};
        final Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, Arrays.asList(arr));
        Session openActiveSession = Session.openActiveSession(this, true, new Session.StatusCallback()
        {
            @Override
            public void call(Session session, SessionState state, Exception exception)
            {
                Log.d("FACEBOOK", "call");
                if (session.isOpened() && !isFetching)
                {
                    Log.d("FACEBOOK", "if (session.isOpened() && !isFetching)");
                    isFetching = true;
                    session.requestNewReadPermissions(newPermissionsRequest);
                    Request getMe = Request.newMeRequest(session, new Request.GraphUserCallback()
                    {
                        @Override
                        public void onCompleted(GraphUser user, Response response)
                        {
                            Log.d("FACEBOOK", "onCompleted");
                            if (user != null)
                            {
                                Log.d("FACEBOOK", "user != null");
                                org.json.JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
                                String email = graphResponse.optString("email");
                                String gender = graphResponse.optString("gender");
                                String user_birthday = graphResponse.optString("birthday");
                                String id = graphResponse.optString("id");
                                String facebookName = user.getUsername();

                                if (email == null || email.length() < 0)
                                {


                                return;
                                }else{

                                    save(user.getFirstName(),user.getLastName(),gender,email,user_birthday,id);
//                                        String str="email "+email+" gender : "+gender+" b "+user_birthday+" user.getLastName()"+user.getLastName();
//                                        makeToast(str);


                                }

                            }
                        }
                    });
                    getMe.executeAsync();
                }
                else
                {
                    if (!session.isOpened())
                        Log.d("FACEBOOK", "!session.isOpened()");
                    else
                        Log.d("FACEBOOK", "isFetching");

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);

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
    public void save(String name,String lastName,String genderString,String email,String date,String id){
        int gender=-1;
        if(genderString.equals("male")){
            gender=1;
        }
        if(genderString.equals("female")){
            gender=0;
        }
        String[] arr = null;
        if(date!=null){
            arr=date.split("/");
        }
        int day=-1;
        int month=-1;
        int year=-1;
        if(arr!=null&&arr.length==3){
            day=Integer.parseInt(arr[1]);
             month=Integer.parseInt(arr[0]);
            /// in Android month from 0-11
             month=month-1;
             year=Integer.parseInt(arr[2]);
        }
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit=sp.edit();
        boolean allData=true;
        if(name!=null&&name.length()>0){
            edit.putString(ConfigAppData.USER_NAME,name);
        }else{
            allData=false;
        }
        if(lastName!=null&&lastName.length()>0){
            edit.putString(ConfigAppData.USER_LAST_NAME,lastName);
        }else{
            allData=false;
        }
        if(email!=null&&email.length()>0){
            edit.putString(ConfigAppData.USER_EMAIL,email);
        }else{
            allData=false;
        }
        if(gender>=0){
            edit.putInt(ConfigAppData.USER_GENDER, gender);
        }else{
            allData=false;
        }
        if(day>0&&month>0&&year>0){
            edit.putInt(ConfigAppData.USER_BIRTH_YEAR,year);
            edit.putInt(ConfigAppData.USER_BIRTH_MONTH,month);
            edit.putInt(ConfigAppData.USER_BIRTH_DAY,day);
        }else{
            allData=false;
        }
        edit.putString(ConfigAppData.USER_FB_ID, id);

        if(sp.getString(ConfigAppData.USER_PHONE_NUMBER,null)==null){
            TelephonyManager tel=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);


            if(tel!=null&&tel.getLine1Number()!=null){
                String str=tel.getLine1Number().toString();

                edit.putString(ConfigAppData.USER_PHONE_NUMBER,str);

            }
        }
        String phoneNumber=sp.getString(ConfigAppData.USER_PHONE_NUMBER,"");
        edit.commit();
        if(allData){
            EditProfile.updateUserData(this,-1,name,lastName,gender,email,day,month,year,id,phoneNumber);
            startRunIfNeed();
           startActivity(new Intent("com.ringchash.dodot.aviad.ringchash.HELLO"));
          //  startActivity(new Intent("com.ringchash.dodot.aviad.ringchash.EDIT_PROFILE"));

        }else{
            startActivity(new Intent("com.ringchash.dodot.aviad.ringchash.EDIT_PROFILE"));
        }


    }
    public void removeOldFile() {
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "RingCashFolder");
        if(yourDir==null||!yourDir.exists()){
            return;
        }

        File[] arr = BackActivity.getAllFileNameToRemove(this);
        if (arr == null || arr.length == 0) {
            Log.d("FILR m : ", "no old file founded");
            return;
        }
        for (int i = 0; i < arr.length; i++) {
            String name = arr[i].getName();
            Log.d("FILE  " + i + " : ", arr[i].getName());
            boolean isS = arr[i].delete();
        }
    }

}
