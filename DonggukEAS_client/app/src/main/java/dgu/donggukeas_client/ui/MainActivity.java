package dgu.donggukeas_client.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import dgu.donggukeas_client.R;
import dgu.donggukeas_client.adapter.SubjectAdapter;
import dgu.donggukeas_client.model.Student;
import dgu.donggukeas_client.model.Subject;
import dgu.donggukeas_client.model.WaitingClient;
import dgu.donggukeas_client.model.firebase.StudentDevice;
import dgu.donggukeas_client.util.Constants;


public class MainActivity extends AppCompatActivity {

    private String mStudentId;
    private RecyclerView mRecyclerView;
    private SubjectAdapter mSubjectAdapter;
    private ArrayList<Subject> result;
    private FloatingActionButton mQrBtn;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDeviceRef,mReserveRef;

    private Button mRegisterBtn,mCreateQRBtn;
    private TextView mDeviceId;
    private ImageView mQrView;
    private Toast mToast;

    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;

    private DatabaseReference mDatabaseClients;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQrBtn = (FloatingActionButton)findViewById(R.id.fab);


        mRecyclerView = (RecyclerView)findViewById(R.id.rv_subject);
        result = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSubjectAdapter = new SubjectAdapter(this,result);
        mRecyclerView.setAdapter(mSubjectAdapter);

        mStudentId = getIntent().getStringExtra(getString(R.string.extra_id));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseClients = mFirebaseDatabase.getReference(getString(R.string.table_student_classes))
                .child(mStudentId);
        mDeviceRef = mFirebaseDatabase.getReference(getString(R.string.table_student_device))
                .child(mStudentId);
        mReserveRef = mFirebaseDatabase.getReference(getString(R.string.table_device_reserve))
                .child(mStudentId);


        updateList();
        mQrBtn.setOnClickListener(new View.OnClickListener() {
            /*
                FROM  STUDENT_DEVICE
                1. 기기가 없는경우
                - STUDENT_RESRVATION에 있는지 확인
                있으면 등록대기중 띄우기
                없으면 등록 메세지 띄우기

                2. 기기가 있는경우
                - 현재 기기와 같을 경우 QR코드 생성하기
                - 현재 기기와 다를 경우 경고 메세지 창 띄우기.

                        기기 신청시 STUDENT_RESERVATION
                에 등록하고 STUDENT_DEVICE 의 학생기기는 Constants. 로 초기화하기
            */

            @Override
            public void onClick(View v) {

                mDeviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        StudentDevice studentDevice = dataSnapshot.getValue(StudentDevice.class);
                        if(studentDevice.getDeviceToken().equals(Constants.deviceNotRegisterd)){
                            //TODO 이렇게 할경우 모든 학생의 데이터를 미리 -1로 넣어줘야한다
                            //1. 기기가 없는 경우
                            Log.d("#####","1.기기가 없는경우");
                            mReserveRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    StudentDevice reserveDevice = dataSnapshot.getValue(StudentDevice.class);
                                    if(reserveDevice == null){

                                        Log.d("#####","1-1 기기 예약 NO");
                                        //기기 예약도 안했으면
                                        //등록 메세지 띄우기
                                        showRegisterDialog();
                                    }
                                    else{
                                        Log.d("#####","1-1 기기 예약 YES");
                                        //기기 예약이 되어있으면
                                        //기다리라는 메세지 띄우기
                                        showToast(getString(R.string.info_wait_device_accept));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        else{
                            //2.기기가 있는 경우
                            Log.d("#####","2. 기기 있는경우");
                            String thisDeviceToken = FirebaseInstanceId.getInstance().getToken();

                            if(studentDevice.getDeviceToken().equals(thisDeviceToken)){
                                Log.d("#####","2-1. 현재기기와 같다");
                                //현재기기와 같을 경우
                                //QR코드 띄우기
                            }
                            else{
                                Log.d("#####","2-2. 현재기기와 다르다");
                                //현재기기와 다를 경우
                                //경고 메세지 띄우기(새로 신청하시겠습니까? 새로 신청하면 이 학번은 이 기기로만 로그인 가능)
                                showReRegisterDialog();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    private void updateList(){
        mDatabaseClients.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                result.add(dataSnapshot.getValue(Subject.class));
                mSubjectAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Subject subject = dataSnapshot.getValue(Subject.class);
                int index = getItemIndex(subject);
                result.set(index,subject);
                mSubjectAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Subject subject = dataSnapshot.getValue(Subject.class);
                int index = getItemIndex(subject);
                result.remove(index);
                mSubjectAdapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int getItemIndex(Subject subject){
        int index = -1;
        for(int i=0;i<result.size();i++){
            if(result.get(i).getSubjectCode().equals(subject.getSubjectCode())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public void showToast(String str){
        if(mToast!=null)
            mToast.cancel();

        mToast = Toast.makeText(this,str,Toast.LENGTH_SHORT);
        mToast.show();
    }


    //기기를 새로 등록할때 나오는 창
    public void showRegisterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.info_register)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener(){
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){

                        mReserveRef.setValue(new StudentDevice(mStudentId,FirebaseInstanceId.getInstance().getToken()));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();
    }

    //현재 기기와 등록된 기기가 다를때 나오는 메세지창
    public void showReRegisterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.info_re_register)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener(){
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        //현재 등록된 기기를 초기화하고
                        //해당 기기를 CS센터에 등록한다
                        mDeviceRef.setValue(new StudentDevice(mStudentId,Constants.deviceNotRegisterd));
                        mReserveRef.setValue(new StudentDevice(mStudentId,FirebaseInstanceId.getInstance().getToken()));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    // 취소 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton){
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();
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
}
