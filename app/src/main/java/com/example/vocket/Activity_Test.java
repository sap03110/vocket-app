package com.example.vocket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

public class Activity_Test extends AppCompatActivity {
    int mode;
    int vocanum;
    Intent pIntent;
    private DatabaseReference dbRef,userDB;

    ArrayList<Class_VocaObject> vocalist = new ArrayList<>();
    ArrayList<Class_VocaObject> incorrectList = new ArrayList<>();
    ArrayList<String> correctWordKeyList = new ArrayList<>();
    int[] checklist;
    int[] answerlist; // 0,1,2,3 중 정답 위치

    TextView a1,a2,a3,a4;
    TextView voca,pron,qnum,test_title;
    ImageView correct_image,incorrect_image;
    LinearLayout ansbox1,ansbox2,ansbox3,ansbox4;
    int current_prob_num = 1;
    int correct_num = 0;
    int incorrect_num = 0;
    int pick = 0; // 다음 랜덤 단어 선택
    answerClickListener al;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__test);

        dbRef = FirebaseDatabase.getInstance().getReference("mynote");
        userDB = FirebaseDatabase.getInstance().getReference("users");
        pIntent = getIntent();
        mode = pIntent.getIntExtra("mode",0);
        vocanum = pIntent.getExtras().getInt("vcnum");
        ansbox1 = (LinearLayout)findViewById(R.id.ans1);
        ansbox2 = (LinearLayout)findViewById(R.id.ans2);
        ansbox3 = (LinearLayout)findViewById(R.id.ans3);
        ansbox4 = (LinearLayout)findViewById(R.id.ans4);
        correct_image = (ImageView)findViewById(R.id.correctimage);
        incorrect_image = (ImageView)findViewById(R.id.incorrectimage);

        checklist = new int[vocanum];
        answerlist = new int[vocanum];
        for(int i=0;i<vocanum;i++) {
            checklist[i] = 0;
            answerlist[i] = (int)(Math.random() * 4);
        }

        a1 = (TextView)findViewById(R.id.a);
        a2 = (TextView)findViewById(R.id.b);
        a3 = (TextView)findViewById(R.id.c);
        a4 = (TextView)findViewById(R.id.d);
        voca = (TextView)findViewById(R.id.report);
        pron = (TextView)findViewById(R.id.mention);
        qnum = (TextView)findViewById(R.id.qnum);
        test_title = (TextView)findViewById(R.id.test_title);

        switch (mode){
            case 1:
                test_title.setText("최근 추가한 단어 모드");
                break;
            case 2:
                test_title.setText("오래된 단어 모드");
                break;
            case 3:
                test_title.setText("자주 틀리는 단어 모드");
                break;
            case 4:
                test_title.setText("알아서 단어 모드");
        }

        al = new answerClickListener();
        a1.setOnClickListener(al);
        a2.setOnClickListener(al);
        a3.setOnClickListener(al);
        a4.setOnClickListener(al);

        dbRef.child(getIntent().getStringExtra("uid")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {   //시험 모드에 따라 다르게 DB로드.
                int getvocaCount = 0;
                for (DataSnapshot userData : dataSnapshot.getChildren()) {
                    //if(getvocaCount == vocanum)
                    //    break;
                    int savetime = Integer.parseInt(userData.child("savetime").getValue().toString());
                    String voca = userData.child("word").getValue().toString();
                    String mean1 = userData.child("meaning1").getValue().toString();
                    String mean2 = userData.child("meaning2").getValue().toString();
                    String pron = userData.child("pronounce").getValue().toString();

                    int total = Integer.parseInt(userData.child("try").getValue().toString());
                    int suc = Integer.parseInt(userData.child("success").getValue().toString());
                    Double fail = 1. - (double)suc/total+0.0001;

                    vocalist.add(new Class_VocaObject(savetime,voca,mean1,mean2,pron,fail));
                    getvocaCount++; // 일단 전부 다 받는다
                }

                Collections.sort(vocalist);

                switch(mode){
                    case 1: // 최근 추가
                        Collections.reverse(vocalist);
                        break;
                    case 2: // 오래된 단어
                        Collections.sort(vocalist);
                        break;
                    case 3: // 자주 틀림

                        break;
                    case 4: // 알아서
                }


                if(getvocaCount < vocanum)
                    vocanum = getvocaCount; // 만약 불러온 단어수가 사용자가 설정한 수보다 적다면,
                renderVoca();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    //user touch
    private class answerClickListener implements View.OnClickListener{
        boolean correct = false;
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.a:  //0
                    if(answerlist[pick] == 0) {
                        correct_num++;
                        ansbox1.setBackgroundColor(0xffAFD485);
                        correct = true;
                    }
                    else {
                        incorrect_num++;
                        ansbox1.setBackgroundColor(0xfff54949);
                        incorrectList.add(vocalist.get(pick));
                        correct = false;
                    }
                    break;
                case R.id.b:  //1
                    if(answerlist[pick] == 1) {
                        correct_num++;
                        ansbox2.setBackgroundColor(0xffAFD485);
                        correct = true;
                    }
                    else {
                        incorrect_num++;
                        ansbox2.setBackgroundColor(0xfff54949);
                        incorrectList.add(vocalist.get(pick));
                        correct = false;
                    }
                    break;
                case R.id.c:  //2
                    if(answerlist[pick] == 2) {
                        correct_num++;
                        ansbox3.setBackgroundColor(0xffAFD485);
                        correct = true;
                    }
                    else {
                        incorrect_num++;
                        ansbox3.setBackgroundColor(0xfff54949);
                        incorrectList.add(vocalist.get(pick));
                        correct = false;
                    }
                    break;
                case R.id.d:  //3
                    if(answerlist[pick] == 3) {
                        correct_num++;
                        ansbox4.setBackgroundColor(0xffAFD485);
                        correct = true;
                    }
                    else {
                        incorrect_num++;
                        ansbox4.setBackgroundColor(0xfff54949);
                        incorrectList.add(vocalist.get(pick));
                        correct = false;
                    }
            }

            if(!correct){
                switch (answerlist[pick]){
                    case 0:
                        ansbox1.setBackgroundColor(0xffAFD485);
                        break;
                    case 1:
                        ansbox2.setBackgroundColor(0xffAFD485);
                        break;
                    case 2:
                        ansbox3.setBackgroundColor(0xffAFD485);
                        break;
                    case 3:
                        ansbox4.setBackgroundColor(0xffAFD485); //93ef5e
                        break;
                }
                incorrect_image.setVisibility(View.VISIBLE);
            }
            else {
                correct_image.setVisibility(View.VISIBLE);
                correctWordKeyList.add(vocalist.get(pick).voca);
           }

            //disable touch
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            //delay 1.5초 후 실행
            Handler delayHandler = new Handler();
            delayHandler.postDelayed(new Runnable() {
                @Override public void run()
                {
                    current_prob_num++;
                    qnum.setText("Q"+current_prob_num);
                    renderVoca();
                }}, 1500);
        }
    }


    // testing algorithm
    public int renderVoca(){
        ansbox1.setBackgroundColor(0xff1287cf);
        ansbox2.setBackgroundColor(0xff1287cf);
        ansbox3.setBackgroundColor(0xff1287cf);
        ansbox4.setBackgroundColor(0xff1287cf);
        correct_image.setVisibility(View.INVISIBLE);
        incorrect_image.setVisibility(View.INVISIBLE);
        //enable touch
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        boolean bpick = true;

        //모든 단어를 조회완료했는지 검사
        int ch =0;
        for(int i=0;i<vocanum;i++){
            if(checklist[i]==1)
                ch++;
        }

        if(ch == vocanum) {
            //Toast.makeText(Activity_Test.this, "모든 단어 조회끝", Toast.LENGTH_SHORT).show();
            Intent resultIntent = new Intent(getApplicationContext(), Activity_Test_Result.class);
            resultIntent.putExtra("correct",correct_num);
            resultIntent.putExtra("incorrect",incorrect_num);
            resultIntent.putExtra("vocanum",vocanum);
            resultIntent.putParcelableArrayListExtra("incorrectList",incorrectList);
            switch (mode){
                case 1:
                    resultIntent.putExtra("mode","최근 추가한 단어 모드");
                    break;
                case 2:
                    resultIntent.putExtra("mode","오래된 단어 모드");
                    break;
                case 3:
                    resultIntent.putExtra("mode","자주 틀리는 단어 모드");
                    break;
                case 4:
                    resultIntent.putExtra("mode","알아서 단어 모드");
            }

            updateDB();
            startActivity(resultIntent);    // --> 결과창으로
            finish();
            return 1;
        }

        while(bpick) {    // 다음 단어의 랜덤 선정
            pick = (int)(Math.random()*vocanum);
            for (int i = 0; i < vocanum; i++) {
                if (checklist[pick] == 1) {
                    bpick = true;
                    break;
                }
                bpick = false;
            }
        }
        checklist[pick] = 1;
        Log.i("pick  :",pick+"");

        int[] another_ans = new int[3];   // 오답 리스트
        int[] ans_checklist = new int[vocanum];
        for(int i=0;i<vocanum;i++)
            ans_checklist[i] = 0;
        ans_checklist[pick] = 1;
        int num = 0;
        while(num<3) {  // 다음 오답 출력의 랜덤 설정
            int rand = (int)(Math.random()*vocanum);
            if(rand != pick && ans_checklist[rand] == 0) {
                ans_checklist[rand] = 1;
                another_ans[num++]=rand;
            }
        }
        Log.i("another_ans  :",another_ans[0]+","+another_ans[1]+","+another_ans[2]+",");

        voca.setText(vocalist.get(pick).voca);
        pron.setText(vocalist.get(pick).pronun);

        int j = 0;
        for(int i=0;i<4;i++){  // 답안 설정
            if (i == answerlist[pick]) {
                if(i == 0)
                    a1.setText(vocalist.get(pick).mean1+", "+vocalist.get(pick).mean2);
                else if(i == 1)
                    a2.setText(vocalist.get(pick).mean1+", "+vocalist.get(pick).mean2);
                else if(i==2)
                    a3.setText(vocalist.get(pick).mean1+", "+vocalist.get(pick).mean2);
                else
                    a4.setText(vocalist.get(pick).mean1+", "+vocalist.get(pick).mean2);
            }
            else{
               switch (i){
                   case 0:
                       a1.setText(vocalist.get(another_ans[j]).mean1+", "+vocalist.get(another_ans[j]).mean2);
                       break;
                   case 1:
                       a2.setText(vocalist.get(another_ans[j]).mean1+", "+vocalist.get(another_ans[j]).mean2);
                       break;
                   case 2:
                       a3.setText(vocalist.get(another_ans[j]).mean1+", "+vocalist.get(another_ans[j]).mean2);
                       break;
                   case 3:
                       a4.setText(vocalist.get(another_ans[j]).mean1+", "+vocalist.get(another_ans[j]).mean2);
               }
               j++;
            }
        }
        return  0;
    }


    // 각 단어 정보 업데이트.
    void updateDB(){
        //note 업데이트
        dbRef.child(getIntent().getStringExtra("uid")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {   //시험 모드에 따라 다르게 DB로드.
                //"try" 컬럼 업데이트
                for(int i=0;i<vocanum;i++) {
                    int trynum = Integer.parseInt(dataSnapshot.child(vocalist.get(i).voca).child("try").getValue().toString());   //load
                    trynum++;
                    dbRef.child(getIntent().getStringExtra("uid")).child(vocalist.get(i).voca).child("try").setValue(trynum); //save
                }
                //"success" 컬럼 업데이트
                for(int i=0;i<correctWordKeyList.size();i++) {
                    int success = Integer.parseInt(dataSnapshot.child(correctWordKeyList.get(i)).child("success").getValue().toString());   //load
                    success++;
                    dbRef.child(getIntent().getStringExtra("uid")).child(correctWordKeyList.get(i)).child("success").setValue(success); //save
                    dbRef.child(getIntent().getStringExtra("uid")).child(correctWordKeyList.get(i)).child("understand").setValue(1);
                }

                //"understand" 컬럼 업데이트
                for(int i=0;i<incorrectList.size();i++) {
                    dbRef.child(getIntent().getStringExtra("uid")).child(incorrectList.get(i).voca).child("understand").setValue(0);
                }
            }
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        //user 정보 업데이트
        userDB.child(getIntent().getStringExtra("uid")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cumul = Integer.parseInt(dataSnapshot.child("cumul").getValue().toString());
                int exp = Integer.parseInt(dataSnapshot.child("exp").getValue().toString());
                int today, today_cor,today_incor;

                Date d = new Date(System.currentTimeMillis());
                SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
                TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
                sf.setTimeZone(tz);
                String date = sf.format(d);

                today = Integer.parseInt(dataSnapshot.child("weekly").child(date).child("today").getValue().toString());
                today_cor = Integer.parseInt(dataSnapshot.child("weekly").child(date).child("today_cor").getValue().toString());
                today_incor = Integer.parseInt(dataSnapshot.child("weekly").child(date).child("today_incor").getValue().toString());

                cumul+=vocanum;
                exp+=correctWordKeyList.size()*10;

                today+=vocanum;
                today_cor+=correctWordKeyList.size();
                today_incor+=incorrectList.size();

                userDB.child(getIntent().getStringExtra("uid")).child("cumul").setValue(cumul);
                userDB.child(getIntent().getStringExtra("uid")).child("exp").setValue(exp);

                userDB.child(getIntent().getStringExtra("uid")).child("weekly").child(date).child("today").setValue(today);
                userDB.child(getIntent().getStringExtra("uid")).child("weekly").child(date).child("today_cor").setValue(today_cor);
                userDB.child(getIntent().getStringExtra("uid")).child("weekly").child(date).child("today_incor").setValue(today_incor);
            }

            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }
}
