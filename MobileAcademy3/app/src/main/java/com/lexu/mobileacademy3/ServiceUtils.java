package com.lexu.mobileacademy3;

import java.util.List;

public interface ServiceUtils {
    interface ServiceCallback {
        void onServiceCompleted(List<NewsArticle> articles);
    }
}
