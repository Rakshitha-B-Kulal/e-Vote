package com.example.e_vote.activities;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_vote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends android.app.Activity {

    private EditText userEmail,userPassword;
    private Button loginBtn;
    private TextView forgetPassword;
    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;

    private static final String TAG = "LoginActivity";

    public static final String PREFERENCES="prefKey";
    public static final String Name="nameKey";
    public static final String Voter="voteKey";
    public static final String Nationality="nationKey";
    public static final String Dob="dobKey";
    public static final String Address="addKey";
    public static final String Email="emailKey";
    public static final String Password="passKey";
    public static final String UploadData="uploaddata";

    SharedPreferences sharedPreferences;

    StorageReference reference;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        reference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        findViewById(R.id.dont_hv_acc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });

        userEmail = findViewById(R.id.user_email);
        userPassword = findViewById(R.id.user_password);
        loginBtn = findViewById(R.id.login_btn);
        forgetPassword = findViewById(R.id.forgot_password);
        mAuth=FirebaseAuth.getInstance();

        userPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (userPassword.getRight() - userPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    // Disable focus temporarily to prevent InputConnection issues
                    userPassword.clearFocus();

                    // Toggle password visibility
                    if (isPasswordVisible) {
                        userPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        userPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                    } else {
                        userPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        userPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
                    }
                    isPasswordVisible = !isPasswordVisible;

                    // Move cursor to the end of the text and restore focus
                    userPassword.setSelection(userPassword.length());
                    userPassword.requestFocus();
                    return true;
                }
            }
            return false;
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            verifyEmail();
                        } else {
                            Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        forgetPassword.findViewById(R.id.forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });

    }
    private void verifyEmail() {
         FirebaseUser user=mAuth.getCurrentUser();

         assert user!=null;
         if(user.isEmailVerified()){

             boolean bol=sharedPreferences.getBoolean(UploadData,false);
             if(bol){
                 startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                 finish();
             }
             else{
                String name=sharedPreferences.getString(Name,null);
                String voter=sharedPreferences.getString(Voter,null);
                String nationality=sharedPreferences.getString(Nationality,null);
                String dob=sharedPreferences.getString(Dob,null);
                String address=sharedPreferences.getString(Address,null);
                String email=sharedPreferences.getString(Email,null);
                String password=sharedPreferences.getString(Password,null);

                if(name!=null && voter!=null && nationality!=null && dob!=null && address!=null
                         && email!=null && password!=null){
                    String uid = mAuth.getUid();

                    Map<String,String> map=new HashMap<>();
                    map.put("name",name);
                    map.put("voter",voter);
                    map.put("nationality",nationality);
                    map.put("dob",dob);
                    map.put("address",address);
                    map.put("email",email);
                    map.put("password",password);
                    map.put("uid",uid);

                    firebaseFirestore.collection("Users")
                         .document(uid)
                         .set(map)
                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if (task.isSuccessful()){

                                     sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                                     SharedPreferences.Editor pref = sharedPreferences.edit();
                                     pref.putBoolean(UploadData, true);
                                     pref.apply();
                                     startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                     finish();
                                 }
                                 else{
                                     Toast.makeText(LoginActivity.this,"Data not stored",Toast.LENGTH_SHORT).show();
                                 }
                             }
                         });
                }
                else{
                    Toast.makeText(LoginActivity.this,"User data not found",Toast.LENGTH_SHORT).show();
                }
             }
         }
         else{
             mAuth.signOut();
             Toast.makeText(LoginActivity.this,"Please verify your email",Toast.LENGTH_SHORT).show();
         }
    }
}


