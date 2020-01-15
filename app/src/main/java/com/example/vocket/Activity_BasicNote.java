package com.example.vocket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

public class Activity_BasicNote extends AppCompatActivity {
    LinearLayout toeic,toefl,teps,sat,tran,publicc; // 해당 뷰로 자동위치조정을 위한 뷰객체
    ScrollView scrollView;
    Intent intent,gotoNextIntent;
    Button eachBt1, eachBt2, eachBt3, eachBt4;  // 토익
    Button eachBt5, eachBt6, eachBt7, eachBt8;  // 텝스
    Button eachBt9, eachBt10, eachBt11, eachBt12;  // 토플
    Button eachBt13, eachBt14;  // 공무원
    Button eachBt15, eachBt16, eachBt17;  // 편입
    Button eachBt18, eachBt19, eachBt20, eachBt21;  // 수능

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__basic_note);

        intent = getIntent();
        toeic = (LinearLayout) findViewById(R.id.basicToeic);
        toefl = (LinearLayout) findViewById(R.id.basicToefl);
        teps = (LinearLayout) findViewById(R.id.basicTeps);
        sat = (LinearLayout) findViewById(R.id.basicSat);
        tran = (LinearLayout) findViewById(R.id.basicTrans);
        publicc = (LinearLayout) findViewById(R.id.basicPublicc);

        scrollView = (ScrollView) findViewById(R.id.basicScroll);
        scrollView.post(new Runnable() {        // post 메서드는 UI thread의 MessageQueue에 작업을 push 해둔다. 이는 main thread의 ui 초기화 이후 진행된다.
            @Override
            public void run() {
                int viewPosY = 0;
                switch (intent.getStringExtra("type")) {
                    case "toeic":
                        viewPosY = toeic.getTop();
                        break;
                    case "toefl":
                        viewPosY = toefl.getTop();
                        break;
                    case "teps":
                        viewPosY = teps.getTop();
                        break;
                    case "sat":
                        viewPosY = sat.getTop();
                        break;
                    case "trans":
                        viewPosY = tran.getTop();
                        break;
                    case "publicc":
                        viewPosY = publicc.getTop();

                }
                scrollView.scrollTo(0,viewPosY);
            }
        });


        gotoNextIntent =  new Intent(getApplicationContext(), Activity_VocaFrame.class);
        gotoNextIntent.putExtra("uid",intent.getStringExtra("uid"));
        eachBt1 = (Button)findViewById(R.id.bt_toeic_1);
        eachBt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","토익");
                gotoNextIntent.putExtra("category","[초급] 토익이 처음이라면? 기초 영단어 for 영포자");
                startActivity(gotoNextIntent);
            }
        });

        eachBt2 = (Button)findViewById(R.id.bt_toeic_2);
        eachBt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","토익");
                gotoNextIntent.putExtra("category","[중급] 공대생이라면 750점은 나와야쥬?");
                startActivity(gotoNextIntent);
            }
        });

        eachBt3 = (Button)findViewById(R.id.bt_toeic_3);
        eachBt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","토익");
                gotoNextIntent.putExtra("category","[고급] 신토익 필수! 새로운 유형에 대비하는 단어 모음 200선");
                startActivity(gotoNextIntent);
            }
        });

        eachBt4 = (Button)findViewById(R.id.bt_toeic_4);
        eachBt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","토익");
                gotoNextIntent.putExtra("category","[고급] 900+을 위한 영단어 모음");
                startActivity(gotoNextIntent);
            }
        });

        eachBt5 = (Button)findViewById(R.id.bt_teps_1);
        eachBt5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","텝스");
                gotoNextIntent.putExtra("category","[초급] 어휘파트 시간이 부족하다면 먼저 이것부터!");
                startActivity(gotoNextIntent);
            }
        });

        eachBt6 = (Button)findViewById(R.id.bt_teps_2);
        eachBt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","텝스");
                gotoNextIntent.putExtra("category","[중급] 연세대 가고싶어요! 301점만 넘기는 텝스 빈출 단어");
                startActivity(gotoNextIntent);
            }
        });

        eachBt7 = (Button)findViewById(R.id.bt_teps_3);
        eachBt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","텝스");
                gotoNextIntent.putExtra("category","[중급] 리스닝 240점 맞는 빈출 단어 모음");
                startActivity(gotoNextIntent);
            }
        });

        eachBt8 = (Button)findViewById(R.id.bt_teps_4);
        eachBt8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","텝스");
                gotoNextIntent.putExtra("category","[고급] 텝스 고오오오오오오오오오ㅡ급 단어");
                startActivity(gotoNextIntent);
            }
        });

        eachBt9 = (Button)findViewById(R.id.bt_toefl_1);
        eachBt9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","토플");
                gotoNextIntent.putExtra("category","[초급] 시사 교양 어휘 200선");
                startActivity(gotoNextIntent);
            }
        });

        eachBt10 = (Button)findViewById(R.id.bt_toefl_2);
        eachBt10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","토플");
                gotoNextIntent.putExtra("category","[중급] 해커스 토플 초록이");
                startActivity(gotoNextIntent);
            }
        });

        eachBt11 = (Button)findViewById(R.id.bt_toefl_3);
        eachBt11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","토플");
                gotoNextIntent.putExtra("category","[중급] 500개로 정리한 토플 적중 단어 모음");
                startActivity(gotoNextIntent);
            }
        });

        eachBt12 = (Button)findViewById(R.id.bt_toefl_4);
        eachBt12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","토플");
                gotoNextIntent.putExtra("category","[고급] 독해가 어려워요 ㅠㅠㅠㅠ");
                startActivity(gotoNextIntent);
            }
        });

        eachBt13 = (Button)findViewById(R.id.bt_gong_1);
        eachBt13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","공무원");
                gotoNextIntent.putExtra("category","[초급] 공무원을 준비하는 왕초보라면?");
                startActivity(gotoNextIntent);
            }
        });

        eachBt14 = (Button)findViewById(R.id.bt_gong_2);
        eachBt14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","공무원");
                gotoNextIntent.putExtra("category","[고급] 공무원을 준비하는 고수라면?");
                startActivity(gotoNextIntent);
            }
        });

        eachBt15 = (Button)findViewById(R.id.bt_pyun_1);
        eachBt15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","편입");
                gotoNextIntent.putExtra("category","[초급] 보카바이블 입문자용");
                startActivity(gotoNextIntent);
            }
        });

        eachBt16 = (Button)findViewById(R.id.bt_pyun_2);
        eachBt16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","편입");
                gotoNextIntent.putExtra("category","[중급] 편머리 독해 기본 단어 500");
                startActivity(gotoNextIntent);
            }
        });

        eachBt17 = (Button)findViewById(R.id.bt_pyun_3);
        eachBt17.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","편입");
                gotoNextIntent.putExtra("category","[고급] 편입 10개년 정리 빈출 단어");
                startActivity(gotoNextIntent);
            }
        });

        eachBt18 = (Button)findViewById(R.id.bt_su_1);
        eachBt18.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","수능");
                gotoNextIntent.putExtra("category","[초급] 많이 노셨던 학생들을 위한 개념원리 TOP 200");
                startActivity(gotoNextIntent);
            }
        });

        eachBt19 = (Button)findViewById(R.id.bt_su_2);
        eachBt19.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","수능");
                gotoNextIntent.putExtra("category","[중급] 이것만 알면 수능은 문제없다! 기본적으로 알아야 할 단어 모음");
                startActivity(gotoNextIntent);
            }
        });

        eachBt20 = (Button)findViewById(R.id.bt_su_3);
        eachBt20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","수능");
                gotoNextIntent.putExtra("category","[중급] 안정적인 점수를 위한 우선순위 영단어");
                startActivity(gotoNextIntent);
            }
        });

        eachBt21 = (Button)findViewById(R.id.bt_su_4);
        eachBt21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextIntent.putExtra("exam","수능");
                gotoNextIntent.putExtra("category","[고급] 시험장에 들어가기 전에 꼭 봐야할 단어");
                startActivity(gotoNextIntent);
            }
        });
    }

    public void Back(View view) {
        finish();
    }
}
