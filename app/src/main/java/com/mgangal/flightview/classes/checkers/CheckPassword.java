package com.mgangal.flightview.classes.checkers;

public class CheckPassword {

    private String password;

    public String check(String password){
        String isValid;

        if (password.length() >= 6){
            isValid = "valid";
            return isValid;
        }

        else{
            isValid = "invalid";
            return isValid;
        }
    }

}
