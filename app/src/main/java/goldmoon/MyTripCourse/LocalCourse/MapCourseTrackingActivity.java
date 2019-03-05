package goldmoon.MyTripCourse.LocalCourse;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import goldmoon.MyTripCourse.DataSet.CourseListDataSet;
import goldmoon.MyTripCourse.DataSet.FBCourseListDataSet;
import goldmoon.MyTripCourse.DataSet.GPSDataSet;
import goldmoon.MyTripCourse.others.GPSLocationDistance;
import goldmoon.MyTripCourse.R;
import goldmoon.MyTripCourse.others.NetworkUtil;

import static goldmoon.MyTripCourse.others.Constants.IS_MAIN_IMG;
import static goldmoon.MyTripCourse.others.Constants.IS_VIEW_POINT;
import static goldmoon.MyTripCourse.others.Constants.NOT_MAIN_IMG;
import static goldmoon.MyTripCourse.others.Constants.NOT_VIEW_POINT;
import static goldmoon.MyTripCourse.others.Constants.REQUEST_IMG_CAPTURE;
import static goldmoon.MyTripCourse.others.Constants.REQUEST_IMG_NAME_SET;
import static goldmoon.MyTripCourse.others.NetworkUtil.TYPE_MOBILE;
import static goldmoon.MyTripCourse.others.NetworkUtil.TYPE_NOT_CONNECTED;
import static goldmoon.MyTripCourse.others.NetworkUtil.TYPE_WIFI;

/**
 * button--> createFile--> 카메라앱 --> galleryAddPic
 */

public class MapCourseTrackingActivity extends AppCompatActivity implements LocationListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {


    MapView daumMapView;
    ViewGroup daumMapViewContainer;
    MapPolyline polyline;

    //GPS 객체
    GPSInfo gpsInfo;

    //위도 경도
    double lat=0.0;
    double lon=0.0;

    double preLat=0.0;
    double preLon=0.0;
    double distance=0.0;

    //거리계산 클래스
    GPSLocationDistance gpsLocationDistance;

    boolean gpsStatus;

    ToggleButton test_btn;
    LinearLayout make_view_point_btn;
    LinearLayout save_to_server_btn;
    TextView map_course_tracking_distance;

    ArrayList<MapPOIItem> markers;
    int cnt_marker = 0;   // 0~


    ArrayList<GPSDataSet> gpsDatas; //서버로 보내기전 localUri를 가지고 있는 데이터
    ArrayList<CourseListDataSet> courseListDataSets;    //서버로 보낼 데이터

    String courseName;  //경로
    int pointNo = 0; //0~     경로 순서
    int pointNoArrayIndex;  //경로 순서에 따른 gpsDatas의 마지막 인덱스   pointNo-1
    int viewPoint = 0; //0 false 1 true  사진 찍은 포인트


    Uri fileUri;        //이미지 URI
    String mCurrentPhotoPath;   //이미지 path

    private DatabaseReference fbDatabaseReference;
    private FirebaseUser fbUser;
    String fbUserUid;
    private StorageReference fbStorage;

    //경로추적 시작,종료 시간
    Date startDate;
    Date stopDate;
    String startDateStr;
    String stopDateStr;

    boolean startTimer=false;

    boolean isDoneUploadImage=false;
    boolean isDoneUploadData=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_course_tracking_layout);

