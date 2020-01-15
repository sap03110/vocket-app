package com.example.vocket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Fragment_Pvp extends Fragment {
    TextView btn, mypvpstate, mypvprating;
    TextView date, content;
    ImageView my_tier;
    static ProgressDialog progressDialog;

    private DatabaseReference dbRef, userRef;
    View view;
    Intent pIntent;
    private int myRating = 0, pvp_win, pvp_lose, pvp_total;
    String ratingKey;
    String userid;

    //String userid="LlqbrbJkzJMsLwvLpXyW1Sl2Ugf1"; //나
    //String userid="frameuser";
    public long roomno;
    int t=0; // 자꾸 실행 안되게
    int remain_r;


    PieChart pieChart;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbRef = FirebaseDatabase.getInstance().getReference("test_pvp");
        userRef = FirebaseDatabase.getInstance().getReference("users");
        view = inflater.inflate(R.layout.fragment_pvp, container, false);
        progressDialog = new ProgressDialog(view.getContext());
        pIntent = getActivity().getIntent();
        date = (TextView)view.findViewById(R.id.date);
        content = (TextView)view.findViewById(R.id.content);
        mypvprating = (TextView)view.findViewById(R.id.mypvprating);
        mypvpstate = (TextView)view.findViewById(R.id.mypvpstate);
        my_tier = (ImageView)view.findViewById(R.id.my_tier);
        userid=getActivity().getIntent().getStringExtra("uid"); // 이게 원래 키


        pieChart = (PieChart) view.findViewById(R.id.piechart2);

        btn=(TextView) view.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("매칭대상을 찾고 있습니다...");
                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        t=0;
                        dbRef.child("room").child(ratingKey).child("room"+(roomno-1)).removeValue();
                        Toast.makeText(view.getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                    }
                });
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
                progressDialog.show();

                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        roomno = dataSnapshot.child("room").child(ratingKey).getChildrenCount();
                        if (t==0) {
                            boolean room_exists=false;
                            for (int i = 0; i < roomno; i++) {
                                if (dataSnapshot.child("room").child(ratingKey).child("room" + i).child("user2").getValue().toString().equals("")) { // 누가 방을 만들었을 때
                                    dbRef.child("room").child(ratingKey).child("room" + i).child("user2").setValue(userid);
                                    room_exists = true;
                                }
                            }
                            if (room_exists == false) {
                                dbRef.child("room").child(ratingKey).child("room" + roomno).child("user1").setValue(userid);
                                dbRef.child("room").child(ratingKey).child("room" + roomno).child("user2").setValue("");
                                roomno = roomno + 1;
                            }
                            t = 1;
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
                dbRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(t==1&&dataSnapshot.child(ratingKey).child("room"+(roomno-1)).child("user2").exists()) {
                            if (!dataSnapshot.child(ratingKey).child("room" + (roomno - 1)).child("user2").getValue().toString().equals("")) {
                                Toast.makeText(view.getContext(), "매칭 성공! 잠시 후에 이동합니다...", Toast.LENGTH_SHORT).show();
                                Intent gotoPVP = new Intent(view.getContext(), Activity_pvp.class);
                                if (userid.equals(dataSnapshot.child(ratingKey).child("room" + (roomno - 1)).child("user1").getValue().toString())) {
                                    dbRef.child("room").child(ratingKey).child("room" + (roomno - 1)).child("user1score").child("score").setValue(0);
                                    dbRef.child("room").child(ratingKey).child("room" + (roomno - 1)).child("user2score").child("score").setValue(0);
                                    gotoPVP.putExtra("userid", userid);
                                    gotoPVP.putExtra("ratingKey",ratingKey);
                                    gotoPVP.putExtra("roomno","room"+(roomno-1));
                                    gotoPVP.putExtra("userno","user1");
                                    gotoPVP.putExtra("yourno","user2");
                                    gotoPVP.putExtra("yourid",dataSnapshot.child(ratingKey).child("room" + (roomno - 1)).child("user2").getValue().toString());
                                    startActivity(gotoPVP);
                                    t=0;
                                }
                                else if (userid.equals(dataSnapshot.child(ratingKey).child("room" + (roomno - 1)).child("user2").getValue().toString())) {
                                    dbRef.child("room").child(ratingKey).child("room" + (roomno - 1)).child("user2score").child("score").setValue(0);
                                    dbRef.child("room").child(ratingKey).child("room" + (roomno - 1)).child("user1score").child("score").setValue(0);
                                    gotoPVP.putExtra("userid", userid);
                                    gotoPVP.putExtra("yourid",dataSnapshot.child(ratingKey).child("room" + (roomno - 1)).child("user1").getValue().toString());
                                    gotoPVP.putExtra("ratingKey",ratingKey);
                                    gotoPVP.putExtra("roomno","room"+(roomno-1));
                                    gotoPVP.putExtra("userno","user2");
                                    gotoPVP.putExtra("yourno","user1");
                                    try {
                                        Toast.makeText(view.getContext(), "매칭 성공! 잠시 후에 이동합니다...", Toast.LENGTH_SHORT).show();
                                        Thread.sleep(1000);
                                    }catch (Exception e) {}

                                    startActivity(gotoPVP);
                                    t=0;
                                }
                                progressDialog.dismiss();
                            }
                        }
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });

        return view;
    }
    @Override
    public void onResume() {
        t=0;
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pvp_total = Integer.parseInt(dataSnapshot.child(userid).child("pvp_total").getValue().toString());
                pvp_win = Integer.parseInt(dataSnapshot.child(userid).child("pvp_win").getValue().toString());
                pvp_lose = Integer.parseInt(dataSnapshot.child(userid).child("pvp_lose").getValue().toString());
                myRating = Integer.parseInt(dataSnapshot.child(userid).child("rating").getValue().toString());

                if (dataSnapshot.child(userid).child("PVP").exists()) {
                    date.setVisibility(View.VISIBLE);
                    date.setText(dataSnapshot.child(userid).child("PVP").child("date").getValue().toString());
                    content.setText(dataSnapshot.child(userid).child("PVP").child("me").getValue().toString() + "(나)    VS    " + dataSnapshot.child(userid).child("PVP").child("you").getValue().toString()+"(상대)");
                }

                if(myRating >=500 && myRating <1000) {
                    ratingKey = "r500to1000";
                    remain_r=1000-myRating;
                    my_tier.setImageResource(R.drawable.tier1);
                }
                else if(myRating >=1000 && myRating <1500) {
                    ratingKey = "r1000to1500";
                    remain_r=1500-myRating;
                    my_tier.setImageResource(R.drawable.tier2);
                }
                else if(myRating >=1500 && myRating <2000) {
                    ratingKey = "r1500to2000";
                    remain_r=2000-myRating;
                    my_tier.setImageResource(R.drawable.tier3);
                }
                else if (myRating>=2000 && myRating <2500) {
                    ratingKey = "r2000to2500";
                    remain_r=2500-myRating;
                    my_tier.setImageResource(R.drawable.tier4);
                }

                mypvprating.setText("Rating "+myRating+"\n(다음 티어까지 "+remain_r+" 남음)");
                mypvpstate.setText(pvp_total+"전 "+pvp_win+"승 "+pvp_lose+"패\n(승률 "+Math.round(((double)pvp_win/(pvp_total+0.001))*100)+"%)");


                pieChart.setUsePercentValues(true);
                ArrayList<Entry> yvalues = new ArrayList<Entry>();
                yvalues.add(new Entry((float)pvp_win, 0));
                yvalues.add(new Entry((float)pvp_lose, 1));
                final PieDataSet dataSet = new PieDataSet(yvalues, "");
                dataSet.setDrawValues(false);
                ArrayList<String> xVals = new ArrayList<String>();
                xVals.add("");
                xVals.add("");

                PieData data = new PieData(xVals, dataSet);
                data.setValueFormatter(new PercentFormatter());
                pieChart.setData(data);
                dataSet.setColors(ColorTemplate.LIBERTY_COLORS);pieChart.setHoleRadius(70f);
                pieChart.setHoleColorTransparent(true);
                pieChart.setCenterText(Math.round(((double)pvp_win/(pvp_total+0.001))*100)+"%");
                pieChart.setCenterTextSize(15f);
                pieChart.animateXY(1400, 1400);
                pieChart.setDescription("");
                pieChart.getLegend().setEnabled(false);
            }
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        super.onResume();
    }

    @Override
    public void onPause() {
        t=0;
        super.onPause();
    }

    @Override
    public void onStop() {
        t=0;
        super.onStop();
    }
}