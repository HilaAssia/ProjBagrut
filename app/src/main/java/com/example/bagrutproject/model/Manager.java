package com.example.bagrutproject.model;

import com.google.firebase.auth.FirebaseAuth;

public class Manager {
    private String eMail;
    private String uID;

    public Manager(){}

    public Manager(String eMail, String uID){
        this.eMail=eMail;
        this.uID=uID;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public void setuID(FirebaseAuth mAuth){ this.uID=mAuth.getUid(); }

    public String getuID() { return uID; }
}
