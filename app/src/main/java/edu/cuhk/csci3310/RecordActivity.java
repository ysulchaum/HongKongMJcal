package edu.cuhk.csci3310;
// Yu Sui Chung 1155177344
// Wong Tin Po 1155177337

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecordActivity extends AppCompatActivity {
    private RecyclerView recordPopUpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_record);

        // Make sure the app's content is not drawn under the system bars
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(0, insets.top, 0, insets.bottom);
            return windowInsets;
        });

        // Main page button
        ImageButton mainPageButton = findViewById(R.id.mainPage);
        if (mainPageButton != null) {
            mainPageButton.setOnClickListener(v -> finish());
        } else {
            Log.w("RecordActivity", "mainPage button not found in layout");
        }

        // Set up RecyclerView
        recordPopUpView = findViewById(R.id.recordPopUp);
        recordPopUpView.setLayoutManager(new LinearLayoutManager(this));
        updateRecordUI();

        // Set up stat and record toggle
        statPopUp();
    }

    private void updateRecordUI() {
        HKMJ game = HKMJ.getInstance();
        for (HKMJ.Seat seat : HKMJ.Seat.values()) {
            try {
                String playerName = game.getPlayerNameBySeat(seat);

                TextView eatNumView = findViewById(SeatToPlayerEatNumRId(seat));
                if (eatNumView != null) {
                    eatNumView.setText(String.valueOf(game.getGameRecord().getEatPlayerCount(playerName)));
                } else {
                    Log.e("RecordActivity", "eatNumView is null for seat: " + seat);
                }

                TextView selfNumView = findViewById(SeatToPlayerSelfNumRId(seat));
                if (selfNumView != null) {
                    selfNumView.setText(String.valueOf(game.getGameRecord().getSelfDrawCount(playerName)));
                } else {
                    Log.e("RecordActivity", "selfNumView is null for seat: " + seat);
                }

                TextView dealInNumView = findViewById(SeatToPlayerDealInNumRId(seat));
                if (dealInNumView != null) {
                    dealInNumView.setText(String.valueOf(game.getGameRecord().getLoseCount(playerName)));
                } else {
                    Log.e("RecordActivity", "dealInNumView is null for seat: " + seat);
                }
            } catch (IllegalArgumentException e) {
                Log.e("RecordActivity", "No player assigned to seat " + seat + ": " + e.getMessage());
                // Optionally set default text for unassigned seats
                TextView eatNumView = findViewById(SeatToPlayerEatNumRId(seat));
                if (eatNumView != null) eatNumView.setText("0");
                TextView selfNumView = findViewById(SeatToPlayerSelfNumRId(seat));
                if (selfNumView != null) selfNumView.setText("0");
                TextView dealInNumView = findViewById(SeatToPlayerDealInNumRId(seat));
                if (dealInNumView != null) dealInNumView.setText("0");
            }
        }
        // Update RecyclerView with round data
        RoundAdapter adapter = new RoundAdapter(game.gameList());
        recordPopUpView.setAdapter(adapter);
    }

    private void statPopUp(){
        recordPopUpView = findViewById(R.id.recordPopUp);
        LinearLayout recordButton = findViewById(R.id.recordButton);
        LinearLayout statButton = findViewById(R.id.statButton);
        LinearLayout recordLine = findViewById(R.id.recordLine);
        LinearLayout statLine = findViewById(R.id.statLine);

        recordButton.setOnClickListener(v -> {
            recordPopUpView.setVisibility(View.VISIBLE);
            recordLine.setVisibility(View.VISIBLE);
            statLine.setVisibility(View.GONE);
        });

        statButton.setOnClickListener(v -> {
            recordPopUpView.setVisibility(View.GONE);
            recordLine.setVisibility(View.GONE);
            statLine.setVisibility(View.VISIBLE);
        });


    }

    private static void togglePopUpVisibility(HorizontalScrollView layout) {
        if (layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
        }
    }

    private int SeatToPlayerEatNumRId(HKMJ.Seat seat) {
        switch (seat) {
            case EAST: return R.id.eastPlayerEatNum;
            case SOUTH: return R.id.southPlayerEatNum;
            case WEST: return R.id.westPlayerEatNum;
            case NORTH: return R.id.northPlayerEatNum;
            default: return -1;
        }
    }
    private int SeatToPlayerSelfNumRId(HKMJ.Seat seat) {
        switch (seat) {
            case EAST: return R.id.eastPlayerSelfNum;
            case SOUTH: return R.id.southPlayerSelfNum;
            case WEST: return R.id.westPlayerSelfNum;
            case NORTH: return R.id.northPlayerSelfNum;
            default: return -1;
        }
    }
    private int SeatToPlayerDealInNumRId(HKMJ.Seat seat) {
        switch (seat) {
            case EAST: return R.id.eastPlayerDealInNum;
            case SOUTH: return R.id.southPlayerDealInNum;
            case WEST: return R.id.westPlayerDealInNum;
            case NORTH: return R.id.northPlayerDealInNum;
            default: return -1;
        }
    }
}