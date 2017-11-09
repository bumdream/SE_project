package dgu.donggukeas_client.adapter;

/**
 * Created by hanseungbeom on 2017. 11. 7..
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

import dgu.donggukeas_client.R;
import dgu.donggukeas_client.model.Subject;


/**
 * Created by hansb on 2017-09-06.
 */


public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private Context mContext;
    private ArrayList<Subject> subjects;
    public SubjectAdapter(Context context, ArrayList<Subject> result){
        mContext = context;
        subjects = result;
    }

    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_subject,parent,false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, int position) {
        Subject subject = subjects.get(position);
        holder.subjectName.setText(subject.getSubjectName());
        holder.subjectCode.setText(subject.getSubjectCode());
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public void swapData(){

    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView subjectCode;
        TextView subjectName;


        public SubjectViewHolder(View itemView){
            super(itemView);
            subjectCode = (TextView)itemView.findViewById(R.id.tv_subject_code);
            subjectName = (TextView)itemView.findViewById(R.id.tv_subject_name);
        }

        @Override
        public void onClick(View v) {

        }
    }
}