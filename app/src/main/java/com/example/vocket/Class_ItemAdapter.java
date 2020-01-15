package com.example.vocket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

// 리사이클러 뷰의 동적 메모리할당을 위한 어댑터 구현

public class Class_ItemAdapter extends RecyclerView.Adapter<Class_ItemAdapter.ViewHolder> {

    private ArrayList<SingleVoca> mVocaList = null;

    Class_ItemAdapter(){
        mVocaList = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView englishText;
        TextView meanText;
        ViewHolder(View itemView){
            super(itemView);
            englishText = itemView.findViewById(R.id.voca_en);
            meanText = itemView.findViewById(R.id.voca_mean);
        }
    }

    class SingleVoca{
        String mEnglish;
        String mMean;

        SingleVoca(String en,String mean){
            mEnglish = en;
            mMean = mean;
        }
    }

    @Override
    public Class_ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_voca,parent,false);
        Class_ItemAdapter.ViewHolder vh = new Class_ItemAdapter.ViewHolder(view); // 인플레이트한 뷰(커스텀뷰)를 생성자에 넘겨줌.
        return vh;
    }

    @Override
    public void onBindViewHolder(Class_ItemAdapter.ViewHolder holder, int position) {
        SingleVoca singleVoca = mVocaList.get(position);
        holder.englishText.setText(singleVoca.mEnglish);     // 현재 화면에 보여지는 View에 텍스트 출력
        holder.meanText.setText(singleVoca.mMean);           // (현재 화면에 보여지는 것들은 뷰홀더가 관리)
    }

    @Override
    public int getItemCount() {
        return mVocaList.size();
    }



    public void addItem(String en, String mean){
        mVocaList.add(new SingleVoca(en,mean));
    }
}
