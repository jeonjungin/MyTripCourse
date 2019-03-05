package goldmoon.MyTripCourse.DataSet;

import java.util.ArrayList;

import goldmoon.MyTripCourse.DataSet.CourseListDataSet;

/**
 * 서버에서 받아온 데이터
 */

public class FBCourseListDataSet {
    private ArrayList<CourseListDataSet> courseListDataSets;
    private String CourseName;
    private String startTime;
    private String stopTime;
    private String mainImg;
    private double distance; //거리

    public ArrayList<CourseListDataSet> getCourseListDataSets() {
        return courseListDataSets;
    }

    public void setCourseListDataSets(ArrayList<CourseListDataSet> courseListDataSets) {
        this.courseListDataSets = courseListDataSets;
    }

    public String getCourseName() {
        return CourseName;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
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
}
