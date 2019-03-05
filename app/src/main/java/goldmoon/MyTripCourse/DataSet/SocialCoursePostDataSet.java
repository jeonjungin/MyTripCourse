package goldmoon.MyTripCourse.DataSet;

import java.util.ArrayList;

import goldmoon.MyTripCourse.DataSet.CourseListDataSet;

public class SocialCoursePostDataSet {
    private ArrayList<CourseListDataSet> courseListDataSets;
    private String courseName;
    private String postTime;
    private String startTime;
    private String stopTime;
    private String mainImg;
    private String writerUid;
    private String postKey;
    private double distance;
    private int like;

    public ArrayList<CourseListDataSet> getCourseListDataSets() {
        return courseListDataSets;
    }

    public void setCourseListDataSets(ArrayList<CourseListDataSet> courseListDataSets) {
        this.courseListDataSets = courseListDataSets;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getMainImg() {
        return mainImg;
    }

    public void setMainImg(String mainImg) {
        this.mainImg = mainImg;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getWriterUid() {
        return writerUid;
    }

    public void setWriterUid(String writerUid) {
        this.writerUid = writerUid;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isSame= false;

        if(obj instanceof SocialCoursePostDataSet && obj !=null){
            String cn=((SocialCoursePostDataSet) obj).getCourseName();
            String pt=((SocialCoursePostDataSet) obj).getPostTime();
            String st=((SocialCoursePostDataSet) obj).getStartTime();
            String pk=((SocialCoursePostDataSet) obj).getPostKey();
            isSame=(courseName+postTime+startTime+postKey).equals(cn+pt+st+pk);
        }


        return isSame;
    }
}

