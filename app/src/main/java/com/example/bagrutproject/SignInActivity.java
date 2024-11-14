package com.example.bagrutproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {

    private FBAuthHelper fbAuthHelper;
    private EditText username;
    private EditText password, verPass;
    private Button signinButton;
    private TextView loginText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        fbAuthHelper = new FBAuthHelper(this,this);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        verPass = findViewById(R.id.verificationPassword);
        signinButton = findViewById(R.id.signinButton);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = username.getText().toString();
                String password = password.getBytes().toString();
                String verPass = verPass.getBytes().toString();

                checkEmailValidity(email);
                checkPasswordValidity(password);

                fbAuthHelper.creatUser(email, password);
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

}