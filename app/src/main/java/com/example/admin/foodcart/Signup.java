package com.example.admin.foodcart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.foodcart.Common.Common;
import com.example.admin.foodcart.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Signup extends AppCompatActivity {
    private EditText edtmail,edtphone,edtname,edtstatus;
    private Button btnsignup;
    private CircleImageView circleImageView;
    private DatabaseReference mRoot;
   // private String uploadid;
    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private static final int GalleryPick = 1;
    private StorageReference UserProfileImageRef;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        edtmail = (EditText)findViewById(R.id.editmail);
        edtphone = (EditText)findViewById(R.id.editphone);
        circleImageView = (CircleImageView)findViewById(R.id.profile_image);
        edtname = (EditText)findViewById(R.id.editname);
        edtstatus = (EditText)findViewById(R.id.editstatus);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        btnsignup = (Button)findViewById(R.id.btnsignup);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        mRoot = database.getReference().child("User");
        //uploadid = mRoot.push().getKey();

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {

                    final String email = edtmail.getText().toString();
                    final String status = edtstatus.getText().toString();
                    final String phone = edtphone.getText().toString();
                    final String name = edtname.getText().toString();
                    if (TextUtils.isEmpty(email)){
                        Toast.makeText(Signup.this,"Please provide email...",Toast.LENGTH_LONG).show();
                    }
                    if (TextUtils.isEmpty(status)){
                        Toast.makeText(Signup.this,"Please provide status...",Toast.LENGTH_LONG).show();
                    } if (TextUtils.isEmpty(phone)){
                        Toast.makeText(Signup.this,"Please provide phone...",Toast.LENGTH_LONG).show();
                    }
                    if (TextUtils.isEmpty(name)){
                        Toast.makeText(Signup.this,"Please provide name...",Toast.LENGTH_LONG).show();
                    } else {
                        final ProgressDialog progressDialog = new ProgressDialog(Signup.this);
                        progressDialog.setMessage("Please wait...");
                        progressDialog.show();
                        mRoot.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //check if already user phone is registered
                                if (dataSnapshot.child(edtphone.getText().toString()).exists()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Signup.this, "Phone number already registered", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressDialog.dismiss();
                                    Map<String,Object> profilemap =new HashMap<>();
                                    profilemap.put("name",name);
                                    profilemap.put("status",status);
                                    profilemap.put("email",email);
                                    profilemap.put("phone",phone);
                                   // User user = new User(edtname.getText().toString(), edtmail.getText().toString(), edtstatus.getText().toString());
                                   // mRoot.child(edtphone.getText().toString()).setValue(user);
                                    mRoot.child(phone).setValue(profilemap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                SendUserToMainActivity();
                                                progressDialog.dismiss();
                                                Toast.makeText(Signup.this, "Profile Updated Successfully..", Toast.LENGTH_SHORT).show();
                                            } else {
                                                String message = task.getException().toString();
                                                progressDialog.dismiss();
                                                Toast.makeText(Signup.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    Toast.makeText(Signup.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                    finish();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(Signup.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick);
                //uploadImage();
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent intent = new Intent(Signup.this,ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            Uri downloadurls = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog.setTitle("Set Profile Image");
                progressDialog.setMessage("Please wait your profile image is updating...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                final Uri resulturi = result.getUri();
                final StorageReference filepath = UserProfileImageRef.child(edtphone.getText().toString() + ".jpg");
                if (resulturi != null) {
                    filepath.putFile(resulturi)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            String profileImageUrl = task.getResult().toString();
                                            mRoot.child(edtphone.getText().toString()).child("image").setValue(profileImageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(Signup.this, "Successfully added in databse.", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(Signup.this, "Error in uploading image.", Toast.LENGTH_LONG).show();
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });
                                            Log.i("URL", profileImageUrl);
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Signup.this, "aaa " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }
    }
}
