package com.example.ams6860;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class admin_panel extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    TextView tvname;
    String result = "";
    TextView tvmmessage;
    ProgressDialog progressDialog;
    String[] userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        tvname = findViewById(R.id.tvadminname);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        Button btnleaverequest = findViewById(R.id.btnuserleaverequest);
        Button btnviewalluser = findViewById(R.id.btnviewalluser);
        btnviewalluser.setOnClickListener(view ->
                startActivity(new Intent(admin_panel.this,alluserview.class)));

        btnleaverequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getleavemessage();
            }
        });

        adminlogin();

    }

    public void adminlogin() {
        databaseReference.child("admin").child("name").get().addOnCompleteListener(task -> {
            String name = task.getResult().getValue().toString();
            tvname.setText(name);
        });
    }

    public void getleavemessage() {
        result = "";
        progressDialog.show();
        databaseReference.child("admin").child("leaves").addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int c = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.getKey();
                    String message = ds.getValue().toString();
                    result = result + "USER ID:\t" + key + "\nUSER MESSAGE:\t" + message + "\n\n";

                }
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(admin_panel.this).setTitle("All Leave request");
                builder.setMessage(result).setNegativeButton("OK", (dialogInterface, i) -> {
                }).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

    }


}
