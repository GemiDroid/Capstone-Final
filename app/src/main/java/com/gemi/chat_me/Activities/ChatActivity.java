package com.gemi.chat_me.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gemi.chat_me.Application.BaseApp;
import com.gemi.chat_me.Application.GetTime;
import com.gemi.chat_me.Models.Messages;
import com.gemi.chat_me.R;
import com.gemi.chat_me.adapters.ChatAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {

    String UID, currentUser;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    TextView name, lastseen;
    CircleImageView image;
    EditText message;
    ImageView send, back;
    RecyclerView recyclerView;
    List<Messages> messagesList = new ArrayList<>();
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        message = (EditText) findViewById(R.id.chatEditText);
        send = (ImageView) findViewById(R.id.send);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser().getUid();
        UID = getIntent().getStringExtra("UID");
        Toolbar toolbar = (Toolbar) findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toolbar_chat, null);
        actionBar.setCustomView(view);
        chatAdapter = new ChatAdapter(messagesList);
        recyclerView = (RecyclerView) findViewById(R.id.chatList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        recyclerView.setAdapter(chatAdapter);
        LoadMessages();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, FriendActivity.class);
                intent.putExtra("UID", UID);
                intent.putExtra("ChatActivityToFriendActivity", "intent");
                startActivity(intent);
            }
        });
        back = (ImageView) findViewById(R.id.chatBack);
        name = (TextView) findViewById(R.id.chatName);
        lastseen = (TextView) findViewById(R.id.chatLastSeen);
        image = (CircleImageView) findViewById(R.id.chatImage);
        databaseReference.child("Users").child(UID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("image").getValue().toString().equals("")) {
                    Picasso.with(ChatActivity.this).load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.ic_account_circle_black_24dp).into(image);
                }
                name.setText(dataSnapshot.child("name").getValue().toString());
                if (dataSnapshot.child("online").getValue().toString().equals("true")) {
                    lastseen.setText("Active now");
                } else {
                    GetTime getTime = new GetTime();
                    String lastSeenS = getTime.getTimeAgo(Long.parseLong(dataSnapshot.child("online").getValue().toString()), getApplicationContext());
                    lastseen.setText("Active " + lastSeenS);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(message.getText().toString())) {
                    HashMap<String, String> messagesMap = new HashMap<>();
                    messagesMap.put("senderID", currentUser);
                    messagesMap.put("receiverID", UID);
                    messagesMap.put("message", message.getText().toString());
                    databaseReference.child("Messages").child(currentUser).child(UID).push().setValue(messagesMap);
                    databaseReference.child("Messages").child(UID).child(currentUser).push().setValue(messagesMap);
                    message.setText("");
                }
            }
        });

//        if (getIntent().hasExtra("ChatsF")) {
//            intent = "ChatsF";
//        } else if (getIntent().hasExtra("FriendsF")) {
//            intent = "FriendsF";
//        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
//                Intent back = new Intent(ChatActivity.this, MainActivity.class);
//                back.putExtra(intent, "intent");
//                startActivity(back);
//                finish();
            }
        });
    }

    public void LoadMessages() {
        databaseReference.child("Messages").child(currentUser).child(UID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                chatAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}




