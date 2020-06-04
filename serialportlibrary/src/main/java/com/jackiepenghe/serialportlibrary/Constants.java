package com.jackiepenghe.serialportlibrary;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

public class Constants {

    static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r);
        }
    };

    static final Handler HANDLER = new Handler();
}
