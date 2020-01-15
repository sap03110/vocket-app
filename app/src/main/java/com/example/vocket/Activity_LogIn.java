package com.example.vocket;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Activity_LogIn extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    // google login 연동
    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    // firebase database 연동 및 자동로그인
    private DatabaseReference dbRef;
    String regID; //기기 고유아이디
    String[] recentSixday = new String[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // google login 연동
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        // firebase 연동
        FirebaseApp.initializeApp(getApplicationContext());
        dbRef = FirebaseDatabase.getInstance().getReference("users");  // 로그인을 위한 디비 가져오기
        regID = FirebaseInstanceId.getInstance().getToken(); // 단말기 등록아이디 확인

        // 최근 6일의 날짜 계산( 하루는 86,400,000‬ 밀리초)
        long minus = 86400000;
        for(int i=0;i<6;i++) {
            Date d = new Date(System.currentTimeMillis()-minus*i);
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
            TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
            sf.setTimeZone(tz);
            recentSixday[i] = sf.format(d);
        }

    }

    public void onGoogleClick(View view){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onNextClick(View view){
        View dlgView = View.inflate(this,R.layout.dialog_alarm1,null);

        final Dialog loginDialog = new Dialog(this);
        loginDialog.setContentView(dlgView);

        Button ok,cancel;

        ok = (Button)dlgView.findViewById(R.id.ok_bt);
        cancel = (Button)dlgView.findViewById(R.id.cancel_bt);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gotoVocket = new Intent(getApplicationContext(), Activity_Vocket.class);
                startActivity(gotoVocket);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDialog.cancel();
            }
        });
        loginDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(Activity_LogIn.this, "success", Toast.LENGTH_SHORT).show();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(Activity_LogIn.this, "fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = mAuth.getCurrentUser();
                            Intent gotoVocket = new Intent(getApplicationContext(), Activity_Vocket.class);

                            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    boolean firstLogin = true;

                                    for (DataSnapshot userData : dataSnapshot.getChildren()) {
                                        // 새로운기기에 최초 로그인
                                        if(userData.child("Id").getValue().toString().equals(user.getUid())){
                                            dbRef.child(user.getUid()).child("recentlogin").setValue(recentSixday[0]);
                                            firstLogin = false;
                                            break;
                                        }
                                    }

                                    // 최초 등록 && 신규 기기 사용 (완전 처음)
                                    if(firstLogin){
                                        dbRef.child(user.getUid()).child("Id").setValue(user.getUid());
                                        dbRef.child(user.getUid()).child("Email").setValue(user.getEmail());
                                        dbRef.child(user.getUid()).child("regNum").setValue(regID);
                                        dbRef.child(user.getUid()).child("recentlogin").setValue(recentSixday[0]);
                                        //dbRef.child(user.getUid()).child("today").setValue(0);
                                        dbRef.child(user.getUid()).child("cumul").setValue(0);
                                        dbRef.child(user.getUid()).child("avgweek").setValue(0);
                                        dbRef.child(user.getUid()).child("exp").setValue(0);
                                        dbRef.child(user.getUid()).child("rating").setValue(1000);
                                        dbRef.child(user.getUid()).child("pvp_total").setValue(0);
                                        dbRef.child(user.getUid()).child("pvp_win").setValue(0);
                                        dbRef.child(user.getUid()).child("pvp_lose").setValue(0);
                                        //dbRef.child(user.getUid()).child("today_cor").setValue(0);
                                        //dbRef.child(user.getUid()).child("today_incor").setValue(0);
                                        //dbRef.child(user.getUid()).child("weekly").child(recentSixday[0]).setValue(0);
                                        //dbRef.child(user.getUid()).child("weekly").child(recentSixday[1]).setValue(0);
                                        //dbRef.child(user.getUid()).child("weekly").child(recentSixday[2]).setValue(0);
                                        //dbRef.child(user.getUid()).child("weekly").child(recentSixday[3]).setValue(0);
                                        //dbRef.child(user.getUid()).child("weekly").child(recentSixday[4]).setValue(0);
                                        //dbRef.child(user.getUid()).child("weekly").child(recentSixday[5]).setValue(0);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) { }
                            });

                            gotoVocket.putExtra("uid",user.getUid());
                            gotoVocket.putExtra("email",user.getEmail());

                            startActivity(gotoVocket);
                            finish();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){}


}
