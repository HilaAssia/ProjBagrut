package com.example.bagrutproject.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Manager {
    String eMail;
    String uID;

    public Manager(){}

    public Manager(String eMail, FirebaseUser user){
        this.eMail=eMail;
        this.uID=user.getUid();
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public void setUID(FirebaseAuth mAuth){ this.uID=mAuth.getUid(); }

    public String getuID() { return uID; }
}
