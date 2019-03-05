package goldmoon.MyTripCourse.LocalDB;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;


/*
* 데이터베이스 암호화를 위해
* android.database.sqlite.SQLiteDatabase 대신에
* 오픈소스 SQLCipher
* net.sqlcipher.database.SQLiteDatabase 사용
*
* SQLiteDatabase.loadLibs(컨텍스트나 액티비티); DB조작 전에 무조건 사용
*
* */


/*
*
* 테이블
* user----------------------
* id        integer   pk  auto
* password  text
*
* passwordList--------------
* id        integer pk  auto
* site      text
* password  text
*
* */

/*
*
* Course----------------------
* name      text                        경로 이름
* no        integer     pk      auto    경로 순서
* lat       float
* lon       float
* viewpoint integer                     마커? 0false 1true
* imageroot text                        내 폰 이미지 경로
*
*
* */

public class DBManager{

    private Context mContext;
    private String dbName;
    private int dbVersion;
    private CipherDatabaseHelper dbHelper;
    private SQLiteDatabase db;



    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        this.mContext=context;
        this.dbName=name;
        this.dbVersion=version;

        SQLiteDatabase.loadLibs(mContext);

        this.dbHelper=new CipherDatabaseHelper(context, name, factory, version);
        this.db=dbHelper.getWritableDatabase(dbName);



    }

    public void close(){

        if(db.isOpen())
            db.close();

    }

    public class CipherDatabaseHelper extends SQLiteOpenHelper {


        public CipherDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

            super(context, name, factory, version);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            //app login password
            //id integer auto
            //password text
            String sql_1="create table user(id integer primary key autoincrement," +
                    "password text)";
            db.execSQL(sql_1);

            String sql_2="create table course(name text," +
                    "no integer primary key autoincrement," +
                    "lat real," +
                    "lon real," +
                    "viewpoint integer," +
                    "imageroot text)";

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            SQLiteDatabase.loadLibs(mContext);
        }
    }

    //로그인 하기 위한 패스워드
    public String getLoginPassword(){

        String loginPassword=new String();

        SQLiteDatabase.loadLibs(mContext);

        Cursor cursor= db.rawQuery("select * from user",null);

        while(cursor.moveToNext()){
            loginPassword =cursor.getString(1);
        }

        if (loginPassword.isEmpty()){
            Log.e("DBManager","fn: getLoginPassword // return value error");
            return "error";
        }else{
            return loginPassword;
        }

    }


    //로그인 패스워드 설정
    public void setLoginPassword(String inputPassword){

        SQLiteDatabase.loadLibs(mContext);

        String sql="insert into user values(null," +
                inputPassword+")";
        db.execSQL(sql);
    }





}