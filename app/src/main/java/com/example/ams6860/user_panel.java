package com.example.ams6860;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class user_panel extends AppCompatActivity {
    Boolean network = false;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    ProgressDialog progressDialog;
    TextView tvname, tvid;
    ImageView userimage;
    String userid ;
    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_panel);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading user data");
        Button btnmarkattendance = findViewById(R.id.btnmarkattendance);
        Button btviewattendance = findViewById(R.id.btnviewattendance);
        Button btnleaverequest = findViewById(R.id.btnleaverequest);
        ImageButton btnchangeprofile = findViewById(R.id.btn_changeprofile);

        tvname = findViewById(R.id.username);
        tvid = findViewById(R.id.userid);
        userimage = findViewById(R.id.userimge);
        isNetworkAvailable(this);
        Intent intent = new Intent(getIntent());
        userid = intent.getStringExtra("id");
        isNetworkAvailable(this);
        if (!network) {
            Toast.makeText(user_panel.this, "internet connection not available", Toast.LENGTH_SHORT).show();
        }
        else {
            load_userdata();
        }
        btnmarkattendance.setOnClickListener(view -> {
            isNetworkAvailable(this);
            if (!network) {
                Toast.makeText(user_panel.this, "internet connection not available", Toast.LENGTH_SHORT).show();
            }
            else {
                progressDialog.setMessage("uploading attendance");
                markattendance();
            }
        });
        btviewattendance.setOnClickListener(view -> {
            isNetworkAvailable(this);
            if (!network) {
                Toast.makeText(user_panel.this, "internet connection not available", Toast.LENGTH_SHORT).show();
            }
            else {
                progressDialog.setMessage("Downloading attendance history");
                view_attendance();
            }
        });
        btnchangeprofile.setOnClickListener(view -> {
            isNetworkAvailable(this);
            if (!network) {
                Toast.makeText(user_panel.this, "internet connection not available", Toast.LENGTH_SHORT).show();
            }
            else {
                changeprofile();
            }
        });
        btnleaverequest.setOnClickListener(view -> {
            leaverequest();
        });
    }


    private void changeprofile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    public void markattendance() {
        isNetworkAvailable(user_panel.this);

            try {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Attendance");
                dialog.setMessage("Mark your attendance for today, you cannot undo this action");
                dialog.setPositiveButton("Mark Present", (dialogInterface, i) -> {
                    progressDialog.show();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String currentDate = sdf.format(new Date());

                    databaseReference.child("users").child(userid).child("Attendance").child(currentDate).get().
                            addOnCompleteListener(task -> {
                                String value = String.valueOf((Objects.requireNonNull(task.getResult())).getValue());
                                if (value.equals("present")) {
                                    Toast.makeText(user_panel.this, "already marked for today\n" + currentDate, Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                } else {
                                    databaseReference.child("users").child(userid).child("Attendance").child(currentDate).setValue("present");
                                    Toast.makeText(this, "Attendance marked successfully", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(user_panel.this, "Cannot mark attendance try again", Toast.LENGTH_SHORT).show();

                            });

                });
                dialog.setNegativeButton("cancel", (dialogInterface, i) -> {

                });
                dialog.show();

            } catch (NullPointerException exception) {
                Toast.makeText(this, "eror found", Toast.LENGTH_SHORT).show();
            }

    }

    public void view_attendance() {
            progressDialog.show();
            DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("users").child(userid).child("Attendance");
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(user_panel.this);
                    builder.setTitle("Attendance history")
                            .setMessage("Total present Days:\t" + count + "\n" + value1);
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
        }

    private void leaverequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(user_panel.this);
        builder.setTitle("Leave Request").setMessage("Your leave request will be sent to your admin\nAre you sure to send leave request?")
                .setPositiveButton("confirm", (dialogInterface, i) -> {
                    databaseReference.child("admin").child("leaves").child(userid).setValue("pending")
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(user_panel.this, "your request has been sent successfully", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(runnable -> {
                        Toast.makeText(user_panel.this, "failed to send leave request try again", Toast.LENGTH_SHORT).show();
                    });

                }).setNegativeButton("cancel", (dialogInterface, i) -> {
        });
        builder.show();
    }

    public void load_userdata() {
        progressDialog.show();
        try {

            File file = File.createTempFile("user", "jpg");
            storageReference.child(userid + ".jpg").getFile(file).addOnSuccessListener(taskSnapshot -> {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                userimage.setImageBitmap(bitmap);
                databaseReference.child("users").child(userid).child("name").get()
                        .addOnCompleteListener(task1 -> {
                            String name = String.valueOf(task1.getResult().getValue());
                            tvname.setText(name);
                        });
                String id = databaseReference.child(userid).getKey();
                tvid.setText(id);
                progressDialog.dismiss();
            }).addOnFailureListener(e -> {
                Toast.makeText(user_panel.this, "failed to load user record", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
            network = true;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                userimage.setImageBitmap(bitmap);
                isNetworkAvailable(user_panel.this);
                if (filePath != null&& network) {
                    progressDialog.setMessage("changing profile picture");
                    progressDialog.show();
                    StorageReference childRef = storageReference.child(userid + ".jpg");
                    UploadTask uploadTask = childRef.putFile(filePath);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(user_panel.this, "Upload successful!", Toast.LENGTH_SHORT).show();

                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(user_panel.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                    });
                }
                else {
                    Toast.makeText(this, "network connection not available or invalid path", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return true;
    }


    public void user_logout(MenuItem item) {
        user_panel.this.finish();
        startActivity(new Intent(user_panel.this, MainActivity.class));
    }

    public void leaveaproverequest(MenuItem item) {
        databaseReference.child("admin").child("leaves").child(userid).get().addOnCompleteListener(task -> {
          try {
              String mm=task.getResult().getValue().toString();
              Toast.makeText(this, "your leave request is:\t"+mm, Toast.LENGTH_SHORT).show();
          }catch (Exception e){
              Toast.makeText(this, "no leave request found", Toast.LENGTH_SHORT).show();
          }

        });

    }
}