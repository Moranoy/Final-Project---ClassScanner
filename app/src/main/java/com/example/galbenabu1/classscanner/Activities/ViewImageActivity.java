package com.example.galbenabu1.classscanner.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.galbenabu1.classscanner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import Logic.Database.DBManager;

import Logic.Models.Album;


/**
 * Created by chen capon on 11/10/2018.
 */


public class ViewImageActivity extends AppCompatActivity {

    private FloatingActionButton btnDownload;
    private FloatingActionButton btnEdit;
    private FloatingActionButton btnDelete;
    private ConstraintLayout totalView;
    private ImageView imageView;
    private Bitmap mBitmap;
    private String path;
    private String dbId;
    private String storageId;
    private Album album;

    private FirebaseStorage storage;
    private StorageReference ref;
    private boolean isPrivateAlbum;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        initViews();

        storage = FirebaseStorage.getInstance();
        ref = storage.getReference().child(path);

        getImageByPathAndBitmap();

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setDrawingCacheEnabled(true);
                try
                {
                    MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "classImage" , "");
                    Toast.makeText(getApplicationContext(),  "Image saved to gallery", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(imageView.getContext(), CropImageActivity.class);
                intent.putExtra("PATH", path);
                intent.putExtra("ALBUM",album);
                intent.putExtra("is_private_album",isPrivateAlbum);
                imageView.getContext().startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBManager dbmanager =new DBManager();
                String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                String pictureDbId=dbId;
                String pictureId=storageId;
                dbmanager.removePictureFromDB(album,userId,pictureId,pictureDbId,isPrivateAlbum);
                Intent newIntent = new Intent(v.getContext(), AlbumInfoActivity.class);
                newIntent.putExtra("album_data", album);
                newIntent.putExtra("is_private_album", isPrivateAlbum);
                startActivity(newIntent);

            }
        });


    }



    private void getImageByPathAndBitmap() {
        String pictureId=storageId;
        DBManager dbManager = new DBManager();
        dbManager.fetchImageFromStoragePath(pictureId,
        (bitmap) -> {
            imageView.setImageBitmap(bitmap);
            imageBitmap=bitmap;
        });
    }

    private void initViews() {

        btnDownload = (FloatingActionButton) findViewById(R.id.download_button);
        imageView = (ImageView) findViewById(R.id.picture_view);
        totalView = (ConstraintLayout) findViewById(R.id.total_view);
        btnDelete = (FloatingActionButton) findViewById(R.id.delete_button);
        btnEdit = (FloatingActionButton) findViewById(R.id.edit_button);

        if (getIntent().hasExtra("PATH")) {
            Bundle extras = getIntent().getExtras();
            path = extras.getString("PATH");
        }

        if (getIntent().hasExtra("ALBUM")) {
            Bundle extras = getIntent().getExtras();
            album = (Album)extras.getParcelable("ALBUM");
        }


        if (getIntent().hasExtra("DB_ID")) {
            Bundle extras = getIntent().getExtras();
            dbId = extras.getString("DB_ID");
        }

        if (getIntent().hasExtra("STORAGE_ID")) {
            Bundle extras = getIntent().getExtras();
            storageId = extras.getString("STORAGE_ID");
        }

        if (getIntent().hasExtra("is_private_album")) {
            Bundle extras = getIntent().getExtras();
            isPrivateAlbum = extras.getBoolean("is_private_album");
        }

        if (!this.isUserTheCreator()) { //only creator user can edit/delete pictures
            hideCreatorOnlyButtons();
        }

    }

    private void hideCreatorOnlyButtons() {
        this.btnEdit.setVisibility(View.INVISIBLE);
        this.btnDelete.setVisibility(View.INVISIBLE);
    }

   private boolean isUserTheCreator() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid().equals(this.album.getM_AlbumCreatorId());
   }

}



