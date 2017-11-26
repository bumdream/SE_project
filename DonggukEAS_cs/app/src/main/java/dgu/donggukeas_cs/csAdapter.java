package dgu.donggukeas_cs;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class csAdapter extends RecyclerView.Adapter<csAdapter.csViewHolder>{

    private List<csModel> list;
    private List<String> idList = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference stu_reference;
    private DatabaseReference reference;
    private DatabaseReference dev_reference;


    public csAdapter(List<csModel> list){
        this.list =list;
    }

    @Override
    public csViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new csViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false));
    }
    @Override
    public void onBindViewHolder(csViewHolder holder, final int position) {
        csModel cs = list.get(position);

        holder.deviceTOKEN.setText(cs.deviceToken);
        holder.studentID.setText(cs.studentId);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    class csViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView studentID, deviceTOKEN;
        Button btn_register;

        public csViewHolder(View itemView){
            super(itemView);

            studentID= (TextView)itemView.findViewById(R.id.studentId);
            deviceTOKEN= (TextView)itemView.findViewById(R.id.deviceToken);
            btn_register= (Button)itemView.findViewById(R.id.btn_register);

            btn_register.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(final View view) {
                                                    database = database.getInstance();
                                                    dev_reference = database.getReference("STUDENT_DEVICE");
                                                    reference = database.getReference("RESERVATION_DEVICE");
                                                    stu_reference = database.getReference();
                                                    stu_reference.child("STUDENT").orderByChild("studentId").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        boolean control = false;
                                                        int index = getAdapterPosition();
                                                        csModel cs = list.get(index);
                                                        Map<String, Object> csValues = cs.toMap();
                                                        Map<String, Object> newCS = new HashMap<>();

                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            idList.clear();
                                                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                                                idList.add(child.getKey());
                                                            }

                                                            for (int j = 0; j < idList.size(); j++) {
                                                                if (idList.get(j).equals(cs.studentId)) {
                                                                    control = true;
                                                                    Toast.makeText( view.getContext(),"registered", Toast.LENGTH_SHORT).show();

                                                                    break;
                                                                }
                                                            }
                                                            if (control) {
                                                                newCS.put(cs.studentId, csValues);
                                                                dev_reference.updateChildren(newCS);
                                                                reference.child(cs.studentId).removeValue();
                                                            } else {
                                                                Toast.makeText( view.getContext(),"not exist", Toast.LENGTH_SHORT).show();
                                                            }
                                                            control = false;
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
                                                    });
                                                }
                                            });
        }

        @Override
        public void onClick(View view) {
        }
    }
}
