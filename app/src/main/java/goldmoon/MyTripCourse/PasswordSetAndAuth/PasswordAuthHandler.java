package goldmoon.MyTripCourse.PasswordSetAndAuth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;


import goldmoon.MyTripCourse.LocalDB.DBManager;
import goldmoon.MyTripCourse.R;

/**
 * 패스워드 인증
 */

public class PasswordAuthHandler implements View.OnClickListener{

    private Context mContext;

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

    private int index_pw=0; //현재 입력될 비밀번호 index
    private int index_pw_pre=0; //입력 전 비밀번호 index
    private boolean index_pw_isFull=false;  //비밀번호 4개 입력?

    private Vibrator vibrator;

    private DBManager dbManager;



    public PasswordAuthHandler(Context context, DBManager dbManager,ImageView num1,ImageView num2, ImageView num3,ImageView num4,ImageView num5,ImageView num6,ImageView num7,ImageView num8,ImageView num9,ImageView num0,ImageView numB,ImageView input1,ImageView input2,ImageView input3,ImageView input4){
        this.mContext=context;
        this.dbManager=dbManager;
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

        this.vibrator=(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

//        this.dbManager=new DBManager(mContext,dbName,null,dbVersion);

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
            case R.id.numkey1:
                input_pw=input_pw+'1';
                index_pw++;
                break;
            case R.id.numkey2:
                input_pw=input_pw+'2';
                index_pw++;
                break;
            case R.id.numkey3:
                input_pw=input_pw+'3';
                index_pw++;
                break;
            case R.id.numkey4:
                input_pw=input_pw+'4';
                index_pw++;
                break;
            case R.id.numkey5:
                input_pw=input_pw+'5';
                index_pw++;
                break;
            case R.id.numkey6:
                input_pw=input_pw+'6';
                index_pw++;
                break;
            case R.id.numkey7:
                input_pw=input_pw+'7';
                index_pw++;
                break;
            case R.id.numkey8:
                input_pw=input_pw+'8';
                index_pw++;
                break;
            case R.id.numkey9:
                input_pw=input_pw+'9';
                index_pw++;
                break;
            case R.id.numkey0:
                input_pw=input_pw+'0';
                index_pw++;
                break;
            case R.id.numkeyB:
                if(input_pw.length()>=1) {
                    input_pw = new String(input_pw.substring(0, input_pw.length() - 1));
                    index_pw--;
                }
                break;
        }

        //비밀번호 4개 입력 될 시
        if(input_pw.length()>=4){
            if(input_pw.equals(dbManager.getLoginPassword())){
//                Intent i = new Intent(mContext, MapCourseTrackingActivity.class);
                Intent i = new Intent(mContext, GoogleLoginActivity.class);

                mContext.startActivity(i);
                ((Activity)mContext).finish();
            }else{
                input_pw=new String();
                vibrator.vibrate(500);

            }
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