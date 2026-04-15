package com.example.hethongbangiay.models;

public final class VaiTro {
    public static final String USER = "USER";
    public static final String USER_ADMIN = "USER_ADMIN";
    public static final String ORDER_ADMIN = "ORDER_ADMIN";
    public static final String PRODUCT_ADMIN = "PRODUCT_ADMIN";
    public static final String SUPER_ADMIN = "SUPER_ADMIN";

    // Legacy role to keep backward compatibility with old data.
    public static final String ADMIN = "ADMIN";

    private VaiTro() { }
}