//카카오 디벨로퍼 키해시
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("goldmoon.MyTripCourse", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

        //거리 계산
        gpsLocationDistance=new GPSLocationDistance();


        //인텐트로 넘어온 경로 이름
        Intent inputIntent = getIntent();
        courseName = inputIntent.getStringExtra("CourseName");

        //파이어베이스 데이터베이스 인스턴스
        fbDatabaseReference= FirebaseDatabase.getInstance().getReference();

        //파이어베이스 사용자 정보
        fbUser= FirebaseAuth.getInstance().getCurrentUser();
        fbUserUid=fbUser.getUid();

        //파이어베이스 스토리지
        fbStorage=FirebaseStorage.getInstance().getReference();


        //서버로 보내기 전
        gpsDatas = new ArrayList<GPSDataSet>();
        //서버로 보낼 데이터
        courseListDataSets=new ArrayList<CourseListDataSet>();

        //지도 지정한 포인트들 마커
        markers = new ArrayList<MapPOIItem>();
        //위치 추적 토글버튼
        test_btn = (ToggleButton) findViewById(R.id.test_btn);
        test_btn.setOnCheckedChangeListener(this);
        //포인트 지정하기 위한 버튼
        make_view_point_btn = (LinearLayout) findViewById(R.id.make_view_point_btn);
        make_view_point_btn.setOnClickListener(this);
        //서버로 전송 버튼
        save_to_server_btn = (LinearLayout) findViewById(R.id.save_to_server_btn);
        save_to_server_btn.setOnClickListener(this);
        //거리
        map_course_tracking_distance=(TextView)findViewById(R.id.tv_map_course_tracking_distance);
        map_course_tracking_distance.setText("0.0 km");


        //뷰에 지도 띄움
        daumMapView = new MapView(this);
        daumMapView.setDaumMapApiKey("54a336d93d264308d81bf2bd1363a330");
        daumMapViewContainer = (ViewGroup) findViewById(R.id.daum_map_view);
        daumMapViewContainer.addView(daumMapView);





        gpsInfo = new GPSInfo(this, this);


        //경로표시
        polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.argb(128, 255, 51, 0));




    }

    @Override
    protected void onResume() {

        super.onResume();
        NetworkUtil networkUtil = new NetworkUtil();
        if(networkUtil.getConnectivityStatus(this)==TYPE_NOT_CONNECTED){
            Toast.makeText(this, "모바일 데이터 상태를 확인하세요.", Toast.LENGTH_SHORT).show();
            finish();
        }





    }

    @Override
    protected void onPause() {

        //위치 받아오기 해제
        gpsInfo.stopGetGPS();
        test_btn.setChecked(false);
        super.onPause();
    }

    //GPS 위치 변경 리스너
    @Override
    public void onLocationChanged(Location location) {


        preLat=lat;
        preLon=lon;
        this.lat = location.getLatitude();
        this.lon = location.getLongitude();

        if(preLat!=0.0 && preLon!=0.0){
           distance+=gpsLocationDistance.distanceMeasure(preLat,preLon,lat,lon);
           map_course_tracking_distance.setText(String.format("%.2f",distance)+" km");
        }


        Log.e("lat: ", String.valueOf(lat));
        Log.e("lon: ", String.valueOf(lon));

        daumMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat, lon), 1, true);
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(lat, lon));
        daumMapView.addPolyline(polyline);


        GPSDataSet tempData = new GPSDataSet();
