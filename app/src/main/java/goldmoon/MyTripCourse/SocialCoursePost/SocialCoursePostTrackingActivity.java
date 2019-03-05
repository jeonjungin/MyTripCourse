package goldmoon.MyTripCourse.SocialCoursePost;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import goldmoon.MyTripCourse.DataSet.CourseListDataSet;
import goldmoon.MyTripCourse.DataSet.SocialCoursePostDataSet;
import goldmoon.MyTripCourse.R;
import goldmoon.MyTripCourse.RewindCourse.RewindCourseViewPointActivity;

import static goldmoon.MyTripCourse.others.Constants.IS_VIEW_POINT;

/**
 *  게시물 경로 출력 할 액티비티
 */

public class SocialCoursePostTrackingActivity extends AppCompatActivity implements View.OnClickListener, MapView.POIItemEventListener, CompoundButton.OnCheckedChangeListener{

    LinearLayout li_social_tracking_back_btn;
    TextView tv_social_tracking_course_name;
    TextView tv_social_tracking_distance;
    LinearLayout li_social_tracking_starting_address_parent;
    TextView tv_social_tracking_starting_address;
    ToggleButton tg_social_tracking_my_location_btn;



    MapView daumMapView;
    ViewGroup daumMapViewContainer;
    MapPolyline polyline;
    MapReverseGeoCoder mapReverseGeoCoder;

    DatabaseReference fbDatabaseReference;
    String key;
    SocialCoursePostDataSet postData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_course_post_tracking_layout);


        Intent getKeyIntent=getIntent();
        key=getKeyIntent.getStringExtra("postKey");

        setMapping();
        getSocialCoursePostData();

    }

    void getSocialCoursePostData(){
        fbDatabaseReference= FirebaseDatabase.getInstance().getReference();
        Query getCoursePostDataQuery=fbDatabaseReference.child("posts").child(key).orderByKey();
        getCoursePostDataQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postData=dataSnapshot.getValue(SocialCoursePostDataSet.class);
                setUI();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SocialCoursePostTrackingActivity.this,"데이터 다운로드 에러",Toast.LENGTH_SHORT).show();
                finish();
            }
        });



    }

    void setMapping(){

        li_social_tracking_back_btn=(LinearLayout) findViewById(R.id.social_tracking_back_btn);
        tv_social_tracking_course_name=(TextView)findViewById(R.id.social_tracking_course_name);
        tv_social_tracking_distance=(TextView)findViewById(R.id.social_tracking_distance);
        li_social_tracking_back_btn.setOnClickListener(this);
        li_social_tracking_starting_address_parent=(LinearLayout)findViewById(R.id.social_tracking_starting_address_parent);
        tv_social_tracking_starting_address=(TextView)findViewById(R.id.social_tracking_starting_address);
        tg_social_tracking_my_location_btn=(ToggleButton)findViewById(R.id.social_tracking_my_location_btn);
        tg_social_tracking_my_location_btn.setOnCheckedChangeListener(this);

        daumMapView=new MapView(this);
        daumMapView.setDaumMapApiKey(getString(R.string.daum_map_api_key));
        daumMapView.setPOIItemEventListener(this);
        daumMapViewContainer = (ViewGroup) findViewById(R.id.social_tracking_daum_map_view);
        daumMapViewContainer.addView(daumMapView);
        polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(128, 255, 51, 0));


    }

    void setUI(){
        tv_social_tracking_course_name.setText(postData.getCourseName());
        tv_social_tracking_distance.setText(String.format("%.2f",postData.getDistance())+" km");

        for(CourseListDataSet temp: postData.getCourseListDataSets()){

            double lat=temp.getLat();
            double lon=temp.getLon();
            MapPoint mapPoint=MapPoint.mapPointWithGeoCoord(lat,lon);
            polyline.addPoint(mapPoint);

            if(temp.getViewPoint()==IS_VIEW_POINT){
                MapPOIItem marker=new MapPOIItem();
                marker.setItemName(temp.getPicCaption());
                marker.setTag(temp.getNo());
                marker.setMapPoint(mapPoint);
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

                daumMapView.addPOIItem(marker);
            }

            if(temp.getNo()==0 || temp.getNo()==postData.getCourseListDataSets().size()-1){
                //출발지
                if(temp.getNo()==0){

                    //출발지 마커 생성
                    MapPOIItem start=new MapPOIItem();
                    start.setItemName("출발");
                    start.setTag(temp.getNo());
                    start.setMapPoint(mapPoint);
                    start.setMarkerType(MapPOIItem.MarkerType.YellowPin);
                    daumMapView.addPOIItem(start);

                    //출발지 주소
                    mapReverseGeoCoder=new MapReverseGeoCoder("54a336d93d264308d81bf2bd1363a330", mapPoint, new MapReverseGeoCoder.ReverseGeoCodingResultListener() {
                        @Override
                        public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
                            li_social_tracking_starting_address_parent.setVisibility(View.VISIBLE);
                            tv_social_tracking_starting_address.setText(s);
                        }

                        @Override
                        public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
                            li_social_tracking_starting_address_parent.setVisibility(View.VISIBLE);
                            tv_social_tracking_starting_address.setText("주소를 찾지 못하였습니다.");
                        }
                    },  this);
                    mapReverseGeoCoder.startFindingAddress();



                    //도착지
                }else if(temp.getNo()==postData.getCourseListDataSets().size()-1){
                    MapPOIItem end=new MapPOIItem();
                    end.setItemName("도착");
                    end.setTag(temp.getNo());
                    end.setMapPoint(mapPoint);
                    end.setMarkerType(MapPOIItem.MarkerType.YellowPin);


                    daumMapView.addPOIItem(end);
                }
            }
        }
        //라인 그리기 및 전체 뷰로 이동
        daumMapView.addPolyline(polyline);
        MapPointBounds mapPointBounds= new MapPointBounds(polyline.getMapPoints());
        daumMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.social_tracking_back_btn:
                finish();
                break;

        }
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        for(CourseListDataSet temp : postData.getCourseListDataSets()){

            if(mapPOIItem.getTag()==temp.getNo() && temp.getViewPoint()==IS_VIEW_POINT){
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

    //내 위치 찾기 토글버튼
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(isChecked){
            tg_social_tracking_my_location_btn.setBackgroundColor(getColor(R.color.colorRed));
            daumMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading); //내위치 및 나침반 모드

        }else{
            tg_social_tracking_my_location_btn.setBackgroundColor(getColor(R.color.colorPur));
            daumMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            //현위치 표시 지우기
            daumMapView.setShowCurrentLocationMarker(false);
            //카메라 이동
            MapPointBounds mapPointBounds= new MapPointBounds(polyline.getMapPoints());
            daumMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));

        }

    }
}
