package dgu.donggukeas_prof.adapter;

/**
 * Created by hanseungbeom on 2017. 11. 7..
 * Edited by francisbae on 2017. 11. 17..
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import dgu.donggukeas_prof.R;
import dgu.donggukeas_prof.model.SubjectInfo;
import dgu.donggukeas_prof.ui.AttendanceActivity;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    private int subjectColors[] = {
        R.color.material_teal,
        R.color.material_pink,
        R.color.material_amber,
        R.color.material_indigo,
        R.color.material_depp_orange,
        R.color.material_deep_purple
    };

    private Context mContext;
    private ArrayList<SubjectInfo> mSubjects;
    public SubjectAdapter(Context context, ArrayList<SubjectInfo> result){
        mContext = context;
        mSubjects = result;
    }

    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_subject,parent,false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, int position) {
        SubjectInfo subject = mSubjects.get(position);
        holder.subjectName.setText(subject.getSubjectName());
        holder.subjectCode.setText(subject.getSubjectCode());
        holder.subjectNumOfStudents.setText(subject.getNumOfStudents()+"명");
        holder.subjectColor.setBackgroundResource(subjectColors[position%6]); //강좌별로 다른 색깔 출력
    }

    @Override
    public int getItemCount() {
        return mSubjects.size();
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView subjectCode;
        TextView subjectName;
        TextView subjectNumOfStudents;
        LinearLayout layout;
        LinearLayout subjectColor;

        public SubjectViewHolder(View itemView){
            super(itemView);
            layout = (LinearLayout)itemView.findViewById(R.id.layout_subject);
            subjectColor = (LinearLayout)itemView.findViewById(R.id.ll_subjectColor);
            subjectCode = (TextView)itemView.findViewById(R.id.tv_subject_code);
            subjectName = (TextView)itemView.findViewById(R.id.tv_subject_name);
            subjectNumOfStudents = (TextView)itemView.findViewById(R.id.tv_subject_numOfStudents);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv_sc = (TextView)v.findViewById(R.id.tv_subject_code);
                    TextView tv_sn = (TextView)v.findViewById(R.id.tv_subject_name);
                    //Log.d("#####",tv_sc.getText().toString());

                    Intent i = new Intent(mContext, AttendanceActivity.class);
                    i.putExtra(mContext.getString(R.string.extra_subject_code),tv_sc.getText().toString());
                    i.putExtra(mContext.getString(R.string.extra_subject_name),tv_sn.getText().toString());
                    mContext.startActivity(i);
                }
            });
        }
        @Override
        public void onClick(View v) {}
    }
}