package com.example.vocket;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Vocket extends AppCompatActivity {

    // Instance of BottomNavigation & ViewPager
    private BottomNavigationView botNavi;
    private ViewPager viewPager;
    private Class_ViewAdapter vocAdapter;
    private MenuItem prevMenuItem;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_note:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_test:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_pvp:
                    viewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        }
    };

    Intent pIntent;
    private DatabaseReference dbRef;

    //left navi
    DrawerLayout drawer;
    NavigationView navigationView;
    TextView user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocket);

        //ViewPager Setting
        viewPager = (ViewPager)findViewById(R.id.viewpager);

        botNavi = (BottomNavigationView) findViewById(R.id.botnavi);
        botNavi.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        vocAdapter = new Class_ViewAdapter(getSupportFragmentManager());
        vocAdapter.AddFragment(new Fragment_Home(),"frag1");
        vocAdapter.AddFragment(new Fragment_Note(),"frag2");
        vocAdapter.AddFragment(new Fragment_Test(),"freg3");
        vocAdapter.AddFragment(new Fragment_Pvp(),"freg4");

        viewPager.setAdapter(vocAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    botNavi.getMenu().getItem(0).setChecked(false);

                botNavi.getMenu().getItem(i).setChecked(true);
                prevMenuItem = botNavi.getMenu().getItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        // left navigation
        pIntent = getIntent();
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_menunavi,navigationView,true);
        user_email = (TextView)navigationView.findViewById(R.id.textEmail);
        user_email.setText(pIntent.getStringExtra("email"));
    }

    public void onOcrClick(View view){

        View dlgView = View.inflate(this,R.layout.dialog_ocrmode,null);
        final Dialog dlg = new Dialog(this);
        dlg.setContentView(dlgView);

        ImageView all,under;

        all = (ImageView)dlgView.findViewById(R.id.ocr_all_bt);
        under = (ImageView)dlgView.findViewById(R.id.ocr_underline_bt);

        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ocrIntent = new Intent(getApplicationContext(), Activity_Ocr.class);
                ocrIntent.putExtra("uid",pIntent.getStringExtra("uid"));
                ocrIntent.putExtra("str","aa");
                startActivity(ocrIntent);
            }
        });

        under.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ocrIntent = new Intent(getApplicationContext(), Activity_Ocr.class);
                ocrIntent.putExtra("uid",pIntent.getStringExtra("uid"));
                ocrIntent.putExtra("str","bb");
                startActivity(ocrIntent);
            }
        });

        dlg.show();
    }

    public void onMenuClick(View view){

    }


}
