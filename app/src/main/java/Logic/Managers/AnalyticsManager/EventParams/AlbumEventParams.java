package Logic.Managers.AnalyticsManager.EventParams;

import Logic.Models.Album;

public class AlbumEventParams {
    private Album mAlbum;
    private String mCourseID;
    private int mNumberOfAlbums;

    public Album getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(Album mAlbum) {
        this.mAlbum = mAlbum;
    }

    public String getmCourseID() {
        return mCourseID;
    }

    public void setmCourseID(String mCourseID) {
        this.mCourseID = mCourseID;
    }

    public int getmNumberOfAlbums() {
        return mNumberOfAlbums;
    }

    public void setmNumberOfAlbums(int mNumberOfAlbums) {
        this.mNumberOfAlbums = mNumberOfAlbums;
    }
}
