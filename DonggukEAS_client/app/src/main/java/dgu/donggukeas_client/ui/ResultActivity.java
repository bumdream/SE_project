package dgu.donggukeas_client.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import dgu.donggukeas_client.R;
import dgu.donggukeas_client.model.firebase.AttendanceStatus;
import dgu.donggukeas_client.util.Constants;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    private String mStudentId,mSubjectCode,mSubjectName;
    private int mWeek;
    private ImageView mResultImg;
    private TextView mResult,mInfo;
    private LinearLayout mOk,mRetry;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mAttendanceReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        mResultImg = (ImageView)findViewById(R.id.iv_img);
        mResult = (TextView)findViewById(R.id.tv_result);
        mInfo = (TextView)findViewById(R.id.tv_info);
        mOk = (LinearLayout)findViewById(R.id.ll_ok);
        mOk.setOnClickListener(this);
        mRetry = (LinearLayout)findViewById(R.id.ll_retry);
        mRetry.setOnClickListener(this);

        Intent i = getIntent();
        mStudentId = i.getStringExtra("studentId");
        mSubjectCode = i.getStringExtra("subjectCode");
        mSubjectName = i.getStringExtra("subjectName");
        mWeek = i.getIntExtra("week", -1);


        mDatabase = FirebaseDatabase.getInstance();
        mAttendanceReference = mDatabase.getReference(getString(R.string.table_attendance))
                .child(mSubjectCode)
                .child(String.valueOf(mWeek)).child(mStudentId);
        mAttendanceReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               AttendanceStatus as =  dataSnapshot.getValue(AttendanceStatus.class);
                Log.d("#####",as.getStudentId()+"/"+as.getAttendanceStatus());
                if(as.getAttendanceStatus()== Constants.ATTENDANCE_NONE){
                    showRetryView();
                }
                else if(as.getAttendanceStatus() == Constants.ATTENDANCE_OK){
                    showOkView();
                }
                else if(as.getAttendanceStatus() == Constants.ATTENDANCE_LATE){
                    showLateView();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void showOkView(){
        mRetry.setVisibility(View.INVISIBLE);
        mOk.setVisibility(View.VISIBLE);
        mOk.setBackgroundResource(R.color.material_green);

        mResultImg.setImageResource(R.drawable.ic_attendance_ok);
        mResult.setText(getString(R.string.ok));
        mInfo.setText(mSubjectName+"["+mSubjectCode+"]\n"+mWeek+"주차 출석이 완료되었습니다.");
    }

    public void showLateView(){
        mRetry.setVisibility(View.INVISIBLE);
        mOk.setVisibility(View.VISIBLE);
        mOk.setBackgroundResource(R.color.material_amber);
        mResultImg.setImageResource(R.drawable.ic_attendance_late);
        mResult.setText(getString(R.string.late));
        mInfo.setText(mSubjectName+"["+mSubjectCode+"]\n"+mWeek+"주차 지각처리 되었습니다.");
    }
    public void showRetryView(){
        mRetry.setVisibility(View.VISIBLE);
        mOk.setVisibility(View.INVISIBLE);
        mResultImg.setImageResource(R.drawable.ic_attendance_fa);
        mResult.setText(getString(R.string.retry));
        mInfo.setText(getString(R.string.info_not_match));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_ok:
                finish();
                break;
            case R.id.ll_retry:
                Intent intent = new Intent(this, SendWifiActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("studentId", mStudentId);
                intent.putExtra("subjectCode", mSubjectCode);
                intent.putExtra("week", mWeek);
                intent.putExtra("subjectName", mSubjectName);
                startActivity(intent);
                finish();
                break;
        }
    }
}
