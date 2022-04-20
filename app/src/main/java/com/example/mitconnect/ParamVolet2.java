package com.example.mitconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ParamVolet2 extends AppCompatActivity {

    ImageButton fermer,ouvret;
    Button suiv;
//    String strBinary;
//    String AA,CC, BB,DD;

    private TaskEsp taskEsp;
    private String ip;
    private final Handler handler = new Handler();
    CheckBox checkclose,checkBoxo;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param_volet2);

        fermer= findViewById(R.id.ferme1);
        ouvret=findViewById(R.id.ouvre1);
        suiv = findViewById(R.id.suivant2);
        checkclose=findViewById(R.id.checkBoxF);
        checkBoxo=findViewById(R.id.checkBoxO2);

        suiv.setAlpha(0.5f);
        suiv.setEnabled(false);
        checkBoxo.setEnabled(false);
        ip = "203.114.5.2";
    }

    @Override
    protected void onStart(){
        super.onStart();
        String roueCoudeuse = "0000";
        String s1 = roueCoudeuse + "0101";
        int decimalValue2 = Integer.parseInt(s1,2);
        taskEsp = new TaskEsp(ip + "/send?state=" + decimalValue2);
        taskEsp.execute();
    }
    @Override
    protected void onResume() {
        super.onResume();
     checkclose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkclose.isChecked()){
                    new AlertDialog.Builder(ParamVolet2.this)
                            .setTitle(android.R.string.dialog_alert_title)
                            .setMessage("Etes vous sur que le volet est totalement fermer?")
                            .setPositiveButton("oui", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which){
                                    // continue with delete
                                    dialog.dismiss();

                                    checkclose.setChecked(true);
                                    suiv.setAlpha(1.0f);
                                    suiv.setEnabled(true);
                                    //finish();
                                }
                            })
                            .setNegativeButton("non", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                    dialog.dismiss();
                                    checkclose.setChecked(false);
                                    suiv.setAlpha(0.5f);
                                    suiv.setEnabled(false);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert).show();
                }

            }
        });
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                taskEsp = new TaskEsp(ip + "/etat");
                taskEsp.execute();
                taskEsp = new TaskEsp(ip + "/eeprom");
                taskEsp.execute();
                // taskEsp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                handler.postDelayed(this, 2500);
            }
        }, 0);
        ouvret.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        taskEsp = new TaskEsp(ip + "/open?state=on");
                        taskEsp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        // PRESSED
                    }
                    return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP: {
                        taskEsp = new TaskEsp(ip + "/open?state=off");
                        taskEsp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        // RELEASED
                    }
                    return true;    //if you want to handle the touch event
                }
                return false;
            }
        });
        fermer.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //  releasePlayer();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        taskEsp = new TaskEsp(ip + "/close?state=on");
                        taskEsp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        // PRESSED
                    }
                    return true;    // if you want to handle the touch event
                    case MotionEvent.ACTION_UP: {
                        taskEsp = new TaskEsp(ip + "/close?state=off");
                        taskEsp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        // RELEASED
                    }
                    return true; // if you want to handle the touch event
                }
                return false;
            }
        });
        suiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent act1 = new Intent(getApplicationContext(), ConfigTerminer.class);
                startActivity(act1);
            }
        });
    }
    private class TaskEsp extends AsyncTask<Void, Void, String> {
        final String server;
        TaskEsp(String server) {
            this.server = server;
        }
        @Override
        protected String doInBackground(Void... params) {
            final String p = "http://" + server;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
            String serverResponse = "";
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(p).openConnection());
                httpURLConnection.setConnectTimeout(2000);
                httpURLConnection.setReadTimeout(2000);

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    InputStream inputStream;
                    inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader =
                            new BufferedReader(new InputStreamReader(inputStream));
                    serverResponse = bufferedReader.readLine();

                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                serverResponse = e.getMessage();
            }
            return serverResponse;
        }
        @Override
        //*************************************************************************************//
        //**************************** Affichage des paramètres *******************************//
        //************************************************************************************//
        protected void onPostExecute(String s) {
            if (s != null) {
                Log.d("etttt",s);
                //Toast.makeText(MainActivity.this,s, Toast.LENGTH_SHORT).show();
                if (s.contains("failed to connect to /" + ip)) {
                } else {
                    if (s.contains("EROLLY:")){
                        String[] relayE = s.split(":");
                        if(relayE[1].equals("0")){
                            fermer.setBackgroundResource(0);
                            fermer.setImageResource(R.mipmap.bt001);
                        }
                        if (relayE[1].equals("1")){
                            fermer.setBackgroundResource(0);
                            fermer.setImageResource(R.mipmap.bbt1);
                        }
                        if(relayE[2].equals("0")){
                            ouvret.setBackgroundResource(0);
                            ouvret.setImageResource(R.mipmap.bt002);
                        }
                        if(relayE[2].equals("1")){
                            ouvret.setBackgroundResource(0);
                            ouvret.setImageResource(R.mipmap.bbt2);
                        }
                    }
                    /*if (s.contains("EEPROM:")){
                        String[] relayE1 = s.split(":");
                        String strB = relayE1[1];

                        int decimalValue1 = Integer.parseInt(strB);

                        strBinary = Integer.toBinaryString(decimalValue1);

                        int longueur = strBinary.length();

                        while (longueur < 8) {
                            strBinary = "0" + strBinary;
                            longueur++;
                        }

                        if ((!strBinary.isEmpty())) {
                            //Log.d("ettttbbbb", "" + strBinary);
                            AA = strBinary.substring(0, 4);  // ROUE
                            CC = strBinary.substring(4, 5);  //FORCAGE
                            BB = strBinary.substring(5, 6);  //PROG
                            DD = strBinary.substring(7, 8);  //R/L

                        }
                    }*/
                }
            }
        }
    }

}