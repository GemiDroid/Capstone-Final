package com.gemi.chat_me.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.gemi.chat_me.Activities.FriendActivity;
import com.gemi.chat_me.Models.Requests;
public class RequestsFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    DatabaseReference friendsDBR;
    DatabaseReference DBR;
    DatabaseReference friends;
    FirebaseUser fCurrentUser;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        fCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("FriendRequests").child(fCurrentUser.getUid()).child("ReceivedRequests");
        friendsDBR = FirebaseDatabase.getInstance().getReference().child("Users");
        DBR = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        friends = FirebaseDatabase.getInstance().getReference().child("Friends");
        progressDialog = new ProgressDialog(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.requestsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Requests, RequestsViewHolder> adapter = new FirebaseRecyclerAdapter<Requests, RequestsViewHolder>(Requests.class, R.layout.request_card, RequestsViewHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(final RequestsViewHolder viewHolder, Requests model, int position) {
                final String UID = getRef(position).getKey();
                friendsDBR.child(UID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.child("image").getValue().toString().equals("")) {
                            Picasso.with(getActivity()).load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.ic_account_circle_black_24dp).into(viewHolder.image);
                        }
                        viewHolder.name.setText(dataSnapshot.child("name").getValue().toString());
                        viewHolder.card.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), FriendActivity.class);
                                intent.putExtra("UID", UID);
                                intent.putExtra("RequestFragmentToFriendActivity", "intent");
                                startActivity(intent);
                            }
                        });
                        viewHolder.confirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progressDialog.setMessage(getString(R.string.loading));
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                friends.child(fCurrentUser.getUid()).child(UID).child("key").setValue(ServerValue.TIMESTAMP);
                                friends.child(UID).child(fCurrentUser.getUid()).child("key").setValue(ServerValue.TIMESTAMP);
                                DBR.child(fCurrentUser.getUid()).child("ReceivedRequests").child(UID).removeValue();
                                DBR.child(UID).child("SentRequests").child(fCurrentUser.getUid()).removeValue();
                                progressDialog.dismiss();
                            }
                        });
                        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progressDialog.setMessage(getString(R.string.loading));
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                DBR.child(fCurrentUser.getUid()).child("ReceivedRequests").child(UID).removeValue();
                                DBR.child(UID).child("SentRequests").child(fCurrentUser.getUid()).removeValue();
                                progressDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        LinearLayout card;
        CircleImageView image;
        TextView name;
        Button confirm, delete;

        public RequestsViewHolder(View itemView) {
            super(itemView);
            card = (LinearLayout) itemView.findViewById(R.id.requestCard);
            image = (CircleImageView) itemView.findViewById(R.id.requestImage);
            name = (TextView) itemView.findViewById(R.id.requestName);
            confirm = (Button) itemView.findViewById(R.id.requestConfirmBTN);
            delete = (Button) itemView.findViewById(R.id.requestDeleteBTN);
        }
    }
}