package goldmoon.MyTripCourse.Splash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import goldmoon.MyTripCourse.PasswordSetAndAuth.AuthPasswordActivity;
import goldmoon.MyTripCourse.PasswordSetAndAuth.SetPasswordActivity;
import goldmoon.MyTripCourse.R;

/**
 * 앱 실행시 시작되는 액티비티
 * 처음 실행 여부와 스플래시 액티비티
 */

public class CheckFirstExecuteAndSplashActivity extends AppCompatActivity {


    private final int SPLASH_DELAY = 1500; //스플래시 딜레이

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          Intent i;
                                            if(!checkFirstExecute()){
                                                i = new Intent(CheckFirstExecuteAndSplashActivity.this, SetPasswordActivity.class);
                                            }else{
                                                i = new Intent(CheckFirstExecuteAndSplashActivity.this, AuthPasswordActivity.class);
                                            }

                                            startActivity(i);
                                            finish();

                                      }
                                  },SPLASH_DELAY);
    }

    //앱 최초 실행 체크 메소드
    //PasswordSetHandler에서 앱 비밀번호 생성시 true로 바꿔준다.
    boolean checkFirstExecute(){
        SharedPreferences pref= getSharedPreferences("isPasswordExist", Activity.MODE_PRIVATE);
        boolean isPasswordExist= pref.getBoolean("isPasswordExist",false);


        return isPasswordExist;

    }

}
