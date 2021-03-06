package com.ringchash.dodot.aviad.ringuest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by AVIAD on 1/10/2015.
 */
public class Hello extends Activity {
    public void makeToastOfSuccessGettingMoney(){


        String text=getResources().getString(R.string.ask_money_ok);
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hello);
        Intent iin=getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            String j =(String) b.get("fromGotMoney");
            if(j!=null){
                if(j.equals("true")){
                    makeToastOfSuccessGettingMoney();
                }else{
                 //   Toast.makeText(this,"j is not null nut not true!",Toast.LENGTH_LONG).show();
                }

            }else{
               // Toast.makeText(this,"j  null nut not true!",Toast.LENGTH_LONG).show();
            }

        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sp.getString(ConfigAppData.USER_NAME, null);
        TextView tName=(TextView)findViewById(R.id.user_name);
        tName.setText(name);
        Button btn_choose_ring=(Button)findViewById(R.id.choose_ring);
        Button btn_account_status=(Button)findViewById(R.id.account_status);
        Button btn_profile=(Button)findViewById(R.id.profile);
        Button btn_get_my_money=(Button)findViewById(R.id.get_my_money);
        Button btn_coupon=(Button)findViewById(R.id.coupon);
        Button[] buttonArr={btn_choose_ring,btn_account_status,btn_profile,btn_get_my_money,btn_coupon};
        //btn_choose_ring
        Typeface tfAlef;
        tfAlef = Typeface.createFromAsset(getAssets(), "fonts/alef.ttf");
        for(int i=0;i<buttonArr.length;i++){
            buttonArr[i].setTypeface(tfAlef);

        }
        tName.setTypeface(tfAlef);
        TextView hello=(TextView)findViewById(R.id.hello);
        hello.setTypeface(tfAlef);
        btn_choose_ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.RING_LIST");
                startActivity(intent);
            };
        });
        btn_account_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.BILL");
                startActivity(intent);
            };
        });
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.EDIT_PROFILE");
                startActivity(intent);

            };
        });
        btn_get_my_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//

                if(isNetworkAvailable()){
                    Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.CASH_IN");
                    startActivity(intent);
                }else{

                    makeToastError();
                }
            };
        });
        btn_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.CUPON");
                    startActivity(intent);
                }else{

                    makeToastError();
                }


            };
        });

    }
//    public void tryA(){
//        AlarmReceiver a=new AlarmReceiver();
//        a._context=this;
//        a.updateFile(this);
//    }
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

}
