package ru.vladsa.wordteacher;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import ru.vladsa.wordteacher.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.LOG_TAG + " (SetA)";

    public static final String APP_PREFERENCES = "app_preferences";
    private SharedPreferences preferences;

    public static final String TIMER_KEY = "timer";
    public static final String WRONG_WORD_MOVE_KEY = "WWM";
    public static final String RIGHT_WORDS_TO_MEMORIZED_KEY = "RWTM";
    private boolean timer;
    private int wrongWordMove;
    private int rightWordsToMemorized;

    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.timerSwitch.setOnClickListener(v -> saveSettings());
        binding.timerSwitch.setOnFocusChangeListener((v, hasFocus) -> saveSettings());
        binding.timerSwitch.setOnFocusChangeListener((v, hasFocus) -> saveSettings());

        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        timer = preferences.getBoolean(TIMER_KEY, false);
        wrongWordMove = preferences.getInt(WRONG_WORD_MOVE_KEY, 1);
        rightWordsToMemorized = preferences.getInt(RIGHT_WORDS_TO_MEMORIZED_KEY, 2);

        binding.timerSwitch.setChecked(timer);
        binding.wordRightValue.setText(String.valueOf(rightWordsToMemorized));
        binding.wordMoveValue.setText(String.valueOf(wrongWordMove));

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Log.d(LOG_TAG, "SettingsActivity has created.");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveSettings();
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void saveSettings() {
        Log.d(LOG_TAG, "Saving settings...");

        timer = binding.timerSwitch.isChecked();

        String wrong = binding.wordMoveValue.getText().toString();
        String right = binding.wordRightValue.getText().toString();

        if (!wrong.isEmpty()) {
            wrongWordMove = Integer.parseInt(binding.wordMoveValue.getText().toString());
        } if (!right.isEmpty()) {
            rightWordsToMemorized = Integer.parseInt(binding.wordRightValue.getText().toString());
        }

        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(TIMER_KEY, timer);
        editor.putInt(WRONG_WORD_MOVE_KEY, wrongWordMove);
        editor.putInt(RIGHT_WORDS_TO_MEMORIZED_KEY, rightWordsToMemorized);
        editor.apply();

        Log.d(LOG_TAG, String.format("Settings timer = %b, WWM = %d, RWTM = %d", timer, wrongWordMove, rightWordsToMemorized));
    }
}