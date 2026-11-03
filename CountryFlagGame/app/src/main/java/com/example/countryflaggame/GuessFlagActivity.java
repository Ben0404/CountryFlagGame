package com.example.countryflaggame;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Guess the Flag mode.
 * Displays three flags and asks the user to click the one matching a country name.
 */
public class GuessFlagActivity extends AppCompatActivity {

    // Three clickable flag images presented as the possible answers.
    private ImageButton flagButton1;
    private ImageButton flagButton2;
    private ImageButton flagButton3;

    // Displays the target country name, result feedback and current round score.
    private TextView countryNameText;
    private TextView resultText;
    private TextView scoreText;

    // Next begins another round; Back asks for exit confirmation.
    private Button nextButton;
    private Button backButton;

    // Random generator used to choose flags and the target position.
    private final Random random =
            new Random();

    // Holds the three country indexes shown in the current round.
    private final int[] currentFlags =
            {-1, -1, -1};

    // Stores the previous round so those flags can be avoided immediately after a reset.
    private final int[] previousFlags =
            {-1, -1, -1};

    // Tracks which flags have already been used before the pool is recycled.
    private final boolean[] usedFlags =
            new boolean[
                    FlagData.COUNTRY_NAMES.length
                    ];

    // Position 0, 1 or 2 indicates which displayed flag matches the target country name.
    private int correctPosition;

    // One point is awarded only when the selected flag is correct.
    private int roundScore = 0;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        // Load the Guess the Flag XML layout.
        setContentView(
                R.layout.activity_guess_flag
        );

        // Bind the Java controls to their matching XML views.
        flagButton1 =
                findViewById(R.id.flagButton1);

        flagButton2 =
                findViewById(R.id.flagButton2);

        flagButton3 =
                findViewById(R.id.flagButton3);

        countryNameText =
                findViewById(R.id.countryNameText);

        resultText =
                findViewById(R.id.resultText);

        scoreText =
                findViewById(R.id.scoreText);

        nextButton =
                findViewById(R.id.nextButton);

        backButton =
                findViewById(R.id.backButton);

        // Each flag immediately submits its own position as the user's answer.
        flagButton1.setOnClickListener(
                v -> checkAnswer(0)
        );

        flagButton2.setOnClickListener(
                v -> checkAnswer(1)
        );

        flagButton3.setOnClickListener(
                v -> checkAnswer(2)
        );

        // Next starts another round after the result has been shown.
        nextButton.setOnClickListener(
                v -> startNewRound()
        );

        // Back shows a confirmation before leaving the mode.
        backButton.setOnClickListener(
                v -> showExitDialog()
        );

        // Set up the first set of three flags.
        startNewRound();

