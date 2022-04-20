package com.example.mitconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConfigTerminer extends AppCompatActivity{

    private String ip;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_terminer);
        ip = "203.114.5.2";
    }
    public void retour (View view){
        String roueCoudeuse = "0000";
        String s1 = roueCoudeuse +"0001";
        int decimalValue2 = Integer.parseInt(s1,2);
        TaskEsp taskEsp = new TaskEsp(ip + "/send?state=" + decimalValue2);
        taskEsp.execute();

        final Intent act1 = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(act1);
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
        protected void onPostExecute(String s) {
        }
    }
}