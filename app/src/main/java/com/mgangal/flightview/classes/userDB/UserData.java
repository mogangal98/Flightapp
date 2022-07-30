package com.mgangal.flightview.classes.userDB;

public class UserData {

        private int id;
        private String userEmail, userPassword;
        private String userDesc;

        public UserData(int id, String userEmail, String userPassword, String userDesc) {
            this.id = id;
            this.userEmail = userEmail;
            this.userPassword = userPassword;
            this.userDesc = userDesc;
        }

        public int getId() {
            return id;
        }

        public String getEmail() {
            return userEmail;
        }

        public String getPassword() {
            return userPassword;
        }

        public String getDesc() {
            return userDesc;
        }

}
