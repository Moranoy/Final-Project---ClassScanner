package com.example.galbenabu1.classscanner.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.galbenabu1.classscanner.Adapters.PhotoGalleryAdapter;
import com.example.galbenabu1.classscanner.R;

import java.util.ArrayList;

import Logic.Managers.AnalyticsManager.AnalyticsHelpers.AlbumEventsHelper;
import Logic.Managers.AnalyticsManager.AnalyticsManager;
import Logic.Managers.AnalyticsManager.EventParams.AlbumEventParams;
import Logic.Models.Album;
import Logic.Models.PictureAudioData;

public class AlbumInfoActivity extends Activity {
    private static final String TAG = "AlbumInfoActivity";
    private static final String ALBUM_DATA = "album_data";
    private static final String IS_PRIVATE_ALBUM = "is_private_album";

    private boolean mIsPrivateAlbum;
    private Album mAlbum;
    private ArrayList<PictureAudioData> mAlbumPhotosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_info);
        Log.e(TAG, "onCreate >>");

        this.mIsPrivateAlbum = getIntent().getExtras().getBoolean(IS_PRIVATE_ALBUM);

        mAlbum = getIntent().getExtras().getParcelable(ALBUM_DATA);
        Log.e(TAG, "onCreate >> album: " + mAlbum.toString() + ". Is private album: " + this.mIsPrivateAlbum);

        RecyclerView recyclerView = findViewById(R.id.image_gallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        
        this.logViewAlbumImagesEvent();

        PhotoGalleryAdapter adapter = new PhotoGalleryAdapter(mAlbumPhotosList, this.mAlbum, this.mIsPrivateAlbum);

        fetchAlbumPhotos();


        recyclerView.setAdapter(adapter);
        Log.e(TAG, "onCreate <<");
    }

    private void fetchAlbumPhotos() {

        for (int i=0; i<mAlbum.getM_NumOfPictures()-1; i++)
        {
            PictureAudioData photo=mAlbum.getM_Pictures().get(i);
            if (photo!=null)
            {
                mAlbumPhotosList.add(photo);
            }
        }
    }

    private void logViewAlbumImagesEvent() {
        AlbumEventParams albumEventParams = new AlbumEventParams();

        albumEventParams.setmAlbum(this.mAlbum);

        AnalyticsManager.getInstance().trackAlbumEvent(AlbumEventsHelper.eAlbumEventType.ViewAlbumImages, albumEventParams);
    }

    public void onPresentAlbumClick(View v) {
        Intent presentIntent = new Intent(getApplicationContext(), PlayAlbumActivity.class);
        presentIntent.putExtra(IS_PRIVATE_ALBUM, this.mIsPrivateAlbum);
        presentIntent.putExtra(ALBUM_DATA, this.mAlbum);
        startActivity(presentIntent);
    }

    public void onAlbumInfoBackBtnClick(View v){
        finish();
    }

    public void onHomeBtnClick(View v)
    {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}