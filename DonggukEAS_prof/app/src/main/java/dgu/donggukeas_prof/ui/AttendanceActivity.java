package dgu.donggukeas_prof.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dgu.donggukeas_prof.R;
import dgu.donggukeas_prof.adapter.AttendanceAdapter;
import dgu.donggukeas_prof.adapter.SubjectAdapter;
import dgu.donggukeas_prof.model.StudentInfo;
import dgu.donggukeas_prof.model.firebase.AttendanceStatus;
import dgu.donggukeas_prof.model.firebase.RunawayActive;
import dgu.donggukeas_prof.model.firebase.Student;
import dgu.donggukeas_prof.model.firebase.Subject;
import dgu.donggukeas_prof.util.Constants;
import dgu.donggukeas_prof.util.SemesterDate;

public class AttendanceActivity extends AppCompatActivity {
    private RecyclerView mAttendanceRecyclerView;
    private SubjectAdapter mSubjectAdapter;
    private TextView subjectCode, subjectName, subjectWeek, subjectWeekDays;
    private FloatingActionButton fabQR, fabRA;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mStudentReference,mDeviceReference,mAttendanceReference, mRunawayActiveReference;
    private Subject mSubject;
    private ArrayList<StudentInfo> mStudents;
    private AttendanceAdapter mAdapter;
    private LinearLayout mProgressLayout;
    private int currentWeek, mTodaysWeek;
    private ImageButton ibLeft, ibRight;
    private Calendar semesterStart = null, currentDate = null, semesterEnd = null;
    private Calendar weekStartDay = null, weekEndDay = null;
    private int startDay=0, startMonth=0;
    private int endDay=0, endMonth=0;
    private boolean isProgress = false;
    //private boolean isRunawayButtonClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        subjectCode = (TextView)findViewById(R.id.tv_a_subjectCode);
        subjectCode.setText(getIntent().getStringExtra(getString(R.string.extra_subject_code)));
        subjectName = (TextView)findViewById(R.id.tv_a_subjectName);
        subjectName.setText(getIntent().getStringExtra(getString(R.string.extra_subject_name)));

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

        semesterStart = Calendar.getInstance();
        semesterEnd = Calendar.getInstance();
        weekStartDay = Calendar.getInstance();
        weekEndDay = Calendar.getInstance();

        currentWeek = getWeek();
        mTodaysWeek = currentWeek;
        updateWeekInfo(currentWeek);



