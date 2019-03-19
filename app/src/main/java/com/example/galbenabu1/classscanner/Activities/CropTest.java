package com.example.galbenabu1.classscanner.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.Matrix;

import com.example.galbenabu1.classscanner.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
/**
 * Created by galbenabu1 on 25/08/2018.
 */
import com.fenchtose.nocropper.CropperView;

public class CropTest extends AppCompatActivity {
    private Button btnCrop, btnToggleGesture, continueEditingBtn;
    private ImageView btnSnap, btnRotate;
    private CropperView cropperView;
    private Bitmap mBitmap;
    private boolean isSnappedtoCenter = false;
    private int angle = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        initViews();
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lazershark);
        cropperView.setImageBitmap(originalBitmap);
        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });

        btnToggleGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean enabled = cropperView.isGestureEnabled();
                enabled = !enabled;
                cropperView.setGestureEnabled(enabled);
                Toast.makeText(getBaseContext(), "Gesture : " + (enabled ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
            }
        });

        btnSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSnappedtoCenter)
                    cropperView.cropToCenter();
                else
                    cropperView.fitToCenter();
                isSnappedtoCenter = !isSnappedtoCenter;
            }
        });

        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBitmap=cropperView.getCroppedBitmap();
                cropperView.setImageBitmap(rotateBitmap(mBitmap, 0));
            }
        });
    }


    private Bitmap rotateBitmap(Bitmap mBitmap, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
    }

    private void cropImage() {
        mBitmap = cropperView.getCroppedBitmap();
        if (mBitmap != null)
            cropperView.setImageBitmap(mBitmap);
    }

    private void initViews() {
        btnCrop = (Button) findViewById(R.id.btnCrop);
        btnToggleGesture = (Button) findViewById(R.id.btnToggleGesture);
        continueEditingBtn = (Button) findViewById(R.id.continueEditingBtn);
        btnSnap = (ImageView) findViewById(R.id.snap_button);
        btnRotate = (ImageView) findViewById(R.id.rotate_button);
        cropperView = (CropperView) findViewById(R.id.imageView1);
    }

    public void onContinueEditingBtnClicked(View v) {
        Intent intent = new Intent(CropTest.this, ImageEditingActivity.class);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bs);
        intent.putExtra("IMAGE", bs.toByteArray());
        startActivity(intent);
    }
}