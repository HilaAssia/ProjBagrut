package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class SignInActivity extends AppCompatActivity implements FBAuthHelper.FBReply, FireStoreHelper.FBReply {

    private FBAuthHelper fbAuthHelper;
    private FireStoreHelper fireStoreHelper;
    private EditText eMail;
    private EditText password, verPass;
    //private EditText managerPass;
    private Button signinButton;
    private TextView loginText;
    private CheckBox checkBox;
    private int chances;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        fbAuthHelper = new FBAuthHelper(this, this);
        fireStoreHelper = new FireStoreHelper(this);
        eMail = findViewById(R.id.email);
        password = findViewById(R.id.password);
        checkBox = findViewById(R.id.checkbox_manager);
        verPass = findViewById(R.id.verificationPassword);
        chances=3;
        //managerPass = findViewById(R.id.manager_verification);

        signinButton = findViewById(R.id.signinButton);
        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = eMail.getText().toString();
                String passWord = password.getText().toString();
                String verpass = verPass.getText().toString();
                //String managerVer = managerPass.getText().toString();

                checkEmailValidity(email);
                checkPasswordValidity(passWord);
                verifyPassword(passWord, verpass);

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

    private boolean isManager(String userInput){
        if (AdminPasswordChecker.validatePassword(userInput)) {
            Toast.makeText(this, "success",
                    Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(this, "failed",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
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
        if (checkBox.isChecked()){
            Dialog dialog = new Dialog(SignInActivity.this);
            dialog.setContentView(R.layout.manager_dialog);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            EditText etManagerPass=dialog.findViewById(R.id.manager_verification);
            Button buttonAdd=dialog.findViewById(R.id.btnAdd);
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isManager(etManagerPass.getText().toString())){
                        Manager manager =new Manager(user.getEmail(),user.getUid());
                        fireStoreHelper.add(manager);
                        Intent intent=new Intent(SignInActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                    }
                    else {
                        if (chances>0)
                            etManagerPass.setError("Invalid code");
                        else{
                            Intent intent=new Intent(SignInActivity.this, UserActivity.class);
                            startActivity(intent);
                            finish();
                            dialog.dismiss();
                        }
                        chances--;
                    }
                }
            });
            dialog.show();
        }
        else {
            Intent intent=new Intent(SignInActivity.this, UserActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void loginSuccess(FirebaseUser user) {

    }

    @Override
    public void logoutSuccess(FirebaseUser user) {

    }

    @Override
    public void getAllSuccess(ArrayList<Product> products) {

    }

    @Override
    public void getOneSuccess(Product product) {

    }
}