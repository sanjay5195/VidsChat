package com.sanju.chat.vidschat.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sanju.chat.vidschat.R;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    Button bt_submit;
    EditText et_name, et_pass, et_email, et_confirm,et_mob,et_count;
    String name;
    String email;
    String password;
    String confirm;
    String mob_No;
    String country;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        bt_submit = (Button) findViewById(R.id.bt_submit);
        et_name = (EditText) findViewById(R.id.et_name);
        et_pass = (EditText) findViewById(R.id.et_pass);
        et_email = (EditText) findViewById(R.id.et_email);
        et_mob=(EditText)findViewById(R.id.et_mob);
        et_count=(EditText)findViewById(R.id.et_count);
        et_confirm = (EditText) findViewById(R.id.et_confirm);
        bt_submit.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("", "onAuthStateChanged:signed_out");
                }

            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        name = et_name.getText().toString().trim();
        email = et_email.getText().toString().trim();
        password = et_pass.getText().toString().trim();
        confirm = et_confirm.getText().toString().trim();
        mob_No=et_mob.getText().toString().trim();
        country=et_count.getText().toString().trim();
        switch (v.getId()) {
            case R.id.bt_submit:
                if (name.equals("")) {
                    et_name.setError("Empty field");
                } else if (email.equals("")) {
                    et_email.setError("Empty field");
                } else if (et_pass.equals("")) {
                    et_pass.setError("Empty field");
                } else if (et_confirm.equals("")) {
                    et_confirm.setError("Empty field");
                } else if (!password.equals(confirm)) {
                    et_confirm.setError("NOT MATCH");
                } else {
                    createUser(email, password);
                    Toast.makeText(SignupActivity.this, "Processing", Toast.LENGTH_LONG).show();
                }
        }

    }

    private void createUser(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("", "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {

                            Toast.makeText(SignupActivity.this, "You are Regstered", Toast.LENGTH_LONG).show();

                            saveUserToDataBase(task.getResult().getUser(), name,email,mob_No,country);

                        } else {

                            Toast.makeText(SignupActivity.this, "not Registered", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }


    public void saveUserToDataBase(FirebaseUser firebaseUser, final String name, final String email, final String mob_No, final String country){
      final   String uid=firebaseUser.getUid();
        Toast.makeText(SignupActivity.this, name+ " is Registered", Toast.LENGTH_SHORT).show();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference=firebaseDatabase.getReference().child("users").child(uid);
        databaseReference.child("email").setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    databaseReference.child("mobile").setValue(mob_No);
                    databaseReference.child("country").setValue(country);
                    databaseReference.child("name").setValue(name);
                    Toast.makeText(SignupActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(SignupActivity.this, "Not inserted", Toast.LENGTH_SHORT).show();
            }
        });

    }

}

