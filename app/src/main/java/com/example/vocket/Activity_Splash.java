package com.example.vocket;

import android.content.Intent;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class Activity_Splash extends AppCompatActivity {

    boolean autologin = false;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
    String regID = FirebaseInstanceId.getInstance().getToken();

    Intent gotoNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 상태바 까지 제거(Full screen)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userData : dataSnapshot.getChildren()) {
                    if (userData.child("regNum").getValue().toString().equals(regID)) {
                        autologin = true;
                        gotoNext = new Intent(getApplication(), Activity_Vocket.class);
                        gotoNext.putExtra("uid",userData.getKey());
                        gotoNext.putExtra("email",userData.child("Email").getValue().toString());
                    }
                    else {
                        Handler hd = new Handler();
                        hd.postDelayed(new splash(), 3000); // 딜레이(쓰레딩 작렬)
                    }
                }
            }
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private class splash implements Runnable{
        public void run(){
            if(autologin)
                startActivity(gotoNext);
            else
                startActivity(new Intent(getApplication(), Activity_Howto.class));
            Activity_Splash.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
       //non action
    }
}
