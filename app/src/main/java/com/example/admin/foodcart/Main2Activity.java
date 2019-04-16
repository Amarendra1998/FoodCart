package com.example.admin.foodcart;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.foodcart.Common.Common;
import com.example.admin.foodcart.Model.User;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Main2Activity extends AppCompatActivity {
    private static final int REQUEST_CODE = 7171;
    private Button btnContinue;
    private TextView textslogan;
    FirebaseDatabase database;
   DatabaseReference users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AccountKit.initialize(this);
       /* if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }*/

        setContentView(R.layout.activity_main2);
        database = FirebaseDatabase.getInstance();
        users = database.getReference("User");
        btnContinue = (Button)findViewById(R.id.btnContinue);
        textslogan = (TextView)findViewById(R.id.txtSlogan);
        //Paper.init(this);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoginSystem();
            }
        });

        if (AccountKit.getCurrentAccessToken()!=null){
            final AlertDialog waitingDialog = new SpotsDialog(this);
            waitingDialog.show();
            waitingDialog.setMessage("Please wait");
            waitingDialog.setCancelable(false);
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    users.child(account.getPhoneNumber().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User localUser = dataSnapshot.getValue(User.class);
                            Intent newintent = new Intent(Main2Activity.this,Home.class);
                            Common.currentUser = localUser;
                            startActivity(newintent);
                            waitingDialog.dismiss();
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });
        }
      /*  String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if (user!=null && pwd!=null)
        {
            if (!user.isEmpty()&& !pwd.isEmpty())
                login(user,pwd);
        }*/

        printKeyHash();
    }

    private void startLoginSystem() {
        Intent intent = new Intent(Main2Activity.this,AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder = new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult(intent,REQUEST_CODE);
    }

    private void printKeyHash() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.admin.foodcart",PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash",Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void login(final String phone, final String pwd) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference().child("User");
        if (Common.isConnectedToInternet(getBaseContext())) {

            final ProgressDialog progressDialog = new ProgressDialog(Main2Activity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //check the existence of user in database
                    if (dataSnapshot.child(phone).exists()) {
                        progressDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(phone);
                        if (user.getStatus().equals(pwd)) {
                            Intent homeintent = new Intent(Main2Activity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(homeintent);
                            finish();
                            Toast.makeText(Main2Activity.this, "Sign in successfully !", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Main2Activity.this, "Sign in failed!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Main2Activity.this, "User does not exist in database", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toast.makeText(Main2Activity.this,"Please check your internet connection",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE){
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (result.getError() !=null)
            {
                Toast.makeText(Main2Activity.this,""+result.getError().getErrorType().getMessage(),Toast.LENGTH_SHORT).show();
                return;
            }else if (result.wasCancelled()){
                Toast.makeText(Main2Activity.this,"Cancelled",Toast.LENGTH_SHORT).show();
                return;
            }else {
                if (result.getAccessToken()!=null){
                    final AlertDialog waitingDialog = new SpotsDialog(this);
                    waitingDialog.show();
                    waitingDialog.setMessage("Please wait");
                    waitingDialog.setCancelable(false);
                    AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                        @Override
                        public void onSuccess(Account account) {
                          final String userPhone = account.getPhoneNumber().toString();
                          users.orderByKey().equalTo(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                  if (!dataSnapshot.child(userPhone).exists())
                                  {
                                      User newUser = new User();
                                      newUser.setPhone(userPhone);
                                      newUser.setName("");
                                      newUser.setBalance(0.0);
                                      users.child(userPhone).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                              if (task.isSuccessful())
                                              {
                                                  Toast.makeText(Main2Activity.this,"User registered successfully",Toast.LENGTH_SHORT).show();
                                                  users.child(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                                                      @Override
                                                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                          User localUser = dataSnapshot.getValue(User.class);
                                                          Intent newintent = new Intent(Main2Activity.this,Home.class);
                                                          Common.currentUser = localUser;
                                                          startActivity(newintent);
                                                          waitingDialog.dismiss();
                                                          finish();
                                                      }

                                                      @Override
                                                      public void onCancelled(@NonNull DatabaseError databaseError) {

                                                      }
                                                  });
                                              }
                                          }
                                      });
                                  }else
                                  {
                                      users.child(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                              User localUser = dataSnapshot.getValue(User.class);
                                              Intent newintent = new Intent(Main2Activity.this,Home.class);
                                              Common.currentUser = localUser;
                                              startActivity(newintent);
                                              waitingDialog.dismiss();
                                              finish();
                                          }

                                          @Override
                                          public void onCancelled(@NonNull DatabaseError databaseError) {

                                          }
                                      });

                                  }
                              }

                              @Override
                              public void onCancelled(@NonNull DatabaseError databaseError) {

                              }
                          });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                          Toast.makeText(Main2Activity.this,""+accountKitError.getErrorType().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }
}
