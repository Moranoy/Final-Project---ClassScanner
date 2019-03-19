package com.example.galbenabu1.classscanner.ViewHolders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.galbenabu1.classscanner.Activities.AlbumInfoActivity;
import com.example.galbenabu1.classscanner.Activities.CropImageActivity;
import com.example.galbenabu1.classscanner.Activities.ViewImageActivity;
import com.example.galbenabu1.classscanner.R;
import com.google.firebase.auth.FirebaseAuth;

import Logic.Database.DBManager;
import Logic.Models.Album;
import Logic.Models.PictureAudioData;


public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {
    private static final String TAG = "PhotoViewHolder";
    private TextView mtvTitle;
    private ImageView mivPhoto;
    private PictureAudioData mSelectedPhoto;
    private Album mAlbum;
    private boolean mIsPrivateAlbum;

    @SuppressLint("ClickableViewAccessibility")
    public PhotoViewHolder(View view, Album album) {
        super(view);
        this.mAlbum = album;

        view.setOnClickListener(this);
        view.setOnCreateContextMenuListener(this);

        mtvTitle = view.findViewById(R.id.tv_photo_title);
        mivPhoto = view.findViewById(R.id.iv_photo);

        mivPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.getMenuInflater().inflate(R.menu.picture_options_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.Edit:
                                Intent intent = new Intent(view.getContext(), CropImageActivity.class);
                                intent.putExtra("PATH", mSelectedPhoto.getM_Path());

                                intent.putExtra("ALBUM", mAlbum);

                                intent.putExtra("is_private_album",mIsPrivateAlbum);

                                view.getContext().startActivity(intent);
                                return true;
                            case R.id.Delete:
                                DBManager dbmanager = new DBManager();
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String pictureDbId = findSelectedPhotoIndex();
                                String pictureId = mSelectedPhoto.getM_Id();
                                boolean isPrivateAlbum = mIsPrivateAlbum;
                                dbmanager.removePictureFromDB(album, userId, pictureId, pictureDbId, isPrivateAlbum);
                                Intent newIntent = new Intent(view.getContext(), AlbumInfoActivity.class);
                                newIntent.putExtra("album_data", album);
                                newIntent.putExtra("is_private_album", isPrivateAlbum);
                                view.getContext().startActivity(newIntent);
                                return true;
                        }

                        return false;
                    }});

                if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(mAlbum.getM_AlbumCreatorId())) { //only creator user can edit pictures
                    popup.show();
                }

                return true;
            }
        });

        mivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "CardView.onClick() >> Photo: " + mSelectedPhoto.toString());
                Intent intent = new Intent(view.getContext(), ViewImageActivity.class);
                intent.putExtra("PATH", mSelectedPhoto.getM_Path());
                intent.putExtra("ALBUM",mAlbum);
                intent.putExtra("DB_ID",findSelectedPhotoIndex());
                intent.putExtra("STORAGE_ID",mSelectedPhoto.getM_Id());
                view.getContext().startActivity(intent);
            }
        });
    }

    public String findSelectedPhotoIndex()
    {
        int photoIndex=-1;
        for (int i=0; i<mAlbum.getM_Pictures().size()-1; i++ )
        {
            if(mAlbum.getM_Pictures().get(i)!=null) {
                if (mAlbum.getM_Pictures().get(i).getM_Id() == mSelectedPhoto.getM_Id()) {
                    photoIndex = i;
                    break;
                }
            }
        }
        return Integer.toString(photoIndex);
    }


    public TextView getTvTitle() {
        return mtvTitle;
    }

    public void setTvTitle(TextView title) {
        this.mtvTitle = title;
    }

    public ImageView getIvPhoto() {
        return mivPhoto;
    }

    public void setIvPhoto(ImageView photo) {
        this.mivPhoto = photo;
    }

    public PictureAudioData getSelectedPhoto() {
        return mSelectedPhoto;
    }

    public void setSelectedPhoto(PictureAudioData selectedPhoto) {
        this.mSelectedPhoto = selectedPhoto;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo contextMenuInfo) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.picture_options_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    public void setIsPrivateAlbum(boolean isPrivateAlbum) {
        this.mIsPrivateAlbum = isPrivateAlbum;
    }
}


