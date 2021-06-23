package com.example.ams6860;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class alluserview extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    List<String> listid = new ArrayList<>();
    List<String> listname = new ArrayList<>();
    List<String> listattendance = new ArrayList<>();
    ProgressDialog pd;
    adapterclass obj;
    RecyclerView recyclerView;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alluserview);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.show();
        loadactivity();


    }

    public void loadactivity() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
//        FirebaseDatabase db=FirebaseDatabase.getInstance();
//        DatabaseReference ref=db.getReference();
        reference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listid.clear();
                listname.clear();
                listattendance.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String key = ds.getKey();
                    String name = ds.child("name").getValue().toString();
                    listid.add(key);
                    listname.add(name);
                    try {
                        String attendance = ds.child("Attendance").child(currentDate).getValue().toString();
                        listattendance.add(attendance);
                    } catch (Exception e) {
                        listattendance.add("absent");
                    }
                }


                obj = new adapterclass(alluserview.this, listname, listid, listattendance);
                 recyclerView = findViewById(R.id.recycler);
                recyclerView.setAdapter(obj);
                recyclerView.setLayoutManager(new LinearLayoutManager(alluserview.this));
                pd.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
            }
        });
    }


}