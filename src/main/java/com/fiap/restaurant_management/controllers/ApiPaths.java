package com.fiap.restaurant_management.controllers;

public final class ApiPaths {

    private ApiPaths() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String API_BASE = "/api";
    public static final String V1 = API_BASE + "/v1";

    public static final String V1_USERS = V1 + "/users";
    public static final String V1_USERS_ME = V1_USERS + "/me";

    public static final String V1_AUTH = V1 + "/auth";
    public static final String V1_AUTH_REGISTER = V1_AUTH + "/register";
    public static final String V1_AUTH_LOGIN = V1_AUTH + "/login";

}
