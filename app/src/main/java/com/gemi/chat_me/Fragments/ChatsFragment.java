package com.gemi.chat_me.Fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.gemi.chat_me.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import com.gemi.chat_me.Activities.ChatActivity;
import com.gemi.chat_me.Activities.FriendActivity;
import com.gemi.chat_me.Models.Messages;


public class ChatsFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    DatabaseReference friendsDBR;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    String UserID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = (ProgressBar) view.findViewById(R.id.chatLoading);
        if (firebaseAuth.getCurrentUser() != null) {
            UserID = firebaseAuth.getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Messages").child(UserID);
            friendsDBR = FirebaseDatabase.getInstance().getReference().child("Users");
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.chatsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseAuth.getCurrentUser() != null) {
            FirebaseRecyclerAdapter<Messages, ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Messages, ChatsViewHolder>(Messages.class, R.layout.friend_card, ChatsViewHolder.class, databaseReference) {
                @Override
                protected void populateViewHolder(final ChatsViewHolder viewHolder, Messages model, int position) {
                    final String UID = getRef(position).getKey();
                    friendsDBR.child(UID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            if (!dataSnapshot.child("image").getValue().toString().equals("")) {
                                Picasso.with(getActivity()).load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.ic_account_circle_black_24dp).into(viewHolder.FriendImage);
                            }
                            viewHolder.FriendName.setText(dataSnapshot.child("name").getValue().toString());
                            viewHolder.FriendStatus.setText(dataSnapshot.child("status").getValue().toString());
                            if (dataSnapshot.child("online").getValue().equals("true")) {
                                viewHolder.isOnline.setVisibility(View.VISIBLE);
                            } else {
                                viewHolder.isOnline.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    viewHolder.FriendImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), FriendActivity.class);
                            intent.putExtra("UID", UID);
                            intent.putExtra("ChatFragmentToFriendActivity", "intent");
                            startActivity(intent);
                        }
                    });
                    viewHolder.Card.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("UID", UID);
                            intent.putExtra("ChatsF", "intent");
                            startActivity(intent);
                        }
                    });
                }
            };
            recyclerView.setAdapter(adapter);

        }
    }

    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        LinearLayout Card;
        CircleImageView FriendImage;
        TextView FriendName, FriendStatus;
        ImageView isOnline;

        public ChatsViewHolder(View itemView) {
            super(itemView);
            FriendImage = (CircleImageView) itemView.findViewById(R.id.friendImage);
            FriendName = (TextView) itemView.findViewById(R.id.friendName);
            FriendStatus = (TextView) itemView.findViewById(R.id.friendLastMSG);
            Card = (LinearLayout) itemView.findViewById(R.id.card);
            isOnline = (ImageView) itemView.findViewById(R.id.friendOnlineIcon);
        }
    }
}