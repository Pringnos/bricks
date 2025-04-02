package com.example.firebase.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebase.R;
import com.example.firebase.models.UserScore;

import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private final List<UserScore> scores;
    private final Context context;

    public LeaderboardAdapter(Context context, List<UserScore> scores) {
        this.context = context;
        this.scores = scores;
    }

    @NonNull
    @Override
    public LeaderboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_leaderboard_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.ViewHolder holder, int position) {
        UserScore score = scores.get(position);

        // Emoji or number rank
        String rankText;
        switch (position) {
            case 0:
                rankText = "🥇";
                holder.itemView.setBackgroundColor(Color.parseColor("#FFD700")); // Gold
                holder.nameText.setTextColor(Color.BLACK);
                holder.scoreText.setTextColor(Color.BLACK);
                break;
            case 1:
                rankText = "🥈";
                holder.itemView.setBackgroundColor(Color.parseColor("#C0C0C0")); // Silver
                holder.nameText.setTextColor(Color.BLACK);
                holder.scoreText.setTextColor(Color.BLACK);
                break;
            case 2:
                rankText = "🥉";
                holder.itemView.setBackgroundColor(Color.parseColor("#CD7F32")); // Bronze
                holder.nameText.setTextColor(Color.WHITE);
                holder.scoreText.setTextColor(Color.WHITE);
                break;
            default:
                rankText = String.valueOf(position + 1) + ".";
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                holder.nameText.setTextColor(Color.WHITE);
                holder.scoreText.setTextColor(Color.parseColor("#FFD700"));
                break;
        }

        holder.rankText.setText(rankText);
        holder.nameText.setText(score.name);
        holder.scoreText.setText(String.valueOf(score.score));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView rankText, nameText, scoreText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.text_rank);
            nameText = itemView.findViewById(R.id.text_name);
            scoreText = itemView.findViewById(R.id.text_score);
        }
    }
}