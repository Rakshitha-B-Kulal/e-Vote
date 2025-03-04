package com.example.e_vote.activities;

import androidx.appcompat.app.AppCompatActivity;
import com.example.e_vote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends android.app.Activity {
    private EditText emailEdit;
    private Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEdit=findViewById(R.id.email_edit);
        reset=findViewById(R.id.button);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=emailEdit.getText().toString().trim();

                if(!TextUtils.isEmpty(email)){

                    FirebaseAuth auth=FirebaseAuth.getInstance();

                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {

                            if(task.isSuccessful()){

                                Toast.makeText(ForgotPasswordActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else{
                                Toast.makeText(ForgotPasswordActivity.this, "Email not sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}