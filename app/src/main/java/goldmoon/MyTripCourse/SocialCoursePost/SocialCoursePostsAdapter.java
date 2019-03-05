package goldmoon.MyTripCourse.SocialCoursePost;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import goldmoon.MyTripCourse.DataSet.CourseListDataSet;
import goldmoon.MyTripCourse.DataSet.SocialCoursePostCommentDataSet;
import goldmoon.MyTripCourse.DataSet.SocialCoursePostDataSet;
import goldmoon.MyTripCourse.R;

import static goldmoon.MyTripCourse.others.Constants.IS_VIEW_POINT;



public class SocialCoursePostsAdapter extends RecyclerView.Adapter<SocialCoursePostsAdapter.SocialCoursePostsViewHolder>{

    ArrayList<SocialCoursePostDataSet> datas;
    ArrayList<String>keys;
    Context mContext;
    String nickName;
    DatabaseReference fbDbRef;
    FirebaseUser fbUser;
    String fbUserUid;



    SocialCoursePostsAdapter(Context mContext, ArrayList<SocialCoursePostDataSet> datas,ArrayList<String> keys, String nickName){
        this.mContext=mContext;
        this.datas=datas;
        this.nickName=nickName;
        this.keys=keys;
        fbDbRef= FirebaseDatabase.getInstance().getReference();
        fbUser= FirebaseAuth.getInstance().getCurrentUser();
        fbUserUid=fbUser.getUid();



    }




    public static class SocialCoursePostsViewHolder extends RecyclerView.ViewHolder{

        ViewPager social_course_posts_row_view_pager;
        TextView tv_social_course_posts_row_course_name;
        TextView tv_social_course_posts_row_period;
        TextView tv_social_course_posts_row_distance;
        TextView tv_social_course_posts_row_comment_my_nick;
        EditText et_social_course_posts_row_comment_edit;
        ImageView iv_social_course_posts_row_comment_submit;
        ImageView iv_social_course_posts_row_delete_btn;
        LinearLayout li_social_course_posts_row_comments;
        LinearLayout li_social_course_posts_row_info;


        public SocialCoursePostsViewHolder(final View itemView) {
            super(itemView);

            tv_social_course_posts_row_course_name=itemView.findViewById(R.id.social_course_posts_row_course_name);
            tv_social_course_posts_row_period=itemView.findViewById(R.id.social_course_posts_row_period);
            tv_social_course_posts_row_distance=itemView.findViewById(R.id.social_course_posts_row_distance);
            li_social_course_posts_row_comments=itemView.findViewById(R.id.social_course_posts_comments);
            social_course_posts_row_view_pager=itemView.findViewById(R.id.social_course_posts_row_view_pager);
            tv_social_course_posts_row_comment_my_nick=itemView.findViewById(R.id.social_course_posts_row_comment_my_nick);
            et_social_course_posts_row_comment_edit=itemView.findViewById(R.id.social_course_posts_row_comment_edit);
            iv_social_course_posts_row_comment_submit=itemView.findViewById(R.id.social_course_posts_row_comment_submit);
            li_social_course_posts_row_info=itemView.findViewById(R.id.social_course_posts_row_info);
            iv_social_course_posts_row_delete_btn=itemView.findViewById(R.id.social_course_posts_row_delete_btn);




        }

    }

