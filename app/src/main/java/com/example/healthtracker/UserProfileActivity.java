package com.example.healthtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

public class UserProfileActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText ageEditText;
    private EditText weightEditText;
    private EditText heightEditText;
    private Button nextButton;
    private Button deleteDataButton;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "UserProfilePrefs";
    private static final long DATA_RETENTION_PERIOD = 30 * 24 * 60 * 60 * 1000L; // 30 days in milliseconds
    private boolean isEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize views
        initializeViews();

        // Check if we're in edit mode
        isEditing = getIntent().getBooleanExtra("isEditing", false);
        if (isEditing) {
            loadSavedData();
        }

        // Check data retention
        checkDataRetention();

        setupButtonListeners();
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        weightEditText = findViewById(R.id.weightEditText);
        heightEditText = findViewById(R.id.heightEditText);
        nextButton = findViewById(R.id.nextButton);
        deleteDataButton = findViewById(R.id.deleteDataButton);
    }

    private void setupButtonListeners() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // Save the data
                    saveData();
                    
                    // Create intent for next activity
                    Intent intent = new Intent(UserProfileActivity.this, ExerciseActivity.class);
                    
                    // Add user data to intent
                    intent.putExtra("name", nameEditText.getText().toString());
                    intent.putExtra("age", Integer.parseInt(ageEditText.getText().toString()));
                    intent.putExtra("weight", Double.parseDouble(weightEditText.getText().toString()));
                    intent.putExtra("height", Double.parseDouble(heightEditText.getText().toString()));
                    
                    // If editing, go back to SummaryActivity
                    if (isEditing) {
                        intent = new Intent(UserProfileActivity.this, SummaryActivity.class);
                        intent.putExtra("name", nameEditText.getText().toString());
                        intent.putExtra("age", Integer.parseInt(ageEditText.getText().toString()));
                        intent.putExtra("weight", Double.parseDouble(weightEditText.getText().toString()));
                        intent.putExtra("height", Double.parseDouble(heightEditText.getText().toString()));
                        
                        // Get and pass exercise and diet data
                        Intent previousIntent = getIntent();
                        intent.putExtra("exerciseType", previousIntent.getStringExtra("exerciseType"));
                        intent.putExtra("duration", previousIntent.getIntExtra("duration", 0));
                        intent.putExtra("caloriesBurned", previousIntent.getIntExtra("caloriesBurned", 0));
                        intent.putExtra("mealType", previousIntent.getStringExtra("mealType"));
                        intent.putExtra("foodItems", previousIntent.getStringExtra("foodItems"));
                        intent.putExtra("caloriesConsumed", previousIntent.getIntExtra("caloriesConsumed", 0));
                    }
                    
                    // Start next activity
                    startActivity(intent);
                    finish();
                }
            }
        });

        deleteDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Delete Data")
            .setMessage("Are you sure you want to delete all your data? This action cannot be undone.")
            .setPositiveButton("Delete", (dialog, which) -> {
                deleteUserData();
                clearInputFields();
                Toast.makeText(this, "All data has been deleted", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void loadSavedData() {
        try {
            String name = SecurityUtils.decryptAndGetData(this, "name");
            String age = SecurityUtils.decryptAndGetData(this, "age");
            String weight = SecurityUtils.decryptAndGetData(this, "weight");
            String height = SecurityUtils.decryptAndGetData(this, "height");

            if (name != null) nameEditText.setText(name);
            if (age != null) ageEditText.setText(age);
            if (weight != null) weightEditText.setText(weight);
            if (height != null) heightEditText.setText(height);
        } catch (Exception e) {
            showError("Error loading saved data");
        }
    }

    private void saveData() {
        try {
            SecurityUtils.encryptAndSaveData(this, "name", nameEditText.getText().toString());
            SecurityUtils.encryptAndSaveData(this, "age", ageEditText.getText().toString());
            SecurityUtils.encryptAndSaveData(this, "weight", weightEditText.getText().toString());
            SecurityUtils.encryptAndSaveData(this, "height", heightEditText.getText().toString());
            
            // Update last modified timestamp
            sharedPreferences.edit().putLong("last_update", System.currentTimeMillis()).apply();
        } catch (Exception e) {
            showError("Error saving data");
        }
    }

    private void deleteUserData() {
        SecurityUtils.deleteAllData(this);
        sharedPreferences.edit().clear().apply();
    }

    private void clearInputFields() {
        nameEditText.setText("");
        ageEditText.setText("");
        weightEditText.setText("");
        heightEditText.setText("");
    }

    private void checkDataRetention() {
        long lastUpdate = sharedPreferences.getLong("last_update", 0);
        if (lastUpdate > 0 && System.currentTimeMillis() - lastUpdate > DATA_RETENTION_PERIOD) {
            showDataRetentionDialog();
        }
    }

    private void showDataRetentionDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Data Update Required")
            .setMessage("Your data is older than 30 days. Please update your information to ensure accuracy.")
            .setPositiveButton("Update Now", null)
            .setNegativeButton("Later", null)
            .show();
    }

    private boolean validateInputs() {
        if (nameEditText.getText().toString().trim().isEmpty()) {
            showError("Please enter your name");
            return false;
        }
        
        try {
            int age = Integer.parseInt(ageEditText.getText().toString());
            if (age <= 0 || age > 120) {
                showError("Please enter a valid age (1-120)");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid age");
            return false;
        }

        try {
            double weight = Double.parseDouble(weightEditText.getText().toString());
            if (weight <= 0 || weight > 500) {
                showError("Please enter a valid weight (1-500 kg)");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid weight");
            return false;
        }

        try {
            double height = Double.parseDouble(heightEditText.getText().toString());
            if (height <= 0 || height > 300) {
                showError("Please enter a valid height (1-300 cm)");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid height");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
} 