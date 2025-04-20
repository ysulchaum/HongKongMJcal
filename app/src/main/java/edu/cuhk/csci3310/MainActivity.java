package edu.cuhk.csci3310;
// Yu Sui Chung 1155177344
// Wong Tin Po 1155177337


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import androidx.core.content.FileProvider;


public class MainActivity extends AppCompatActivity {

    private static HKMJ game = HKMJ.getInstance(); // 使用单例
    private LinearLayout popUpLayout1; // popup eat panel
    private LinearLayout popUpLayout2; // popup new game panel
    private LinearLayout popUpLayout3; // popup Master Setting panel
    private LinearLayout popUpLayout4; // popup Add New Player panel
    private LinearLayout popUpLayout5; // popup dice panel
    private LinearLayout popUpLayout6; // popup add player name
    private LinearLayout popUpLayout7; // popup add player name
    private LinearLayout popUpLayout8; // popup add cash out
    private LinearLayout popUpLayout9; // popup share
    private LinearLayout dropDownLayout1; // popup drop down panel
    private List<String> playerNames = new ArrayList<>(Arrays.asList("self-draw", "wrong-draw")); // create name
    private final int maxNameLength = 10; // Set your maximum name length here
    private TextView yes; // yes button for eat
    private TextView firm; // firm/yes button for add playerName
    private TextView firmBackward; // for backward button
    private TextInputEditText addPlayerNameEditText;
    private Button drawButton;
    int numberOfPlayersAdded = 0; // for checking no. of players
    int currentPlayer = 0; // for identify current player(playerList)
    String dealInPlayer; // for identify deal In Player
    String faan;
    int baseFaan = 128;
    int tempFaan = 0;
    boolean isGameHistoryExists;
    private static ValueAnimator colorAnimator;

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

        // for test


        // Load game state from file
        game.loadFromFile(getFilesDir());
        // show json on the LOG
        readJsonFile();
        // Check if game history exists by game state
        if (game.gameList() != null && !game.gameList().isEmpty()) {
            isGameHistoryExists = true; // History exists if there are recorded rounds
        } else if (game.getPlayerNameBySeat(HKMJ.Seat.EAST) != null ||
                game.getPlayerNameBySeat(HKMJ.Seat.SOUTH) != null ||
                game.getPlayerNameBySeat(HKMJ.Seat.WEST) != null ||
                game.getPlayerNameBySeat(HKMJ.Seat.NORTH) != null) {
            isGameHistoryExists = true; // History exists if any player is loaded
        } else {
            isGameHistoryExists = false;
        }


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

        // Sync playerNames with loaded game state
        if (isGameHistoryExists) {
            syncGameStateWithUI();
        }

