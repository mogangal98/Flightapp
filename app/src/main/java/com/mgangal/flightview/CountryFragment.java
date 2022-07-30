package com.mgangal.flightview;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mgangal.flightview.classes.responses.CountryResponse;
import com.mgangal.flightview.classes.FlightService;
import com.mgangal.flightview.classes.ListViewAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CountryFragment extends Fragment {

    public CountryFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    ArrayList<String> countryNameList;
    ArrayList<String> countryCodeList;
    ListViewAdapter adapter;
    private final String BaseUrl = "https://airlabs.co/";
    private final String Apikey = "d34236f7-ddd1-4304-bbe9-dc6b08c6a7db";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        countryNameList = new ArrayList<>();
        countryCodeList = new ArrayList<>();

        Retrofit retrofit = new Retrofit.Builder()          //retrofit builder
                .baseUrl(BaseUrl)                           //base url
                .addConverterFactory(GsonConverterFactory.create()) //gson json converter
                .build();
        FlightService service = retrofit.create(FlightService.class);
        Call<CountryResponse> call = service.getCountries(Apikey);
        call.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
                CountryResponse countryResponse = response.body();
                assert countryResponse != null;
                for(int i = 0; i < countryResponse.response.size();i++){
                    countryNameList.add(countryResponse.response.get(i).name);
                    countryCodeList.add(countryResponse.response.get(i).code);
                }

                ListView listView = view.findViewById(R.id.listViewCountries);
                adapter = new ListViewAdapter(getContext(),countryNameList,countryCodeList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener((adapterView, view1, i, l) -> {
                    String selectedCountry = countryNameList.get(i);
                    String selectedCountryCode = countryCodeList.get(i);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, new CityFragment(selectedCountry,selectedCountryCode));
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.addToBackStack(null);
                    ft.commit();
                });

            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {

            }
        });

        FloatingActionButton goFirstButton = view.findViewById(R.id.goToFirstFragmentButton);
        goFirstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, new MainFragment());
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.remove(CountryFragment.this);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        SearchView simpleSearchView = (SearchView) view.findViewById(R.id.simpleSearchView); // search view
        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;
                if(!countryNameList.isEmpty() && !countryCodeList.isEmpty())adapter.filter(text);
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_country, container, false);
    }

}