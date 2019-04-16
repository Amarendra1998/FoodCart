package com.example.admin.foodcart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.rey.material.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.foodcart.Common.Common;
import com.example.admin.foodcart.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.paperdb.Paper;

public class Signin extends AppCompatActivity {
      private EditText edtpass,edtphone;
      private Button btnsign;
      private CheckBox checkBox;
      private TextView textView;
       FirebaseDatabase database;
      DatabaseReference table_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        edtpass = (EditText)findViewById(R.id.editpass);
        edtphone = (EditText)findViewById(R.id.editphone);
        btnsign = (Button)findViewById(R.id.btnsignin);
        textView = (TextView)findViewById(R.id.fgtPassword);

        checkBox = (CheckBox)findViewById(R.id.ckbRemember);

        Paper.init(this);
         database = FirebaseDatabase.getInstance();
         table_user = database.getReference().child("User");

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPwdDialog();
            }
        });
        btnsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                    if (checkBox.isChecked())
                    {
                        Paper.book().write(Common.USER_KEY,edtphone.getText().toString());
                        Paper.book().write(Common.PWD_KEY,edtpass.getText().toString());
                    }
                    final ProgressDialog progressDialog = new ProgressDialog(Signin.this);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //check the existence of user in database
                            if (dataSnapshot.child(edtphone.getText().toString()).exists()) {
                                progressDialog.dismiss();
                                User user = dataSnapshot.child(edtphone.getText().toString()).getValue(User.class);
                                user.setPhone(edtphone.getText().toString());
                                if (user.getStatus().equals(edtpass.getText().toString())) {
                                    Intent homeintent = new Intent(Signin.this, Home.class);
                                    Common.currentUser = user;
                                    startActivity(homeintent);
                                    finish();

                                    table_user.removeEventListener(this);
                                    Toast.makeText(Signin.this, "Sign in successfully !", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Signin.this, "Sign in failed!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(Signin.this, "User does not exist in database", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(Signin.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void showForgotPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forget Password");
        builder.setMessage("Enter your secure code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forget_view = inflater.inflate(R.layout.forget_password_layout,null);
        builder.setView(forget_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);
        final MaterialEditText edtphone = (MaterialEditText)forget_view.findViewById(R.id.editphone);
        final MaterialEditText edtsecureCode= (MaterialEditText)forget_view.findViewById(R.id.editsecureCode);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               table_user.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       User user = dataSnapshot.child(edtphone.getText().toString())
                               .getValue(User.class);
                       if (user.getSecureCode().equals(edtsecureCode.getText().toString()))
                           Toast.makeText(Signin.this,"Your Password:"+user.getStatus(),Toast.LENGTH_LONG).show();
                       else
                           Toast.makeText(Signin.this,"Wrong Secure Code!",Toast.LENGTH_LONG).show();

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
