package Logic.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by galbenabu1 on 08/05/2018.
 */

public class Album implements Parcelable {
    private String m_Id;
    private String m_AlbumCreatorId;
    private String m_AlbumName;
    private String m_AlbumCreatorName;
    private Date m_CreationDate;
    private String m_Description;
    private int m_NumOfPictures;
    private List <PictureAudioData> m_Pictures;
    private PictureAudioData m_Audio;

    public Album() {}

    public Album(String m_Id, String m_AlbumName, Date m_CreationDate, String m_AlbumCreatorName, String m_AlbumCreatorId) {
        this.m_Id = m_Id;
        this.m_AlbumName = m_AlbumName;
        this.m_CreationDate = m_CreationDate;
        this.m_AlbumCreatorName = m_AlbumCreatorName;
        this.m_AlbumCreatorId = m_AlbumCreatorId;
    }

    protected Album(Parcel in) {
        m_Id = in.readString();
        m_AlbumName = in.readString();
        m_AlbumCreatorName = in.readString();
        long tempDateAsLong = in.readLong();
        if(tempDateAsLong==-1)
            m_CreationDate=Calendar.getInstance().getTime();
        else
            m_CreationDate=new Date(tempDateAsLong);
        m_Description = in.readString();
        m_NumOfPictures = in.readInt();

        m_Pictures = in.readArrayList(PictureAudioData.class.getClassLoader());
        if(m_Pictures == null) {
            m_Pictures = new ArrayList<>();
        }
        m_Audio = (PictureAudioData) in.readSerializable();
        m_AlbumCreatorId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_Id);
        dest.writeString(m_AlbumName);
        dest.writeString(m_AlbumCreatorName);
        dest.writeLong(m_CreationDate != null ? m_CreationDate.getTime() : -1);
        dest.writeString(m_Description);
        dest.writeInt(m_NumOfPictures);
        dest.writeList(m_Pictures);
        dest.writeSerializable(m_Audio);
        dest.writeString(m_AlbumCreatorId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public void setM_Id(String m_Id) {
        this.m_Id = m_Id;
    }

    public String getM_AlbumName() {
        return m_AlbumName;
    }

    public void setM_AlbumName(String m_AlbumName) {
        this.m_AlbumName = m_AlbumName;
    }

    public Date getM_CreationDate() { return this.m_CreationDate; }

    public String getM_AlbumCreatorName(){ return this.m_AlbumCreatorName; }

    public String getM_Description() {
        return m_Description;
    }

    public void setM_Description(String m_Description) {
        this.m_Description = m_Description;
    }

    public int getM_NumOfPictures() {
        return m_NumOfPictures;
    }

    public void setM_NumOfPictures(int m_NumOfPictures) {
        this.m_NumOfPictures = m_NumOfPictures;
    }

    public List<PictureAudioData> getM_Pictures() {
        return m_Pictures;
    }

    public void setM_Pictures(List<PictureAudioData> m_Pictures) {
        this.m_Pictures = m_Pictures;
    }

    public PictureAudioData getM_Audio() {
        return m_Audio;
    }

    public void setM_Audio(PictureAudioData m_Audio) {
        this.m_Audio = m_Audio;
    }

    public String getM_Id() {
        return m_Id;
    }

    public String getM_AlbumCreatorId(){ return this.m_AlbumCreatorId;}

    public void setM_AlbumCreatorId(String m_AlbumCreatorId) { this.m_AlbumCreatorId = m_AlbumCreatorId;
    }

    public void deletePictureFromAlbum (int pictureId){
        m_Pictures.set(pictureId,null);
    }

    public void setM_CreationDate(Date m_CreationDate) {
        this.m_CreationDate = m_CreationDate;
    }
}
