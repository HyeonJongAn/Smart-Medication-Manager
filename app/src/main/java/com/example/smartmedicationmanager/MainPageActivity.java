/****************************
 MainPageActivity.java
 작성 팀 : Hello World!
 주 작성자 : 송승우
 프로그램명 : Medication Helper
 ***************************/
package com.example.smartmedicationmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainPageActivity extends AppCompatActivity {

    private AdView mAdview;
    private ListView mListView;

    //뒤로가기 누르면 앱종료시키는 함수
    @Override
    public void onBackPressed() {
        //다이어로그를 화면에 나타냄
        AlertDialog.Builder exitDialogBuilder = new AlertDialog.Builder(MainPageActivity.this);
        exitDialogBuilder
                .setTitle("프로그램 종료")
                .setMessage("종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네",
                        //네를 누르면 앱 종료
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                int pid = android.os.Process.myPid();
                                android.os.Process.killProcess(pid);
                                finish();
                            }
                        })
                //아니오 누르면 다이어로그를 종료
                .setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
        AlertDialog exitDialog = exitDialogBuilder.create();
        exitDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        Button btnPill = findViewById(R.id.pillbtn);
        Button btnJar = findViewById(R.id.jarbtn);
        bottomNavigationView.setSelectedItemId(R.id.homeNav);

        //바텀네비게이션을 나타나게 해주는 함수
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    //현재 페이지에서 보여주는 액티비티
                    case R.id.homeNav:
                        return true;
                    //camera 버튼을 누르면 화면을 전환시켜준다.
                    case R.id.cameraNav:
                        startActivity(new Intent(getApplicationContext(), MedicRegisterActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    //article 버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.articleNav:
                        startActivity(new Intent(getApplicationContext(), MedicineListActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    //user 버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.userNav:
                        startActivity(new Intent(getApplicationContext(), MyPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
        //현재 액티비티에서 MedicRegisterActivity로 넘겨주는 버튼
        btnPill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediRegIntent = new Intent(MainPageActivity.this, MedicRegisterActivity.class);
                startActivity(mediRegIntent);
            }
        });
        //현재 액티비티에서 MedicCheckActivity로 넘겨주는 버튼
        btnJar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mediCheckIntent = new Intent(MainPageActivity.this, MedicCheckActivity.class);
                startActivity(mediCheckIntent);
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() { //광고 초기화
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdview = findViewById(R.id.adView); //배너광고 레이아웃 가져오기
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER); //광고 사이즈는 배너 사이즈로 설정
        adView.setAdUnitId("\n" + " ca-app-pub-3940256099942544/630097811");

        mListView = (ListView) findViewById(R.id.productlist);
        dataSetting();
    }
    private void dataSetting(){
        MyAdapter myAdapter = new MyAdapter();

        myAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mysitol_size), "마이시톨 2045mg X 60포" , "39,900원");
        myAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mysitol_size), "마이시톨 2045mg X 60포" , "39,900원");
        myAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mysitol_size), "마이시톨 2045mg X 60포" , "39,900원");
        myAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mysitol_size), "마이시톨 2045mg X 60포" , "39,900원");
        myAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.mysitol_size), "마이시톨 2045mg X 60포" , "39,900원");

        mListView.setAdapter(myAdapter);
    }
}