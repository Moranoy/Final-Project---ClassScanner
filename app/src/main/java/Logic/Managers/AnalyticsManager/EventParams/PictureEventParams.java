package Logic.Managers.AnalyticsManager.EventParams;

public class PictureEventParams {
    public PictureEventParams(String mPictureID) {
        this.mPicturePath = mPictureID;
    }

    public PictureEventParams() { }

    private String mPicturePath;

    public String getmPicturePath() {
        return mPicturePath;
    }
}
