package com.example.vocket;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Activity_OcrAdd extends AppCompatActivity {
    private DatabaseReference dbRef;
    Date d = new Date(System.currentTimeMillis());
    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
    String today = sf.format(d);

    Elements mean;  // 크롤링한 단어 덩어리
    Document doc = null;
    ArrayList<String> meaning;
    String s;  // 단어 갯수 파악
    int max = 0;
    String voca, pronounce, sentence, sentence_mean;  // M은 텍스트뷰 결과 확인용
    TextView addbtn;
    ImageView ocr_result, back;
    String getUrl= "http://165.194.17.140:8000/test";  // 결과파일
    String imgUrl = "http://165.194.17.140:8000/";  // 이미지
    back task;
    Bitmap bmImg;
    String[][] save_voca;  // 파싱 결과
    String check;
    int length;
    String cc;

    int b;
    String userid;  // 이 부분만 인텐트값 넣어요

    ListView listview;
    ViewAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_add_hori);
        dbRef = FirebaseDatabase.getInstance().getReference("mynote");

        addbtn = (TextView) findViewById(R.id.addbtn);
        listview = (ListView) findViewById(R.id.listview);
        mMyAdapter = new ViewAdapter();
        back = (ImageView) findViewById(R.id.back);
        listview.setAdapter(mMyAdapter);
        ocr_result = (ImageView) findViewById(R.id.ocr_result);
        task = new back();
        task.execute(imgUrl);
        userid = getIntent().getStringExtra("uid");
        NetworkTask networkTask = new NetworkTask(getUrl, null);
        networkTask.execute();

        ocr_result.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    for(int i=0; i<length; i++) {
                        if((x>=Integer.parseInt(save_voca[i][1]))&&(x<=Integer.parseInt(save_voca[i][2])))
                            if ((y>=Integer.parseInt(save_voca[i][3]))&&(y<=Integer.parseInt(save_voca[i][4]))) {
                                s="";
                                voca=save_voca[i][0];
                                meaning = new ArrayList<String>(Arrays.asList("","","","",""));

                                new AsyncTask() {
                                    @Override
                                    protected Object doInBackground(Object[] params) {
                                        try {
                                            doc = Jsoup.connect("https://alldic.daum.net/search.do?q="+voca+"&dic=eng&search_first=Y").get();
                                            mean = doc.select("span.txt_search");
                                            s = doc.select("span.num_search").text();
                                        } catch (Exception e) {e.printStackTrace(); }
                                        try {
                                            String[] num = s.substring(0, s.length() - 1).replaceAll(". ", ",").split(",");
                                            for (int i = 0; i < num.length; i++) {
                                                if (Integer.parseInt(num[i]) >= max)
                                                    max = Integer.parseInt(num[i]);
                                                else
                                                    break;
                                                //if (max>4) max=4;
                                            }
                                        }  catch (Exception e) {}

                                        int count=0;
                                        for (Element e : mean) {
                                            count++;
                                            meaning.set(count-1,e.text().trim());
                                            if (count==max)
                                                break;
                                        }
                                        return null;
                                    }
                                    @Override
                                    protected void onPostExecute(Object o) {
                                        please(meaning);
                                        super.onPostExecute(o);
                                    }
                                }.execute();
                            }
                    }
                }
                return false;
            }
        });
    }

    private class back extends AsyncTask<String, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmImg;
        }
        protected void onPostExecute(Bitmap img) {
            ocr_result.setImageBitmap(bmImg);
        }
    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(Activity_OcrAdd.this, "로딩 완료", Toast.LENGTH_SHORT).show();
            jsonParsing(s);
        }
    }

    public void jsonParsing(String r) {
        try{
            JSONArray movieArray =  new JSONArray(r);
            length=movieArray.length();
            save_voca=new String[movieArray.length()][5];
            for(int i=0; i<movieArray.length(); i++)
            {
                JSONObject vocaObject = movieArray.getJSONObject(i);
                String voca = vocaObject.getString("result_text");
                String x1 = vocaObject.getString("x1");
                String x2 = vocaObject.getString("x3");
                String y1 = vocaObject.getString("y1");
                String y2 = vocaObject.getString("y3");
                save_voca[i][0]=voca;
                save_voca[i][1]=x1;
                save_voca[i][2]=x2;
                save_voca[i][3]=y1;
                save_voca[i][4]=y2;
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void please(final ArrayList<String> glgl) {
        final View dlgView = View.inflate(Activity_OcrAdd.this,R.layout.dialog_addvoca,null);
        final Dialog vcnumDialog = new Dialog(Activity_OcrAdd.this);
        vcnumDialog.setContentView(dlgView);

        TextView ok, word, mean1, mean2, mean3, mean4, mean5;
        final CheckBox check1, check2, check3, check4, check5;
        ok = (TextView) dlgView.findViewById(R.id.ok_bt);
        word = (TextView) dlgView.findViewById(R.id.words);
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
        word.setText(voca);
        mean1.setText(glgl.get(0));
        mean2.setText(glgl.get(1));
        mean3.setText(glgl.get(2));
        mean4.setText(glgl.get(3));
        mean5.setText(glgl.get(4));
        if (glgl.get(1).equals("")) check2.setVisibility(View.INVISIBLE);
        if (glgl.get(2).equals("")) check3.setVisibility(View.INVISIBLE);
        if (glgl.get(3).equals("")) check4.setVisibility(View.INVISIBLE);
        if (glgl.get(4).equals("")) check5.setVisibility(View.INVISIBLE);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check="";
                int m=0;

                for (int a=0;a<mMyAdapter.getCount();a++) {
                    if (voca.equals(mMyAdapter.getItem(a).getId()))
                        m=1;
                }

                if (!check1.isChecked()&&!check2.isChecked()&&!check3.isChecked()&&!check4.isChecked()&&!check5.isChecked())
                    Toast.makeText(Activity_OcrAdd.this, "추가할 뜻을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                else if (m==1)
                    Toast.makeText(Activity_OcrAdd.this, "선택한 단어는 이미 리스트에 추가한 단어입니다.", Toast.LENGTH_SHORT).show();
                else {
                    String item="";
                    if (check1.isChecked()) {
                        item=glgl.get(0);
                        check+="1";
                    }
                    else check+="0";
                    if (check2.isChecked()) {
                        item=glgl.get(1);
                        check+="1";
                    }
                    else check+="0";
                    if (check3.isChecked()) {
                        item=glgl.get(2);
                        check+="1";
                    }
                    else check+="0";
                    if (check4.isChecked()) {
                        item=glgl.get(3);
                        check+="1";
                    }
                    else check+="0";
                    if (check5.isChecked()) {
                        item=glgl.get(4);
                        check+="1";
                    }
                    else check+="0";

                    mMyAdapter.addItem(voca, item, check);
                    vcnumDialog.cancel();
                }
                mMyAdapter.notifyDataSetChanged();
            }
        });

        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(listview, new SwipeDismissListViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(int position) {
                return true;
            }

            @Override
            public void onDismiss(final ListView listView, int[] reverseSortedPositions) {
                for (final int position : reverseSortedPositions) {
                    mMyAdapter.removeItem(position);
                }
                mMyAdapter.notifyDataSetChanged();
            }
        });
        listview.setOnTouchListener(touchListener);
        listview.setOnScrollListener(touchListener.makeScrollListener());

        if (!glgl.get(0).equals("")) vcnumDialog.show();  // 사전에 있는 단어만 검색
    }

    public void AddVoca(View view) {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                try {

                    for (int i=0;i<mMyAdapter.getCount();i++) {
                        meaning = new ArrayList<String>(Arrays.asList("", "", "", "", ""));
                        voca = mMyAdapter.getItem(i).getId();
                        cc = mMyAdapter.getItem(i).getCheck();
                        pronounce = "";
                        sentence = "";
                        sentence_mean = "";

                        doc = Jsoup.connect("https://alldic.daum.net/search.do?q=" + voca + "&dic=eng&search_first=Y").get();
                        mean = doc.select("span.txt_search");
                        pronounce = doc.selectFirst("span.txt_pronounce").text();
                        sentence = doc.selectFirst("span.txt_ex").text();
                        sentence_mean = doc.selectFirst("span.mean_example").text();

                        int count=0;
                        for (Element e : mean) {
                            count++;
                            meaning.set(count-1,e.text().trim());
                            if (count==max)
                                break;
                        }

                        b = 1;
                        dbRef.child(userid).child(voca).child("meaning1").setValue("");
                        dbRef.child(userid).child(voca).child("meaning2").setValue("");
                        dbRef.child(userid).child(voca).child("meaning3").setValue("");
                        dbRef.child(userid).child(voca).child("meaning4").setValue("");
                        dbRef.child(userid).child(voca).child("meaning5").setValue("");
                        for (int a = 0; a < 5; a++) {
                            if (cc.substring(a, a + 1).equals("1")) {
                                dbRef.child(userid).child(voca).child("meaning" + b).setValue(meaning.get(a));
                                b++;
                            }
                        }
                        dbRef.child(userid).child(voca).child("word").setValue(voca);
                        dbRef.child(userid).child(voca).child("pronounce").setValue(pronounce);
                        dbRef.child(userid).child(voca).child("sentence").setValue(sentence);
                        dbRef.child(userid).child(voca).child("sentence_mean").setValue(sentence_mean);
                        dbRef.child(userid).child(voca).child("bookmark").setValue(0);
                        dbRef.child(userid).child(voca).child("try").setValue(0);
                        dbRef.child(userid).child(voca).child("success").setValue(0);
                        dbRef.child(userid).child(voca).child("understand").setValue(0);
                        dbRef.child(userid).child(voca).child("date").setValue(Integer.parseInt(today));
                        dbRef.child(userid).child(voca).child("savetime").setValue((int)((System.currentTimeMillis())));
                    }
                } catch (Exception e) {e.printStackTrace(); }
                return null;
            }
            @Override
            protected void onPostExecute(Object o) {
                Toast.makeText(Activity_OcrAdd.this, "저장 완료", Toast.LENGTH_SHORT).show();
                super.onPostExecute(o);
            }
        }.execute();
        finish();
    }
    public void Back(View view) {
        finish();
    }
}