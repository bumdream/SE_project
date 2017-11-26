package dgu.donggukeas_prof.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dgu.donggukeas_prof.R;
import dgu.donggukeas_prof.model.RunawayInfo;
import dgu.donggukeas_prof.model.firebase.AttendanceStatus;
import dgu.donggukeas_prof.util.Constants;

/**
 * Created by francisbae on 2017-11-20.
 */


public class RunawayAdapter extends RecyclerView.Adapter<RunawayAdapter.RunawayViewHolder> {
    private Context mContext;
    private ArrayList<RunawayInfo> mRStudents;
    private String mSubjectCode;
    private int mCurrentWeek;
    private final AdapterInterface mInterface;
    public interface AdapterInterface{
        void showEmptyView();
    };
    public RunawayAdapter(Context context, ArrayList<RunawayInfo> result, String subjectCode,AdapterInterface inter){
        mContext = context;
        mRStudents = result;
        mSubjectCode = subjectCode;
        mInterface = inter;
    }

    public void setWeek(int curWeek)
    {
        mCurrentWeek = curWeek;
        Log.d("#####","현재 : "+curWeek+"주");
    }

    @Override
    public RunawayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_runaway,parent,false);
        return new RunawayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RunawayViewHolder holder, int position) {
        RunawayInfo rInfo = mRStudents.get(position);
        holder.studentOrderNum.setText("( "+(position+1)+" / "+getItemCount()+" )");
        holder.studentName.setText(rInfo.getStudentName());
        holder.studentId.setText(rInfo.getStudentId());
    }

    @Override
    public int getItemCount() {
        return mRStudents.size();
    }

    public void swapData(){

    }

    public class RunawayViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView studentOrderNum;
        TextView studentId;
        TextView studentName;
        LinearLayout llRunaway;
        LinearLayout llCheck;



        public RunawayViewHolder(final View itemView) {
            super(itemView);
            studentOrderNum = (TextView)itemView.findViewById(R.id.tv_ra_sOrder);
            studentId = (TextView) itemView.findViewById(R.id.tv_raSID);
            studentName = (TextView) itemView.findViewById(R.id.tv_raSName);

            llCheck = (LinearLayout)itemView.findViewById(R.id.llCheck);
            llRunaway = (LinearLayout)itemView.findViewById(R.id.llRunaway);

            llCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase mDatabase;
                    DatabaseReference mRunawayReference;
                    mDatabase = FirebaseDatabase.getInstance();
                    mRunawayReference = mDatabase.getReference("RUNAWAY_STUDENT");

                    int currPos = ((RecyclerView)itemView.getParent()).getChildAdapterPosition(itemView);
                    mRunawayReference.child(mSubjectCode).child(studentId.getText().toString()).removeValue();

                    try {
                        mRStudents.remove(currPos);
                        notifyItemRemoved(currPos);
                        notifyItemRangeChanged(0, getItemCount());
                    }
                    catch (Exception e){
                        Toast.makeText(mContext, "갱신 실패, 다시 시도해주세요.",Toast.LENGTH_SHORT).show();
                    }
                    if(mRStudents.size() == 0)
                    {
                        Toast.makeText(mContext, "처리 완료.",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            llRunaway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase mDatabase;
                    DatabaseReference mRunawayReference, mAttendanceReference;
                    mDatabase = FirebaseDatabase.getInstance();
                    mRunawayReference = mDatabase.getReference("RUNAWAY_STUDENT");
                    mAttendanceReference = mDatabase.getReference("STUDENT_ATTENDANCE");

                    int currPos = ((RecyclerView)itemView.getParent()).getChildAdapterPosition(itemView);
                    mRunawayReference.child(mSubjectCode).child(studentId.getText().toString()).removeValue();

                    try {

                        mRStudents.remove(currPos);

                        try {
                            mAttendanceReference = mAttendanceReference.child(mSubjectCode).child(mCurrentWeek + "");
                            AttendanceStatus mAttendanceStatus = new AttendanceStatus(studentId.getText().toString(), Constants.ATTENDANCE_RUN);

                            Map<String, Object> mAttendanceValues = mAttendanceStatus.toMap();
                            Map<String, Object> mNewAttendance = new HashMap<>();

                            mNewAttendance.put(mAttendanceStatus.getStudentId(), mAttendanceValues);
                            mAttendanceReference.updateChildren(mNewAttendance);

                        }
                        catch (Exception e){
                            Toast.makeText(mContext, "갱신 실패, 다시 시도해주세요.",Toast.LENGTH_SHORT).show();
                        }

                        notifyItemRemoved(currPos);
                        notifyItemRangeChanged(0, getItemCount());
                        if(mRStudents.size()==0)
                            mInterface.showEmptyView();


                    }
                    catch (Exception e){
                        Toast.makeText(mContext, "삭제 실패, 다시 시도해주세요.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        @Override
        public void onClick(View v) {

        }

    }
}