        if(isGameHistoryExists){
            setupPostPlayerAdditionUI();
        }else{
            setupPrePlayerAdditionUI();
        }


//        if (numberOfPlayersAdded < 3){
//            setupPrePlayerAdditionUI();
//        }else {
//
//            setupPostPlayerAdditionUI();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save game state when the app is paused (e.g., minimized or closed)
        game.saveToFile(getFilesDir());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Save game state when the activity is destroyed
        game.saveToFile(getFilesDir());
    }

    private void syncGameStateWithUI() {
        // Sync playerNames with loaded players
        playerNames.clear();
        playerNames.add("self-draw");
        playerNames.add("wrong-draw");
        for (HKMJ.Seat seat : HKMJ.Seat.values()) {
            String playerName = game.getPlayerNameBySeat(seat);
            if (playerName != null && !playerNames.contains(playerName)) {
                playerNames.add(playerName);
            }
        }

        // Update numberOfPlayersAdded based on loaded players
        numberOfPlayersAdded = 0;
        for (HKMJ.Seat seat : HKMJ.Seat.values()) {
            if (game.getPlayerNameBySeat(seat) != null) {
                numberOfPlayersAdded++;
                // Update player name tags in UI
                TextView playerNameTag = findViewById(SeatToNameTagRId(seat));
                playerNameTag.setText(game.getPlayerNameBySeat(seat));
                // Update score display
                displayScore(seat, game.getScoreBySeat(seat));
            }
        }

        // Update dealer indicators
        updateDealerIndicators(getDealerIndicatorId(game.currentDealer));
        // Update info text
        updateInfoText();
    }


    private void setupPrePlayerAdditionUI() {
        setBaseFaan();
        startAddPlayerName();
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


        // popup Add New Player panel
        popUpLayout4 = findViewById(R.id.popUpAddNewPlayer);
        setupButton(R.id.newPlayer,popUpLayout4);


        // drop down menu
        dropDownLayout1 = findViewById(R.id.dropDownMenu);
        setupButton(R.id.more,dropDownLayout1);

        // roll dice
        rollDiceButton();

        // new game button
        newGameButton();

        // change dealer
        changeDealerButton();

        // draw button
        drawButton();

        // cash out button
        cashOutButton();

        // eat panel button
        eatPanelButton();

        // back ward button
        backwardButton();

        // info button (testing)
        infoButton();

        // share result
        shareButton();

        // record page
        ImageButton recordButton = findViewById(R.id.recordPage);
        recordButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
        });

        // enter Faan page
        ImageButton enterFaan = findViewById(R.id.enterFaan);
        enterFaan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EnterFaanActivity.class);
            startActivity(intent);
        });

    }

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


        // enter the hand and calculate the faan, then update the num of the spinner

        if (game.getHandFaan()<=10 && game.getHandFaan()>=3){
            numberSpinner.setSelection(faanToNumSpinnerPosition(game.getHandFaan()));
        }

        // Add item selection listener (optional)
        numberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedNum = numbers.get(position);
                faan = selectedNum;
                // Handle selection here
                // Ensure popUpLayout1 is initialized
                if (popUpLayout1 == null) {
                    popUpLayout1 = findViewById(R.id.popUp);
                }

                if (Objects.equals(selectedNum, "10")) {
                    // Start color animation if not already running
                    if (colorAnimator == null || !colorAnimator.isRunning()) {
                        setupColorAnimation(popUpLayout1);
                    }
                } else {
                    // Stop animation and set to white
                    if (colorAnimator != null && colorAnimator.isRunning()) {
                        colorAnimator.cancel(); // Stop the animation
                    }
                    popUpLayout1.setBackgroundColor(Color.WHITE); // Set to white
                }
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
        // Filter the list to exclude himself which is the winning player
        // Get the winning player
        String winningPlayer = game.getPlayerNameBySeat(RIdToSeat(currentPlayer));

        // Create adapter for the spinner
        // Create custom adapter with disabled winning player
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                names) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the winning player item
                return !getItem(position).equals(winningPlayer);
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                // Optional: Grey out the disabled item
                if (getItem(position).equals(winningPlayer)) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
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
                // Only process if the selected item isn't the winning player
                if (!selectedName.equals(winningPlayer)) {
                    dealInPlayer = selectedName;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });

        container.addView(nameSpinner);
    }

    private int faanToNumSpinnerPosition(int faan){
        switch (faan) {
            case 3: return 0;
            case 4: return 1;
            case 5: return 2;
            case 6: return 3;
            case 7: return 4;
            case 8: return 5;
            case 9: return 6;
            case 10: return 7;
            default: return -1;
        }
    }

    //when press the button, popup window or discard popup window
    private static void togglePopUpVisibility(LinearLayout layout) {
        if (layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
            if (colorAnimator != null) colorAnimator.pause();
        } else {
            layout.setVisibility(View.VISIBLE);
            if (colorAnimator != null) colorAnimator.resume();
        }
    }

    // press other area to discard the popup window
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Array of all closeable pop-up views
        View[] popUpViews = {popUpLayout1, popUpLayout2, popUpLayout3, popUpLayout4, popUpLayout5, popUpLayout6, popUpLayout7, popUpLayout8, popUpLayout9};

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
            double score = 1.5 * FaanCalculator.calculateScore(Integer.parseInt(faan), baseFaan);
            updateScoreAndUI(playerWin, (int) score, scoreChanges);
            updateOtherPlayers(playerWin, -score / 3, scoreChanges, playerNames);
            game.recordRound(getPlayerName(playerWin), HKMJ.WinType.SELF_DRAW, playerNames, scoreChanges);

        } else if (dealInPlayer.equals("wrong-draw")) {
            // 诈胡逻辑
            double score = 3 * FaanCalculator.calculateScore(10, baseFaan);
            updateScoreAndUI(playerWin, (int) -score, scoreChanges);
            updateOtherPlayers(playerWin, score / 3, scoreChanges, playerNames);
            game.recordRound(getPlayerName(playerWin), HKMJ.WinType.SELF_DRAW, playerNames, scoreChanges);

        } else {
            // 普通胡牌逻辑
            double score = FaanCalculator.calculateScore(Integer.parseInt(faan), baseFaan);
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
    private void drawButton(){
        drawButton = findViewById(R.id.drawButton);
        drawButton.setOnClickListener(v -> {
            Map<String, Integer> scoreChanges = new HashMap<>();
            scoreChanges.put(game.getPlayerNameBySeat(HKMJ.Seat.EAST), 0);
            scoreChanges.put(game.getPlayerNameBySeat(HKMJ.Seat.SOUTH), 0);
            scoreChanges.put(game.getPlayerNameBySeat(HKMJ.Seat.WEST), 0);
            scoreChanges.put(game.getPlayerNameBySeat(HKMJ.Seat.NORTH), 0);
            game.recordRound("Draw", HKMJ.WinType.DRAW, Arrays.asList("Player1","Player2", "Player3","player4"), scoreChanges);

            game.rotateSeats();
            updateDealerIndicators(getDealerIndicatorId(game.currentDealer));
            game.handleConsecutiveWin(false);
            game.nextRoundSeat();
            updateInfoText();

        });
    }
    private void backwardButton(){
        // popup previous round
        popUpLayout7 = findViewById(R.id.popUpBackward);
        setupButton(R.id.backwardRound,popUpLayout7);

        TextView cancel = findViewById(R.id.cancelBackward);

        cancel.setOnClickListener(v -> togglePopUpVisibility(popUpLayout7));

        // confirm back to previous round
        firmBackward = findViewById(R.id.firmBackward);
        firmBackward.setOnClickListener(v -> {
            if (game.gameList() != null) {
                game.popGameRecord();
                updateInfoText();
                updateDealerIndicators(getDealerIndicatorId(game.currentDealer));
                for (HKMJ.Seat seat : HKMJ.Seat.values()) {
                    displayScore(seat, game.getScoreBySeat(seat));
                }
                Toast.makeText(this, "Undo success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No rounds to undo", Toast.LENGTH_SHORT).show();
            }
            togglePopUpVisibility(popUpLayout7);

        });
    }
    private void cashOutButton() {
        popUpLayout8 = findViewById(R.id.popUpCashOut);
        setupButton(R.id.backwardRound,popUpLayout8);

        ImageButton cashOut = findViewById(R.id.cashOut); // Add this button to your layout
        cashOut.setOnClickListener(v -> {

            togglePopUpVisibility(popUpLayout8);

            List<HKMJ.Transaction> transactions = game.calculateSettlement();

            // Display the transactions (e.g., in a Toast, TextView, or Dialog)
            StringBuilder result = new StringBuilder("Settlement Transactions:\n");
            for (HKMJ.Transaction t : transactions) {
                result.append(t.toString()).append("\n");
            }

            TextView scoreTextView = findViewById(R.id.whoPayWho);
            scoreTextView.setText(result);

            //Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
        });

    }
    private void eatPanelButton(){
        // popup eat panel
        popUpLayout1 = findViewById(R.id.popUp);

        for (int playerId : playerList) {
            ImageButton playerButton = findViewById(playerId);
            playerButton.setOnClickListener(v -> {
                togglePopUpVisibility(popUpLayout1);
                currentPlayer = playerId; // update current player
                game.putCurrentEat(RIdToSeat(currentPlayer)); // for EnterFaanActivity to check which seat is using


                // Refresh the spinner with the updated currentPlayer
                LinearLayout container = findViewById(R.id.numberListContainer2);
                container.removeAllViews(); // Clear previous spinner
                addNameSpinnerToContainer(container, playerNames);
            });
        }

        // confirm eat
        yes = findViewById(R.id.yes);
        yes.setOnClickListener(v -> {
            // calculate score
            calculateFaan();
            // 冧莊
            HKMJ.Seat playerSeat = RIdToSeat(currentPlayer);
            if (playerSeat == game.currentDealer) {
                game.handleConsecutiveWin(true); // 冧莊 +1
            }
            // no 冧莊
            else {
                game.rotateSeats();
                updateDealerIndicators(getDealerIndicatorId(game.currentDealer));
                game.handleConsecutiveWin(false); // 冧莊 = 0
                if(game.nextRoundSeat()){
                     Toast.makeText(getApplicationContext(), game.currentRoundSeat + " Round", Toast.LENGTH_SHORT).show();
                }

            }
            togglePopUpVisibility(popUpLayout1); // close the popup
            updateInfoText();

            // Play sound
            MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ding_louder);
            mediaPlayer.start();

        });
    }

    // create new game.........
    private void newGameButton() {
        // popup new game panel
        popUpLayout2 = findViewById(R.id.popUpStartNewGame);
        setupButton(R.id.startNewGame, popUpLayout2);
        TextView firm = findViewById(R.id.newGameFirm);
        TextView cancel = findViewById(R.id.newGameCancel);

        cancel.setOnClickListener(v -> togglePopUpVisibility(popUpLayout2));

        firm.setOnClickListener(v -> {
            // 重置游戏数据
            resetGameData();
            // 重置UI状态
            resetUIState();
            // 关闭弹窗
            togglePopUpVisibility(popUpLayout2);
            Toast.makeText(this, "New game started", Toast.LENGTH_SHORT).show();
        });
    }
    private void resetGameData() {
        // 重置单例实例
        HKMJ.resetInstance(getFilesDir());
        game = HKMJ.getInstance();
        numberOfPlayersAdded = 0;
        currentPlayer = 0;
        playerNames = new ArrayList<>(Arrays.asList("self-draw", "wrong-draw"));
    }
    private void resetUIState() {
        LinearLayout numberListContainer = findViewById(R.id.numberListContainer);
        numberListContainer.removeAllViews(); // 清空番数选择器

        LinearLayout nameSpinnerContainer = findViewById(R.id.numberListContainer2);
        nameSpinnerContainer.removeAllViews(); // 清空玩家名称选择器

        LinearLayout faanListContainer = findViewById(R.id.faanNumberListContainer);
        faanListContainer.removeAllViews(); // clear starting chose faan
        // 重置玩家名称标签
        int[] nameTags = {
                R.id.eastPlayerName,
                R.id.southPlayerName,
                R.id.westPlayerName,
                R.id.northPlayerName
        };
        for (int tag : nameTags) {
            ((TextView) findViewById(tag)).setText("");
        }

        // all black 0
        for (HKMJ.Seat seat : HKMJ.Seat.values()) {
            displayScore(seat, 0);
        }

        // 重置分数显示
        int[] scoreViews = {
                R.id.eastPlayerScore,
                R.id.southPlayerScore,
                R.id.westPlayerScore,
                R.id.northPlayerScore
        };
        for (int scoreView : scoreViews) {
            ((TextView) findViewById(scoreView)).setText("0");
            findViewById(scoreView).setVisibility(View.GONE);
        }

        // 重新启用所有玩家按钮
        for (int playerId : playerList) {
            ImageButton btn = findViewById(playerId);
            btn.setEnabled(true);
        }

        // 回到初始UI状态
        setupPrePlayerAdditionUI();
    }
    // create new game.........

    private void startAddPlayerName(){
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

    // starting set base Faan.........
    private void setBaseFaan(){
        LinearLayout popUpsetBaseFaan = findViewById(R.id.popUpFaanBase);
        LinearLayout container = findViewById(R.id.faanNumberListContainer);
        togglePopUpVisibility(popUpsetBaseFaan);
        addBaseFaanToScrollView(container);

        TextView firmFaan = findViewById(R.id.firmFaan);
        firmFaan.setOnClickListener(v -> {
            baseFaan = tempFaan;
            togglePopUpVisibility(popUpsetBaseFaan);
        });
    }
    private void addBaseFaanToScrollView(LinearLayout container) {
        // Create list of numbers for the spinner
        List<String> numbers = new ArrayList<>();
        numbers.add(String.valueOf(32));
        numbers.add(String.valueOf(64));
        numbers.add(String.valueOf(128));


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
                String selectedBaseFaan = numbers.get(position);
                tempFaan = Integer.parseInt(selectedBaseFaan);
                // Handle selection here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle no selection
            }
        });
        container.addView(numberSpinner);
    }
    // starting set base Faan.........

    // testing button
    private void infoButton() {
        updateInfoText(); // Initial setup
        // Remove the empty click listener if not needed
        findViewById(R.id.info).setOnClickListener(v -> updateInfoText());
    }

    private void updateInfoText() {
        Button info = findViewById(R.id.info);
        info.setText("ROUND: " + game.currentRoundSeat.toString() + " DEALER: " + game.currentDealer.toString());
    }

    private void changeDealerButton(){
        // popup Master Setting panel
        popUpLayout3 = findViewById(R.id.popUpMasterSetting);
        setupButton(R.id.masterSetting,popUpLayout3);

        int[] dealerId ={
                R.id.eastPlayerMaster,
                R.id.southPlayerMaster,
                R.id.westPlayerMaster,
                R.id.northPlayerMaster
        };
        AtomicInteger whoIsChosen = new AtomicInteger();
        for (int dealerID: dealerId){
            ImageButton chosenDealer = findViewById(dealerID);
            chosenDealer.setOnClickListener(v -> {
                whoIsChosen.set(dealerID);

                // Update alpha for all buttons
                for (int id : dealerId) {
                    ImageButton button = findViewById(id);
                    if (button != null) { // Null check for safety
                        if (id == whoIsChosen.get()) {
                            button.setAlpha(1f); // Highlight the chosen button
                        } else {
                            button.setAlpha(0.5f); // Dim other buttons
                        }
                    }
                }
            });
        }
        // for switch1 resetConsecutiveDealer
        AtomicBoolean resetCheck = new AtomicBoolean(false);
        SwitchCompat resetConsecutiveDealer = findViewById(R.id.switch1);
        resetConsecutiveDealer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch is turned on
                resetCheck.set(true);
                resetConsecutiveDealer.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#89BD74")));
            } else {
                // Switch is turned off
                resetCheck.set(false);
                resetConsecutiveDealer.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#C5D0BF")));
            }
        });

        // Optionally set an initial state
        resetConsecutiveDealer.setChecked(false);

        TextView confirm = findViewById(R.id.firmChosenDealer);
        confirm.setOnClickListener(v -> {
            game.currentDealer = changeDealerRIdToSeat(whoIsChosen.get());
            updateInfoText();
            if(resetCheck.get()){
                game.setConsecutiveWins(0);
            }
            updateDealerIndicators(getDealerIndicatorId(game.currentDealer));
            togglePopUpVisibility(popUpLayout3);
        });
    }

    private void rollDiceButton(){
        // popup dice panel
        popUpLayout5 = findViewById(R.id.popUpDice);
        setupButton(R.id.dice,popUpLayout5);

        AtomicInteger total = new AtomicInteger();
        TextView diceTotal = findViewById(R.id.diceTotal); // Cast to TextView

        TextView roll = findViewById(R.id.roll);
        roll.setOnClickListener(v -> {
            // Array of ImageView IDs for the dice
            int[] diceIds = {R.id.dice1, R.id.dice2, R.id.dice3};
            total.set(0);

            // Loop through the dice IDs
            for (int diceId : diceIds) {
                ImageView dice = findViewById(diceId);
                if (dice != null) { // Null check for safety
                    int randomNum = randomNum(1, 6); // Generate a random number between 1 and 6
                    total.addAndGet(randomNum);
                    String srcDice = "dice_" + randomNum; // Build resource name dynamically
                    int resID = getResources().getIdentifier(srcDice, "drawable", getPackageName()); // Get resource ID
                    dice.setImageResource(resID); // Set the dice image
                }
            }

            diceTotal.setText(String.valueOf(total.get())); // Set the text
        });
    }

    private void shareButton() {
        popUpLayout9 = findViewById(R.id.popUpShare);
        setupButton(R.id.share, popUpLayout9);

        ImageView capImage = findViewById(R.id.homeImage);
        TextView share = findViewById(R.id.shareButton);
        AtomicReference<Bitmap> screenshot = new AtomicReference<>(); // Initialize without capturing yet

        // Capture screenshot and display it when popup opens
        ImageButton shareBtn = findViewById(R.id.share);
        shareBtn.setOnClickListener(v -> {
            Bitmap bitmap = captureScreen(); // Capture fresh screenshot
            screenshot.set(bitmap); // Store in AtomicReference
            capImage.setImageBitmap(screenshot.get()); // Display screenshot in ImageView
            togglePopUpVisibility(popUpLayout9);
        });

        // Share when "Share!" TextView is clicked
        share.setOnClickListener(v -> {
            Bitmap bitmap = screenshot.get(); // Get the stored screenshot
            if (bitmap != null) {
                // Save screenshot to cache and get URI
                Uri screenshotUri = saveScreenshotToCache(bitmap); // Pass the Bitmap directly
                if (screenshotUri != null) {
                    // Prepare share intent
                    String shareText = generateShareText(); // Optional: Text summary of game results
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Grant permission to read URI

                    // Start share activity
                    startActivity(Intent.createChooser(shareIntent, "Share Game Results"));
                } else {
                    Toast.makeText(this, "Failed to prepare screenshot for sharing", Toast.LENGTH_SHORT).show();
                }
                togglePopUpVisibility(popUpLayout9); // Close popup after sharing
            } else {
                Toast.makeText(this, "No screenshot available to share", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Capture the screen as a Bitmap
    private Bitmap captureScreen() {
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    // Save screenshot to cache directory and return content:// URI
    private Uri saveScreenshotToCache(Bitmap bitmap) {
        File imageFile = new File(getCacheDir(), "game_result_screenshot.png");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // Save as PNG
            fos.flush();
            // Use FileProvider to get a content:// URI
            return FileProvider.getUriForFile(this, "edu.cuhk.csci3310.fileprovider", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Optional: Generate text summary of game results
    private String generateShareText() {
        StringBuilder result = new StringBuilder("Game Results:\n");
        for (HKMJ.Seat seat : HKMJ.Seat.values()) {
            String name = game.getPlayerNameBySeat(seat);
            int score = game.getScoreBySeat(seat);
            if (name != null) {
                result.append(name).append(": ").append(score).append("\n");
            }
        }
        result.append("Round: ").append(game.currentRoundSeat).append(", Dealer: ").append(game.currentDealer);
        return result.toString();
    }

    private int randomNum(int start, int end){
        Random random = new Random();
        return random.nextInt(end) + start;
    }

    private HKMJ.Seat changeDealerRIdToSeat(int playerId) {
        if (playerId == R.id.eastPlayerMaster) {
            return HKMJ.Seat.EAST;
        } else if (playerId == R.id.southPlayerMaster) {
            return HKMJ.Seat.SOUTH;
        } else if (playerId == R.id.westPlayerMaster) {
            return HKMJ.Seat.WEST;
        } else if (playerId == R.id.northPlayerMaster) {
            return HKMJ.Seat.NORTH;
        } else {
            throw new IllegalArgumentException("Invalid player ID: " + playerId);
        }
    }

    private void readJsonFile() {
        File file = new File(getFilesDir(), "edu.cuhk.csci3310/files/game_state.json"); // Adjust path based on your setup
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                StringBuilder content = new StringBuilder();
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    content.append(new String(buffer, 0, bytesRead));
                }
                Log.d("JSON_CONTENT", content.toString()); // Log to Android Logcat
                // Alternatively, show in a Toast
                // Toast.makeText(this, content.toString(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("JSON_CONTENT", "File does not exist");
        }
    }

    private void setupColorAnimation(LinearLayout layout) {
        // Define the colors to cycle through
        int[] colors = {
                Color.RED,    // #FF0000
                Color.BLUE,   // #0000FF
                Color.GREEN,  // #00FF00
                Color.YELLOW, // #FFFF00
                Color.MAGENTA // #FF00FF
        };

        // Create a ValueAnimator to interpolate between colors
        colorAnimator = ValueAnimator.ofInt(colors);
        colorAnimator.setDuration(5000); // Duration for one full cycle (5 seconds)
        colorAnimator.setEvaluator(new android.animation.ArgbEvaluator()); // Smooth color transition
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE); // Repeat indefinitely
        colorAnimator.setRepeatMode(ValueAnimator.RESTART); // Restart from the first color
        colorAnimator.setInterpolator(new LinearInterpolator()); // Smooth, linear transition

        // Update the background color as the animation progresses
        colorAnimator.addUpdateListener(animation -> {
            int animatedColor = (int) animation.getAnimatedValue();
            layout.setBackgroundColor(animatedColor);
        });

        // Start the animation
        colorAnimator.start();
    }

}