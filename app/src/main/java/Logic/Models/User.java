package Logic.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galbenabu1 on 08/05/2018.
 */

public class User implements Parcelable{

    private String m_Id;
    private String m_UserName;
    private String m_Mail;
    private String m_NickName;
    private List<String> m_CourseIds;

    public User() {
        this.m_CourseIds = new ArrayList<>();
    }

    public User(String m_UserName, String m_Mail) {
        this.m_UserName = m_UserName;
        this.m_Mail = m_Mail;
        this.m_CourseIds = new ArrayList<>();
    }

    protected User(Parcel in) {
        m_Id = in.readString();
        m_UserName = in.readString();
        m_Mail = in.readString();
        m_NickName = in.readString();
        m_CourseIds = in.createStringArrayList();

        if(this.m_CourseIds == null) {
            this.m_CourseIds = new ArrayList<>();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_Id);
        dest.writeString(m_UserName);
        dest.writeString(m_Mail);
        dest.writeString(m_NickName);
        dest.writeStringList(m_CourseIds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getM_Id() {
        return m_Id;
    }

    public void setM_Id(String m_Id) {
        this.m_Id = m_Id;
    }

    public String getM_UserName() {
        return m_UserName;
    }

    public void setM_UserName(String m_UserName) {
        this.m_UserName = m_UserName;
    }

    public String getM_Mail() {
        return m_Mail;
    }

    public void setM_Mail(String m_Mail) {
        this.m_Mail = m_Mail;
    }

    public String getM_NickName() {
        return m_NickName;
    }

    public void setM_NickName(String m_NickName) {
        this.m_NickName = m_NickName;
    }

    public List<String> getM_CourseIds() {
        return m_CourseIds;
    }

    public void setM_CourseIds(List<String> m_CourseIds) {
        this.m_CourseIds = m_CourseIds;
    }
}
