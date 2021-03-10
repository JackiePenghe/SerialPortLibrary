package com.jackiepenghe.serialportsample;

import android.app.Application;

import com.sscl.baselibrary.files.FileUtil;
import com.sscl.baselibrary.utils.DebugUtil;


/**
 * @author pengh
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DebugUtil.setDebugFlag(true);
        FileUtil.init(this);
    }
}
