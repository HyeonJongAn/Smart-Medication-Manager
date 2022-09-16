/****************************
 DuplicateListActivity.java
 작성 팀 : Hello World!
 주 작성자 : 백인혁
 프로그램명 : Medication Helper
 ***************************/
package com.example.smartmedicationmanager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class DuplicateListActivity extends AppCompatActivity {

    /* 의약품DB를 사용하기 위한 변수들 */
    String data;
    UserData userData;
    com.example.smartmedicationmanager.MedicDBHelper myHelper;
    SQLiteDatabase sqlDB;

    @Override // 하단의 뒤로가기(◀) 버튼을 눌렀을 시 동작
    public void onBackPressed() {
        super.onBackPressed();
        Intent Back = new Intent(DuplicateListActivity.this, MedicineListActivity.class); // 메인화면으로 돌아가는 기능
        Back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 효능중복 페이지가 백그라운드에서 돌아가지 않도록 완전종료
        startActivity(Back); // 실행
        finish(); // Progress 완전 종료
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_duplicatelist);
        setTitle("Medication Helper");

        userData = (UserData) getApplicationContext();
        myHelper = new com.example.smartmedicationmanager.MedicDBHelper(this);
        sqlDB = myHelper.getReadableDatabase(); // 복용의약품 DB를 읽기 전용으로 불러옴
        // 현재 로그인중인 사용자가 복용중인 의약품의 DB를 읽어들임
        Cursor cursor = sqlDB.rawQuery("SELECT * FROM medicTBL WHERE uID = '" + userData.getUserID() + "';", null);

        Toast.makeText(getApplicationContext(), "조회 중입니다. 잠시만 기다려주세요", Toast.LENGTH_LONG).show();

        ListView DuplicateList=(ListView)findViewById(R.id.duplicateList);
        TextView DuplicateTextView=(TextView)findViewById(R.id.duplicateIng);

        //약 목록을 저장하는 배열
        String[] medicineList = new String[cursor.getCount()];
        int serialNo = 0;

        while (cursor.moveToNext()) { // DB에서 받아온 처방약 목록을 상단의 배열에 저장
            medicineList[serialNo] = cursor.getString(2);
            serialNo++;
        }

        //약 목록이 저장되어 있는 배열의 길이
        int size=medicineList.length;

        //받은 약 목록 전체의 이름과 효능중복성분을 저장하는 배열(성분이 없으면 [?][1]은 ""이다)
        String[][] medicNameINGList=new String[size][2];

        //OpenApI xml 파싱 스레드
        new Thread(new Runnable() {

            int forbiddenlistSize=0;

            int index=0;

            @Override
            public void run() {

                for(int i=0;i<size;i++){
                    //처방약 목록에서 약 이름을 차례대로 받아 OpenAPI로 처리
                    data=getXmlData(medicineList[i]);

                    medicNameINGList[i][0]=medicineList[i];
                    medicNameINGList[i][1]=data;
                }

                //효능 중복이 있는 약들만 보관할 배열들의 크기를 구한다.
                for(int i=0;i<size;i++){
                    String str=medicNameINGList[i][1];
                    if(TextUtils.isEmpty(str)==false){
                        forbiddenlistSize++;
                    }
                }

                //효능중복 약물에 해당하는 약물들의 이름만 저장하는 배열
                String[] duplicateMedicNameList=new String[forbiddenlistSize];

                for(int i=0;i<size;i++){
                    String str=medicNameINGList[i][1];
                    if(TextUtils.isEmpty(str)==false){
                        duplicateMedicNameList[index]=medicNameINGList[i][0];
                        index++;
                    }
                }

                //효능중복 약물에 해당하는 약물들의 효능만 저장하는 배열
                String[] DuplicateIngredientList=new String[forbiddenlistSize];

                index=0;

                for(int i=0;i<size;i++){
                    String str=medicNameINGList[i][1];
                    if(TextUtils.isEmpty(str)==false){
                        DuplicateIngredientList[index]=medicNameINGList[i][1];
                        index++;
                    }
                }

                //효능중복 사항이 있는 약품 이름 목록을 가지는 arraylist
                ArrayList<String> DuplicateMedicationList=new ArrayList<>(Arrays.asList(duplicateMedicNameList));

                ArrayAdapter DuplicateNameAdapter=new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_single_choice,DuplicateMedicationList);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //화면에 효능 중복 대상 약품 이름 목록 표시
                        DuplicateList.setAdapter(DuplicateNameAdapter);

                        //약품 리스트뷰에서 항목을 선택했을 때 효능을 Textview에 표시
                        DuplicateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                String medicineName=(String) adapterView.getAdapter().getItem(i);

                                for(int x=0;x<forbiddenlistSize;x++){
                                    if(medicineName.equals(duplicateMedicNameList[x])==true){
                                        String Ingredient=DuplicateIngredientList[x];
                                        DuplicateTextView.setText(Ingredient);
                                        break;
                                    }
                                }
                            }
                        });

                        //ScrollView 안에서 리스트뷰를 스크롤 할 수 있도록 설정
                        DuplicateList.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                DuplicateList.requestDisallowInterceptTouchEvent(true);
                                return false;
                            }
                        });
                    }
                });
            }
        }).start();

    }

    //Xml 파싱으로 효능중복에 해당하는 약을 찾아내고 효과 알아내어 저장
    String getXmlData(String medicname) {
        StringBuffer buffer=new StringBuffer();
        String str=medicname;
        String MedicineName= URLEncoder.encode(str);

        String queryUrl="http://apis.data.go.kr/1471000/DURPrdlstInfoService01/getEfcyDplctInfoList?serviceKey=RZnyfUGsOhY2tWWUv262AHpeMQYn4Idqd5cgG0rGNHPd648m5j0Pu3eiS3ewN4XhhHT%2FvuliAmF9KLJdzh1TFA%3D%3D&itemName="+MedicineName+"&pageNo=1&numOfRows=1&type=xml";
        try {
            URL url=new URL(queryUrl);
            InputStream is=url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp=factory.newPullParser();
            xpp.setInput(new InputStreamReader(is,"UTF-8"));

            String tag;

            xpp.next();
            int eventType=xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){

                    case XmlPullParser.START_TAG:
                        tag=xpp.getName();

                        if(tag.equals("item"));
                        //약의 효능을 가져오기
                        else if(tag.equals("EFFECT_NAME")){
                            xpp.next();
                            buffer.append(xpp.getText());
                        }
                        break;
                }
                eventType=xpp.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }

}
