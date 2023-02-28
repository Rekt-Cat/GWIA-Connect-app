package com.maid.gwiaconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Models.CommentModel;

public class splashScreen extends AppCompatActivity implements FirebaseAuth.AuthStateListener {
    FirebaseAuth auth;
    String name;
    NetworkInfo info = null;
    boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        auth = FirebaseAuth.getInstance();


    }

    private boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;

            }
        }
        return  false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                connected = isNetworkAvailable();
                FirebaseAuth.getInstance().addAuthStateListener(splashScreen.this);



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        Log.d("lolt", "internet spl : " + connected);
        if(connected) {
            if (auth.getCurrentUser() != null) {

                String ui = auth.getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.child(ui).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                        if (readWriteUserDetails != null) {
                            name = readWriteUserDetails.fullName;
                            Log.d("lol", "The name is : " + name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                startActivity(new Intent(splashScreen.this, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(splashScreen.this, SignIn.class));
                finish();
            }
        }
        else{
            Toast.makeText(splashScreen.this, "No Internet Connection!", Toast.LENGTH_LONG).show();
        }

    }
}