package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bagrutproject.R;
import com.example.bagrutproject.model.AdminPasswordChecker;
import com.example.bagrutproject.model.Manager;
import com.example.bagrutproject.model.Product;
import com.example.bagrutproject.utils.FBAuthHelper;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity implements FBAuthHelper.FBReply, FireStoreHelper.FBReply {

    // Firebase helpers
    private FBAuthHelper fbAuthHelper;
    private FireStoreHelper fireStoreHelper;

    // UI elements
    private EditText eMail, password, verPass;
    private Button signinButton;
    private TextView loginText;
    private CheckBox checkBox;

    private int chances;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase helpers
        fbAuthHelper = new FBAuthHelper(this, this);
        fireStoreHelper = new FireStoreHelper(this);

        // Link UI components
        eMail = findViewById(R.id.email);
        password = findViewById(R.id.password);
        verPass = findViewById(R.id.verificationPassword);
        checkBox = findViewById(R.id.checkbox_manager);
        signinButton = findViewById(R.id.signinButton);
        loginText = findViewById(R.id.loginText);

        chances = 2; // Number of tries for manager verification

        // Sign up button click
        signinButton.setOnClickListener(view -> {
            String email = eMail.getText().toString();
            String passWord = password.getText().toString();

            // Validate input
            if (!checkEmailValidity(email) || !checkPasswordValidity(passWord))
                return;

            // Create user
            fbAuthHelper.createUser(email, passWord);
        });

        // Redirect to login
        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
            startActivity(intent);
        });
    }

    // Check if manager password is valid
    private boolean isManager(String userInput) {
        if (AdminPasswordChecker.validatePassword(userInput)) {
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Check if password and confirmation match
    private boolean verifyPassword(String passWord, String verpass) {
        if (passWord.equals(verpass)) {
            return true;
        } else {
            verPass.setError("your passwords don't match");
            return false;
        }
    }

    // Validate password length and match
    private boolean checkPasswordValidity(String passWord) {
        if (passWord.length() >= 6) {
            return verifyPassword(passWord, verPass.getText().toString());
        } else {
            password.setError("Password must be at least 6 characters long");
            return false;
        }
    }

    // Validate email format
    private boolean checkEmailValidity(String email) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        } else {
            eMail.setError("Invalid email address");
            return false;
        }
    }

    // Callback for successful user creation
    @Override
    public void createUserSuccess(FirebaseUser user) {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

        if (checkBox.isChecked()) { // Manager checkbox checked
            Dialog dialog = new Dialog(SignUpActivity.this);
            dialog.setContentView(R.layout.manager_dialog);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);

            EditText etManagerPass = dialog.findViewById(R.id.manager_verification);
            Button buttonAdd = dialog.findViewById(R.id.btnAdd);

            // Manager code validation
            buttonAdd.setOnClickListener(v -> {
                if (isManager(etManagerPass.getText().toString())) {
                    Manager manager = new Manager(user.getEmail(), user.getUid());
                    fireStoreHelper.add(manager);

                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                } else {
                    if (chances > 0) {
                        etManagerPass.setError("Invalid code");
                        Toast.makeText(this, "you have "+chances+" chances left", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(SignUpActivity.this, UserActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                    chances--;
                }
            });
            dialog.show();

        } else {
            // Regular user
            Intent intent = new Intent(SignUpActivity.this, UserActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void loginSuccess(FirebaseUser user) {}

    @Override
    public void logoutSuccess(FirebaseUser user) {}

    @Override
    public void getAllSuccess(ArrayList<Product> products) {}

    @Override
    public void getOneSuccess(Product product) {}

    @Override
    public void onProductsLoaded(ArrayList<Product> products) {}
}