package dgu.donggukeas_client.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import dgu.donggukeas_client.R;
import dgu.donggukeas_client.model.firebase.AttendanceStatus;
import dgu.donggukeas_client.model.firebase.StudentWifi;
import dgu.donggukeas_client.util.Constants;

public class SendWifiActivity extends AppCompatActivity {

    private TextView mSubName,mSubCode;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mWifiReference,mAttendanceReference;
    private WifiManager mWifiManager;
    private LinearLayout mLoadingView;
    private String mStudentId;
    private Toast mToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_wifi);
        mSubName = (TextView)findViewById(R.id.tv_subject_name);
        mSubCode = (TextView)findViewById(R.id.tv_subject_code) ;
        mLoadingView = (LinearLayout)findViewById(R.id.ll_loading);
        Intent i = getIntent();
        mStudentId = i.getStringExtra("studentId");
        String subjectCode = i.getStringExtra("subjectCode");
        String subjectName = i.getStringExtra("subjectName");
        int week = i.getIntExtra("week",-1);


        Log.d("#####",subjectCode+"/"+subjectName+"/"+week);
        mSubName.setText(subjectName);
        mSubCode.setText("["+subjectCode+"]");

        mDatabase = FirebaseDatabase.getInstance();
        mAttendanceReference = mDatabase.getReference(getString(R.string.table_attendance))
                .child(subjectCode)
                .child(String.valueOf(week));

        mAttendanceReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                AttendanceStatus as = dataSnapshot.getValue(AttendanceStatus.class);
                if(as.getStudentId().equals(mStudentId)){
                    switch(as.getAttendanceStatus()){
                        case Constants.ATTENDANCE_OK:
                            showToast(getString(R.string.info_attendance_ok));
                            break;

                        case Constants.ATTENDANCE_ABSENCE:
                            showToast(getString(R.string.info_attendance_no));
                            break;
                    }
                    finish();

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mWifiReference = mDatabase.getReference(getString(R.string.table_wifi)).child(mStudentId);
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        sendWifiInfo();
    }
    public void sendWifiInfo() {
        if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {

            // register WiFi scan results receiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    List<ScanResult> wifiResult = mWifiManager.getScanResults();
                    final int N = wifiResult.size();
                    mLoadingView.setVisibility(View.INVISIBLE);
                    mWifiReference.setValue(new StudentWifi(mStudentId,wifiResult.get(0).BSSID));

                }
            }, filter);

            // start WiFi Scan
            mWifiManager.startScan();
            mLoadingView.setVisibility(View.VISIBLE);
        }
        else{
            mWifiManager.setWifiEnabled(true);
            sendWifiInfo();
        }
    }


    public void showToast(String str){
        if(mToast!=null)
            mToast.cancel();

        mToast = Toast.makeText(this,str,Toast.LENGTH_SHORT);
        mToast.show();
    }

}
