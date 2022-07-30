package com.mgangal.flightview;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.mgangal.flightview.classes.PerformNetworkRequest;
import com.mgangal.flightview.classes.checkers.CheckEmail;
import com.mgangal.flightview.classes.checkers.CheckPassword;
import com.mgangal.flightview.classes.userDB.UserDBApiHandler;
import com.mgangal.flightview.classes.userDB.UserData;
import com.mgangal.flightview.classes.userDB.UserDatabaseAPI;
import com.mgangal.flightview.databinding.FragmentLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO:
// deprture rrivl ekranina hvayollrinin simgeleri eklenebilir

public class LoginFragment extends Fragment {
    public LoginFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     *keytool -list -v -alias androiddebugkey -keystore %USERPROFILE%\.android\debug.keystore               ---> Password : android
     *SHA1: 45:4E:46:1B:32:15:4D:68:74:EA:FB:70:E1:BE:25:35:7B:AF:1C:21                          --> google login
     *Hsh : RU5GGzIVTWh06vtw4b4lNXuvHCE=                                                         --> facebook login
     */

    private FragmentLoginBinding binding;

    //Google
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;

    //Facebook
    CallbackManager callbackManager;

    //DB
    List<UserData> userList;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        CheckPassword checkPassword = new CheckPassword();
        CheckEmail checkEmail = new CheckEmail();

        //FACEBOOK LOGIN
        FacebookSdk.sdkInitialize(getContext());
        AppEventsLogger.activateApp(getActivity().getApplication());


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // GOOGLE LOGIN
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        account = GoogleSignIn.getLastSignedInAccount(getContext());    // null if not signed in via google
        if (account != null){goToMainFragment();}
        else{binding.btnGoogle.setOnClickListener(view -> { signIn(); });}


        // FACEBOOK LOGIN
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();   // Access token  expires after a while
        if(isLoggedIn){goToMainFragment();}
        callbackManager = CallbackManager.Factory.create();     // callback manager for fb login activity result

        binding.loginButton.setReadPermissions("email");        // permissions
        binding.loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {         // callback registration
            @Override
            public void onSuccess(LoginResult loginResult) {
                String email = loginResult.getAccessToken().getUserId();
                String password = "facebook";
                String desc = "facebookLogin";
                try{login(email,password);
                    goToMainFragment();
                } catch (Exception e) {}
                createUser(email,password,desc);
                goToMainFragment();
            }
            @Override
            public void onCancel() {}
            @Override
            public void onError(FacebookException error) {}
        });


        // DB
        userList = new ArrayList<>();

        // Register
        binding.btnToRegister.setOnClickListener(view -> {
            String email = binding.txtEmailAddress.getText().toString().trim();
            String password = binding.txtPassword.getText().toString().trim();
            String desc = "emailLogin";
            if (checkEmail.check(email).matches("valid")) {
                if (checkPassword.check(password).matches("valid")) {
                    createUser(email, password, desc);
                } else {
                    Toast.makeText(getApplicationContext(), "Password should be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), checkEmail.check(email), Toast.LENGTH_SHORT).show(); // e-mail error message
            }
        });

        // Login
        binding.btnLogin.setOnClickListener(view -> {
            String email = binding.txtEmailAddress.getText().toString().trim();
            String password = binding.txtPassword.getText().toString().trim();
            login(email,password);

        });
        sp = getActivity().getSharedPreferences("com.mgangal.flightview", Context.MODE_PRIVATE);
        // automatic login if previous login exists
        if (!sp.getString("email","").matches("") && sp.getString("password","") != null) goToMainFragment();

        return binding.getRoot();
    }





    // Google Sign in activity
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    // Login activity results
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Results from google and facebook login activites
        // requestCode = 1 --> Google login
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);  // The Task returned from this call is always completed, no need to attach
            handleSignInResult(task);                                                         // a listener.
        }

        //Result returned from facebook
        else if (requestCode == 64206){
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Google sign in handler
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String email = account.getEmail();
            String password = "google";
            String desc = "googleLogin";

            try{
                login(email,password);
                goToMainFragment();
            } catch (Exception e) {
                System.out.println("Google login error: " + e);
            }
            createUser(email,password,desc);
            goToMainFragment();
        } catch (ApiException e) {
            System.out.println("Google sign in error, error code: " + e.getStatusCode());
        }
    }

    // mysql db register
    private void createUser(String email, String password, String desc) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userEmail", email);
        params.put("userPassword", password);
        params.put("userDesc", desc);

        PerformNetworkRequest request = new PerformNetworkRequest(UserDatabaseAPI.URL_CREATE_USER, params, PerformNetworkRequest.CODE_POST_REQUEST,"register",getFragmentManager());
        request.execute();
    }

    //DB login
    private void login(String email, String password){
        HashMap<String, String> params = new HashMap<>();
        params.put("userEmail", email);
        params.put("userPassword", password);

        PerformNetworkRequest request = new PerformNetworkRequest(UserDatabaseAPI.URL_LOGIN, params, PerformNetworkRequest.CODE_POST_REQUEST,"login",getFragmentManager());
        request.execute();
    }

    // Go to main fragment
    private void goToMainFragment(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new MainFragment());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.remove(this);
        ft.addToBackStack(null);
        ft.commit();
    }
}