package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.Random;

public class MyService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    // lay so ngau nhien
    private final Random mGenerator = new Random();
    public MyService() {
    }
    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    /**
     * lay so ngau nhien
     * @return
     */
    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }
}