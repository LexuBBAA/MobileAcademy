package com.lexu.mobileacademy2;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.lexu.mobileacademy2.QuoteRequester.StatusCodes.BAD_REQUEST;
import static com.lexu.mobileacademy2.QuoteRequester.StatusCodes.SUCCESS;

/**
 * Created by lexu on 28.03.2018.
 */

class QuoteRequester {

    public interface StatusCodes {
        int SUCCESS = 200;
        int BAD_REQUEST = 400;
        int NOT_FOUND = 404;
        int METHOD_FAILURE = 420;
    }

    private static final String API = "http://quotesondesign.com/wp-json/posts?filter[orderby]=rand&filter[posts_per_page]=1";
    private static final String API_MULTIPLE = "http://quotesondesign.com/wp-json/posts?filter[orderby]=rand&filter[posts_per_page]=20";

    private OkHttpClient mClient = new OkHttpClient();

    QuoteRequester() {
    }

    void getQuote(final OnQuoteEventHandler handler) {
        mClient.newCall(new Request.Builder().url(API).build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                OnQuoteEventHandler.ResponseData<IOException> result = new OnQuoteEventHandler.ResponseData<IOException>();
                result.setCode(BAD_REQUEST);
                result.setData(e);
                result.setMsg(e.getLocalizedMessage());

                handler.onFailure(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody body = response.body();
                int code = response.code();
                String msg = response.message();

                switch (code) {
                    case SUCCESS:
                        build(body.string(), code, msg, handler);
                        break;

                    default:
                        fail(code, msg, handler);
                }
            }
        });
    }

    void getQuotes(final OnQuoteEventHandler handler) {
        mClient.newCall(new Request.Builder().url(API_MULTIPLE).build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                OnQuoteEventHandler.ResponseData<IOException> result = new OnQuoteEventHandler.ResponseData<IOException>();
                result.setCode(BAD_REQUEST);
                result.setData(e);
                result.setMsg(e.getLocalizedMessage());

                handler.onFailure(result);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                ResponseBody body = response.body();
                int code = response.code();
                String msg = response.message();

                switch (code) {
                    case SUCCESS:
                        buildMultiple(body.string(), code, msg, handler);
                        break;

                    default:
                        fail(code, msg, handler);
                }
            }
        });
    }

    private void build(@Nullable String jsonData, int code, String msg, @NonNull OnQuoteEventHandler handler) {
        Quote newQuote = new QuoteParser(jsonData).doInBackground();

        OnQuoteEventHandler.ResponseData responseData;
        if(newQuote == null) {
            Error e = new Error("Data: " + jsonData);

            responseData = new OnQuoteEventHandler.ResponseData<Error>();
            responseData.setMsg(msg);
            responseData.setCode(code);
            responseData.setData(e);
        } else {
            responseData = new OnQuoteEventHandler.ResponseData<Quote>();
            responseData.setData(newQuote);
            responseData.setMsg(msg);
            responseData.setCode(code);
        }

        handler.onSuccess(responseData);
    }

    private void buildMultiple(String string, int code, String msg, final OnQuoteEventHandler handler) {
        QuoteParserMultiple parser = new QuoteParserMultiple(string, new QuoteParserMultiple.OnQuoteParsedListener() {
            @Override
            public void onQuoteParsed(Quote newQuote) {
                OnQuoteEventHandler.ResponseData<Quote> responseData = new OnQuoteEventHandler.ResponseData<Quote>();
                responseData.setCode(SUCCESS);
                responseData.setMsg("SUCCESS");
                responseData.setData(newQuote);

                handler.onSuccess(responseData);
            }
        });

        parser.doInBackground();
    }

    private void fail(int code, String msg, OnQuoteEventHandler handler) {
        OnQuoteEventHandler.ResponseData<Void> responseData = new OnQuoteEventHandler.ResponseData<Void>();
        responseData.setMsg(msg);
        responseData.setCode(code);
        responseData.setData(null);

        handler.onSuccess(responseData);
    }
}

class QuoteParserMultiple extends  AsyncTask<Void, Quote, Void> {

    private String jsonData = null;
    private OnQuoteParsedListener mOnQuoteParsedListener = null;

    public QuoteParserMultiple(@Nullable String jsonData, OnQuoteParsedListener listener) {
        this.jsonData = jsonData;
        mOnQuoteParsedListener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(this.jsonData);
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonQuote = jsonArray.getJSONObject(i);
                Quote newQuote = Quote.parseFromJson(jsonQuote);
                mOnQuoteParsedListener.onQuoteParsed(newQuote);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    interface OnQuoteParsedListener {
        void onQuoteParsed(Quote newQuote);
    }
}

class QuoteParser extends AsyncTask<Void, Void, Quote> {

    private String jsonData = null;

    QuoteParser(@Nullable String jsonData) {
        this.jsonData = jsonData;
    }

    @Nullable
    @Override
    protected Quote doInBackground(Void... params) {
        Quote result = null;
        try {
            if(this.jsonData != null) {
                JSONArray jsonArray = new JSONArray(this.jsonData);
                if (jsonArray.length() != 0) {
                    JSONObject json = jsonArray.getJSONObject(0);
                    result = Quote.parseFromJson(json);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}

interface OnQuoteEventHandler {
    class ResponseData<T> {
        private T data = null;
        private int code = -1;
        private String msg = null;

        void setData(T data) {
            this.data = data;
        }

        void setCode(int code) {
            this.code = code;
        }

        void setMsg(String msg) {
            this.msg = msg;
        }

        public T getData() {
            return data;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    void onSuccess(ResponseData response);
    void onFailure(ResponseData responseData);
}

class Quote implements Serializable {
    interface QuoteUtils {
        String FIELD_ID = "ID";
        String FIELD_TITLE = "title";
        String FIELD_CONTENT = "content";
        String FIELD_SOURCE = "link";
    }

    private int mId = -1;
    private String mTitle = null;
    private String mContent = null;
    private String mSource = null;

    private Quote(int id, String title, String content, String source) {
        mId = id;
        mTitle = title;
        mContent = content;
        mSource = source;
    }

    void setId(int id) {
        mId = id;
    }

    void setTitle(String title) {
        mTitle = title;
    }

    void setContent(String content) {
        mContent = content;
    }

    void setSource(String source) {
        mSource = source;
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getContent() {
        return mContent;
    }

    public String getSource() {
        return mSource;
    }

    static Quote parseFromJson(JSONObject json) throws JSONException {
        int id = json.getInt(QuoteUtils.FIELD_ID);
        String title = json.getString(QuoteUtils.FIELD_TITLE);
        String content = json.getString(QuoteUtils.FIELD_CONTENT);
        String source = json.getString(QuoteUtils.FIELD_SOURCE);

        return new Quote(id, title, content, source);
    }
}