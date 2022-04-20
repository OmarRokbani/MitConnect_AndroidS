package com.example.mitconnect;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Parametre extends AppCompatActivity {

    ImageButton settingRL;
    ImageButton backed;
    Button settingParaVolRemot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametre);

        settingRL =findViewById(R.id.settingRemotLocaL);
        settingParaVolRemot = findViewById(R.id.paraVoletRemot);
        backed = findViewById(R.id.backed);

    }
    @Override
    protected void onResume(){
        super.onResume();
        settingRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent act5 = new Intent(getApplicationContext(), EnvoiRemotLocal.class);
                startActivity(act5);
            }
        });
        backed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent act = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(act);
            }
        });
        settingParaVolRemot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Parametre.this)
                        .setTitle(android.R.string.dialog_alert_title)
                        .setMessage("Suivi les étapes pour configurer le volet de la piscine en mode remot")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which){
                                // continue with delete
                                final Intent act = new Intent(getApplicationContext(), ParamVolet1.class);
                                startActivity(act);
                                //finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert).show();
            }
        });
    }
}