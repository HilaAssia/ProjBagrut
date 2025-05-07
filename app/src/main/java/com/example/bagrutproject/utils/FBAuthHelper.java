package com.example.bagrutproject.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class FBAuthHelper {
    private static FirebaseAuth mAuth;
    private FBReply fbReply;
    private Activity activity;
    private static final String TAG="myTAG";

    public FBAuthHelper(Activity activity, FBReply fbReply) {
        mAuth = FirebaseAuth.getInstance();
        this.activity = activity;
        this.fbReply = fbReply;
    }
    public static FirebaseUser getCurrentUser(){return mAuth.getCurrentUser();}

    public interface FBReply{
        public void createUserSuccess (FirebaseUser user);
        public void loginSuccess (FirebaseUser user);
        public void logoutSuccess (FirebaseUser user);
    }

    public void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        fbReply.createUserSuccess(user);
                    } else {
                        Exception e = task.getException();
                        String message = "Registration failed.";

                        if (e instanceof FirebaseAuthUserCollisionException) {
                            message = "This email is already registered.";
                        } else if (e instanceof FirebaseAuthWeakPasswordException) {
                            message = "Password is too weak.";
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            message = "Invalid email format.";
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", e);
                        }

                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        fbReply.loginSuccess(user);
                    } else {
                        Exception e = task.getException();
                        String message = "Login failed.";

                        if (e instanceof FirebaseAuthInvalidUserException) {
                            message = "No account found with this email.";
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            message = "Incorrect password or email.";
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", e);
                        }

                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void logoutUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        mAuth.getInstance().signOut();
        fbReply.logoutSuccess(user);
    }

}
