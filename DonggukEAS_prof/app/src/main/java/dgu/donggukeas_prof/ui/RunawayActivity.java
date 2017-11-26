package dgu.donggukeas_prof.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dgu.donggukeas_prof.R;
import dgu.donggukeas_prof.adapter.RunawayAdapter;
import dgu.donggukeas_prof.model.RunawayInfo;
import dgu.donggukeas_prof.model.firebase.RunawayActive;
import dgu.donggukeas_prof.model.firebase.RunawayStudent;
import dgu.donggukeas_prof.model.firebase.Student;
import dgu.donggukeas_prof.util.Constants;

public class RunawayActivity extends AppCompatActivity implements RunawayAdapter.AdapterInterface
{
    private FirebaseDatabase mDatabase;
    private DatabaseReference mStudentReference, mRunawayReference, mRunawayActiveReference;
    private RecyclerView mRunawayRecyclerView;
    private RunawayAdapter mRunawayAdapter;
    private ArrayList<RunawayInfo> mRStudents;
    private String mSubjectCode;
    private int mSubjectWeek;
    private RunawayStudent mRunawayStudent;
    private LinearLayout mCheckComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runaway);

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                //Log.w(LOG_TAG, "Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }


        ImageView mCloseActivity = (ImageView)findViewById(R.id.iv_close);
        mCloseActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunawayActive mRunawayActive = new RunawayActive(Constants.SUBJECT_ATTENDANCE_END);

                Map<String, Object> mRunawayValue = mRunawayActive.toMap();
                Map<String, Object> mNewRunaway = new HashMap<>();

                mNewRunaway.put(mSubjectCode, mRunawayValue);
                mRunawayActiveReference.updateChildren(mNewRunaway);

                finish();
            }
        });

        mSubjectCode = getIntent().getStringExtra(getString(R.string.extra_ra_subject_code));
        mSubjectWeek = getIntent().getIntExtra(getString(R.string.extra_ra_subject_week),-1);

        mCheckComplete = (LinearLayout)findViewById(R.id.ll_checkComp);

        mDatabase = FirebaseDatabase.getInstance();
        mStudentReference = mDatabase.getReference(getString(R.string.table_student));
        mRunawayReference = mDatabase.getReference(getString(R.string.table_runaway_student));
        mRunawayActiveReference = mDatabase.getReference("RUNAWAY_ACTIVE");
      //  Log.d("#####",mSubjectCode.toString());
        //mRunawayReference = mRunawayReference.child(mSubjectCode);


        if(mSubjectWeek == -1) {
            Toast.makeText(getApplicationContext(), "Week 정보를 읽을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(),"체크 완료",Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "Week " + mSubjectWeek, Toast.LENGTH_SHORT).show();
        }

        mRunawayRecyclerView = (RecyclerView)findViewById(R.id.rv_runaway);
        mRunawayRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRunawayRecyclerView.setLayoutManager(llm);

        mRStudents = new ArrayList<>();

        mRunawayAdapter = new RunawayAdapter(this, mRStudents, mSubjectCode.toString(),this);
        mRunawayAdapter.setWeek(mSubjectWeek);
        mRunawayRecyclerView.setAdapter(mRunawayAdapter);

        SnapHelper snapHelper = new PagerSnapHelper(); //SnapHelper는 리사이클러뷰를 한번에 한 개체씩 보여주는 것을 지원한다.
        snapHelper.attachToRecyclerView(mRunawayRecyclerView);

        updateRunawayList(mSubjectCode);


        /*RunawayInfo r1 = new RunawayInfo("2013112066","배종후");
        RunawayInfo r2 = new RunawayInfo("2013112069","한재현");
        mRStudents.add(r1);
        mRStudents.add(r2);

        mRunawayAdapter.notifyDataSetChanged();
*/

/*
        LinearLayout llCheck = (LinearLayout)findViewById(R.id.llCheck);
        llCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        RunawayActive mRunawayActive = new RunawayActive(Constants.SUBJECT_ATTENDANCE_END);

        Map<String, Object> mRunawayValue = mRunawayActive.toMap();
        Map<String, Object> mNewRunaway = new HashMap<>();

        mNewRunaway.put(mSubjectCode, mRunawayValue);
        mRunawayActiveReference.updateChildren(mNewRunaway);

        finish();
    }


    public void updateRunawayList(final String subjectCode)
    {

        //해당 과목 subjectCode 로 mSubject(전연변수) 동기화

        mRunawayReference.child(subjectCode).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                    //    final ArrayList<RunawayStudent> mRunawayStudentList = new ArrayList<>();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            mRunawayStudent = child.getValue(RunawayStudent.class);
                           // Toast.makeText(getApplicationContext(),mRunawayStudent.getStudentId(),Toast.LENGTH_SHORT).show();
                           // Toast.makeText(getApplicationContext(),mRunawayStudent.getUpdatedTime(),Toast.LENGTH_SHORT).show();
                            mRStudents.add(new RunawayInfo(mRunawayStudent.getStudentId(), mRunawayStudent.getUpdatedTime()));

//                            mRunawayStudentList.add(mRunawayStudent);
                        }
                        if(mRStudents.size() == 0)
                            mCheckComplete.setVisibility(View.VISIBLE);

                        mStudentReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    Student student = child.getValue(Student.class);
                                    int changedIndex = -1;
                                    for(int i=0;i<mRStudents.size();i++){
                                        if(mRStudents.get(i).getStudentId().equals(student.getStudentId())){
                                            mRStudents.get(i).setStudentName(student.getStudentName());
                                            changedIndex = i;
                                            break;
                                        }
                                    }
                                    if(changedIndex!=-1)
                                        mRunawayAdapter.notifyItemChanged(changedIndex);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //mRunawayAdapter.setTotal(mRStudents.size());
                        mRunawayAdapter.notifyDataSetChanged();


                   //     mRunawayStudent = dataSnapshot.getValue(RunawayStudent.class);




                        //현재 subjectCode 에 해당하는 학생 mStudents(전역변수) 리스트에 추가
                        //String studentId = mRunawayStudent.getStudentId();

                   //     mRStudents.add(new RunawayInfo(mRunawayStudent.getStudentId(), mRunawayStudent.getUpdatedTime()));

                   //     mRunawayAdapter.notifyDataSetChanged();
                        //student 테이블로부터 조인.

                        //학생 이름을 학번으로 부터 불러온다
                       /* mStudentReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    Student student = child.getValue(Student.class);
                                    int changedIndex = -1;
                                    for(int i=0;i<mRStudents.size();i++){
                                        if(mRStudents.get(i).getStudentId().equals(student.getStudentId())){
                                            mRStudents.get(i).setStudentName(student.getStudentName());
                                            changedIndex = i;
                                            break;
                                        }
                                    }
                                    if(changedIndex!=-1)
                                        mRunawayAdapter.notifyItemChanged(changedIndex);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });*/
/*
                        mAttendanceReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                AttendanceStatus as = dataSnapshot.getValue(AttendanceStatus.class);
                                int index = getStudentIndex(as.getStudentId());
                                if(index!=-1){
                                    mRStudents.get(index).setAttendanceStatus(as.getAttendanceStatus());
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
*/
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }


    public int getStudentIndex(String studentId){
        int index = -1;
        for (int i = 0; i < mRStudents.size(); i++) {
            if (mRStudents.get(i).getStudentId().equals(studentId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    public void showEmptyView() {
        mCheckComplete.setVisibility(View.VISIBLE);
    }
}
