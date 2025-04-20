package edu.cuhk.csci3310;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoundAdapter extends RecyclerView.Adapter<RoundAdapter.RoundViewHolder> {

    private List<HKMJ.Round> roundList;

    public RoundAdapter(List<HKMJ.Round> roundList) {
        this.roundList = roundList;
    }

    @NonNull
    @Override
    public RoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_round, parent, false);
        return new RoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoundViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == 1) { // Empty state
            holder.winnerTextView.setText("No data");
            holder.winTypeTextView.setVisibility(View.GONE);
            holder.scoreChangesTextView.setVisibility(View.GONE);
        } else { // Normal state
            HKMJ.Round round = roundList.get(position);

            // Set winner
            holder.winnerTextView.setText("Winner: " + round.getWinner());

            // Set win type
            holder.winTypeTextView.setText("Win Type: " + round.getWinType().toString());

            // Set round
            holder.roundSit.setText("Round: " + round.getRoundSit().toString());

            // Set dir image
            if(Objects.equals(round.getWinner(), "Draw")){
                holder.sitImage.setImageResource(R.drawable.special2);
            }else {
                holder.sitImage.setImageResource(SeatToPlayerDirRId(round.getWinSit()));
            }


            // Set score changes
            Map<String, Integer> scoreChanges = round.getScoreChanges();
            StringBuilder scoreChangesText = new StringBuilder("Score Changes: ");
            for (Map.Entry<String, Integer> entry : scoreChanges.entrySet()) {
                scoreChangesText.append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
            }
            // Remove trailing comma and space
            if (scoreChangesText.length() > "Score Changes: ".length()) {
                scoreChangesText.setLength(scoreChangesText.length() - 2);
            }
            holder.scoreChangesTextView.setText(scoreChangesText.toString());

            // Ensure visibility for normal state
            holder.winTypeTextView.setVisibility(View.VISIBLE);
            holder.scoreChangesTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return roundList == null || roundList.isEmpty() ? 1 : roundList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (roundList == null || roundList.isEmpty()) ? 1 : 0;
    }

    public static class RoundViewHolder extends RecyclerView.ViewHolder {
        TextView winnerTextView, winTypeTextView, scoreChangesTextView, roundSit;
        ImageView sitImage;

        public RoundViewHolder(@NonNull View itemView) {
            super(itemView);
            winnerTextView = itemView.findViewById(R.id.round_winner);
            winTypeTextView = itemView.findViewById(R.id.round_win_type);
            scoreChangesTextView = itemView.findViewById(R.id.round_score_changes);
            sitImage = itemView.findViewById(R.id.sitImage);
            roundSit = itemView.findViewById(R.id.roundSit);
        }
    }

    private int SeatToPlayerDirRId(HKMJ.Seat seat) {
        switch (seat) {
            case EAST: return R.drawable.dir1;
            case SOUTH: return R.drawable.dir2;
            case WEST: return R.drawable.dir3;
            case NORTH: return R.drawable.dir4;
            default: return -1;
        }
    }
}