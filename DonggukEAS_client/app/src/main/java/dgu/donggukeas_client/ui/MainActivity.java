package dgu.donggukeas_client.ui;

import android.content.Intent;
import android.graphics.Bitmap;
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


public class MainActivity extends AppCompatActivity {

    private String mStudentId;
    private RecyclerView mRecyclerView;
    private SubjectAdapter mSubjectAdapter;
    private ArrayList<Subject> result;

    private Button mRegisterBtn,mCreateQRBtn;
    private TextView mDeviceId;
    private ImageView mQrView;
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;

    private DatabaseReference mDatabaseClients;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView)findViewById(R.id.rv_subject);
        result = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mSubjectAdapter = new SubjectAdapter(this,result);
        mRecyclerView.setAdapter(mSubjectAdapter);
        
        mStudentId = getIntent().getStringExtra(getString(R.string.extra_id));
        mDatabaseClients = FirebaseDatabase.getInstance().getReference(getString(R.string.table_student_classes)).child(mStudentId);
        updateList();


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
