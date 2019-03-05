package goldmoon.MyTripCourse.PasswordSetAndAuth;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

import goldmoon.MyTripCourse.R;

/**
 * 초기 실행시 앱 비밀번호를 설정하기 위한 액티비티
 *
 */

public class SetPasswordActivity extends AppCompatActivity {

    PasswordSetHandler passwordSetHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_pw_set_layout);

        permissionChk();

        passwordSetHandler=new PasswordSetHandler(this,
                (TextView)findViewById(R.id.first_text),
                (ImageView) findViewById(R.id.first_numKey1),
                (ImageView)findViewById(R.id.first_numKey2),
                (ImageView)findViewById(R.id.first_numKey3),
                (ImageView)findViewById(R.id.first_numKey4),
                (ImageView)findViewById(R.id.first_numKey5),
                (ImageView)findViewById(R.id.first_numKey6),
                (ImageView)findViewById(R.id.first_numKey7),
                (ImageView)findViewById(R.id.first_numKey8),
                (ImageView)findViewById(R.id.first_numKey9),
                (ImageView)findViewById(R.id.first_numKey0),
                (ImageView)findViewById(R.id.first_numKeyB),
                (ImageView)findViewById(R.id.first_inputKey1),
                (ImageView)findViewById(R.id.first_inputKey2),
                (ImageView)findViewById(R.id.first_inputKey3),
                (ImageView)findViewById(R.id.first_inputKey4)
        );


    }
    //권한 체크
    void permissionChk(){
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(SetPasswordActivity.this,"권한 거부",Toast.LENGTH_LONG).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("앱을 이용하기 위해선 권한이 필요합니다.")
                .setDeniedMessage("설정>권한에서 허용해주세용")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }


}
