package com.example.myapplication;

import android.app.Application;
import java.io.File;

public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Lấy thư mục cache để chứa file .dex
        File dexOutputDir = getCodeCacheDir();

        // Đặt quyền chỉ đọc cho thư mục
        dexOutputDir.setReadOnly();
    }
}
