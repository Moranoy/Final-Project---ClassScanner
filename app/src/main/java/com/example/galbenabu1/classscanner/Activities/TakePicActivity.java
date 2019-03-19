package com.example.galbenabu1.classscanner.Activities;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Handler;

import com.example.galbenabu1.classscanner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Logic.Database.DBManager;
import Logic.Interfaces.MyConsumer;
import Logic.Managers.AnalyticsManager.AnalyticsHelpers.PictureEventsHelper;
import Logic.Managers.AnalyticsManager.AnalyticsManager;
import Logic.Managers.AnalyticsManager.EventParams.PictureEventParams;
import Logic.Models.PictureAudioData;

public class TakePicActivity extends AppCompatActivity {

    private final static String NEW_ALBUM_PICTURE_AUDIO_DATA = "new_album_picture_audio_data";
    private final static String NEW_ALBUM_ID = "new_album_id";

    private static String TAG = "TakePicActivity";
    private static int DELAY_BETWEEN_PICTURES = 5;
    private static final int REQUEST_CAMERA_PERMISSION_RESULT = 0;
    private static final SparseIntArray CAMERA_ORIENTATIONS = new SparseIntArray();
    static {
        CAMERA_ORIENTATIONS.append(Surface.ROTATION_0, 0);
        CAMERA_ORIENTATIONS.append(Surface.ROTATION_90, 90);
        CAMERA_ORIENTATIONS.append(Surface.ROTATION_180, 180);
        CAMERA_ORIENTATIONS.append(Surface.ROTATION_270, 270);
    }

    private CaptureRequest.Builder mCaptureRequestBuilder;
    private MediaRecorder mRecorder;
    private String mFileName = null;

    // Sending pics to DB.
    private DBManager mDBManager;
    private List<PictureAudioData> mPictureList;

    //FireBase
    private StorageReference mStorageRef;
    private PictureAudioData mAudioData;

    // An enum that represents the state of this activity.
    private enum eTakePicActivityState {
        InActive,
        InProgress,
        Paused
    }
    private eTakePicActivityState mActivityState = eTakePicActivityState.InActive;

    //Take pictures in a loop
    private final Handler mHandler = new Handler();

    private final Runnable mTakePictureRunnable = new Runnable() {
        public void run() {
           if(mActivityState.equals(eTakePicActivityState.InProgress)) {
                uploadImage(mTextureView.getBitmap());
                Toast.makeText(getApplicationContext(), "Taking a picture", Toast.LENGTH_SHORT).show();
                mHandler.postDelayed(mTakePictureRunnable, 1000 * DELAY_BETWEEN_PICTURES);
           }
        }
    };


