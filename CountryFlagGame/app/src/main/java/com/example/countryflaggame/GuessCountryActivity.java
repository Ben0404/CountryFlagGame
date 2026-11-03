package com.example.countryflaggame;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

/**
 * Guess the Country mode.
 * Shows one random flag and asks the user to select its country from a Spinner.
 */
public class GuessCountryActivity extends AppCompatActivity {

    // Main game controls: the displayed flag and the country dropdown.
    private ImageView flagImage;
    private Spinner countrySpinner;

    // Text views used for feedback, the revealed answer and the round score.
    private TextView resultText;
    private TextView correctAnswerText;
    private TextView scoreText;

    // Submit/Next controls the round; Back returns to the menu after confirmation.
    private Button submitButton;
    private Button backButton;

    // Random is used to select a flag index for each new round.
    private final Random random = new Random();

    // currentFlagIndex identifies the active country.
    // previousFlagIndex prevents the same flag appearing in consecutive rounds.
    private int currentFlagIndex;
    private int previousFlagIndex = -1;

    // Score for the current round only: 0 for wrong, 1 for correct.
    private int roundScore = 0;

    // Tracks whether Submit should check the answer or start the next round.
    private boolean roundFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the Guess the Country screen.
        setContentView(R.layout.activity_guess_country);

        // Bind the Java fields to the views declared in activity_guess_country.xml.
        flagImage = findViewById(R.id.flagImage);
        countrySpinner = findViewById(R.id.countrySpinner);

        resultText = findViewById(R.id.resultText);
        correctAnswerText = findViewById(R.id.correctAnswerText);
        scoreText = findViewById(R.id.scoreText);

        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);

        // Build an adapter containing all 21 country names for the Spinner.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        FlagData.COUNTRY_NAMES
                );

        // Use Android's standard dropdown-row layout when the Spinner opens.
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        // Attach the country-name adapter to the Spinner.
        countrySpinner.setAdapter(adapter);

        // The same button checks an answer during play and starts a new round after completion.
        submitButton.setOnClickListener(v -> {

            if (roundFinished) {
                // Initialise the first playable round.
        startNewRound();
            } else {
                checkAnswer();
            }
        });

        // Show an exit confirmation instead of immediately leaving the game.
        backButton.setOnClickListener(
                v -> showExitDialog()
        );

        startNewRound();

        // Make the Android system Back action use the same confirmation dialog.
        setupSystemBackButton();
    }

    /** Resets the screen and selects a new non-repeating random flag. */
    private void startNewRound() {

        // Mark the new round as active so Submit checks an answer.
        roundFinished = false;

        // Every round starts with a score of zero.
        roundScore = 0;

        updateScore();

        // Remove feedback left over from the previous round.
        resultText.setText("");
        correctAnswerText.setText("");

        // Restore the button label from Next back to Submit.
        submitButton.setText("Submit");

        // Allow the user to choose an answer again.
        countrySpinner.setEnabled(true);

        int newIndex;

        // Keep generating an index until it differs from the previous round.
        do {

            newIndex = random.nextInt(
                    FlagData.COUNTRY_NAMES.length
            );

        } while (newIndex == previousFlagIndex);

        // Store the chosen random index as the active flag for this round.
        currentFlagIndex = newIndex;

        // Remember this flag so it cannot be chosen again immediately next round.
        previousFlagIndex =
                currentFlagIndex;

        // Display the drawable that matches the randomly selected country index.
        flagImage.setImageResource(
                FlagData.FLAG_IMAGES[
                        currentFlagIndex
                        ]
        );
    }

    /** Compares the Spinner selection with the country belonging to the current flag. */
    private void checkAnswer() {

        // Read the country currently selected by the user.
        String selectedCountry =
                countrySpinner
                        .getSelectedItem()
                        .toString();

        // Look up the correct country using the current flag index.
        String correctCountry =
                FlagData.COUNTRY_NAMES[
                        currentFlagIndex
                        ];

        // Compare case-insensitively so letter case does not affect correctness.
        if (selectedCountry
                .equalsIgnoreCase(
                        correctCountry
                )) {

            resultText.setText(
                    "CORRECT!"
            );

            resultText.setTextColor(
                    Color.GREEN
            );

            // A correct country is worth one point for this round.
            roundScore = 1;

        } else {

            resultText.setText(
                    "WRONG!"
            );

            resultText.setTextColor(
                    Color.RED
            );

            // On a wrong answer, reveal the correct country name.
            correctAnswerText.setText(
                    "Correct answer: "
                            + correctCountry
            );

            correctAnswerText.setTextColor(
                    Color.BLUE
            );
        }

        // Refresh the score display after the answer has been checked.
        updateScore();

        // Lock the round so the next press starts a new round instead of scoring again.
        roundFinished = true;

        // Prevent changing the answer after the round has finished.
        countrySpinner.setEnabled(false);
        // Change Submit to Next to indicate that the round is complete.
        submitButton.setText("Next");
    }

    /** Updates the top-right score for the current round. */
    private void updateScore() {

        scoreText.setText(
                "Score: " + roundScore
        );
    }

    /** Asks the user to confirm before leaving the current game mode. */
    private void showExitDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Exit Game")
                .setMessage(
                        "Are you sure you want to return to the game menu?"
                )
                .setPositiveButton(
                        "Yes",
                        (dialog, which) -> finish()
                )
                .setNegativeButton(
                        "No",
                        null
                )
                .show();
    }

    /** Overrides the system Back action so it also shows the exit confirmation. */
    private void setupSystemBackButton() {

        getOnBackPressedDispatcher()
                .addCallback(
                        this,
                        new OnBackPressedCallback(true) {

                            @Override
                            public void handleOnBackPressed() {

                                showExitDialog();
                            }
                        }
                );
    }
}