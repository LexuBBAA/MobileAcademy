package com.lexu.mobileacademy3;

import okhttp3.OkHttpClient;

/**
 * Created by lexu on 31.03.2018.
 */

public class Manager {

    private OkHttpClient mOkHttpClient = new OkHttpClient();

    private static class NetworkManagerWrapper {
        static Manager INSTANCE = new Manager();
    }

    public static Manager getInstance() {
        return NetworkManagerWrapper.INSTANCE;
    }



}