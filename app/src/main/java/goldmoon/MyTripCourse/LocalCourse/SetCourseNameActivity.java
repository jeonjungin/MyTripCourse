package goldmoon.MyTripCourse.LocalCourse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import goldmoon.MyTripCourse.R;

/*
* 경로 추적 전 설정할 경로 이름 설정 액티비티
*
* */

public class SetCourseNameActivity extends AppCompatActivity implements View.OnClickListener{

    private LinearLayout back_btn;
    private TextView submit_btn;
    private EditText course_name_edit;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_course_name_layout);

        back_btn=(LinearLayout)findViewById(R.id.set_course_name_back_btn);
        submit_btn=(TextView)findViewById(R.id.set_course_name_submit_btn);
        course_name_edit=(EditText)findViewById(R.id.set_course_name_edit);

        back_btn.setOnClickListener(this);
        submit_btn.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){
            //CourseListActivity로 감
            case R.id.set_course_name_back_btn:
                finish();
                break;

            case R.id.set_course_name_submit_btn:
                Intent tracking_intent = new Intent(this, MapCourseTrackingActivity.class);
                tracking_intent.putExtra("CourseName",course_name_edit.getText().toString());
                startActivity(tracking_intent);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
