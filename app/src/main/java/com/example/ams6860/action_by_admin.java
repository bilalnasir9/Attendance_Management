package com.example.ams6860;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class action_by_admin extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    ProgressDialog progressDialog;
    alluserview obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_by_admin);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        Button btnviewall = findViewById(R.id.btnactionviewall);
        Button btnedit = findViewById(R.id.btnactionedit);
        Button btndelete = findViewById(R.id.btnactiondelete);
        Button btnaproverequest = findViewById(R.id.btnaproverequest);

        Intent intent = new Intent(getIntent());
        String id = intent.getStringExtra("id");

        btnaproverequest.setOnClickListener(view -> {
            progressDialog.show();
            databaseReference.child("admin").child("leaves").child(id).get().addOnCompleteListener(task -> {
                try {
                    String request = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getValue()).toString();
                    if (request.equals("pending")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this).
                                setMessage("user leave request is pending").setPositiveButton("aprove", (dialogInterface, i) -> {
                            progressDialog.dismiss();
                            databaseReference.child("admin").child("leaves").child(id).setValue("aproved");

                        }).setNegativeButton("cancel", (dialogInterface, i) -> {
                            progressDialog.dismiss();
                        });
                        builder.show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "user has no pending leave request", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(this, "user has no pending leave request", Toast.LENGTH_SHORT).show();
                }

            });
        });

        btnviewall.setOnClickListener(view -> {
            DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("users").child(id).child("Attendance");
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value1 = "";
                    int count = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String value = ds.getKey();
                        value1 = value1 + "\nDate:\t" + value;
                        count += 1;
                    }
                    progressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(action_by_admin.this);
                    builder.setTitle("Attendance history")
                            .setMessage("Total Attendance:\t" + count + "\n" + value1);
                    builder.setNegativeButton("OK", (dialogInterface, i) -> {

                    });
                    builder.show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("TAG", databaseError.getMessage()); //Don't ignore potential errors!
                }
            };
            itemsRef.addListenerForSingleValueEvent(eventListener);
        });
        btnedit.setOnClickListener(view -> {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage("Select attendance status for today");
            builder.setPositiveButton("Present", (dialogInterface, i) -> {
                progressDialog.show();
                databaseReference.child("users").child(id).child("Attendance").child(currentDate).setValue("present");

                progressDialog.dismiss();
            }).setNegativeButton("absent", (dialogInterface, i) -> {
                progressDialog.show();
                databaseReference.child("users").child(id).child("Attendance").child(currentDate).setValue("absent");

                progressDialog.dismiss();
            });
            builder.show();
        });
        btndelete.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage("Delete user all attendance ");
            builder.setPositiveButton("Delete", (dialogInterface, i) -> {
                progressDialog.show();
                databaseReference.child("users").child(id).child("Attendance").removeValue();
                progressDialog.dismiss();
            }).setNegativeButton("cancel", (dialogInterface, i) -> {
                progressDialog.dismiss();
            });
            builder.show();
        });
    }
}