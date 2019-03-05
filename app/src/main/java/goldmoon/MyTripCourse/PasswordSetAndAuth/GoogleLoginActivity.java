package goldmoon.MyTripCourse.PasswordSetAndAuth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import goldmoon.MyTripCourse.LocalCourse.CourseListActivity;
import goldmoon.MyTripCourse.R;


/**
 * 구글 로그인 액티비티
 *
 */

public class GoogleLoginActivity  extends AppCompatActivity implements View.OnClickListener{

    ImageView login_btn;
    LottieAnimationView login_animation_view;
    LinearLayout login_animation_parent_view;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_login_layout);

        mContext=this;

        //로그인 버튼
        login_btn=(ImageView) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(this);

        //로그인 시 onStartActivityResult로 다시 넘어온 후 애니메이션 시작
        login_animation_view=(LottieAnimationView)findViewById(R.id.login_animation_view);
        login_animation_parent_view=(LinearLayout)findViewById(R.id.login_animation_parent_view);

        //구글 로그인 init
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth=FirebaseAuth.getInstance();


    }

    //로그인 되어있을 시 바로 넘어감
    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser!=null){
//            Intent i = new Intent(this, MapCourseTrackingActivity.class);
//            startActivity(i);
//            finish();
//        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    //로그인 시작 메서드
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //로그인 애니메이션
        login_animation_parent_view.setVisibility(View.VISIBLE);
        login_animation_view.setAnimation("lottie/outline-lock.json");
        login_animation_view.setSpeed(2.5f);
        login_animation_view.loop(true);
        login_animation_view.playAnimation();

        //
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("GoogleLogin", "Google sign in failed", e);
                Toast.makeText(this,"GoogleLoginFail",Toast.LENGTH_SHORT).show();
                finish();
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("GoogleLogin", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("GoogleLogin", "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();

                            mContext.startActivity(new Intent(mContext,CourseListActivity.class));

                            finish();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("GoogleLogin", "signInWithCredential:failure", task.getException());


                        }

                        // ...
                    }
                });
    }





    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btn:
                signIn();
                break;
        }
    }
}
