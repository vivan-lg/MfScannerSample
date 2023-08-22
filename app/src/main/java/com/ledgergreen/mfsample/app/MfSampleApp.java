package com.ledgergreen.mfsample.app;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.morefun.yapi.engine.DeviceServiceEngine;

import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

@HiltAndroidApp
public class MfSampleApp extends Application {

    private final String TAG = MfSampleApp.class.getName();
    private final String SERVICE_ACTION = "com.morefun.ysdk.service";
    private final String SERVICE_PACKAGE = "com.morefun.ysdk";
    private static MfSampleApp instance;
    private DeviceServiceEngine deviceServiceEngine = null;


    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new DebugTree());

        instance = this;
        bindDeviceService();
    }

    public static Application getInstance() {
        return instance;
    }

    public DeviceServiceEngine getDeviceService() {
        return deviceServiceEngine;
    }

    public void bindDeviceService() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (null != deviceServiceEngine) {
                        return;
                    }

                    Intent intent = new Intent();
                    intent.setAction(SERVICE_ACTION);
                    intent.setPackage(SERVICE_PACKAGE);
                    Log.e(TAG, "======bindService======");
                    bindService(intent, connection, Context.BIND_AUTO_CREATE);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            deviceServiceEngine = null;
            Log.e(TAG, "======onServiceDisconnected======");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            deviceServiceEngine = DeviceServiceEngine.Stub.asInterface(service);
            Log.d(TAG, "======onServiceConnected======");

            try {
                DeviceHelper.reset();
                DeviceHelper.initDevices(MfSampleApp.this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            linkToDeath(service);
        }

        private void linkToDeath(IBinder service) {
            try {
                service.linkToDeath(() -> {
                    Log.d(TAG, "======binderDied======");
                    deviceServiceEngine = null;
                    bindDeviceService();
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };


}
