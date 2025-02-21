package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bagrutproject.utils.FBAuthHelper;
import com.example.bagrutproject.R;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity implements FBAuthHelper.FBReply {

    private FBAuthHelper fbAuthHelper;
    private FireStoreHelper fireStoreHelper;
    EditText email;
    EditText password;
    Button loginButton;
    TextView signupText;
    CheckBox checkBox;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            Intent intent=new Intent(LogInActivity.this, UserActivity.class);
            startActivity(intent);
        }

        fbAuthHelper = new FBAuthHelper(this, this);
        fireStoreHelper = new FireStoreHelper(null);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        checkBox= findViewById(R.id.checkbox_manager);

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
    public void createUserSuccess(FirebaseUser user) {

    }

    @Override
    public void loginSuccess(FirebaseUser user) {
        Toast.makeText(this, "success",
                Toast.LENGTH_SHORT).show();
        if (fireStoreHelper.getCollectionRefManager().whereEqualTo("uID",user.getUid())!=null){
            Intent intent=new Intent(LogInActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent=new Intent(LogInActivity.this, UserActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void logoutSuccess(FirebaseUser user) {

    }
}