package com.lexu.mobileacademy3;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public interface ServiceUtils {
    class ArticlesWrapper implements Serializable {
        public List<NewsArticle> articles = null;
    }

    interface ServiceCallback {
        void onDataRetrieved(List<NewsArticle> articles);
        void onErrorReceived(String msg);
    }

    interface OnNetworkUpdatesListener {
        class ResponseData<T> {
            private T data = null;
            private int code = -1;
            private String message = null;

            public T getData() {
                return this.data;
            }

            public int getCode() {
                return this.code;
            }

            public String getMessage() {
                return this.message;
            }

            void setData(T data) {
                this.data = data;
            }

            void setCode(int code) {
                this.code = code;
            }

            void setMessage(String message) {
                this.message = message;
            }
        }

        void onSuccess(@NonNull ResponseData responseData);
        void onFailure(@NonNull Exception e);
    }
}
