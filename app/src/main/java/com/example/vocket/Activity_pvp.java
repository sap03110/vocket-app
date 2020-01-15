package com.example.vocket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Activity_pvp extends AppCompatActivity {
    private int num = 5;
    static ProgressDialog progressDialog;
    private Timer timer;
    private final android.os.Handler handler = new android.os.Handler();
    int numcount = 0;

    Date d = new Date(System.currentTimeMillis());
    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
    String today = sf.format(d);

    private DatabaseReference userDB, pvpDB, vocaDB;
    Intent pIntent;
    ImageView tier_me, tier_you;
    TextView me, you, score_me, score_you;
    TextView voca,pron,qnum, qtime;
    String userid, yourid, userno, yourno, ratingKey, roomno; // 인텐트에서 받아오는 값
    TextView a1, a2, a3, a4;
    int first=1;
    int you_score; // 상대방 점수(나중에 결과표시할 때 사용)
    int my_rating, total, win, lose; // 나중에 결과에서 레이팅 표시

    ArrayList<Class_VocaObject> vocalist = new ArrayList<>();
    ArrayList<Class_VocaObject> incorrectList = new ArrayList<>();
    ArrayList<String> correctWordKeyList = new ArrayList<>();
    ArrayList<String> sang = new ArrayList<>();
    int[] checklist;
    int[] answerlist; // 0,1,2,3 중 정답 위치
    int current_prob_num = 1;
    int correct_num = 0;
    int incorrect_num = 0;
    int pick = 0; // 다음 랜덤 단어 선택
    answerClickListener al;
    int vocanum=10; // 총 10문제

    Boolean sangdae=false;
    Boolean hangbok=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pvp);
        progressDialog = new ProgressDialog(Activity_pvp.this);

        pIntent = getIntent();
        userid = pIntent.getStringExtra("userid");
        yourid = pIntent.getStringExtra("yourid");
        userno = pIntent.getExtras().getString("userno");
        yourno = pIntent.getExtras().getString("yourno");
        ratingKey = pIntent.getExtras().getString("ratingKey");
        roomno = pIntent.getExtras().getString("roomno");

        vocaDB = FirebaseDatabase.getInstance().getReference("pvpvoca");
        userDB = FirebaseDatabase.getInstance().getReference("users");
        pvpDB = FirebaseDatabase.getInstance().getReference("test_pvp");
        me = (TextView) findViewById(R.id.me);
        you = (TextView) findViewById(R.id.you);
        tier_me = (ImageView) findViewById(R.id.tier_me);
        tier_you = (ImageView) findViewById(R.id.tier_you);
        score_me = (TextView) findViewById(R.id.score_me);
        score_you = (TextView) findViewById(R.id.score_you);
        voca = (TextView) findViewById(R.id.report);
        pron = (TextView) findViewById(R.id.mention);
        qnum = (TextView) findViewById(R.id.qnum);
        qtime = (TextView) findViewById(R.id.qtime);
        a1 = (TextView) findViewById(R.id.a1);
        a2 = (TextView) findViewById(R.id.a2);
        a3 = (TextView) findViewById(R.id.a3);
        a4 = (TextView) findViewById(R.id.a4);

        checklist = new int[vocanum];
        answerlist = new int[vocanum];
        for (int i = 0; i < vocanum; i++) {
            checklist[i] = 0;
            answerlist[i] = (int) (Math.random() * 4);
        }
        al = new answerClickListener();
        a1.setOnClickListener(al);
        a2.setOnClickListener(al);
        a3.setOnClickListener(al);
        a4.setOnClickListener(al);

        // 상대방 끝낫냐??
        pvpDB.child("room").child(ratingKey).child(roomno).child(yourno+"pick").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                sang.add(dataSnapshot.getValue().toString());
                if (qnum.getText().equals("Q10")&&sang.size()>=10&&!sangdae&&!hangbok) {
                    progressDialog.dismiss();
                    ShowResult();
                    Log.i("상대가 끈낫당!",sangdae+"");
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                sang.add(dataSnapshot.getValue().toString());
                if (qnum.getText().equals("Q10")&&sang.size()>=10&&sangdae&&!hangbok) {
                    progressDialog.dismiss();
                    ShowResult();
                    Log.i("상대가 끈낫당!",sangdae+"");
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        // 상대방 정답
        pvpDB.child("room").child(ratingKey).child(roomno).child(yourno+"score").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                you_score=Integer.parseInt(dataSnapshot.getValue().toString());
                score_you.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                you_score=Integer.parseInt(dataSnapshot.getValue().toString());
                score_you.setText(dataSnapshot.getValue().toString());
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        // 상대방 포기
        pvpDB.child("room").child(ratingKey).child(roomno).child("cancel").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("항복",dataSnapshot.getValue().toString());
                if (dataSnapshot.exists())
                    if (dataSnapshot.getValue().toString().equals("user1")&&userno.equals("user2"))
                    {
                        userDB.child(userid).child("pvp_total").setValue(total+1);
                        userDB.child(userid).child("pvp_win").setValue(win+1);
                        userDB.child(userid).child("rating").setValue(my_rating+50);

                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_pvp.this);
                        builder.setMessage("상대방이 항복해서 게임이 종료되었습니다.\nRating "+my_rating+" + 50 = "+(my_rating+50));
                        builder.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        hangbok=true;
                                        finishAndRemoveTask();
                                    }
                                });
                        builder.show();
                    }
                    else if (dataSnapshot.getValue().toString().equals("user2")&&userno.equals("user1")) {
                        userDB.child(userid).child("pvp_total").setValue(total+1);
                        userDB.child(userid).child("pvp_win").setValue(win+1);
                        userDB.child(userid).child("rating").setValue(my_rating+50);

                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity_pvp.this);
                        builder.setMessage("상대방이 항복해서 게임이 종료되었습니다.\nRating "+my_rating+" + 50 = "+(my_rating+50));
                        builder.setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        hangbok=true;
                                        finishAndRemoveTask();
                                    }
                                });
                        builder.show();
                    }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    //user touch
    private class answerClickListener implements View.OnClickListener{
        boolean correct = false;
        @Override
        public void onClick(View v) {
            timer.cancel();
            Log.i("브이",v.getId()+"");
            switch (v.getId()){
                case R.id.a1: //0
                    if(answerlist[pick] == 0) {
                        correct_num++;
                        a1.setBackgroundResource(R.drawable.sp_main5);
                        correct = true;
                    }
                    else {
                        incorrect_num++;
                        a1.setBackgroundResource(R.drawable.sp_main4);
                        incorrectList.add(vocalist.get(pick));
                        correct = false;
                    }
                    break;
                case R.id.a2: //1
                    if(answerlist[pick] == 1) {
                        correct_num++;
                        a2.setBackgroundResource(R.drawable.sp_main5);
                        correct = true;
                    }
                    else {
                        incorrect_num++;
                        a2.setBackgroundResource(R.drawable.sp_main4);
                        incorrectList.add(vocalist.get(pick));
                        correct = false;
                    }
                    break;
                case R.id.a3: //2
                    if(answerlist[pick] == 2) {
                        correct_num++;
                        a3.setBackgroundResource(R.drawable.sp_main5);
                        correct = true;
                    }
                    else {
                        incorrect_num++;
                        a3.setBackgroundResource(R.drawable.sp_main4);
                        incorrectList.add(vocalist.get(pick));
                        correct = false;
                    }
                    break;
                case R.id.a4: //3
                    if(answerlist[pick] == 3) {
                        correct_num++;
                        a4.setBackgroundResource(R.drawable.sp_main5);
                        correct = true; //@drawable/sp_main
                    }
                    else {
                        incorrect_num++;
                        a4.setBackgroundResource(R.drawable.sp_main4);
                        incorrectList.add(vocalist.get(pick));
                        correct = false;
                    }
            }

            if(!correct){
                switch (answerlist[pick]){
                    case 0:
                        a1.setBackgroundResource(R.drawable.sp_main5);
                        break;
                    case 1:
                        a2.setBackgroundResource(R.drawable.sp_main5);
                        break;
                    case 2:
                        a3.setBackgroundResource(R.drawable.sp_main5);
                        break;
                    case 3:
                        a4.setBackgroundResource(R.drawable.sp_main5);
                        break;
                }

            }
            else {
                correctWordKeyList.add(vocalist.get(pick).voca);
            }

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            Handler delayHandler = new Handler();
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pvpDB.child("room").child(ratingKey).child(roomno).child(userno+"pick").child("v"+(current_prob_num-1)).setValue(correct);
                    pvpDB.child("room").child(ratingKey).child(roomno).child(userno+"score").child("score").setValue(correct_num);
                    score_me.setText(correct_num+"");

                    if (qnum.getText().equals("Q10")&&!hangbok) {
                        Log.i("아니","끝낫다!!");
                        ShowWait();
                    }
                    else {
                        Log.i("아니", current_prob_num + "");
                        current_prob_num++;
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                        }
                        qnum.setText("Q" + current_prob_num);
                        if (!hangbok) renderVoca();
                    }

                }}, 1000);

        }
    }

    // testing algorithm
    public int renderVoca(){
        a1.setBackgroundResource(R.drawable.sp_main);
        a2.setBackgroundResource(R.drawable.sp_main);
        a3.setBackgroundResource(R.drawable.sp_main);
        a4.setBackgroundResource(R.drawable.sp_main);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        boolean bpick = true;

//모든 단어를 조회완료했는지 검사
        int ch =0;
        for(int i=0;i<vocanum;i++){
            if(checklist[i]==1)
                ch++;
        }


        while(bpick) { // 다음 단어의 랜덤 선정
            pick = numcount++;
            for (int i = 0; i < vocanum; i++) {
                if (checklist[pick] == 1) {
                    bpick = true;
                    break;
                }
                bpick = false;
            }
        }

        checklist[pick] = 1;
        Log.i("pick :",pick+"");

        int[] another_ans = new int[3]; // 오답 리스트
        int[] ans_checklist = new int[vocanum];
        for(int i=0;i<vocanum;i++)
            ans_checklist[i] = 0;
        ans_checklist[pick] = 1;
        int num = 0;
        while(num<3) { // 다음 오답 출력의 랜덤 설정
            int rand = (int)(Math.random()*vocanum);
            if(rand != pick && ans_checklist[rand] == 0) {
                ans_checklist[rand] = 1;
                another_ans[num++]=rand;
            }
        }
        Log.i("another_ans :",another_ans[0]+","+another_ans[1]+","+another_ans[2]+",");

        voca.setText(vocalist.get(pick).voca);
        pron.setText(vocalist.get(pick).pronun);

        int j = 0;
        for(int i=0;i<4;i++){ // 답안 설정
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
        tt();
        return 0;
    }

    @Override
    public void onResume() {
        userDB.child(userid).addListenerForSingleValueEvent(new ValueEventListener() { //me
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = dataSnapshot.child("Email").getValue().toString();
                String rating = dataSnapshot.child("rating").getValue().toString();
                String pvp_win = dataSnapshot.child("pvp_win").getValue().toString();
                String pvp_lose = dataSnapshot.child("pvp_lose").getValue().toString();
                String pvp_total = dataSnapshot.child("pvp_total").getValue().toString();

                my_rating=Integer.parseInt(rating);
                win=Integer.parseInt(pvp_win);
                lose=Integer.parseInt(pvp_lose);
                total=Integer.parseInt(pvp_total);

                me.setText(email + "\nRating" + rating + "\n" + pvp_total + "전 " + pvp_win + "승 " + pvp_lose + "패");
                if (Integer.parseInt(rating) >= 500 && Integer.parseInt(rating) < 1000)
                    tier_me.setImageResource(R.drawable.tier1);
                else if (Integer.parseInt(rating) >= 1000 && Integer.parseInt(rating) < 1500)
                    tier_me.setImageResource(R.drawable.tier2);
                else if (Integer.parseInt(rating) >= 1500 && Integer.parseInt(rating) < 2000)
                    tier_me.setImageResource(R.drawable.tier3);
                else if (Integer.parseInt(rating) >= 2000 && Integer.parseInt(rating) < 2500)
                    tier_me.setImageResource(R.drawable.tier4);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        userDB.child(yourid).addListenerForSingleValueEvent(new ValueEventListener() { //상대방
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email = dataSnapshot.child("Email").getValue().toString();
                String rating = dataSnapshot.child("rating").getValue().toString();
                String pvp_win = dataSnapshot.child("pvp_win").getValue().toString();
                String pvp_lose = dataSnapshot.child("pvp_lose").getValue().toString();
                String pvp_total = dataSnapshot.child("pvp_total").getValue().toString();
                you.setText(email + "\nRating" + rating + "\n" + pvp_total + "전 " + pvp_win + "승 " + pvp_lose + "패");
                if (Integer.parseInt(rating) >= 500 && Integer.parseInt(rating) < 1000)
                    tier_you.setImageResource(R.drawable.tier1);
                else if (Integer.parseInt(rating) >= 1000 && Integer.parseInt(rating) < 1500)
                    tier_you.setImageResource(R.drawable.tier2);
                else if (Integer.parseInt(rating) >= 1500 && Integer.parseInt(rating) < 2000)
                    tier_you.setImageResource(R.drawable.tier3);
                else if (Integer.parseInt(rating) >= 2000 && Integer.parseInt(rating) < 2500)
                    tier_me.setImageResource(R.drawable.tier4);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        vocaDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //시험 모드에 따라 다르게 DB로드.
                if (first==1) {
                    if (userno.equals("user1")) {
                        int getvocaCount = 0, p;
                        Random rnd = new Random();
                        for (DataSnapshot vocaData : dataSnapshot.getChildren()) {
                            for (int i = 0; i < 12; i++) {
                                p = rnd.nextInt(498);
                                if (getvocaCount == 10)
                                    break;
                                String voca = vocaData.child(p + "").child("word").getValue().toString();
                                String mean1 = vocaData.child(p + "").child("meaning1").getValue().toString();
                                String mean2 = vocaData.child(p + "").child("meaning2").getValue().toString();
                                String pron = vocaData.child(p + "").child("pronounce").getValue().toString();
                                pvpDB.child("room").child(ratingKey).child(roomno).child("vocalist").child("v" + getvocaCount).child("voca").setValue(voca);
                                pvpDB.child("room").child(ratingKey).child(roomno).child("vocalist").child("v" + getvocaCount).child("pron").setValue(pron);
                                pvpDB.child("room").child(ratingKey).child(roomno).child("vocalist").child("v" + getvocaCount).child("meaning1").setValue(mean1);
                                pvpDB.child("room").child(ratingKey).child(roomno).child("vocalist").child("v" + getvocaCount).child("meaning2").setValue(mean2);
                                vocalist.add(new Class_VocaObject(1, voca, mean1, mean2, pron));
                                getvocaCount++;
                            }
                            first=0;
                            renderVoca();
                        }
                    }
                    else if (userno.equals("user2")) {
                        pvpDB.child("room").child(ratingKey).child(roomno).child("vocalist").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (int i = 0; i < 10; i++) {
                                    Log.i("히히",dataSnapshot.child("v" + i).child("voca").getValue().toString());
                                    vocalist.add(new Class_VocaObject(1, dataSnapshot.child("v" + i).child("voca").getValue().toString(), dataSnapshot.child("v" + i).child("meaning1").getValue().toString(), dataSnapshot.child("v" + i).child("meaning2").getValue().toString(), dataSnapshot.child("v" + i).child("pron").getValue().toString()));
                                }
                                first=0;
                                renderVoca();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        super.onResume();
    }

    private void update(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(num<0){
                    timer.cancel();
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    if (qnum.getText().equals("Q10")&&!hangbok) {
                        Log.i("아니","끝낫다!!");
                        ShowWait();
                    }
                    else {
                        pvpDB.child("room").child(ratingKey).child(roomno).child(userno + "pick").child("v" + (current_prob_num - 1)).setValue("false");
                        current_prob_num++;
                        try {Thread.sleep(1500);} catch (Exception e) { }
                        qnum.setText("Q" + current_prob_num);
                        if (!hangbok) renderVoca();
                    }
                }else{
                    qtime.setText(String.valueOf(num));
                    if (num==0) {
                        try {Thread.sleep(300);} catch (Exception e) { }
                        color();
                    }
                    num--;
                    Log.i("시간",qtime.getText().toString());
                }
            }
        };
        handler.post(runnable);
    }

    public void tt() {
        num=5;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                update();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
    }

    public void Back(View v) {
        final View dlgView = View.inflate(Activity_pvp.this,R.layout.dialog_pvp22,null);
        final Dialog pvpDialog3 = new Dialog(Activity_pvp.this);
        pvpDialog3.setContentView(dlgView);
        pvpDialog3.setCancelable(false);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = pvpDialog3.getWindow();
        int x = (int)(size.x * 0.9f);
        int y = (int)(size.y * 0.3f);
        window.setLayout(x, y);
        pvpDialog3.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        LinearLayout yes,no;
        yes = (LinearLayout)dlgView.findViewById(R.id.yes);
        no = (LinearLayout)dlgView.findViewById(R.id.no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDB.child(userid).child("pvp_total").setValue(total+1);
                userDB.child(userid).child("pvp_lose").setValue(lose+1);
                userDB.child(userid).child("rating").setValue(my_rating-50);
                pvpDB.child("room").child(ratingKey).child(roomno).child("cancel").child("cancel").setValue(userno);
                pvpDialog3.cancel();
                finishAndRemoveTask();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvpDialog3.cancel();
            }
        });
        pvpDialog3.show();
    }

    public void color() {
        switch (answerlist[pick]) {
            case 0:
                a1.setBackgroundResource(R.drawable.sp_main3);
                a2.setBackgroundResource(R.drawable.sp_main4);
                a3.setBackgroundResource(R.drawable.sp_main4);
                a4.setBackgroundResource(R.drawable.sp_main4);
                break;
            case 1:
                a1.setBackgroundResource(R.drawable.sp_main4);
                a2.setBackgroundResource(R.drawable.sp_main3);
                a3.setBackgroundResource(R.drawable.sp_main4);
                a4.setBackgroundResource(R.drawable.sp_main4);
                break;
            case 2:
                a1.setBackgroundResource(R.drawable.sp_main4);
                a2.setBackgroundResource(R.drawable.sp_main4);
                a3.setBackgroundResource(R.drawable.sp_main3);
                a4.setBackgroundResource(R.drawable.sp_main4);
                break;
            case 3:
                a1.setBackgroundResource(R.drawable.sp_main4);
                a2.setBackgroundResource(R.drawable.sp_main4);
                a3.setBackgroundResource(R.drawable.sp_main4);
                a4.setBackgroundResource(R.drawable.sp_main3);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        Log.i("항복","ㅎㅎ");
        hangbok=true;
        super.onDestroy();
    }

    public void ShowResult() {
        String rufrhk="";
        String msg="";

        pvpDB.child("room").child(ratingKey).child(roomno).child(userno+"pick").child("v10").setValue(true);
        sangdae=true;

        userDB.child(userid).child("PVP").child("date").setValue(today);
        userDB.child(userid).child("PVP").child("me").setValue(correct_num);
        userDB.child(userid).child("PVP").child("you").setValue(you_score);

        if (correct_num>you_score) {
            rufrhk="WIN!";
            msg="축하드려요! 총 "+correct_num+"점을 얻어 승리하셨습니다.\nRating "+my_rating+" + 50 = "+(my_rating+50);
            userDB.child(userid).child("pvp_total").setValue(total+1);
            userDB.child(userid).child("pvp_win").setValue(win+1);
            userDB.child(userid).child("rating").setValue(my_rating+50);
        }
        else if (correct_num==you_score) {
            rufrhk="DRAW";
            msg="다행이네요! 총 "+correct_num+"점을 얻어 비기셨습니다.\nRating "+my_rating+" + 0 = "+my_rating;
// 비기는 건 디비에 저장 ㄴㄴ
        }
        else {
            rufrhk="LOSE!";
            msg="안타깝네요! 총 "+correct_num+"점을 얻어 패배하셨습니다.\nRating "+my_rating+" - 50 = "+(my_rating-50);
            userDB.child(userid).child("pvp_total").setValue(total+1);
            userDB.child(userid).child("pvp_lose").setValue(lose+1);
            userDB.child(userid).child("rating").setValue(my_rating-50);
        }

// 결과 다이얼로그
        final View dlgView = View.inflate(Activity_pvp.this,R.layout.dialog_pvpexit,null);
        final Dialog pvpDialog2 = new Dialog(Activity_pvp.this);
        pvpDialog2.setContentView(dlgView);
        pvpDialog2.setCancelable(false);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Window window = pvpDialog2.getWindow();
        int x = (int)(size.x * 0.9f);
        int y = (int)(size.y * 0.3f);
        window.setLayout(x, y);
        pvpDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView result, message, ok;
        LinearLayout dd;
        result = (TextView)dlgView.findViewById(R.id.result);
        message = (TextView)dlgView.findViewById(R.id.message);
        dd = (LinearLayout)dlgView.findViewById(R.id.dd);
        result.setText(rufrhk);
        message.setText(msg);

        dd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pvpDialog2.cancel();
                finishAndRemoveTask();
            }
        });
        pvpDialog2.show();
    }

    public void ShowWait() {
        progressDialog.setMessage("상대가 끝날 때까지 대기중입니다...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
        progressDialog.show();
    }
}