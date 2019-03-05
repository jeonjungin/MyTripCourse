package goldmoon.MyTripCourse.PasswordSetAndAuth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


import goldmoon.MyTripCourse.LocalDB.DBManager;
import goldmoon.MyTripCourse.R;

/*
*  처음 실행시 비밀번호를 설정하기 위한 핸들러
* */
public class PasswordSetHandler implements View.OnClickListener{

    private Context mContext;

    private TextView infoText;
    //KeyPad
    private ImageView num1;
    private ImageView num2;
    private ImageView num3;
    private ImageView num4;
    private ImageView num5;
    private ImageView num6;
    private ImageView num7;
    private ImageView num8;
    private ImageView num9;
    private ImageView num0;
    private ImageView numB;
    private ImageView input1;
    private ImageView input2;
    private ImageView input3;
    private ImageView input4;

    private ArrayList<ImageView> arrInput;

    //패스워드
    private String input_pw="";
    //두번째 패스워드
    private String retry_pw="";

    private int index_pw=0; //현재 입력될 비밀번호 index
    private int index_pw_pre=0; //입력 전 비밀번호 index
    private boolean index_pw_isFull=false;  //비밀번호 4개 입력?

    private boolean retry=false;
//    public PasswordAuthHandler(Context context){
//        mContext=context;
//    }


    public PasswordSetHandler(Context context, TextView info, ImageView num1, ImageView num2, ImageView num3, ImageView num4, ImageView num5, ImageView num6, ImageView num7, ImageView num8, ImageView num9, ImageView num0, ImageView numB, ImageView input1, ImageView input2, ImageView input3, ImageView input4){
        this.mContext=context;
        this.infoText=info;
        this.num1=num1;
        this.num2=num2;
        this.num3=num3;
        this.num4=num4;
        this.num5=num5;
        this.num6=num6;
        this.num7=num7;
        this.num8=num8;
        this.num9=num9;
        this.num0=num0;
        this.numB=numB;
        this.input1=input1;
        this.input2=input2;
        this.input3=input3;
        this.input4=input4;

        num1.setOnClickListener(this);
        num2.setOnClickListener(this);
        num3.setOnClickListener(this);
        num4.setOnClickListener(this);
        num5.setOnClickListener(this);
        num6.setOnClickListener(this);
        num7.setOnClickListener(this);
        num8.setOnClickListener(this);
        num9.setOnClickListener(this);
        num0.setOnClickListener(this);
        numB.setOnClickListener(this);

        arrInput=new ArrayList<ImageView>();
        arrInput.add(input1);
        arrInput.add(input2);
        arrInput.add(input3);
        arrInput.add(input4);




    }

    //
    void changePwImg(int pre, int next, boolean isFull){

        Log.e("pre",String.valueOf(pre));
        Log.e("next",String.valueOf(next));
        Log.e("isFUll",String.valueOf(isFull));

        if(isFull){
            arrInput.get(pre).setImageResource(R.drawable.circle_salgu);
            for(ImageView temp:arrInput){
                temp.setImageResource(R.drawable.circle);
            }
        }else{
            if(next-pre>0){
                arrInput.get(pre).setImageResource(R.drawable.circle_salgu);
            }else if(pre-next>0){
                arrInput.get(next).setImageResource(R.drawable.circle);
            }
        }


    }




    @Override
    public void onClick(View v) {


        switch (v.getId()){
            case R.id.first_numKey1:
                input_pw=input_pw+'1';
                index_pw++;
                break;
            case R.id.first_numKey2:
                input_pw=input_pw+'2';
                index_pw++;
                break;
            case R.id.first_numKey3:
                input_pw=input_pw+'3';
                index_pw++;
                break;
            case R.id.first_numKey4:
                input_pw=input_pw+'4';
                index_pw++;
                break;
            case R.id.first_numKey5:
                input_pw=input_pw+'5';
                index_pw++;
                break;
            case R.id.first_numKey6:
                input_pw=input_pw+'6';
                index_pw++;
                break;
            case R.id.first_numKey7:
                input_pw=input_pw+'7';
                index_pw++;
                break;
            case R.id.first_numKey8:
                input_pw=input_pw+'8';
                index_pw++;
                break;
            case R.id.first_numKey9:
                input_pw=input_pw+'9';
                index_pw++;
                break;
            case R.id.first_numKey0:
                input_pw=input_pw+'0';
                index_pw++;
                break;
            case R.id.first_numKeyB:
                if(input_pw.length()>=1) {
                    input_pw = new String(input_pw.substring(0, input_pw.length() - 1));
                    index_pw--;
                }
                break;
        }

        //비밀번호 4개 입력 될 시
        if(input_pw.length()>=4){

            if(!retry){ //첫번째?
                retry=true; //두번째
                retry_pw=new String(input_pw);
                infoText.setText("retry input password");

            }else{  //두번째
                if(retry_pw.equals(input_pw)){  //다시 넣은 비밀번호가 같으면

                    DBManager dbManager= new DBManager(mContext,"password.db",null,1);
                    dbManager.setLoginPassword(input_pw);


                    //앱 실행시 최초실행 확인용, true로 바꿔준다.
                    SharedPreferences pref= mContext.getSharedPreferences("isPasswordExist", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isPasswordExist",true);
                    editor.commit();

                    dbManager.close();

                    //비밀번호 저장 및 액티비티 이동
                    Intent i = new Intent(mContext, GoogleLoginActivity.class);
                    mContext.startActivity(i);
                    ((Activity)mContext).finish();
                    Toast.makeText(mContext, "비밀번호 설정.",Toast.LENGTH_LONG).show();
                }else{
                    retry=false;
                    retry_pw=new String();
                    infoText.setText("set your password");
                    Toast.makeText(mContext, "비밀번호가 다릅니다.",Toast.LENGTH_LONG).show();
                }
            }

            input_pw=new String();
            index_pw_isFull=true;
            changePwImg(index_pw_pre,index_pw,index_pw_isFull);
            index_pw=0;
        }else{
            index_pw_isFull=false;
            changePwImg(index_pw_pre,index_pw,index_pw_isFull);
        }


        index_pw_pre=index_pw;

    }
}