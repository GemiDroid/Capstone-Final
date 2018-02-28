package com.gemi.chat_me.Activities;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gemi.chat_me.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import de.hdodenhof.circleimageview.CircleImageView;
import com.gemi.chat_me.Widget.NewAppWidget;

public class ProfileActivity extends AppCompatActivity {

    public static final int GALLERY_PICK = 1;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    String currentUID;
    CircleImageView imageView;
    TextView name, status;
    Button updateStatus, logout;
    ProgressDialog progressDialog;
    String statusJ;
    String nameJ;
    ImageView backI;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        backI = (ImageView) findViewById(R.id.profileBack);
        backI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageView = (CircleImageView) findViewById(R.id.profileImage);
        name = (TextView) findViewById(R.id.profileName);
        status = (TextView) findViewById(R.id.profileStatus);
        logout = (Button) findViewById(R.id.profileLogoutBTN);
        updateStatus = (Button) findViewById(R.id.profileChangeStatusBTN);
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUID = firebaseUser.getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameJ = dataSnapshot.child("name").getValue().toString();
                statusJ = dataSnapshot.child("status").getValue().toString();
                if (!dataSnapshot.child("image").getValue().toString().equals("")) {
                    final String imageJ = dataSnapshot.child("image").getValue().toString();
                    Picasso.with(ProfileActivity.this).load(imageJ).placeholder(R.drawable.ic_account_circle_black_24dp).into(imageView);
                }
                name.setText(nameJ);
                status.setText(statusJ);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_PICK);
            }
        });
        updateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                View view = getLayoutInflater().inflate(R.layout.change_status_dialog, null);
                final EditText newStatus = (EditText) view.findViewById(R.id.changestatusedittext);
                newStatus.setText(statusJ);
                Button save = (Button) view.findViewById(R.id.savestatuschangebutton);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog = new ProgressDialog(ProfileActivity.this);
                        alertDialog.dismiss();
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();
                        String newStatusS = newStatus.getText().toString();
                        databaseReference.child("status").setValue(newStatusS).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                } else {
                                    Toast.makeText(ProfileActivity.this, "Changing Status Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder logoutDialog = new AlertDialog.Builder(ProfileActivity.this);
                logoutDialog.setMessage("Are you sure you want to logout?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference.child("online").setValue(ServerValue.TIMESTAMP);
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = logoutDialog.create();
                alertDialog.show();
                setWidgetName();
                setWidgetStatus();
                setWidgetImage();
                Intent intent = new Intent(ProfileActivity.this, NewAppWidget.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ProfileActivity.this);
                int[] ai = appWidgetManager.getAppWidgetIds(new ComponentName(ProfileActivity.this, NewAppWidget.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ai);
                sendBroadcast(intent);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri ImageURI = data.getData();
            CropImage.activity(ImageURI).setAspectRatio(1, 1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(ProfileActivity.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                Uri uri = activityResult.getUri();
                StorageReference filePath = mStorageRef.child("Profile_Images").child("IMG-'" + currentUID + "'.jpg");
                filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            @SuppressWarnings("VisibleForTests")
                            String downloadURL = task.getResult().getDownloadUrl().toString();
                            databaseReference.child("image").setValue(downloadURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this, "Image Updated", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ProfileActivity.this, "Uploading Image Failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception exception = activityResult.getError();
                Toast.makeText(ProfileActivity.this, exception + "", Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
        }
    }

    void setWidgetName() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("name", "").apply();
    }

    void setWidgetStatus() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("status", "").apply();
    }

    void setWidgetImage() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("image", "https://wiki.shibboleth.net/confluence/images/icons/profilepics/default.png").apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}