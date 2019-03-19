package com.example.galbenabu1.classscanner.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.galbenabu1.classscanner.ViewHolders.AlbumsViewHolder;
import com.example.galbenabu1.classscanner.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import Logic.Models.Album;
import Logic.Interfaces.MyConsumer;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsViewHolder> {
    private final String TAG = "AlbumsAdapter";

    private List<Album> mAlbumsList;
    private MyConsumer<Album> mOnLongClickListener;
    private boolean mIsPrivateAlbum;
    private boolean mShouldShowCheckedImage;

    public AlbumsAdapter(List<Album> albumsList, MyConsumer<Album> onLongClickListener, boolean isPrivateAlbum, boolean isUserSelectingAlbums) {
        mAlbumsList = albumsList;
        mOnLongClickListener = onLongClickListener;
        this.mIsPrivateAlbum = isPrivateAlbum;
        this.mShouldShowCheckedImage = isUserSelectingAlbums;
    }

    @Override
    public AlbumsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.e(TAG, "onCreateViewHolder() >>");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_item, parent, false);

        Log.e(TAG, "onCreateViewHolder() <<");
        return new AlbumsViewHolder(itemView, this.mIsPrivateAlbum);
    }

    @Override
    public void onBindViewHolder(AlbumsViewHolder holder, int position) {

        Log.e(TAG, "onBindViewHolder() >> " + position);

        Album album = mAlbumsList.get(position);

        // bind Album data to it's view items
        holder.setSelectedAlbum(album);
        holder.getAlbumName().setText("Name: " + album.getM_AlbumName());
        holder.getCreatorName().setText("Publisher: " + album.getM_AlbumCreatorName());

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if(album.getM_CreationDate()==null)
            album.setM_CreationDate(Calendar.getInstance().getTime());
        String dateStr = dateFormat.format(album.getM_CreationDate());

        holder.getCreationDate().setText("Creation date: " + dateStr);
        holder.getAlbumCardView().setBackgroundColor(Color.WHITE);
        holder.setOnLongClickListener(mOnLongClickListener);

        if(!this.mShouldShowCheckedImage) {
            holder.getmCheckedSign().setVisibility(View.INVISIBLE); // Hide checked sign.
        }

        Log.e(TAG, "onBindViewHolder() << " + position);
    }

    @Override
    public int getItemCount() {
        return mAlbumsList.size();
    }
}
