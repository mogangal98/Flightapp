package com.mgangal.flightview;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mgangal.flightview.classes.responses.CityResponse;
import com.mgangal.flightview.classes.FlightService;
import com.mgangal.flightview.classes.ListViewAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CityFragment extends Fragment {
    public CityFragment(String countryName, String countryCode) {
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {return inflater.inflate(R.layout.fragment_city, container, false);}

    String countryName,countryCode;

    ArrayList<String> citiesArrayList,citiesCodeList;
    ListViewAdapter adapter;
    private final String BaseUrl = "https://airlabs.co/";                   //api url
    private final String Apikey = "d34236f7-ddd1-4304-bbe9-dc6b08c6a7db";   //apikey

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        citiesArrayList = new ArrayList<>();
        citiesCodeList = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()          //retrofit builder
                .baseUrl(BaseUrl)                           //base url
                .addConverterFactory(GsonConverterFactory.create()) //gson json converter
                .build();
        FlightService service = retrofit.create(FlightService.class);
        Call<CityResponse> call = service.getCities(countryCode,Apikey);
        call.enqueue(new Callback<CityResponse>() {
            @Override
            public void onResponse(Call<CityResponse> call, Response<CityResponse> response) {
                CityResponse cityResponse = response.body();
                for(int i = 0; i <  cityResponse.response.size() ;i++) {
                    citiesArrayList.add(cityResponse.response.get(i).name);         // name = United States of America
                    citiesCodeList.add(cityResponse.response.get(i).city_code);     // code: iso2
                }

                ListView listView = view.findViewById(R.id.listView);
                adapter = new ListViewAdapter(getContext(),citiesArrayList,citiesCodeList);                    //custom adapter
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((adapterView, view13, i, l) -> {
                    String selected = citiesArrayList.get(i);
                    String selectedCode = citiesCodeList.get(i);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, new AirportsFragment(countryName,countryCode,selected,selectedCode));  // selected = sehir ismi
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    ft.commit();
                });
                }

            @Override
            public void onFailure(Call<CityResponse> call, Throwable t) {
            }
        });


        // Cancel Button
        FloatingActionButton goFirstButton = view.findViewById(R.id.goToFirstFragmentButton);
        goFirstButton.setOnClickListener(view12 -> goToMainFragment());

        // Go back to country selection button
        FloatingActionButton goBackButton = view.findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(view1 -> goToCountryFragment());

        SearchView citySearchView = (SearchView) view.findViewById(R.id.citySearchView); // inititate a search view
        CharSequence query = citySearchView.getQuery(); // get the query string currently in the text field
        citySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
                adapter.filter(text);
                return false;
            }
        });
    }

    public void goToCountryFragment(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, new CountryFragment());
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.remove(CityFragment.this);
                ft.addToBackStack(null);
                ft.commit();
    }

    public void goToMainFragment(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new MainFragment());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.remove(CityFragment.this);
        ft.addToBackStack(null);
        ft.commit();
    }
}