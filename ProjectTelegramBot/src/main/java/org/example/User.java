package org.example;

public class User extends Todo {


        private String userName; // שם המשתמש

        public User( String userName) {

            this.userName=userName;

        }

        public String getFullName() {
            return userName;
        }
    }


