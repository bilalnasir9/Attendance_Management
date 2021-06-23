package com.example.ams6860;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import static com.example.ams6860.R.*;

public class MainActivity extends AppCompatActivity {
    ConstraintLayout userlayout, adminlayout;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    String userid = "", userpassword = "", adminid = "", adminpassword = "";
    ProgressDialog progressDialog;
    String chekadminid = "", chekadminpaswrd = "";
    String chekuserid = "", chekuserpswrd = "";
    Boolean network = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        progressDialog = new ProgressDialog(this);
        EditText etuserid = findViewById(R.id.et_useremail);
        EditText etuserpswrd = findViewById(id.et_userpasswrd);
        EditText etadminid = findViewById(id.et_adminemail);
        EditText etadminpswrd = findViewById(id.et_adminpassword);
        TextView tvregister = findViewById(id.tv_registerclick);
        Button userlogin = findViewById(id.btn_userlogin);
        Button btnadmin = findViewById(id.btn_adminlogin);
        userlayout = findViewById(id.user_layout);
        adminlayout = findViewById(id.adminlayout);
        userstatus();

        userlogin.setOnClickListener(view -> {
            isNetworkAvailable(this);
            if (!network) {
                Toast.makeText(this, "internet connection not available", Toast.LENGTH_SHORT).show();
            } else {
                userid = etuserid.getText().toString();
                userpassword = etuserpswrd.getText().toString();
                userlogin();
            }
        });
        btnadmin.setOnClickListener(view -> {
            isNetworkAvailable(this);
            if (!network) {
                Toast.makeText(this, "internet connection not available", Toast.LENGTH_SHORT).show();
            } else {
                adminid = etadminid.getText().toString();
                adminpassword = etadminpswrd.getText().toString();
                adminlogin();
            }
        });

        tvregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, user_registration.class));
            }
        });

    }

    private void adminlogin() {

        if (adminid.equals("") || adminpassword.equals("")) {
            Toast.makeText(this, "invalid input", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("login working");
            progressDialog.show();
            String id = databaseReference.child(adminid).getKey();
            databaseReference.child(adminid).child("password").get().addOnCompleteListener(task2 -> {
                try {
                    String pswrd = Objects.requireNonNull(Objects.requireNonNull(task2.getResult()).getValue()).toString();
                    if (adminid.equals(id) && adminpassword.equals(pswrd)) {
                        Intent intent = new Intent(MainActivity.this, admin_panel.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(this, "invalid search", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    Toast.makeText(this, "invalid search\n" + e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            progressDialog.dismiss();

        }
    }

    public void userlogin() {
        if (userid.equals("") || userpassword.equals("")) {
            Toast.makeText(this, "invalid input", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("Login working");
            progressDialog.show();
            chekuserid = databaseReference.child(userid).getKey();
            databaseReference.child("users").child(userid).child("password").get().addOnCompleteListener(task -> {
                try {
                    chekuserpswrd = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getValue()).toString();
                    //Toast.makeText(this, chekuserid+chekuserpswrd, Toast.LENGTH_SHORT).show();
                    if (userid.equals(chekuserid) && userpassword.equals(chekuserpswrd)) {
                        Intent intent = new Intent(MainActivity.this, user_panel.class);
                        intent.putExtra("id", userid);
                        startActivity(intent);
                        progressDialog.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "your id or password is invalid!", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(this, "your id or password is invalid!\t\n" + e.toString(), Toast.LENGTH_SHORT).show();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "result failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navigation_drawer, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == id.user_panel) {
            userlayout.setVisibility(View.VISIBLE);
            adminlayout.setVisibility(View.INVISIBLE);

        }
        if (item.getItemId() == id.admin_panel) {
            userlayout.setVisibility(View.INVISIBLE);
            adminlayout.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    public void isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
            network = true;
        }
    }

    public void userstatus() {
        isNetworkAvailable(this);
        if (!network) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Network error!").setIcon(drawable.ic_baseline_networkcheck).setMessage("please check your internet connection");
            builder.setNegativeButton("OK", (dialogInterface, i) -> {
            });
            builder.show();
        }
    }
}