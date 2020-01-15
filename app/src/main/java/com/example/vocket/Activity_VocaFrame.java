package com.example.vocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class Activity_VocaFrame extends AppCompatActivity {
    private DatabaseReference dbRef;
    private static String TAG = "phpquerytest";
    private static final String TAG_JSON="webnautes";
    Date d = new Date(System.currentTimeMillis());
    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
    String today = sf.format(d);

    ArrayList<HashMap<String, String>> mArrayList = new ArrayList<>();
    String mJsonString;  // 수신받은 메시지(JSON형식)
    ArrayList<Integer> pos=new ArrayList<>();  // 선택된 단어의 position
    TextView vocaCount, vocaName, select_num, add;
    ListAdapter adapter;


    ListView vocaListView;
    String exam, category;
    CheckBox checkBox;
    String[][] save_voca;  // 단어 정보 저장하는 2차원 배열
    int total;  // 총 단어 갯수

    Intent pIntent;
    ArrayList<Boolean> listState = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__voca_frame);
        Intent intent = getIntent();
        exam = intent.getExtras().getString("exam");
        category = intent.getExtras().getString("category");
        select_num=(TextView)findViewById(R.id.select_num);
        dbRef = FirebaseDatabase.getInstance().getReference("mynote");
        checkBox = (CheckBox)findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // 체크되면 모두 보이도록 설정
                if (checkBox.isChecked()) {
                    pos.clear();
                    vocaListView.setBackgroundColor(Color.parseColor("#63b6e6"));

                    for (int i=0;i<total;i++) {
                        pos.add(i);
                    }
                    select_num.setText("총 " + pos.size() + "단어 선택     ");
                }
                if (!checkBox.isChecked()) {
                    pos.clear();vocaListView.setBackgroundColor(Color.WHITE);
                    select_num.setText("총 0단어 선택     ");
                }
            }
        });
        add=(TextView)findViewById(R.id.add);
        vocaListView = (ListView)findViewById(R.id.vocaListView);
        vocaListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        vocaCount = (TextView)findViewById(R.id.vocaCount);
        vocaName = (TextView)findViewById(R.id.vocaName);

        GetData task = new GetData();
        task.execute(exam,category);
        //vocaListView.

        vocaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("position check","position : "+position+" id : "+id+" pos="+pos);

                if (!pos.contains(position)) {
                    pos.add(position);
                    //view.setBackgroundColor(Color.parseColor("#63b6e6"));
                    //parent.getChildAt(position).setBackgroundColor(Color.parseColor("#63b6e6"));
                    adapter.getView(position,view,parent).setBackgroundColor(Color.parseColor("#63b6e6"));
                    //listState.set(position,true);
                }
                else if (pos.contains(position)) {
                    pos.remove((Integer) position);
                    //parent.getChildAt(position).setBackgroundColor(Color.WHITE);
                    adapter.getView(position,view,parent).setBackgroundColor(Color.WHITE);
                    //listState.set(position,false);
                    //view.setBackgroundColor(Color.WHITE);
                }
                select_num.setText("총 "+pos.size()+"단어 선택     ");
            }
        });

        pIntent = getIntent();
    }

    // 비동기 통신 구현
    private class GetData extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "response - " + result);
            if (result != null) {
                mJsonString = result;      // 서버로부터 받은 메시지(JSON)
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String searchKeyword1 = params[0];
            String searchKeyword2 = params[1];
            String serverURL = "http://13.209.72.30/rlqhseksdj.php";
            String postParameters = "exam=" + searchKeyword1 + "&category=" + searchKeyword2;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }  // 비동기 통신규현 class 종료

    private void showResult() {
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            save_voca=new String[jsonArray.length()][7];
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);
                String word = item.getString("word");  // 단어
                String pronounce = item.getString("pronounce");  // 발음기호
                String meaning1 = item.getString("meaning1");
                String meaning2 = item.getString("meaning2");
                String meaning3 = item.getString("meaning3");
                String sentence = item.getString("sentence");  // 예시 문장
                String sentence_mean = item.getString("sentence_mean");  // 문장 뜻
                total=jsonArray.length();

                save_voca[i][0]=word;
                save_voca[i][1]=pronounce;
                save_voca[i][2]=meaning1;
                save_voca[i][3]=meaning2;
                save_voca[i][4]=meaning3;
                save_voca[i][5]=sentence;
                save_voca[i][6]=sentence_mean;

                String meaning = "1. " + meaning1;  // 리스트 표시용
                if (!item.getString("meaning2").equals(""))
                    meaning += "   2. " + meaning2;
                if (!item.getString("meaning3").equals(""))
                    meaning += "\n3. " + meaning3;
                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put("word", word);
                hashMap.put("meaning", meaning);
                vocaCount.setText("총 " + total + "개");
                vocaName.setText(category);

                mArrayList.add(hashMap);
            }

            adapter = new SimpleAdapter(Activity_VocaFrame.this, mArrayList, R.layout.item_voca, new String[]{"word", "meaning"}, new int[]{
                    R.id.voca_en, R.id.voca_mean
            }){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    final View view = super.getView(position, convertView, parent);
                    final Context context = parent.getContext();

                    final int fpos = position;



                    /*view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Log.d("id","----> "+view.getId());
                            if(listState.get(fpos)) {
                                listState.set(fpos, false);
                                view.setBackgroundColor(Color.WHITE);
                            }
                            else {
                                listState.set(fpos,true);
                                //view.setBackgroundColor(Color.WHITE);
                                view.setBackgroundColor(Color.parseColor("#63b6e6"));
                            }
                        }
                    });*/


                    return view;
                }
            };

            for(int i =0;i<total;i++)   // background
                listState.add(false);

            vocaListView.setAdapter(adapter);
            //vocaListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
           // vocaListView.setItemsCanFocus(false);

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
    public void AddMyNote(View view) {
        if (pos.size()>0) {
            for (int i:pos) {
                String userid = getIntent().getStringExtra("uid");

                dbRef.child(userid).child(save_voca[i][0]).child("word").setValue(save_voca[i][0]);
                dbRef.child(userid).child(save_voca[i][0]).child("pronounce").setValue(save_voca[i][1]);
                dbRef.child(userid).child(save_voca[i][0]).child("meaning1").setValue(save_voca[i][2]);
                dbRef.child(userid).child(save_voca[i][0]).child("meaning2").setValue(save_voca[i][3]);
                dbRef.child(userid).child(save_voca[i][0]).child("meaning3").setValue(save_voca[i][4]);
                dbRef.child(userid).child(save_voca[i][0]).child("sentence").setValue(save_voca[i][5]);
                dbRef.child(userid).child(save_voca[i][0]).child("sentence_mean").setValue(save_voca[i][6]);
                dbRef.child(userid).child(save_voca[i][0]).child("bookmark").setValue(0);
                dbRef.child(userid).child(save_voca[i][0]).child("try").setValue(0);
                dbRef.child(userid).child(save_voca[i][0]).child("success").setValue(0);
                dbRef.child(userid).child(save_voca[i][0]).child("understand").setValue(0);
                dbRef.child(userid).child(save_voca[i][0]).child("date").setValue(Integer.parseInt(today));
                dbRef.child(userid).child(save_voca[i][0]).child("savetime").setValue((int)((System.currentTimeMillis())));
            }
            Toast.makeText(getApplicationContext(),"성공적으로 저장되었습니다.",Toast.LENGTH_SHORT).show();
            finish();
        }
        else
            Toast.makeText(getApplicationContext(),"단어를 선택하세요",Toast.LENGTH_SHORT).show();
    }

    public void Back(View view) {
        finish();
    }
}
