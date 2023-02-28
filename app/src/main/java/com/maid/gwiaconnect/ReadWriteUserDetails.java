package com.maid.gwiaconnect;

public class ReadWriteUserDetails {

    public String fullName,email,password,id;

    public ReadWriteUserDetails() {
    }

    public ReadWriteUserDetails(String fullName, String email, String password,String id) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.id=id;
    }

}
