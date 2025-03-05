package edu.cuhk.csci3310;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static HKMJ game = HKMJ.getInstance(); // 使用单例
    private LinearLayout popUpLayout1; // popup eat panel
    private LinearLayout popUpLayout2; // popup new game panel
    private LinearLayout popUpLayout3; // popup Master Setting panel
    private LinearLayout popUpLayout4; // popup Add New Player panel
    private LinearLayout popUpLayout5; // popup dice panel
    private LinearLayout popUpLayout6; // popup add player name
    private LinearLayout popUpLayout7; // popup add player name
    private LinearLayout dropDownLayout1; // popup drop down panel
    private List<String> playerNames = new ArrayList<>(Arrays.asList("self-draw", "wrong-draw")); // create name
    private final int maxNameLength = 10; // Set your maximum name length here
    private TextView yes; // yes button for eat
    private TextView firm; // firm/yes button for add playerName
    private TextView firmBackward; // for backward button
    private TextInputEditText addPlayerNameEditText;
    int numberOfPlayersAdded = 0; // for checking no. of players
    int currentPlayer = 0; // for identify current player(playerList)
    String dealInPlayer; // for identify deal In Player
    String faan;
    int baseFaan = 128;
    //HKMJ game = new HKMJ();
    int checkRecord = 0;


    // for player
    int[] playerList = {
            R.id.eastPlayer, //1
            R.id.southPlayer, //2
            R.id.westPlayer, //3
            R.id.northPlayer, //4
    };

    // for name



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("playerCount", numberOfPlayersAdded);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            numberOfPlayersAdded = savedInstanceState.getInt("playerCount", 0);
        }
        setContentView(R.layout.activity_main);
        //restoreUIFromGameData(); // 从单例恢复UI状态

        // Make sure the app's content is not drawn under the system bars
        // Disable default system padding handling
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Apply insets manually
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, windowInsets) -> {
            // Get system bar insets (status bar + navigation bar)
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply top inset for status bar and bottom inset for navigation bar
            view.setPadding(0, insets.top, 0, insets.bottom);
            // Return the insets so child views can use them if needed
            return windowInsets;
        });

        //testing();
        if (numberOfPlayersAdded < 3){
            setupPrePlayerAdditionUI();
        }else {

            setupPostPlayerAdditionUI();
        }
    }

    private void setupPrePlayerAdditionUI() {
        // add player name 1st
        popUpLayout6 = findViewById(R.id.popUpAddPlayerStart);
        for (int playerId : playerList) {
            ImageButton playerButton = findViewById(playerId);
            playerButton.setOnClickListener(v -> {
                togglePopUpVisibility(popUpLayout6);
                currentPlayer = playerId; // update current player who press the panel button
            });
        }

        firm = findViewById(R.id.firm);
        addPlayerNameEditText = findViewById(R.id.addPlayerName);
        firm.setOnClickListener(v -> {
            // Get the name from the TextInputEditText
            String playerName = addPlayerNameEditText.getText().toString();

            // Check if the name is not empty
            if (playerName.isEmpty()) {
                // Display a reminder for empty name
                Toast.makeText(this, "Please enter a player name", Toast.LENGTH_SHORT).show();
            } else if (playerName.length() > maxNameLength) {
                // Display a reminder for too long name
                Toast.makeText(this, "Player name is too long (max " + maxNameLength + " characters)", Toast.LENGTH_SHORT).show();
            } else {
                // Clear the TextInputEditText
                addPlayerNameEditText.getText().clear();

                // Add the player to the game
                HKMJ.Seat playerSeat = RIdToSeat(currentPlayer);
                // player name
                game.addPlayer(game.new Player(playerName), playerSeat);

                // add name to the playerNames list for the drop down bar
                playerNames.add(playerName);

                // Update the player name tag
                TextView playerNameTag = findViewById(SeatToNameTagRId(playerSeat));
                playerNameTag.setText(playerName);

                togglePopUpVisibility(popUpLayout6); // Close the popup

                // for check the no. player
                numberOfPlayersAdded++;

                // Disable the button after adding the player
                ImageButton myImageButton = findViewById(currentPlayer);
                myImageButton.setEnabled(false);

                if (numberOfPlayersAdded == 4) {
                    setupPostPlayerAdditionUI(); // Activate game UI
                }
            }
        });
    }
    private void setupPostPlayerAdditionUI() {
        // visible the score
        for(HKMJ.Seat seat : HKMJ.Seat.values()){
            TextView scoreView = findViewById(SeatToScoreRId(seat));
            scoreView.setVisibility(View.VISIBLE);
        }


        // enable the popup menu buttons
        for(int playerId : playerList){
            ImageButton playerButton = findViewById(playerId);
            playerButton.setEnabled(true);
        }
        // scroll faan, player
        LinearLayout numberListContainer = findViewById(R.id.numberListContainer);
        addNumbersToScrollView(numberListContainer, 3, 10);

        // for the playerNames drop down bar
        LinearLayout container = findViewById(R.id.numberListContainer2);
        addNameSpinnerToContainer(container, playerNames);


        // popup eat panel
        popUpLayout1 = findViewById(R.id.popUp);
        for (int playerId : playerList) {
            ImageButton playerButton = findViewById(playerId);
            playerButton.setOnClickListener(v -> {
                togglePopUpVisibility(popUpLayout1);
                currentPlayer = playerId; // update current player
            });
        }
        // popup new game panel
        popUpLayout2 = findViewById(R.id.popUpStartNewGame);
        setupButton(R.id.startNewGame,popUpLayout2);

        // popup Master Setting panel
        popUpLayout3 = findViewById(R.id.popUpMasterSetting);
        setupButton(R.id.masterSetting,popUpLayout3);

        // popup Add New Player panel
        popUpLayout4 = findViewById(R.id.popUpAddNewPlayer);
        setupButton(R.id.newPlayer,popUpLayout4);

        // popup dice panel
        popUpLayout5 = findViewById(R.id.popUpDice);
        setupButton(R.id.dice,popUpLayout5);

        // popup previous round
        popUpLayout7 = findViewById(R.id.popUpBackward);
        setupButton(R.id.backwardRound,popUpLayout7);

        // drop down menu
        dropDownLayout1 = findViewById(R.id.dropDownMenu);
        setupButton(R.id.more,dropDownLayout1);

        // record page
        ImageButton recordButton = findViewById(R.id.recordPage);
        recordButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
        });



        // confirm eat
        yes = findViewById(R.id.yes);
        yes.setOnClickListener(v -> {
            // calculate score
            calculateFaan();
            // 冧莊
            HKMJ.Seat playerSeat = RIdToSeat(currentPlayer);
            if (playerSeat == game.currentDealer) {
                game.handleConsecutiveWin(true); //????????
            }
            // no 冧莊
            else {
                game.rotateSeats();
                updateDealerIndicators(getDealerIndicatorId(game.currentDealer));
            }
            togglePopUpVisibility(popUpLayout1); // close the popup
            // testing
//            int selfDraws = game.getGameRecord().getEatPlayerCount(game.getPlayerNameBySeat(HKMJ.Seat.EAST));
//            Toast.makeText(getApplicationContext(), "Self Draws: " + selfDraws, Toast.LENGTH_SHORT).show();
        });

        // confirm back to previous round
        firmBackward = findViewById(R.id.firmBackward);
        firmBackward.setOnClickListener(v -> {
            game.popGameRecord();
            updateDealerIndicators(getDealerIndicatorId(game.currentDealer));
            for(HKMJ.Seat seat : HKMJ.Seat.values()){
                displayScore(seat, game.getScoreBySeat(seat));
            }
            togglePopUpVisibility(popUpLayout7);
        });
    }
    // need to debug


    private void testing(){
        game.addPlayer(game.new Player("Player1"), HKMJ.Seat.EAST);
        game.addPlayer(game.new Player("Player2"), HKMJ.Seat.SOUTH);
        game.addPlayer(game.new Player("Player3"), HKMJ.Seat.WEST);
        game.addPlayer(game.new Player("Player4"), HKMJ.Seat.NORTH);
    }

    private HKMJ.Seat RIdToSeat(int playerId) {
        if (playerId == R.id.eastPlayer) {
            return HKMJ.Seat.EAST;
        } else if (playerId == R.id.southPlayer) {
            return HKMJ.Seat.SOUTH;
        } else if (playerId == R.id.westPlayer) {
            return HKMJ.Seat.WEST;
        } else if (playerId == R.id.northPlayer) {
            return HKMJ.Seat.NORTH;
        } else {
            throw new IllegalArgumentException("Invalid player ID: " + playerId);
        }
    }
    private int SeatToNameTagRId(HKMJ.Seat seat) {
        switch (seat) {
            case EAST: return R.id.eastPlayerName;
            case SOUTH: return R.id.southPlayerName;
            case WEST: return R.id.westPlayerName;
            case NORTH: return R.id.northPlayerName;
            default: return -1;
        }
    }

    private int getDealerIndicatorId(HKMJ.Seat seat) {
        switch (seat) {
            case EAST: return R.id.eastDealer;
            case SOUTH: return R.id.southDealer;
            case WEST: return R.id.westDealer;
            case NORTH: return R.id.northDealer;
            default: return -1;
        }
    }
    //.................

    private int SeatToScoreRId(HKMJ.Seat seat) {
        switch (seat) {
            case EAST: return R.id.eastPlayerScore;
            case SOUTH: return R.id.southPlayerScore;
            case WEST: return R.id.westPlayerScore;
            case NORTH: return R.id.northPlayerScore;
            default: return -1;
        }
    }
    private void updateDealerIndicators(int currentPlayer){
        int[] dealerList = {
                R.id.eastDealer, //1
                R.id.southDealer, //2
                R.id.westDealer, //3
                R.id.northDealer, //4
        };
        for (int dealerId : dealerList){
            if (currentPlayer == dealerId){
                findViewById(dealerId).setVisibility(View.VISIBLE);
            }
            else {
                findViewById(dealerId).setVisibility(View.INVISIBLE);
            }
        }
    }



    // dropdown faan
    private void addNumbersToScrollView(LinearLayout container, int start, int end) {
        // Create list of numbers for the spinner
        List<String> numbers = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            numbers.add(String.valueOf(i));
        }

        // Create adapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                numbers
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Create single spinner
        Spinner numberSpinner = new Spinner(this);
        numberSpinner.setAdapter(adapter);

        // Add padding and layout parameters
        numberSpinner.setPadding(10, 10, 10, 10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        numberSpinner.setLayoutParams(params);
        // Add item selection listener (optional)
        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedNum = numbers.get(position);
                faan = selectedNum;
                // Handle selection here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });
        container.addView(numberSpinner);
    }

    // dropdown player
    private void addNameSpinnerToContainer(LinearLayout container, List<String> names) {
        // Create adapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                names
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Create single spinner
        Spinner nameSpinner = new Spinner(this);
        nameSpinner.setAdapter(adapter);

        // Add padding and layout parameters
        nameSpinner.setPadding(10, 10, 10, 10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameSpinner.setLayoutParams(params);

        // Add item selection listener (optional)
        nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = names.get(position);
                dealInPlayer = selectedName;
                // need to prevent the duplication crash

                // Handle selection here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });

        container.addView(nameSpinner);
    }

    //when press the button, popup window or discard popup window
    private static void togglePopUpVisibility(LinearLayout layout) {
        if (layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
        }
    }

    // press other area to discard the popup window
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Array of all closeable pop-up views
        View[] popUpViews = {popUpLayout1, popUpLayout2, popUpLayout3, popUpLayout4, popUpLayout5, popUpLayout6, popUpLayout7};

        for (View popUp : popUpViews) {
            if (popUp != null && popUp.getVisibility() == View.VISIBLE) {
                Rect viewRect = new Rect();
                popUp.getGlobalVisibleRect(viewRect);

                if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    popUp.setVisibility(View.GONE);
                    return true; // Event consumed after first valid close
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void calculateFaan() {
        Map<String, Integer> scoreChanges = new HashMap<>();
        HKMJ.Seat playerWin = RIdToSeat(currentPlayer);
        List<String> playerNames = new ArrayList<>(); // 统一初始化

        if (dealInPlayer.equals("self-draw")) {
            // 自摸逻辑
            double score = 1.5 * ScoreCalculator.calculateScore(Integer.parseInt(faan), baseFaan);
            updateScoreAndUI(playerWin, (int) score, scoreChanges);
            updateOtherPlayers(playerWin, -score / 3, scoreChanges, playerNames);
            game.recordRound(getPlayerName(playerWin), HKMJ.WinType.SELF_DRAW, playerNames, scoreChanges);

        } else if (dealInPlayer.equals("wrong-draw")) {
            // 诈胡逻辑
            double score = 3 * ScoreCalculator.calculateScore(10, baseFaan);
            updateScoreAndUI(playerWin, (int) -score, scoreChanges);
            updateOtherPlayers(playerWin, score / 3, scoreChanges, playerNames);
            game.recordRound(getPlayerName(playerWin), HKMJ.WinType.SELF_DRAW, playerNames, scoreChanges);

        } else {
            // 普通胡牌逻辑
            double score = ScoreCalculator.calculateScore(Integer.parseInt(faan), baseFaan);
            HKMJ.Seat dealIn = game.getSeatByPlayerName(dealInPlayer);
            if (dealIn == null) return; // 处理无效输入

            updateScoreAndUI(playerWin, (int) score, scoreChanges);
            updateScoreAndUI(dealIn, (int) -score, scoreChanges);
            playerNames.add(getPlayerName(dealIn));
            game.recordRound(getPlayerName(playerWin), HKMJ.WinType.EAT_PLAYER, playerNames, scoreChanges);
        }

        scoreChanges.clear();
    }

    // 辅助方法：更新单个玩家的分数和UI
    private void updateScoreAndUI(HKMJ.Seat seat, int scoreChange, Map<String, Integer> scoreChanges) {
        String name = game.getPlayerNameBySeat(seat);
        if (name == null) return;

        game.addScoreBySeat(seat, scoreChange);
        displayScore(seat, game.getScoreBySeat(seat));
        scoreChanges.put(name, scoreChange);
    }

    // 辅助方法：更新其他玩家的分数
    private void updateOtherPlayers(HKMJ.Seat winnerSeat, double scoreChangePerPlayer, Map<String, Integer> scoreChanges, List<String> playerNames) {
        for (HKMJ.Seat seat : HKMJ.Seat.values()) {
            if (seat != winnerSeat) {
                String name = game.getPlayerNameBySeat(seat);
                if (name != null) {
                    int roundedScore = (int) Math.round(scoreChangePerPlayer);
                    updateScoreAndUI(seat, roundedScore, scoreChanges);
                    playerNames.add(name);
                }
            }
        }
    }
    // 辅助方法：安全获取玩家名称
    private String getPlayerName(HKMJ.Seat seat) {
        String name = game.getPlayerNameBySeat(seat);
        return name != null ? name : "Unknown";
    }

    private void setupButton(int id, LinearLayout popupLayout) {
        ImageButton btn = findViewById(id);
        if (btn != null) {
            btn.setOnClickListener(v -> {
                togglePopUpVisibility(popupLayout);
                // currentPlayer = id;
            });
        }
    }
    // update score in UI
    private void displayScore(HKMJ.Seat seat, int score) {
        TextView scoreTextView = findViewById(SeatToScoreRId(seat));
        scoreTextView.setText(String.valueOf(score));

        if (score < 0) {
            scoreTextView.setTextColor(ContextCompat.getColor(this, R.color.red));
        } else if (score > 0) {
            scoreTextView.setTextColor(ContextCompat.getColor(this, R.color.green));
        } else {
            scoreTextView.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }

//    private void restoreUIFromGameData() {
//        for (HKMJ.Seat seat : HKMJ.Seat.values()) {
//            String player = game.getPlayerNameBySeat(seat);
//            if (player != null) {
//                TextView nameView = findViewById(SeatToNameTagRId(seat));
//                nameView.setText(player);
//                displayScore(seat, game.getScoreBySeat(seat));
//            }
//        }
//        updateDealerIndicators(getDealerIndicatorId(game.currentDealer));
//    }


}