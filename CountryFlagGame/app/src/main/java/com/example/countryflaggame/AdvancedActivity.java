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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Advanced mode.
 * Shows three different flags and checks three country-name text answers over up to
 * three unsuccessful submissions. Correct fields lock and keep their earned points.
 */
public class AdvancedActivity extends AppCompatActivity {

    // The three flag images displayed during the current round.
    private ImageView flagImage1;
    private ImageView flagImage2;
    private ImageView flagImage3;

    // User input fields corresponding to flag 1, flag 2 and flag 3.
    private EditText answerInput1;
    private EditText answerInput2;
    private EditText answerInput3;

    // These views reveal missed country names if the player reaches three failed submissions.
    private TextView correctAnswer1;
    private TextView correctAnswer2;
    private TextView correctAnswer3;

    // Header/feedback text for score, unsuccessful attempts and overall result.
    private TextView scoreText;
    private TextView attemptText;
    private TextView resultText;

    // Submit checks the editable answers; Back opens the exit confirmation.
    private Button submitButton;
    private Button backButton;

    // Used when randomly selecting the three flags for each round.
    private final Random random = new Random();

    // Country/flag indexes currently being displayed.
    private final int[] currentFlags =
            {-1, -1, -1};

    // Country/flag indexes from the previous round, used to prevent immediate repeats.
    private final int[] previousFlags =
            {-1, -1, -1};

    // These booleans remember which answers are already correct so points cannot be awarded twice.
    private boolean answer1Correct =
            false;

    private boolean answer2Correct =
            false;

    private boolean answer3Correct =
            false;

    // Counts submissions where at least one answer is still incorrect.
    private int unsuccessfulAttempts =
            0;

    // One point is added for each newly identified country, up to three points.
    private int roundScore = 0;

    // Controls whether the main button checks answers or starts the next round.
    private boolean roundFinished =
            false;

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {

        super.onCreate(savedInstanceState);

        // Loads the Advanced-mode XML layout.
        setContentView(
                R.layout.activity_advanced
        );

        // Bind all Java fields to their corresponding XML controls.
        flagImage1 =
                findViewById(R.id.flagImage1);

        flagImage2 =
                findViewById(R.id.flagImage2);

        flagImage3 =
                findViewById(R.id.flagImage3);

        answerInput1 =
                findViewById(R.id.answerInput1);

        answerInput2 =
                findViewById(R.id.answerInput2);

        answerInput3 =
                findViewById(R.id.answerInput3);

        correctAnswer1 =
                findViewById(R.id.correctAnswer1);

        correctAnswer2 =
                findViewById(R.id.correctAnswer2);

        correctAnswer3 =
                findViewById(R.id.correctAnswer3);

        scoreText =
                findViewById(R.id.scoreText);

        attemptText =
                findViewById(R.id.attemptText);

        resultText =
                findViewById(R.id.resultText);

        submitButton =
                findViewById(R.id.submitButton);

        backButton =
                findViewById(R.id.backButton);

        // During a round, Submit checks answers. After completion, the same button starts the next round.
        submitButton.setOnClickListener(v -> {

            if (roundFinished) {
                // Initialise the first set of three flags and answer fields.
        startNewRound();
            } else {
                checkAnswers();
            }
        });

        // Show a confirmation dialog before leaving the Advanced game.
        backButton.setOnClickListener(
                v -> showExitDialog()
        );

        startNewRound();

        // Make the Android system Back action use the same confirmation dialog.
        setupSystemBackButton();
    }

