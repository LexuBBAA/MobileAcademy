package com.lexu.mobileacademy3;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public interface ServiceUtils {
    class Wrapper<T> implements Serializable {
        public List<T> data = null;
    }

    interface ServiceCallback<T> {
        void onDataRetrieved(List<T> data);
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
