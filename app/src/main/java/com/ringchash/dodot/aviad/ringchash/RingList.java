package com.ringchash.dodot.aviad.ringchash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by AVIAD on 1/12/2015.
 */
public class RingList extends Activity{
    public static int _id = 1;
    public RingPlayData[] _ringPlayButtonData;
    public static String _currentPlay=null;
    public boolean _pause =false;
    MediaPlayer _mp ;
    public static int padding=15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rings);
        updateRing();
        ImageButton backButton=(ImageButton)findViewById(R.id.back);
        TextView headerChooseRing=(TextView)findViewById(R.id.header_choose_ring);
        Typeface tfAlef;
        tfAlef = Typeface.createFromAsset(getAssets(), "fonts/alef.ttf");
        headerChooseRing.setTypeface(tfAlef);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.HELLO");
                startActivity(intent);


            };
        });


    }


    // Returns a valid id that isn't in use
    public int findId(){
        _id=_id+5;
        int i=_id;
        View v = findViewById(i);

        while (v != null){
            v = findViewById(++_id);
        }
        return _id++;
    }

    public void addPlayRing(String fileName,int idIsPlay,int isOnId){
        RingPlayData r=new RingPlayData(fileName,idIsPlay,isOnId);
        if(this._ringPlayButtonData==null){
            this._ringPlayButtonData=new RingPlayData[1];
            this._ringPlayButtonData[0]=r;
        }else{
            RingPlayData[] newArr=new RingPlayData[this._ringPlayButtonData.length+1];
            for(int i=0;i<this._ringPlayButtonData.length;i++){
                newArr[i]=this._ringPlayButtonData[i];
            }
            newArr[newArr.length-1]=r;
            this._ringPlayButtonData=newArr;
        }

    }
    public void updateRing(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.ringsList);
        layout.setOrientation(LinearLayout.VERTICAL);
        String[] fileNameArr=AlarmReceiver.getAllFileNameInRingCash();
        Drawable d = getResources().getDrawable(R.drawable.play_rings);
        int hPlay = d.getIntrinsicHeight();
        int wPlay = d.getIntrinsicWidth();
        d = getResources().getDrawable(R.drawable.stop_rings);
        int hStop = d.getIntrinsicHeight();
        int wStop = d.getIntrinsicWidth();
        d = getResources().getDrawable(R.drawable.v_rings);
        int vH = d.getIntrinsicHeight();
        int vW = d.getIntrinsicWidth();
        int wBside=Math.max(Math.max(wPlay,vW),wStop)+30;
        int hBside=Math.max(Math.max(hPlay,vH),hStop)+30;
        LinearLayout l=(LinearLayout)findViewById(R.id.ringsList);
        int left=l.getPaddingLeft();
        int right=l.getPaddingRight();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x-left-right-wBside*2;

        if(fileNameArr!=null){
            Arrays.sort(fileNameArr);
            for(int i=0;i<fileNameArr.length;i++){
                LinearLayout layoutInSide =new LinearLayout(this);
                layoutInSide.setOrientation(LinearLayout.HORIZONTAL);
                Button btn = new Button(this);
                btn.setText(fileNameArr[i]);
                btn.setBackgroundColor(Color.parseColor("#ff9b8b"));
                final String FILE_NAME=fileNameArr[i];
                ImageButton btnPlay = new ImageButton(this);
                int idIsPlay=findId();
                btnPlay.setId(idIsPlay);
                ImageButton btnIsOn = new ImageButton(this);
                int idIsOn=findId();
                btnIsOn.setId(idIsOn);

                if(getStatus(fileNameArr[i])){
                    btnIsOn.setImageResource(R.drawable.v_rings);
                }else{
                    btnIsOn.setImageResource(R.drawable.empty);
                }
                btnIsOn.setBackgroundColor(Color.parseColor("#ff9b8b"));
                addPlayRing(fileNameArr[i], idIsPlay, idIsOn);
                btnPlay.setBackgroundColor(Color.parseColor("#ff9b8b"));
                btnPlay.setImageResource(R.drawable.play_rings);
                btnPlay.setLayoutParams(new LinearLayout.LayoutParams(wBside, hBside));
                btnIsOn.setLayoutParams (new LinearLayout.LayoutParams(wBside, hBside));
                btn.setLayoutParams (new LinearLayout.LayoutParams( width, hBside));
                btn.setTextColor(getResources().getColor(R.color.text_color_profile));

                Typeface tfAlef;
                tfAlef = Typeface.createFromAsset(getAssets(), "fonts/alef.ttf");
                btn.setTypeface(tfAlef);

                layoutInSide.addView(btnPlay);
                layoutInSide.addView(btn);
                layoutInSide.addView(btnIsOn);
                layout.addView(layoutInSide);
                ImageView lineImage=new ImageView(this);
                lineImage.setImageResource(R.drawable.line_rings_list);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, padding*2);
                params.gravity= Gravity.CENTER;
                lineImage.setLayoutParams(params);
                layout.addView(lineImage);
                btnPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updatePlay(FILE_NAME);
                    };
                });
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateIsOnStatus(FILE_NAME);


                    };
                });
                btnIsOn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateIsOnStatus(FILE_NAME);


                    };
                });

            }
        }
    }

    public void updateIsOnStatus(String fileName){
        Gson gson = new Gson();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String adsSummaryManagerString = sp.getString(ConfigAppData.ADS_SUMMARY_MANEGER, null);

        AdsSummaryManager adsSummaryManager = gson.fromJson(adsSummaryManagerString, AdsSummaryManager.class);
        if (adsSummaryManager == null) {
            return;
        }
        adsSummaryManager._context = this;

        for (int i = 0; i < adsSummaryManager._arr.length; i++) {
            if(fileName.equals(adsSummaryManager._arr[i]._fileName)){
                adsSummaryManager._arr[i]._isOn=!adsSummaryManager._arr[i]._isOn;
            }
        }
        adsSummaryManager._context = null;
        SharedPreferences.Editor edit=sp.edit();
        String str = new Gson().toJson(adsSummaryManager);
        edit.putString(ConfigAppData.ADS_SUMMARY_MANEGER, str);
        edit.commit();
        if(this._ringPlayButtonData!=null){
            for(int i=0;i<this._ringPlayButtonData.length;i++){
                if(fileName.equals(this._ringPlayButtonData[i]._fileName)){
                    ImageButton b=(ImageButton)this.findViewById(this._ringPlayButtonData[i]._isOnId);
                    if(getStatus(fileName)){
                        b.setImageResource(R.drawable.v_rings);
                    }else{
                        b.setImageResource(R.drawable.empty);

                    }
                }
            }
        }


    }

    public void playSound(String filename){
        File f=RingtoneControl.getFileByName(filename);
        _mp= new MediaPlayer();
        if(f!=null){
            try {
                _mp.setDataSource(f.getAbsolutePath());
                _mp.prepare();
                _mp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }



    }
    public void updatePlay(String fileName){
        if(_currentPlay!=null){
            if(_currentPlay.equals(fileName)){
                for(int i=0;i<this._ringPlayButtonData.length;i++){
                    if(_currentPlay.equals(this._ringPlayButtonData[i]._fileName)){
                        ImageButton b=(ImageButton)this.findViewById(this._ringPlayButtonData[i]._isPlayId);
                        b.setImageResource(R.drawable.play_rings);
                        if(_mp.isPlaying()){
                            _mp.release();
                        }
                    }
                }
                _currentPlay=null;
            }else{
                if(_mp.isPlaying()){
                    _mp.release();
                }
                for(int i=0;i<this._ringPlayButtonData.length;i++){
                    if(_currentPlay.equals(this._ringPlayButtonData[i]._fileName)){
                        ImageButton b=(ImageButton)this.findViewById(this._ringPlayButtonData[i]._isPlayId);
                        b.setImageResource(R.drawable.play_rings);
                    }
                    if(fileName.equals(this._ringPlayButtonData[i]._fileName)){
                        ImageButton b=(ImageButton)this.findViewById(this._ringPlayButtonData[i]._isPlayId);
                        b.setImageResource(R.drawable.stop_rings);
                        playSound(fileName);
                    }
                }
                _currentPlay=fileName;
            }
        }else{
            _currentPlay=fileName;
            for(int i=0;i<this._ringPlayButtonData.length;i++){

                if(fileName.equals(this._ringPlayButtonData[i]._fileName)){
                    ImageButton b=(ImageButton)this.findViewById(this._ringPlayButtonData[i]._isPlayId);
                    b.setImageResource(R.drawable.stop_rings);
                    playSound(fileName);
                }
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(_mp!=null){

            if(_currentPlay!=null){
                if(_mp.isPlaying()){
                    updatePlay(_currentPlay);
                    _currentPlay=null;
                }
            }

        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(_mp!=null){
            if(_mp.isPlaying()){
                _mp.release();
            }
        }

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
