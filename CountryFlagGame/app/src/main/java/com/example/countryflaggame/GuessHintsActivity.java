package com.example.countryflaggame;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

/**
 * Guess-Hints mode.
 * The user reveals a hidden country name by entering one letter at a time.
 */
public class GuessHintsActivity extends AppCompatActivity {

    // Displays the randomly selected flag for the current round.
    private ImageView flagImage;

    // Text views for the dashed word, attempts, feedback, revealed answer and score.
    private TextView hiddenWordText;
    private TextView attemptText;
    private TextView resultText;
    private TextView correctAnswerText;
    private TextView scoreText;

    // Input field where the player enters one character per guess.
    private EditText letterInput;

    // Submit/Next handles the round; Back opens the exit confirmation.
    private Button submitButton;
    private Button backButton;

    // Used to choose a random country index for each round.
    private final Random random =
            new Random();

    // Track the current flag and the previous flag to prevent immediate repeats.
    private int currentFlagIndex;
    private int previousFlagIndex = -1;

    // Counts incorrect character guesses; the round ends at three.
    private int wrongGuesses = 0;

    // Current round score: 1 only when the full country name is completed.
    private int roundScore = 0;

    // Stores the correct country name for the current flag.
    private String countryName;

    // Mutable character array containing dashes and any letters already revealed.
    private char[] hiddenCountry;

    // Determines whether the main button should check a guess or start another round.
    private boolean roundFinished =
            false;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        // Load the Guess-Hints XML screen.
        setContentView(
                R.layout.activity_guess_hints
        );

        // Bind Java variables to the matching views from activity_guess_hints.xml.
        flagImage =
                findViewById(R.id.flagImage);

        hiddenWordText =
                findViewById(R.id.hiddenWordText);

        attemptText =
                findViewById(R.id.attemptText);

        resultText =
                findViewById(R.id.resultText);

        correctAnswerText =
                findViewById(R.id.correctAnswerText);

        scoreText =
                findViewById(R.id.scoreText);

        letterInput =
                findViewById(R.id.letterInput);

        submitButton =
                findViewById(R.id.submitButton);

        backButton =
                findViewById(R.id.backButton);

        // Submit checks a letter during play; after the round it starts a new round.
        submitButton.setOnClickListener(v -> {

            if (roundFinished) {
                // Prepare the first round when the activity opens.
        startNewRound();
            } else {
                checkLetter();
            }
        });

        // Show a confirmation before leaving the game.
        backButton.setOnClickListener(
                v -> showExitDialog()
        );

        startNewRound();