    @Override
    public SocialCoursePostsAdapter.SocialCoursePostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.social_course_posts_row, parent, false);
        return new SocialCoursePostsViewHolder(v);
    }


    @Override
    public void onBindViewHolder(SocialCoursePostsAdapter.SocialCoursePostsViewHolder holder, final int position) {

        Log.e("PostsAdapter ",datas.get(position).getCourseName());


        ArrayList<String> imgRefStrs=new ArrayList<>();

        final SocialCoursePostsViewHolder mViewHolder = holder;






        //뷰페이저
        for(CourseListDataSet temp:datas.get(position).getCourseListDataSets()){
            if(temp.getViewPoint()==IS_VIEW_POINT){
                imgRefStrs.add(temp.getImgRefStr());
            }
        }
        if (imgRefStrs.size() > 0) {

            SocialCoursePostImgViewPagerAdapter viewPagerAdapter=new SocialCoursePostImgViewPagerAdapter(mContext,imgRefStrs);
            mViewHolder.social_course_posts_row_view_pager.setAdapter(viewPagerAdapter);


        }


        mViewHolder.tv_social_course_posts_row_period.setText(datas.get(position).getStartTime()+" ~ "+datas.get(position).getStopTime());
        mViewHolder.tv_social_course_posts_row_course_name.setText(datas.get(position).getCourseName());
        mViewHolder.tv_social_course_posts_row_distance.setText(String.format("%.2f",datas.get(position).getDistance())+ " km");
        mViewHolder.tv_social_course_posts_row_comment_my_nick.setText(nickName);

        //게시물 삭제버튼
        if(datas.get(position).getWriterUid().equals(fbUserUid)){
            mViewHolder.iv_social_course_posts_row_delete_btn.setVisibility(View.VISIBLE);
            mViewHolder.iv_social_course_posts_row_delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fbDbRef.child("posts").child(keys.get(position)).removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            fbDbRef.child("comments").child(keys.get(position)).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                    Toast.makeText(mContext, "게시물 삭제 완료",Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });

                }
            });
        }else{
            mViewHolder.iv_social_course_posts_row_delete_btn.setVisibility(View.GONE);
        }



        //댓글가져옴
        Query getCommentsQuery=fbDbRef.child("comments").child(keys.get(position)).orderByChild("commentTime");
        getCommentsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("getCommentsQuery",dataSnapshot.toString());
                SocialCoursePostCommentDataSet comment;
                if(dataSnapshot!=null) {
                    comment = dataSnapshot.getValue(SocialCoursePostCommentDataSet.class);

                    LinearLayout tempLinearLayout=new LinearLayout(mContext);
                    TextView tempCommentTextView=new TextView(mContext);
                    TextView tempNickNameTextView=new TextView(mContext);

                    tempLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    tempLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(15,0,0,0);

                    tempCommentTextView.setLayoutParams(lp);
                    tempNickNameTextView.setLayoutParams(lp);

                    tempCommentTextView.setText(comment.getComment());
                    tempNickNameTextView.setText(comment.getUserNick());

                    tempLinearLayout.addView(tempNickNameTextView);
                    tempLinearLayout.addView(tempCommentTextView);
                    mViewHolder.li_social_course_posts_row_comments.addView(tempLinearLayout);
                }





            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //댓글 달기
        mViewHolder.iv_social_course_posts_row_comment_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {

                    case R.id.social_course_posts_row_comment_submit:

                        if (mViewHolder.et_social_course_posts_row_comment_edit.getTextSize() > 0) {

                            String myComment = mViewHolder.et_social_course_posts_row_comment_edit.getText().toString();

                            Date commentTimeDate=new Date(System.currentTimeMillis());
                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                            String commentTimeStr=simpleDateFormat.format(commentTimeDate); //서버에 보내줄 시간

                            SocialCoursePostCommentDataSet commentDataSet=new SocialCoursePostCommentDataSet();
                            commentDataSet.setComment(myComment);
                            commentDataSet.setCommentTime(commentTimeStr);
                            commentDataSet.setUserNick(nickName);

                            mViewHolder.et_social_course_posts_row_comment_edit.setText("");



                            fbDbRef.child("comments").child(keys.get(position)).push().setValue(commentDataSet).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(mContext,"댓글 등록 완료",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext,"댓글 등록 실패",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            Toast.makeText(mContext,"댓글을 채워주세요",Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
            }
        });
        //댓글 이외의 뷰 클릭시
        mViewHolder.li_social_course_posts_row_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent socialCoursePostTrackingIntent=new Intent(mContext.getApplicationContext(), SocialCoursePostTrackingActivity.class);
                socialCoursePostTrackingIntent.putExtra("postKey",keys.get(position));
                mContext.startActivity(socialCoursePostTrackingIntent);
            }
        });








    }

    @Override
    public int getItemCount() {
        return datas.size();
    }





}


