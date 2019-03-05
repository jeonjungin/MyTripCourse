package goldmoon.MyTripCourse.LocalCourse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import goldmoon.MyTripCourse.DataSet.FBCourseListDataSet;
import goldmoon.MyTripCourse.R;
import goldmoon.MyTripCourse.SocialCoursePost.SocialCoursePostsActivity;


public class CourseListActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    CourseListAdapter adapter;


    private DatabaseReference fbDatabaseReference;
    private FirebaseUser fbUser;
    private String fbUserUid;

    ImageView add_course_btn;
    ImageView sns_btn;


    ArrayList<FBCourseListDataSet> getDatas;
    ArrayList<String> getKeys;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list_layout);

        getDatas=new ArrayList<FBCourseListDataSet>();
        getKeys=new ArrayList<String>();

        add_course_btn=(ImageView)findViewById(R.id.add_course_btn);
        add_course_btn.setOnClickListener(this);
        sns_btn=(ImageView) findViewById(R.id.sns);
        sns_btn.setOnClickListener(this);

        fbDatabaseReference=FirebaseDatabase.getInstance().getReference();
        fbUser= FirebaseAuth.getInstance().getCurrentUser();
        fbUserUid=fbUser.getUid();

        setRecyclerView();





    }

    public void setRecyclerView(){
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView=(RecyclerView)findViewById(R.id.course_list_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new CourseListAdapter(this,getDatas,getKeys);


        recyclerView.setAdapter(adapter);
        getDataFromStorage();
    }


    public void getDataFromStorage(){

        Query query = fbDatabaseReference.child("users").child(fbUserUid).child("roots").orderByChild("courseName");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                FBCourseListDataSet data=dataSnapshot.getValue(FBCourseListDataSet.class);
                getKeys.add(dataSnapshot.getKey());
                getDatas.add(data);
//                if(s!=null)             //첫번째 값일 시 null 삽입
//                    getKeys.add(s);     //키값을 기준으로 수정,삭제한 데이터를 구별해야해서 저장

                adapter.notifyDataSetChanged();



                Log.e("onChildAdded",data.getCourseName());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FBCourseListDataSet data=dataSnapshot.getValue(FBCourseListDataSet.class);
                //최초항목(s==null) 일 시 바로 수정
                //아닐시 getKeys와 비교해서 수정 위치를 찾음(s는 수정값의 앞 데이터의 키를 가지기 때문에 첫번째 데이터는 s가 null임)
                if(s!=null){
                    int keyIndex=getKeys.indexOf(s);

                    getDatas.set(keyIndex+1, data);       //수정된 데이터 자리에 삽입
                    Log.e("onChildChanged",data.getCourseName()+"으로 수정("+String.valueOf(keyIndex+2)+"번째)");

                }else{
                    getDatas.set(0,data);
                    Log.e("onChildChanged",data.getCourseName()+"으로 수정(첫번째)");
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                FBCourseListDataSet removeData=dataSnapshot.getValue(FBCourseListDataSet.class);
                int removeIndex=getDatas.indexOf(removeData);
                getDatas.remove(removeIndex);
                getKeys.remove(removeIndex);

                adapter.notifyDataSetChanged();


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.add_course_btn:
                Intent set_course_name_intent= new Intent(this, SetCourseNameActivity.class);
                startActivity(set_course_name_intent);
                break;
            case R.id.sns:
                Intent tempIntent=new Intent(this, SocialCoursePostsActivity.class);
                startActivity(tempIntent);
                break;
        }
    }
}
