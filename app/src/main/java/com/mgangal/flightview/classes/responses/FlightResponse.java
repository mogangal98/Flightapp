package com.mgangal.flightview.classes.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class FlightResponse {

    @SerializedName("response")
    public ArrayList<Response> response = new ArrayList<Response>();

    @SerializedName("request")
    public Request request;

    @SerializedName("terms")
    public String terms;

    public class Agent{
    }

    public class Client{
        public String ip;
        public Geo geo;
        public Connection connection;
        public Device device;
        public Agent agent;
        public Karma karma;
    }

    public class Connection{
        public String type;
        public int isp_code;
        public String isp_name;
    }

    public class Device{
    }

    public class Geo{
        public String country_code;
        public String country;
        public String continent;
        public String city;
        public double lat;
        public double lng;
        public String timezone;
    }

    public class Karma{
        public boolean is_blocked;
        public boolean is_crawler;
        public boolean is_bot;
        public boolean is_friend;
        public boolean is_regular;
    }

    public class Key{
        public int id;
        public String api_key;
        public String type;
        public Date expired;
        public Date registered;
        public int limits_by_hour;
        public int limits_by_minute;
        public int limits_by_month;
        public int limits_total;
    }

    public class Params{
        public String dep_iata;
        public String lang;
    }

    public class Request{
        public String lang;
        public String currency;
        public int time;
        public String id;
        public String server;
        public String host;
        public int pid;
        public Key key;
        public Params params;
        public int version;
        public String method;
        public Client client;
    }

    public class Response{
        public String airline_iata;
        public String airline_icao;
        public String flight_iata;
        public String flight_icao;
        public String flight_number;
        public String dep_iata;
        public String dep_icao;
        public Object dep_terminal;
        public Object dep_gate;
        public String dep_time;
        public String dep_time_utc;
        public String arr_iata;
        public String arr_icao;
        public String arr_terminal;
        public Object arr_gate;
        public String arr_baggage;
        public String arr_time;
        public String arr_time_utc;
        public String arr_estimated;
        public String arr_estimated_utc;
        public String arr_actual;
        public String arr_actual_utc;
        public String cs_airline_iata;
        public String cs_flight_number;
        public String cs_flight_iata;
        public String status;
        public int duration;
        public Object delayed;
        public String aircraft_icao;
        public int arr_time_ts;
        public int dep_time_ts;
        public int arr_estimated_ts;
        public int arr_actual_ts;
    }





}