    /** Resets every round variable, chooses three new flags and clears the input fields. */
    private void startNewRound() {

        // Mark the new round as active.
        roundFinished = false;
        // No answers have been correctly identified yet in the new round.
        answer1Correct = false;
        answer2Correct = false;
        answer3Correct = false;
        // Give the player a fresh allowance of three unsuccessful submissions.
        unsuccessfulAttempts = 0;

        // Score is per round, so start again from zero.
        roundScore = 0;

        // Refresh the score after any newly correct answers have been awarded.
        updateScore();

        // Reset the visible unsuccessful-attempt counter.
        attemptText.setText(
                "Unsuccessful attempts: 0 / 3"
        );

        // Remove any CORRECT/WRONG message from the previous round.
        resultText.setText("");

        // Hide any correct answers that were revealed at the end of the previous round.
        correctAnswer1.setText("");
        correctAnswer2.setText("");
        correctAnswer3.setText("");

        // Clear all three answer fields.
        answerInput1.setText("");
        answerInput2.setText("");
        answerInput3.setText("");

        // Restore the normal black text colour before new answers are entered.
        answerInput1.setTextColor(
                Color.BLACK
        );

        answerInput2.setTextColor(
                Color.BLACK
        );

        answerInput3.setTextColor(
                Color.BLACK
        );

        // Re-enable all fields; correct fields from the last round may have been locked.
        answerInput1.setEnabled(true);
        answerInput2.setEnabled(true);
        answerInput3.setEnabled(true);

        // Change Next back to Submit for active play.
        submitButton.setText("Submit");

        // Randomly choose three distinct flags that were not used in the immediately previous round.
        generateThreeFlags();

        // Display the drawable resources that match the selected indexes.
        flagImage1.setImageResource(
                FlagData.FLAG_IMAGES[
                        currentFlags[0]
                        ]
        );

        flagImage2.setImageResource(
                FlagData.FLAG_IMAGES[
                        currentFlags[1]
                        ]
        );

        flagImage3.setImageResource(
                FlagData.FLAG_IMAGES[
                        currentFlags[2]
                        ]
        );

        // Save this set so the following round can avoid these three flags.
        copyCurrentToPrevious();
    }

    /** Creates a list excluding previous-round flags and randomly removes three unique indexes. */
    private void generateThreeFlags() {

        // Pool of flag indexes allowed to appear in this round.
        List<Integer> availableIndexes =
                new ArrayList<>();

        /*
         * Do not allow any of the previous
         * round's three flags to appear.
         */
        for (int i = 0;
             i < FlagData.COUNTRY_NAMES.length;
             i++) {

            if (!wasInPreviousRound(i)) {

                availableIndexes.add(i);
            }
        }

        /*
         * Pick three different flags.
         */
        for (int position = 0;
             position < 3;
             position++) {

            // Select a random position from the shrinking available list.
            int randomListPosition =
                    random.nextInt(
                            availableIndexes.size()
                    );

            // Store that chosen country/flag index in the current round.
            currentFlags[position] =
                    availableIndexes.get(
                            randomListPosition
                    );

            // Remove it so the same flag cannot be selected twice within this round.
            availableIndexes.remove(
                    randomListPosition
            );
        }
    }

    /** Checks whether a candidate flag index was shown in the immediately previous round. */
    private boolean wasInPreviousRound(
            int index
    ) {

        for (int previousFlag :
                previousFlags) {

            if (previousFlag == index) {

                // All required editable fields contain input, so answer checking can continue.
        return true;
            }
        }

        return false;
    }

    /** Copies the current three flag indexes for use by the next round's exclusion check. */
    private void copyCurrentToPrevious() {

        for (int i = 0;
             i < 3;
             i++) {

            previousFlags[i] =
                    currentFlags[i];
        }
    }

