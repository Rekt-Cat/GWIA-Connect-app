package com.maid.gwiaconnect;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity{
    Button register, signIn;
    TextInputLayout email, password;
    FirebaseAuth auth;
    DatabaseReference reference;
    String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        register = findViewById(R.id.registerscreen);
        signIn = findViewById(R.id.signIn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);

        auth = FirebaseAuth.getInstance();

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = email.getEditText().getText().toString();
                String pass = password.getEditText().getText().toString();
                if (mail.isEmpty()) {
                    email.setError("Please enter an Email");
                } else {
                    email.setError(null);
                    email.setErrorEnabled(false);
                }
                if (pass.isEmpty()) {
                    password.setError("Please enter a password");
                } else if (pass.length() < 6) {
                    password.setError("Please must have at least 6 character");
                } else {
                    password.setError(null);
                    password.setErrorEnabled(false);
                }
                if (!mail.isEmpty() && !pass.isEmpty() && pass.length() >= 6) {
                    final ProgressDialog[] pd = {new ProgressDialog(SignIn.this)};
                    pd[0].setMessage("Please Wait...");
                    pd[0].show();
                    auth.signInWithEmailAndPassword(mail, pass)
                            .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        Log.d("lolp", "inside!");
                                        FirebaseUser firebaseUser = auth.getCurrentUser();
                                        if (firebaseUser.isEmailVerified()) {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User")
                                                    .child(auth.getUid());
                                            ref.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    try {
                                                        if ((pd[0] != null) && pd[0].isShowing()) {
                                                            pd[0].dismiss();
                                                        }

                                                    } catch (final IllegalArgumentException e) {
                                                    } catch (final Exception e) {
                                                    } finally {
                                                        pd[0] = null;
                                                        startActivity(new Intent(SignIn.this, MainActivity.class));
                                                        finish();
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.d("lolp", "inside cancelled!");
                                                    //pd[0].dismiss();
                                                }
                                            });
                                        } else {
                                            Log.d("lolp", "inside verification else!");
                                            pd[0].dismiss();
                                            firebaseUser.sendEmailVerification();
                                            Toast.makeText(SignIn.this, "Verification mail sent to your mail. Please check your spam folder.", Toast.LENGTH_LONG).show();
                                            //showAlertDialoge();

                                        }

                                    } else {
                                        Log.d("lolp", "inside bigger else!");
                                        pd[0].dismiss();
                                        Toast.makeText(SignIn.this, "User does not exists. Please register!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                }


            }

        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignIn.this, RegisterScreen.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(findViewById(R.id.email), "email");
                pairs[1] = new Pair<View, String>(findViewById(R.id.pass), "pass");
                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(SignIn.this, pairs);
                startActivity(i, activityOptions.toBundle());
                finish();

            }
        });
    }

    private void showAlertDialoge() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
        builder.setTitle("Email not verified!");
        builder.setMessage("Please verify your email now. You cannot login without email verification.");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_APP_EMAIL);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}