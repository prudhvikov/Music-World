package com.prudhvi.musicworld.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.prudhvi.musicworld.R;
import com.prudhvi.musicworld.Utils.Utilities;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
//
//    Boolean sync = true;
//    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {


            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Request for permissions")
                    .setMessage("For music player to work we need your permission to access files on your device.")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    1);
                        }
                    })
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                new PerformBackgroundTasks(this).execute("task");
        } else {
            (new Utilities(this)).showLongToast("Please enable permission from " +
                    "Settings > Apps > Noad Player > Permissions.");
        }

} // OnCreate Method Ends Here


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                new PerformBackgroundTasks(this).execute("tasks");

            } else {
                Toast.makeText(this, "Application needs permission to run. Go to Settings > Apps > " +
                        "Noad Player to allow permission.", Toast.LENGTH_SHORT).show();
                finish();
            }

        }
    }


    private static class PerformBackgroundTasks extends AsyncTask<String, Integer, Long> {

        private WeakReference<Activity> weakReference;
//        private Boolean sync;
        ProgressDialog p;

        PerformBackgroundTasks(Activity activity) {
            this.weakReference = new WeakReference<>(activity);
//            this.sync = sync;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p = new ProgressDialog(weakReference.get());
            p.setMessage("Please wait...");
            p.setIndeterminate(false);
            p.setCancelable(false);
            p.show();
        }

        @Override
        protected Long doInBackground(String... params) {
                  return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //setUpdatedTextView(values[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            weakReference.get().startActivity(new Intent(weakReference.get(), AllSongs.class));
            weakReference.get().finish();
            p.dismiss();
        }
    }



} // MainActivity Ends Here
