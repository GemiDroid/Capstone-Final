package com.gemi.chat_me.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gemi.chat_me.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendActivity extends AppCompatActivity {

    CircleImageView FriendImage;
    TextView Name, Job, FriendsCount;
    Button Add, Delete;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    DatabaseReference friendRequestDBR;
    DatabaseReference friendsDBR;
    DatabaseReference MessagesDBR;
    FirebaseUser fCurrentUser;
    String friendCurrentState;
    String UID;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        back = (ImageView) findViewById(R.id.friendBack);
        UID = getIntent().getStringExtra("UID");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        friendRequestDBR = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        friendsDBR = FirebaseDatabase.getInstance().getReference().child("Friends");
        MessagesDBR = FirebaseDatabase.getInstance().getReference().child("Messages");
        fCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        FriendImage = (CircleImageView) findViewById(R.id.friendImage);
        Name = (TextView) findViewById(R.id.friendName);
        Job = (TextView) findViewById(R.id.friendJob);
        FriendsCount = (TextView) findViewById(R.id.friendsCount);
        Add = (Button) findViewById(R.id.friendAddBTN);
        Delete = (Button) findViewById(R.id.friendDeleteBTN);
        friendCurrentState = "notFriends";
        progressDialog = new ProgressDialog(FriendActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageS = dataSnapshot.child("image").getValue().toString();
                final String nameS = dataSnapshot.child("name").getValue().toString();
                String jobS = dataSnapshot.child("job").getValue().toString();
                if (!imageS.equals("")) {
                    Picasso.with(FriendActivity.this).load(imageS).placeholder(R.drawable.ic_account_circle_black_24dp).into(FriendImage);
                }
                Name.setText(nameS);
                Job.setText(jobS);
                friendsDBR.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count = 0;
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            count++;
                        }
                        if (count == 1) {
                            FriendsCount.setText(count + " Friend");
                        } else {
                            FriendsCount.setText(count + " Friends");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                friendRequestDBR.child(fCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("ReceivedRequests").hasChild(UID)) {
                            friendCurrentState = "requestReceived";
                            Add.setText("Confirm");
                            Add.setBackground(getDrawable(R.drawable.button));
                            Delete.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        } else if (dataSnapshot.child("SentRequests").hasChild(UID)) {
                            friendCurrentState = "requestSent";
                            Add.setText("Cancel Friend Request");
                            Add.setBackground(getDrawable(R.drawable.buttonyellow));
                            progressDialog.dismiss();
                        } else {
                            friendsDBR.child(fCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(UID)) {
                                        friendCurrentState = "Users";
                                        Add.setText("Unfriend");
                                        Add.setBackground(getDrawable(R.drawable.buttonred));
                                        Delete.setVisibility(View.INVISIBLE);
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                switch (friendCurrentState) {
                    case "notFriends":
                        friendRequestDBR.child(fCurrentUser.getUid()).child("SentRequests").child(UID).child("RequestType").setValue("sent");
                        friendRequestDBR.child(UID).child("ReceivedRequests").child(fCurrentUser.getUid()).child("RequestType").setValue("received");
                        friendCurrentState = "requestSent";
                        Add.setText("Cancel Friend Request");
                        Add.setBackground(getDrawable(R.drawable.buttonyellow));
                        progressDialog.dismiss();
                        break;


                    case "requestSent":
                        friendRequestDBR.child(fCurrentUser.getUid()).child("SentRequests").child(UID).removeValue();
                        friendRequestDBR.child(UID).child("ReceivedRequests").child(fCurrentUser.getUid()).removeValue();
                        friendCurrentState = "notFriends";
                        Add.setText("Send Friend Request");
                        Add.setBackground(getDrawable(R.drawable.buttongreen));
                        progressDialog.dismiss();
                        break;


                    case "requestReceived":
                        friendsDBR.child(fCurrentUser.getUid()).child(UID).child("key").setValue(ServerValue.TIMESTAMP);
                        friendsDBR.child(UID).child(fCurrentUser.getUid()).child("key").setValue(ServerValue.TIMESTAMP);
                        friendRequestDBR.child(fCurrentUser.getUid()).child("ReceivedRequests").child(UID).removeValue();
                        friendRequestDBR.child(UID).child("SentRequests").child(fCurrentUser.getUid()).removeValue();
                        friendCurrentState = "Users";
                        Add.setText("Unfriend");
                        Add.setBackground(getDrawable(R.drawable.buttonred));
                        Delete.setVisibility(View.INVISIBLE);
                        progressDialog.dismiss();
                        break;


                    case "Users":
                        friendsDBR.child(fCurrentUser.getUid()).child(UID).removeValue();
                        friendsDBR.child(UID).child(fCurrentUser.getUid()).removeValue();
                        MessagesDBR.child(fCurrentUser.getUid()).child(UID).removeValue();
                        MessagesDBR.child(UID).child(fCurrentUser.getUid()).removeValue();
                        friendCurrentState = "notFriends";
                        Add.setText("Send Friend Request");
                        Add.setBackground(getDrawable(R.drawable.buttongreen));
                        progressDialog.dismiss();
                        break;
                }
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendRequestDBR.child(fCurrentUser.getUid()).child("ReceivedRequests").child(UID).removeValue();
                friendRequestDBR.child(UID).child("SentRequests").child(fCurrentUser.getUid()).removeValue();
                friendCurrentState = "notFriends";
                Add.setText("Send Friend Request");
                Add.setBackground(getDrawable(R.drawable.buttongreen));
                Delete.setVisibility(View.INVISIBLE);
                progressDialog.dismiss();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}