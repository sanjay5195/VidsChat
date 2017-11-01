package com.sanju.chat.vidschat.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sanju.chat.vidschat.R;

import java.util.Iterator;

public class UserProfileUpdateActivity extends AppCompatActivity implements View.OnClickListener {


    EditText edt_name;
    EditText edt_email;
    EditText edt_mobile;
    EditText edt_city;
    EditText edt_country;
    Button btn_update;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_update);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        edt_name = (EditText) findViewById(R.id.edt_name);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_mobile = (EditText) findViewById(R.id.edt_mobile);
        edt_city = (EditText) findViewById(R.id.edt_city);
        edt_country = (EditText) findViewById(R.id.edt_country);
        btn_update = (Button) findViewById(R.id.btn_update);

        btn_update.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = databaseReference;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    DataSnapshot snapshot = (DataSnapshot) iterator.next();
                    if (snapshot.getKey().equals("name")){
                        edt_name.setText(snapshot.getValue().toString());
                    }else if (snapshot.getKey().equals("email")){
                        edt_email.setText(snapshot.getValue().toString());
                    }else if (snapshot.getKey().equals("mobile")){
                        edt_mobile.setText(snapshot.getValue().toString());
                    }else if (snapshot.getKey().equals("city")){
                        edt_city.setText(snapshot.getValue().toString());
                    }else if (snapshot.getKey().equals("country")){
                        edt_country.setText(snapshot.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_update:

                if (! edt_name.getText().toString().trim().isEmpty()){
                    databaseReference.child("name").setValue(edt_name.getText().toString().trim());
                }
                if (! edt_email.getText().toString().trim().isEmpty()){
                    databaseReference.child("email").setValue(edt_email.getText().toString().trim());
                }
                if (! edt_mobile.getText().toString().trim().isEmpty()){
                    databaseReference.child("mobile").setValue(edt_mobile.getText().toString().trim());
                }
                if (! edt_city.getText().toString().trim().isEmpty()){
                    databaseReference.child("city").setValue(edt_city.getText().toString().trim());
                }
                if (! edt_country.getText().toString().trim().isEmpty()){
                    databaseReference.child("country").setValue(edt_country.getText().toString().trim());
                }

                Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show();
                finish();
                break;
        }
    }
}
