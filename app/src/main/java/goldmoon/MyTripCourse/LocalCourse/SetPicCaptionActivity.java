package goldmoon.MyTripCourse.LocalCourse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import goldmoon.MyTripCourse.R;

//사진 설명

public class SetPicCaptionActivity extends AppCompatActivity implements View.OnClickListener{


    LinearLayout back_btn;
    TextView submit_btn;
    EditText pic_caption_edit;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_pic_caption_layout);

        back_btn=(LinearLayout)findViewById(R.id.set_pic_caption_back_btn);
        submit_btn=(TextView)findViewById(R.id.set_pic_caption_submit_btn);
        pic_caption_edit=(EditText)findViewById(R.id.set_pic_caption_edit);

        back_btn.setOnClickListener(this);
        submit_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.set_pic_caption_back_btn:
                setResult(RESULT_CANCELED);
                finish();

                break;
            case R.id.set_pic_caption_submit_btn:
                Intent setCaptionIntent=new Intent();
                setCaptionIntent.putExtra("pic_caption",pic_caption_edit.getText().toString());
                setResult(RESULT_OK,setCaptionIntent);
                finish();
                break;
        }
    }
}
