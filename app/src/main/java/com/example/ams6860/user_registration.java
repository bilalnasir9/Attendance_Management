package com.example.ams6860;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class user_registration extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference();
    ImageView profileimage;
    String name, email, password;
    int PICK_IMAGE_REQUEST = 111;
    ProgressDialog pd;
    Uri filePath;
    Boolean network = false;
    user objuser;
    user_panel obj_userpanel;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        EditText etname = findViewById(R.id.etname);
        EditText etemail = findViewById(R.id.etemail);
        EditText etpasword = findViewById(R.id.etpassword);
        Button btn_register = findViewById(R.id.btn_userregister);
        Button btnchooseimge = findViewById(R.id.btnchooseimge);
        profileimage = findViewById(R.id.profileimage);

        pd = new ProgressDialog(this);
        pd.setMessage("user Registration ");
        btn_register.setOnClickListener(view -> {
            name = etname.getText().toString();
            password = etpasword.getText().toString();
            email = etemail.getText().toString();
            objuser = new user(name, password);
                user_register();


        });

        btnchooseimge.setOnClickListener(view -> {
            choose_image();
        });

    }


    public void choose_image() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    public void user_register() {
        isNetworkAvailable(user_registration.this);
        if (!network) {
            Toast.makeText(user_registration.this, "internet connection not available!", Toast.LENGTH_SHORT).show();
        } else {

            if (filePath != null&& !email.equals("") && !password.equals("") && !name.equals("")) {
                pd.show();
                databaseReference.child("users").child(email).setValue(objuser);
                StorageReference childRef = storageReference.child(email + ".jpg");
                //uploading the image
                UploadTask uploadTask = childRef.putFile(filePath);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    pd.dismiss();
                    Toast.makeText(user_registration.this, "Upload successful!", Toast.LENGTH_SHORT).show();
                   Intent intent=new Intent(user_registration.this,user_panel.class);
                   intent.putExtra("id",email);
                   startActivity(intent);
                })
                        .addOnFailureListener(e -> {
                            pd.dismiss();
                            Toast.makeText(user_registration.this, "Upload failed!", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(user_registration.this, "all inputs are required", Toast.LENGTH_SHORT).show();
            }

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
                //  Toast.makeText(this, "select image", Toast.LENGTH_LONG).show();
                //Setting image to ImageView
                profileimage.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}