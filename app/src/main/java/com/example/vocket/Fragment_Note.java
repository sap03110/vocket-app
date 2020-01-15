package com.example.vocket;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Fragment_Note extends Fragment {
    ImageView toeic,toefl,teps,sat,publicc,trans;
    Button button1, button2, button3, button4;
    int b1, b2, b3, b4;
    View view;
    private DatabaseReference dbRef;

    Intent pIntent;
    Intent gotoMy;
    Intent gotoBasic;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbRef = FirebaseDatabase.getInstance().getReference("mynote");
        view = inflater.inflate(R.layout.fragment_note, container, false);
        button1 = (Button)view.findViewById(R.id.button1);
        button2 = (Button)view.findViewById(R.id.button2);
        button3 = (Button)view.findViewById(R.id.button3);
        button4 = (Button)view.findViewById(R.id.button4);

        pIntent = getActivity().getIntent();

        gotoMy = new Intent(view.getContext(), Activity_MyNote.class);
        gotoMy.putExtra("uid",pIntent.getStringExtra("uid"));
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMy.putExtra("mode",1);
                startActivity(gotoMy);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMy.putExtra("mode",2);
                startActivity(gotoMy);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMy.putExtra("mode",3);
                startActivity(gotoMy);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoMy.putExtra("mode",4);
                startActivity(gotoMy);
            }
        });

        toeic = (ImageView)view.findViewById(R.id.bt_toeic);
        toefl = (ImageView)view.findViewById(R.id.bt_toefl);
        teps = (ImageView)view.findViewById(R.id.bt_teps);
        sat = (ImageView)view.findViewById(R.id.bt_sat);
        publicc = (ImageView)view.findViewById(R.id.bt_publicc);
        trans = (ImageView)view.findViewById(R.id.bt_trans);

        gotoBasic = new Intent(view.getContext(), Activity_BasicNote.class);
        gotoBasic.putExtra("uid",getActivity().getIntent().getStringExtra("uid"));
        toeic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBasic.putExtra("type","toeic");
                startActivity(gotoBasic);
            }
        });
        toefl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBasic.putExtra("type","toefl");
                startActivity(gotoBasic);
            }
        });
        teps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBasic.putExtra("type","teps");
                startActivity(gotoBasic);
            }
        });
        sat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBasic.putExtra("type","sat");
                startActivity(gotoBasic);
            }
        });
        publicc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBasic.putExtra("type","publicc");
                startActivity(gotoBasic);
            }
        });
        trans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoBasic.putExtra("type","trans");
                startActivity(gotoBasic);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        dbRef.child(pIntent.getStringExtra("uid")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                b1=0;
                b2=0;
                b3=0;
                b4=0;
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String word = fileSnapshot.child("word").getValue().toString();
                    b1++;
                    String understand = fileSnapshot.child("understand").getValue().toString();
                    String trying = fileSnapshot.child("try").getValue().toString();
                    String bookmark = fileSnapshot.child("bookmark").getValue().toString();
                    if (bookmark.equals("1"))
                        b2++;
                    if (understand.equals("0")&&!trying.equals("0"))
                        b3++;
                    String success = fileSnapshot.child("success").getValue().toString();
                    if (!trying.equals("0")&&(Integer.parseInt(success)/Integer.parseInt(trying)<=0.5f))
                        b4++;
                }
                button1.setText(" 내가 추가한 단어 모두 보기 (총 "+b1+"개)                     >");
                button2.setText(" 내가 북마크한 단어 모두 보기 (총 "+b2+"개)                     >");
                button3.setText(" 최근 틀린 단어 보기 (총 "+b3+"개)                                 >");
                button4.setText(" 자주 틀리는 단어 보기 (총 "+b4+"개)                              >");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        super.onResume();
    }
}
