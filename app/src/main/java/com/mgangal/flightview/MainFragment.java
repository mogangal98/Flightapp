package com.mgangal.flightview;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mgangal.flightview.classes.responses.FlightResponse;
import com.mgangal.flightview.classes.FlightService;
import com.mgangal.flightview.databinding.FragmentMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Airlabs apikey: d34236f7-ddd1-4304-bbe9-dc6b08c6a7db */

public class MainFragment extends Fragment {

    public MainFragment() {}

    private String selected;        //selected airport, null if app is just created
    private String selectedCode;    //iata code of the selected airport, null if app is just created
    public MainFragment(String selected, String selectedCode) {this.selected = selected;this.selectedCode = selectedCode;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private Gson gson;
    private final String BaseUrl = "https://airlabs.co/"; // api url
    private final String Apikey = "d34236f7-ddd1-4304-bbe9-dc6b08c6a7db";
    private String dep_iata,arr_iata;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    boolean isLoggedIn;
    FragmentMainBinding binding;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,container,false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setLifecycleOwner(getViewLifecycleOwner());
        if(selected!=null){binding.textView.setText(selected+"\n");}
        sp = getActivity().getSharedPreferences("com.mgangal.flightview", Context.MODE_PRIVATE);

        // GOOGLE INIT
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        account = GoogleSignIn.getLastSignedInAccount(getContext());

        // FACEBOOK INIT
        FacebookSdk.sdkInitialize(getContext());
        AppEventsLogger.activateApp(getActivity().getApplication());
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (!sp.getString("email","").matches(""))binding.mailTextView.setText(sp.getString("email",""));       //logged in with email password
        else if (account != null) binding.mailTextView.setText(account.getEmail());                                               //logged in with google
        else if (isLoggedIn) binding.mailTextView.setText(accessToken.getUserId());                                               //logged in with facebook

        // LOGOUT
        binding.logoutButton.setOnClickListener(view -> logout());

        // API CALL
        gson = new GsonBuilder().setLenient().create();
        dep_iata = "esb";   // departures iata code     --default value
        arr_iata = "esb";   // arrivals
        binding.dataButton.setOnClickListener(view -> {
            if (selectedCode != null) {
                getData(selectedCode);
            }
            else{
                binding.textView.setText("Ankara Esenboga International Airport\n"); //default value
                getData(dep_iata);
            }
        });
        binding.countryButton.setOnClickListener(view -> goToCountryFragment());

        return binding.getRoot();
    }


    // DATA CALL METHOD
    String depString,arrString;
    private void getData(String airportCode){
        depString = "DEPARTURES \n\n";
        Retrofit retrofit = new Retrofit.Builder()          //retrofit builder
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create()) //gson json converter
                .build();
        FlightService service = retrofit.create(FlightService.class);
        Call<FlightResponse> call = service.getDepartureFlights(airportCode, Apikey);
        call.enqueue(new Callback<FlightResponse>() {
            @Override
            public void onResponse(Call<FlightResponse> call, Response<FlightResponse> response) {
                FlightResponse flightResponse = response.body();
                assert flightResponse != null;
                for(int i=0; i < flightResponse.response.size();i++){
                    depString +=  flightResponse.response.get(i).dep_time + "\nGoing to: "+flightResponse.response.get(i).arr_iata+
                            "\nAirline:"+flightResponse.response.get(i).airline_icao+"\t\t\t"+ flightResponse.response.get(i).flight_iata+
                            "\nStatus:"+ flightResponse.response.get(i).status+"\n\n";
                }
                binding.gridLeft.setText(depString);
            }

            @Override
            public void onFailure(Call<FlightResponse> call, Throwable t) {System.out.println("departure data error,mainf : " + t);}
        });

        Call<FlightResponse> callArr = service.getArrivalFlights(airportCode,Apikey);
        callArr.enqueue(new Callback<FlightResponse>() {
            @Override
            public void onResponse(Call<FlightResponse> call, Response<FlightResponse> response) {
                arrString = "ARRIVALS \n\n";
                FlightResponse flightResponse = response.body();
                assert flightResponse != null;
                for(int i=0; i < flightResponse.response.size();i++){
                    arrString +=  flightResponse.response.get(i).dep_time + "\nComing from: "+flightResponse.response.get(i).dep_iata+
                            "\nAirline:"+flightResponse.response.get(i).airline_icao+"\t\t\t"+ flightResponse.response.get(i).flight_iata+
                            "\nStatus:"+ flightResponse.response.get(i).status+"\n\n";
                }
                binding.gridRight.setText(arrString);
            }

            @Override
            public void onFailure(Call<FlightResponse> call, Throwable t) {
                System.out.println("arrival data error,mainf : " + t);
            }
        });
    }


    // Cikis yap
    private void logout(){
        sp.edit().remove("email").apply();
        sp.edit().remove("password").apply();

        // Google logout
        if(account != null){
            mGoogleSignInClient.signOut().addOnCompleteListener(task -> {goToLoginFragment();});
        }

        // Facebook logout
        else if (isLoggedIn){
            LoginManager lm = LoginManager.getInstance();
            lm.logOut();
            goToLoginFragment();
        }

        // E-mail password logout
        else{
            goToLoginFragment();
        }
    }

    private void goToLoginFragment(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new LoginFragment());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();}

    private void goToCountryFragment(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new CountryFragment());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

}