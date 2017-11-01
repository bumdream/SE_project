package dgu.donggukeas_admin.ui;

import android.Manifest;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import dgu.donggukeas_admin.R;
import dgu.donggukeas_admin.adapter.RegisterAdapter;
import dgu.donggukeas_admin.util.AppPermissions;
import dgu.donggukeas_admin.util.PointsOverlayView;
import dgu.donggukeas_client.model.WaitingClient;

public class MainActivity extends AppCompatActivity  implements QRCodeReaderView.OnQRCodeReadListener {
    private RecyclerView mRegisterRecyclerView;
    private ArrayList<WaitingClient> result;
    private RegisterAdapter mAdapter;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private QRCodeReaderView qrCodeReaderView;
    private PointsOverlayView pointsOverlayView;
    private TextView mDecode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDecode = (TextView)findViewById(R.id.tv_decode);
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference(getString(R.string.register_db_table));

        result = new ArrayList<>();
        mRegisterRecyclerView = (RecyclerView)findViewById(R.id.rv_register);
        mRegisterRecyclerView.setHasFixedSize(true);
        mRegisterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pointsOverlayView = (PointsOverlayView) findViewById(R.id.points_overlay_view);

        mAdapter = new RegisterAdapter(this,result);
        mRegisterRecyclerView.setAdapter(mAdapter);


        updateList();

            qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
            qrCodeReaderView.setOnQRCodeReadListener(this);

            // Use this function to enable/disable decoding
            qrCodeReaderView.setQRDecodingEnabled(true);

            // Use this function to change the autofocus interval (default is 5 secs)
            qrCodeReaderView.setAutofocusInterval(2000L);

            // Use this function to enable/disable Torch
            qrCodeReaderView.setTorchEnabled(true);

            // Use this function to set front camera preview
            qrCodeReaderView.setBackCamera();



    }


    private void updateList(){
        mReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                result.add(dataSnapshot.getValue(WaitingClient.class));
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                WaitingClient waitingClient = dataSnapshot.getValue(WaitingClient.class);
                int index = getItemIndex(waitingClient);
                result.set(index,waitingClient);
                mAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                WaitingClient waitingClient = dataSnapshot.getValue(WaitingClient.class);
                int index = getItemIndex(waitingClient);
                result.remove(index);
                mAdapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int getItemIndex(WaitingClient waitingClient){
        int index = -1;
        for(int i=0;i<result.size();i++){
            if(result.get(i).getStudentId().equals(waitingClient.getStudentId())) {
                index = i;
                break;
            }
        }
        return index;
    }


    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        mDecode.setText(text);
        pointsOverlayView.setPoints(points);
    }



    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

}
