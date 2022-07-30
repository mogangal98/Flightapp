package com.mgangal.flightview.classes.responses;

import java.util.ArrayList;
import java.util.Date;

public class CityResponse {

    public Request request;
    public ArrayList<Response> response;
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
        public String country_code;
        public double lng;
        public String name;
        public String city_code;
        public double lat;
    }


}
