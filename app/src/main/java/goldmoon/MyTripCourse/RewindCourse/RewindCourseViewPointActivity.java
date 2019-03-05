package goldmoon.MyTripCourse.RewindCourse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import goldmoon.MyTripCourse.R;

/**
 * 마커 클릭시 사진 보여줄 액티비티
 */

public class RewindCourseViewPointActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout li_rewind_course_view_point_back_btn;
    ImageView iv_rewind_course_view_point_image;
    TextView tv_rewind_course_view_point_caption;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rewind_course_view_point_layout);

        li_rewind_course_view_point_back_btn=(LinearLayout)findViewById(R.id.rewind_course_view_point_back_btn);
        li_rewind_course_view_point_back_btn.setOnClickListener(this);
        iv_rewind_course_view_point_image=(ImageView)findViewById(R.id.rewind_course_view_point_image);
        tv_rewind_course_view_point_caption=(TextView)findViewById(R.id.rewind_course_view_point_caption);

        Intent getIntent = getIntent();

        String fbRefStr=getIntent.getStringExtra("ImageRefStr");
        String caption=getIntent.getStringExtra("ImageCaption");

        tv_rewind_course_view_point_caption.setText(caption);

        StorageReference imgRef=FirebaseStorage.getInstance().getReference().child(fbRefStr);
        Glide.with(this)
                .load(imgRef)
                .into(iv_rewind_course_view_point_image);





    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rewind_course_view_point_back_btn:
                finish();
                break;
        }
    }
}
