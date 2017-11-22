package dgu.donggukeas_client.ui;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import dgu.donggukeas_client.R;

public class QRActivity extends AppCompatActivity  {


    private String mStudentId;
    private ImageView mQRImg;
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qr);

        mStudentId = (getIntent().getStringExtra("studentId"));
        mQRImg = (ImageView)findViewById(R.id.iv_qr);
        try {
            mQRImg.setImageBitmap(encodeAsBitmap(mStudentId.toString()));
            Toast.makeText(this,getString(R.string.info_QrGenerate),Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){}

    }
/*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }*/

    Bitmap encodeAsBitmap(String str) throws WriterException {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = (int)(displayMetrics.heightPixels * 0.4);
        int width = (int)(displayMetrics.widthPixels * 0.7);


        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE,width ,height , null);
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

    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }
}
