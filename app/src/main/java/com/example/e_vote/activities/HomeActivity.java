package com.example.e_vote.activities;
import com.example.e_vote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class HomeActivity extends android.app.Activity {

    public static final String PREFERENCES = "prefKey";
    SharedPreferences sharedPreferences;
    public static final String IsLogIn = "islogin";
    private TextView nametxt, national_id;
    private String uid;
    private FirebaseFirestore firebaseFirestore;
    private Button createBtn, post, startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseFirestore = FirebaseFirestore.getInstance();
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        nametxt = findViewById(R.id.name);
        national_id = findViewById(R.id.national_id);
        createBtn = findViewById(R.id.admin_btn);
//        post = findViewById(R.id.give_vote);
        startBtn = findViewById(R.id.candidate_create_voting);

        sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor pref = sharedPreferences.edit();
        pref.putBoolean(IsLogIn, true);
        pref.apply();

//        findViewById(R.id.log_out).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                pref.putBoolean(IsLogIn, false);
//                pref.apply();
//                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
//                finish();
//            }
//        });

        firebaseFirestore.collection("Users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String name = task.getResult().getString("name");
                            String nationId = task.getResult().getString("voter");
                            String pass= task.getResult().getString("password");

                            assert name != null;
                            if (name.equals("Admin")&& pass.equals("admin@123")) {
//                                createBtn.setVisibility(View.VISIBLE);
                                createBtn.setText("Create candidate");
//                                startBtn.setVisibility(View.GONE);
//                                post.setVisibility(View.VISIBLE);
                                createBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(HomeActivity.this, Create_Candidate_Activity.class));

                                    }
                                });
                            } else {
//                                createBtn.setVisibility(View.VISIBLE);
                                createBtn.setText("Start voting");
//                                startBtn.setVisibility(View.VISIBLE);
//                                post.setVisibility(View.GONE);
                                createBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(HomeActivity.this, AllCandidateActivity.class));
                                    }
                                });
                            }
                            nametxt.setText(name);
                            national_id.setText(nationId);
                        } else {
                            Toast.makeText(HomeActivity.this, "User not Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

//        createBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, Create_Candidate_Activity.class));
//
//            }
//        });
//        startBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, AllCandidateActivity.class));
//            }
//        });

//        post.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, ResultActivity.class));
//            }
//        });
    }
        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.main_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            int id = item.getItemId();
            SharedPreferences.Editor pref = sharedPreferences.edit();
            switch (id) {
                case R.id.show_result:
                    startActivity(new Intent(HomeActivity.this, ResultActivity.class));

                    return true;

                case R.id.log_out:
                    FirebaseAuth.getInstance().signOut();
                    pref.putBoolean(IsLogIn, false);
                    pref.apply();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();

                    return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }
}