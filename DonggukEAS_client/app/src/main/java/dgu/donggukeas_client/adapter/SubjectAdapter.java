package dgu.donggukeas_client.adapter;

/**
 * Created by hanseungbeom on 2017. 11. 7..
 */
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dgu.donggukeas_client.R;
import dgu.donggukeas_client.model.SubjectInfo;
import dgu.donggukeas_client.ui.AttendanceActivity;
import dgu.donggukeas_client.ui.MainActivity;


/**
 * Created by hansb on 2017-09-06.
 */


public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private Context mContext;
    private ArrayList<SubjectInfo> mSubjectInfos;
    public SubjectAdapter(Context context, ArrayList<SubjectInfo> result){
        mContext = context;
        mSubjectInfos = result;
    }

    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_subject,parent,false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, int position) {
        SubjectInfo subjectInfo = mSubjectInfos.get(position);
        holder.subjectName.setText(subjectInfo.getSubjectName());
        holder.subjectCode.setText(subjectInfo.getSubjectCode());
        //현재 출석이 진행중이면 불을 킨다.
        if(subjectInfo.isAttendanceChecking()){
            holder.attendanceLight.setImageResource(R.drawable.ic_circle_green);

        }
        else{
            holder.attendanceLight.setImageResource(R.drawable.ic_circle_grey);

        }


    }

    @Override
    public int getItemCount() {
        return mSubjectInfos.size();
    }


    public class SubjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView subjectCode;
        TextView subjectName;
        ImageView attendanceLight;


        public SubjectViewHolder(View itemView){
            super(itemView);
            subjectCode = (TextView)itemView.findViewById(R.id.tv_subject_code);
            subjectName = (TextView)itemView.findViewById(R.id.tv_subject_name);
             attendanceLight = (ImageView)itemView.findViewById(R.id.iv_attendance_light);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int index = getAdapterPosition();
            SubjectInfo subjectInfo = mSubjectInfos.get(index);
            Intent i = new Intent(mContext, AttendanceActivity.class);
            i.putExtra(mContext.getString(R.string.extra_subject_code),subjectInfo.getSubjectCode());
            i.putExtra(mContext.getString(R.string.extra_subject_name),subjectInfo.getSubjectName());
            mContext.startActivity(i);
        }
    }
}