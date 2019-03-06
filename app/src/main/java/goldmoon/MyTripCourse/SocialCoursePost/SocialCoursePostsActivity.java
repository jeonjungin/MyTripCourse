package goldmoon.MyTripCourse.SocialCoursePost;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import goldmoon.MyTripCourse.DataSet.SocialCoursePostDataSet;
import goldmoon.MyTripCourse.R;

/**
 * 게시물 리스트 보여줄 액티비티
 */

public class SocialCoursePostsActivity extends AppCompatActivity implements View.OnClickListener{

    SocialCoursePostsAdapter adapter;


    RecyclerView social_course_posts_list_view;
    TextView tv_social_course_posts_is_exist;
    LinearLayout li_social_course_posts_back_btn;

    ArrayList<SocialCoursePostDataSet> postDataSets;

    DatabaseReference fbDatabaseReference;
    FirebaseUser fbUser;
    String fbUserUid;
    String cNickName;   //댓글에 달릴 내 닉네임
    ArrayList<String> getKeys;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.social_course_posts_layout);

        postDataSets=new ArrayList<>();
        getKeys=new ArrayList<>();
        social_course_posts_list_view=findViewById(R.id.social_course_posts_list_view);
        tv_social_course_posts_is_exist=findViewById(R.id.social_course_posts_is_exist);
        li_social_course_posts_back_btn=findViewById(R.id.social_course_posts_back_btn);
        li_social_course_posts_back_btn.setOnClickListener(this);


        fbUser= FirebaseAuth.getInstance().getCurrentUser();
        cNickName=fbUser.getDisplayName();
        fbUserUid=fbUser.getUid();


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        social_course_posts_list_view.setLayoutManager(linearLayoutManager);
        adapter=new SocialCoursePostsAdapter(this, postDataSets,getKeys,cNickName);
        social_course_posts_list_view.setAdapter(adapter);


        fbDatabaseReference=FirebaseDatabase.getInstance().getReference();
        final Query getPostsQuery=fbDatabaseReference.child("posts").orderByChild("stopTime");
        //게시물 추가
        getPostsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(postDataSets.size()==0) {
                    postDataSets.add(dataSnapshot.getValue(SocialCoursePostDataSet.class));
                    Log.e("SocialPostsActivity","onChildAdded "+adapter.datas.get(0).getCourseName());
                    getKeys.add(dataSnapshot.getKey());
                    adapter.notifyItemInserted(0);
                }
                else {
                    postDataSets.add(0, dataSnapshot.getValue(SocialCoursePostDataSet.class));
                    Log.e("SocialPostsActivity","onChildAdded "+adapter.datas.get(0).getCourseName());
                    getKeys.add(0,dataSnapshot.getKey());
                    adapter.notifyItemInserted(0);
                }

                tv_social_course_posts_is_exist.setVisibility(View.GONE);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            //게시물 삭제
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int removedIndex=0;
                SocialCoursePostDataSet temp=dataSnapshot.getValue(SocialCoursePostDataSet.class);
                for(SocialCoursePostDataSet findData : postDataSets){
                    if(findData.equals(temp)){
                        break;
                    }
                    removedIndex++;
                }
                postDataSets.remove(removedIndex);
                adapter.notifyItemRemoved(removedIndex);
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
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.social_course_posts_back_btn:
                finish();
                break;
        }
    }
}
