package com.ledgergreen.mfsample;

import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.ledgergreen.mfsample.app.DeviceHelper;
import com.morefun.yapi.device.scanner.OnScannedListener;
import com.morefun.yapi.device.scanner.ScannerConfig;
import com.morefun.yapi.device.scanner.ZebraParam;

import java.util.ArrayList;

public class MainActivity extends ComponentActivity {

    private static final String TAG = MainActivity.class.getName();

    private int callbackCalledCounter = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_layout);
        findViewById(R.id.scan_button).setOnClickListener(view -> startScanner());
    }

    private void startScanner() {
        try {
            DeviceHelper.getInnerScanner().initScanner(scannerInitBundle());

            Log.d(TAG, "startScan");
            callbackCalledCounter = 0; // clear counter before each startScan() call
            DeviceHelper.getInnerScanner().startScan(60, new OnScannedListener.Stub() {
                @Override
                public void onScanResult(int retCode, byte[] bytes) {
                    callbackCalledCounter++;
                    Log.d(TAG, "ScanResult called " + callbackCalledCounter + " times");
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScanner();
    }

    public void stopScanner() {
        try {
            DeviceHelper.getInnerScanner().stopScan();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Bundle scannerInitBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(ScannerConfig.COMM_SCANNER_TYPE, 0);
        ArrayList<Parcelable> arrayList = new ArrayList<>();
        arrayList.add(new ZebraParam(ScannerConfig.ZerbaParamNum.PICKLIST_MODE, ScannerConfig.ZebraParamVal.SUPP_AUTOD));
        arrayList.add(new ZebraParam((short) 681, (byte) 1));
        arrayList.add(new ZebraParam((short) 685, (byte) 11));
        arrayList.add(new ZebraParam((short) 15, (byte) 1));
        arrayList.add(new ZebraParam((short) 277, (byte) 1));
        bundle.putParcelableArrayList(ScannerConfig.ZEBRA_PARAM, arrayList);
        return bundle;
    }
}
