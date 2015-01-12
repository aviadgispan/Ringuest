package com.ringchash.dodot.aviad.ringchash;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import static android.net.Uri.*;


public class Start extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Button downloadButton = (Button) findViewById(R.id.downloadButton);
        Button soundButton = (Button) findViewById(R.id.setSoundButton);

        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File k = new File("/RingCashFolder/CocaCola", "CocaCola.mp3");
                if (k == null) {
                    Toast.makeText(getApplicationContext(), "file is null", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "file is l" + k.length(), Toast.LENGTH_LONG).show();
                }
            }
        });
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText urlText = (EditText) findViewById(R.id.editUrl);
                String urlString = urlText.getText().toString();
                String isAvailable = "";
                if (isDownloadManagerAvailable(getApplicationContext())) {
                    isAvailable = "available";
                    boolean onlyWifi = true;
                    boolean userCanSeeDownload = false;
                    downloadFile(urlString, "CocaCola", onlyWifi, userCanSeeDownload, "Title", "space for description");
                    // downloadAction(urlString);
                } else {
                    isAvailable = "not available";
                }


                Toast.makeText(getApplicationContext(), isAvailable, Toast.LENGTH_LONG).show();

            }
        });
        Intent i=new Intent("com.ringchash.dodot.aviad.ringchash.EVENTADS");
        startActivity(i);

    }

    public void downloadFile(String url, String fileName, boolean onlyWifi, boolean userCanSeeDownload, String title, String description) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);
        request.setDescription(description);
        if (onlyWifi) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        }
        if (userCanSeeDownload) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        ///Environment.DIRECTORY_DOWNLOADS
        request.setDestinationInExternalPublicDir("/RingCashFolder", fileName);
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(this, "External SD card not mounted", Toast.LENGTH_LONG).show();
        }
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        // DownloadManager manger=(DownloadManager) getActivity().


    }
    public static void downloadFileStaticVersion(Context context,String url, String fileName, boolean onlyWifi, boolean userCanSeeDownload, String title, String description) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);
        request.setDescription(description);
        if (onlyWifi) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        }
        if (userCanSeeDownload) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        ///Environment.DIRECTORY_DOWNLOADS
        request.setDestinationInExternalPublicDir("/RingCashFolder", fileName);
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(context, "External SD card not mounted", Toast.LENGTH_LONG).show();
        }
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        // DownloadManager manger=(DownloadManager) getActivity().


    }
    public static boolean isDownloadManagerAvailable(Context context) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void downloadAction(String url) {

        DownloadManager.Request request = new DownloadManager.Request(parse(url));
        request.setDescription("Ring cash test");
        request.setTitle("Ring cash");
// in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "/data/data/com.ringchash.dodot.aviad.ringchash");
//
//// get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the hello; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
