package com.example.countryflaggame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Displays the four available game modes and navigates to the selected mode.
 */
public class GameMenuActivity extends AppCompatActivity {

    // Navigation controls for the game-mode menu.
    private Button backButton;
    private Button guessCountryButton;
    private Button guessHintsButton;
    private Button guessFlagButton;
    private Button advancedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the XML layout containing the four mode cards/buttons.
        setContentView(R.layout.activity_game_menu);

        // Connect each Java button variable to the matching XML control.
        backButton =
                findViewById(R.id.backButton);

        guessCountryButton =
                findViewById(R.id.guessCountryButton);

        guessHintsButton =
                findViewById(R.id.guessHintsButton);

        guessFlagButton =
                findViewById(R.id.guessFlagButton);

        advancedButton =
                findViewById(R.id.advancedButton);

        // Close this activity and return to the main menu.
        backButton.setOnClickListener(v -> finish());

        // Open Guess the Country mode.
        guessCountryButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    GameMenuActivity.this,
                    GuessCountryActivity.class
            );

            startActivity(intent);
        });

        // Open Guess-Hints mode.
        guessHintsButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    GameMenuActivity.this,
                    GuessHintsActivity.class
            );

            startActivity(intent);
        });

        // Open Guess the Flag mode.
        guessFlagButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    GameMenuActivity.this,
                    GuessFlagActivity.class
            );

            startActivity(intent);
        });

        // Open Advanced mode.
        advancedButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    GameMenuActivity.this,
                    AdvancedActivity.class
            );

            startActivity(intent);
        });
    }
}