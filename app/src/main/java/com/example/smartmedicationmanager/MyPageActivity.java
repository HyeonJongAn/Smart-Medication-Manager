/****************************
 ComForbiddenListActivity.java
 작성 팀 : Hello World!
 주 작성자 : 신윤호
 프로그램명 : Medication Helper
 ***************************/
package com.example.smartmedicationmanager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MyPageActivity extends AppCompatActivity{
    TextView TvName;
    TextView TvBirth;
    TextView TvGender;
    TextView BtnLogout;
    Button BtnDel;

    UserData userData;
    com.example.smartmedicationmanager.UserDBHelper userDBHelper;
    com.example.smartmedicationmanager.MedicDBHelper medicDBHelper;
    SQLiteDatabase sqlUserDB, sqlMedicDB;

    //뒤로가기 버튼을 누르면 스택에 쌓여있는 전 액티비티로 돌아가게 하는 함수
    @Override
    public void onBackPressed() { // 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작
        super.onBackPressed();
        Intent Back = new Intent(MyPageActivity.this, com.example.smartmedicationmanager.MainPageActivity.class); // 메인화면으로 돌아가는 기능
        Back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 회원정보 페이지가 백그라운드에서 돌아가지 않도록 완전종료
        startActivity(Back); // 실행
        finish(); // Progress 완전 종료
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_my_page);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 기본 타이틀 사용 안함
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); // 커스텀 사용
        getSupportActionBar().setCustomView(R.layout.mytitlebar_custom); // 커스텀 사용할 파일 위치

        TvName = (TextView) findViewById(R.id.tvName);
        TvBirth = (TextView) findViewById(R.id.tvBirth);
        TvGender = (TextView) findViewById(R.id.tvGender);
        BtnLogout = (TextView) findViewById(R.id.btnLogout);
        BtnDel = (Button) findViewById(R.id.btnDel);

        userData = (UserData) getApplicationContext();
        userDBHelper = new com.example.smartmedicationmanager.UserDBHelper(this);
        medicDBHelper = new com.example.smartmedicationmanager.MedicDBHelper(this);
        sqlUserDB = userDBHelper.getWritableDatabase();
        sqlMedicDB = medicDBHelper.getWritableDatabase();

        /* UserData 클래스에서 현재 로그인중인 사용자의 정보를 불러옴 */
        TvName.setText("아이디 : " + userData.getUserNickName());
        TvBirth.setText("생년월일 : " + userData.getUserBirth());
        TvGender.setText("성별 : " + userData.getUserGender());

        BtnLogout.setOnClickListener(new View.OnClickListener() { // 로그아웃 버튼을 눌렀을 때
            @Override
            public void onClick(View view) {
                userData.Init(); // UserData의 모든 내용 초기화
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), com.example.smartmedicationmanager.MainActivity.class)); // 로그인 화면으로 돌려보냄
                finish(); // Progress 완전 종료
            }
        });

        BtnDel.setOnClickListener(new View.OnClickListener() { // 회원탈퇴 버튼을 눌렀을 때
            @Override
            public void onClick(View view) {
                // 두 DB에서 로그인 중인 회원의 정보를 담고 있는 행을 삭제
                sqlUserDB.execSQL("DELETE FROM userTBL WHERE uID = '" + userData.getUserID() + "'");
                sqlMedicDB.execSQL("DELETE FROM medicTBL WHERE uID = '" + userData.getUserID() + "'");
                userData.Init(); // UserData의 모든 내용 초기화
                Toast.makeText(getApplicationContext(), "회원탈퇴가 완료되었습니다. 이용해주셔서 감사합니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), com.example.smartmedicationmanager.MainActivity.class)); // 로그인 화면으로 돌려보냄
                finish(); // Progress 완전 종료
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.userNav);
        //바텀네비게이션을 나타나게 해주는 함수
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    //home버튼을 누르면 액티비티 화면을 전환시켜준다
                    case R.id.homeNav:
                        startActivity(new Intent(getApplicationContext(), com.example.smartmedicationmanager.MainPageActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
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
                    //현재 페이지에서 보여주는 액티비티
                    case R.id.userNav:
                        return true;
                }
                return false;
            }
        });
    }
}
