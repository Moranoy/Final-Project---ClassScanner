package com.example.galbenabu1.classscanner.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.galbenabu1.classscanner.ViewHolders.PhotoViewHolder;
import com.example.galbenabu1.classscanner.R;
import java.util.ArrayList;

import Logic.Database.DBManager;
import Logic.Models.Album;
import Logic.Models.PictureAudioData;

public class PhotoGalleryAdapter extends RecyclerView.Adapter<PhotoViewHolder> {
    private static final String TAG = "PhotoGalleryAdapter";
    private final boolean mIsPrivateAlbum;
    private ArrayList<PictureAudioData> mPhotoList;
    private Album mAlbum;

    public PhotoGalleryAdapter(ArrayList<PictureAudioData> photoList, Album album, boolean isPrivateAlbum) {
        mPhotoList = photoList;
        this.mAlbum = album;
        this.mIsPrivateAlbum = isPrivateAlbum;
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_item, viewGroup, false);
        return new PhotoViewHolder(view, this.mAlbum);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        Log.e(TAG, "onBindViewHolder() >> " + position);

        PictureAudioData photo = mPhotoList.get(position);

        holder.setSelectedPhoto(photo);
        holder.getTvTitle().setText(photo.getM_Description());
        holder.setIsPrivateAlbum(this.mIsPrivateAlbum);

        DBManager dbManager = new DBManager();
        dbManager.fetchImageFromStoragePath(photo.getM_Id(),
                (bitmap) -> holder.getIvPhoto().setImageBitmap(bitmap)
        );
        Log.e(TAG, "onBindViewHolder() >> " + photo);
    }



    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }
}