        // Give the Android Back action the same confirmation behaviour.
        setupSystemBackButton();
    }

    /** Resets round state, chooses a non-repeating flag and creates the dashed word. */
    private void startNewRound() {

        // The new round is active, so Submit should process guesses.
        roundFinished = false;

        // Reset the incorrect-guess counter for the new country.
        wrongGuesses = 0;

        // Reset the score because scoring is per round.
        roundScore = 0;

        updateScore();

        // Clear any result message from the previous round.
        resultText.setText("");

        // Remove any previously revealed correct answer.
        correctAnswerText.setText("");

        // Clear the character-entry box.
        letterInput.setText("");

        // Re-enable letter entry for the new round.
        letterInput.setEnabled(true);

        // Change Next back to Submit at the start of a round.
        submitButton.setText("Submit");

        // Reset the visible attempt counter.
        attemptText.setText(
                "Incorrect guesses: 0 / 3"
        );

        int newIndex;

        // Choose a random flag but reject the flag used in the previous round.
        do {

            newIndex = random.nextInt(
                    FlagData.COUNTRY_NAMES.length
            );

        } while (
                newIndex ==
                        previousFlagIndex
        );

        currentFlagIndex =
                newIndex;

        previousFlagIndex =
                currentFlagIndex;

        // Store the answer string for the chosen flag.
        countryName =
                FlagData.COUNTRY_NAMES[
                        currentFlagIndex
                        ];

        // Display the chosen flag image.
        flagImage.setImageResource(
                FlagData.FLAG_IMAGES[
                        currentFlagIndex
                        ]
        );

        // Create an array with one position for every character in the country name.
        hiddenCountry =
                new char[
                        countryName.length()
                        ];

        // Replace each letter with a dash while preserving any spaces.
        for (int i = 0;
             i < hiddenCountry.length;
             i++) {

            if (countryName.charAt(i) == ' ') {

                // Reveal the matching letter at this position.
                hiddenCountry[i] = ' ';

            } else {

                hiddenCountry[i] = '-';
            }
        }

        // Show the completed dashed representation on screen.
        updateHiddenWord();
    }

    /** Validates one character and reveals every matching occurrence in the answer. */
    private void checkLetter() {

        // Read and trim the user's current input.
        String input =
                letterInput
                        .getText()
                        .toString()
                        .trim();

        // Accept exactly one alphabetic character; otherwise show validation feedback.
        if (input.length() != 1 ||
                !Character.isLetter(
                        input.charAt(0)
                )) {

            Toast.makeText(
                    this,
                    "Please enter one letter",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Convert the guess to lowercase so matching is case-insensitive.
        char guessedLetter =
                Character.toLowerCase(
                        input.charAt(0)
                );

        // Track whether this guess matches at least one character in the country name.
        boolean letterFound =
                false;

        // Check the guessed character against every character in the country name.
        for (int i = 0;
             i < countryName.length();
             i++) {

            char actualLetter =
                    Character.toLowerCase(
                            countryName.charAt(i)
                    );

            if (actualLetter ==
                    guessedLetter) {

                hiddenCountry[i] =
                        Character.toUpperCase(
                                countryName.charAt(i)
                        );

                letterFound = true;
            }
        }

        // Only incorrect guesses increase the attempt counter.
        if (!letterFound) {

            wrongGuesses++;

            attemptText.setText(
                    "Incorrect guesses: "
                            + wrongGuesses
                            + " / 3"
            );
        }

        updateHiddenWord();

        // Clear the box ready for the next character guess.
        letterInput.setText("");

        // Check whether the word is complete or the player has reached three wrong guesses.
        checkRoundEnd();
    }

    /** Converts the hidden-character array to text and displays it. */
    private void updateHiddenWord() {

        hiddenWordText.setText(
                new String(hiddenCountry)
        );
    }

    /** Ends the round after success or after three incorrect character guesses. */
    private void checkRoundEnd() {

        // The word is complete when no dash characters remain.
        boolean wordCompleted =
                !new String(
                        hiddenCountry
                ).contains("-");

        if (wordCompleted) {

            resultText.setText(
                    "CORRECT!"
            );

            resultText.setTextColor(
                    Color.GREEN
            );

            // Completing the country name awards one point for the round.
            roundScore = 1;

            updateScore();

            finishRound();

        } else if (
                wrongGuesses >= 3
        ) {

            resultText.setText(
                    "WRONG!"
            );

            resultText.setTextColor(
                    Color.RED
            );

            // After three wrong guesses, reveal the correct country name.
            correctAnswerText.setText(
                    "Correct answer: "
                            + countryName
            );

            correctAnswerText.setTextColor(
                    Color.BLUE
            );

            updateScore();

            finishRound();
        }
    }

    /** Locks the round and changes the main button from Submit to Next. */
    private void finishRound() {

        roundFinished = true;

        // Prevent more character guesses after the round has ended.
        letterInput.setEnabled(false);

        submitButton.setText("Next");
    }

    /** Displays the score for the current round in the header. */
    private void updateScore() {

        scoreText.setText(
                "Score: " + roundScore
        );
    }

    /** Displays a confirmation dialog before leaving this game mode. */
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

    /** Routes the system Back action through the same exit confirmation. */
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