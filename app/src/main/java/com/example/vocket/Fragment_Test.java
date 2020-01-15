package com.example.vocket;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.facebook.FacebookSdk.getApplicationContext;

public class Fragment_Test extends Fragment {
    View view;
    ImageView check1, check2, check3, check4;
    TextView tTotal,tCor,tIncor;
    LinearLayout mode1, mode2, mode3, mode4;
    int testmode=1;
    TextView button;  // 테스트 액티비티로 이동
    private DatabaseReference dbRef;
    int today = 0;
    int today_cor = 0;
    int today_incor = 0;
    Date d = new Date(System.currentTimeMillis());
    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
    String t = sf.format(d);

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        view = inflater.inflate(R.layout.fragment_test, container, false);
        button = (TextView)view.findViewById(R.id.button);
        check1 = (ImageView)view.findViewById(R.id.check1);
        check2 = (ImageView)view.findViewById(R.id.check2);
        check3 = (ImageView)view.findViewById(R.id.check3);
        check4 = (ImageView)view.findViewById(R.id.check4);
        mode1 = (LinearLayout) view.findViewById(R.id.mode1);
        mode2 = (LinearLayout)view.findViewById(R.id.mode2);
        mode3 = (LinearLayout)view.findViewById(R.id.mode3);
        mode4 = (LinearLayout)view.findViewById(R.id.mode4);
        tTotal = (TextView)view.findViewById(R.id.test_total);
        tCor = (TextView)view.findViewById(R.id.test_cor);
        tIncor = (TextView)view.findViewById(R.id.test_incor);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View dlgView = View.inflate(view.getContext(),R.layout.dialog_vocanum,null);
                final Dialog vcnumDialog = new Dialog(view.getContext());
                vcnumDialog.setContentView(dlgView);
                Button ok;
                final EditText edit;
                ok = (Button)dlgView.findViewById(R.id.ok_bt);
                edit = (EditText)dlgView.findViewById(R.id.editText);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent gotoTest = new Intent(view.getContext(), Activity_Test.class);
                        gotoTest.putExtra("mode", testmode);
                        gotoTest.putExtra("vcnum", Integer.parseInt(edit.getText().toString()));
                        gotoTest.putExtra("uid",getActivity().getIntent().getStringExtra("uid"));
                        startActivity(gotoTest);
                        vcnumDialog.cancel();
                    }
                });
                vcnumDialog.show();
            }
        });

        mode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testmode=1;
                mode1.setBackgroundColor(0xff1187cf);
                mode2.setBackgroundColor(0xaa1187cf);
                mode3.setBackgroundColor(0xaa1187cf);
                mode4.setBackgroundColor(0xaa1187cf);
                check1.setVisibility(View.VISIBLE);
                check2.setVisibility(View.INVISIBLE);
                check3.setVisibility(View.INVISIBLE);
                check4.setVisibility(View.INVISIBLE);
            }
        });

        mode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testmode=2;
                mode1.setBackgroundColor(0xaa1187cf);
                mode2.setBackgroundColor(0xff1187cf);
                mode3.setBackgroundColor(0xaa1187cf);
                mode4.setBackgroundColor(0xaa1187cf);
                check1.setVisibility(View.INVISIBLE);
                check2.setVisibility(View.VISIBLE);
                check3.setVisibility(View.INVISIBLE);
                check4.setVisibility(View.INVISIBLE);
            }
        });

        mode3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testmode=3;
                mode1.setBackgroundColor(0xaa1187cf);
                mode2.setBackgroundColor(0xaa1187cf);
                mode3.setBackgroundColor(0xff1187cf);
                mode4.setBackgroundColor(0xaa1187cf);
                check1.setVisibility(View.INVISIBLE);
                check2.setVisibility(View.INVISIBLE);
                check3.setVisibility(View.VISIBLE);
                check4.setVisibility(View.INVISIBLE);
            }
        });

        mode4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testmode=4;
                mode1.setBackgroundColor(0xaa1187cf);
                mode2.setBackgroundColor(0xaa1187cf);
                mode3.setBackgroundColor(0xaa1187cf);
                mode4.setBackgroundColor(0xff1187cf);
                check1.setVisibility(View.INVISIBLE);
                check2.setVisibility(View.INVISIBLE);
                check3.setVisibility(View.INVISIBLE);
                check4.setVisibility(View.VISIBLE);
            }
        });


        dbRef.child(getActivity().getIntent().getStringExtra("uid")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("weekly").child(t).exists()) {
                    today = Integer.parseInt(dataSnapshot.child("weekly").child(t).child("today").getValue().toString());
                    today_cor = Integer.parseInt(dataSnapshot.child("weekly").child(t).child("today_cor").getValue().toString());
                    today_incor = Integer.parseInt(dataSnapshot.child("weekly").child(t).child("today_incor").getValue().toString());
                }
                tTotal.setText("총 "+today+"개 단어 학습");
                tCor.setText("맞은 단어 "+today_cor+"개");
                tIncor.setText("틀린 단어 "+today_incor+"개");
            }

            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        dbRef.child(getActivity().getIntent().getStringExtra("uid")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int today, today_cor ,today_incor;
                Date d = new Date(System.currentTimeMillis());
                SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
                TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
                sf.setTimeZone(tz);
                String date = sf.format(d);

                try {
                    today = Integer.parseInt(dataSnapshot.child("weekly").child(date).child("today").getValue().toString());
                    today_cor = Integer.parseInt(dataSnapshot.child("weekly").child(date).child("today_cor").getValue().toString());
                    today_incor = Integer.parseInt(dataSnapshot.child("weekly").child(date).child("today_incor").getValue().toString());
                }catch (Exception e){
                    dbRef.child(getActivity().getIntent().getStringExtra("uid")).child("weekly").child(date).child("today").setValue(0);
                    dbRef.child(getActivity().getIntent().getStringExtra("uid")).child("weekly").child(date).child("today_cor").setValue(0);
                    dbRef.child(getActivity().getIntent().getStringExtra("uid")).child("weekly").child(date).child("today_incor").setValue(0);
                    today = 0;
                    today_cor = 0;
                    today_incor = 0;
                }

                tTotal.setText("총 "+today+"개 단어 학습");
                tCor.setText("맞은 단어 "+today_cor+"개");
                tIncor.setText("틀린 단어 "+today_incor+"개");
            }

            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}