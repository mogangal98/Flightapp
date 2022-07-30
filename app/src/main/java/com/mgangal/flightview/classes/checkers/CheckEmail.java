package com.mgangal.flightview.classes.checkers;

import android.util.Patterns;

import java.util.regex.Pattern;

public class CheckEmail {

    private String email;

    public String check(String email){
        String isValid;
        Pattern pattern = Patterns.EMAIL_ADDRESS;

        if (pattern.matcher(email).matches()){
            isValid = "valid";
            return isValid;
        }

        else if(email.matches("")){
            isValid = "E-mail address is empty.";
            return isValid;
        }

        else{
            isValid = "Invalid E-mail";
            return isValid;
        }
    }

}
