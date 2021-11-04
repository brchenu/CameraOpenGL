package com.example.cameraopengl;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity {
    private GLView glView;

    private static final int CAMERA_PERM_CODE = 707;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }

        glView = new GLView(this);

        initCamera();

        setContentView(glView);
    }

    private void initCamera() {
        Camera2 cameraGL2 = new Camera2(this);

        glView.setSurfaceCallback(() -> {
            cameraGL2.openCamera(glView.getRendererSurfaceTexture());
        });
    }

    @Override
    protected void onPause() {
        glView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCamera();
            }
        }
    }
}