package ru.vladsa.wordteacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import ru.vladsa.wordteacher.databinding.ActivityLearningBinding;
import ru.vladsa.wordteacher.words.WordData;

public class LearningActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.LOG_TAG + " (LearningActivity)";

    public static final String WORD_COUNT = "Word_count";
    public static final String WORD_ = "Word_";
    public static final String MEANING_ = "Meaning_";
    public static final String IMAGE_ = "Image_";
    public static final String DICTIONARY_ID_ = "Dict_id_";
    public static final String MAX_RIGHT_WORDS = "MRW";
    public static final String WRONG_WORD_SHIFT = "WWS";

    private LinkedList<WordData> wordList = new LinkedList<>();
    private HashMap<WordData, Integer> wordMap = new HashMap<>();
    private final TreeSet<Integer> tempID = new TreeSet<>();

    private int activeWord = 0;
    private int maxRightWords;
    private int wrongWordShift;

    private ActivityLearningBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLearningBinding.inflate(getLayoutInflater());

        Intent intent = getIntent();
        wordList = getWordsFromExtra(intent);
        wordMap = getWordsFromList(wordList, 0);
        maxRightWords = intent.getIntExtra(MAX_RIGHT_WORDS, 2);
        wrongWordShift = intent.getIntExtra(WRONG_WORD_SHIFT, 2);


        displayWord(activeWord % wordList.size());

        setContentView(binding.getRoot());

        binding.next.setOnClickListener(v -> next());
        binding.right.setOnClickListener(v -> right());
        binding.wrong.setOnClickListener(v -> wrong());


        Log.d(LOG_TAG, String.format("LearningActivity created. Max right words = %d, wrong word shift = %d", maxRightWords, wrongWordShift));

    }

    private void displayWord(int position) {
        if (!wordList.isEmpty()) {
            WordData displayedWord = wordList.get(position);

            binding.word.setText(displayedWord.getWord());
            binding.meaning.setText(displayedWord.getMeaning());
            //TODO: Set image

            binding.image.setVisibility(View.GONE);
            binding.meaningAnnotation.setVisibility(View.GONE);
            binding.meaning.setVisibility(View.GONE);
            binding.right.setVisibility(View.GONE);
            binding.wrong.setVisibility(View.GONE);

            binding.next.setVisibility(View.VISIBLE);

            if (tempID.contains(position)) {
                tempID.remove(position);
                wordList.remove(position);
                activeWord--;
            }

            Log.d(LOG_TAG, String.format("Word %s at position %s has been displayed", displayedWord, position));
        } else {
            Log.d(LOG_TAG, String.format("Exit at word at position %d", position));
            finish();
        }
    }


    private HashMap<WordData, Integer> getWordsFromList(List<WordData> wordData, int value) {
        HashMap<WordData, Integer> wordHashMap = new HashMap<>();
        for (WordData word : wordData) {
            wordHashMap.put(word, value);
        }

        return wordHashMap;
    }

    private void next() {
        Log.d(LOG_TAG, "Next");

        binding.image.setVisibility(View.VISIBLE);
        binding.meaningAnnotation.setVisibility(View.VISIBLE);
        binding.meaning.setVisibility(View.VISIBLE);
        binding.right.setVisibility(View.VISIBLE);
        binding.wrong.setVisibility(View.VISIBLE);

        binding.next.setVisibility(View.GONE);

    }

    private void right() {
        Log.d(LOG_TAG, "Right");

        WordData word = wordList.get(activeWord % wordList.size());
        int value = wordMap.get(word);

        if (value >= maxRightWords) {
            Log.d(LOG_TAG, String.format("Remove %s at %d", word, activeWord));
            wordList.remove(activeWord == 0 ? 0 : wordList.size() % activeWord);
        } else {
            value++;
            wordMap.put(word, value);
        }

        activeWord++;
        displayWord(activeWord % wordList.size());

    }

    private void wrong() {
        Log.d(LOG_TAG, "Wrong");

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


    private LinkedList<WordData> getWordsFromExtra(Intent intent) {
        LinkedList<WordData> words = new LinkedList<>();

        int word_count = intent.getIntExtra(WORD_COUNT, 0);

        for (int i = 0; i < word_count; i++) {
            words.add(new WordData(
                    intent.getStringExtra(WORD_ + i),
                    intent.getStringExtra(MEANING_ + i),
                    intent.getStringExtra(IMAGE_ + i),
                    //TODO: get image from extra
                    intent.getLongExtra(DICTIONARY_ID_ + i, -1)
            ));
        }

        return words;
    }
}