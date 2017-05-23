package com.usac.brayan.mensajeriaarquitectura;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

/*
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
*/

import java.io.IOException;

public class lectorQRCode extends AppCompatActivity {
    /*BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    SurfaceView cameraView;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lector_qrcode);
        /*barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        // creo la camara fuente
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        // listener de ciclo de vida de la camaraf
        final Activity miActivity = this;
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                // verifico si el usuario dio los permisos para la camara
                if (ContextCompat.checkSelfPermission(miActivity, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.d("Barcode", ie.getMessage());
                    }
                } else {
                    Log.d("Barcode","Dont have permisson camera");
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });*/

    }

    public void detect(View v){
        /*barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    String lectura =barcodes.valueAt(0).displayValue.toString();
                    // hacer algo
                    Log.d("Barcode",lectura);
                    finish();
                }else{
                    Log.d("Barcode","Dont have barsources");
                }

                barcodeDetector.release();
            }});*/
    }

}
