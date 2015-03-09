package com.ringchash.dodot.aviad.ringuest;

import android.app.Activity;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.InputStream;

/**
 * Created by AVIAD on 1/11/2015.
 * this class is for the cupon activity
 */
public class Cupon extends Activity {

    int _w;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cupon);
        /**
         * download the image of the cupon
         */
        new DownloadImageTask((ImageView) findViewById(R.id.all_cupons))
                .execute(ConfigAppData.CUPON_URL);



        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        TextView t=(TextView)findViewById(R.id.cuppon_header);
        Typeface tfAlef;
        tfAlef = Typeface.createFromAsset(getAssets(), "fonts/alef.ttf");
        t.setTypeface(tfAlef);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.ringchash.dodot.aviad.ringchash.HELLO");
                startActivity(intent);

            }

            ;
        });


    }
    public Bitmap getResizedBitmap(Bitmap bm) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int newWidth = size.x;
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = scaleWidth;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
           result=getResizedBitmap(result);
            bmImage.setImageBitmap(result);

        }
    }
}
