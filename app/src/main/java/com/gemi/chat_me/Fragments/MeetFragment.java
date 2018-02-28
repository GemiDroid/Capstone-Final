package com.gemi.chat_me.Fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import com.gemi.chat_me.Activities.FriendActivity;
import com.gemi.chat_me.Models.Meets;
import com.gemi.chat_me.R;

public class MeetFragment extends Fragment {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meet, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.meetLoading);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView = (RecyclerView) view.findViewById(R.id.meetList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Meets, MeetViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Meets, MeetViewHolder>(Meets.class, R.layout.meet_card, MeetViewHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(MeetViewHolder viewHolder, Meets model, int position) {
                progressBar.setVisibility(View.GONE);
                final String UID = getRef(position).getKey();
                if (!model.getImage().equals("")) {
                    Picasso.with(getActivity()).load(model.getImage()).placeholder(R.drawable.ic_account_circle_black_24dp).into(viewHolder.MeetImage);
                }
                viewHolder.MeetName.setText(model.getName());
                viewHolder.Card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), FriendActivity.class);
                        intent.putExtra("UID", UID);
                        intent.putExtra("MeetFragmentToFriendActivity", "intent");
                        startActivity(intent);
                    }
                });

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MeetViewHolder extends RecyclerView.ViewHolder {

        LinearLayout Card;
        CircleImageView MeetImage;
        TextView MeetName;

        public MeetViewHolder(View itemView) {
            super(itemView);
            Card = (LinearLayout) itemView.findViewById(R.id.meetCard);
            MeetImage = (CircleImageView) itemView.findViewById(R.id.meetImage);
            MeetName = (TextView) itemView.findViewById(R.id.meetName);
        }
    }
}