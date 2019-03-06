package goldmoon.MyTripCourse.RewindCourse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import goldmoon.MyTripCourse.DataSet.CourseListDataSet;
import goldmoon.MyTripCourse.DataSet.FBCourseListDataSet;
import goldmoon.MyTripCourse.DataSet.SocialCoursePostDataSet;
import goldmoon.MyTripCourse.R;

import static goldmoon.MyTripCourse.others.Constants.IS_VIEW_POINT;

/**
 * 저장된 경로 출력할 액티비티
 */

public class RewindCourseTrackingActivity extends AppCompatActivity implements View.OnClickListener, MapView.POIItemEventListener{

    MapView daumMapView;
    ViewGroup daumMapViewContainer;
    MapPolyline polyline;


    DatabaseReference fbDatabaseReference;
    FirebaseUser fbUser;
    String fbUserUid;
    String fbKey;

    FBCourseListDataSet courseData;

    LinearLayout li_back_btn;
    TextView tv_course_name;
    TextView tv_course_distance;
    TextView tv_course_share_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewind_course_tracking_layout);

        li_back_btn=(LinearLayout)findViewById(R.id.rewind_course_tracking_back_btn);
        li_back_btn.setOnClickListener(this);
        tv_course_name=(TextView)findViewById(R.id.rewind_course_tracking_course_name);
        tv_course_distance=(TextView)findViewById(R.id.rewind_course_tracking_distance);
        tv_course_share_btn=(TextView)findViewById(R.id.rewind_course_tracking_share_btn);
        tv_course_share_btn.setOnClickListener(this);

        fbUser= FirebaseAuth.getInstance().getCurrentUser();
        fbUserUid=fbUser.getUid();
        fbDatabaseReference= FirebaseDatabase.getInstance().getReference();

        Intent courseDataIntent=getIntent();
        fbKey=courseDataIntent.getStringExtra("FBKey");

        daumMapView=new MapView(this);
        daumMapView.setDaumMapApiKey(getString(R.string.daum_map_api_key));
        daumMapView.setPOIItemEventListener(this);
        daumMapViewContainer = (ViewGroup) findViewById(R.id.rewind_daum_map_view);
        daumMapViewContainer.addView(daumMapView);

        //경로표시
        polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(128, 255, 51, 0));

        initCourseLineAndUI();

    }


    void initCourseLineAndUI(){

        final Query getDataQuery=fbDatabaseReference.child("users").child(fbUserUid).child("roots").child(fbKey).orderByKey();
        getDataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                FBCourseListDataSet data=dataSnapshot.getValue(FBCourseListDataSet.class);

                courseData=data;


                setUI(data);



                for(CourseListDataSet temp : data.getCourseListDataSets()){

                    double lat=temp.getLat();
                    double lon=temp.getLon();
                    int no=temp.getNo();
                    Log.e("onDataChange",String.valueOf(no));
                    String caption=temp.getPicCaption();

                    //라인
                    MapPoint mapPoint=MapPoint.mapPointWithGeoCoord(lat,lon);

                    polyline.addPoint(mapPoint);



                    //마커
                    if(temp.getViewPoint()==IS_VIEW_POINT){
                        MapPOIItem marker=new MapPOIItem();
                        marker.setItemName(caption);
                        marker.setTag(no);
                        marker.setMapPoint(mapPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

                        daumMapView.addPOIItem(marker);
                    }

                }


                //출발, 도착지에 마커
                CourseListDataSet firstPoint=data.getCourseListDataSets().get(0);
                CourseListDataSet lastPoint=data.getCourseListDataSets().get(data.getCourseListDataSets().size()-1);

                MapPOIItem firstPointPOIItem=new MapPOIItem();
                MapPOIItem lastPointPOIItem=new MapPOIItem();

                firstPointPOIItem.setItemName("출발");
                firstPointPOIItem.setTag(firstPoint.getNo());
                firstPointPOIItem.setMapPoint(MapPoint.mapPointWithGeoCoord(firstPoint.getLat(),firstPoint.getLon()));
                firstPointPOIItem.setMarkerType(MapPOIItem.MarkerType.YellowPin);

                lastPointPOIItem.setItemName("도착");
                lastPointPOIItem.setTag(lastPoint.getNo());
                lastPointPOIItem.setMapPoint(MapPoint.mapPointWithGeoCoord(lastPoint.getLat(),lastPoint.getLon()));
                lastPointPOIItem.setMarkerType(MapPOIItem.MarkerType.YellowPin);

                daumMapView.addPOIItem(firstPointPOIItem);
                daumMapView.addPOIItem(lastPointPOIItem);


                //라인 그리기 및 전체 뷰로 이동
                daumMapView.addPolyline(polyline);
                MapPointBounds mapPointBounds= new MapPointBounds(polyline.getMapPoints());
                daumMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //텍스트뷰 등등 셋팅
    void setUI(FBCourseListDataSet input){

        tv_course_name.setText(input.getCourseName());
        tv_course_distance.setText(String.format("%.2f",input.getDistance())+" km");


    }

    @Override
    public void onClick(View v) {


        switch (v.getId()){

            case R.id.rewind_course_tracking_back_btn:

                finish();

                break;
            case R.id.rewind_course_tracking_share_btn:
                SocialCoursePostDataSet sendData=new SocialCoursePostDataSet();
                sendData.setCourseListDataSets(courseData.getCourseListDataSets());
                sendData.setCourseName(courseData.getCourseName());
                sendData.setDistance(courseData.getDistance());
                sendData.setMainImg(courseData.getMainImg());
                sendData.setStartTime(courseData.getStartTime());
                sendData.setStopTime(courseData.getStopTime());
                sendData.setWriterUid(fbUserUid);
                sendData.setWriterNick(fbUser.getDisplayName());

                Date postTimeDate=new Date(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                String postTime=simpleDateFormat.format(postTimeDate);
                sendData.setPostTime(postTime);

                fbDatabaseReference.child("posts").push().setValue(sendData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RewindCourseTrackingActivity.this, "코스 등록 완료!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RewindCourseTrackingActivity.this,"등록 실패",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });




                break;
        }
    }


    //마커 이벤트 리스너
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

        Log.e("onPOIItemSelected","선택됨");
        ArrayList<CourseListDataSet> viewPointList=courseData.getCourseListDataSets();
        int pointTag=mapPOIItem.getTag();

        for(CourseListDataSet temp : viewPointList){
            if(temp.getNo()==pointTag && temp.getViewPoint()==IS_VIEW_POINT){

                Intent rewind_course_view_point_intent=new Intent(this, RewindCourseViewPointActivity.class);
                rewind_course_view_point_intent.putExtra("ImageRefStr",temp.getImgRefStr());
                rewind_course_view_point_intent.putExtra("ImageCaption",temp.getPicCaption());

                Log.e("onPOIItemSelected",String.valueOf(temp.getNo()));
                startActivity(rewind_course_view_point_intent);
                break;
            }
        }


    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}
