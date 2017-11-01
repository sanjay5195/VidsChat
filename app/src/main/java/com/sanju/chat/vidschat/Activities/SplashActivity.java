package com.sanju.chat.vidschat.Activities;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sanju.chat.vidschat.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SplashActivity extends AppCompatActivity {

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        user = FirebaseAuth.getInstance().getCurrentUser();



        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.sanju.chat.vidschat",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }




        int ms = 3000;//3000 -> 3 second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                final int totalProgressTime = 100;
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        Intent intent;
                        int jumpTime = 0;
                        while (jumpTime < totalProgressTime){

                            try {
                                sleep(200);
                                jumpTime += 5;
                                //progressBar.setProgress(jumpTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                        if (jumpTime == totalProgressTime){


                            if (user != null){
                                intent = new Intent(SplashActivity.this, HomeActivity.class);
                                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                intent = new Intent(SplashActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }

                    }
                };
                thread.start();

            }
        },ms);
    }





}
