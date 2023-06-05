package ru.vladsa.wordteacher;

import static ru.vladsa.wordteacher.DictionaryEditActivity.GETTING_IMAGE;
import static ru.vladsa.wordteacher.MainActivity.WORDS;
import static ru.vladsa.wordteacher.SettingsActivity.APP_PREFERENCES;
import static ru.vladsa.wordteacher.SettingsActivity.RESULT_KEY;
import static ru.vladsa.wordteacher.SettingsActivity.RIGHT_WORDS_TO_MEMORIZED_KEY;
import static ru.vladsa.wordteacher.SettingsActivity.TIMER_KEY;
import static ru.vladsa.wordteacher.SettingsActivity.WRONG_WORD_MOVE_KEY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import ru.vladsa.wordteacher.databinding.ActivityLearningBinding;
import ru.vladsa.wordteacher.words.WordData;

public class LearningActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.LOG_TAG + " (LearningActivity)";

    private static final int MIN_SCROLL_DISTANCE = 5;

    private LinkedList<WordData> wordList = new LinkedList<>();
    private HashMap<WordData, Integer> wordMap = new HashMap<>();
    private final TreeSet<Integer> tempID = new TreeSet<>();

    private Timer timer;
    private boolean buttonsHidden = false;

    private int rightAnswers = 0;
    private int wrongAnswers = 0;

    private int activeWord = 0;
    private int maxRightWords;
    private int wrongWordShift;

    private boolean timerEnabled;
    private boolean resultEnabled;
    private int time;
    private String timerText;

    private boolean answerHasShowed = false;
    private boolean scrollable;

    private ActivityLearningBinding binding;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Creating LearningActivity...");

        super.onCreate(savedInstanceState);
        binding = ActivityLearningBinding.inflate(getLayoutInflater());

        binding.resultContainer.setVisibility(View.INVISIBLE);

        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        maxRightWords = preferences.getInt(RIGHT_WORDS_TO_MEMORIZED_KEY, 2) - 1;
        wrongWordShift = preferences.getInt(WRONG_WORD_MOVE_KEY, 1);
        timerEnabled = preferences.getBoolean(TIMER_KEY, false);
        resultEnabled = preferences.getBoolean(RESULT_KEY, true);

        Intent intent = getIntent();
        wordList = getWordsFromExtra(intent);
        wordMap = getWordsFromList(wordList, 0);

        if (wordList.isEmpty()) {
            endLearning();
        }

        setTimer();

        displayWord(activeWord % wordList.size());

        setContentView(binding.getRoot());

        binding.next.setOnClickListener(v -> next());
        binding.right.setOnClickListener(v -> right());
        binding.wrong.setOnClickListener(v -> wrong());

        binding.mainContainer.setOnClickListener(v -> hideButtons());
        binding.wordAnnotation.setOnClickListener(v -> hideButtons());
        binding.word.setOnClickListener(v -> hideButtons());
        binding.image.setOnClickListener(v -> hideButtons());
        binding.meaningAnnotation.setOnClickListener(v -> hideButtons());
        binding.meaning.setOnClickListener(v -> hideButtons());

        binding.mainContainer.setOnScrollChangeListener(onScroll);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Log.d(LOG_TAG, String.format("LearningActivity created. Max right words = %d, wrong word shift = %d", maxRightWords, wrongWordShift));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final View.OnScrollChangeListener onScroll = (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
        if (answerHasShowed) {
            if (scrollY - oldScrollY >= MIN_SCROLL_DISTANCE) {
                Log.d(LOG_TAG + "_Scroll", "Scroll down");
                hide();
            } else if (oldScrollY - scrollY >= MIN_SCROLL_DISTANCE) {
                Log.d(LOG_TAG + "_Scroll", "Scroll up");
                show();
            }
        }

    };

    private void hideButtons() {
            if (!scrollable && answerHasShowed) {
                if (buttonsHidden) {
                    show();
                    buttonsHidden = false;
                } else {
                    hide();
                    buttonsHidden = true;
                }
            }
    }

    private void hide() {
        ConstraintLayout.LayoutParams rightLayoutParams =
                (ConstraintLayout.LayoutParams) binding.right.getLayoutParams();
        int rightBottomMargin = rightLayoutParams.bottomMargin;

        binding.right.animate().translationY(binding.right.getHeight() + rightBottomMargin)
                .setInterpolator(new LinearInterpolator()).start();

        ConstraintLayout.LayoutParams wrongLayoutParams =
                (ConstraintLayout.LayoutParams) binding.wrong.getLayoutParams();
        int wrongBottomMargin = wrongLayoutParams.bottomMargin;

        binding.wrong.animate().translationY(binding.wrong.getHeight() + wrongBottomMargin)
                .setInterpolator(new LinearInterpolator()).start();
    }

    private void show() {
        binding.right.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
        binding.wrong.animate().translationY(0).setInterpolator(new LinearInterpolator()).start();
    }

    private void endLearning() {
        Log.d(LOG_TAG, "Ending learning...");

        binding.right.hide();
        binding.wrong.hide();
        binding.next.hide();

        if (resultEnabled) {
            binding.resultContainer.setVisibility(View.VISIBLE);

            timer.cancel();

            FragmentManager fm = getSupportFragmentManager();
            Fragment resultFragment = ResultFragment.newInstance(time, wordMap.size(), rightAnswers, wrongAnswers);

            fm.beginTransaction()
                    .replace(R.id.result_container, resultFragment)
                    .commit();

        } else {
            finish();

            Log.d(LOG_TAG, "LearningActivity finished.");
        }


        Log.d(LOG_TAG, "Learning ended.");
    }

    private void setTimer() {
        time = 0;

        timerText = this.getString(R.string.time) + ": ";

        String timerTextNow = timerText + "0";
        binding.timer.setText(timerTextNow);

        if (timerEnabled) {
            binding.timer.setVisibility(View.VISIBLE);
        } else {
            binding.timer.setVisibility(View.GONE);
        }

        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        time++;

                        updateTimer();
                    }
                },
                1000,
                1000);

    }

    private void updateTimer() {
        String timerTextNow = timerText + time;

        binding.timer.setText(timerTextNow);
    }

    private void displayWord(int position) {
        WordData displayedWord = wordList.get(position);

        Log.d(LOG_TAG, String.format("Displaying word %s...", displayedWord));

        answerHasShowed = false;

        binding.word.setText(displayedWord.getWord());
        binding.meaning.setText(displayedWord.getMeaning());
        binding.image.setImageBitmap(getImage(displayedWord.getImage()));

        binding.image.setVisibility(View.INVISIBLE);
        binding.meaningAnnotation.setVisibility(View.INVISIBLE);
        binding.meaning.setVisibility(View.INVISIBLE);
        binding.right.setVisibility(View.INVISIBLE);
        binding.wrong.setVisibility(View.INVISIBLE);

        binding.next.setVisibility(View.VISIBLE);

        if (tempID.contains(position)) {
            tempID.remove(position);
            wordList.remove(position);
            activeWord--;
        }

        Log.d(LOG_TAG, String.format("Word %s at position %s has been displayed", displayedWord, position));

    }

    private void next() {
        Log.d(LOG_TAG, "Next");

        answerHasShowed = true;

        binding.image.setVisibility(View.VISIBLE);
        binding.meaningAnnotation.setVisibility(View.VISIBLE);
        binding.meaning.setVisibility(View.VISIBLE);
        binding.right.setVisibility(View.VISIBLE);
        binding.wrong.setVisibility(View.VISIBLE);

        binding.next.setVisibility(View.GONE);

        scrollable = binding.meaning.getBottom() > binding.getRoot().getBottom();

    }

    private Bitmap getImage(String image) {
        Log.d(LOG_TAG, "Getting image...");

        if (image != null && !image.isEmpty() && !image.equals("null") & !image.equals(GETTING_IMAGE)) {
            Bitmap bitmap;

            try {
                FileInputStream fis = new FileInputStream(image);
                bitmap = BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            return bitmap;
        }
        return null;
    }

    private HashMap<WordData, Integer> getWordsFromList(List<WordData> wordData, int value) {
        HashMap<WordData, Integer> wordHashMap = new HashMap<>();

        for (WordData word : wordData) {
            wordHashMap.put(word, value);
        }

        return wordHashMap;
    }

    private void right() {
        Log.d(LOG_TAG, "Right");

        if (wordList.size() == 0) {
            Log.d(LOG_TAG, "WordList is empty. Ending learning...");

            endLearning();
        } else {
            rightAnswers++;

            WordData word = wordList.get(activeWord % wordList.size());

            int value = wordMap.get(word);

            if (value >= maxRightWords) {
                Log.d(LOG_TAG, String.format("Remove %s at %d", word, activeWord));
                int oldSize = wordList.size();
                wordList.remove(activeWord % wordList.size());
                int count = activeWord / oldSize;
                activeWord = count * wordList.size() + activeWord % oldSize - 1;
            } else {
                value++;
                wordMap.put(word, value);
            }

            activeWord++;

            if (wordList.size() == 0) {
                Log.d(LOG_TAG, "Exit because wordList is null...");
                endLearning();
            } else {
                displayWord(activeWord % wordList.size());
            }

        }

    }

    private void wrong() {
        Log.d(LOG_TAG, "Wrong");
        wrongAnswers++;

        WordData word = wordList.get(activeWord % wordList.size());
        int value = wordMap.get(word);

        if (value > 0) {
            value--;
            wordMap.put(word, value);
        }

        tempID.add(activeWord + wrongWordShift);
        wordList.add(activeWord + wrongWordShift, word);

        activeWord++;
        displayWord(activeWord % wordList.size());

    }

    private LinkedList<WordData> getWordsFromExtra(Intent data) {
        Log.d(LOG_TAG, "Getting words from extra...");

        LinkedList<WordData> words = new LinkedList<>();
        words.addAll((ArrayList<WordData>) data.getSerializableExtra(WORDS));

        return words;
    }
}