package Logic.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by galbenabu1 on 08/05/2018.
 */

public class Course implements Parcelable {
    private String m_Id;
    private String m_CourseName;
    private String m_CreatorID;
    private String mCreatorName;
    private Date m_CreationDate;
    private String m_Description;
    private List<String> m_UsersId;
    private List<String> m_AlbumIds;

    public Course() {}

    public Course(String courseID, String creatorName, String userID, String courseName, Date creationDate) {
        this.m_Id = courseID;
        this.mCreatorName = creatorName;
        this.m_CreatorID = userID;
        this.m_CourseName = courseName;
        this.m_CreationDate = creationDate;
        this.m_AlbumIds = new ArrayList<>();
        this.m_UsersId = new ArrayList<>();
        this.m_UsersId.add(this.m_CreatorID);
    }

    protected Course(Parcel in) {
        m_Id = in.readString();
        m_CreatorID = in.readString();
        long tempDateAsLong = in.readLong();
        m_CreationDate = tempDateAsLong == -1 ? null : new Date(tempDateAsLong);
        m_CourseName = in.readString();
        mCreatorName = in.readString();
        m_Description = in.readString();
        m_UsersId = in.createStringArrayList();
        m_AlbumIds = in.createStringArrayList();

        if (this.m_UsersId == null){
            this.m_UsersId = new ArrayList<>();
            this.m_UsersId.add(this.m_CreatorID);
        }

        if (this.m_AlbumIds == null){
            this.m_AlbumIds = new ArrayList<>();
        }
    }

    public List<String> getM_AlbumIds() {
        if(m_AlbumIds == null) {
            m_AlbumIds = new ArrayList<>();
        }

        return m_AlbumIds;
    }

    public void setM_AlbumIds(List<String> m_AlbumIds) {
        this.m_AlbumIds = m_AlbumIds;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_Id);
        dest.writeString(m_CreatorID);
        dest.writeLong(m_CreationDate != null ? m_CreationDate.getTime() : -1);
        dest.writeString(m_CourseName);
        dest.writeString(mCreatorName);
        dest.writeString(m_Description);
        dest.writeStringList(m_UsersId);
        dest.writeStringList(m_AlbumIds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public String getID() { return this.m_Id; }

    public void setId(String id) {
        this.m_Id = id;
    }

    public String getManegerId() {
        return m_CreatorID;
    }

    public void setManegerId(String manegerId) {
        this.m_CreatorID = manegerId;
    }

    public String getCourseName() {
        return m_CourseName;
    }

    public void setCourseName(String courseName) {
        this.m_CourseName = courseName;
    }

    public Date getCreationDate() {
        return m_CreationDate;
    }

    public void setCreationDate(Date date) {
        this.m_CreationDate = date;
    }

    public String getDescription() {
        return m_Description;
    }

    public void setDescription(String description) {
        this.m_Description = description;
    }

    public List<String> getM_UsersId() {
        return m_UsersId;
    }

    public void setUsersId(List<String> usersId) {
        this.m_UsersId = usersId;
    }


    public String getCreatorName(){ return this.mCreatorName; }

    public void setCreatorName(String creatorName) {
        this.mCreatorName = creatorName;
    }

    public void setCreatorID(String creatorID) {
        this.m_CreatorID = creatorID;
    }

    public String getCreatorID() {
        return this.m_CreatorID;
    }

    @Override
    public String toString() {
        return "Course{" +
                "m_Id='" + m_Id + '\'' +
                ", m_CourseName='" + m_CourseName + '\'' +
                ", m_CreatorID='" + m_CreatorID + '\'' +
                ", mCreatorName='" + mCreatorName + '\'' +
                ", m_CreationDate=" + m_CreationDate +
                ", m_Description='" + m_Description + '\'' +
                ", m_UsersId=" + m_UsersId +
                ", m_AlbumIds=" + m_AlbumIds +
                '}';
    }
}
