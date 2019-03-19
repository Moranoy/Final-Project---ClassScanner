package com.example.galbenabu1.classscanner.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.galbenabu1.classscanner.R;
import com.fenchtose.nocropper.CropperView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.NoSuchElementException;

import Logic.ConvolutionMatrix;
import Logic.Models.Album;

public class ImageEditingActivity extends AppCompatActivity {

    private static final String TAG = "ImageEditingActivity";
    private Button finishEditingBtn;
    private ImageView sharpnessImageView, imageToEdit;
    private FrameLayout frameLayout;
    private Bitmap currImage, oldImage;
    private SeekBar sb_value;
    private String path;
    private Album album;
    private boolean isPrivateAlbum;
    private boolean isSharpnessClicked = false;
    private FirebaseStorage storage;
    private StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editing);

        getDataFromIntentExtra();

        storage = FirebaseStorage.getInstance();
        ref = storage.getReference().child("Images/").child(path.substring(path.lastIndexOf("Images/") + 7));

        frameLayout=(FrameLayout)findViewById(R.id.top_frame);
        sb_value = (SeekBar) findViewById(R.id.sb_value);
        imageToEdit = (ImageView) findViewById(R.id.im_brightness);
        sharpnessImageView = (ImageView) findViewById(R.id.sharpnessImageView);
        finishEditingBtn = (Button) findViewById(R.id.finishEditingBtn);

        if (getIntent().hasExtra("IMAGE")) {
            Bitmap b = BitmapFactory.decodeByteArray(
                    getIntent().getByteArrayExtra("IMAGE"), 0, getIntent().getByteArrayExtra("IMAGE").length);
            imageToEdit.setImageBitmap(b);
            //fitImageToImageView();
            oldImage = ((BitmapDrawable) imageToEdit.getDrawable()).getBitmap();
            currImage = oldImage;
        }


        sb_value.setProgress(100);
        sb_value.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                imageToEdit.setColorFilter(setBrightness(progress));
                oldImage = ((BitmapDrawable) imageToEdit.getDrawable()).getBitmap();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sharpnessImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onSharpnessImageViewClicked();
            }
        });
    }

    private void getDataFromIntentExtra() {

        if (getIntent().hasExtra("ALBUM")) {
            Bundle extras = getIntent().getExtras();
            album = (Album)extras.getParcelable("ALBUM");
        }

        if (getIntent().hasExtra("PATH")) {
            Bundle extras = getIntent().getExtras();
            path = extras.getString("PATH");
        }

        if (getIntent().hasExtra("is_private_album")) {
            Bundle extras = getIntent().getExtras();
            isPrivateAlbum = (boolean)extras.getBoolean("is_private_album");
        }
    }

    private void fitImageToImageView() {
        // Get bitmap from the the ImageView.
        Bitmap bitmap = null;

        try {
            Drawable drawing = imageToEdit.getDrawable();
            bitmap = ((BitmapDrawable) drawing).getBitmap();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No drawable on given view");
        }

        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(370);
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));

        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Log.i("Test", "xScale = " + Float.toString(xScale));
        Log.i("Test", "yScale = " + Float.toString(yScale));
        Log.i("Test", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        imageToEdit.setImageDrawable(result);

        Log.i("Test", "done");
    }

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    public static PorterDuffColorFilter setBrightness(int progress) {
        if (progress >= 100) {
            return new PorterDuffColorFilter(Color.argb(progress, 255, 255, 255), PorterDuff.Mode.SRC_OVER);
        } else {
            int value = (int) (100 - progress) * 255 / 100;
            return new PorterDuffColorFilter(Color.argb(value, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
        }
    }

    public void onSharpnessImageViewClicked() {
        if (!isSharpnessClicked) {
            oldImage = currImage;
            isSharpnessClicked = true;
            imageToEdit.setImageBitmap(sharpenImage(currImage, currImage.getHeight() * currImage.getWidth()));
        } else {
            currImage = oldImage;
            isSharpnessClicked = false;
        }
    }

    public Bitmap sharpenImage(Bitmap src, double weight) {
        // set sharpness configuration
        double[][] SharpConfig = new double[][]{
                {0, -2, 0},
                {-2, weight, -2},
                {0, -2, 0}
        };
        //create convolution matrix instance
        ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
        //apply configuration
        convMatrix.applyConfig(SharpConfig);
        //set weight according to factor
        convMatrix.Factor = weight - 8;
        return ConvolutionMatrix.computeConvolution3x3(src, convMatrix);
    }

    public void onDoneBtnClick(View v)
    {
        imageToEdit.setDrawingCacheEnabled(true);
        imageToEdit.buildDrawingCache();
        Bitmap bitmap = imageToEdit.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                toastMessage("An error occurred while saving the image,\n" +
                        "please try again");
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                toastMessage("Image saved successfully");
                Intent intent = new Intent(ImageEditingActivity.this, AlbumInfoActivity.class);
                intent.putExtra("album_data", album);
                intent.putExtra("is_private_album", isPrivateAlbum);
                startActivity(intent);
            }
        });
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

