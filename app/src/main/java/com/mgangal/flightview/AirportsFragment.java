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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mgangal.flightview.classes.responses.AirportResponse;
import com.mgangal.flightview.classes.FlightService;
import com.mgangal.flightview.classes.ListViewAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AirportsFragment extends Fragment {

    public AirportsFragment(String countryName,String countryCode,String cityName, String cityCode) {
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.cityName = cityName;
        this.cityCode = cityCode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {return inflater.inflate(R.layout.fragment_airports, container, false);}

    String countryName,countryCode,cityName,cityCode;
    ArrayList<String> airportsArrayList,airportsCodeList;

    ListViewAdapter adapter;
    private final String BaseUrl = "https://airlabs.co/"; // api url
    private final String Apikey = "d34236f7-ddd1-4304-bbe9-dc6b08c6a7db"; //api key

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        airportsArrayList = new ArrayList<>();
        airportsCodeList = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()          //retrofit builder
                .baseUrl(BaseUrl)                           //base url
                .addConverterFactory(GsonConverterFactory.create()) //gson json converter
                .build();
        FlightService service = retrofit.create(FlightService.class);
        Call<AirportResponse> call = service.getAirports(cityCode,Apikey);
        call.enqueue(new Callback<AirportResponse>() {
            @Override
            public void onResponse(Call<AirportResponse> call, Response<AirportResponse> response) {
                AirportResponse airportResponse = response.body();
                for(int i = 0; i <  airportResponse.response.size() ;i++) {
                    airportsArrayList.add(airportResponse.response.get(i).name);
                    airportsCodeList.add(airportResponse.response.get(i).iata_code);
                }

                ListView listView = view.findViewById(R.id.listView);
                adapter = new ListViewAdapter(getContext(),airportsArrayList,airportsCodeList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((adapterView, view12, i, l) -> {
                    String selected = airportsArrayList.get(i);
                    String selectedCode = airportsCodeList.get(i);
                    goToMainFragment(selected,selectedCode);
                });
            }

            @Override
            public void onFailure(Call<AirportResponse> call, Throwable t) {
            }
        });

        // Cancel Button
        FloatingActionButton goFirstButton = view.findViewById(R.id.goToFirstFragmentButton);
        goFirstButton.setOnClickListener(view1 -> cancelSelection());

        // Go back to city selection button
        FloatingActionButton goBackButton = view.findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(view1 -> goToCityFragment());
    }


    private void goToMainFragment(String selected, String selectedCode){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new MainFragment(selected,selectedCode));
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void goToCityFragment(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new CityFragment(countryName,countryCode));
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.remove(AirportsFragment.this);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void cancelSelection(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new MainFragment());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.remove(AirportsFragment.this);
        ft.addToBackStack(null);
        ft.commit();
    }

}