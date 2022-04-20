package com.example.mitconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class FullScreen extends AppCompatActivity {

    private static final int SPLASH_TIME = 500; //This is 4 seconds
    private final Handler handler = new Handler();
    boolean mobileYN = false;
    boolean testdata = false;
    private static final int PERMS_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        testdata=true;

        if (hasPermissions()){
        }
        else {
            requestPerms();
        }

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1)
            {
                mobileYN = android.provider.Settings.Global.getInt(this.getContentResolver(), "mobile_data", 1) == 1;
            }
            else{
                mobileYN = Settings.Secure.getInt(this.getContentResolver(), "mobile_data", 1) == 1;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mobileYN){
            testdata=false;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Priére de désactiver les données mobiles.")
                    .setTitle("ATTENTION ! ")
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_notification_clear_all)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    testdata=true;
                                    dialog.dismiss();
                                    Intent mySuperIntent = new Intent(FullScreen.this, MainActivity.class);
                                    startActivity(mySuperIntent);
                                    finish();
                                }
                            }
                    );
            AlertDialog alert = builder.create();
            alert.show();
        }else testdata=true;

        if(testdata){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mySuperIntent = new Intent(FullScreen.this, MainActivity.class);
                    startActivity(mySuperIntent);
                    finish();
                    handler.postDelayed(this, 100);
                }
            }, SPLASH_TIME);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    private boolean hasPermissions(){
        int res = 0;
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }
    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,PERMS_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allowed = true;

        switch (requestCode) {
            case PERMS_REQUEST_CODE:

                for (int res : grantResults) {
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed) {
            //user granted all permissions we can perform our task.
            // gotonext();
        } else{
            // we will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //Toast.makeText(this, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}