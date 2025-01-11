package com.example.myapplication.cloudinary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Map;

public class ImageHelper {
    private static final int GALLERY_REQ_CODE = 3000;
    private static final String TAG = "ImageHelper";
    private final Activity activity;
    private final ImageView imageView;
    private Uri imageUri;
    private boolean imageChanged = false;

    public ImageHelper(Activity activity, ImageView imageView) {
        this.activity = activity;
        this.imageView = imageView;
    }

    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, GALLERY_REQ_CODE);
    }

    public void handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQ_CODE && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            imageChanged = true;
        }
    }

    public void uploadImage(OnImageUploadedListener successListener, Runnable errorCallback) {
        if (imageUri == null) {
            Toast.makeText(activity, "Vui lòng chọn một ảnh!", Toast.LENGTH_SHORT).show();
            errorCallback.run(); // Bật lại nút khi không có ảnh
            return;
        }

        MediaManager.get().upload(imageUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Đang tải lên...");
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        Log.d(TAG, "Upload thành công: " + imageUrl);
                        successListener.onUploaded(imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Upload thất bại: " + error.getDescription());
                        errorCallback.run();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d(TAG, "Đang tải lên...");
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Upload bị hoãn lại: " + error.getDescription());
                        errorCallback.run(); // Bật lại nút khi upload bị hoãn
                    }
                }).dispatch();
    }

    public interface OnImageUploadedListener {
        void onUploaded(String imageUrl);
    }

    public boolean hasImageChanged() {
        return imageChanged; // Trả về trạng thái ảnh
    }
    public Uri getImageUri() {
        return imageUri;
    }
}
