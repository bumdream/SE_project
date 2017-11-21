package dgu.donggukeas_prof.adapter;

/**
 * Created by hanseungbeom on 2017. 11. 7..
 * Edited by francisbae on 2017. 11. 17..
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    }

    @Override
    public int getItemCount() {
        Log.d("#####","getItemCount 호출"); //이거 문제인듯

        return mSubjects.size();
    }

    public void swapData(){

    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView subjectCode;
        TextView subjectName;
        TextView subjectNumOfStudents;
        LinearLayout layout;


        public SubjectViewHolder(View itemView){
            super(itemView);
            layout = (LinearLayout)itemView.findViewById(R.id.layout_subject);
            subjectCode = (TextView)itemView.findViewById(R.id.tv_subject_code);
            subjectName = (TextView)itemView.findViewById(R.id.tv_subject_name);
            subjectNumOfStudents = (TextView)itemView.findViewById(R.id.tv_subject_numOfStudents);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* TextView subject = (TextView)v.findViewById(R.id.tv_subject_code);
                    Log.d("#####",subject.getText().toString());

                    Intent i = new Intent(mContext, AttendanceActivity.class);
                    i.putExtra(mContext.getString(R.string.extra_subject_code),subject.getText().toString());
                    //Toast.makeText(mContext,subject.getText().toString(), Toast.LENGTH_SHORT).show();
                    mContext.startActivity(i);*/
                    TextView tv_sc = (TextView)v.findViewById(R.id.tv_subject_code);
                    TextView tv_sn = (TextView)v.findViewById(R.id.tv_subject_name);
                    Log.d("#####",tv_sc.getText().toString());

                    Intent i = new Intent(mContext, AttendanceActivity.class);
                    i.putExtra(mContext.getString(R.string.extra_subject_code),tv_sc.getText().toString());
                    i.putExtra(mContext.getString(R.string.extra_subject_name),tv_sn.getText().toString());

                    //Toast.makeText(mContext,subject.getText().toString(), Toast.LENGTH_SHORT).show();
                    mContext.startActivity(i);
                }
            });
        }

        @Override
        public void onClick(View v) {
            /*TextView tv_sc = (TextView)v.findViewById(R.id.tv_subject_code);
            TextView tv_sn = (TextView)v.findViewById(R.id.tv_subject_name);
            Log.d("#####",tv_sc.getText().toString());

            Intent i = new Intent(mContext, AttendanceActivity.class);
            i.putExtra(mContext.getString(R.string.extra_subject_code),tv_sc.getText().toString());
            i.putExtra(mContext.getString(R.string.extra_subject_name),tv_sn.getText().toString());

            //Toast.makeText(mContext,subject.getText().toString(), Toast.LENGTH_SHORT).show();
            mContext.startActivity(i);
*/
        }

    }
}