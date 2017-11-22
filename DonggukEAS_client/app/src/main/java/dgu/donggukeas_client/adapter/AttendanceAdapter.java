package dgu.donggukeas_client.adapter;

/**
 * Created by hanseungbeom on 2017. 11. 22..
 */

        import android.content.Context;
        import android.support.v7.widget.RecyclerView;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.TextView;

        import java.util.ArrayList;

        import dgu.donggukeas_client.R;
        import dgu.donggukeas_client.model.AttendanceInfo;
        import dgu.donggukeas_client.model.SubjectInfo;
        import dgu.donggukeas_client.model.firebase.AttendanceStatus;
        import dgu.donggukeas_client.util.Constants;


/**
 * Created by hansb on 2017-09-06.
 */


public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    private Context mContext;
    private ArrayList<AttendanceInfo> mAttendances;
    public AttendanceAdapter(Context context, ArrayList<AttendanceInfo> result){
        mContext = context;
        mAttendances = result;
    }

    @Override
    public AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_attendance,parent,false);
        return new AttendanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AttendanceViewHolder holder, int position) {
        AttendanceInfo attendanceInfo = mAttendances.get(position);
        holder.week.setText(String.valueOf(attendanceInfo.getWeek())+"주차");
        switch(attendanceInfo.getAttendanceStatus()){
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
        return mAttendances.size();
    }

    public void swapData(){

    }

    public class AttendanceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView week;
        ImageView attendance;
        LinearLayout attendanceBackground;

        public AttendanceViewHolder(View itemView){
            super(itemView);
            week = (TextView)itemView.findViewById(R.id.tv_week);
            attendance = (ImageView) itemView.findViewById(R.id.iv_attendance);
            attendanceBackground = (LinearLayout)itemView.findViewById(R.id.ll_bg);
        }

        @Override
        public void onClick(View v) {

        }

    }
}