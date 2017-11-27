package dgu.donggukeas_prof.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dgu.donggukeas_prof.R;
import dgu.donggukeas_prof.adapter.AttendanceAdapter;
import dgu.donggukeas_prof.model.StudentInfo;
import dgu.donggukeas_prof.model.firebase.AttendanceStatus;
import dgu.donggukeas_prof.model.firebase.RunawayActive;
import dgu.donggukeas_prof.model.firebase.Student;
import dgu.donggukeas_prof.model.firebase.Subject;
import dgu.donggukeas_prof.util.Constants;
import dgu.donggukeas_prof.util.SemesterDate;

public class AttendanceActivity extends AppCompatActivity {
    private int mSelectedWeek, mCurrentWeek;
    private int mStartDay =0, mStartMonth =0;
    private int mEndDay =0, endMonth=0;
    private boolean mIsProgress = false;

    private ImageButton ibLeft, ibRight;
    private TextView subjectCode, subjectName, subjectWeek, subjectWeekDays;
    private FloatingActionButton fabQR, fabRA; //둥근 메뉴 버튼

    private Calendar mSemesterStart = null, mCurrentDate = null, mSemesterEnd = null;
    private Calendar mWeekStartDay = null, mWeekEndDay = null;

    private RecyclerView mAttendanceRecyclerView;
    private Subject mSubject;
    private ArrayList<StudentInfo> mStudents;
    private AttendanceAdapter mAdapter;
    private LinearLayout mProgressLayout;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mStudentReference, mAttendanceReference, mRunawayActiveReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        subjectCode = (TextView)findViewById(R.id.tv_a_subjectCode);
        subjectCode.setText(getIntent().getStringExtra(getString(R.string.extra_subject_code)));
        subjectName = (TextView)findViewById(R.id.tv_a_subjectName);
        subjectName.setText(getIntent().getStringExtra(getString(R.string.extra_subject_name)));
        //데이터베이스 참조
        mDatabase = FirebaseDatabase.getInstance();
        mStudentReference = mDatabase.getReference(getString(R.string.table_student));
        mAttendanceReference = mDatabase.getReference(getString(R.string.table_attendance));

        mAttendanceRecyclerView = (RecyclerView)findViewById(R.id.rv_attendance);
        mAttendanceRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mAttendanceRecyclerView.setLayoutManager(llm);

        mStudents = new ArrayList<>();
        mAdapter = new AttendanceAdapter(this, mStudents, subjectCode.getText().toString());
        mAttendanceRecyclerView.setAdapter(mAdapter);

        mSemesterStart = Calendar.getInstance();
        mSemesterEnd = Calendar.getInstance();
        mWeekStartDay = Calendar.getInstance();
        mWeekEndDay = Calendar.getInstance();

        mSelectedWeek = getWeek();
        mCurrentWeek = mSelectedWeek;
        updateWeekInfo(mSelectedWeek);

