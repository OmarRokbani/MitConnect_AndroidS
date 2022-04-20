package com.example.mitconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EnvoiRemotLocal extends AppCompatActivity {

    int Int_Roue_Codeuse , Int_Forcage, Int_RemotLocal;
    int Int_Prog;
    String strBinary;
    boolean stop = false;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switchprog , switchfor;
    String str1="0", str2="0", str3="0", str4="0";
    TextView displayInteger;
    int minteger;
    Button plus , moin;
    CheckBox remot , local ;
    ImageButton back;
    private Toast toast;

    int recoi_2;
    String rc1;

    private TaskEsp taskEsp;
    private String ip;
    private final Handler handler2 = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envoi_remot_local);

        switchprog = findViewById(R.id.switchprog);
        switchfor = findViewById(R.id.switchforc);
        displayInteger = findViewById(R.id.integer_number);
        moin =findViewById(R.id.decrease);
        plus = findViewById(R.id.increase);
        remot = findViewById(R.id.remote);
        local = findViewById(R.id.locale);
        back =findViewById(R.id.back);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        ip = "203.114.5.2";

        switchfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                stop=true;
                boolean checked = ((Switch) v).isChecked();
                if (checked){ str1 ="1"; }
                else{ str1 ="0"; }
            }
        });
        switchprog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop=true;
                boolean checked = ((Switch) v).isChecked();
                if (checked){
                    str2 = "1"; }
                else{ str2="0"; }
            }
        });
        remot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                stop=true;
                if(remot.isChecked()){
                    local.setChecked(false);
                }
            }
        });
        local.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                stop=true;
                if (local.isChecked()){
                    remot.setChecked(false);
                }
            }
        });
    }
    public void sendBtn(View view){
        if (remot.isChecked()){ str4= "1";}
        if (local.isChecked()){ str4= "0";}

        String c1 = displayInteger.getText().toString();
        int d =Integer.parseInt(c1);
        rc1 =Integer.toBinaryString(d);
        String s1 =rc1 + str1 + str2 + str3+ str4;
        int decimalValue1 = Integer.parseInt(s1,2);

        if (!s1.equals("")){
            taskEsp = new TaskEsp(ip + "/send?state=" + decimalValue1);
            taskEsp.execute();
        }
        onBackPressed();
    }
    @Override
    protected void onResume(){
        super.onResume();
        handler2.postDelayed(new Runnable(){
            @Override
            public void run(){
                taskEsp = new TaskEsp(ip + "/EtatRollyO");
                taskEsp.execute();
                // taskEsp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                handler2.postDelayed(this, 2500);
            }
        }, 0);  //the time is in miliseconds
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                final Intent act3 = new Intent(getApplicationContext(), Parametre.class);
                startActivity(act3);
                finish();
            }
        });
    }
    @Override
    protected void onPause(){
        super.onPause();
        handler2.removeCallbacksAndMessages(null);
        toast.cancel();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        handler2.removeCallbacksAndMessages(null);
        toast.cancel();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        final Intent act3 = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(act3);
        finish();
    }
    public void decreaseInteger(View view){
        stop=true;
        minteger = minteger - 1;
        display(minteger);
    }
    public void increaseInteger(View view) {
        stop=true;
        minteger = minteger + 1;
        display(minteger);
    }
    private void display(int number) {
        stop=true;
        if ((number <= 9 && number>0 )){
            displayInteger.setText("" + number);
            moin.setEnabled(true);
            plus.setEnabled(true);
        }
        else  if(number>9){
            plus.setEnabled(false);
            moin.setEnabled(true);
        }
        else if(number<=0){
            displayInteger.setText("" + number);
            plus.setEnabled(true);
            moin.setEnabled(false);
        }
    }
    //******************
    private class TaskEsp extends AsyncTask<Void, Void, String> {
        final String server;
        TaskEsp(String server) {this.server = server;}
        @Override
        protected String doInBackground(Void... params){
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
            } catch (IOException e){
                e.printStackTrace();
                serverResponse = e.getMessage();
            }
            return serverResponse;
        }
        protected void onPostExecute(String s){
            if (s != null){
                //Toast.makeText(MainActivity.this,s, Toast.LENGTH_SHORT).show();
                if (s.contains("failed to connect to /" + ip)){
                } else {
                    if (s.contains("EROLLYO:")) {
                        if (!stop) {
                            String[] relayE = s.split(":");

                            String str = relayE[1];

                            //************************************************
                            String Q = str.substring(48, 51);   //recoi_2
                            Q = Q.replace("*", "");
                            recoi_2 = Integer.parseInt(Q);
                            //************************************************

                            strBinary = Integer.toBinaryString(recoi_2);

                            int longueur = strBinary.length();

                            while (longueur < 8) {
                                strBinary = "0" + strBinary;
                                longueur++;
                            }

                            if ((!strBinary.isEmpty())) {
                                Log.d("ettttbbbb", "" + strBinary);

                                String AA = strBinary.substring(0, 4);  // ROUE
                                String CC = strBinary.substring(4, 5);  //FORCAGE
                                String BB = strBinary.substring(5, 6);  //PROG
                                String DD = strBinary.substring(7, 8);  //R/L

                                int AAd = Integer.parseInt(AA);// ROUE
                                int CCd = Integer.parseInt(CC);//FORCAGE
                                int BBd = Integer.parseInt(BB);//PROG
                                int DDd = Integer.parseInt(DD);  //R/L

                                //************************************************ VALEUR STATE
                                Int_Roue_Codeuse = Integer.parseInt(String.valueOf(AAd), 2);
                                displayInteger.setText(String.valueOf(Int_Roue_Codeuse));
                                minteger = Int_Roue_Codeuse;
                                Int_Forcage = Integer.parseInt(String.valueOf(CCd));
                                switchfor.setChecked(Int_Forcage == 1);
                                Int_Prog = Integer.parseInt(String.valueOf(BBd));
                                switchprog.setChecked(Int_Prog == 1);

                                Int_RemotLocal = Integer.parseInt(String.valueOf(DDd));
                                remot.setChecked(Int_RemotLocal == 1);
                                local.setChecked(Int_RemotLocal == 0);
                            }
                        }
                    }
                }
            }
        }
    }
}