        // Apply the same confirmation to the Android system Back action.
        setupSystemBackButton();
    }

    /** Resets the round, selects three flags and chooses one as the target answer. */
    private void startNewRound() {

        // Scoring is per round, so every new round begins at zero.
        roundScore = 0;

        updateScore();

        // Remove the previous CORRECT/WRONG feedback.
        resultText.setText("");

        // Hide Next until the player has selected a flag.
        // Reveal the Next button now that the round has ended.
        nextButton.setVisibility(
                View.GONE
        );

        // Allow all three flag images to be selected again.
        enableFlagButtons();

        /*
         * Restore the normal card background
         * after the previous round.
         */
        flagButton1.setBackgroundResource(
                R.drawable.secondary_button
        );

        flagButton2.setBackgroundResource(
                R.drawable.secondary_button
        );

        flagButton3.setBackgroundResource(
                R.drawable.secondary_button
        );

        // Choose three distinct flags for this round.
        generateThreeFlags();

        // Load the selected drawable resources into the three ImageButtons.
        flagButton1.setImageResource(
                FlagData.FLAG_IMAGES[
                        currentFlags[0]
                        ]
        );

        flagButton2.setImageResource(
                FlagData.FLAG_IMAGES[
                        currentFlags[1]
                        ]
        );

        flagButton3.setImageResource(
                FlagData.FLAG_IMAGES[
                        currentFlags[2]
                        ]
        );

        // Randomly choose which of the three displayed flags will be the target answer.
        correctPosition =
                random.nextInt(3);

        // Display the country name belonging to the target flag.
        countryNameText.setText(
                FlagData.COUNTRY_NAMES[
                        currentFlags[
                                correctPosition
                                ]
                        ]
        );

        // Save this set so the next reset can recognise the immediately previous flags.
        copyCurrentToPrevious();
    }

    /** Builds a pool of available flags and randomly selects three different indexes. */
    private void generateThreeFlags() {

        // Temporary list of flag indexes that are currently available for selection.
        List<Integer> availableIndexes =
                new ArrayList<>();

        // Add every flag that has not yet been used to the available pool.
        for (int i = 0;
             i < FlagData.COUNTRY_NAMES.length;
             i++) {

            if (!usedFlags[i]) {

                availableIndexes.add(i);
            }
        }

        /*
         * Once all 21 flags have been used,
         * reset the available flag pool.
         */
        if (availableIndexes.size() < 3) {

            // All flags have effectively been used, so recycle the pool.
            resetUsedFlags();

            // Rebuild the list after resetting the usage markers.
            availableIndexes.clear();

            /*
             * Even after the reset, don't immediately
             * reuse a flag from the previous round.
             */
            for (int i = 0;
                 i < FlagData.COUNTRY_NAMES.length;
                 i++) {

                if (!wasInPreviousRound(i)) {

                    availableIndexes.add(i);
                }
            }
        }

        /*
         * Select three different flags.
         */
        for (int position = 0;
             position < 3;
             position++) {

            // Pick a random position from the remaining available-index list.
            int randomListPosition =
                    random.nextInt(
                            availableIndexes.size()
                    );

            // Convert that random list position into the actual country/flag index.
            int selectedFlag =
                    availableIndexes.get(
                            randomListPosition
                    );

            // Store the chosen flag in the current round.
            currentFlags[position] =
                    selectedFlag;

            // Mark it as used so it will not be selected again from the pool.
            usedFlags[selectedFlag] =
                    true;

            // Remove the chosen index so the other positions in this round must be different.
            availableIndexes.remove(
                    randomListPosition
            );
        }
    }

    /** Clears the used-flag history when the available pool needs to be recycled. */
    private void resetUsedFlags() {

        for (int i = 0;
             i < usedFlags.length;
             i++) {

            usedFlags[i] = false;
        }
    }

    /** Returns true when an index appeared in the immediately previous set of three flags. */
    private boolean wasInPreviousRound(
            int index
    ) {

        for (int previousFlag :
                previousFlags) {

            if (previousFlag == index) {

                return true;
            }
        }

        return false;
    }

    /** Copies the three current indexes into previousFlags for the next round. */
    private void copyCurrentToPrevious() {

        for (int i = 0;
             i < 3;
             i++) {

            previousFlags[i] =
                    currentFlags[i];
        }
    }

    /** Checks the clicked flag position, locks all choices and shows the result. */
    private void checkAnswer(
            int selectedPosition
    ) {

        // Only one attempt is allowed, so disable all flags immediately after a click.
        disableFlagButtons();

        if (selectedPosition ==
                correctPosition) {

            resultText.setText(
                    "CORRECT!"
            );

            resultText.setTextColor(
                    Color.GREEN
            );

            // Award one point when the selected flag matches the target position.
            roundScore = 1;

        } else {

            resultText.setText(
                    "WRONG!"
            );

            resultText.setTextColor(
                    Color.RED
            );
        }

        updateScore();

        // Highlight the correct flag in blue regardless of whether the user was right or wrong.
        highlightCorrectFlag();

        nextButton.setVisibility(
                View.VISIBLE
        );
    }

    /** Applies a blue background to whichever ImageButton contains the correct flag. */
    private void highlightCorrectFlag() {

        if (correctPosition == 0) {

            flagButton1.setBackgroundColor(
                    Color.BLUE
            );

        } else if (
                correctPosition == 1
        ) {

            flagButton2.setBackgroundColor(
                    Color.BLUE
            );

        } else {

            flagButton3.setBackgroundColor(
                    Color.BLUE
            );
        }
    }

    /** Prevents further answers after one flag has been selected. */
    private void disableFlagButtons() {

        flagButton1.setEnabled(false);
        flagButton2.setEnabled(false);
        flagButton3.setEnabled(false);
    }

    /** Re-enables all flag choices at the start of a new round. */
    private void enableFlagButtons() {

        flagButton1.setEnabled(true);
        flagButton2.setEnabled(true);
        flagButton3.setEnabled(true);
    }

    /** Updates the visible current-round score. */
    private void updateScore() {

        scoreText.setText(
                "Score: " + roundScore
        );
    }

    /** Confirms whether the player wants to leave this mode. */
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

    /** Sends the Android system Back action through the exit-confirmation dialog. */
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