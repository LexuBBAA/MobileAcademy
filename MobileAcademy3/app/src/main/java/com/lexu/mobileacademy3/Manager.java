package com.lexu.mobileacademy3;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by lexu on 31.03.2018.
 */

public final class Manager {

    abstract static class NetworkUtils {
        enum CallbackType {
            NEWS
        }

        private static Callback newsCallback(final ServiceUtils.OnNetworkUpdatesListener listener) {
            return new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    listener.onFailure(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    int code = response.code();
                    String message = response.message();
                    ResponseBody body = response.body();

                    ServiceUtils.OnNetworkUpdatesListener.ResponseData<String> responseData = new ServiceUtils.OnNetworkUpdatesListener.ResponseData<String>();
                    responseData.setCode(code);
                    responseData.setMessage(message);
                    switch (code) {
                        case 200: {
                            String jsonString = body.string();
                            responseData.setData(jsonString);
                            break;
                        }

                        default:
                            responseData.setData(null);
                    }

                    listener.onSuccess(responseData);
                }
            };
        }

        @Nullable
        static Callback build(CallbackType type, ServiceUtils.OnNetworkUpdatesListener listener) {
            switch (type) {
                case NEWS:
                    return newsCallback(listener);
            }

            return null;
        }
    }

    private OkHttpClient mOkHttpClient = new OkHttpClient();

    public Manager() {
    }

    public void requestNews(int pageNo, ServiceUtils.OnNetworkUpdatesListener listener) {
        Bundle bundle = new Bundle();
        bundle.putInt("pageNo", pageNo);
        mOkHttpClient.newCall(
                RequestBuilder.build(RequestBuilder.RequestType.NEWS, bundle)
        ).enqueue(
                NetworkUtils.build(NetworkUtils.CallbackType.NEWS, listener)
        );
    }
}

//  https://newsapi.org/v2/top-headlines?country=ro&1e0dbd5e81714d7abd42cba83a64d2b6
final class RequestBuilder {
    private static final String BASE_URL = "https://newsapi.org/v2/";
    private static final String HEADLINES_URL = "top-headlines?";
    private static final String COUNTRY_QUERY = "country";
    private static final String PAGE_SIZE_QUERY = "pageSize";
    private static final String API_KEY = "1e0dbd5e81714d7abd42cba83a64d2b6";
    private static final String AUTH_KEY = "X-Api-Key";
    private static final String EVERYTHING = "everything?";

    enum RequestType {
        NEWS_COUNTRY, NEWS
    }

    enum RequestQueryType {
        COUNTRY
    }

    @Nullable
    static Request build(@NonNull RequestType type, Bundle data) {
        switch (type) {
            case NEWS: {
                return buildNews(data.getInt("pageNo"));
            }
            case NEWS_COUNTRY: {
                return buildNewsWithCountry();
            }
            default: {
                return null;
            }
        }
    }

    private static Request buildNews(int pageNo) {
        return new Request.Builder()
                .url(BASE_URL + HEADLINES_URL + "category=technology&page=" + pageNo)
                .addHeader(AUTH_KEY, API_KEY)
                .build();
    }
    
    private static Request buildNewsWithCountry() {
        return new Request.Builder()
                .url(BASE_URL + HEADLINES_URL + COUNTRY_QUERY + '=' + "ro")
                .addHeader(AUTH_KEY, API_KEY)
                .build();
    }

    private static RequestBody buildBody(RequestQueryType type, String selector) {
        return new FormBody.Builder()
                .add(COUNTRY_QUERY, selector)
                .build();
    }
}