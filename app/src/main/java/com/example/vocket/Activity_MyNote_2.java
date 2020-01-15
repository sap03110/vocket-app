package com.example.vocket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Activity_MyNote_2 extends AppCompatActivity {
    TextView voca, pronounce, mean, sentence, sentence_mean, info;
    String word;
    String userid;
    String bookmark_yes_or_no;  // 단어장 내의 북마크 여부
    String meaning1="", meaning2="", meaning3="", meaning4="", meaning5="";
    ImageView bookmark, edit, remove;
    private DatabaseReference dbRef;
    ArrayList<String> m;  // 뜻 저장
    String mm="";
    int b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__my_note_2);
        dbRef = FirebaseDatabase.getInstance().getReference("mynote");
        info = (TextView)findViewById(R.id.info);
        voca = (TextView)findViewById(R.id.voca);
        pronounce = (TextView)findViewById(R.id.pronounce);
        mean = (TextView)findViewById(R.id.mean);
        sentence = (TextView)findViewById(R.id.sentence);
        sentence_mean = (TextView)findViewById(R.id.sentence_mean);
        bookmark = (ImageView)findViewById(R.id.bookmark);
        m = new ArrayList<String>();
        userid = getIntent().getStringExtra("uid");

        word = getIntent().getExtras().getString("word");
        voca.setText(word);

        remove = (ImageView)findViewById(R.id.remove);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dlgView = View.inflate(Activity_MyNote_2.this,R.layout.dialog_alarm1,null);
                final Dialog loginDialog = new Dialog(Activity_MyNote_2.this);
                loginDialog.setContentView(dlgView);
                Button ok,cancel;
                TextView textView16;
                textView16 = (TextView)dlgView.findViewById(R.id.textView16);
                textView16.setText("단어를 삭제하시겠습니까?");
                ok = (Button)dlgView.findViewById(R.id.ok_bt);
                cancel = (Button)dlgView.findViewById(R.id.cancel_bt);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dbRef.child(userid).child(word).removeValue();
                        Toast.makeText(Activity_MyNote_2.this, "단어가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginDialog.dismiss();
                    }
                });
                loginDialog.show();
            }
        });


        edit = (ImageView)findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View dlgView = View.inflate(Activity_MyNote_2.this,R.layout.dialog_addvoca,null);
                final Dialog editDialog = new Dialog(Activity_MyNote_2.this);
                editDialog.setContentView(dlgView);
                TextView ok, title, words, mean1, mean2, mean3, mean4, mean5;
                final CheckBox check1, check2, check3, check4, check5;
                title = (TextView) dlgView.findViewById(R.id.title);
                ok = (TextView) dlgView.findViewById(R.id.ok_bt);
                words = (TextView) dlgView.findViewById(R.id.words);
                mean1= (TextView) dlgView.findViewById(R.id.mean1);
                mean2= (TextView) dlgView.findViewById(R.id.mean2);
                mean3= (TextView) dlgView.findViewById(R.id.mean3);
                mean4= (TextView) dlgView.findViewById(R.id.mean4);
                mean5= (TextView) dlgView.findViewById(R.id.mean5);
                check1=(CheckBox) dlgView.findViewById(R.id.check1);
                check2=(CheckBox) dlgView.findViewById(R.id.check2);
                check3=(CheckBox) dlgView.findViewById(R.id.check3);
                check4=(CheckBox) dlgView.findViewById(R.id.check4);
                check5=(CheckBox) dlgView.findViewById(R.id.check5);
                words.setVisibility(View.GONE);
                title.setText("삭제할 단어 뜻을 선택하세요!");
                mean1.setText(meaning1);
                mean2.setText(meaning2);
                mean3.setText(meaning3);
                mean4.setText(meaning4);
                mean5.setText(meaning5);
                if (meaning1.equals("")) {check1.setVisibility(View.GONE); mean1.setVisibility(View.GONE);}
                if (meaning2.equals("")) {check2.setVisibility(View.GONE); mean2.setVisibility(View.GONE);}
                if (meaning3.equals("")) {check3.setVisibility(View.GONE); mean3.setVisibility(View.GONE);}
                if (meaning4.equals("")) {check4.setVisibility(View.GONE); mean4.setVisibility(View.GONE);}
                if (meaning5.equals("")) {check5.setVisibility(View.GONE); mean5.setVisibility(View.GONE);}


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!check1.isChecked()&&!check2.isChecked()&&!check3.isChecked()&&!check4.isChecked()&&!check5.isChecked())
                            Toast.makeText(Activity_MyNote_2.this, "삭제할 뜻을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                        else {
                            if (check1.isChecked())
                                dbRef.child(userid).child(word).child("meaning1").setValue("");
                            if (check2.isChecked())
                                dbRef.child(userid).child(word).child("meaning2").setValue("");
                            if (check3.isChecked())
                                dbRef.child(userid).child(word).child("meaning3").setValue("");
                            if (check4.isChecked())
                                dbRef.child(userid).child(word).child("meaning4").setValue("");
                            if (check5.isChecked())
                                dbRef.child(userid).child(word).child("meaning5").setValue("");
                            Toast.makeText(Activity_MyNote_2.this, "단어 수정 완료", Toast.LENGTH_SHORT).show();
                            editDialog.dismiss();
                            mean_c();
                            update();
                        }
                    }
                });

                editDialog.show();
            }
        });

        bookmark.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if ( bookmark_yes_or_no.equals("0") ){
                    bookmark_yes_or_no="1";
                    dbRef.child(userid).child(word).child("bookmark").setValue(1);
                    bookmark.setImageResource(R.drawable.ic_bookmark);
                    Toast.makeText(getApplicationContext(),"북마크가 설정되었습니다.",Toast.LENGTH_SHORT).show();
                }
                else if( bookmark_yes_or_no.equals("1") ) {
                    bookmark_yes_or_no="0";
                    dbRef.child(userid).child(word).child("bookmark").setValue(0);
                    bookmark.setImageResource(R.drawable.ic_unbookmark);
                    Toast.makeText(getApplicationContext(),"북마크가 해제되었습니다.",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    public void Back(View view) {
        finish();
    }

    @Override
    public void onResume() {
        update();
        super.onResume();
    }

    public void update() {
        dbRef.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("실행","update");
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    b=1;
                    if (fileSnapshot.child("word").getValue().toString().equals(word)) {
                        meaning1 = fileSnapshot.child("meaning1").getValue().toString();
                        if (fileSnapshot.child("meaning2").exists())
                            meaning2 = fileSnapshot.child("meaning2").getValue().toString();
                        if (fileSnapshot.child("meaning3").exists())
                            meaning3 = fileSnapshot.child("meaning3").getValue().toString();
                        if (fileSnapshot.child("meaning4").exists())
                            meaning4 = fileSnapshot.child("meaning4").getValue().toString();
                        if (fileSnapshot.child("meaning5").exists())
                            meaning5 = fileSnapshot.child("meaning5").getValue().toString();
                        String trying = fileSnapshot.child("try").getValue().toString();
                        String success = fileSnapshot.child("success").getValue().toString();
                        bookmark_yes_or_no = fileSnapshot.child("bookmark").getValue().toString();
                        if (bookmark_yes_or_no.equals("1"))
                            bookmark.setImageResource(R.drawable.ic_bookmark);
                        sentence.setText(fileSnapshot.child("sentence").getValue().toString());
                        pronounce.setText(fileSnapshot.child("pronounce").getValue().toString());
                        sentence_mean.setText(fileSnapshot.child("sentence_mean").getValue().toString());

                        String meaning = "";
                        if (!meaning1.equals("")) {
                            meaning += b+". " + meaning1+"\n";
                            b+=1;
                        }
                        if (!meaning2.equals("")) {
                            meaning += b+". " + meaning2+"\n";
                            b+=1;
                        }
                        if (!meaning3.equals("")) {
                            meaning += b+". " + meaning3+"\n";
                            b+=1;
                        }
                        if (!meaning4.equals("")) {
                            meaning += b+". " + meaning4+"\n";
                            b += 1;
                        }
                        if (!meaning5.equals("")) {
                            meaning += b+". " + meaning5+"\n";
                            b += 1;
                        }
                        mean.setText(meaning);

                        float percent=0.0f;
                        if (!trying.equals("0"))
                            percent = Float.parseFloat(success)/Float.parseFloat(trying)*100;
                        //percent = Math.round(Integer.parseInt(success)/Integer.parseInt(trying));
                        Log.i("암기",""+percent);
                        info.setText("최근 암기 여부\n"+trying+"번 시도 중 "+success+"번 성공\n암기율 "+String.format("%2f",percent)+"%");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void mean_c() {
        // 단어 끌어올리기
        m.clear();
        dbRef.child(userid).child(word).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("meaning1").exists()&&!dataSnapshot.child("meaning1").getValue().toString().equals(""))  {
                    m.add(dataSnapshot.child("meaning1").getValue().toString());
                    dbRef.child(userid).child(word).child("meaning1").setValue("");
                }
                if (dataSnapshot.child("meaning2").exists()&&!dataSnapshot.child("meaning2").getValue().toString().equals("")) {
                    m.add(dataSnapshot.child("meaning2").getValue().toString());
                    dbRef.child(userid).child(word).child("meaning2").setValue("");
                }
                if (dataSnapshot.child("meaning3").exists()&&!dataSnapshot.child("meaning3").getValue().toString().equals("")) {
                    m.add(dataSnapshot.child("meaning3").getValue().toString());
                    dbRef.child(userid).child(word).child("meaning3").setValue("");
                }
                if (dataSnapshot.child("meaning4").exists()&&!dataSnapshot.child("meaning4").getValue().toString().equals("")) {
                    m.add(dataSnapshot.child("meaning4").getValue().toString());
                    dbRef.child(userid).child(word).child("meaning4").setValue("");
                }
                if (dataSnapshot.child("meaning5").exists()&&!dataSnapshot.child("meaning5").getValue().toString().equals("")) {
                    m.add(dataSnapshot.child("meaning5").getValue().toString());
                    dbRef.child(userid).child(word).child("meaning5").setValue("");
                }

                for (int i=0;i<m.size();i++) {
                    dbRef.child(userid).child(word).child("meaning"+(i+1)).setValue(m.get(i));
                }

                if (m.size()==0) {
                    dbRef.child(userid).child(word).removeValue();
                    Toast.makeText(Activity_MyNote_2.this, "모든 뜻을 삭제했으므로 단어가 삭제됩니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}