package dgu.donggukeas_prof.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dgu.donggukeas_prof.R;
import dgu.donggukeas_prof.model.StudentInfo;
import dgu.donggukeas_prof.model.firebase.AttendanceStatus;
import dgu.donggukeas_prof.util.Constants;

import static dgu.donggukeas_prof.R.color.Grey900;

/**
 * Created by francisbae on 2017-11-17.
 *
 */

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private Context mContext;
    private ArrayList<StudentInfo> students;
    private String mSubjectCode;
    private int mCurrentWeek;

    public AttendanceAdapter(Context context, ArrayList<StudentInfo> result, String subjectCode){
        mContext = context;
        students = result;
        mSubjectCode = subjectCode;
    }
    private AlertDialog mMenuDialog;


    @Override
    public AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_attendance,parent,false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttendanceViewHolder holder, int position) {
        StudentInfo studentInfo = students.get(position);
        holder.studentId.setText(studentInfo.getStudentId());
        holder.studentName.setText(studentInfo.getStudentName());

        holder.curWeek = mCurrentWeek;

        Log.d("#####", "curWeek : "+holder.curWeek+" / mCurrentWeek : "+mCurrentWeek);

        switch(studentInfo.getAttendanceStatus()){
            case Constants.ATTENDANCE_OK://출석
                holder.attendance.setImageResource(R.drawable.ic_check);
                holder.attendanceBackground.setBackgroundResource(R.color.material_green);

                holder.attendanceInfo.setBackgroundResource(R.color.Grey100);
                holder.studentName.setTextColor(Color.parseColor("#616161"));
                holder.studentId.setTextColor(Color.parseColor("#616161"));

                holder.attendanceBackground.setTag("tag_ok");
                break;
            case Constants.ATTENDANCE_ABSENCE://결석
                holder.attendance.setImageResource(R.drawable.ic_absence);
                holder.attendanceBackground.setBackgroundResource(R.color.material_red);

                holder.attendanceInfo.setBackgroundResource(R.color.Grey100);
                holder.studentName.setTextColor(Color.parseColor("#616161"));
                holder.studentId.setTextColor(Color.parseColor("#616161"));

                holder.attendanceBackground.setTag("tag_absence");
                break;

            case Constants.ATTENDANCE_LATE://지각
                holder.attendance.setImageResource(R.drawable.ic_late);
                holder.attendanceBackground.setBackgroundResource(R.color.material_amber);

                holder.attendanceInfo.setBackgroundResource(R.color.Grey100);
                holder.studentName.setTextColor(Color.parseColor("#616161"));
                holder.studentId.setTextColor(Color.parseColor("#616161"));

                holder.attendanceBackground.setTag("tag_late");
                break;
            case Constants.ATTENDANCE_RUN://출튀
                holder.attendance.setImageResource(R.drawable.ic_run);
//                holder.attendanceBackground.setBackgroundResource(R.color.material_red);
                holder.attendanceBackground.setBackgroundResource(Grey900);
                //
                holder.attendanceInfo.setBackgroundResource(R.color.material_red);
                holder.studentName.setTextColor(Color.parseColor("#fafafa"));
                holder.studentId.setTextColor(Color.parseColor("#fafafa"));

                holder.attendanceBackground.setTag("tag_run");
                break;
            case Constants.ATTENDANCE_NONE://미처리
                holder.attendance.setImageResource(R.drawable.ic_none);
                holder.attendanceBackground.setBackgroundResource(R.color.material_grey);
                holder.attendanceInfo.setBackgroundResource(R.color.Grey100);
                holder.studentName.setTextColor(Color.parseColor("#616161"));
                holder.studentId.setTextColor(Color.parseColor("#616161"));

                holder.attendanceBackground.setTag("tag_none");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void swapData(){

    }
    public void setWeek(int curWeek)
    {
        mCurrentWeek = curWeek;
        Log.d("#####","현재 : "+curWeek+"주");
    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView studentId;
        TextView studentName;
        ImageView attendance;
        LinearLayout attendanceBackground;
        //
        LinearLayout attendanceInfo;
        int attendanceStat;
        int curWeek;
        //

        public AttendanceViewHolder(final View itemView){
            super(itemView);
            studentId = (TextView)itemView.findViewById(R.id.tv_student_id);
            studentName = (TextView)itemView.findViewById(R.id.tv_student_name);
            attendance = (ImageView) itemView.findViewById(R.id.iv_attendance);
            attendanceBackground = (LinearLayout)itemView.findViewById(R.id.ll_bg);
            //
            attendanceInfo = (LinearLayout)itemView.findViewById(R.id.ll_infobg);

           attendanceBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeAttendanceStatus();
                }
            });



        }

        public void changeAttendanceStatus(){
            CharSequence[] mItems = {"미처리","출석","결석","지각","출튀"};
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            if(attendanceBackground.getTag().toString().equals("tag_ok"))
            {
                attendanceStat = Constants.ATTENDANCE_OK;
            }
            else if(attendanceBackground.getTag().toString().equals("tag_absence"))
            {
                attendanceStat = Constants.ATTENDANCE_ABSENCE;
            }
            else if(attendanceBackground.getTag().toString().equals("tag_late"))
            {
                attendanceStat = Constants.ATTENDANCE_LATE;
            }
            else if(attendanceBackground.getTag().toString().equals("tag_run"))
            {
                attendanceStat = Constants.ATTENDANCE_RUN;
            }
            else {
                attendanceStat = Constants.ATTENDANCE_NONE;
            }

            builder.setTitle(studentId.getText()+" "+studentName.getText());

            builder.setSingleChoiceItems(mItems, attendanceStat, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    FirebaseDatabase mDatabase;
                    DatabaseReference mStudentReference,mDeviceReference,mAttendanceReference;

                    mDatabase = FirebaseDatabase.getInstance();
                    mAttendanceReference = mDatabase.getReference("STUDENT_ATTENDANCE");


                    //mContext.getResources().getLayout()
                    Log.d("#####","업데이트된 주는 "+curWeek);

                    mAttendanceReference = mAttendanceReference.child(mSubjectCode).child(curWeek+"");

                    int idx = getStudentIndex(studentId.getText().toString());
                    AttendanceStatus mAttendanceStatus = new AttendanceStatus(students.get(idx).getStudentId(), item);

                    Map<String, Object> mAttendanceValues = mAttendanceStatus.toMap();
                    Map<String, Object> mNewAttendance = new HashMap<>();

                    mNewAttendance.put(mAttendanceStatus.getStudentId(), mAttendanceValues);
                    mAttendanceReference.updateChildren(mNewAttendance);



                    //reference.updateChildren(newUser);
/*
                    Log.d("#####",idx+"");

                    switch(item)
                    {
                        case 0:
                            Toast.makeText(mContext, "First Item Clicked", Toast.LENGTH_LONG).show();
                            break;
                        case 1:
                            Toast.makeText(mContext, "Second Item Clicked", Toast.LENGTH_LONG).show();
                            break;
                        case 2:
                            Toast.makeText(mContext, "Third Item Clicked", Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            Toast.makeText(mContext, "First Item Clicked", Toast.LENGTH_LONG).show();
                            break;
                        case 4:
                            Toast.makeText(mContext, "Second Item Clicked", Toast.LENGTH_LONG).show();
                            break;
                    }*/
                    mMenuDialog.dismiss();
                }
            });
            mMenuDialog = builder.create();
            mMenuDialog.show();

        }

        public int getStudentIndex(String studentId){
            int index = -1;
            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).getStudentId().equals(studentId)) {
                    index = i;
                    break;
                }
            }
            return index;
        }


        @Override
        public void onClick(View v) {

        }
    }
}