//        tempData.setCourseName(courseName);
        tempData.setLat(lat);
        tempData.setLon(lon);
        tempData.setNo(pointNo);
        tempData.setViewPoint(NOT_VIEW_POINT);

        pointNo++;
        pointNoArrayIndex = pointNo - 1;

        gpsDatas.add(tempData);


    }

    //GPS 서비스 상태 변할때
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //status 0-down 1-shortly 2-able
        Log.e("LocationListener", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        if (status == LocationProvider.OUT_OF_SERVICE) {

            //토글버튼 on-off로 gps제어
            test_btn.setChecked(false);
            Toast.makeText(this, "Out_of_service_gps", Toast.LENGTH_SHORT).show();

        }
    }


    //위치 제공자 상태 바뀔때
    @Override
    public void onProviderEnabled(String provider) {
        Log.e("LocationListener", "onProviderEnabled, provider:" + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("LocationListener", "onProviderDisabled, provider:" + provider);
    }


    //토글버튼 리스너
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (isChecked) {
            buttonView.setBackgroundColor(getResources().getColor(R.color.colorRed));
            gpsInfo.startGetGPS(); //gps 시작
            daumMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading); //내위치 및 나침반 모드
            //시작 시간 저장
            if(!startTimer){
                startDate=new Date(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                startDateStr=simpleDateFormat.format(startDate); //서버에 보내줄 시작 시간 string
                startTimer=true;


            }
        } else {
            buttonView.setBackgroundColor(getResources().getColor(R.color.colorPur));
            gpsInfo.stopGetGPS();
            daumMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            //현위치 표시 지우기
            daumMapView.setShowCurrentLocationMarker(false);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //사진 찍기 및 마커 설정
            case R.id.make_view_point_btn:
                if(daumMapView.getPolylines().length>1) {


                    String state = Environment.getExternalStorageState();
                    //외장메모리 검사!
                    if (Environment.MEDIA_MOUNTED.equals(state)) {
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        if (openCameraIntent.resolveActivity(getPackageManager()) != null) {
                            File file = null;
                            try {
                                file = createImageFile();

                            } catch (Exception e) {
                                Log.e("openCameraIntet error", e.toString());

                            }
                            if (file != null) {
                                Uri providerUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                                fileUri = providerUri;
                                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerUri);
                                startActivityForResult(openCameraIntent, REQUEST_IMG_CAPTURE);
                            }
                        }
                    } else {
                        Toast.makeText(this, "저장공간 접근 불가", Toast.LENGTH_SHORT);

                    }
                }else{
                    Toast.makeText(this,"GPS를 확인해주세요",Toast.LENGTH_SHORT).show();
                }
                break;

                //서버에 저장
            case R.id.save_to_server_btn:
                test_btn.setChecked(false);
                if(gpsDatas.isEmpty()){
                    Toast.makeText(this, "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //종료 시간
                stopDate=new Date(System.currentTimeMillis());
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                stopDateStr=simpleDateFormat.format(stopDate);

                FBCourseListDataSet sendData=new FBCourseListDataSet();
                sendData.setCourseName(courseName);
                sendData.setStartTime(startDateStr);
                sendData.setStopTime(stopDateStr);
                sendData.setDistance(distance);

                int cntViewPoint=0; //마커 수와 비교
                int tempIndex=0;       //gpsDatas 인텍스
                for (GPSDataSet temp : new ArrayList<>(gpsDatas)) {

                    //사진있는 포인트
                    if(temp.getViewPoint()==IS_VIEW_POINT){
                        Uri localUri=temp.getUri();

                        //스토리지 안 이미지 참조객체
                        final StorageReference imageRef=fbStorage.child("images/"+fbUserUid+"/"+localUri.getLastPathSegment());
                        //데이터베이스에 넣을 참조객체 string
                        final String imageRefStr=new String("images/"+fbUserUid+"/"+localUri.getLastPathSegment());

                        temp.setImgRefStr(imageRefStr);

                        if(tempIndex==gpsDatas.size()){     //리스트의 마지막이면
                            gpsDatas.add(temp);

                        }
                        else{
                            gpsDatas.set(tempIndex,temp);
                        }

                        if(temp.getIsMainImg()==IS_MAIN_IMG){
                            sendData.setMainImg(imageRefStr);
                        }

                        cntViewPoint++;

                        //업로딩함
                        UploadTask uploadTask=imageRef.putFile(localUri);

                        //업로드 리스너
                        //마지막 사진 업로드 시

                        if(cntViewPoint==cnt_marker){
                            uploadTask.addOnFailureListener(new OnFailureListener() {   //실패
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.e("UploadToServer","image upload fail");
                                    Toast.makeText(MapCourseTrackingActivity.this,"이미지 업로드 실패.",Toast.LENGTH_SHORT).show();
                                    finish();

                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {  //이미지 업로드 성공
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                    //이미지 업로드 끝나고, 데이터 업로드도 끝났으면
                                    isDoneUploadImage=true;
                                    if(isDoneUploadData && isDoneUploadImage){
                                        Log.e("UploadToServer","last image upload success");
                                        finish();
                                    }

                                }
                            });
                        }else{
                            uploadTask.addOnFailureListener(new OnFailureListener() {   //실패
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.e("UploadToServer","image upload fail");
                                    Toast.makeText(MapCourseTrackingActivity.this,"이미지 업로드 실패.",Toast.LENGTH_SHORT).show();


                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {  //이미지 업로드 성공
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                                    Log.e("UploadToServer","image upload success");
                                }
                            });
                        }


                    }

                    tempIndex++;
                }

                for(GPSDataSet temp1 : gpsDatas){
                    CourseListDataSet temp2=new CourseListDataSet();
                    temp2.setNo(temp1.getNo());
                    temp2.setLat(temp1.getLat());
                    temp2.setLon(temp1.getLon());
                    temp2.setImgRefStr(temp1.getImgRefStr());
                    temp2.setIsMainImg(temp1.getIsMainImg());
                    temp2.setPicCaption(temp1.getPicCaption());
                    temp2.setViewPoint(temp1.getViewPoint());
                    courseListDataSets.add(temp2);
                }

                sendData.setCourseListDataSets(courseListDataSets);
                fbDatabaseReference.child("users")
                        .child(fbUserUid)
                        .child("roots")
                        .push()
                        .setValue(sendData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        isDoneUploadData=true;
                        if(isDoneUploadData && isDoneUploadImage){
                            finish();
                        }
                    }
                })      .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapCourseTrackingActivity.this,"코스 업로드 실패",Toast.LENGTH_SHORT).show();
                    }
                });

                //이미지 업로드, 데이터 업로드 끝났으면


                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //사진 찍고 캡션 설정
        if (requestCode == REQUEST_IMG_CAPTURE) {

            if (resultCode == RESULT_OK) {
                try {
                    //사진 URI
                    galleryAddPic();

                    Intent setPicNameIntent=new Intent(this, SetPicCaptionActivity.class);
                    startActivityForResult(setPicNameIntent,REQUEST_IMG_NAME_SET);

                    return;


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(this, "갤러리 취소", Toast.LENGTH_SHORT).show();
            }
        }

        //사진, 캡션 후 마커 생성 및 gpsDatas에 마커, 사진 uri 넣음
        if(requestCode == REQUEST_IMG_NAME_SET){

            if(resultCode == RESULT_OK){
                //마커 생성
                MapPOIItem current_marker = new MapPOIItem();

                current_marker.setItemName(String.valueOf(cnt_marker));
                current_marker.setMapPoint(MapPoint.mapPointWithGeoCoord(lat, lon));
                current_marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                current_marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                daumMapView.addPOIItem(current_marker);

                //마커들 관리하기위해 리스트에 넣음
                markers.add(current_marker);


                //어레이리스트에 바꿔넣음
                GPSDataSet temp = gpsDatas.get(pointNoArrayIndex);

                temp.setViewPoint(IS_VIEW_POINT);
                temp.setUri(fileUri);
                temp.setPicCaption(data.getStringExtra("pic_caption"));

                if(cnt_marker==0){      //첫번재 이미지를 대표 이미지로
                    temp.setIsMainImg(IS_MAIN_IMG);
                }else{
                    temp.setIsMainImg(NOT_MAIN_IMG);
                }
                cnt_marker++;

                gpsDatas.remove(pointNoArrayIndex);
                gpsDatas.add(temp);

            }else{
                Toast.makeText(this,"사진 설정 취소",Toast.LENGTH_SHORT).show();
            }
        }


    }

    private File createImageFile() throws Exception{
        String time=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName="IMG_"+ time +".jpg";
        File imageFile = null;
        File storageDir= new File(Environment.getExternalStorageDirectory()+"/Pictures","MyTripCourse");

        if(!storageDir.exists()){
            storageDir.mkdir();

        }
        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath=imageFile.getAbsolutePath();

        return imageFile;
    }

    private void galleryAddPic(){
        Intent mediaScanIntent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this,"사진이 앨범에 저장되었습니다.",Toast.LENGTH_SHORT).show();
    }



}
