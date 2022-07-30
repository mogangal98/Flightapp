package com.mgangal.flightview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        /** back button closes the app in login ve main frgments */
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof MainFragment || fragment instanceof LoginFragment){
            finish();
        }
        else {super.onBackPressed();}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager(); //fmanager initialize üstte yap
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //fragment işlemelri için
        fragmentTransaction.replace(R.id.fragment_container, new LoginFragment()).commit();
    }
}