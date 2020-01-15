package com.example.vocket;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class Activity_Test_Result extends AppCompatActivity {
    Intent pIntent;
    TextView report,mention,tmodename;
    String modename;
    ImageView retestbt;
    boolean allclear = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__test__result);

        pIntent = getIntent();
        report = (TextView)findViewById(R.id.report);
        mention = (TextView)findViewById(R.id.mention);
        tmodename = (TextView)findViewById(R.id.modename);
        retestbt = (ImageView)findViewById(R.id.retestbt);

        int total,correct,incorrect;

        total = pIntent.getIntExtra("vocanum",0);
        correct = pIntent.getIntExtra("correct",0);
        incorrect = pIntent.getIntExtra("incorrect",0);
        modename = pIntent.getStringExtra("mode");

        report.setText("총 "+total+"문제 중 "+correct+"문제 정답");
        tmodename.setText(modename);

        if((double)correct/total > 0.8)
            mention.setText("단어 암기계의 천상계 시군요!");
        else if((double)correct/total > 0.5)
            mention.setText("조금만 더 노력하시면 좋은 결과가 있을거에요!");
        else if((double)correct/total <= 0.5)
            mention.setText("노력이 많이 필요해보입니다. 공부하세요!");

        if(total == correct) {
            retestbt.setImageResource(R.drawable.inact_testbt);
            allclear = true;
        }
        else
            retestbt.setImageResource(R.drawable.btn);


        //--- Pie Chart ---
        PieChart pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);

        ArrayList<Entry> yvalues = new ArrayList<Entry>();
        yvalues.add(new Entry(pIntent.getIntExtra("correct",0), 0));
        yvalues.add(new Entry(pIntent.getIntExtra("incorrect",0), 1));

        PieDataSet dataSet = new PieDataSet(yvalues, "");
        ArrayList<String> xVals = new ArrayList<String>();

        xVals.add("정답");
        xVals.add("오답");

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);

        //dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        //dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        //dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        //dataSet.setColors(ColorTemplate.PASTEL_COLORS);
        //pieChart.setDrawHoleEnabled(false);
        //pieChart.setTransparentCircleRadius(100f);
        pieChart.setHoleRadius(40f);
        pieChart.setHoleColorTransparent(true);
        //pieChart.setCenterText("정답률 : "+Math.round(((double)correct/total)*100)+"%");
        //pieChart.setCenterTextSize(20f);;
        pieChart.animateXY(1400, 1400);
        pieChart.setDescription("");
        pieChart.getLegend().setEnabled(false);
    }

    public void onIncorrect(View v){
        View dlgView = View.inflate(this,R.layout.dialog_incorrectvoca,null);

        final Dialog loginDialog = new Dialog(this);
        loginDialog.setContentView(dlgView);

        ArrayList<Class_VocaObject> incorrectList = new ArrayList<>();
        incorrectList = pIntent.getParcelableArrayListExtra("incorrectList");

        Class_VocaAdapter adapter = new Class_VocaAdapter();
        for(int i=0;i<incorrectList.size();i++)
            adapter.addItem(incorrectList.get(i).voca,incorrectList.get(i).mean1);

        ListView listView = (ListView)dlgView.findViewById(R.id.incorrectlist);
        listView.setAdapter(adapter);

        loginDialog.show();
    }

    public void onRetest(View v){
        if(!allclear) {
            Intent retestIntent = new Intent(getApplicationContext(), Activity_Retest.class);
            retestIntent.putParcelableArrayListExtra("list", pIntent.getParcelableArrayListExtra("incorrectList")); //앞에서 받은 오답리스트, 전달.
            retestIntent.putExtra("mode", "틀린 단어 재시험");
            startActivity(retestIntent);
            finish();
        }
    }
}
