package com.example.vocket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Class_VocaAdapter extends BaseAdapter {
    private ArrayList<SingleVoca> mVocaList = new ArrayList<>();

    class SingleVoca{
        String mEnglish;
        String mMean;

        SingleVoca(String en,String mean){
            mEnglish = en;
            mMean = mean;
        }

        public String getRowtext1() {
            return mEnglish;
        }
        public String getRowtext2() {
            return mMean;
        }
    }

    @Override
    public int getCount() { return mVocaList.size(); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public Object getItem(int position) { return mVocaList.get(position); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_voca, parent, false);
        }
        TextView textView1 = (TextView) convertView.findViewById(R.id.voca_en);
        TextView textView2 = (TextView) convertView.findViewById(R.id.voca_mean);

        SingleVoca listViewItem = mVocaList.get(position);
        textView1.setText(listViewItem.getRowtext1());
        textView2.setText(listViewItem.getRowtext2());



        return convertView;
    }

    public void addItem(String word, String mean) {
        SingleVoca item = new SingleVoca(word,mean);
        mVocaList.add(item);
    }
}
