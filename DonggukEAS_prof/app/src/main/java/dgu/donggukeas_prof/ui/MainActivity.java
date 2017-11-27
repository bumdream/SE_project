package dgu.donggukeas_prof.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dgu.donggukeas_prof.R;
import dgu.donggukeas_prof.adapter.SubjectAdapter;
import dgu.donggukeas_prof.model.SubjectInfo;
import dgu.donggukeas_prof.model.firebase.Professor;
import dgu.donggukeas_prof.model.firebase.Subject;

public class MainActivity extends AppCompatActivity {
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;

    private String mProfessorId;
    private Professor mProf;
    //리사이클러뷰 관련
    private RecyclerView mRecyclerView;
    private SubjectAdapter mSubjectAdapter;
    private ArrayList<SubjectInfo> mSubjects;
    //데이터베이스 연동 관련
    private DatabaseReference mDatabaseProfessor;
    private DatabaseReference mDatabaseSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProfessorId = getIntent().getStringExtra(getString(R.string.extra_id));
        mDatabaseProfessor = FirebaseDatabase.getInstance().getReference(getString(R.string.table_professor)).child(mProfessorId);
        mDatabaseSubject = FirebaseDatabase.getInstance().getReference(getString(R.string.table_subject));

        mSubjects = new ArrayList<>();
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_subject);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mSubjectAdapter = new SubjectAdapter(this,mSubjects);
        mRecyclerView.setAdapter(mSubjectAdapter);

        //파이어베이스 내 데이터들에 대한 참조 (PROFESSOR 테이블과 SUBJECT 테이블 조인)
        mDatabaseProfessor.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProf = dataSnapshot.getValue(Professor.class);

                for(int i=0;i<mProf.getLectureSubject().size();i++)
                    mSubjects.add(new SubjectInfo(mProf.getLectureSubject().get(i)));
                mSubjectAdapter.notifyDataSetChanged(); //데이터가 들어왔다는 걸 알게됨

                mDatabaseSubject.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Subject subject = dataSnapshot.getValue(Subject.class); //하나에 대해 불러옴
                        int index = getItemIndex(subject.getSubjectCode());

                        if(index!=-1)
                        {
                            mSubjects.get(index).setSubjectName(subject.getSubjectName());
                            mSubjects.get(index).setNumOfStudents(subject.getListenStudent().size());
                            mSubjectAdapter.notifyItemChanged(index);
                        }
                        mSubjectAdapter.notifyDataSetChanged();
                    }
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private int getItemIndex(String subjectCode){
        int index = -1;
        for(int i=0;i<mSubjects.size();i++){
            if(mSubjects.get(i).getSubjectCode().equals(subjectCode)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
