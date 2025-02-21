package com.example.bagrutproject.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminPasswordChecker {

    public static String generatePasswordForToday() {
        // יצירת סיסמה שמשתנה כל יום
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }

    public static boolean validatePassword(String inputPassword) {
        // בדיקה אם הסיסמה שהוזנה תואמת
        String generatedPassword = generatePasswordForToday();
        boolean b= generatedPassword.equals(inputPassword);
        return b;
    }

}
