package com.example.mitconnect;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int Int_actuelle_1,Int_actuelle;
    int Int_limite_close,Int_limite_close_1;
    int Int_Roue_Codeuse,Int_Forcage,Int_Prog;
    int Int_Def_cap ,Int_Def_surcouple ,Int_Def_Prog;
    int recoi_2;

    int Int_AddCourant, Int_AddCourant_1, Int_Old;
    int Int_Old_1, Int_Pente, Int_Pente_1;

    int Minimum = 10;
    int Maximum,Instant;

    String pourcentage  , Affichage_Defaut;
    ImageButton ouvret, fermer, setting;

    TextView courant;

    TextView positionVolet,temp;
    TextView ledOrange, ledVert, ledRouge;

    private ProgressBar progressBar;

    private Toast toast;
    private TaskEsp taskEsp;
    private String ip;

    private final Handler handler2 = new Handler();
    private final Handler handler3 = new Handler();
    //****WIFI*******//
    private final String networkSSID = "PICMit123";
    private final BroadcastReceiver broadcastReceiver = new WifiBroadcastReceiver();
    private final IntentFilter intentFilter = new IntentFilter();
    private final WifiConfiguration conf = new WifiConfiguration();
    private WifiManager wifiManager;
    //************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        positionVolet = findViewById(R.id.posVolet);
        progressBar = findViewById(R.id.progress_bar);

        ouvret = findViewById(R.id.ouvre);
        fermer = findViewById(R.id.ferme);
        setting = findViewById(R.id.setting);
        temp = findViewById(R.id.temp);

        //courant = findViewById(R.id.courant);     // Courant

        ledOrange = findViewById(R.id.ledOrange);
        ledVert =findViewById(R.id.ledVert);
        ledRouge =findViewById(R.id.ledRouge);

        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        ip = "203.114.5.2";
        //****WIFI*******//
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        conf.SSID = "\"" + networkSSID + "\"";
        String networkPass = "123456789";
        conf.preSharedKey = "\""+ networkPass +"\"";
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        conf.priority = 1000;
        conf.status=WifiConfiguration.Status.ENABLED;
        wifiManager.addNetwork(conf);
        //*****************************************
    }

    @Override
    protected void onResume() {
        super.onResume();
        //*****************
        getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setAction(WifiManager.RSSI_CHANGED_ACTION);
            sendBroadcast(intent);
        }
        //*****************
        handler2.postDelayed(new Runnable() {
            @Override
            public void run(){
                taskEsp = new TaskEsp(ip + "/EtatRollyO");
                taskEsp.execute();
                taskEsp = new TaskEsp(ip + "/etat");
                taskEsp.execute();
                // taskEsp.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                handler2.postDelayed(this, 2500);
            }
        }, 0);
        /////////////////////////////////////////////////////////////////////////////////////////
        fermer.setOnTouchListener(new View.OnTouchListener() {
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
        //////////////////////////////////////////////////////////////////////////////////////////
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
        //*************************************************************************************//
        //*********************** Appui sur le bouto paramétrage ******************************//
        //************************************************************************************//
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent act = new Intent(getApplicationContext(), Parametre.class);
                startActivity(act);
                finish();
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        if(!checkConnectedToDesiredWifi()){
            wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
            wifiManager.startScan();
            assert wifiManager != null;
            wifiManager.setWifiEnabled(true);
            ConnectToDesiredWifi();
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        handler2.removeCallbacksAndMessages(null);
        toast.cancel();
    }
    @Override
    protected void onPause(){
        super.onPause();
        getApplicationContext().unregisterReceiver(broadcastReceiver);
        handler3.removeCallbacksAndMessages(null);
        toast.cancel();
    }
    /////////////////////////////////////////////////////////////////////////////////
    public class WifiBroadcastReceiver extends BroadcastReceiver {
        public WifiBroadcastReceiver(){}
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.State state = networkInfo.getState();
                if (state == NetworkInfo.State.CONNECTED) {
                    //connected
                    if (checkConnectedToDesiredWifi()) {
                        //Toast.makeText(context, "Connected to " + networkSSID, Toast.LENGTH_SHORT).show();
                    } else {
                        ConnectToDesiredWifi();
                        // Toast.makeText(context, "Connecting to " + networkSSID, Toast.LENGTH_SHORT).show();
                    }
                }
                if (state == NetworkInfo.State.DISCONNECTED) {
                    //disconnected
                    if (!wifiManager.isWifiEnabled()) {
                        wifiManager.setWifiEnabled(true);
                        while (!wifiManager.isWifiEnabled()) {
                            //Toast.makeText(context, "Activating Wifi", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //Toast.makeText(context, "Connecting to " + networkSSID, Toast.LENGTH_SHORT).show();
                    if (!checkConnectedToDesiredWifi()) {
                        ConnectToDesiredWifi();
                    }
                }
            }
        }
    }
    private void ConnectToDesiredWifi() {
        if (wifiManager.isWifiEnabled()) {
            wifiManager.startScan();
            @SuppressLint("MissingPermission") List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            if (list == null) {
                return;  //Or throw ex
            }
            for (WifiConfiguration i : list){
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    //wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();
                    break;
                }
            }
        }
    }
    private boolean checkConnectedToDesiredWifi() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        if (Build.VERSION.SDK_INT >= 23){
            return ssid != null && ssid.equals("\"" + networkSSID + "\"");
        } else {
            return ssid != null && ssid.equals(networkSSID);
        }
    }
    /////////////////////////////////////////////////////////////////////////////////
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
        //*************************************************************************************//
        //**************************** Affichage des paramètres *******************************//
        //************************************************************************************//
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                Log.d("etttt",s);
                //Toast.makeText(MainActivity.this,s, Toast.LENGTH_SHORT).show();
                if (s.contains("failed to connect to /" + ip)) {
                } else {
                    if (s.contains("EROLLYO:")) {
                        String[] relayE = s.split(":");
                        //Toast.makeText(MainActivity.this,relayE[1], Toast.LENGTH_SHORT).show();
                        Log.d("erollyy=", relayE[1]);
                        String str = relayE[1];

                        String A = str.substring(0, 3);    //ADDCOURANT
                        String B = str.substring(3, 6);
                        String C = str.substring(6, 9);    //OLDC
                        String D = str.substring(9, 12);
                        String E = str.substring(12, 15);   //pente
                        String F = str.substring(15, 18);
                        //***********************************
                        String G = str.substring(18, 21);   //actuelle
                        String H = str.substring(21, 24);
                        String I = str.substring(24, 27);   //limit_close
                        String J = str.substring(27, 30);
                        //************************************
                        String K = str.substring(30, 33);  //roue
                        String L = str.substring(33, 36);   // im_prog
                        String M = str.substring(36, 39);  // im_forc
                        String N = str.substring(39, 42);   //defaut
                        String O = str.substring(42, 45);   //surchoff
                        String P = str.substring(45, 48);   //program
                        String Q = str.substring(48, 51);   //recoi_2

                        //************************************************
                        //************************************************

                        A = A.replace("*", "");  //ADDCOURANT
                        Int_AddCourant = Integer.parseInt(A);
                        B = B.replace("*", "");  //ADDCOURANT+1
                        Int_AddCourant_1 = Integer.parseInt(B);
                        C = C.replace("*", ""); //OLDC
                        Int_Old = Integer.parseInt(C);
                        D = D.replace("*", "");  //OLDC+1
                        Int_Old_1 = Integer.parseInt(D);
                        E = E.replace("*", "");   //pente
                        Int_Pente = Integer.parseInt(E);
                        F = F.replace("*", "");  //pente+1
                        Int_Pente_1 = Integer.parseInt(F);

                        //************************************************
                        G = G.replace("*", "");  //actuelle
                        Int_actuelle = Integer.parseInt(G);
                        H = H.replace("*", "");
                        Int_actuelle_1 = Integer.parseInt(H);
                        I = I.replace("*", "");  //limit_close
                        Int_limite_close = Integer.parseInt(I);
                        J = J.replace("*", "");
                        Int_limite_close_1 = Integer.parseInt(J);

                        //********************************************** VALEUR STATE
                        K = K.replace("*", "");  //roue
                        Int_Roue_Codeuse = Integer.parseInt(K);
                        L = L.replace("*", ""); // im_prog
                        Int_Prog = Integer.parseInt(L);
                        M = M.replace("*", ""); // im_forc
                        Int_Forcage = Integer.parseInt(M);
                        N = N.replace("*", "");  //defaut
                        Int_Def_cap = Integer.parseInt(N);
                        O = O.replace("*", ""); //surchoff
                        Int_Def_surcouple = Integer.parseInt(O);
                        P = P.replace("*", ""); //program
                        Int_Def_Prog = Integer.parseInt(P);
                        Q = Q.replace("*", ""); //recoi_2
                        recoi_2 = Integer.parseInt(Q);
                        //************************************************
                        //************************************************

                        //courant.setText(Int_AddCourant+ ":" +Int_AddCourant_1 + ":" +Int_Old + ":" + Int_Old_1 + ":" +Int_Pente + ":" + Int_Pente_1);

                        if (Int_Def_cap==1){
                            Affichage_Defaut = "Defaut capteur";
                            //Toast.makeText(MainActivity.this,"" + Affichage_Defaut, Toast.LENGTH_SHORT).show();
                            ledVert.setBackgroundResource(R.drawable.circle_white);
                            ledRouge.setBackgroundResource(R.drawable.led_rouge);
                        } else if (Int_Def_Prog==1){
                            Affichage_Defaut = "Defaut logiciel";
                            //Toast.makeText(MainActivity.this,"" + Affichage_Defaut, Toast.LENGTH_SHORT).show();
                            ledVert.setBackgroundResource(R.drawable.led_vert);
                            ledRouge.setBackgroundResource(R.drawable.led_rouge);
                        } else if (Int_Def_surcouple==1){
                            //Toast.makeText(MainActivity.this,"" + Affichage_Defaut, Toast.LENGTH_SHORT).show();
                            ledOrange.setBackgroundResource(R.drawable.led_orange);
                            ledVert.setBackgroundResource(R.drawable.circle_white);
                            ledRouge.setBackgroundResource(R.drawable.circle_white);
                        } else{
                            Affichage_Defaut = "Pas de defaut";
                            ledVert.setBackgroundResource(R.drawable.circle_white);
                            ledRouge.setBackgroundResource(R.drawable.circle_white);
                        }
                        Minimum =10 ;
                        Maximum = Int_limite_close_1 + 255 * Int_limite_close ;
                        if (Int_actuelle == Int_limite_close){
                            Instant = Maximum - (Int_limite_close_1 - Int_actuelle_1);
                        }else{
                            Instant = (Int_limite_close - Int_actuelle) * 255 + Maximum - (Int_limite_close_1 - Int_actuelle_1);
                        }
                        progressBar.setMax(Maximum);
                        progressBar.setMin(Minimum);
                        progressBar.setProgress(Instant);

                        int calcul = ((Instant - Minimum) * 100) / (Maximum - Minimum ) ;
                        pourcentage = Integer.toString(calcul);
                        if(calcul>=100){
                            positionVolet.setText("100 %");
                        }else{
                            positionVolet.setText( pourcentage +" %" );
                        }
                    }

                    if (s.contains("EROLLY:")){
                        String[] relayE = s.split(":");
                        if(relayE[1].equals("0")){
                            fermer.setBackgroundResource(0);
                            fermer.setImageResource(R.mipmap.bt001);
                        }
                        if (relayE[1].equals("1")) {
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
                        temp.setText(relayE[3]+"°C");
                    }
                }
            }
        }
    }
}