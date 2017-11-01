package com.sanju.chat.vidschat.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sanju.chat.vidschat.Adapters.ChatMsgAdapter;
import com.sanju.chat.vidschat.Models.Chat;
import com.sanju.chat.vidschat.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    ListView listView;
    List<Chat> chatList;
    ChatMsgAdapter chatListAdapter;
    ImageButton sendButton;
    DatabaseReference databaseReference;
    String otherUserId = "";
    String name = "";
    String curruntUserId = "";
    ValueEventListener valueEventListener;
    String chatRoomId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        curruntUserId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        Intent intent = getIntent();

        if (intent != null){

            otherUserId = getIntent().getStringExtra("otherUserId");
            name = getIntent().getStringExtra("name");
        }
        if (!name.equals("")){
            setTitle(name);
        }

        listView = (ListView) findViewById(R.id.list);
        sendButton = (ImageButton) findViewById(R.id.sendMessageButton);

        chatList = new ArrayList<Chat>();

//        for (int i = 0; i < 10; i++){
//            Chat chat = new Chat("Hi", "sanju chodhary", "");
//            chatList.add(chat);
//        }
//        chatListAdapter = new ChatMsgAdapter(this, chatList);
//
//        listView.setAdapter(chatListAdapter);



        // Setup our input methods. Enter key on the keyboard or pushing the send button
        EditText inputText = (EditText) findViewById(R.id.messageEditText);
        inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        findViewById(R.id.sendMessageButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
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

    @Override
    protected void onStart() {
        super.onStart();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                chatList.clear();

                if (dataSnapshot.getValue() != null){
                    DataSnapshot child;
                    Iterator iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()){
                        child = (DataSnapshot) iterator.next();
                        if (child.getKey().equals("chat")){
                            Iterator chatRoomIterator = child.getChildren().iterator();
                            while (chatRoomIterator.hasNext()){
                                DataSnapshot chatRoomSnapshot = (DataSnapshot) chatRoomIterator.next();
                                if (chatRoomSnapshot.getKey().equals(curruntUserId+":"+otherUserId)){

                                    chatRoomId = curruntUserId+":"+otherUserId+"";

                                    Iterator iterator1 = chatRoomSnapshot.getChildren().iterator();
                                    while (iterator1.hasNext()){
                                        DataSnapshot snapshot = (DataSnapshot) iterator1.next();

                                        Chat chat = snapshot.getValue(Chat.class);
                                        chatList.add(chat);
                                    }

                                }else if (chatRoomSnapshot.getKey().equals(otherUserId+":"+curruntUserId)){

                                    chatRoomId = otherUserId+":"+curruntUserId;
                                    Iterator iterator1 = chatRoomSnapshot.getChildren().iterator();
                                    while (iterator1.hasNext()){
                                        DataSnapshot snapshot = (DataSnapshot) iterator1.next();

                                        Chat chat = snapshot.getValue(Chat.class);
                                        chatList.add(chat);
                                    }

                                }

                            }
                        }
                    }
                }
                chatListAdapter = new ChatMsgAdapter(ChatActivity.this, chatList);
                listView.setAdapter(chatListAdapter);
                listView.setSelection(chatList.size() - 1);

//                chatListAdapter.registerDataSetObserver(new DataSetObserver() {
//                    @Override
//                    public void onChanged() {
//                        super.onChanged();
//                        listView.setSelection(chatList.size() - 1);
//                    }
//                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        databaseReference.addValueEventListener(eventListener);

        valueEventListener = eventListener;

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (valueEventListener != null){
            databaseReference.child("chat").removeEventListener(valueEventListener);
        }
    }

    private void sendMessage() {
        String timeStamp = "";
        EditText inputText = (EditText) findViewById(R.id.messageEditText);
        String input = inputText.getText().toString();
        if (chatRoomId.equals("")){
            chatRoomId = curruntUserId+":"+otherUserId;
        }
        if (!input.equals("") && !curruntUserId.equals("") && !otherUserId.equals("")) {
            Long tsLong = System.currentTimeMillis()/1000;
            timeStamp = tsLong.toString();
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, curruntUserId, timeStamp, true);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            Map<String, Object> postValues = chat.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/chat/"+chatRoomId+"/"+timeStamp, postValues);

            databaseReference.updateChildren(childUpdates);

            inputText.setText("");
        }
    }
}
