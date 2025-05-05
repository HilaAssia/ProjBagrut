package com.example.bagrutproject.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bagrutproject.R;
import com.example.bagrutproject.utils.FBAuthHelper;
import com.example.bagrutproject.utils.FireStoreHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

public class LogInActivity extends AppCompatActivity implements FBAuthHelper.FBReply {

    // Firebase helpers
    private FBAuthHelper fbAuthHelper;
    private FireStoreHelper fireStoreHelper;
    private FirebaseUser user;

    // UI components
    private EditText email, password;
    private Button loginButton;
    private TextView signupText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        // If user already logged in, check if they're a manager or user
        if (user != null) {
            fireStoreHelper = new FireStoreHelper(null);
            fireStoreHelper.getCollectionRefManager()
                    .whereEqualTo("uID", user.getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();

                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // User is a manager
                                startActivity(new Intent(LogInActivity.this, ManagerActivity.class));
                                finish();
                                Log.d("Firestore", "מסמך נמצא עבור המשתמש");
                            } else {
                                // User is a regular user
                                startActivity(new Intent(LogInActivity.this, UserActivity.class));
                                finish();
                                Log.d("Firestore", "לא נמצא מסמך עם ה-uID של המשתמש");
                            }
                        } else {
                            Log.e("Firestore", "שגיאה בקריאת המסמכים: ", task.getException());
                        }
                    });
        }

        // Init Firebase helper
        fbAuthHelper = new FBAuthHelper(this, this);

        // Link UI components
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);

        // Login button click
        loginButton.setOnClickListener(view -> {
            String eMail = email.getText().toString();
            String passWord = password.getText().toString();

            // Validate input
            if (!checkEmailValidity(eMail) || !checkPasswordValidity(passWord)) return;

            // Log in
            fbAuthHelper.loginUser(eMail, passWord);
        });

        // Redirect to sign up screen
        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    // Validate password length
    private boolean checkPasswordValidity(String passWord) {
        if (passWord.length() >= 6) {
            return true;
        } else {
            password.setError("Password must be at least 6 characters long");
            return false;
        }
    }

    // Validate email format
    private boolean checkEmailValidity(String eMail) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(eMail).matches()) {
            return true;
        } else {
            email.setError("Invalid email address");
            return false;
        }
    }

    // Callback on successful user creation (not used here)
    @Override
    public void createUserSuccess(FirebaseUser user) {}

    // Callback on successful login
    @Override
    public void loginSuccess(FirebaseUser user) {
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();

        fireStoreHelper.setCurrentUser(user);

        // Check if user is a manager
        fireStoreHelper.getCollectionRefManager()
                .whereEqualTo("uID", user.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // User is a manager
                            startActivity(new Intent(LogInActivity.this, ManagerActivity.class));
                            finish();
                            Log.d("Firestore", "מסמך נמצא עבור המשתמש");
                        } else {
                            // User is a regular user
                            startActivity(new Intent(LogInActivity.this, UserActivity.class));
                            finish();
                            Log.d("Firestore", "לא נמצא מסמך עם ה-uID של המשתמש");
                        }
                    } else {
                        Log.e("Firestore", "שגיאה בקריאת המסמכים: ", task.getException());
                    }
                });
    }

    // Callback on logout (not used here)
    @Override
    public void logoutSuccess(FirebaseUser user) {}
}