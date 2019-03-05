package goldmoon.MyTripCourse.DataSet;


/**
 * GPSDataSet 에서 Uri을 뺀 데이터셋
 * Firebase db에 올리기 위함
 */

public class CourseListDataSet {

    private int no;             //순서
    private double lat;         //
    private double lon;         //
    private int viewPoint; //0 false 1 true
    private String imgRefStr; //스토리지 참조객체
    private int isMainImg; //0 false 1 true
    private String picCaption;  //사진 캡션


    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getViewPoint() {
        return viewPoint;
    }

    public void setViewPoint(int viewPoint) {
        this.viewPoint = viewPoint;
    }

    public String getImgRefStr() {
        return imgRefStr;
    }

    public void setImgRefStr(String imgRefStr) {
        this.imgRefStr = imgRefStr;
    }

    public int getIsMainImg() {
        return isMainImg;
    }

    public void setIsMainImg(int isMainImg) {
        this.isMainImg = isMainImg;
    }

    public String getPicCaption() {
        return picCaption;
    }

    public void setPicCaption(String picCaption) {
        this.picCaption = picCaption;
    }

}
