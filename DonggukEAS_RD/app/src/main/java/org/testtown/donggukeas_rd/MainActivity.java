package org.testtown.donggukeas_rd;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        readText();

    }


    public void readText() {
        database = database.getInstance();
        reference = database.getReference();
        AssetManager assetManager = getApplication().getAssets();//ASSETS에 저장된 TXT파일 호출
        int stuNo=0;//DB 내의 순서 지정할 변수
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(assetManager.open("DBInfo.txt")));
            String str = null;

            while ((str = br.readLine())!= null){

                //한 줄씩 읽어 들인 것에서 공백 문자로 토큰을 나눠 이름과 학번 따로 저장
                String[] numbers = str.split(" ");
                //sm 객체 생성과 동시에 초기화
                stuModel sm = new stuModel(numbers[1],"-1","-1",0,"1234",numbers[0]);

                Map<String, Object> newSM = new HashMap<>();
                Map<String, Object> csAuth = sm.stu_auth_Map();
                //STUDENT_AUTH DB에 들어갈 값 newSM에 저장후 DB에 입력
                newSM.put(sm.studentId, csAuth);
                reference.child("STUDENT_AUTH").updateChildren(newSM);

                Map<String, Object> newSM1 = new HashMap<>();
                Map<String, Object> csDev = sm.stu_dev_Map();
                //STUDENT_DEVICE DB에 들어갈 값 newSM1에 저장후 DB에 입력
                newSM1.put(sm.studentId, csDev);
                reference.child("STUDENT_DEVICE").updateChildren(newSM1);

                Map<String, Object> newSM2 = new HashMap<>();
                Map<String, Object> csWifi = sm.stu_wifi_Map();
                //STUDENT_WIFI DB에 들어갈 값 newSM2에 저장후 DB에 입력
                newSM2.put(sm.studentId, csWifi);
                reference.child("STUDENT_WIFI").updateChildren(newSM2);

                Map<String, Object> newSM3 = new HashMap<>();
                Map<String, Object> csMap = sm.stu_Map();
                //STUDENT DB에 들어갈 값 newSM3에 저장후 DB에 입력
                newSM3.put(sm.studentId, csMap);
                reference.child("STUDENT").updateChildren(newSM3);

                Map<String, Object> newSM4 = new HashMap<>();
                Map<String, Object> csAtd = sm.stu_atd_Map();
                //STUDENT_ATTENDANCE DB에 들어갈 값 newSM4에 저장후 DB에 입력
                newSM4.put(sm.studentId, csAtd);
                for(int index=1;index<=16;index++) {
                    reference.child("STUDENT_ATTENDANCE").child("CSE4058-02").child(String.valueOf(index)).updateChildren(newSM4);
                    reference.child("STUDENT_ATTENDANCE").child("CSE2017-01").child(String.valueOf(index)).updateChildren(newSM4);
                }

                //SUBJECT에 들어갈 값 newSM5에 저장후 DB에 입력
                Map<String, Object> newSM5 = new HashMap<>();
                newSM5.put(String.valueOf(stuNo), sm.studentId);
                reference.child("SUBJECT").child("CSE4058-02").child("listenStudent").updateChildren(newSM5);
                reference.child("SUBJECT").child("CSE2017-01").child("listenStudent").updateChildren(newSM5);

                stuNo+=1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null)try {
                Map<String, Object> newSM6 = new HashMap<>();
                //SUBJECT에 들어갈 값 newSM6에 저장후 DB에 입력
                newSM6.put("days", "0101000");
                reference.child("SUBJECT").child("CSE4058-02").updateChildren(newSM6);
                newSM6.put("subjectCode", "CSE4058-02");
                reference.child("SUBJECT").child("CSE4058-02").updateChildren(newSM6);
                newSM6.put("subjectName", "소프트웨어공학개론");
                reference.child("SUBJECT").child("CSE4058-02").updateChildren(newSM6);

                //SUBJECT에 들어갈 값 newSM7에 저장후 DB에 입력
                Map<String, Object> newSM7 = new HashMap<>();
                newSM7.put("days", "0101000");
                reference.child("SUBJECT").child("CSE2017-01").updateChildren(newSM7);
                newSM7.put("subjectCode", "CSE2017-01");
                reference.child("SUBJECT").child("CSE2017-01").updateChildren(newSM7);
                newSM7.put("subjectName", "자료구조와실습");
                reference.child("SUBJECT").child("CSE2017-01").updateChildren(newSM7);

                br.close();
            }catch (IOException e){}
        }
    }
}
