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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
            Toast.makeText(activity, "Please select an image!", Toast.LENGTH_SHORT).show();
            errorCallback.run(); // Enable buttons in case of no image
            return;
        }

        MediaManager.get().upload(imageUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Upload started...");
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        Log.d(TAG, "Upload successful: " + imageUrl);
                        successListener.onUploaded(imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Upload failed: " + error.getDescription());
                        errorCallback.run();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d(TAG, "Uploading...");
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Upload rescheduled: " + error.getDescription());
                        errorCallback.run();
                    }
                }).dispatch();
    }

    public interface OnImageUploadedListener {
        void onUploaded(String imageUrl);
    }

    public boolean hasImageChanged() {
        return imageChanged;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void saveImageToLocalFolder(Uri imageUri, String folderName, ImageSaveCallback callback) {
        try {
            // Create directory path
            File directory = new File(activity.getFilesDir(), folderName);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create file for the image
            String fileName = System.currentTimeMillis() + ".jpg";
            File file = new File(directory, fileName);

            // Read data from URI and save to file
            InputStream inputStream = activity.getContentResolver().openInputStream(imageUri);
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            // Return saved file path
            callback.onImageSaved(file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            callback.onError();
        }
    }

    public interface ImageSaveCallback {
        void onImageSaved(String filePath);

        void onError();
    }
}