        fabQR = (FloatingActionButton)findViewById(R.id.fab_qr);
        fabQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), QrActivity.class);
                i.putExtra("QRCODE",subjectCode.getText().toString());
                Log.d("#####",subjectCode.getText().toString());


                startActivity(i);

            }
        });

        fabRA = (FloatingActionButton)findViewById(R.id.fab_runaway);
        fabRA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // isRunawayButtonClicked = true;
                isProgress = true;

                final String mSubjectCode = subjectCode.getText().toString();

                FirebaseDatabase mDatabase;
                //DatabaseReference mRunawwayActiveReference;

                mDatabase = FirebaseDatabase.getInstance();
                mRunawayActiveReference = mDatabase.getReference("RUNAWAY_ACTIVE");




                //mRunawwayActiveReference = mRunawwayActiveReference.child(mSubjectCode);

                mRunawayActiveReference.child(mSubjectCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        RunawayActive ra = snapshot.getValue(RunawayActive.class);
                        if(ra.getIsActive() == Constants.SUBJECT_ATTENDANCE_END) {
                            //Toast.makeText(getApplicationContext(), "출석완료", Toast.LENGTH_LONG).show();
                            RunawayActive mRunawayActive = new RunawayActive(Constants.SUBJECT_ATTENDANCE_RUNAWAY_ACTIVE);

                            Map<String, Object> mRunawayValue = mRunawayActive.toMap();
                            Map<String, Object> mNewRunaway = new HashMap<>();

                            mNewRunaway.put(mSubjectCode,mRunawayValue);
                            mRunawayActiveReference.updateChildren(mNewRunaway);

                            mProgressLayout = (LinearLayout)findViewById(R.id.ll_pb);
                            mProgressLayout.setVisibility(View.VISIBLE);

////////////////
                            mRunawayActiveReference.child(mSubjectCode).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    int value = dataSnapshot.getValue(Integer.class);
                                    if(value == Constants.SUBJECT_ATTENDANCE_RUNAWAY_END){
                                        isProgress = false;

                                        mProgressLayout.setVisibility(View.INVISIBLE);

                                        Intent i = new Intent(getApplicationContext(), RunawayActivity.class);

                                        i.putExtra(getString(R.string.extra_ra_subject_code),subjectCode.getText().toString());
                                        i.putExtra(getString(R.string.extra_ra_subject_week),mTodaysWeek);

                                        //Log.d("#####",subjectCode.getText().toString());

                                       // isRunawayButtonClicked = false;



                                        startActivity(i);
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
                            });



                        }else
                             Toast.makeText(getApplicationContext(),"출석이 아직 종료되지 않았습니다.\n잠시 후 다시 시도하세요.",Toast.LENGTH_LONG).show();

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



           /*여기 주석




*/




            }
        });

        //currentWeek = getWeek();
        //updateWeekInfo(currentWeek);

        //mAdapter.setWeek(currentWeek);

        subjectWeek = (TextView)findViewById(R.id.tv_a_week);
        subjectWeek.setText(currentWeek+"주차");
        subjectWeekDays = (TextView)findViewById(R.id.tv_weekdays);
        subjectWeekDays.setText(startMonth+"/"+startDay+" ~ "+endMonth+"/"+endDay);

        ibLeft = (ImageButton)findViewById(R.id.ib_a_left);
        ibRight = (ImageButton)findViewById(R.id.ib_a_right) ;



        if(currentWeek == 1) {
            ibLeft.setClickable(false);
            ibLeft.setImageResource(R.drawable.ic_left_disabled);
        }
        else if(currentWeek == 16) {
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
                currentWeek--;
                weekStartDay.add(Calendar.DAY_OF_YEAR, -7);
                updateWeekInfo(currentWeek);
                //mAdapter.setWeek(currentWeek);

                subjectWeek.setText(currentWeek+"주차");
                subjectWeekDays.setText(startMonth+"-"+startDay+" ~ "+endMonth+"-"+endDay);

                if(currentWeek == 1)
                {
                    ibLeft.setImageResource(R.drawable.ic_left_disabled);
                    ibLeft.setClickable(false);
                }
                else
                {
                    ibLeft.setImageResource(R.drawable.ic_left);
                    ibRight.setImageResource(R.drawable.ic_right);
                    ibLeft.setClickable(true);
                    ibRight.setClickable(true);
                }
                mAttendanceReference = mDatabase.getReference(getString(R.string.table_attendance));

                mStudents = new ArrayList<>();
                mAdapter = new AttendanceAdapter(AttendanceActivity.this, mStudents, subjectCode.getText().toString());
                mAttendanceRecyclerView.setAdapter(mAdapter);

                updateStudentList(subjectCode.getText().toString(), currentWeek);
            }
        });

        ibRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentWeek++;
                weekStartDay.add(Calendar.DAY_OF_YEAR, 7);
                updateWeekInfo(currentWeek);
                //mAdapter.setWeek(currentWeek);

                subjectWeek.setText(currentWeek+"주차");
                subjectWeekDays.setText(startMonth+"-"+startDay+" ~ "+endMonth+"-"+endDay);

                if(currentWeek == 16)
                {
                    ibRight.setImageResource(R.drawable.ic_right_disabled);
                    ibRight.setClickable(false);
                }
                else
                {
                    ibLeft.setImageResource(R.drawable.ic_left);
                    ibRight.setImageResource(R.drawable.ic_right);
                    ibLeft.setClickable(true);
                    ibRight.setClickable(true);
                }
                mAttendanceReference = mDatabase.getReference(getString(R.string.table_attendance));

                mStudents = new ArrayList<>();
                mAdapter = new AttendanceAdapter(AttendanceActivity.this, mStudents, subjectCode.getText().toString());
                mAttendanceRecyclerView.setAdapter(mAdapter);
                updateStudentList(subjectCode.getText().toString(), currentWeek);
            }
        });

        updateStudentList(subjectCode.getText().toString(), currentWeek);

    }

    @Override
    public void onBackPressed() {
        if(isProgress == true)
        {}
        else {
            super.onBackPressed();
        }
    }


    public int getWeek()
    {
       // semesterStart = Calendar.getInstance();
        //semesterStart.set(Calendar.YEAR,  2017);
        //semesterStart.set(Calendar.MONTH,  Calendar.AUGUST);
        //semesterStart.set(Calendar.DATE,  28);

        semesterStart.set(Calendar.YEAR, SemesterDate.SEMESTER_START_YEAR);
        semesterStart.set(Calendar.MONTH, SemesterDate.SEMESTER_START_MONTH);
        semesterStart.set(Calendar.DATE, SemesterDate.SEMESTER_START_DATE);

        int startDate = semesterStart.get(Calendar.DAY_OF_YEAR);
        Log.d("###","startDate : "+startDate);

        currentDate = Calendar.getInstance();
        int todayDate = currentDate.get(Calendar.DAY_OF_YEAR);
        Log.d("###","todayDate : "+todayDate);

      //  semesterEnd = Calendar.getInstance();
      //  semesterEnd.set(Calendar.YEAR,  2017);
      //  semesterEnd.set(Calendar.MONTH,  Calendar.DECEMBER);
      //  semesterEnd.set(Calendar.DATE,  18);

        semesterEnd.set(Calendar.YEAR, SemesterDate.SEMESTER_END_YEAR);
        semesterEnd.set(Calendar.MONTH, SemesterDate.SEMESTER_END_MONTH);
        semesterEnd.set(Calendar.DATE, SemesterDate.SEMESTER_END_DATE);

//        int endDate = semesterEnd.get(Calendar.DAY_OF_YEAR);

        int weekNo = (todayDate-startDate)/7 + 1;

      /*  weekStartDay.set(Calendar.YEAR, semesterStart.get(Calendar.YEAR));
        weekStartDay.set(Calendar.MONTH, semesterStart.get(Calendar.MONTH));
        weekStartDay.set(Calendar.DATE, semesterStart.get(Calendar.DATE));
        weekStartDay.add(Calendar.DAY_OF_YEAR,(weekNo-1)*7);


       // int wstartDay = weekStartDay.get(Calendar.DATE);
        //int wstartMonth = weekStartDay.get(Calendar.MONTH)+1;

       // Toast.makeText(getApplicationContext(),wstartMonth+"/"+wstartDay+"\n"+"왜안돼",Toast.LENGTH_LONG).show();

        if(weekNo>16) {
            weekEndDay.set(Calendar.YEAR, semesterEnd.get(Calendar.YEAR));
            weekEndDay.set(Calendar.MONTH, semesterEnd.get(Calendar.MONTH));
            weekEndDay.set(Calendar.DATE, semesterEnd.get(Calendar.DATE));
        }
        else {
            weekEndDay.set(Calendar.YEAR, weekStartDay.get(Calendar.YEAR));
            weekEndDay.set(Calendar.MONTH, weekStartDay.get(Calendar.MONTH));
            weekEndDay.set(Calendar.DATE, weekStartDay.get(Calendar.DATE));
            weekEndDay.add(Calendar.DAY_OF_YEAR, 6);
        }
        */
      //Toast.makeText(getApplicationContext(),"오늘은 개강일로부터 "+(todayDate-startDate)+"일 경과\n현재 "+weekNo+"주차",Toast.LENGTH_LONG).show();


        if(weekNo>16)
            return 16;
        else
            return weekNo;
    }

    public void updateWeekInfo(int currentWeek)
    {
        weekStartDay.set(Calendar.YEAR, semesterStart.get(Calendar.YEAR));
        weekStartDay.set(Calendar.MONTH, semesterStart.get(Calendar.MONTH));
        weekStartDay.set(Calendar.DATE, semesterStart.get(Calendar.DATE));
        weekStartDay.add(Calendar.DAY_OF_YEAR,(currentWeek-1)*7);


        // int wstartDay = weekStartDay.get(Calendar.DATE);
        //int wstartMonth = weekStartDay.get(Calendar.MONTH)+1;

        // Toast.makeText(getApplicationContext(),wstartMonth+"/"+wstartDay+"\n"+"왜안돼",Toast.LENGTH_LONG).show();

        if(currentWeek>=16) {
            weekEndDay.set(Calendar.YEAR, semesterEnd.get(Calendar.YEAR));
            weekEndDay.set(Calendar.MONTH, semesterEnd.get(Calendar.MONTH));
            weekEndDay.set(Calendar.DATE, semesterEnd.get(Calendar.DATE));
        }
        else {
            weekEndDay.set(Calendar.YEAR, weekStartDay.get(Calendar.YEAR));
            weekEndDay.set(Calendar.MONTH, weekStartDay.get(Calendar.MONTH));
            weekEndDay.set(Calendar.DATE, weekStartDay.get(Calendar.DATE));
            weekEndDay.add(Calendar.DAY_OF_YEAR, 6);
        }

        startDay = weekStartDay.get(Calendar.DATE);
        startMonth = weekStartDay.get(Calendar.MONTH)+1;
        endDay = weekEndDay.get(Calendar.DATE);
        endMonth = weekEndDay.get(Calendar.MONTH)+1;

        //Toast.makeText(getApplicationContext(),startMonth+"/"+startDay+"\n"+endMonth+"/"+endDay,Toast.LENGTH_LONG).show();

    }


    public void updateStudentList(final String subjectCode,final int week)
    {
        DatabaseReference reference = mDatabase.getReference(getString(R.string.table_subject));
        mAttendanceReference = mAttendanceReference.child(subjectCode).child(String.valueOf(week));
        mAdapter.setWeek(week);

        //해당 과목 subjectCode 로 mSubject(전연변수) 동기화

        reference.child(subjectCode).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mSubject = dataSnapshot.getValue(Subject.class);
                        //setTitle(mSubject.getSubjectName()+"["+mSubject.getSubjectCode()+"] - "+week+"주차");
                        //Log.d("#####",mSubject.getSubjectCode()+mSubject.getSubjectName());



                        //현재 subjectCode 에 해당하는 학생 mStudents(전역변수) 리스트에 추가
                        List<String> studentsId = mSubject.getListenStudent();


                        for(int i=0;i<studentsId.size();i++) {
                            mStudents.add(new dgu.donggukeas_prof.model.StudentInfo(studentsId.get(i)));
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

    public static String getDateFromMilli(Long milli){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milli);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
        return new String(sdf.format(calendar.getTime()));
    }


}