    /** Validates and checks all still-editable answers, then updates score and attempt state. */
    private void checkAnswers() {

        // Do not consume an attempt if a required editable field has been left blank.
        if (!validateInputs()) {
            return;
        }

        /* FLAG 1: only check it while it has not already been answered correctly. */
        if (!answer1Correct) {

            // Read and trim the player's current answer for the first flag.
            String enteredAnswer1 =
                    answerInput1
                            .getText()
                            .toString()
                            .trim();

            // Look up the correct country corresponding to currentFlags[0].
            String country1 =
                    FlagData.COUNTRY_NAMES[
                            currentFlags[0]
                            ];

            if (enteredAnswer1
                    .equalsIgnoreCase(
                            country1
                    )) {

                // Remember this field is solved so later submissions cannot score it again.
                answer1Correct = true;

                // Award exactly one point the first time this country is identified.
                roundScore++;

                // Show a correct answer in green.
                answerInput1.setTextColor(
                        Color.GREEN
                );

                // Lock the solved field so the answer cannot be edited or resubmitted.
                answerInput1.setEnabled(
                        false
                );

            } else {

                // Wrong answers are shown in red but remain editable for another attempt.
                answerInput1.setTextColor(
                        Color.RED
                );
            }
        }

        /* FLAG 2: the same scoring/locking rules are applied to the second answer. */
        if (!answer2Correct) {

            String enteredAnswer2 =
                    answerInput2
                            .getText()
                            .toString()
                            .trim();

            String country2 =
                    FlagData.COUNTRY_NAMES[
                            currentFlags[1]
                            ];

            if (enteredAnswer2
                    .equalsIgnoreCase(
                            country2
                    )) {

                answer2Correct = true;

                roundScore++;

                answerInput2.setTextColor(
                        Color.GREEN
                );

                answerInput2.setEnabled(
                        false
                );

            } else {

                answerInput2.setTextColor(
                        Color.RED
                );
            }
        }

        /* FLAG 3: the same scoring/locking rules are applied to the third answer. */
        if (!answer3Correct) {

            String enteredAnswer3 =
                    answerInput3
                            .getText()
                            .toString()
                            .trim();

            String country3 =
                    FlagData.COUNTRY_NAMES[
                            currentFlags[2]
                            ];

            if (enteredAnswer3
                    .equalsIgnoreCase(
                            country3
                    )) {

                answer3Correct = true;

                roundScore++;

                answerInput3.setTextColor(
                        Color.GREEN
                );

                answerInput3.setEnabled(
                        false
                );

            } else {

                answerInput3.setTextColor(
                        Color.RED
                );
            }
        }

        updateScore();

        /* If all three fields are solved, finish immediately with a successful result. */
        if (answer1Correct &&
                answer2Correct &&
                answer3Correct) {

            resultText.setText(
                    "CORRECT!"
            );

            resultText.setTextColor(
                    Color.GREEN
            );

            finishRound();

            return;
        }

        /*
         * A submission is unsuccessful
         * if at least one country remains wrong.
         */
        // At least one answer is still wrong, so this submission uses one unsuccessful attempt.
        unsuccessfulAttempts++;

        attemptText.setText(
                "Unsuccessful attempts: "
                        + unsuccessfulAttempts
                        + " / 3"
        );

        // Three unsuccessful submissions end the round as a failure.
        if (unsuccessfulAttempts >= 3) {

            resultText.setText(
                    "WRONG!"
            );

            resultText.setTextColor(
                    Color.RED
            );

            // Reveal only the countries that were not successfully identified.
            showCorrectAnswers();

            finishRound();
        }
    }

    /** Ensures every answer field that is still active contains some text before scoring. */
    private boolean validateInputs() {

        // A blank first field is invalid only while that field is still unsolved.
        if (!answer1Correct &&
                answerInput1
                        .getText()
                        .toString()
                        .trim()
                        .isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter an answer for Flag 1",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        // Apply the same validation rule to the second field.
        if (!answer2Correct &&
                answerInput2
                        .getText()
                        .toString()
                        .trim()
                        .isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter an answer for Flag 2",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        // Apply the same validation rule to the third field.
        if (!answer3Correct &&
                answerInput3
                        .getText()
                        .toString()
                        .trim()
                        .isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter an answer for Flag 3",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        return true;
    }

    /** Reveals the correct country name in blue for every field the user failed to solve. */
    private void showCorrectAnswers() {

        if (!answer1Correct) {

            correctAnswer1.setText(
                    "Correct answer: "
                            + FlagData.COUNTRY_NAMES[
                            currentFlags[0]
                            ]
            );

            correctAnswer1.setTextColor(
                    Color.BLUE
            );
        }

        if (!answer2Correct) {

            correctAnswer2.setText(
                    "Correct answer: "
                            + FlagData.COUNTRY_NAMES[
                            currentFlags[1]
                            ]
            );

            correctAnswer2.setTextColor(
                    Color.BLUE
            );
        }

        if (!answer3Correct) {

            correctAnswer3.setText(
                    "Correct answer: "
                            + FlagData.COUNTRY_NAMES[
                            currentFlags[2]
                            ]
            );

            correctAnswer3.setTextColor(
                    Color.BLUE
            );
        }
    }

    /** Locks the finished round and converts the Submit button into a Next button. */
    private void finishRound() {

        roundFinished = true;

        // Disable every field because no further answers are allowed after the round ends.
        answerInput1.setEnabled(false);
        answerInput2.setEnabled(false);
        answerInput3.setEnabled(false);

        submitButton.setText("Next");
    }

    /** Writes the current round score (0-3) to the header. */
    private void updateScore() {

        scoreText.setText(
                "Score: " + roundScore
        );
    }

    /** Shows a confirmation dialog before returning to the game-mode menu. */
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

    /** Makes the Android system Back action use the same confirmation dialog. */
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