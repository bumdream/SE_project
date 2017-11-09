package dgu.donggukeas_client.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Calendar;

import dgu.donggukeas_client.R;
import dgu.donggukeas_client.model.Student;
import dgu.donggukeas_client.model.WaitingClient;


public class MainActivity extends AppCompatActivity {

    private String mStudentId;
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

        mDatabaseClients = FirebaseDatabase.getInstance().getReference(getString(R.string.table_student_classes));
        mStudentId = getIntent().getStringExtra(getString(R.string.extra_id));
        if(mStudentId!=null) {
            mDatabaseClients.child(mStudentId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            Student stu = dataSnapshot.getValue(Student.class);
                            if (stu != null) {

                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("#####", "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }
       /* mRegisterBtn = (Button)findViewById(R.id.btn_register);
        mDeviceId = (TextView)findViewById(R.id.tv_device_id);
        mQrView = (ImageView) findViewById(R.id.iv_qr);
        mCreateQRBtn = (Button)findViewById(R.id.btn_create_qr);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerId();
            }
        });
        mCreateQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentId = mStudentId.getText().toString();
                if(!TextUtils.isEmpty(studentId)){
                    try {
                        Calendar cal = java.util.Calendar.getInstance();
                        Bitmap bitmap = encodeAsBitmap(studentId+" "+getDateFromMilli(cal.getTimeInMillis()));
                        mQrView.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }

            }
        });*/


    }

/*    private void registerId(){
      //  String studentId = mStudentId.getText().toString();

        if(!TextUtils.isEmpty(studentId)){
            String deviceId = FirebaseInstanceId.getInstance().getToken();
            WaitingClient client = new WaitingClient(studentId,deviceId);
            mDatabaseClients.child(studentId).setValue(client);
            Toast.makeText(this,"device added",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"empty name",Toast.LENGTH_SHORT).show();
        }
    }*/

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
