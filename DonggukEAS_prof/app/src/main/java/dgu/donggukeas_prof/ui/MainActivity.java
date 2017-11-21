package dgu.donggukeas_prof.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import dgu.donggukeas_prof.R;
import dgu.donggukeas_prof.adapter.SubjectAdapter;
import dgu.donggukeas_prof.model.SubjectInfo;
import dgu.donggukeas_prof.model.firebase.Professor;
import dgu.donggukeas_prof.model.firebase.Subject;


public class MainActivity extends AppCompatActivity {

    //private String mStudentId;

    private String mProfessorId;

    private RecyclerView mRecyclerView;
    private SubjectAdapter mSubjectAdapter;

    private Professor mProf;

    private ArrayList<SubjectInfo> mSubjects;

    private Button mRegisterBtn,mCreateQRBtn;
    private TextView mDeviceId;
    private ImageView mQrView;
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;

//    private DatabaseReference mDatabaseClients;
    private DatabaseReference mDatabaseProfessor;
    private DatabaseReference mDatabaseSubject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProfessorId = getIntent().getStringExtra(getString(R.string.extra_id));
        mDatabaseProfessor = FirebaseDatabase.getInstance().getReference(getString(R.string.table_professor)).child(mProfessorId);
        //.child(getString(R.string.lecture_subject));
        mDatabaseSubject = FirebaseDatabase.getInstance().getReference(getString(R.string.table_subject));

        mSubjects = new ArrayList<>();

        mRecyclerView = (RecyclerView)findViewById(R.id.rv_subject);
        mRecyclerView.setHasFixedSize(true);
///
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
///
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mSubjectAdapter = new SubjectAdapter(this,mSubjects);
        mRecyclerView.setAdapter(mSubjectAdapter);
        
        //mStudentId = getIntent().getStringExtra(getString(R.string.extra_id));

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
                        Log.d("#####","꺄하");
                        mSubjectAdapter.notifyDataSetChanged();
                    }
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        //mDatabaseClients = FirebaseDatabase.getInstance().getReference(getString(R.string.table_student_classes)).child(mStudentId);
        updateList();

    }

    private void updateList(){

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

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE,WIDTH ,HEIGHT , null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    public static String getDateFromMilli(Long milli){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milli);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
        return new String(sdf.format(calendar.getTime()));
    }

    public void onSubjectClicked(View v){
        /*TextView subject = (TextView)v.findViewById(R.id.tv_subject_code);
        Log.d("#####",subject.getText().toString());

        Intent i = new Intent(MainActivity.this, AttendanceActivity.class);
        i.putExtra(getString(R.string.extra_subject_code),subject.getText().toString());
        Toast.makeText(MainActivity.this,subject.getText().toString(), Toast.LENGTH_SHORT).show();
        startActivity(i);
*/
    }

}
