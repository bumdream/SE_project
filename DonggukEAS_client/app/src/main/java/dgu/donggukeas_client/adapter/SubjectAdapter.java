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


/**
 * Created by hansb on 2017-09-06.
 */

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.RegisterViewHolder> {
    private Context mContext;
    private ArrayList<WaitingClient> waitingClients;
    public SubjectAdapter(Context context, ArrayList<WaitingClient> result){
        mContext = context;
        waitingClients = result;
    }

    @Override
    public RegisterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_register,parent,false);
        return new RegisterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RegisterViewHolder holder, int position) {
        WaitingClient wc = waitingClients.get(position);
        holder.studentId.setText(wc.getStudentId());
        holder.phoneId.setText(wc.getPhoneId());
    }

    @Override
    public int getItemCount() {
        return waitingClients.size();
    }

    public void swapData(){

    }

    public class RegisterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView studentId;
        TextView phoneId;
        Button btnRegister;

        public RegisterViewHolder(View itemView){
            super(itemView);
            studentId = (TextView)itemView.findViewById(R.id.tv_student_id);
            phoneId = (TextView)itemView.findViewById(R.id.tv_phone_id);
            btnRegister = (Button)itemView.findViewById(R.id.btn_register);
        }

        @Override
        public void onClick(View v) {

        }
    }
}