package dgu.donggukeas_admin.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PointF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dgu.donggukeas_admin.R;
import dgu.donggukeas_admin.adapter.AttendanceAdapter;
import dgu.donggukeas_admin.firebase.AndroidPush;
import dgu.donggukeas_admin.model.StudentInfo;
import dgu.donggukeas_admin.model.firebase.AttendanceStatus;
import dgu.donggukeas_admin.model.firebase.RunAwayStudent;
import dgu.donggukeas_admin.model.firebase.RunawayActive;
import dgu.donggukeas_admin.model.firebase.Student;
import dgu.donggukeas_admin.model.firebase.StudentDevice;
import dgu.donggukeas_admin.model.firebase.StudentWifi;
import dgu.donggukeas_admin.model.firebase.Subject;
import dgu.donggukeas_admin.util.Constants;
import dgu.donggukeas_admin.util.PointsOverlayView;
import dgu.donggukeas_admin.util.Utils;


public class MainActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private RecyclerView mAttendanceView;
    private AttendanceAdapter mAdapter;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private DatabaseReference mStudentReference, mDeviceReference, mAttendanceReference, mWifiReference;
    private DatabaseReference mRunawayActiveRef, mRunawayStudentRef;
    private QRCodeReaderView qrCodeReaderView;
    private PointsOverlayView pointsOverlayView;
    private TextView mDecode;
    private Subject mSubject;
    private LinearLayout mEmptyView;
    private ArrayList<StudentInfo> mStudents;
    private ArrayList<RunAwayStudent> mSuspects;
    private WifiManager mWifiManager;
    private List<ScanResult> mWifiResults;


    private int mWeeks;
    private String mLastCheckedStudent;
    private Toast mToast;
    private boolean mIsCheckingMode;
    private boolean mIsEndAttendance;//출석이 끝났을떄 이게 true 로바뀜

    private boolean mQRflag; //QR이 한번에 여러개 읽히는거 방지

    private TextView mTitle;
    private FloatingActionButton mFab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDecode = (TextView) findViewById(R.id.tv_decode);
        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mEmptyView = (LinearLayout)findViewById(R.id.empty_view);
        mTitle = (TextView)findViewById(R.id.tv_title);

        mDatabase = FirebaseDatabase.getInstance();
        mStudentReference = mDatabase.getReference(getString(R.string.table_student));
        mDeviceReference = mDatabase.getReference(getString(R.string.table_device));
        mAttendanceReference = mDatabase.getReference(getString(R.string.table_attendance));
        mWifiReference = mDatabase.getReference(getString(R.string.table_wifi));

        mRunawayActiveRef = mDatabase.getReference(getString(R.string.table_runaway_active));
        mRunawayStudentRef = mDatabase.getReference(getString(R.string.table_runaway_student));


        mAttendanceView = (RecyclerView) findViewById(R.id.rv_register);
        mAttendanceView.setHasFixedSize(true);
        mAttendanceView.setLayoutManager(new LinearLayoutManager(this));
        pointsOverlayView = (PointsOverlayView) findViewById(R.id.points_overlay_view);

        mStudents = new ArrayList<>();
        mSuspects = new ArrayList<>();
        mAdapter = new AttendanceAdapter(this, mStudents);
        mAttendanceView.setAdapter(mAdapter);

        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);

        mLastCheckedStudent = "";
        mIsCheckingMode = false;
        mIsEndAttendance = true;
        mQRflag = false;
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //수업 종료
                if(!mIsEndAttendance){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setMessage(R.string.info_stop_attendance)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener(){
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton){
                                    mIsEndAttendance = true;
                                    mFab.setImageResource(R.drawable.ic_out);
                                    mRunawayActiveRef.child(mSubject.getSubjectCode())
                                            .setValue(new RunawayActive(Constants.SUBJECT_ATTENDANCE_END));
                                    showToast(getString(R.string.info_stop_attendance_toast));
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                                // 취소 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton){
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setMessage(R.string.info_stop_reader)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener(){
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton){
                                    stopReader();
                                    mLastCheckedStudent = "";
                                    mFab.setImageResource(R.drawable.ic_teacher);
                                    showToast(getString(R.string.info_reader_reset));
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                                // 취소 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton){
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();
                }
            }
        });
        mFab.setVisibility(View.INVISIBLE);
        mTitle.setText(getString(R.string.info_no_subject));


        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(1000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrCodeReaderView.setBackCamera();

    }



    //교수가 QR코드를 찍으면 리더기를 초기화 하고 학생들의 정보를 불러옴
    public void initReader(final String subjectCode, final int week) {
        Log.d("#####","initReader");
        scanWifi();

        DatabaseReference reference = mDatabase.getReference(getString(R.string.table_subject));
        mWeeks = week;
        //해당 과목 subjectCode 로 mSubject(전연변수) 동기화
        reference.child(subjectCode).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mSubject = dataSnapshot.getValue(Subject.class);

                        mQRflag = false;
                        if(mSubject == null)
                            return;

                        mIsEndAttendance = false;
                        mFab.setVisibility(View.VISIBLE);
                        mEmptyView.setVisibility(View.INVISIBLE);
                        mTitle.setText(mSubject.getSubjectName() + "[" + mSubject.getSubjectCode() + "] - " + week + "주");

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
                        mDeviceReference.addChildEventListener(deviceListener);
                        mAttendanceReference.child(subjectCode).child(String.valueOf(week)).addChildEventListener(attendanceListener);
                        showToast(mSubject.getSubjectName()+"과목이 설정되었습니다.");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        mWifiReference.addChildEventListener(wifiListener);
        mRunawayActiveRef.child(subjectCode).addChildEventListener(runawayListener);

        mRunawayActiveRef.child(subjectCode).setValue(new RunawayActive(Constants.SUBJECT_ATTENDANCE_ACTIVE));
    }
    private void stopReader() {
        mIsEndAttendance = true;
        mFab.setVisibility(View.INVISIBLE);
        mEmptyView.setVisibility(View.VISIBLE);
        mStudents.clear();
        mSuspects.clear();
        mAdapter.notifyDataSetChanged();
        mDeviceReference.removeEventListener(deviceListener);
        mAttendanceReference.child(mSubject.getSubjectCode()).child(String.valueOf(mWeeks)).removeEventListener(attendanceListener);
        mWifiReference.removeEventListener(wifiListener);
        mRunawayActiveRef.child(mSubject.getSubjectCode()).removeEventListener(runawayListener);
        mRunawayActiveRef.child(mSubject.getSubjectCode()).setValue(new RunawayActive(Constants.SUBJECT_END));
        unregisterReceiver(broadcastReceiver);
        mSubject = null;
        mTitle.setText(getString(R.string.info_no_subject));
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


        AndroidPush.sendPushNotification(mSubject.getSubjectCode(),
                Constants.ATTENDANCE_REQUEST,
                mWeeks,
                mSubject.getSubjectName(),
                studentInfo.getStudentId(),
                deviceToken);
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

    public int getRunAwayStudentIndex(String studentId) {
        int index = Constants.studentNotFound;
        for (int i = 0; i < mSuspects.size(); i++) {
            if (mSuspects.get(i).getStudentId().equals(studentId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override

    public void onQRCodeRead(String text, PointF[] points) {
        if(!mQRflag){
            mQRflag = true;
            if(mSubject == null){
                //Log.d("#####","일루왔당");
                mDatabase.getReference(getString(R.string.table_subject))
                        .child(text)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //찍는과목이 있는 과목일때
                                if(dataSnapshot!= null){
                                    mIsEndAttendance = false;
                                    initReader(dataSnapshot.getKey(), Utils.getWeek());

                                }
                                else{//찍은 과목이 없는과목일때
                                    mQRflag = false;
                                    //Log.d("#####","없는과목");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            else{
                mQRflag = false;
                if (mLastCheckedStudent.equals(text)){
                    return;

                }

                int index = getStudentIndex(text);
                if (index == Constants.studentNotFound) {
                    return;
                }

                mLastCheckedStudent = text;
                requestWifiIfo(text);


            }
            pointsOverlayView.setPoints(points);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    public void scanWifi() {
        if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {

            // register WiFi scan results receiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

            registerReceiver(broadcastReceiver,filter);

            // start WiFi Scan
            mWifiManager.startScan();
        } else {
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

    public void showToast(String str) {
        if (mToast != null)
            mToast.cancel();

        mToast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static String getDateFromMilli(Long milli) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milli);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return new String(sdf.format(calendar.getTime()));
    }

    ChildEventListener deviceListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            StudentDevice studentDevice = dataSnapshot.getValue(StudentDevice.class);
            Log.d("#####", studentDevice.getStudentId() + "/" + studentDevice.getDeviceToken());
            int index = getStudentIndex(studentDevice.getStudentId());

            if (index != Constants.studentNotFound) {
                mStudents.get(index).setDeviceToken(studentDevice.getDeviceToken());
                Log.d("#####", mStudents.get(index).getDeviceToken());
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
    };
    ChildEventListener attendanceListener = new ChildEventListener() {
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
                mAttendanceView.smoothScrollToPosition(index);
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
    };
    ChildEventListener wifiListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            //StudentWifi studentWifi = dataSnapshot.getValue(StudentWifi.class);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
             final StudentWifi studentWifi = dataSnapshot.getValue(StudentWifi.class);
            int index = getStudentIndex(studentWifi.getStudentId());
            if (index != Constants.studentNotFound) {
                //해당 학생 wifi를 리더기 wifi 와 비교

                mAttendanceReference.child(mSubject.getSubjectCode())
                        .child(String.valueOf(mWeeks))
                        .child(studentWifi.getStudentId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        AttendanceStatus as = dataSnapshot.getValue(AttendanceStatus.class);

                            //미처리인 경우만 출석 처리
                            if (!mIsEndAttendance) {
                                //출석처리중
                                if(as.getAttendanceStatus() == Constants.ATTENDANCE_NONE){
                                    if (isExistWifi(studentWifi.getWifiInfo())) {
                                        Log.d("#####", "출석중:(" + studentWifi.getStudentId() + ") 와이파이 동일");

                                        mAttendanceReference.child(mSubject.getSubjectCode())
                                                .child(String.valueOf(mWeeks))
                                                .child(studentWifi.getStudentId())
                                                .setValue(new AttendanceStatus(studentWifi.getStudentId(), Constants.ATTENDANCE_OK));



                                    } else {
                                        Log.d("#####", "출석중(" + studentWifi.getStudentId() + "): 와이파이 다름");
                                        mAttendanceReference.child(mSubject.getSubjectCode())
                                                .child(String.valueOf(mWeeks))
                                                .child(studentWifi.getStudentId())
                                                .setValue(new AttendanceStatus(studentWifi.getStudentId(), Constants.ATTENDANCE_NONE));
                                    }
                                    //학생에게 알려주기
                                    AndroidPush.sendPushNotification(mSubject.getSubjectCode(),
                                            Constants.ATTENDANCE_RESULT,
                                            mWeeks,
                                            mSubject.getSubjectName(),
                                            studentWifi.getStudentId(),
                                            mStudents.get(getStudentIndex(studentWifi.getStudentId())).getDeviceToken());
                                }
                                else{
                                    showToast(getString(R.string.info_already_done));
                                }

                            } else {
                                //출석처리가 끝난경우
                                if (mIsCheckingMode) {
                                    //출튀체크 모드인경우
                                    if (isExistWifi(studentWifi.getWifiInfo())) {
                                        Log.d("#####", "출석처리끝남(출튀체크모드)(" + studentWifi.getStudentId() + "): 와이파이 동일");
                                        //교실에 있는 학생은 출튀 리스트에서 빼준다
                                        int runawayIndex = getRunAwayStudentIndex(studentWifi.getStudentId());
                                        if (runawayIndex != Constants.studentNotFound) {
                                            mSuspects.remove(runawayIndex);
                                        }

                                    } else {
                                        Log.d("#####", "출석처리끝남(출튀체크모드)(" + studentWifi.getStudentId() + "): 와이파이 다름");

                                        //교실에 없는 학생은 출튀리스트에서 갱신 시간을 넣어준다.
                                        int runawayIndex = getRunAwayStudentIndex(studentWifi.getStudentId());
                                        if (runawayIndex != Constants.studentNotFound) {
                                            mSuspects.remove(runawayIndex);
                                            mDatabase.getReference(getString(R.string.table_runaway_student))
                                                    .child(mSubject.getSubjectCode())
                                                    .child(studentWifi.getStudentId())
                                                    .setValue(new RunAwayStudent(studentWifi.getStudentId(), getDateFromMilli(System.currentTimeMillis())));
                                        }
                                    }

                                }
                                else{
                                    if(as.getAttendanceStatus()==Constants.ATTENDANCE_NONE){
                                        if (isExistWifi(studentWifi.getWifiInfo())) {
                                            Log.d("#####", "출석처리끝남(일반모드):와이파이동일(" + studentWifi.getStudentId() + ")");
                                            mAttendanceReference.child(mSubject.getSubjectCode())
                                                    .child(String.valueOf(mWeeks))
                                                    .child(studentWifi.getStudentId())
                                                    .setValue(new AttendanceStatus(studentWifi.getStudentId(), Constants.ATTENDANCE_LATE));
                                        }
                                        else{
                                            Log.d("#####", "출석처리끝남(일반모드):와이파이다름(" + studentWifi.getStudentId() + ")");
                                            //와이파이 다른경우 미처리.
                                            mAttendanceReference.child(mSubject.getSubjectCode())
                                                    .child(String.valueOf(mWeeks))
                                                    .child(studentWifi.getStudentId())
                                                    .setValue(new AttendanceStatus(studentWifi.getStudentId(), Constants.ATTENDANCE_NONE));
                                        }

                                        //결과를 학생에게 보내준다
                                        AndroidPush.sendPushNotification(mSubject.getSubjectCode(),
                                                Constants.ATTENDANCE_RESULT,
                                                mWeeks,
                                                mSubject.getSubjectName(),
                                                studentWifi.getStudentId(),
                                                mStudents.get(getStudentIndex(studentWifi.getStudentId())).getDeviceToken());
                                    }
                                    else{

                                        showToast(getString(R.string.info_already_done));
                                    }

                                }


                            }
                        }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




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
    };
    ChildEventListener runawayListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildChanged(final DataSnapshot dataSnapshot, String s) {
            int value = dataSnapshot.getValue(Integer.class);
            if(value == Constants.SUBJECT_ATTENDANCE_RUNAWAY_ACTIVE){
                if(mIsEndAttendance){
                    mIsCheckingMode = true;
                    Log.d("#####", "aaa1:");
                    //값들을 다 보내준다.
                    //그리고 db값도 false 로 바꾼다
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                Log.d("#####", "aaa1:thread진입");
                                for (int i = 0; i < Constants.RUN_AWAY_WAITING_TIME; i++) {
                                    Thread.sleep(1000);
                                    Log.d("#####", "timeLeft:" + (Constants.RUN_AWAY_WAITING_TIME - (i + 1)));
                                }

                                mIsCheckingMode = false;

                                //나머지 정보들이 안온 용의자들은 runAwayTable 에 넣는다
                                for (int i = 0; i < mSuspects.size(); i++) {
                                    RunAwayStudent runAwayStudent = mSuspects.get(i);
                                    mDatabase.getReference(getString(R.string.table_runaway_student))
                                            .child(mSubject.getSubjectCode())
                                            .child(runAwayStudent.getStudentId())
                                            .setValue(runAwayStudent);
                                }
                                dataSnapshot.getRef().setValue(new Integer(Constants.SUBJECT_ATTENDANCE_RUNAWAY_END));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                    // 현재 출석한 학생을 대상으로 푸쉬메세지를 전송한다.
                    // 리스너를 통해 학생들이 들어왔는지 확인하고, 학생들이 보낸정보가 현재 와이파이와 일치하면 괜찮음.
                    // 아니면 출튀여부자로 보낸다.

                    Log.d("#####", "aaa2:");
                    mSuspects = new ArrayList<RunAwayStudent>();

                    //현재 출석한 학생들중 출석이거나 지각한 학생을 대상으로 출튀 메세지를 보낸다.
                    for (int i = 0; i < mStudents.size(); i++) {
                        StudentInfo student = mStudents.get(i);
                        if (student.getAttendanceStatus() == Constants.ATTENDANCE_OK ||
                                student.getAttendanceStatus() == Constants.ATTENDANCE_LATE) {
                            mSuspects.add(new RunAwayStudent(student.getStudentId()));
                            //대상자에게 출튀 여부 메세지를 보낸다.

                        }

                    }
                    for(int j=0;j<mSuspects.size();j++){
                        AndroidPush.sendPushNotification(mSubject.getSubjectCode(),
                                Constants.RUNAWAY_REQUEST,
                                mWeeks,
                                mSubject.getSubjectName(),
                                mSuspects.get(j).getStudentId(),
                                mStudents.get(getStudentIndex(mSuspects.get(j).getStudentId())).getDeviceToken());

                    }
                }
                else{
                    //dataSnapshot.getRef().setValue(new Integer(Constants.SUBJECT_ATTENDANCE_ACTIVE));
                    showToast(getString(R.string.info_search_deny));
                }
            }
            Log.d("#####", "value:" + value);
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
    };
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            mWifiResults = mWifiManager.getScanResults();
            final int N = mWifiResults.size();

            Log.d("#####","wifi load ended!!!");
            Log.v("#####", "Wi-Fi Scan Results ... Count:" + N);

        }


    };

    public void init_qrString(View view) {
        mLastCheckedStudent ="";
    }
}


