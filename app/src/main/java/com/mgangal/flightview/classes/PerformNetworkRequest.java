package com.mgangal.flightview.classes;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mgangal.flightview.MainFragment;
import com.mgangal.flightview.R;
import com.mgangal.flightview.classes.userDB.UserDBApiHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
    String url; // url to send request
    HashMap<String, String> params; // parameters: e-mail , password , description
    int requestCode; //request code:  1024 = GET   , 1025 = POST
    SharedPreferences sp;

    String instance;
    FragmentManager fm;

    public static final int CODE_GET_REQUEST = 1024;
    public static final int CODE_POST_REQUEST = 1025;

    // Constructor
    public PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode,String instance,FragmentManager fm) {
        this.url = url;
        this.params = params;
        this.requestCode = requestCode;
        this.fm = fm;
        this.instance = instance;
    }

    // Pre Execute,
    @Override
    public void onPreExecute() {
        super.onPreExecute();
    }

    // Method to get respone from request
    @Override
    public void onPostExecute(String s) {
        super.onPostExecute(s);
        sp = getApplicationContext().getSharedPreferences("com.mgangal.flightview", Context.MODE_PRIVATE);

        try {
            JSONObject object = new JSONObject(s);
            if (!object.getBoolean("error")) { // logging in
                Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                // E-mail password --> Sharedpreferences
                if(instance.matches("login")) {
                    sp.edit().putString("email", object.getJSONObject("usersdb").getString("userEmail")).apply();
                    sp.edit().putString("pass", object.getJSONObject("usersdb").getString("userPassword")).apply();
                    goToMainFragment();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Background network operation
    @Override
    public String doInBackground(Void... voids) {
        UserDBApiHandler requestHandler = new UserDBApiHandler();

        if (requestCode == CODE_POST_REQUEST)
            return requestHandler.sendPostRequest(url, params);

        if (requestCode == CODE_GET_REQUEST)
            return requestHandler.sendGetRequest(url);

        return null;
    }

    // method to change the fragment after login
    private void goToMainFragment(){
        FragmentTransaction ft = fm.beginTransaction()  ;
        ft.replace(R.id.fragment_container, new MainFragment("Ankara","TR"));
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

}