    //Hardware
    private String mCameraID;
    private CameraDevice mCameraDevice;
    private CameraDevice.StateCallback mCameraDeviceStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            startPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraDevice.close();
            mCameraDevice = null;
            Toast.makeText(getApplicationContext(), "Camera Disconnected. Please restart activity.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int i) {
            mCameraDevice.close();
            mCameraDevice = null;
            Toast.makeText(getApplicationContext(), "Camera related error", Toast.LENGTH_SHORT).show();
        }
    };

    //UI
    private Size mPreviewSize;
    private Button mBtnClearPicturesTaken;
    private Button mBtnTakePicture;
    private Button mBtnFinishTakingPictures;
    private TextureView mTextureView;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            setupCamera(width, height);
            connectCamera();
            Log.e(TAG, "Width: " + width + ". Height: " + height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    //Threads
    private HandlerThread mBackgroundHandlerThread;
    private Handler mBackgroundHandler;
    private String mAlbumID;

    // App life cycle functions
    @Override
    protected void onResume() {
        super.onResume();
        this.startBackgroundThread();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(this.mRecorder != null) {
                mRecorder.resume();
            }
        }
        if (mTextureView.isAvailable()) {
            this.setupCamera(mTextureView.getWidth(), mTextureView.getHeight());
            connectCamera();
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION_RESULT) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "App cannot run without camera services", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic);
        mTextureView = findViewById(R.id.bestTextureView);
        mBtnClearPicturesTaken = findViewById(R.id.btnClearPicturesTaken);
        mBtnTakePicture = findViewById(R.id.btnTakeAPic);
        mBtnFinishTakingPictures = findViewById(R.id.btnFinishTakingPictures);
        mStorageRef = FirebaseStorage.getInstance().getReference("images");
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recordAudio.wav";
        mDBManager = new DBManager();
        mPictureList = new ArrayList<>();
        mAlbumID = mDBManager.getNewAlbumID(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mBtnTakePicture.setOnClickListener(this::onStartTakingPictures);
    }

    //@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPause() {
        this.closeCamera();

        if (mRecorder != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mRecorder.pause();
        }

        super.onPause();
    }

    // Camera
    private void setupCamera(int width, int height) {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraID : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraID);
                if (cameraCharacteristics.get(cameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                // Forces the camera orientation to be in landscape mode
                int deviceOrientation = getWindowManager().getDefaultDisplay().getRotation();
                int totalRotation = sensorToDeviceRotation(cameraCharacteristics, deviceOrientation);
                boolean swapRotation = totalRotation == 90 || totalRotation == 270; // Check if in portrait mode
                int rotatedWidth = swapRotation ? height : width;
                int rotatedHeight = swapRotation ? width : height;

                StreamConfigurationMap scaleStreamConfigsMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                mPreviewSize = chooseOptimalSize(scaleStreamConfigsMap.getOutputSizes(SurfaceTexture.class), rotatedWidth, rotatedHeight);
                mCameraID = cameraID;
                Log.e(TAG, "Camera ID: " + mCameraID + "With optimal size: " + mPreviewSize);
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void connectCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //Maybe remove this, depend on the minimum android version we decide on
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(mCameraID, mCameraDeviceStateCallback, mBackgroundHandler);
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(this, "App requires access to camera", Toast.LENGTH_SHORT).show();
                    }

                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_RESULT);
                }
            } else {
                cameraManager.openCamera(mCameraID, mCameraDeviceStateCallback, mBackgroundHandler);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void startPreview() {
        SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface previewSurface = new Surface(surfaceTexture);

        try {
            mCaptureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mCaptureRequestBuilder.addTarget(previewSurface);

            mCameraDevice.createCaptureSession(Arrays.asList(previewSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    try {
                        cameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), null, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(getApplicationContext(), "Unable to setup camera preview", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    private static int sensorToDeviceRotation(CameraCharacteristics cameraCharacteristics, int deviceOrientation) {
        Log.e(TAG, "sensorToDeviceRotation >>");

        int sensorOrientation;

        try {
            sensorOrientation = cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch(Exception e) {
            Log.e(TAG, "sensorToDeviceRotation >> Exception thrown: " + e.getMessage());
            sensorOrientation = 0;
        }

        deviceOrientation = CAMERA_ORIENTATIONS.get(deviceOrientation);

        return (sensorOrientation + deviceOrientation + 360) % 360;
    }

    public void onClearPicturesTakenClick(View v) {
        Log.e(TAG, "onClearPicturesTakenClick >>");

        stopRecording();
        mDBManager.removePicturesFromDB(mAlbumID, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                true, this.mPictureList);
        mPictureList.clear();
        mActivityState = eTakePicActivityState.InActive;
        handleUIForActivityStateChanged();

        Log.e(TAG, "onClearPicturesTakenClick <<");
    }

    public void onStartTakingPictures(View v) {
        Log.e(TAG, "onStartTakingPictures >> activity state: " + mActivityState.name());

        switch(mActivityState) {
            case InActive:
                // Button clicked when state was inactive.
                // Start repetative action to take a picture, every 15 seconds
                this.logStartTakingPicturesEvent();
                startRecording();
                mActivityState = eTakePicActivityState.InProgress;
                mHandler.post(mTakePictureRunnable);
                mBtnTakePicture.setText("Pause");
                break;
            case InProgress:
                // Button clicked when state was InProgress.
                // Pause taking pictures.
                mActivityState = eTakePicActivityState.Paused;
                mBtnTakePicture.setText("Continue");
                break;
            case Paused:
                // Button clicked when state was Paused.
                // Continue taking pictures.
                mActivityState = eTakePicActivityState.InProgress;
                mHandler.post(mTakePictureRunnable);
                mBtnTakePicture.setText("Pause");
                break;
        }

        handleUIForActivityStateChanged();
    }

    private void logStartTakingPicturesEvent() {
        PictureEventParams pictureEventParams = new PictureEventParams();
        AnalyticsManager.getInstance().trackPictureEvent(PictureEventsHelper.ePictureEventType.StartTakingPictures, pictureEventParams);
    }

    public void onFinishTakingPictures(View v) {
        Log.e(TAG, "onFinishTakingPicturesClick >>");
        stopRecording();

        Intent createAlbumIntent = new Intent(TakePicActivity.this, CreateAlbumActivity.class);
        createAlbumIntent.putParcelableArrayListExtra(NEW_ALBUM_PICTURE_AUDIO_DATA,
                (ArrayList<? extends Parcelable>) mPictureList);
        createAlbumIntent.putExtra(NEW_ALBUM_ID, mAlbumID);
        startActivity(createAlbumIntent);

        mActivityState = eTakePicActivityState.InActive;
        handleUIForActivityStateChanged();
    }

    private void handleUIForActivityStateChanged() {
        switch(mActivityState) {
            case InActive:
                mBtnClearPicturesTaken.setVisibility(View.INVISIBLE);
                mBtnTakePicture.setText("Start");
                mBtnFinishTakingPictures.setVisibility(View.INVISIBLE);
                break;
            case InProgress:
                mBtnClearPicturesTaken.setVisibility(View.INVISIBLE);
                mBtnTakePicture.setText("Pause");
                mBtnFinishTakingPictures.setVisibility(View.INVISIBLE);
                break;
            case Paused:
                mBtnClearPicturesTaken.setVisibility(View.VISIBLE);
                mBtnTakePicture.setText("Continue");
                mBtnFinishTakingPictures.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            //Log.e(LOG_TAG, "prepare() failed");
        }

        Date creationDate = Calendar.getInstance().getTime();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        this.mAudioData = mDBManager.addRecordingToPrivateAlbum(userID, this.mAlbumID, creationDate);
        this.mPictureList.add(this.mAudioData);

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        uploadAudio();
    }

    private void uploadAudio() {

        this.mDBManager.uploadAudioFile(this.mFileName, this.mAudioData);
    }

    //Thread
    private void startBackgroundThread() {
        mBackgroundHandlerThread = new HandlerThread("Camera2APISample");
        mBackgroundHandlerThread.start();
        mBackgroundHandler = new Handler(mBackgroundHandlerThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundHandlerThread.quitSafely();
        try {
            mBackgroundHandlerThread.join();
            mBackgroundHandlerThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Preview Size
    private Size chooseOptimalSize(Size[] choices, int width, int height) {
        List<Size> bigEnoughSizes = new ArrayList<Size>();

        for (Size option : choices) {
            //if current size is big enough to display camera preview, add to list
            if (option.getHeight() == option.getWidth() * height / width && option.getWidth() >= width && option.getHeight() >= height) {
                bigEnoughSizes.add(option);
            }
        }

        if (bigEnoughSizes.size() > 1) {
            return Collections.min(bigEnoughSizes, new SizeComparator());
        } else {
            return choices[0];
        }
    }

    private static class SizeComparator implements Comparator<Size> {
        @Override
        public int compare(Size left, Size right) {
            return Long.signum((long) left.getWidth() * left.getHeight() / (long) right.getWidth() * right.getHeight());
        }
    }

    private void uploadImage(Bitmap imageBitmap) {
        //Prepare image to be uploaded
        Log.e(TAG, "Preparing image for upload");
        Bitmap bitmap = imageBitmap;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDBManager.uploadImageToPrivateAlbum(bitmap, userId, mAlbumID,
                (MyConsumer<PictureAudioData>) this::uploadImageSuccess, // on Success
                this::uploadImageFailure); // On failure
    }

    private void uploadImageSuccess(PictureAudioData uploadedPictureData) {
        mPictureList.add(uploadedPictureData);
        Toast.makeText(getApplicationContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
    }

    private void uploadImageFailure() {
        Toast.makeText(getApplicationContext(), "Failed to upload image.", Toast.LENGTH_SHORT).show();
    }
}

