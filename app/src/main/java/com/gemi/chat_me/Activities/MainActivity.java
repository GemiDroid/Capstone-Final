package com.gemi.chat_me.Activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.gemi.chat_me.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import com.gemi.chat_me.Fragments.ChatsFragment;
import com.gemi.chat_me.Fragments.FriendsFragment;
import com.gemi.chat_me.Fragments.MeetFragment;
import com.gemi.chat_me.Fragments.RequestsFragment;

import com.gemi.chat_me.Widget.NewAppWidget;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    BottomNavigationViewEx navigation;
    String currentUID;
    CircleImageView image;
    private BottomNavigationViewEx.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationViewEx.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_chats:
                    if (navigation.getCurrentItem() != 0) {
                        getFragmentManager().beginTransaction().replace(R.id.container, new ChatsFragment()).commit();
                    }
                    return true;
                case R.id.navigation_friends:
                    if (navigation.getCurrentItem() != 1) {
                        getFragmentManager().beginTransaction().replace(R.id.container, new FriendsFragment()).commit();
                    }
                    return true;
                case R.id.navigation_requests:
                    if (navigation.getCurrentItem() != 2) {
                        getFragmentManager().beginTransaction().replace(R.id.container, new RequestsFragment()).commit();
                    }
                    return true;
                case R.id.navigation_all:
                    if (navigation.getCurrentItem() != 3) {
                        getFragmentManager().beginTransaction().replace(R.id.container, new MeetFragment()).commit();
                    }
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toolbar_main, null);
        actionBar.setCustomView(view);
        image = (CircleImageView) findViewById(R.id.myImage);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new ChatsFragment()).commit();
        }
        navigation = (BottomNavigationViewEx) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.enableAnimation(false);
        navigation.enableItemShiftingMode(false);
        navigation.enableShiftingMode(false);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            currentUID = firebaseUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
            databaseReference.child("online").setValue("true");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.child("image").getValue().toString().equals("")) {
                        Picasso.with(MainActivity.this).load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.ic_account_circle_black_24dp).into(image);
                    }
                    setWidgetName(dataSnapshot.child("name").getValue().toString());
                    setWidgetStatus(dataSnapshot.child("status").getValue().toString());
                    setWidgetImage(dataSnapshot.child("image").getValue().toString());
                    Intent intent = new Intent(MainActivity.this, NewAppWidget.class);
                    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MainActivity.this);
                    int[] ai = appWidgetManager.getAppWidgetIds(new ComponentName(MainActivity.this, NewAppWidget.class));
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ai);
                    sendBroadcast(intent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    void setWidgetName(String name) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("name", name).apply();
    }

    void setWidgetStatus(String status) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("status", status).apply();
    }

    void setWidgetImage(String image) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("image", image).apply();
    }
}