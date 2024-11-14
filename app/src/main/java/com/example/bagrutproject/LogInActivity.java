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

public class LogInActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button loginButton;
    TextView signupText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("user") && password.getText().toString().equals("1234")) {
                    Toast.makeText(LogInActivity.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LogInActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                }
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


}