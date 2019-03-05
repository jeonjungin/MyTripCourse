package goldmoon.MyTripCourse.LocalCourse;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import goldmoon.MyTripCourse.DataSet.FBCourseListDataSet;
import goldmoon.MyTripCourse.R;
import goldmoon.MyTripCourse.RewindCourse.RewindCourseTrackingActivity;

//받아오는 데이터 제한 필요

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.CourseListViewHolder>{


    ArrayList<FBCourseListDataSet> datas;
    ArrayList<String>keys;
    Context mContext;


    CourseListAdapter(Context mContext,ArrayList<FBCourseListDataSet> inputDatas,ArrayList<String> inputKeys){
        this.mContext=mContext;
        datas=inputDatas;
        keys=inputKeys;
        Log.e("CourseListAdapter","ing");
    }

    public static class CourseListViewHolder extends RecyclerView.ViewHolder{

        ImageView iv_img;
        TextView tv_course_name;
        TextView tv_course_period;
        TextView tv_course_distance;



        public CourseListViewHolder(View itemView) {
            super(itemView);
            iv_img=(ImageView)itemView.findViewById(R.id.list_row_img);
            tv_course_name=(TextView)itemView.findViewById(R.id.list_row_course_name);
            tv_course_period=(TextView)itemView.findViewById(R.id.list_row_period);
            tv_course_distance=(TextView)itemView.findViewById(R.id.list_row_distance);


        }
    }



    @Override
    public CourseListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_row, parent, false);
        Log.e("onCreateViewHolder","ing");

        return new CourseListViewHolder(v);
    }


    @Override
    public void onBindViewHolder(CourseListViewHolder holder, final int position) {
        CourseListViewHolder mViewHolder= holder;

        if(datas.get(position).getMainImg()!=null) {
            StorageReference imgRef = FirebaseStorage.getInstance().getReference().child(datas.get(position).getMainImg());
            Glide.with(mContext)
                    .load(imgRef)
                    .into(mViewHolder.iv_img);
        }else{
            mViewHolder.iv_img.setImageResource(R.drawable.no_img);
        }

        mViewHolder.tv_course_name.setText(datas.get(position).getCourseName());
        mViewHolder.tv_course_distance.setText(String.format("%.2f",datas.get(position).getDistance())+" km");
        mViewHolder.tv_course_period.setText(datas.get(position).getStartTime()+" ~ "+datas.get(position).getStopTime());


        mViewHolder.iv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent rewindCourseIntent=new Intent(mContext, RewindCourseTrackingActivity.class);
                rewindCourseIntent.putExtra("FBKey",keys.get(position));

                mContext.startActivity(rewindCourseIntent);

            }
        });






    }


    @Override
    public int getItemCount() {
        return datas.size();
    }




}
