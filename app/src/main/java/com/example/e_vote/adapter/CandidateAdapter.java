package com.example.e_vote.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.animation.content.Content;
import com.example.e_vote.R;
import com.example.e_vote.activities.VotingActivity;
import com.example.e_vote.model.Candidate;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.ViewHolder> {

    private Context context;
    private List<Candidate> list;

    public CandidateAdapter(Context context, List<Candidate> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CandidateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.candidate_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CandidateAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(list.get(position).getName());
        holder.party.setText(list.get(position).getParty());
        holder.position.setText(list.get(position).getPosition());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, VotingActivity.class);
                intent.putExtra("name",list.get(position).getName());
                intent.putExtra("party",list.get(position).getParty());
                intent.putExtra("post",list.get(position).getPosition());
                intent.putExtra("id",list.get(position).getId());
                context.startActivity(intent);
                Activity activity=(Activity) context;
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name,party,position;
//        private Button voteBtn;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.name);
            party=itemView.findViewById(R.id.party);
            position=itemView.findViewById(R.id.post);
//            voteBtn=itemView.findViewById(R.id.voteBtn);
            cardView=itemView.findViewById(R.id.card_view);

        }
    }
}
