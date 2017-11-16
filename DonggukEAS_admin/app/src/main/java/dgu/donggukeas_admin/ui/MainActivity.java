package dgu.donggukeas_admin.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dgu.donggukeas_admin.R;
import dgu.donggukeas_admin.adapter.AttendanceAdapter;
import dgu.donggukeas_admin.model.StudentInfo;
import dgu.donggukeas_admin.model.firebase.AttendanceStatus;
import dgu.donggukeas_admin.model.firebase.Student;
import dgu.donggukeas_admin.model.firebase.StudentDevice;
import dgu.donggukeas_admin.model.firebase.StudentWifi;
import dgu.donggukeas_admin.model.firebase.Subject;
import dgu.donggukeas_admin.util.Constants;
import dgu.donggukeas_admin.util.PointsOverlayView;


public class MainActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private RecyclerView mRegisterRecyclerView;
    private AttendanceAdapter mAdapter;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DatabaseReference mStudentReference,mDeviceReference,mAttendanceReference,mWifiReference;
    private QRCodeReaderView qrCodeReaderView;
    private PointsOverlayView pointsOverlayView;
    private TextView mDecode;
    private Subject mSubject;
    private ArrayList<StudentInfo> mStudents;
    private WifiManager mWifiManager;
    private List<ScanResult> mWifiResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDecode = (TextView) findViewById(R.id.tv_decode);
        mDatabase = FirebaseDatabase.getInstance();
        mStudentReference = mDatabase.getReference(getString(R.string.table_student));
        mDeviceReference = mDatabase.getReference(getString(R.string.table_device));
        mAttendanceReference = mDatabase.getReference(getString(R.string.table_attendance));
        mWifiReference = mDatabase.getReference(getString(R.string.table_wifi));

        mRegisterRecyclerView = (RecyclerView) findViewById(R.id.rv_register);
        mRegisterRecyclerView.setHasFixedSize(true);
        mRegisterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pointsOverlayView = (PointsOverlayView) findViewById(R.id.points_overlay_view);

        mStudents = new ArrayList<>();
        mAdapter = new AttendanceAdapter(this, mStudents);
        mRegisterRecyclerView.setAdapter(mAdapter);

        mWifiManager =  (WifiManager) getSystemService(WIFI_SERVICE);

        //updateList();
        initReader("CSE4058-02",1);
        //test_addSubject();
        scanWifi();

      qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrCodeReaderView.setBackCamera();

        //send push
        //AndroidPush.sendPushNotification("title","hihihi", "text");

    }

    public void scanWifi() {
        if(mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {

            // register WiFi scan results receiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

            registerReceiver(new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {

                    mWifiResults = mWifiManager.getScanResults();
                    final int N = mWifiResults.size();

                    Log.v("#####", "Wi-Fi Scan Results ... Count:" + N);
                    for(int i=0; i < N; ++i) {
                        Log.v("#####", "  BSSID       =" + mWifiResults.get(i).BSSID);
                        Log.v("#####", "  SSID        =" + mWifiResults.get(i).SSID);
                        Log.v("#####", "  Capabilities=" + mWifiResults.get(i).capabilities);
                        Log.v("#####", "  Frequency   =" + mWifiResults.get(i).frequency);
                        Log.v("#####", "  Level       =" + mWifiResults.get(i).level);
                        Log.v("#####", "---------------");
                    }
                }
            }, filter);

            // start WiFi Scan
            mWifiManager.startScan();
        }
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        mDecode.setText(text);
        pointsOverlayView.setPoints(points);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //qrCodeReaderView.stopCamera();
    }

    //교수가 QR코드를 찍으면 리더기를 초기화 하고 학생들의 정보를 불러옴
    public void initReader(final String subjectCode,final int week){
        DatabaseReference reference = mDatabase.getReference(getString(R.string.table_subject));
        mAttendanceReference = mAttendanceReference.child(subjectCode).child(String.valueOf(week));
        //해당 과목 subjectCode 로 mSubject(전연변수) 동기화

        reference.child(subjectCode).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mSubject = dataSnapshot.getValue(Subject.class);
                        setTitle(mSubject.getSubjectName()+"["+mSubject.getSubjectCode()+"] - "+week+"주차");
                        Log.d("#####",mSubject.getSubjectCode()+mSubject.getSubjectName());


                        //현재 subjectCode 에 해당하는 학생 mStudents(전역변수) 리스트에 추가
                        List<String> studentsId = mSubject.getListenStudent();

                        for(int i=0;i<studentsId.size();i++) {
                            mStudents.add(new dgu.donggukeas_admin.model.StudentInfo(studentsId.get(i)));

                        }

                        mAdapter.notifyDataSetChanged();
                        //student 테이블로부터 조인.

                        //학생 이름을 학번으로 부터 불러온다
                        mStudentReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    Student student = child.getValue(Student.class);
                                    int changedIndex = -1;
                                    for(int i=0;i<mStudents.size();i++){
                                        if(mStudents.get(i).getStudentId().equals(student.getStudentId())){
                                            mStudents.get(i).setStudentName(student.getStudentName());
                                            changedIndex = i;
                                            break;
                                        }
                                    }
                                    if(changedIndex!=-1)
                                    mAdapter.notifyItemChanged(changedIndex);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        mDeviceReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                StudentDevice studentDevice = dataSnapshot.getValue(StudentDevice.class);

                                int index = getStudentIndex(studentDevice.getDeviceToken());

                                if(index!=-1) {
                                    mStudents.get(index).setDeviceToken(studentDevice.getDeviceToken());
                                    mAdapter.notifyItemChanged(index);
                                }
                            }


                            //TODO 수업을 듣고있는 학생의 디바이스만 참조해야하는데 전체를 참조하고있음
                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                StudentDevice studentDevice = dataSnapshot.getValue(StudentDevice.class);
                                int index = getStudentIndex(studentDevice.getStudentId());
                                if(index != -1) {
                                    mStudents.get(index).setDeviceToken(studentDevice.getDeviceToken());
                                    mAdapter.notifyItemChanged(index);
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
                        mAttendanceReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                AttendanceStatus as = dataSnapshot.getValue(AttendanceStatus.class);
                                int index = getStudentIndex(as.getStudentId());
                                if(index!=-1){
                                    mStudents.get(index).setAttendanceStatus(as.getAttendanceStatus());
                                    mAdapter.notifyItemChanged(index);
                                }
                                //Log.d("#####","attendance:"+dataSnapshot.getKey());
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                AttendanceStatus as = dataSnapshot.getValue(AttendanceStatus.class);
                                int index = getStudentIndex(as.getStudentId());
                                if(index!=-1){
                                    mStudents.get(index).setAttendanceStatus(as.getAttendanceStatus());
                                    mAdapter.notifyItemChanged(index);
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

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        mWifiReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //StudentWifi studentWifi = dataSnapshot.getValue(StudentWifi.class);
            }


            //TODO 이제 wifi 로 출석하는거 했으니, 리더기에서 학생 QR 찍으면 푸쉬보내는거 하기
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                StudentWifi studentWifi = dataSnapshot.getValue(StudentWifi.class);
                int index = getStudentIndex(studentWifi.getStudentId());
                if(index != -1){
                    //해당 학생 wifi를 리더기 wifi 와 비교
                    if(isExistWifi(studentWifi.getWifiInfo())){
                        mAttendanceReference.child(studentWifi.getStudentId()).setValue(new AttendanceStatus(studentWifi.getStudentId(), Constants.ATTENDANCE_OK));
                    }
                    else{
                        mAttendanceReference.child(studentWifi.getStudentId()).setValue(new AttendanceStatus(studentWifi.getStudentId(), Constants.ATTENDANCE_ABSENCE));
                    }
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




    }

    public int getStudentIndex(String studentId){
        int index = -1;
        for (int i = 0; i < mStudents.size(); i++) {
            if (mStudents.get(i).getStudentId().equals(studentId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public boolean isExistWifi(String BSSID){
        for(int i = 0; i < mWifiResults.size(); ++i) {
            if(BSSID.equals(mWifiResults.get(i).BSSID)){
                return true;
            }
        }
        return false;
    }
}
