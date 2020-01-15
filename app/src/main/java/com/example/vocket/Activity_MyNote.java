package com.example.vocket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class Activity_MyNote extends AppCompatActivity {
    private DatabaseReference dbRef;
    public int mode;
    ArrayList<HashMap<String, String>> mArrayList = new ArrayList<>();
    Boolean ft1=false, ft2=false, ft3=false, ft4=false;  // 필터
    ListView vocaListView;
    Class_ItemAdapter adapter;
    TextView total, note_title;
    ImageView filter;
    FloatingActionButton add;
    String userid;
    ArrayList<String> w;  // 선택된 단어
    ArrayList<String> b;  // 선택된 단어의 북마크 여부

    Date d = new Date(System.currentTimeMillis());
    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
    String today = sf.format(d);
    String checked;
    int bb;

    Elements mean;  // 크롤링한 단어 덩어리
    Document doc = null;
    String s, pronounce, sentence, sentence_mean;  // M은 텍스트뷰 결과 확인용
    List meaning = new ArrayList();
    String search_voca;
    int max=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_note);

        Intent intent = getIntent();
        adapter = new Class_ItemAdapter();
        mode = intent.getExtras().getInt("mode");
        dbRef = FirebaseDatabase.getInstance().getReference("mynote");
        total = (TextView)findViewById(R.id.total);
        note_title = (TextView)findViewById(R.id.note_title);
        filter = (ImageView)findViewById(R.id.filter);
        userid=getIntent().getStringExtra("uid");
        add = (FloatingActionButton)findViewById(R.id.add);
        vocaListView = (ListView)findViewById(R.id.vocaListView);
        vocaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent gotoMy2 = new Intent(view.getContext(), Activity_MyNote_2.class);
                gotoMy2.putExtra("word", w.get(position));
                gotoMy2.putExtra("book",b.get(position));
                gotoMy2.putExtra("uid",userid);
                startActivity(gotoMy2);
            }
        });

        //

    }
    public void Back(View view) {
        finish();
    }

    public void FilterClick(View view) {
        final View dlgView = View.inflate(this,R.layout.item_vocafilter,null);
        final Dialog vcnumDialog = new Dialog(this);
        vcnumDialog.setContentView(dlgView);
        vcnumDialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView go;
        final ImageView image1, image2, image3, image4;
        go = (TextView)dlgView.findViewById(R.id.go);
        image1 = (ImageView)dlgView.findViewById(R.id.image1);
        image2 = (ImageView)dlgView.findViewById(R.id.image2);
        image3 = (ImageView)dlgView.findViewById(R.id.image3);
        image4 = (ImageView)dlgView.findViewById(R.id.image4);
        if (ft1) image1.setColorFilter(Color.parseColor("#000000"));
        else image1.setColorFilter(Color.parseColor("#aaaaaa"));
        if (ft2) image2.setColorFilter(Color.parseColor("#000000"));
        else image2.setColorFilter(Color.parseColor("#aaaaaa"));
        if (ft3) image3.setColorFilter(Color.parseColor("#000000"));
        else image3.setColorFilter(Color.parseColor("#aaaaaa"));
        if (ft4) image4.setColorFilter(Color.parseColor("#000000"));
        else image4.setColorFilter(Color.parseColor("#aaaaaa"));

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vcnumDialog.cancel();
            }
        });
        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft1=!ft1;
                if (ft1) {
                    image1.setColorFilter(Color.parseColor("#000000"));
                    alphafilter();
                }
                else {
                    image1.setColorFilter(Color.parseColor("#aaaaaa"));
                    nofilter();
                }
            }
        });
        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft2=!ft2;
                if (ft2) {
                    image2.setColorFilter(Color.parseColor("#000000"));
                    bookfilter();
                }
                else {
                    image2.setColorFilter(Color.parseColor("#aaaaaa"));
                    nofilter();
                }
            }
        });
        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft3=!ft3;
                if (ft3) image3.setColorFilter(Color.parseColor("#000000"));
                else {
                    image3.setColorFilter(Color.parseColor("#aaaaaa"));
                    nofilter();
                }
            }
        });
        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft4=!ft4;
                if (ft4) image4.setColorFilter(Color.parseColor("#000000"));
                else {
                    image4.setColorFilter(Color.parseColor("#aaaaaa"));
                    nofilter();
                }
            }
        });
        vcnumDialog.show();
    }
    public void bookfilter() {
        dbRef.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mArrayList.clear();
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String book = fileSnapshot.child("bookmark").getValue().toString();
                    if (book.equals("1")) {
                        String word = fileSnapshot.child("word").getValue().toString();
                        String meaning1 = fileSnapshot.child("meaning1").getValue().toString();
                        String meaning2 = fileSnapshot.child("meaning2").getValue().toString();
                        String meaning3 = fileSnapshot.child("meaning3").getValue().toString();
                        String meaning = "1. " + meaning1;
                        if (!meaning2.equals(""))
                            meaning += "   2. " + meaning2;
                        if (!meaning3.equals(""))
                            meaning += "   3. " + meaning3;

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("word", word);
                        hashMap.put("meaning", meaning);
                        w.add(word);
                        b.add(book);
                        mArrayList.add(hashMap);
                        ListAdapter adapter = new SimpleAdapter(
                                Activity_MyNote.this, mArrayList, R.layout.item_voca, new String[]{"word", "meaning"}, new int[]{R.id.voca_en, R.id.voca_mean});
                        vocaListView.setAdapter(adapter);
                        total.setText("  총 " + mArrayList.size() + "단어");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void nofilter() {
        dbRef.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mArrayList.clear();
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String word = fileSnapshot.child("word").getValue().toString();
                    String meaning1 = fileSnapshot.child("meaning1").getValue().toString();
                    String meaning2 = fileSnapshot.child("meaning2").getValue().toString();
                    String meaning3 = fileSnapshot.child("meaning3").getValue().toString();
                    String book = fileSnapshot.child("bookmark").getValue().toString();
                    String meaning = "1. "+meaning1;
                    if (!meaning2.equals(""))
                        meaning += "   2. " + meaning2;
                    if (!meaning3.equals(""))
                        meaning += "   3. " + meaning3;

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("word", word);
                    hashMap.put("meaning", meaning);
                    w.add(word);
                    b.add(book);
                    mArrayList.add(hashMap);
                    ListAdapter adapter = new SimpleAdapter(
                            Activity_MyNote.this, mArrayList, R.layout.item_voca, new String[]{"word", "meaning"}, new int[]{R.id.voca_en, R.id.voca_mean});
                    vocaListView.setAdapter(adapter);
                    total.setText("  총 "+mArrayList.size()+"단어");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void recently() {
        dbRef.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mArrayList.clear();
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String understand = fileSnapshot.child("understand").getValue().toString();
                    String trying = fileSnapshot.child("try").getValue().toString();
                    if (understand.equals("0")&!trying.equals("0")) {
                        String book = fileSnapshot.child("bookmark").getValue().toString();
                        String word = fileSnapshot.child("word").getValue().toString();
                        String meaning1 = fileSnapshot.child("meaning1").getValue().toString();
                        String meaning2 = fileSnapshot.child("meaning2").getValue().toString();
                        String meaning3 = fileSnapshot.child("meaning3").getValue().toString();
                        String meaning = "1. " + meaning1;
                        if (!meaning2.equals(""))
                            meaning += "   2. " + meaning2;
                        if (!meaning3.equals(""))
                            meaning += "   3. " + meaning3;

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("word", word);
                        hashMap.put("meaning", meaning);
                        w.add(word);
                        b.add(book);
                        mArrayList.add(hashMap);
                        ListAdapter adapter = new SimpleAdapter(
                                Activity_MyNote.this, mArrayList, R.layout.item_voca, new String[]{"word", "meaning"}, new int[]{R.id.voca_en, R.id.voca_mean});
                        vocaListView.setAdapter(adapter);
                        total.setText("  총 " + mArrayList.size() + "단어");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void usually() {
        dbRef.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mArrayList.clear();
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String trying = fileSnapshot.child("try").getValue().toString();
                    String success = fileSnapshot.child("success").getValue().toString();

                    if (!trying.equals("0")) {
                        float percent = Integer.parseInt(success)/Integer.parseInt(trying);
                        if (percent < 0.5) {
                            String book = fileSnapshot.child("bookmark").getValue().toString();
                            String word = fileSnapshot.child("word").getValue().toString();
                            String meaning1 = fileSnapshot.child("meaning1").getValue().toString();
                            String meaning2 = fileSnapshot.child("meaning2").getValue().toString();
                            String meaning3 = fileSnapshot.child("meaning3").getValue().toString();
                            String meaning = "1. " + meaning1;
                            if (!meaning2.equals(""))
                                meaning += "   2. " + meaning2;
                            if (!meaning3.equals(""))
                                meaning += "   3. " + meaning3;

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("word", word);
                            hashMap.put("meaning", meaning);
                            w.add(word);
                            b.add(book);
                            mArrayList.add(hashMap);
                            ListAdapter adapter = new SimpleAdapter(
                                    Activity_MyNote.this, mArrayList, R.layout.item_voca, new String[]{"word", "meaning"}, new int[]{R.id.voca_en, R.id.voca_mean});
                            vocaListView.setAdapter(adapter);
                            total.setText("  총 " + mArrayList.size() + "단어");
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void alphafilter() {
        dbRef.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mArrayList.clear();
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    String word = fileSnapshot.child("word").getValue().toString();
                    String meaning1 = fileSnapshot.child("meaning1").getValue().toString();
                    String meaning2 = fileSnapshot.child("meaning2").getValue().toString();
                    String meaning3 = fileSnapshot.child("meaning3").getValue().toString();
                    String book = fileSnapshot.child("bookmark").getValue().toString();
                    String meaning = "1. "+meaning1;
                    if (!meaning2.equals(""))
                        meaning += "   2. " + meaning2;
                    if (!meaning3.equals(""))
                        meaning += "   3. " + meaning3;

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("word", word);
                    hashMap.put("meaning", meaning);
                    w.add(word);
                    b.add(book);
                    mArrayList.add(hashMap);
                    MapComparator comp = new MapComparator("word");
                    Collections.sort(mArrayList, comp);
                    ListAdapter adapter = new SimpleAdapter(
                            Activity_MyNote.this, mArrayList, R.layout.item_voca, new String[]{"word", "meaning"}, new int[]{R.id.voca_en, R.id.voca_mean});
                    vocaListView.setAdapter(adapter);
                    total.setText("  총 "+mArrayList.size()+"단어");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    class MapComparator implements Comparator<HashMap<String, String>> {

        private final String key;

        public MapComparator(String key) {
            this.key = key;
        }

        @Override
        public int compare(HashMap<String, String> first, HashMap<String, String> second) {
            int result = first.get(key).compareTo(second.get(key));
            return result;
        }
    }

    @Override
    public void onResume() {
        update();
        super.onResume();
    }

    public void update() {
        w=new ArrayList<>();
        b=new ArrayList<>();
        switch(mode) {
            case 1:
                note_title.setText("내 단어장");
                nofilter();
                break;

            case 2:
                note_title.setText("북마크한 단어장");
                filter.setVisibility(View.INVISIBLE);
                //Invisible();
                add.hide();
                bookfilter();
                break;

            case 3:
                note_title.setText("최근 틀린 단어장");
                filter.setVisibility(View.INVISIBLE);
                //Invisible();
                add.hide();
                recently();
                break;

            case 4:
                note_title.setText("자주 틀리는 단어장");
                filter.setVisibility(View.INVISIBLE);
                //Invisible();
                add.hide();
                usually();
                break;
        }
    }

    public void OnAddClick(View v)
    {
        final View dlgView = View.inflate(Activity_MyNote.this,R.layout.dialog_addvoca2,null);
        final Dialog editDialog = new Dialog(Activity_MyNote.this);
        editDialog.setContentView(dlgView);
        final EditText search;
        final TextView ok, mean1, mean2, mean3, mean4, mean5;
        final CheckBox check1, check2, check3, check4, check5;
        ImageView btn;
        search = (EditText) dlgView.findViewById(R.id.search);
        btn = (ImageView) dlgView.findViewById(R.id.btn);
        ok= (TextView) dlgView.findViewById(R.id.ok_bt);
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
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s="";
                pronounce="";
                sentence="";
                sentence_mean="";
                meaning.clear();
                search_voca = search.getText().toString();
                meaning = new ArrayList<String>(Arrays.asList("", "", "", "", ""));

                new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] params) {
                        try {
                            doc = Jsoup.connect("https://alldic.daum.net/search.do?q="+search_voca+"&dic=eng&search_first=Y").get();
                            mean = doc.select("span.txt_search");
                            s = doc.select("span.num_search").text();
                            pronounce = doc.selectFirst("span.txt_pronounce").text();
                            sentence = doc.selectFirst("span.txt_ex").text();
                            sentence_mean = doc.selectFirst("span.mean_example").text();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            String[] num = s.substring(0, s.length() - 1).replaceAll(". ", ",").split(",");
                            for (int i = 0; i < num.length; i++) {
                                if (Integer.parseInt(num[i]) >= max)
                                    max = Integer.parseInt(num[i]);
                                else
                                    break;
                            }
                        }  catch (Exception e) {}

                        int count=0;
                        for (Element e : mean) {
                            count++;
                            meaning.add(count-1,e.text().trim());
                            if (count==max)
                                break;
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        if (!meaning.get(0).equals("")) {
                            check1.setVisibility(View.VISIBLE);
                            mean1.setVisibility(View.VISIBLE);
                            mean1.setText(""+meaning.get(0));
                        }
                        else {
                            check1.setVisibility(View.GONE);
                            mean1.setText("");
                        }
                        if (!meaning.get(1).equals("")) {
                            check2.setVisibility(View.VISIBLE);
                            mean2.setVisibility(View.VISIBLE);
                            mean2.setText(""+meaning.get(1));
                        }
                        else {
                            check2.setVisibility(View.GONE);
                            mean2.setVisibility(View.GONE);
                            mean2.setText("");
                        }
                        if (!meaning.get(2).equals("")) {
                            check3.setVisibility(View.VISIBLE);
                            mean3.setVisibility(View.VISIBLE);
                            mean3.setText(""+meaning.get(2));
                        }
                        else {
                            check3.setVisibility(View.GONE);
                            mean3.setVisibility(View.GONE);
                            mean3.setText("");
                        }
                        if (!meaning.get(3).equals("")) {
                            check4.setVisibility(View.VISIBLE);
                            mean4.setVisibility(View.VISIBLE);
                            mean4.setText(""+meaning.get(3));
                        }
                        else {
                            check4.setVisibility(View.GONE);
                            mean4.setVisibility(View.GONE);
                            mean4.setText("");
                        }
                        if (!meaning.get(4).equals("")) {
                            check5.setVisibility(View.VISIBLE);
                            mean5.setVisibility(View.VISIBLE);
                            mean5.setText(""+meaning.get(4));
                        }
                        else {
                            check5.setVisibility(View.GONE);
                            mean5.setVisibility(View.GONE);
                            mean5.setText("");
                        }
                    }
                }.execute();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checked="";
                if (!check1.isChecked()&&!check2.isChecked()&&!check3.isChecked()&&!check4.isChecked()&&!check5.isChecked())
                    Toast.makeText(Activity_MyNote.this, "추가할 뜻을 선택해 주세요.", Toast.LENGTH_SHORT).show();
                else {
                    dbRef.child(userid).child(search_voca).child("word").setValue(search_voca);
                    dbRef.child(userid).child(search_voca).child("pronounce").setValue(pronounce);
                    dbRef.child(userid).child(search_voca).child("meaning1").setValue("");
                    dbRef.child(userid).child(search_voca).child("meaning2").setValue("");
                    dbRef.child(userid).child(search_voca).child("meaning3").setValue("");

                    if (check1.isChecked()) {
                        checked+="1";
                    }
                    else {
                        checked+="0";
                    }
                    if (check2.isChecked()) {
                        checked+="1";
                    }
                    else {
                        checked+="0";
                    }
                    if (check3.isChecked()) {
                        checked+="1";
                    }
                    else {
                        checked+="0";
                    }
                    if (check4.isChecked()) {
                        checked+="1";
                    }
                    else {
                        checked+="0";
                    }
                    if (check5.isChecked()) {
                        checked+="1";
                    }
                    else {
                        checked+="0";
                    }
                    bb=1;

                    for (int a = 0; a < 5; a++) {
                        if (checked.substring(a, a + 1).equals("1")) {
                            dbRef.child(userid).child(search_voca).child("meaning" + bb).setValue(meaning.get(a));
                            bb++;
                        }
                    }
                    dbRef.child(userid).child(search_voca).child("sentence").setValue(sentence);
                    dbRef.child(userid).child(search_voca).child("sentence_mean").setValue(sentence_mean);
                    dbRef.child(userid).child(search_voca).child("bookmark").setValue(0);
                    dbRef.child(userid).child(search_voca).child("try").setValue(0);
                    dbRef.child(userid).child(search_voca).child("success").setValue(0);
                    dbRef.child(userid).child(search_voca).child("understand").setValue(0);
                    dbRef.child(userid).child(search_voca).child("date").setValue(today);
                    dbRef.child(userid).child(search_voca).child("savetime").setValue((int)((System.currentTimeMillis())));
                    Toast.makeText(getApplicationContext(), "성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    editDialog.dismiss();
                    update();
                }
            }
        });

        editDialog.show();
    }
}
