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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private LinearLayout popUpLayout1; // popup eat panel
    private LinearLayout popUpLayout2; // popup new game panel
    private LinearLayout popUpLayout3; // popup Master Setting panel
    private LinearLayout popUpLayout4; // popup Add New Player panel
    private LinearLayout popUpLayout5; // popup dice panel
    private LinearLayout popUpLayout6; // popup add player name
    private LinearLayout dropDownLayout1; // popup drop down panel
    private List<String> playerNames = new ArrayList<>(); // create name
    private final int maxNameLength = 10; // Set your maximum name length here
    private TextView yes; // yes button
    private TextView firm; // firm/yes button
    private TextInputEditText addPlayerNameEditText;
    int numberOfPlayersAdded = 0; // for checking no. of players
    // what happen
    int currentPlayer = 0; // for identify current player(playerList)
    HKMJ game = new HKMJ();

    // for player
    int[] playerList = {
            R.id.eastPlayer, //1
            R.id.southPlayer, //2
            R.id.westPlayer, //3
            R.id.northPlayer, //4
    };

    // for name
    int[] nameTag = {
            R.id.eastPlayerName, //1
            R.id.southPlayerName, //2
            R.id.westPlayerName, //3
            R.id.northPlayerName, //4
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        // enable the popup menu buttons
        for(int playerId : playerList){
            ImageButton playerButton = findViewById(playerId);
            playerButton.setEnabled(true);
        }
        // scroll faan, player
        LinearLayout numberListContainer = findViewById(R.id.numberListContainer);
        addNumbersToScrollView(numberListContainer, 1, 10);

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

        //setupButton(R.id.startNewGame,popUpLayout2);
        // popup new game panel
        popUpLayout2 = findViewById(R.id.popUpStartNewGame);
        ImageButton startNewGameButton = findViewById(R.id.startNewGame);
        startNewGameButton.setOnClickListener(v -> togglePopUpVisibility(popUpLayout2));

        // popup Master Setting panel
        popUpLayout3 = findViewById(R.id.popUpMasterSetting);
        ImageButton masterSettingButton = findViewById(R.id.masterSetting);
        masterSettingButton.setOnClickListener(v -> togglePopUpVisibility(popUpLayout3));

        // popup Add New Player panel
        popUpLayout4 = findViewById(R.id.popUpAddNewPlayer);
        ImageButton addNewPlayerButton = findViewById(R.id.newPlayer);
        addNewPlayerButton.setOnClickListener(v -> togglePopUpVisibility(popUpLayout4));

        // popup dice panel
        popUpLayout5 = findViewById(R.id.popUpDice);
        ImageButton diceButton = findViewById(R.id.dice);
        diceButton.setOnClickListener(v -> togglePopUpVisibility(popUpLayout5));

        // drop down menu
        dropDownLayout1 = findViewById(R.id.dropDownMenu);
        ImageButton newPlayerButton = findViewById(R.id.more);
        newPlayerButton.setOnClickListener(v -> togglePopUpVisibility(dropDownLayout1));

        // record page
        ImageButton recordButton = findViewById(R.id.recordPage);
        recordButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
        });

        // confirm eat
        yes = findViewById(R.id.yes);
        yes.setOnClickListener(v -> {
            // 冧莊
            HKMJ.Seat playerSeat = RIdToSeat(currentPlayer);
            if (playerSeat == game.currentDealer) {
                game.handleConsecutiveWin(true);
            }
            // no 冧莊
            else {
                game.rotateSeats();
                updateDealerIndicators(getDealerIndicatorId(game.currentDealer));
            }
            togglePopUpVisibility(popUpLayout1); // close the popup
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
        View[] popUpViews = {popUpLayout1, popUpLayout2, popUpLayout3, popUpLayout4, popUpLayout5, popUpLayout6};

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

    private void setupButton(int id, View popupLayout) {
        ImageButton btn = findViewById(id);
        if (btn != null) {
            btn.setOnClickListener(v -> {
                togglePopUpVisibility((LinearLayout) popupLayout);
                currentPlayer = id;
            });
        }
    }


    // game logic

}