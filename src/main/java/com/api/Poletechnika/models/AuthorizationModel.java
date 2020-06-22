package com.api.Poletechnika.models;

public class AuthorizationModel {

    private String auth_token;
    private String user_id;
    private String user_license;

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_license() {
        return user_license;
    }

    public void setUser_license(String user_license) {
        this.user_license = user_license;
    }
}
