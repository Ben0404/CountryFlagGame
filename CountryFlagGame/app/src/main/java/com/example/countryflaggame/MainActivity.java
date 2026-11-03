package com.example.countryflaggame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Main entry screen for the Country Flag Game.
 * Provides access to the About dialog and the game-mode menu.
 */
public class MainActivity extends AppCompatActivity {

    // Buttons displayed on the main menu.
    private Button aboutButton;
    private Button newGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the main-menu XML layout for this activity.
        setContentView(R.layout.activity_main);

        // Connect the Java button variables to their matching XML views.
        aboutButton = findViewById(R.id.aboutButton);
        newGameButton = findViewById(R.id.newGameButton);

        // Open the About dialog when the About button is pressed.
        aboutButton.setOnClickListener(v -> showAboutDialog());

        // Open the game-mode selection screen when New Country Game is pressed.
        newGameButton.setOnClickListener(v -> {
            // Create an explicit Intent that targets GameMenuActivity.
            Intent intent = new Intent(
                    MainActivity.this,
                    GameMenuActivity.class
            );

            // Start the selected activity.
            startActivity(intent);
        });
    }

    /**
     * Builds and displays the About dialog containing the student details
     * and the declaration required by the coursework specification.
     */
    private void showAboutDialog() {

        // Store all About-dialog text in one message string.
        String message =
                "Name: Ben McMahon\n\n" +
                        "Student ID: B00930201\n\n" +
                        "I confirm that I understand what plagiarism is and have " +
                        "read the University’s policy on plagiarism and understand " +
                        "the definition of plagiarism. The work that I have submitted " +
                        "is entirely my own. Any work from other authors is duly " +
                        "referenced and acknowledged. I understand that I may face " +
                        "sanctions in accordance with the policies and procedures " +
                        "of the University.";

        // Build the dialog, add an OK button, and display it.
        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}