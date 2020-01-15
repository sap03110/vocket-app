package com.example.vocket;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

public class Activity_Howto extends AppCompatActivity {

    private ViewPager viewPager;
    private Class_ViewAdapter viewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howto);

        viewPager = (ViewPager)findViewById(R.id.viewpager2);

        viewAdapter = new Class_ViewAdapter(getSupportFragmentManager());
        viewAdapter.AddFragment(new Fragment_zhow1(),"frag1");
        viewAdapter.AddFragment(new Fragment_zhow2(),"frag2");
        viewAdapter.AddFragment(new Fragment_zhow3(),"freg3");
        viewAdapter.AddFragment(new Fragment_zhow4(),"freg4");

        viewPager.setAdapter(viewAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }
}
