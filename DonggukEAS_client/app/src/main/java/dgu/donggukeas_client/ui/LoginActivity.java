package dgu.donggukeas_client.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import dgu.donggukeas_client.R;
import dgu.donggukeas_client.model.firebase.StudentAuth;


public class LoginActivity extends AppCompatActivity {

    private EditText mIdEt;
    private EditText mPwEt;
    private Button mloginBtn;
    private DatabaseReference mDatabaseClients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//abc
        mIdEt = (EditText) findViewById(R.id.et_id);
        mPwEt = (EditText) findViewById(R.id.et_pw);
        mloginBtn = (Button) findViewById(R.id.btn_login);
        Log.d("#####",FirebaseInstanceId.getInstance().getToken());


/*
        //임시 코드 .. 수강과목 넣는코드
        mDatabaseClients = FirebaseDatabase.getInstance().getReference(getString(R.string.table_student_classes)).child("2011112162");
        mloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SubjectInfo sub = new SubjectInfo();
                sub.setSubjectName(mIdEt.getText().toString());
                sub.setSubjectCode(mPwEt.getText().toString());
                mDatabaseClients.child(sub.getSubjectCode()).setValue(sub);
            }
        });
*/



        mDatabaseClients = FirebaseDatabase.getInstance().getReference(getString(R.string.table_student_auth));

        mloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인 부분 firebase 에서 해당아이디에 관한 정보를 가져와 비교한다
                final StudentAuth inputStudentAuth = new StudentAuth(mIdEt.getText().toString(),mPwEt.getText().toString());
                mDatabaseClients.child(inputStudentAuth.getId()).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Get user value
                                StudentAuth studentAuth = dataSnapshot.getValue(StudentAuth.class);
                                if(studentAuth !=null) {

                                    if (inputStudentAuth.getPw().equals(studentAuth.getPw())) {
                                        Log.d("#####", studentAuth.getId() + "success");
                                        Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                        i.putExtra(getString(R.string.extra_id), studentAuth.getId());
                                        Toast.makeText(LoginActivity.this,getString(R.string.success_login),Toast.LENGTH_SHORT).show();
                                        startActivity(i);

                                    }

                                    else {
                                        Log.d("#####", studentAuth.getId() + "failed");
                                        Toast.makeText(LoginActivity.this,getString(R.string.failed_login_pw),Toast.LENGTH_SHORT).show();

                                        // ...
                                    }
                                }
                                else{

                                    Log.d("#####", inputStudentAuth.getId()+"cannot find");
                                    Toast.makeText(LoginActivity.this,getString(R.string.failed_login_id),Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("#####", "getUser:onCancelled", databaseError.toException());
                            }
                        });
            }
        });
    }

}
