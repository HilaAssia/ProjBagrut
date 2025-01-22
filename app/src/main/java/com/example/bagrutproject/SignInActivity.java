package com.example.bagrutproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements FBAuthHelper.FBReply {

    private FBAuthHelper fbAuthHelper;
    private EditText eMail;
    private EditText password, verPass;
    private Button signinButton;
    private TextView loginText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        fbAuthHelper = new FBAuthHelper(this, this);
        eMail = findViewById(R.id.email);
        password = findViewById(R.id.password);
        verPass = findViewById(R.id.verificationPassword);
        signinButton = findViewById(R.id.signinButton);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = eMail.getText().toString();
                String passWord = password.getText().toString();
                String verpass = verPass.getText().toString();

                checkEmailValidity(email);
                checkPasswordValidity(passWord);
                verifyPassword(passWord,verpass);

                fbAuthHelper.createUser(email, passWord);
            }
        });
        loginText = findViewById(R.id.loginText);
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void verifyPassword(String passWord,String verpass) {
        if (passWord.equals(verpass)) {
            // Password is valid
        } else {
            // Password is invalid, show an error message
            verPass.setError("Password not verified");
        }
    }

    private void checkPasswordValidity(String passWord) {
        if (passWord.length() >= 6) {
            // Password is valid
        } else {
            // Password is invalid, show an error message
            password.setError("Password must be at least 6 characters long");
        }
    }

    private void checkEmailValidity(String email) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Email is valid
        } else {
            // Email is invalid, show an error message
            eMail.setError("Invalid email address");
        }
    }

    @Override
    public void createUserSuccess(FirebaseUser user) {
        Toast.makeText(this, "success",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginSuccess(FirebaseUser user) {

    }
}