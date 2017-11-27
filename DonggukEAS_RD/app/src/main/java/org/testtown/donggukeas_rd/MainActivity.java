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
        AssetManager assetManager = getApplication().getAssets();
        int stuNo=48;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(assetManager.open("DBInfo.txt")));
            String str = null;

            while ((str = br.readLine())!= null){

                String[] numbers = str.split(" ");
                stuModel sm = new stuModel(numbers[1],"-1","-1",0,"1234",numbers[0]);

                Map<String, Object> newSM = new HashMap<>();
                Map<String, Object> csAuth = sm.stu_auth_Map();

                newSM.put(sm.studentId, csAuth);
                reference.child("STUDENT_AUTH").updateChildren(newSM);

                Map<String, Object> newSM1 = new HashMap<>();
                Map<String, Object> csDev = sm.stu_dev_Map();
                newSM1.put(sm.studentId, csDev);
                reference.child("STUDENT_DEVICE").updateChildren(newSM1);

                Map<String, Object> newSM2 = new HashMap<>();
                Map<String, Object> csWifi = sm.stu_wifi_Map();
                newSM2.put(sm.studentId, csWifi);
                reference.child("STUDENT_WIFI").updateChildren(newSM2);

                Map<String, Object> newSM3 = new HashMap<>();
                Map<String, Object> csMap = sm.stu_Map();
                newSM3.put(sm.studentId, csMap);
                reference.child("STUDENT").updateChildren(newSM3);

                Map<String, Object> newSM4 = new HashMap<>();
                Map<String, Object> csAtd = sm.stu_atd_Map();
                newSM4.put(sm.studentId, csAtd);
                for(int index=1;index<=16;index++) {
                    reference.child("STUDENT_ATTENDANCE").child("CSE4058-02").child(String.valueOf(index)).updateChildren(newSM4);
                    reference.child("STUDENT_ATTENDANCE").child("CSE2017-01").child(String.valueOf(index)).updateChildren(newSM4);
                }
                Map<String, Object> newSM5 = new HashMap<>();

                newSM5.put(String.valueOf(stuNo), sm.studentId);
                reference.child("SUBJECT").child("CSE4058-02").child("listenStudent").updateChildren(newSM5);
                reference.child("SUBJECT").child("CSE2017-01").child("listenStudent").updateChildren(newSM5);





                stuNo-=1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null)try {
                Map<String, Object> newSM6 = new HashMap<>();
                newSM6.put("days", "0101000");
                reference.child("SUBJECT").child("CSE4058-02").updateChildren(newSM6);
                newSM6.put("subjectCode", "CSE4058-02");
                reference.child("SUBJECT").child("CSE4058-02").updateChildren(newSM6);
                newSM6.put("subjectName", "소프트웨어공학개론");
                reference.child("SUBJECT").child("CSE4058-02").updateChildren(newSM6);

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
