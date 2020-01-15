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

import java.util.ArrayList;

public class Activity_Retest extends AppCompatActivity {
    int mode;
    int vocanum;
    Intent pIntent;
    private DatabaseReference dbRef;

    ArrayList<Class_VocaObject> vocalist = new ArrayList<>();
    ArrayList<Class_VocaObject> incorrectList = new ArrayList<>();
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

        //dbRef = FirebaseDatabase.getInstance().getReference("mynote");
        pIntent = getIntent();
        vocalist = getIntent().getParcelableArrayListExtra("list");
        vocanum = vocalist.size();

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
        test_title.setText("틀린 단어 재시험");


        al = new answerClickListener();
        a1.setOnClickListener(al);
        a2.setOnClickListener(al);
        a3.setOnClickListener(al);
        a4.setOnClickListener(al);

        renderVoca();
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
                        ansbox1.setBackgroundColor(0xff93ef5e);
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
                        ansbox2.setBackgroundColor(0xff93ef5e);
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
                        ansbox3.setBackgroundColor(0xff93ef5e);
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
                        ansbox4.setBackgroundColor(0xff93ef5e);
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
                        ansbox1.setBackgroundColor(0xff93ef5e);
                        break;
                    case 1:
                        ansbox2.setBackgroundColor(0xff93ef5e);
                        break;
                    case 2:
                        ansbox3.setBackgroundColor(0xff93ef5e);
                        break;
                    case 3:
                        ansbox4.setBackgroundColor(0xff93ef5e);
                        break;
                }
                incorrect_image.setVisibility(View.VISIBLE);
            }
            else
                correct_image.setVisibility(View.VISIBLE);

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
            Intent resultIntent = new Intent(getApplicationContext(), Activity_Test_Result.class);
            resultIntent.putExtra("correct",correct_num);
            resultIntent.putExtra("incorrect",incorrect_num);
            resultIntent.putExtra("vocanum",vocanum);
            resultIntent.putParcelableArrayListExtra("incorrectList",incorrectList);
            resultIntent.putExtra("mode","틀린 단어 모드");

            updateDB();
            startActivity(resultIntent);
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
        //Log.i("another_ans  :",another_ans[0]+","+another_ans[1]+","+another_ans[2]+",");

        voca.setText(vocalist.get(pick).voca);
        pron.setText(vocalist.get(pick).pronun);

        int j = 0;
        for(int i=0;i<4;i++){  // 답안 설정
            if (i == answerlist[pick]) {
                if(i == 0)
                    a1.setText(vocalist.get(pick).mean1);
                else if(i == 1)
                    a2.setText(vocalist.get(pick).mean1);
                else if(i==2)
                    a3.setText(vocalist.get(pick).mean1);
                else
                    a4.setText(vocalist.get(pick).mean1);
            }
            else{
                switch (i){
                    case 0:
                        a1.setText(vocalist.get(another_ans[j]).mean1);
                        break;
                    case 1:
                        a2.setText(vocalist.get(another_ans[j]).mean1);
                        break;
                    case 2:
                        a3.setText(vocalist.get(another_ans[j]).mean1);
                        break;
                    case 3:
                        a4.setText(vocalist.get(another_ans[j]).mean1);
                }
                j++;
            }
        }
        return  0;
    }

    void updateDB(){

    }
}
