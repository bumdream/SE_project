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
import android.widget.Toast;

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
import dgu.donggukeas_admin.firebase.AndroidPush;
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
    private DatabaseReference mStudentReference, mDeviceReference, mAttendanceReference, mWifiReference;
    private QRCodeReaderView qrCodeReaderView;
    private PointsOverlayView pointsOverlayView;
    private TextView mDecode;
    private Subject mSubject;
    private ArrayList<StudentInfo> mStudents;
    private WifiManager mWifiManager;
    private List<ScanResult> mWifiResults;
    private int mWeeks;
    private String mLastCheckedStudent;
    private Toast mToast;
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

        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        mLastCheckedStudent = "";

        //updateList();
        initReader("CSE4058-02", 1);
        //test_addSubject();
        scanWifi();

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(9000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrCodeReaderView.setBackCamera();

        //send push
        //AndroidPush.sendPushNotification("title","hihihi", "text");

    }

    public void scanWifi() {
        if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {

            // register WiFi scan results receiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    mWifiResults = mWifiManager.getScanResults();
                    final int N = mWifiResults.size();

                    Log.v("#####", "Wi-Fi Scan Results ... Count:" + N);
                    for (int i = 0; i < N; ++i) {
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
        else{
            mWifiManager.setWifiEnabled(true);
            scanWifi();
        }
    }

    public boolean isExistWifi(String BSSID) {
        for (int i = 0; i < mWifiResults.size(); ++i) {
            if (BSSID.equals(mWifiResults.get(i).BSSID)) {
                return true;
            }
        }
        return false;
    }


    //교수가 QR코드를 찍으면 리더기를 초기화 하고 학생들의 정보를 불러옴
    public void initReader(final String subjectCode, final int week) {
        DatabaseReference reference = mDatabase.getReference(getString(R.string.table_subject));
        mWeeks = week;
        mAttendanceReference = mAttendanceReference.child(subjectCode).child(String.valueOf(week));
        //해당 과목 subjectCode 로 mSubject(전연변수) 동기화

        reference.child(subjectCode).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mSubject = dataSnapshot.getValue(Subject.class);
                        setTitle(mSubject.getSubjectName() + "[" + mSubject.getSubjectCode() + "] - " + week + "주차");
                        Log.d("#####", mSubject.getSubjectCode() + mSubject.getSubjectName());


                        //현재 subjectCode 에 해당하는 학생 mStudents(전역변수) 리스트에 추가
                        final List<String> studentsId = mSubject.getListenStudent();

                        for (int i = 0; i < studentsId.size(); i++) {
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
                                    int index = getStudentIndex(student.getStudentId());
                                    if (index != Constants.studentNotFound) {
                                        mStudents.get(index).setStudentName(student.getStudentName());
                                        mAdapter.notifyItemChanged(index);
                                    }

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
                                Log.d("#####",studentDevice.getStudentId()+"/"+studentDevice.getDeviceToken());
                                int index = getStudentIndex(studentDevice.getStudentId());

                                if (index != Constants.studentNotFound) {
                                    mStudents.get(index).setDeviceToken(studentDevice.getDeviceToken());
                                    Log.d("#####",mStudents.get(index).getDeviceToken());
                                    mAdapter.notifyItemChanged(index);
                                }
                            }


                            //TODO 수업을 듣고있는 학생의 디바이스만 참조해야하는데 전체를 참조하고있음
                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                StudentDevice studentDevice = dataSnapshot.getValue(StudentDevice.class);
                                int index = getStudentIndex(studentDevice.getStudentId());
                                if (index != Constants.studentNotFound) {
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
                                if (index != Constants.studentNotFound) {
                                    mStudents.get(index).setAttendanceStatus(as.getAttendanceStatus());
                                    mAdapter.notifyItemChanged(index);
                                }
                                //Log.d("#####","attendance:"+dataSnapshot.getKey());
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                AttendanceStatus as = dataSnapshot.getValue(AttendanceStatus.class);
                                int index = getStudentIndex(as.getStudentId());
                                if (index != Constants.studentNotFound) {
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

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                StudentWifi studentWifi = dataSnapshot.getValue(StudentWifi.class);
                int index = getStudentIndex(studentWifi.getStudentId());
                if (index != Constants.studentNotFound) {
                    //해당 학생 wifi를 리더기 wifi 와 비교
                    if (isExistWifi(studentWifi.getWifiInfo())) {
                        Log.d("#####","동일");
                        mAttendanceReference.child(studentWifi.getStudentId()).setValue(new AttendanceStatus(studentWifi.getStudentId(), Constants.ATTENDANCE_OK));
                    } else {
                        Log.d("#####","없음");
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

    //학생이 QR코드를 찍으면 해당학생에게 wifi 정보를 요청하는 push를 보낸다.
    //여기선 push만 보내고 reference 에서 체크
    public void requestWifiIfo(String studentId) {

        int index = getStudentIndex(studentId);
        if (index == Constants.studentNotFound) {
            showToast(getString(R.string.info_not_this_student_class));
            return;
        }

        StudentInfo studentInfo = mStudents.get(index);


        String deviceToken = studentInfo.getDeviceToken();

        if (deviceToken.equals(Constants.deviceNotRegisterd)) {
            showToast(getString(R.string.info_not_register_device));
            return;
        }


        AndroidPush.sendPushNotification(mSubject.getSubjectCode(),mWeeks,mSubject.getSubjectName() ,studentInfo.getStudentId(),deviceToken);
        showToast(getString(R.string.info_sending_push));
    }

    public int getStudentIndex(String studentId) {
        int index = Constants.studentNotFound;
        for (int i = 0; i < mStudents.size(); i++) {
            if (mStudents.get(i).getStudentId().equals(studentId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        if(mLastCheckedStudent.equals(text))
            return;

        int index = getStudentIndex(text);
        if(index==Constants.studentNotFound)
            return;

        mLastCheckedStudent = text;
        requestWifiIfo(text);


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

    public void showToast(String str){
        if(mToast!=null)
            mToast.cancel();

        mToast = Toast.makeText(this,str,Toast.LENGTH_SHORT);
        mToast.show();
    }
}
