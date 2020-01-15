package com.example.vocket;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.icu.util.RangeValueIterator;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Fragment_Home extends Fragment {

    BarChart barChart ;
    ArrayList<BarEntry> BARENTRY ;
    ArrayList<String> BarEntryLabels ;
    BarDataSet Bardataset ;
    BarData BARDATA ;
    Intent pIntent;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");

    Elements mean;  // 크롤링한 단어 덩어리
    Document doc = null;
    String word, pronounce, M;  // M은 텍스트뷰 결과 확인용
    int count;

    //items
    TextView ttoday,cumul,avgweek,rate,level,pan,rating;
    TextView today_voca, today_pronounce, today_mean;
    Button exp;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        today_voca = (TextView)view.findViewById(R.id.today_voca);
        today_pronounce = (TextView)view.findViewById(R.id.today_pronounce);
        today_mean = (TextView)view.findViewById(R.id.today_mean);

        pIntent = getActivity().getIntent();
        ttoday = (TextView)view.findViewById(R.id.today);
        cumul = (TextView)view.findViewById(R.id.cumul);
        avgweek = (TextView)view.findViewById(R.id.avgweek);
        rate = (TextView)view.findViewById(R.id.rate);
        level = (TextView)view.findViewById(R.id.tlevel);
        pan = (TextView)view.findViewById(R.id.pan);
        rating = (TextView)view.findViewById(R.id.rating);
        exp = (Button)view.findViewById(R.id.exp);

        barChart = (BarChart) view.findViewById(R.id.barchart);

        updateDBUI();
        return view;
    }

    @Override
    public void onResume() {   // 다른창 다녀왔을때 갱신
        word="";
        pronounce="";
        M="";
        count=0;

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    doc = Jsoup.connect("https://endic.naver.com/?sLn=kr").get();
                    word = doc.selectFirst("a.word_link").text();  // 서버에서 받아온 단어 넣어주면 될 듯
                    doc = Jsoup.connect("https://alldic.daum.net/search.do?q="+word+"&dic=eng&search_first=Y").get();
                    Log.i("ff",word);
                    mean = doc.select("span.txt_search");
                    pronounce = doc.selectFirst("span.txt_pronounce").text();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (Element e : mean) {
                    count++;
                    M += count + ". "+ e.text().trim() + "\n";
                    if (count==3) break;
                }
                return null;
            }
            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                today_voca.setText(word);
                today_pronounce.setText(pronounce);
                today_mean.setText(M);
            }
        }.execute();

        updateDBUI();
        super.onResume();
    }

    void updateDBUI(){
        // DB & UI 정보 갱신.
        dbRef.child(pIntent.getStringExtra("uid")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Date d = new Date(System.currentTimeMillis());
                SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
                TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
                sf.setTimeZone(tz);
                String date = sf.format(d);

                int today , today_cor, today_incor;
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
                }

                dbRef.child(pIntent.getStringExtra("uid")).child("recentlogin").setValue(today);;
                cumul.setText(dataSnapshot.child("cumul").getValue().toString()+"개");


                long minus = 86400000;
                String[] recent7day = new String[7];
                float[] bardaydata = new float[7];
                for(int i=0;i<7;i++) {
                    Date td = new Date(System.currentTimeMillis()-minus*i);
                    sf.setTimeZone(tz);
                    recent7day[i] = sf.format(td);
                }

                int weekcumul =0, count =0;
                for(int i=0;i<7;i++){
                    try{
                        bardaydata[i] = (float)Integer.parseInt(dataSnapshot.child("weekly").child(recent7day[i]).child("today").getValue().toString());
                        weekcumul += bardaydata[i];
                        count++;
                    }
                    catch (Exception e){
                        bardaydata[i] = 0;
                        count++;
                    }
                }

                dbRef.child(getActivity().getIntent().getStringExtra("uid")).child("avgweek").setValue(weekcumul/count);
                avgweek.setText(dataSnapshot.child("avgweek").getValue().toString()+"개");
                ttoday.setText((int)bardaydata[0]+"개");
                if(today != 0)
                    rate.setText(Math.round(((float)today_cor/today)*100)+"%");
                else
                    rate.setText("None");
                level.setText("level 1");
                pan.setText(dataSnapshot.child("pvp_total").getValue().toString()+"전 "+dataSnapshot.child("pvp_win").getValue().toString()+"승 "+dataSnapshot.child("pvp_lose").getValue().toString()+"패");
                rating.setText("Rating "+dataSnapshot.child("rating").getValue().toString());


                // ------------------- Bar Chart -------------------------------
                BarEntryLabels = new ArrayList<String>();
                BARENTRY = new ArrayList<>();
                for(int i=0;i<7;i++) {
                    BARENTRY.add(new BarEntry(bardaydata[6 - i], i));
                    BarEntryLabels.add("");
                }

                Bardataset = new BarDataSet(BARENTRY, "내 학습 정보");
                Bardataset.setColors(ColorTemplate.LIBERTY_COLORS);
                Bardataset.setDrawValues(false);

                BARDATA = new BarData(BarEntryLabels,Bardataset);

                barChart.setData(BARDATA);
                barChart.setTouchEnabled(false);
                barChart.setBackgroundColor(Color.rgb(236,244,249));
                barChart.setDrawGridBackground(false);
                barChart.setDescription("");

                barChart.animateY(1000);
                barChart.getXAxis().setDrawAxisLine(false);
                barChart.getBarData().setGroupSpace(100);
            }
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}
