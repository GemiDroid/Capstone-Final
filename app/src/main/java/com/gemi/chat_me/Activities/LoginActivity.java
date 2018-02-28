package com.gemi.chat_me.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gemi.chat_me.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBTN, signupBTN;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        email = (EditText) findViewById(R.id.loginemail);
        password = (EditText) findViewById(R.id.loginpassword);
        loginBTN = (Button) findViewById(R.id.loginbtn);
        signupBTN = (Button) findViewById(R.id.signupbtn);
        signupBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailL = email.getText().toString();
                String passwordL = password.getText().toString();
                if (TextUtils.isEmpty(emailL)) {
                    email.setError("Enter E-Mail Address");
                    email.requestFocus();
                } else if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(),emailL)) {
                    email.setError("Invalid E-Mail Address Format");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(passwordL)) {
                    password.setError("Enter Password");
                    password.requestFocus();
                } else if (passwordL.length() < 6) {
                    password.setError("Password must be at least 6 characters");
                    password.requestFocus();
                } else {
                    progressDialog.setMessage("Loading...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(emailL, passwordL).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                progressDialog.hide();
                                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}