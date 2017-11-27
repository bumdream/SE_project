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
    private int mSubjectWeek;
    private String mSubjectCode;

    private ArrayList<RunawayInfo> mRStudents;
    private RunawayStudent mRunawayStudent;

    private RecyclerView mRunawayRecyclerView;
    private RunawayAdapter mRunawayAdapter;
    private LinearLayout mCheckComplete;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mStudentReference, mRunawayReference, mRunawayActiveReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runaway);

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                finish(); //동일한 액티비티가 중복으로 호출되는 것 방지
                return;
            }
        }

        ImageView mCloseActivity = (ImageView)findViewById(R.id.iv_close);
        mCloseActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunawayActive mRunawayActive = new RunawayActive(Constants.SUBJECT_ATTENDANCE_END); //출튀 처리가 완료되었음을 리더기에 알림

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

        //데이터베이스 참조
        mDatabase = FirebaseDatabase.getInstance();
        mStudentReference = mDatabase.getReference(getString(R.string.table_student));
        mRunawayReference = mDatabase.getReference(getString(R.string.table_runaway_student));
        mRunawayActiveReference = mDatabase.getReference("RUNAWAY_ACTIVE");

        if(mSubjectWeek == -1) {
            Toast.makeText(getApplicationContext(), "Week 정보를 읽을 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(),"출튀 체크 완료",Toast.LENGTH_SHORT).show();
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

        SnapHelper snapHelper = new PagerSnapHelper(); //SnapHelper는 리사이클러뷰를 한번에 한 개체씩 보여주는 것을 지원
        snapHelper.attachToRecyclerView(mRunawayRecyclerView);

        updateRunawayList(mSubjectCode); //출튀 학생 리스트를 갱신
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        RunawayActive mRunawayActive = new RunawayActive(Constants.SUBJECT_ATTENDANCE_END); //출튀 처리가 완료되었음을 리더기에 알림

        Map<String, Object> mRunawayValue = mRunawayActive.toMap();
        Map<String, Object> mNewRunaway = new HashMap<>();
        mNewRunaway.put(mSubjectCode, mRunawayValue);
        mRunawayActiveReference.updateChildren(mNewRunaway);

        finish();
    }

    public void updateRunawayList(final String subjectCode)
    {
        //리더기가 업데이트한 출튀 예상 학생 명단을 받아온다.
        mRunawayReference.child(subjectCode).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) { //각각의 child에 대해 처리
                            mRunawayStudent = child.getValue(RunawayStudent.class);
                            mRStudents.add(new RunawayInfo(mRunawayStudent.getStudentId(), mRunawayStudent.getUpdatedTime()));
                        }
                        if(mRStudents.size() == 0) {
                            mCheckComplete.setVisibility(View.VISIBLE);
                        }
                        //출튀 여부를 반영하기 위해 학생 테이블을 참조
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
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                        mRunawayAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
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

    //Runaway 어댑터에서 액티비티의 요소 바꿀 수 있도록 메소드 선언
    public void showEmptyView() {
        mCheckComplete.setVisibility(View.VISIBLE);
    }
}
