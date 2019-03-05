package goldmoon.MyTripCourse.PasswordSetAndAuth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Vibrator;
import android.widget.Toast;

/**
 * 지문 인식
 */

public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback {

    private Context mContext;
    private Vibrator vibrator;
    private CancellationSignal cancellationSignal;

    public FingerPrintHandler(Context context){
        this.mContext=context;
        this.vibrator=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject){
        cancellationSignal = new CancellationSignal();
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        vibrator.vibrate(500);
        Toast.makeText(mContext, "5회 실패하여 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationFailed() {
        vibrator.vibrate(500);
        Toast.makeText(mContext, "등록되지 않은 지문 입니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        Toast.makeText(mContext, "지문인식 성공!", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(mContext, GoogleLoginActivity.class);
        mContext.startActivity(i);
        ((Activity)mContext).finish();
    }

    public void stopFingerAuth(){
        if(cancellationSignal!=null && !cancellationSignal.isCanceled() ){
            cancellationSignal.cancel();
        }
    }
}
