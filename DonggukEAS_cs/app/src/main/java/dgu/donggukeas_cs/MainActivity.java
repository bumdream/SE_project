package dgu.donggukeas_cs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<csModel> result;
    private csAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = database.getInstance();
        reference = database.getReference("RESERVATION_DEVICE");

        emptyText = (TextView) findViewById(R.id.no_input_data);
        result = new ArrayList<>();

        recyclerView =(RecyclerView) findViewById(R.id.cs_list);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager lim = new LinearLayoutManager(this);
        lim.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(lim);

        adapter=new csAdapter(result);
        recyclerView.setAdapter(adapter);

        updateList();
        checkIfEmpty();
    }

    private void updateList(){
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                result.add(dataSnapshot.getValue(csModel.class));
                adapter.notifyDataSetChanged();
                checkIfEmpty();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                csModel model = dataSnapshot.getValue(csModel.class);
                int index = getItemIndex(model);
                result.set(index, model);
                adapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                csModel model = dataSnapshot.getValue(csModel.class);
                int index = getItemIndex(model);
                result.remove(index);
                adapter.notifyItemRemoved(index);
                checkIfEmpty();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }
    private int getItemIndex(csModel user) {
        int index = -1;
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).studentId.equals(user.studentId)) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void checkIfEmpty(){
        if(result.size()==0)
        {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        }else
        {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }
}
