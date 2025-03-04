package com.example.e_vote.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e_vote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

public class SignUpActivity extends android.app.Activity {
    private EditText userName,userVoterId,userNationality,userDob,userAddress,userEmail,userPassword;
    private Button signUpBtn;
    private Uri mainUri=null;
    private FirebaseAuth mAuth;
    private boolean isPasswordVisible = false;

    public static final String PREFERENCES="prefKey";
    public static final String Name="nameKey";
    public static final String Voter="voteKey";
    public static final String Nationality="nationKey";
    public static final String Dob="dobKey";
    public static final String Address="addKey";
    public static final String Email="emailKey";
    public static final String Password="passKey";

    SharedPreferences sharedPreferences;

    String name, voterId, nationality, dob, address, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sharedPreferences= getApplicationContext().getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE);

        findViewById(R.id.have_acc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        userName=findViewById(R.id.user_name);
        userVoterId=findViewById(R.id.voter_id);
        userNationality=findViewById(R.id.nationality);
        userDob=findViewById(R.id.dob);
        userAddress=findViewById(R.id.address);
        userEmail=findViewById(R.id.user_email);
        userPassword=findViewById(R.id.user_password);
        signUpBtn=findViewById(R.id.signup_btn);
        mAuth = FirebaseAuth.getInstance();

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

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name=userName.getText().toString().trim();
                voterId=userVoterId.getText().toString().trim();
                nationality=userNationality.getText().toString().trim();
                dob=userDob.getText().toString().trim();
                address=userAddress.getText().toString().trim();
                email=userEmail.getText().toString().trim();
                password=userPassword.getText().toString().trim();

                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(voterId) && !TextUtils.isEmpty(nationality) && !TextUtils.isEmpty(dob) &&
                        !TextUtils.isEmpty(address) && !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
                        && !TextUtils.isEmpty(password) ){
                    createUser(email,password);
                }
                else {
                    Toast.makeText(SignUpActivity.this,"Please fill all details",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void createUser(String email,String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, "User created", Toast.LENGTH_SHORT).show();
                    verifyEmail();
                }else{
                    Toast.makeText(SignUpActivity.this,"Failed Try again",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyEmail() {
        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){

                        SharedPreferences.Editor pref= sharedPreferences.edit();
                        pref.putString(Name,name);
                        pref.putString(Voter,voterId);
                        pref.putString(Nationality,nationality);
                        pref.putString(Dob,dob);
                        pref.putString(Address,address);
                        pref.putString(Email,email);
                        pref.putString(Password,password);
                        pref.apply();

                        Toast.makeText(SignUpActivity.this,"Email sent",Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                        finish();
                    }
                    else {
                        mAuth.signOut();
                        finish();
                    }
                }
            });
        }
    }
}