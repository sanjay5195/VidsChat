package com.sanju.chat.vidschat.Fragments;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sanju.chat.vidschat.Adapters.ContactListRecyclerViewAdapter;
import com.sanju.chat.vidschat.Manifest;
import com.sanju.chat.vidschat.Models.User;
import com.sanju.chat.vidschat.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactTabFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    List<User> userList;
    List<User> contactList;
    List<User> commonList;
    RecyclerView recycler_view;


    public ContactTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recycler_view = (RecyclerView ) inflater.inflate(R.layout.fragment_chat_tab, container, false);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        userList = new ArrayList<User>();
        contactList = new ArrayList<User>();
        commonList = new ArrayList<User>();

//        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//        startActivityForResult(intent, PICK_CONTACT);
        return recycler_view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //userList.clear();
//        switch (requestCode){
//            case PICK_CONTACT:
//                if (resultCode == Activity.RESULT_OK){
//                    Uri contactData = data.getData();
//                    Cursor cursor = getActivity().getContentResolver().query(contactData, null, null, null, null);
//                    User user = new User();
//                    if (cursor.moveToFirst()){
//                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                        user.name = name;
//                    }
//                    userList.add(user);
//                }
//                ContactListRecyclerViewAdapter chatListRecyclerViewAdapter = new ContactListRecyclerViewAdapter(getActivity(), userList);
//                recycler_view.setAdapter(chatListRecyclerViewAdapter);
//                break;
//        }
    }

    @Override
    public void onStart() {
        super.onStart();

        userList.clear();
        contactList.clear();
        commonList.clear();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (getActivity().checkSelfPermission(android.Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED){

                requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

            }else {
                Toast.makeText(getActivity(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }else {
            getContacts();
        }

        Query query = FirebaseDatabase.getInstance().getReference();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    DataSnapshot snapshot = (DataSnapshot) iterator.next();
                    if (snapshot.getKey().equals("users")){
                        Iterator usersIterator = snapshot.getChildren().iterator();
                        while (usersIterator.hasNext()){
                            DataSnapshot userSnapshot = (DataSnapshot) usersIterator.next();
                            Iterator user_details_iterator = userSnapshot.getChildren().iterator();
                            User user = new User();
                            user.uid = userSnapshot.getKey();
                            while (user_details_iterator.hasNext()){
                                DataSnapshot user_details_dataSnapshot = (DataSnapshot) user_details_iterator.next();
                                if (user_details_dataSnapshot.getKey().equals("name")){
                                    user.name = user_details_dataSnapshot.getValue().toString();
                                }else if (user_details_dataSnapshot.getKey().equals("email")){
                                    user.email = user_details_dataSnapshot.getValue().toString();
                                }else if (user_details_dataSnapshot.getKey().equals("photoUrl")){
                                    user.photoUrl = user_details_dataSnapshot.getValue().toString();
                                }else if (user_details_dataSnapshot.getKey().equals("mobile")){
                                    user.mob_no = user_details_dataSnapshot.getValue().toString();
                                }
                            }
                            userList.add(user);
                        }
                    }
                }

                for (User user : userList){
                    for (User contactUser : contactList){
                        String contact_no = contactUser.mob_no;
                        contact_no = contact_no.replaceAll(" ","").trim();
                        if (contact_no.length() >= 10){
                            contact_no = contact_no.substring(contact_no.length() - 10);
                            if (user.mob_no.equals(contact_no) && !user.mob_no.equals("")){
                                commonList.add(user);
                            }
                        }

                    }
                }
                ContactListRecyclerViewAdapter contactListRecyclerViewAdapter = new ContactListRecyclerViewAdapter(getActivity(), commonList);
                recycler_view.setAdapter(contactListRecyclerViewAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS){

            getContacts();

        }else {

        }
    }

    private void getContacts(){
        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "upper("+ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+") ASC");
        if(cursor.moveToFirst())
        {
            ArrayList<String> alContacts = new ArrayList<String>();
            do
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
                    while (pCur.moveToNext())
                    {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        alContacts.add(contactNumber);
                        User user = new User();
                        user.name = contactName+" -> "+contactNumber;
                        user.mob_no = contactNumber;
                        contactList.add(user);
                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext()) ;
//            ContactListRecyclerViewAdapter chatListRecyclerViewAdapter = new ContactListRecyclerViewAdapter(getActivity(), userList);
//            recycler_view.setAdapter(chatListRecyclerViewAdapter);
        }
    }
}
