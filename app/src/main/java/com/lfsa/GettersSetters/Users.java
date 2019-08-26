package com.lfsa.GettersSetters;

public class Users {

    private static String customer;
    private static String customerId;

    public static String getToken_id() {
        return token_id;
    }

    public static void setToken_id(String token_id) {
        Users.token_id = token_id;
    }

    private static String token_id;

    public static String getCustomer() {
        return customer;
    }

    public static void setCustomer(String customer) {
        Users.customer = customer;
    }

    public static String getCustomerId() {
        return customerId;
    }

    public static void setCustomerId(String customeId) {
        Users.customerId = customeId;
    }


}
