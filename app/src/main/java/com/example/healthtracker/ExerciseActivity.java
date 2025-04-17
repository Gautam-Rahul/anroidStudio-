package com.example.healthtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ExerciseActivity extends AppCompatActivity {
    private EditText exerciseTypeEditText;
    private EditText durationEditText;
    private EditText caloriesBurnedEditText;
    private Button nextButton;
    private boolean isEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        // Initialize views
        initializeViews();

        // Check if we're in edit mode
        isEditing = getIntent().getBooleanExtra("isEditing", false);
        if (isEditing) {
            loadSavedData();
        }

        setupButtonListener();
    }

    private void initializeViews() {
        exerciseTypeEditText = findViewById(R.id.exerciseTypeEditText);
        durationEditText = findViewById(R.id.durationEditText);
        caloriesBurnedEditText = findViewById(R.id.caloriesBurnedEditText);
        nextButton = findViewById(R.id.nextButton);
    }

    private void loadSavedData() {
        try {
            String exerciseType = SecurityUtils.decryptAndGetData(this, "exerciseType");
            String duration = SecurityUtils.decryptAndGetData(this, "duration");
            String calories = SecurityUtils.decryptAndGetData(this, "caloriesBurned");

            if (exerciseType != null) exerciseTypeEditText.setText(exerciseType);
            if (duration != null) durationEditText.setText(duration);
            if (calories != null) caloriesBurnedEditText.setText(calories);
        } catch (Exception e) {
            showError("Error loading saved data");
        }
    }

    private void setupButtonListener() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // Save the data
                    saveData();
                    
                    // Create intent based on whether we're editing
                    Intent intent;
                    if (isEditing) {
                        intent = new Intent(ExerciseActivity.this, SummaryActivity.class);
                    } else {
                        intent = new Intent(ExerciseActivity.this, DietActivity.class);
                    }
                    
                    // Get data from previous activities
                    Intent previousIntent = getIntent();
                    intent.putExtra("name", previousIntent.getStringExtra("name"));
                    intent.putExtra("age", previousIntent.getIntExtra("age", 0));
                    intent.putExtra("weight", previousIntent.getDoubleExtra("weight", 0.0));
                    intent.putExtra("height", previousIntent.getDoubleExtra("height", 0.0));
                    
                    // Add exercise data
                    intent.putExtra("exerciseType", exerciseTypeEditText.getText().toString());
                    intent.putExtra("duration", Integer.parseInt(durationEditText.getText().toString()));
                    intent.putExtra("caloriesBurned", Integer.parseInt(caloriesBurnedEditText.getText().toString()));
                    
                    // If editing, pass diet data back to Summary
                    if (isEditing) {
                        intent.putExtra("mealType", previousIntent.getStringExtra("mealType"));
                        intent.putExtra("foodItems", previousIntent.getStringExtra("foodItems"));
                        intent.putExtra("caloriesConsumed", previousIntent.getIntExtra("caloriesConsumed", 0));
                        intent.putExtra("waterIntake", previousIntent.getIntExtra("waterIntake", 0));
                    }
                    
                    // Start next activity
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void saveData() {
        try {
            SecurityUtils.encryptAndSaveData(this, "exerciseType", exerciseTypeEditText.getText().toString());
            SecurityUtils.encryptAndSaveData(this, "duration", durationEditText.getText().toString());
            SecurityUtils.encryptAndSaveData(this, "caloriesBurned", caloriesBurnedEditText.getText().toString());
        } catch (Exception e) {
            showError("Error saving data");
        }
    }

    private boolean validateInputs() {
        if (exerciseTypeEditText.getText().toString().trim().isEmpty()) {
            showError("Please enter exercise type");
            return false;
        }
        
        try {
            int duration = Integer.parseInt(durationEditText.getText().toString());
            if (duration <= 0 || duration > 480) { // Assuming max 8 hours
                showError("Please enter a valid duration (1-480 minutes)");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid duration");
            return false;
        }

        try {
            int calories = Integer.parseInt(caloriesBurnedEditText.getText().toString());
            if (calories <= 0 || calories > 5000) {
                showError("Please enter a valid calorie amount (1-5000)");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid calorie amount");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
} 