package com.gemi.chat_me.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gemi.chat_me.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    EditText username, password, repassword, email, job;
    Button createAccount;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    ImageView back;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        back = (ImageView) findViewById(R.id.signupBack);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);
        email = (EditText) findViewById(R.id.email);
        job = (EditText) findViewById(R.id.job);
        createAccount = (Button) findViewById(R.id.createAcctount);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameS = username.getText().toString();
                String passwordS = password.getText().toString();
                final String emailS = email.getText().toString();
                final String jobS = job.getText().toString();
                if (TextUtils.isEmpty(usernameS)) {
                    username.setError("Enter Username");
                    username.requestFocus();
                } else if (TextUtils.isEmpty(passwordS)) {
                    password.setError("Enter Password");
                    password.requestFocus();
                } else if (passwordS.length() < 6) {
                    password.setError("Password must be at least 6 characters");
                    password.requestFocus();
                } else if (TextUtils.isEmpty(repassword.getText().toString())) {
                    repassword.setError("Enter Re-Password");
                    repassword.requestFocus();
                } else if (!passwordS.equals(repassword.getText().toString())) {
                    repassword.setError("Password not Match");
                    repassword.requestFocus();
                } else if (TextUtils.isEmpty(emailS)) {
                    email.setError("Enter E-Mail Address");
                    email.requestFocus();
                } else if (!emailS.matches(emailPattern)) {
                    email.setError("Invalid E-Mail Address Format");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(jobS)) {
                    job.setError("Enter Your Job");
                    job.requestFocus();
                } else {
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(emailS, passwordS).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userID = currentUser.getUid();
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("image", "");
                                userMap.put("name", usernameS);
                                userMap.put("status", "Pacito Pacito !");
                                userMap.put("job", jobS);
                                userMap.put("mail", emailS);
                                databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            progressDialog.hide();
                                            Toast.makeText(SignupActivity.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {
                                progressDialog.hide();
                                Toast.makeText(SignupActivity.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}