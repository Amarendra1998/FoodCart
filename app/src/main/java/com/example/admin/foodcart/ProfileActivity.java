package com.example.admin.foodcart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.foodcart.Common.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView circleImageView;
    private DatabaseReference mRoot;
    private FirebaseAuth mAuth;
    private Button button;
    FirebaseDatabase database;
    private ProgressDialog progressDialog;
    private TextView mname,mstatus,memail,mphone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        circleImageView = (CircleImageView)findViewById(R.id.circle);
        mname = (TextView) findViewById(R.id.textview);
        memail = (TextView) findViewById(R.id.textView2);
        mstatus = (TextView) findViewById(R.id.textView4);
        mphone = (TextView) findViewById(R.id.textView3);
        button = (Button)findViewById(R.id.button2);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mRoot = database.getReference().child("User");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,Signup.class);
                startActivity(intent);
            }
        });
        retreiveinfo();
    }

    private void retreiveinfo() {
            mRoot.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if ((dataSnapshot.exists())&&(dataSnapshot.hasChild("name")&&(dataSnapshot.hasChild("image")))){
                        String retrieveusername = dataSnapshot.child("name").getValue().toString();
                        String retrievestatus = dataSnapshot.child("status").getValue().toString();
                        String retrieveprofile = dataSnapshot.child("image").getValue().toString();
                        String retrievephone = dataSnapshot.child("phone").getValue().toString();
                        String retrievemail = dataSnapshot.child("email").getValue().toString();
                        mname.setText(retrieveusername);
                        mstatus.setText(retrievestatus);
                        mphone.setText(retrievephone);
                        memail.setText(retrievemail);
                        Picasso.with(ProfileActivity.this).load(retrieveprofile).into(circleImageView);
                    }else if ((dataSnapshot.exists())&&(dataSnapshot.hasChild("name"))){
                        String retrieveusername = dataSnapshot.child("name").getValue().toString();
                        String retrievestatus = dataSnapshot.child("status").getValue().toString();
                        String retrievephone = dataSnapshot.child("phone").getValue().toString();
                        String retrievemail = dataSnapshot.child("email").getValue().toString();
                        mname.setText(retrieveusername);
                        mstatus.setText(retrievestatus);
                        mphone.setText(retrievephone);
                        memail.setText(retrievemail);
                    }
                    else{
                       // Toast.makeText(ProfileActivity.this,"Please enter your status...",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this,Signup.class);
                        startActivity(intent);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }
}
