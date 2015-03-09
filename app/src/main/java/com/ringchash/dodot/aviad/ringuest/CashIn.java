package com.ringchash.dodot.aviad.ringuest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by AVIAD on 1/11/2015.
 * this activity is for asking money for the user
 */

public class CashIn extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_in);
        ImageButton backButton=(ImageButton)findViewById(R.id.back);

        int[] arrOfTextId={R.id.get_cash_header,R.id.contact_me,R.id.cash_now_in_shekel,R.id.cash_now_in_bill,R.id.cash_now_in_bill_sum};
        Typeface tfAlef;
        tfAlef = Typeface.createFromAsset(getAssets(), "fonts/alef.ttf");

        for(int i=0;i<arrOfTextId.length;i++){
            TextView t=(TextView)findViewById(arrOfTextId[i]);
            t.setTypeface(tfAlef);
        }
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);



        int takbulUnPaid=sp.getInt(ConfigAppData.TAKBUL_ALL_RING_THAT_UNPAID,0);

/**
 * fix the string to be only 2 chars after the point
 */
        double sumOfAllMoneyFromRingUnPaid=(double)takbulUnPaid/100;
        sumOfAllMoneyFromRingUnPaid=sumOfAllMoneyFromRingUnPaid*100;
        sumOfAllMoneyFromRingUnPaid=Math.round(sumOfAllMoneyFromRingUnPaid);
        sumOfAllMoneyFromRingUnPaid=sumOfAllMoneyFromRingUnPaid/100;

        TextView moneyUnPaid=(TextView)findViewById(R.id.cash_now_in_bill_sum);
        moneyUnPaid.setText(" "+sumOfAllMoneyFromRingUnPaid);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.HELLO");
                startActivity(intent);

            };
        });
        Button sendMeMoney=(Button)findViewById(R.id.send_me_btn_cashin);
        sendMeMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    if(canGetMoney()){
                        askForMoney();
                    }

                }else{
                    makeToastError();
                }


            };
        });


    }
    public void makeToastError(){
        // network_error
        String str=getResources().getString(R.string.network_error);
        Toast.makeText(this,str,Toast.LENGTH_LONG).show();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * is user can get money (have the minimum for payed)
     * @return
     */
    public boolean canGetMoney(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int unpaid=sp.getInt(ConfigAppData.COUNTER_ALL_RING_THAT_UNPAID,-1);

        if(unpaid==-1||unpaid*ConfigAppData.PAY_FOR_RING<ConfigAppData.MIN_FOR_GET_CASH){
            String str=getResources().getString(R.string.not_enough_money_to_redeem);
            str=str+ConfigAppData.MIN_FOR_GET_CASH;
            str=str+" ";
            str=str+getResources().getString(R.string.shekel);
            Toast.makeText(this,str,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
//ConfigAppData.COUNTER_ALL_RING_THAT_UNPAID
    };

    /**
     * the action of asking money , only if have more then the minimum
     */
    public void askForMoney() {
        Context context=this;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong(ConfigAppData.LAST_UPDATE_SERVER_DATA_FROM_USER, System.currentTimeMillis());
        edit.commit();
        int user_id = getUserId();
        getDataFromServerAfterTime(user_id);
    }

    /**
     * @return the user id from the sharedPreferance
     */
    private int getUserId() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getInt(ConfigAppData.USER_ID, -1);
    };
    public void getDataFromServerAfterTime(int userId) {
        DatabaseOperations db = new DatabaseOperations(this);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        long maxTime = -1;

        long lastUpdate = sp.getLong(ConfigAppData.LAST_UPDATE, 0);
     //   Log.d("LAST UPDATE: ", "" + lastUpdate);
        ArrayList<EventSQlite> arr = db.getAllAfterTime(db, lastUpdate);
        if (arr == null || arr.size() == 0) {
          //  Log.d("SP :=> ", "no new was found form: " + lastUpdate);
            askUpdateAction();
            return;
        }
        Log.d("SP :=>SIZE OF Arr ", "Size " + arr.size());
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i)._adsTime > maxTime) {
                maxTime = arr.get(i)._adsTime;
            }
        }
        Collections.sort(arr, EventSQlite.EventSQliteComparator);
        int sizeOfStringArr = 1;
        if (arr.size() > 1) {
            for (int i = 1; i < arr.size(); i++) {
                if (arr.get(i)._adsID != arr.get(i - 1)._adsID) {
                    sizeOfStringArr++;
                }
            }
        }
        String[][] updateArr = new String[sizeOfStringArr][2];
        int[] idArr = new int[sizeOfStringArr];
        idArr[0] = arr.get(0)._adsID;
        if (arr.size() > 1) {
            int counter = 1;
            for (int i = 1; i < arr.size(); i++) {
                if (arr.get(i)._adsID != arr.get(i - 1)._adsID) {
                    idArr[counter] = arr.get(i)._adsID;
                    counter++;
                }
            }
        }
        int counterInnerDataPerApp = 0;
        for (int k = 0; k < idArr.length; k++) {
            updateArr[k][0] = idArr[k] + "";
            updateArr[k][1] = "";
            counterInnerDataPerApp = 0;
            for (int i = 0; i < arr.size(); i++) {
                if (arr.get(i)._adsID == idArr[k]) {
                    String regex = "#";
                    if (counterInnerDataPerApp == 0) {
                        regex = "";
                    }
                    updateArr[k][1] = updateArr[k][1] + regex + arr.get(i)._adsTime + "&" + arr.get(i)._gps;
                    counterInnerDataPerApp++;
                }
            }
        }
        String dataToServer = "";
        for (int i = 0; i < idArr.length; i++) {
            if (i > 0) {
                dataToServer = "@" + dataToServer;
            }
            dataToServer = dataToServer + updateArr[i][0] + "$" + updateArr[i][1];
        }


        saveAllEventToServer(userId, dataToServer);
        // if there was a call to update( save to shared preference)
        // the time of the last update
        if (maxTime >= 0) {

            SharedPreferences.Editor edit = sp.edit();
            edit.putLong(ConfigAppData.LAST_UPDATE, maxTime);
            edit.commit();
            Log.d("new UPDATE: ", "" + maxTime);

        }
    }

    /**
     * save to server all event of rings
     * @param userId the user id
     * @param data the data to send to server
     */
    public void saveAllEventToServer(long userId, String data) {



        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(ConfigAppData.UPDATE_ADS_DATA_HISTORY_FROM_USER);
        final List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
        nameValuePair.add(new BasicNameValuePair("userID", userId + ""));
        nameValuePair.add(new BasicNameValuePair("data", data));
        //Encoding POST data
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    // write response to log
                    //	String str=response.getEntity().toString();
                    HttpEntity entity = response.getEntity();
                    //str=EntityUtils.getContentMimeType(entity);
                    String inputLine;
                    String str = "";
                    BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                    try {
                        while ((inputLine = in.readLine()) != null) {
                            str = str + inputLine;
                        }
                        in.close();
                        askUpdateAction();
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
    public void askUpdateAction(){
        Calendar c = Calendar.getInstance(Locale.getDefault());
        final Long current=c.getTimeInMillis();
        int id=getUserId();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String adsHistoryManagerUntilGettingCashString=sp.getString(ConfigAppData.ADS_HISTORY_MANAGER_UNTIL_GETTING_CASH,null);
        if(adsHistoryManagerUntilGettingCashString==null){
            return;
        }

        AdsHistoryManagerUntilGettingCash a;
        Gson gson=new Gson();
        a = gson.fromJson(adsHistoryManagerUntilGettingCashString, AdsHistoryManagerUntilGettingCash.class);
        String str=a.getAllRelevantAdsFromTime();
        if(str==null){
            return;
        }

        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(ConfigAppData.UPDATE_ASK_FOR_MONEY);
        final List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(3);
        nameValuePair.add(new BasicNameValuePair("userId", id + ""));
        nameValuePair.add(new BasicNameValuePair("time", current+""));
        nameValuePair.add(new BasicNameValuePair("appIdArrString",str));

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    // write response to log
                    //	String str=response.getEntity().toString();
                    HttpEntity entity = response.getEntity();
                    //str=EntityUtils.getContentMimeType(entity);
                    String inputLine;
                    String str = "";
                    BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                    try {
                        while ((inputLine = in.readLine()) != null) {
                            str = str + inputLine;
                        }
                        boolean isOk=true;
                        Gson gson=new Gson();
                        ValidAnsFromServer ans = gson.fromJson(str, ValidAnsFromServer.class);
                        if(ans.ok<=0){
                            isOk=false;
                        }
                        if(isOk){


                            updateAdsHistoryManagerUntilCashThatServerFinished(current);


                            Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.HELLO");
                            intent.putExtra("fromGotMoney","true");
                            startActivity(intent);

                        }
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

    public void makeToast(String text){

        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }
//    public void makeToastOfSuccessGettingMoney(){
//
//
//        String text=getResources().getString(R.string.ask_money_ok);
//        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
//    }
    public  void updateAdsHistoryManagerUntilCashThatServerFinished(long cur){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String adsHistoryManagerUntilGettingCashString=sp.getString(ConfigAppData.ADS_HISTORY_MANAGER_UNTIL_GETTING_CASH,null);
        if(adsHistoryManagerUntilGettingCashString==null){
            return;
        }

        AdsHistoryManagerUntilGettingCash a;
        Gson gson=new Gson();
        a = gson.fromJson(adsHistoryManagerUntilGettingCashString, AdsHistoryManagerUntilGettingCash.class);
        a.clear(cur);
        SharedPreferences.Editor edit=sp.edit();
        String str=gson.toJson(a);
        edit.putString(ConfigAppData.ADS_HISTORY_MANAGER_UNTIL_GETTING_CASH,str);
        edit.putInt(ConfigAppData.COUNTER_ALL_RING_THAT_UNPAID,0);
        edit.commit();
    };

}

