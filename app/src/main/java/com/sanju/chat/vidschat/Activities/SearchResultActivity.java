package com.sanju.chat.vidschat.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sanju.chat.vidschat.Adapters.ContactListRecyclerViewAdapter;
import com.sanju.chat.vidschat.Models.User;
import com.sanju.chat.vidschat.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchResultActivity extends AppCompatActivity {

    String query = "";
    DatabaseReference databaseReference;
    List<User> userList;
    RecyclerView recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        userList = new ArrayList<User>();

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            query = extras.getString("query");
            searchItem(query);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super. onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void searchItem(final String query){
        Query queryForSearchUser = databaseReference.child("users");

        queryForSearchUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();

                String name = "";
                String artistType = "";
                String city = "";
                String state = "";
                String country = "";
                String photoUrl = "";
                String email = "";
                String providerId = "";
                String mobile = "";
                String userID = "";
                String fcmId = "";

                while (iterator.hasNext()){
                    DataSnapshot snapshot = (DataSnapshot) iterator.next();
                    Iterator iterator1 = snapshot.getChildren().iterator();
                    while (iterator1.hasNext()){
                        DataSnapshot snapshot1 = (DataSnapshot) iterator1.next();
                        if (snapshot1.getKey().equals("name")){
                            name = snapshot1.getValue().toString();

                            if (name.toLowerCase().contains(query.toLowerCase())){

                                Iterator iterator2 = snapshot.getChildren().iterator();

                                //===== For assigning Other user value not previous user
                                name = "";
                                city = "";
                                state = "";
                                country = "";
                                photoUrl = "";
                                email = "";
                                mobile = "";
                                userID = snapshot.getKey();

                                while (iterator2.hasNext()){
                                    DataSnapshot snapshot2 = (DataSnapshot) iterator2.next();


                                    if (snapshot2.getKey().equals("name")){
                                        name = snapshot2.getValue().toString();
                                    }else if (snapshot2.getKey().equals("email")){
                                        email = snapshot2.getValue().toString();
                                    }else if (snapshot2.getKey().equals("mobile")){
                                        mobile = snapshot2.getValue().toString();
                                    }else if (snapshot2.getKey().equals("city")){
                                        city = snapshot2.getValue().toString();
                                    }else if (snapshot2.getKey().equals("country")){
                                        country = snapshot2.getValue().toString();
                                    }else if (snapshot2.getKey().equals("photoUrl")){
                                        photoUrl = snapshot2.getValue().toString();
                                    }

                                }

                                if (userID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                                }else {
                                    User user = new User(name, email, mobile, userID, country, photoUrl);//new User(name, email, providerId, photoUrl, artistType, mobile, country, state, city, userID, fcmId);


                                    if (user != null && user.uid != ""){
                                        userList.add(user);
                                    }
                                }
                            }
                        }
                    }
                }

                ContactListRecyclerViewAdapter contactListRecyclerViewAdapter = new ContactListRecyclerViewAdapter(SearchResultActivity.this, userList);
                recycler_view.setAdapter(contactListRecyclerViewAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
