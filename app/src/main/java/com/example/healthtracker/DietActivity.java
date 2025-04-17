package com.example.healthtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DietActivity extends AppCompatActivity {
    private EditText mealTypeEditText;
    private EditText foodItemsEditText;
    private EditText caloriesConsumedEditText;
    private EditText waterIntakeEditText;
    private Button nextButton;
    private boolean isEditing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

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
        mealTypeEditText = findViewById(R.id.mealTypeEditText);
        foodItemsEditText = findViewById(R.id.foodItemsEditText);
        caloriesConsumedEditText = findViewById(R.id.caloriesConsumedEditText);
        waterIntakeEditText = findViewById(R.id.waterIntakeEditText);
        nextButton = findViewById(R.id.nextButton);
    }

    private void loadSavedData() {
        try {
            String mealType = SecurityUtils.decryptAndGetData(this, "mealType");
            String foodItems = SecurityUtils.decryptAndGetData(this, "foodItems");
            String calories = SecurityUtils.decryptAndGetData(this, "caloriesConsumed");
            String waterIntake = SecurityUtils.decryptAndGetData(this, "waterIntake");

            if (mealType != null) mealTypeEditText.setText(mealType);
            if (foodItems != null) foodItemsEditText.setText(foodItems);
            if (calories != null) caloriesConsumedEditText.setText(calories);
            if (waterIntake != null) waterIntakeEditText.setText(waterIntake);
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
                    
                    // Create intent for SummaryActivity
                    Intent intent = new Intent(DietActivity.this, SummaryActivity.class);
                    
                    // Get data from previous activities
                    Intent previousIntent = getIntent();
                    intent.putExtra("name", previousIntent.getStringExtra("name"));
                    intent.putExtra("age", previousIntent.getIntExtra("age", 0));
                    intent.putExtra("weight", previousIntent.getDoubleExtra("weight", 0.0));
                    intent.putExtra("height", previousIntent.getDoubleExtra("height", 0.0));
                    intent.putExtra("exerciseType", previousIntent.getStringExtra("exerciseType"));
                    intent.putExtra("duration", previousIntent.getIntExtra("duration", 0));
                    intent.putExtra("caloriesBurned", previousIntent.getIntExtra("caloriesBurned", 0));
                    
                    // Add diet data
                    intent.putExtra("mealType", mealTypeEditText.getText().toString());
                    intent.putExtra("foodItems", foodItemsEditText.getText().toString());
                    intent.putExtra("caloriesConsumed", Integer.parseInt(caloriesConsumedEditText.getText().toString()));
                    intent.putExtra("waterIntake", Integer.parseInt(waterIntakeEditText.getText().toString()));
                    
                    // Start SummaryActivity
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void saveData() {
        try {
            SecurityUtils.encryptAndSaveData(this, "mealType", mealTypeEditText.getText().toString());
            SecurityUtils.encryptAndSaveData(this, "foodItems", foodItemsEditText.getText().toString());
            SecurityUtils.encryptAndSaveData(this, "caloriesConsumed", caloriesConsumedEditText.getText().toString());
            SecurityUtils.encryptAndSaveData(this, "waterIntake", waterIntakeEditText.getText().toString());
        } catch (Exception e) {
            showError("Error saving data");
        }
    }

    private boolean validateInputs() {
        if (mealTypeEditText.getText().toString().trim().isEmpty()) {
            showError("Please enter meal type");
            return false;
        }
        
        if (foodItemsEditText.getText().toString().trim().isEmpty()) {
            showError("Please enter food items");
            return false;
        }
        
        try {
            int calories = Integer.parseInt(caloriesConsumedEditText.getText().toString());
            if (calories <= 0 || calories > 5000) {
                showError("Please enter a valid calorie amount (1-5000)");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid calorie amount");
            return false;
        }

        try {
            int waterIntake = Integer.parseInt(waterIntakeEditText.getText().toString());
            if (waterIntake < 0 || waterIntake > 5000) {
                showError("Please enter a valid water intake amount (0-5000ml)");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid water intake amount");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
} 