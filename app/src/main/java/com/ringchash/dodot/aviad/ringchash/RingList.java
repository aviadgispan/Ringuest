package com.ringchash.dodot.aviad.ringchash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by AVIAD on 1/12/2015.
 */
public class RingList extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rings);
        updateRing();
        ImageButton backButton=(ImageButton)findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.HELLO");
                startActivity(intent);


            };
        });


    }
    public void updateRing(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.ringsList);



        layout.setOrientation(LinearLayout.VERTICAL);

        String[] fileNameArr=AlarmReceiver.getAllFileNameInRingCash();


//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"
//        android:paddingTop="10dp"
//        android:paddingBottom="10dp"
//        android:paddingRight="10dp"
//        android:paddingLeft="10dp"
//        android:drawableRight="@drawable/select_sound_hello"
//        android:drawableLeft="@drawable/arrow_hello"
//        android:text="@string/choose_ring"
//        android:ems="10"
//        android:textSize="32sp"
//        android:id="@+id/choose_ring"
//        android:layout_gravity="center_horizontal"
//        android:background="@null"
        if(fileNameArr!=null){
            Arrays.sort(fileNameArr);
            for(int i=0;i<fileNameArr.length;i++){
                Button btn = new Button(this);
                btn.setText(fileNameArr[i]);
                btn.setBackgroundColor(Color.parseColor("#ff9b8b"));

                final String FILE_NAME=fileNameArr[i];
                layout.addView(btn);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatePlayStatus(FILE_NAME);
                    };
                });

            }
        }
    }
    public void updatePlayStatus(String fileName){

    }
    public boolean getStatus(String fileName){
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);

        AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
        if (adsSummaryManager == null) {
            return false;
        }
        adsSummaryManager._context = this;

        for (int i = 0; i < adsSummaryManager._arr.length; i++) {
            if(fileName.equals(adsSummaryManager._arr[i]._fileName)){
                return adsSummaryManager._arr[i]._isOn;
            }
        }
        return false;
    };

}
