package com.example.healthtracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SummaryActivity extends AppCompatActivity {
    private TextView tvName, tvAge, tvWeight, tvHeight;
    private TextView tvExerciseType, tvDuration, tvCalories;
    private TextView tvMealType, tvFoodItems, tvCaloriesConsumed, tvWaterIntake;
    private TextView tvNetCalories, tvCalorieBalance;
    private Button btnEditProfile, btnEditExercise, btnEditDiet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        // Initialize TextViews
        initializeTextViews();
        
        // Initialize Buttons
        initializeButtons();
        
        // Load and display data
        loadAndDisplayData();
        
        // Setup button listeners
        setupButtonListeners();
    }

    private void initializeTextViews() {
        // Personal Information
        tvName = findViewById(R.id.tvName);
        tvAge = findViewById(R.id.tvAge);
        tvWeight = findViewById(R.id.tvWeight);
        tvHeight = findViewById(R.id.tvHeight);
        
        // Exercise Details
        tvExerciseType = findViewById(R.id.tvExerciseType);
        tvDuration = findViewById(R.id.tvDuration);
        tvCalories = findViewById(R.id.tvCalories);
        
        // Diet Details
        tvMealType = findViewById(R.id.tvMealType);
        tvFoodItems = findViewById(R.id.tvFoodItems);
        tvCaloriesConsumed = findViewById(R.id.tvCaloriesConsumed);
        tvWaterIntake = findViewById(R.id.tvWaterIntake);
        
        // Summary Details
        tvNetCalories = findViewById(R.id.tvNetCalories);
        tvCalorieBalance = findViewById(R.id.tvCalorieBalance);
    }

    private void initializeButtons() {
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditExercise = findViewById(R.id.btnEditExercise);
        btnEditDiet = findViewById(R.id.btnEditDiet);
    }

    private void loadAndDisplayData() {
        // Get data from intent
        Intent intent = getIntent();
        
        // Personal Information
        String name = intent.getStringExtra("name");
        int age = intent.getIntExtra("age", 0);
        double weight = intent.getDoubleExtra("weight", 0.0);
        double height = intent.getDoubleExtra("height", 0.0);
        
        // Exercise Details
        String exerciseType = intent.getStringExtra("exerciseType");
        int duration = intent.getIntExtra("duration", 0);
        int caloriesBurned = intent.getIntExtra("caloriesBurned", 0);
        
        // Diet Details
        displayDietDetails(intent);
        
        // Calculate net calories
        int netCalories = caloriesBurned - intent.getIntExtra("caloriesConsumed", 0);
        String calorieBalance = netCalories > 0 ? "Calorie Deficit" : "Calorie Surplus";
        
        // Display Personal Information
        if (name != null) tvName.setText("Name: " + name);
        if (age > 0) tvAge.setText("Age: " + age + " years");
        if (weight > 0) tvWeight.setText("Weight: " + weight + " kg");
        if (height > 0) tvHeight.setText("Height: " + height + " cm");
        
        // Display Exercise Details
        if (exerciseType != null) tvExerciseType.setText("Exercise Type: " + exerciseType);
        if (duration > 0) tvDuration.setText("Duration: " + duration + " minutes");
        if (caloriesBurned > 0) tvCalories.setText("Calories Burned: " + caloriesBurned + " kcal");
        
        // Display Summary
        tvNetCalories.setText("Net Calories: " + netCalories + " kcal");
        tvCalorieBalance.setText("Status: " + calorieBalance);
    }

    private void displayDietDetails(Intent intent) {
        if (intent.hasExtra("mealType")) {
            tvMealType.setText(intent.getStringExtra("mealType"));
        }
        if (intent.hasExtra("foodItems")) {
            tvFoodItems.setText(intent.getStringExtra("foodItems"));
        }
        if (intent.hasExtra("caloriesConsumed")) {
            tvCaloriesConsumed.setText(String.format("%d calories", intent.getIntExtra("caloriesConsumed", 0)));
        }
        if (intent.hasExtra("waterIntake")) {
            tvWaterIntake.setText(String.format("%d ml", intent.getIntExtra("waterIntake", 0)));
        }
    }

    private void setupButtonListeners() {
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this, UserProfileActivity.class);
                intent.putExtra("isEditing", true);
                // Pass all current data
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
            }
        });

        btnEditExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this, ExerciseActivity.class);
                intent.putExtra("isEditing", true);
                // Pass all current data
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
            }
        });

        btnEditDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this, DietActivity.class);
                intent.putExtra("isEditing", true);
                // Pass all current data
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
            }
        });
    }
} 