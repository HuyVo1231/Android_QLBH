package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ServiceActivity extends AppCompatActivity implements View.OnClickListener{
    TextView tvNumber;
    Button btnStart;
    MyService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        renderLayout();
    }
    /**
     * Khoi tao giao dien
     */
    public void renderLayout(){
        tvNumber = (TextView) findViewById(R.id.tvNumber);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
    }
    /**
     * Hien thi ket qua
     * @param number
     */
    public void displayNumber(int number){
        tvNumber.setText("" + number);
    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnStart){
            int numberRandom =mService.getRandomNumber();
            displayNumber(numberRandom);
        }
    }

    /**
     * Tạo một đối tượng ServiceConnection chuyên dùng quản lý việc đóng mở
     kết nối đến
     * Service. Override hai phương thức onServiceDisconnected() và
     onServiceConnected() sau
     * nhằm bound đến MyService
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder
                service) {
            // Lien ket MyService, truyen IBinder va lay MyService instance
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // Ngat ket noi
            mService = null;
            mBound = false;
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        //Dùng phương thức bindService(parameter) để gọi đến
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
}