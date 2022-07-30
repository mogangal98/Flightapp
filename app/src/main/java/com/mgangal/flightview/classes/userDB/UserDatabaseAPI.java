package com.mgangal.flightview.classes.userDB;

public class UserDatabaseAPI {


        private static final String ROOT_URL = "http://192.168.1.4/UsersApi/v1/Api.php?apicall=";

        public static final String URL_CREATE_USER = ROOT_URL + "createuser";   // register
        public static final String URL_UPDATE_USER = ROOT_URL + "updateuser";   // update user info
        public static final String URL_LOGIN = ROOT_URL + "login";              // login

}
