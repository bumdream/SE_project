package dgu.donggukeas_client.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.StringTokenizer;

import dgu.donggukeas_client.R;
import dgu.donggukeas_client.adapter.AttendanceAdapter;
import dgu.donggukeas_client.model.AttendanceInfo;
import dgu.donggukeas_client.model.firebase.AttendanceStatus;
import dgu.donggukeas_client.util.Constants;

public class AttendanceActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private AttendanceAdapter mAttendanceAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAttendanceRef;
    private TextView mTitle;

    //모든 주차에 대한 리스너
    //나중에 한번에 리스너 해제하기 위해서.
    private ArrayList<DatabaseReference> mRefs;


    private ArrayList<AttendanceInfo> mAttendances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        mRecyclerView = (RecyclerView)findViewById(R.id.rv_attendance);
        mTitle = (TextView)findViewById(R.id.tv_title);
        Intent i = getIntent();
        final String subjectCode = i.getStringExtra(getString(R.string.extra_subject_code));
        String subjectName = i.getStringExtra(getString(R.string.extra_subject_name));
        mTitle.setText(subjectName+"["+subjectCode+"]");
        Log.d("#####","subjectCode:"+subjectCode);

        mAttendances = new ArrayList<>();
        mRefs = new ArrayList<>();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAttendanceRef = mFirebaseDatabase.getReference(getString(R.string.table_attendance)).child(subjectCode);

        mAttendanceAdapter = new AttendanceAdapter(this,mAttendances);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAttendanceAdapter);
        //TODO subject 의 모든 주차에 대해 학생 출석정보 받아와서 리스트에 넣기

        mAttendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //해당 과목의 각 주차를 돌면서 데이터가 있으면 데이터에 출석정보,주차로 해서 값을 넣어줌.
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    final String week = postSnapshot.getKey();
                    mAttendanceRef.child(week).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                AttendanceStatus as = postSnapshot.getValue(AttendanceStatus.class);
                                if (as.getStudentId().equals(MainActivity.mStudentId)) {
                                    mAttendances.add(new AttendanceInfo(as.getAttendanceStatus(), Integer.parseInt(week)));
                                }
                            }
                            mAttendanceAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    //각각의 주의 출석에 대해서 리스너 연결후 리스트에 집어넣음
                    DatabaseReference dr = mAttendanceRef.child(week).child(MainActivity.mStudentId);

                    dr.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            long attendanceStatus =  dataSnapshot.getValue(Long.class);

                            String ref = dataSnapshot.getRef().toString();
                            String[] spilted = ref.split("/");
                            String week = spilted[spilted.length-3];

                            int index = getItemIndex(Integer.parseInt(week));
                            if(index != Constants.attendanceNotFound){
                                mAttendances.get(index).setAttendanceStatus((int)attendanceStatus);
                                mAttendanceAdapter.notifyItemChanged(index);
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
                    mRefs.add(dr);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private int getItemIndex(int week){
        int index = Constants.subjectNotFound;
        for(int i=0;i<mAttendances.size();i++){
            if(mAttendances.get(i).getWeek()==week) {
                index = i;
                break;
            }
        }
        return index;
    }

}
