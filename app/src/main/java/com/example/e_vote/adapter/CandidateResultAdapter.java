package com.example.e_vote.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_vote.R;
import com.example.e_vote.activities.VotingActivity;
import com.example.e_vote.model.Candidate;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class CandidateResultAdapter extends RecyclerView.Adapter<CandidateResultAdapter.ViewHolder> {

    private Context context;
    private List<Candidate> list;
    private FirebaseFirestore firebaseFirestore;

    public CandidateResultAdapter(Context context, List<Candidate> list) {
        this.context = context;
        this.list = list;
        firebaseFirestore=FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CandidateResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.candidate_result_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CandidateResultAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position).getName());
        holder.party.setText(list.get(position).getParty());
        holder.position.setText(list.get(position).getPosition());

        firebaseFirestore.collection("Candidate/"+list.get(position).getId()+"/Vote")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots,@Nullable FirebaseFirestoreException error) {
                        if(!documentSnapshots.isEmpty()){

                            int count=documentSnapshots.size();
                            Candidate candidate=list.get(position);
                            candidate.setCount(count);
                            list.set(position,candidate);

                            notifyDataSetChanged();
                        }
                        else{

                        }
                    }
                });

        holder.result.setText("Result:"+list.get(position).getCount());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name,party,position,result;


        public ViewHolder(View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.name);
            party=itemView.findViewById(R.id.party);
            position=itemView.findViewById(R.id.post);
            result=itemView.findViewById(R.id.candidate_result);


        }
    }
}
