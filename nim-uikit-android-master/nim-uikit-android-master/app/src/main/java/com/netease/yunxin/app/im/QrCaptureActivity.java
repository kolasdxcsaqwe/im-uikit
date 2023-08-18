package com.netease.yunxin.app.im;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.netease.yunxin.app.im.databinding.ActivityLoginBinding;
import com.netease.yunxin.app.im.databinding.ActivityQrScannerBinding;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;
import com.netease.yunxin.kit.common.ui.utils.ToastX;
import com.netease.yunxin.kit.corekit.im.utils.RouterConstant;
import com.netease.yunxin.kit.corekit.route.XKitRouter;

import java.util.Random;

public class QrCaptureActivity extends BaseActivity implements
        DecoratedBarcodeView.TorchListener{



    ActivityQrScannerBinding aqb;
    private CaptureManager capture;

    private ViewfinderView viewfinderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aqb= ActivityQrScannerBinding.inflate(getLayoutInflater());
        setContentView(aqb.getRoot());

        viewfinderView = findViewById(R.id.zxing_viewfinder_view);
        aqb.zxingBarcodeScanner.setTorchListener(this);
        if(hasFlash())
        {
            aqb.switchFlashlight.setVisibility(View.GONE);
        }

        capture = new CaptureManager(this, aqb.zxingBarcodeScanner);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.setShowMissingCameraPermissionDialog(false);
        capture.decode();
        aqb.zxingBarcodeScanner.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                aqb.zxingBarcodeScanner.pause();
                XKitRouter.withKey(RouterConstant.PATH_USER_INFO_PAGE).withParam(RouterConstant.KEY_ACCOUNT_ID_KEY,result.getResult().toString()).withContext(QrCaptureActivity.this).navigate();
                finish();
            }
        });

        changeMaskColor(null);
        changeLaserVisibility(true);
    }



    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }


    public void switchFlashlight(View view) {
        if (getString(R.string.turn_on_flashlight).equals(aqb.switchFlashlight.getText())) {
            aqb.zxingBarcodeScanner.setTorchOn();
        } else {
            aqb.zxingBarcodeScanner.setTorchOff();
        }
    }

    @Override
    public void onTorchOn() {
        aqb.switchFlashlight.setText(R.string.turn_off_flashlight);
    }

    @Override
    public void onTorchOff() {
        aqb.switchFlashlight.setText(R.string.turn_on_flashlight);
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void changeMaskColor(View view) {
        Random rnd = new Random();
        int color = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        viewfinderView.setMaskColor(color);
    }

    public void changeLaserVisibility(boolean visible) {
        viewfinderView.setLaserVisibility(visible);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
