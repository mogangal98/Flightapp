package com.mgangal.flightview.classes;

import com.mgangal.flightview.classes.responses.AirportResponse;
import com.mgangal.flightview.classes.responses.CityResponse;
import com.mgangal.flightview.classes.responses.CountryResponse;
import com.mgangal.flightview.classes.responses.FlightResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlightService {

    /**           base url : https://airlabs.co/            */

    @GET("api/v9/schedules?")
    Call<FlightResponse> getDepartureFlights(@Query("dep_iata") String dep_iata, @Query("api_key") String api_key);

    @GET("api/v9/schedules?")
    Call<FlightResponse> getArrivalFlights(@Query("arr_iata") String dep_iata, @Query("api_key") String api_key);

    @GET("api/v9/countries?")
    Call<CountryResponse> getCountries(@Query("api_key") String api_key);

    @GET("api/v9/cities?")
    Call<CityResponse> getCities(@Query("country_code") String country_code, @Query("api_key") String api_key);

    @GET("api/v9/airports?")
    Call<AirportResponse> getAirports(@Query("city_code") String city_code, @Query("api_key") String api_key);

}
