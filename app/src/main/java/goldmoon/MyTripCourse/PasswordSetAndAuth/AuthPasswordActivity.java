package goldmoon.MyTripCourse.PasswordSetAndAuth;

import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;


import goldmoon.MyTripCourse.LocalDB.DBManager;
import goldmoon.MyTripCourse.R;

/*
*   지문과 비밀번호로 인증하기위한 액티비티
*   FingerPrintHandler, PasswordAuthHandler
*
*
* */
public class AuthPasswordActivity extends AppCompatActivity {

    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private FingerprintManager mFingerprintManager;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;
    private String KEY_NAME="GOD_JUNG_IN";
    private FingerPrintHandler mFingerprintHandler;
    private PasswordAuthHandler passwordAuthHandler;
    private DBManager dbManager;
    private String dbName="password.db";
    private int dbVersion=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_pw_auth_layout);




        mFingerprintManager=(FingerprintManager)getSystemService(FINGERPRINT_SERVICE);
        generateKey();
        dbManager=new DBManager(this,dbName,null,dbVersion);
        passwordAuthHandler=new PasswordAuthHandler(this,
                dbManager,
                (ImageView) findViewById(R.id.numkey1),
                (ImageView)findViewById(R.id.numkey2),
                (ImageView)findViewById(R.id.numkey3),
                (ImageView)findViewById(R.id.numkey4),
                (ImageView)findViewById(R.id.numkey5),
                (ImageView)findViewById(R.id.numkey6),
                (ImageView)findViewById(R.id.numkey7),
                (ImageView)findViewById(R.id.numkey8),
                (ImageView)findViewById(R.id.numkey9),
                (ImageView)findViewById(R.id.numkey0),
                (ImageView)findViewById(R.id.numkeyB),
                (ImageView)findViewById(R.id.inputKey1),
                (ImageView)findViewById(R.id.inputKey2),
                (ImageView)findViewById(R.id.inputKey3),
                (ImageView)findViewById(R.id.inputKey4)
        );
//        passwordAuthHandler=new PasswordAuthHandler(this);

        if(cipherInit()){
            cryptoObject=new FingerprintManager.CryptoObject(cipher);
            mFingerprintHandler=new FingerPrintHandler(this);
            mFingerprintHandler.startAuth(mFingerprintManager, cryptoObject);

        }


    }



    void generateKey(){
        try{
            mKeyStore=KeyStore.getInstance("AndroidKeyStore");

        }catch (Exception e){

        }
        try{
            mKeyGenerator=KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");

        }catch (Exception e){

        }

        try{
            mKeyStore.load(null);
            mKeyGenerator.init(
                    new KeyGenParameterSpec.Builder(
                            KEY_NAME,KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build()
            );
            mKeyGenerator.generateKey();

        }catch (Exception e){

        }
    }

    boolean cipherInit(){
        try{
            cipher=Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES+"/"
                    +KeyProperties.BLOCK_MODE_CBC+"/"
                    +KeyProperties.ENCRYPTION_PADDING_PKCS7
            );
        }catch (Exception e){

        }

        try{
            mKeyStore.load(null);
            SecretKey key=(SecretKey)mKeyStore.getKey(KEY_NAME,null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {

        super.onStop();
    }
    @Override
    protected void onDestroy() {
        //지문인식으로 인증할시 DB가 안닫힘-->여기서 닫아주기위해 passauth핸들러로 DBManager 객체를 보내줌
        dbManager.close();
        mFingerprintHandler.stopFingerAuth();   //
        super.onDestroy();
    }
}
