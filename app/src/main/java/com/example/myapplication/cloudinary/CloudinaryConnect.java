package com.example.myapplication.cloudinary;

import android.content.Context;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryConnect {

    // Biến static để theo dõi trạng thái khởi tạo
    private static boolean isInitialized = false;

    // Phương thức cấu hình Cloudinary
    public static void initCloudinaryConfig(Context context) {
        if (!isInitialized) {
            // Cấu hình Cloudinary
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", "daz1udjeb");
            config.put("api_key", "692586855575142");
            config.put("api_secret", "_2MfEZB9hOuXENcRAGqx-30JvPQ");

            // Khởi tạo Cloudinary MediaManager chỉ khi chưa được khởi tạo
            MediaManager.init(context, config);
            isInitialized = true;  // Đánh dấu đã khởi tạo
        }
    }
}
