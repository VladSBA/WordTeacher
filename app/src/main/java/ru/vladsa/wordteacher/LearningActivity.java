package ru.vladsa.wordteacher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ru.vladsa.wordteacher.databinding.ActivityLearningBinding;

public class LearningActivity extends AppCompatActivity {
    private ActivityLearningBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLearningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




    }
}