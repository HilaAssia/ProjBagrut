package com.example.bagrutproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity implements FBAuthHelper.FBReply {

    private FBAuthHelper fbAuthHelper;
    EditText email;
    EditText password;
    Button loginButton;
    TextView signupText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        fbAuthHelper = new FBAuthHelper(this, this);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);


        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eMail = email.getText().toString();
                String passWord = password.getText().toString();

                checkEmailValidity(eMail);
                checkPasswordValidity(passWord);

                fbAuthHelper.loginUser(eMail, passWord);
            }
        });
        signupText = findViewById(R.id.signupText);
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkPasswordValidity(String passWord) {
        if (passWord.length() >= 6) {
            // Password is valid
        } else {
            // Password is invalid, show an error message
            password.setError("Password must be at least 6 characters long");
        }
    }

    private void checkEmailValidity(String eMail) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(eMail).matches()) {
            // Email is valid
        } else {
            // Email is invalid, show an error message
            email.setError("Invalid email address");
        }
    }

    @Override
    public void creatUserSuccess(FirebaseUser user) {

    }

    @Override
    public void loginSuccess(FirebaseUser user) {
        Toast.makeText(this, "success",
                Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(LogInActivity.this, HomeActivity.class);
        startActivity(intent);

    }
}