        //QR 코드 조회 (교수가 선택한 강의 관련 정보가 QR 코드로 생성)
        fabQR = (FloatingActionButton)findViewById(R.id.fab_qr);
        fabQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), QrActivity.class);
                i.putExtra("QRCODE",subjectCode.getText().toString());
                //Log.d("#####",subjectCode.getText().toString());
                startActivity(i);
            }
        });

        //출튀 체크 기능
        fabRA = (FloatingActionButton)findViewById(R.id.fab_runaway);
        fabRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mSubjectCode = subjectCode.getText().toString();
                mIsProgress = true;

                FirebaseDatabase mDatabase;
                mDatabase = FirebaseDatabase.getInstance();
                mRunawayActiveReference = mDatabase.getReference("RUNAWAY_ACTIVE"); //RUNAWAY_ACTIVE 테이블 참조
                mRunawayActiveReference.child(mSubjectCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        RunawayActive ra = snapshot.getValue(RunawayActive.class);
                        //출튀 기능은 리더기에서 현재 강의의 출석 체크가 종료되었을 때 수행 가능
                        if(ra.getIsActive() == Constants.SUBJECT_ATTENDANCE_END) {
                            //Toast.makeText(getApplicationContext(), "출석완료", Toast.LENGTH_LONG).show();
                            RunawayActive mRunawayActive = new RunawayActive(Constants.SUBJECT_ATTENDANCE_RUNAWAY_ACTIVE); //리더기에게 출튀 관련 액션을 요청

                            Map<String, Object> mRunawayValue = mRunawayActive.toMap();
                            Map<String, Object> mNewRunaway = new HashMap<>();
                            mNewRunaway.put(mSubjectCode,mRunawayValue);
                            mRunawayActiveReference.updateChildren(mNewRunaway);

                            mProgressLayout = (LinearLayout)findViewById(R.id.ll_pb);
                            mProgressLayout.setVisibility(View.VISIBLE);

                            mRunawayActiveReference.child(mSubjectCode).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {}

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    int value = dataSnapshot.getValue(Integer.class);
                                    if(value == Constants.SUBJECT_ATTENDANCE_RUNAWAY_END){
                                        mIsProgress = false;
                                        mProgressLayout.setVisibility(View.INVISIBLE);

                                        Intent i = new Intent(getApplicationContext(), RunawayActivity.class);
                                        i.putExtra(getString(R.string.extra_ra_subject_code),subjectCode.getText().toString());
                                        i.putExtra(getString(R.string.extra_ra_subject_week), mCurrentWeek);
                                        startActivity(i);
                                    }
                                    //Log.d("#####", "value:" + value);
                                }
                                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }else
                             Toast.makeText(getApplicationContext(),"출석이 아직 종료되지 않았습니다.\n잠시 후 다시 시도하세요.",Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        });

        subjectWeek = (TextView)findViewById(R.id.tv_a_week);
        subjectWeek.setText(mSelectedWeek +"주차");
        subjectWeekDays = (TextView)findViewById(R.id.tv_weekdays);
        subjectWeekDays.setText(mStartMonth +"/"+ mStartDay +" ~ "+endMonth+"/"+ mEndDay);

        //출석을 조회하고자하는 주차를 변경 가능한 버튼
        ibLeft = (ImageButton)findViewById(R.id.ib_a_left);
        ibRight = (ImageButton)findViewById(R.id.ib_a_right) ;

        //주차에 따라 버튼 활성화 여부 결정
        if(mSelectedWeek == 1) {
            ibLeft.setClickable(false);
            ibLeft.setImageResource(R.drawable.ic_left_disabled);
        }
        else if(mSelectedWeek == SemesterDate.SEMESTER_MAX_WEEKS) {
            ibRight.setClickable(false);
            ibRight.setImageResource(R.drawable.ic_right_disabled);
        }
        else{
            ibLeft.setClickable(true);
            ibLeft.setImageResource(R.drawable.ic_left);
            ibRight.setClickable(true);
            ibRight.setImageResource(R.drawable.ic_right);
        }

        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedWeek--;
                mWeekStartDay.add(Calendar.DAY_OF_YEAR, -7);
                updateWeekInfo(mSelectedWeek);

                subjectWeek.setText(mSelectedWeek +"주차");
                subjectWeekDays.setText(mStartMonth +"-"+ mStartDay +" ~ "+endMonth+"-"+ mEndDay);

                if(mSelectedWeek == 1) {
                    ibLeft.setImageResource(R.drawable.ic_left_disabled);
                    ibLeft.setClickable(false);
                }
                else {
                    ibLeft.setImageResource(R.drawable.ic_left);
                    ibRight.setImageResource(R.drawable.ic_right);
                    ibLeft.setClickable(true);
                    ibRight.setClickable(true);
                }
                mAttendanceReference = mDatabase.getReference(getString(R.string.table_attendance));

                mStudents = new ArrayList<>();
                mAdapter = new AttendanceAdapter(AttendanceActivity.this, mStudents, subjectCode.getText().toString());
                mAttendanceRecyclerView.setAdapter(mAdapter);

                updateStudentList(subjectCode.getText().toString(), mSelectedWeek);
            }
        });

        ibRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedWeek++;
                mWeekStartDay.add(Calendar.DAY_OF_YEAR, 7);
                updateWeekInfo(mSelectedWeek);

                subjectWeek.setText(mSelectedWeek +"주차");
                subjectWeekDays.setText(mStartMonth +"-"+ mStartDay +" ~ "+endMonth+"-"+ mEndDay);

                if(mSelectedWeek == SemesterDate.SEMESTER_MAX_WEEKS) {
                    ibRight.setImageResource(R.drawable.ic_right_disabled);
                    ibRight.setClickable(false);
                }
                else {
                    ibLeft.setImageResource(R.drawable.ic_left);
                    ibRight.setImageResource(R.drawable.ic_right);
                    ibLeft.setClickable(true);
                    ibRight.setClickable(true);
                }
                mAttendanceReference = mDatabase.getReference(getString(R.string.table_attendance));

                mStudents = new ArrayList<>();
                mAdapter = new AttendanceAdapter(AttendanceActivity.this, mStudents, subjectCode.getText().toString());
                mAttendanceRecyclerView.setAdapter(mAdapter);

                updateStudentList(subjectCode.getText().toString(), mSelectedWeek);
            }
        });
        updateStudentList(subjectCode.getText().toString(), mSelectedWeek);
    }

    @Override
    public void onBackPressed() {
        if(mIsProgress == true)
        {
            //출튀 체크 중이면 뒤로가기 비활성화
        }
        else {
            super.onBackPressed();
        }
    }

    //현재 날짜가 학기 시작 몇 주차인지 계산하는 메소드
    public int getWeek()
    {
        int startDate, todayDate, weekNo;
        mSemesterStart.set(Calendar.YEAR, SemesterDate.SEMESTER_START_YEAR);
        mSemesterStart.set(Calendar.MONTH, SemesterDate.SEMESTER_START_MONTH);
        mSemesterStart.set(Calendar.DATE, SemesterDate.SEMESTER_START_DATE);

        startDate = mSemesterStart.get(Calendar.DAY_OF_YEAR);
        mCurrentDate = Calendar.getInstance();
        todayDate = mCurrentDate.get(Calendar.DAY_OF_YEAR);

        mSemesterEnd.set(Calendar.YEAR, SemesterDate.SEMESTER_END_YEAR);
        mSemesterEnd.set(Calendar.MONTH, SemesterDate.SEMESTER_END_MONTH);
        mSemesterEnd.set(Calendar.DATE, SemesterDate.SEMESTER_END_DATE);

        weekNo = (todayDate-startDate)/7 + 1;

        if(weekNo>SemesterDate.SEMESTER_MAX_WEEKS) //학기는 최대 16주차
            return SemesterDate.SEMESTER_MAX_WEEKS;
        else
            return weekNo;
    }

    //현재 주차의 시작 날짜와 마지막 날짜를 계산하는 메소드
    public void updateWeekInfo(int currentWeek)
    {
        mWeekStartDay.set(Calendar.YEAR, mSemesterStart.get(Calendar.YEAR));
        mWeekStartDay.set(Calendar.MONTH, mSemesterStart.get(Calendar.MONTH));
        mWeekStartDay.set(Calendar.DATE, mSemesterStart.get(Calendar.DATE));
        mWeekStartDay.add(Calendar.DAY_OF_YEAR,(currentWeek-1)*7);

        if(currentWeek>=SemesterDate.SEMESTER_MAX_WEEKS) {
            mWeekEndDay.set(Calendar.YEAR, mSemesterEnd.get(Calendar.YEAR));
            mWeekEndDay.set(Calendar.MONTH, mSemesterEnd.get(Calendar.MONTH));
            mWeekEndDay.set(Calendar.DATE, mSemesterEnd.get(Calendar.DATE));
        }
        else {
            mWeekEndDay.set(Calendar.YEAR, mWeekStartDay.get(Calendar.YEAR));
            mWeekEndDay.set(Calendar.MONTH, mWeekStartDay.get(Calendar.MONTH));
            mWeekEndDay.set(Calendar.DATE, mWeekStartDay.get(Calendar.DATE));
            mWeekEndDay.add(Calendar.DAY_OF_YEAR, 6);
        }

        mStartDay = mWeekStartDay.get(Calendar.DATE);
        mStartMonth = mWeekStartDay.get(Calendar.MONTH)+1;
        mEndDay = mWeekEndDay.get(Calendar.DATE);
        endMonth = mWeekEndDay.get(Calendar.MONTH)+1;
    }

    //데이터베이스로부터 읽어들인 학생들의 목록을 갱신하는 메소드
    public void updateStudentList(final String subjectCode,final int week)
    {
        DatabaseReference reference = mDatabase.getReference(getString(R.string.table_subject));
        mAttendanceReference = mAttendanceReference.child(subjectCode).child(String.valueOf(week));
        mAdapter.setWeek(week);

        //해당 과목 subjectCode 로 mSubject(전역변수) 동기화
        reference.child(subjectCode).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mSubject = dataSnapshot.getValue(Subject.class);

                        //현재 subjectCode 에 해당하는 학생 mStudents(전역변수) 리스트에 추가
                        List<String> studentsId = mSubject.getListenStudent();

                        //student 테이블과 subject 테이블을 내부적으로 JOIN 하기 위해 StudentInfo 클래스 사용
                        for(int i=0;i<studentsId.size();i++) {
                            mStudents.add(new dgu.donggukeas_prof.model.StudentInfo(studentsId.get(i)));
                        }
                        mAdapter.notifyDataSetChanged();

                        //학번과 일치하는 학생 이름을 데이터베이스에서 검색 후 StudentInfo에 추가
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
                            public void onCancelled(DatabaseError databaseError) {}
                        });

                        //학생의 출석 기록을 참조
                        mAttendanceReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                AttendanceStatus aStatus = dataSnapshot.getValue(AttendanceStatus.class);
                                int index = getStudentIndex(aStatus.getStudentId());
                                if(index!=-1){
                                    mStudents.get(index).setAttendanceStatus(aStatus.getAttendanceStatus());
                                    mAdapter.notifyItemChanged(index);
                                }
                            }

                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                AttendanceStatus aStatus = dataSnapshot.getValue(AttendanceStatus.class);
                                int index = getStudentIndex(aStatus.getStudentId());
                                if(index!=-1){
                                    mStudents.get(index).setAttendanceStatus(aStatus.getAttendanceStatus());
                                    mAdapter.notifyItemChanged(index);
                                }
                            }

                            public void onChildRemoved(DataSnapshot dataSnapshot) {}
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                    public void onCancelled(DatabaseError databaseError) {}
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
}