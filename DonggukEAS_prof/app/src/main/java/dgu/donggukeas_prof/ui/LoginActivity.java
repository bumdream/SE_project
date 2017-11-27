package dgu.donggukeas_prof.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dgu.donggukeas_prof.R;
import dgu.donggukeas_prof.model.firebase.Auth;


public class LoginActivity extends AppCompatActivity {
    private EditText mIdEt;
    private EditText mPwEt;
    private Button mLoginBtn;
    private DatabaseReference mDatabaseProf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mIdEt = (EditText) findViewById(R.id.et_id);
        mPwEt = (EditText) findViewById(R.id.et_pw);
        mLoginBtn = (Button) findViewById(R.id.btn_login);

        mPwEt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Enter키눌렀을떄 처리
                    login();
                    return true;
                }
                return false;
            }
        });

        mDatabaseProf = FirebaseDatabase.getInstance().getReference(getString(R.string.table_professor_auth));
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그인 부분 firebase 에서 해당아이디에 관한 정보를 가져와 비교한다
                login();
            }
        });
    }
    public void login()
    {
        final Auth inputAuth = new Auth(mIdEt.getText().toString(),mPwEt.getText().toString());
        mDatabaseProf.child(inputAuth.getId()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Auth auth = dataSnapshot.getValue(Auth.class);
                        if(auth!=null) {

                            if (inputAuth.getPw().equals(auth.getPw())) {
                                Log.d("#####", auth.getId() + "success");
                                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                i.putExtra(getString(R.string.extra_id),auth.getId());
                                Toast.makeText(LoginActivity.this,getString(R.string.success_login),Toast.LENGTH_SHORT).show();
                                startActivity(i);
                            }

                            else {
                                Log.d("#####", auth.getId() + "failed");
                                Toast.makeText(LoginActivity.this,getString(R.string.failed_login_pw),Toast.LENGTH_SHORT).show();

                            }
                        }
                        else{

                            Log.d("#####",inputAuth.getId()+"cannot find");
                            Toast.makeText(LoginActivity.this,getString(R.string.failed_login_id),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("#####", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
}
