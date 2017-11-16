package dgu.donggukeas_admin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import dgu.donggukeas_admin.R;
import dgu.donggukeas_admin.model.StudentInfo;
import dgu.donggukeas_admin.util.Constants;

/**
 * Created by hansb on 2017-09-06.
 */

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {
    private Context mContext;
    private ArrayList<StudentInfo> students;
    public AttendanceAdapter(Context context, ArrayList<StudentInfo> result){
        mContext = context;
        students = result;
    }

    @Override
    public AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_attendance,parent,false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttendanceViewHolder holder, int position) {
        StudentInfo studentInfo = students.get(position);
        holder.studentId.setText(studentInfo.getStudentId());
        holder.studentName.setText(studentInfo.getStudentName());
        switch(studentInfo.getAttendanceStatus()){
            case Constants.ATTENDANCE_OK://출석
                holder.attendance.setImageResource(R.drawable.ic_check);
                holder.attendanceBackground.setBackgroundResource(R.color.material_green);
                break;
            case Constants.ATTENDANCE_ABSENCE://결석
                holder.attendance.setImageResource(R.drawable.ic_absence);
                holder.attendanceBackground.setBackgroundResource(R.color.material_red);
                break;

            case Constants.ATTENDANCE_LATE://지각
                holder.attendance.setImageResource(R.drawable.ic_late);
                holder.attendanceBackground.setBackgroundResource(R.color.material_amber);
                break;
            case Constants.ATTENDANCE_RUN://출튀
                holder.attendance.setImageResource(R.drawable.ic_run);
                holder.attendanceBackground.setBackgroundResource(R.color.material_red);
                break;
            case Constants.ATTENDANCE_NONE://미처리
                holder.attendance.setImageResource(R.drawable.ic_none);
                holder.attendanceBackground.setBackgroundResource(R.color.material_grey);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public void swapData(){

    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView studentId;
        TextView studentName;
        ImageView attendance;
        LinearLayout attendanceBackground;

        public AttendanceViewHolder(View itemView){
            super(itemView);
            studentId = (TextView)itemView.findViewById(R.id.tv_student_id);
            studentName = (TextView)itemView.findViewById(R.id.tv_student_name);
            attendance = (ImageView) itemView.findViewById(R.id.iv_attendance);
            attendanceBackground = (LinearLayout)itemView.findViewById(R.id.ll_bg);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
