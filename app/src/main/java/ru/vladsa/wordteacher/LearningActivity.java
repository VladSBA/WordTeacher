package ru.vladsa.wordteacher;

import static ru.vladsa.wordteacher.MainActivity.WORDS;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import ru.vladsa.wordteacher.databinding.ActivityLearningBinding;
import ru.vladsa.wordteacher.words.WordData;

public class LearningActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.LOG_TAG + " (LearningActivity)";

    public static final String MAX_RIGHT_WORDS = "MRW";
    public static final String WRONG_WORD_SHIFT = "WWS";

    private LinkedList<WordData> wordList = new LinkedList<>();
    private HashMap<WordData, Integer> wordMap = new HashMap<>();
    private final TreeSet<Integer> tempID = new TreeSet<>();

    private int rightAnswers = 0;
    private int wrongAnswers = 0;

    private int activeWord = 0;
    private int maxRightWords;
    private int wrongWordShift;

    private ActivityLearningBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Creating LearningActivity...");

        super.onCreate(savedInstanceState);
        binding = ActivityLearningBinding.inflate(getLayoutInflater());

        Intent intent = getIntent();
        wordList = getWordsFromExtra(intent);
        wordMap = getWordsFromList(wordList, 0);
        maxRightWords = intent.getIntExtra(MAX_RIGHT_WORDS, 2) - 1;
        wrongWordShift = intent.getIntExtra(WRONG_WORD_SHIFT, 2);

        if (wordList.isEmpty()) {
            Log.d(LOG_TAG, "Word list empty. Finishing activity...");
            finish();
            Log.d(LOG_TAG, "LearningActivity had finished");
        }

        displayWord(activeWord % wordList.size());

        setContentView(binding.getRoot());

        binding.next.setOnClickListener(v -> next());
        binding.right.setOnClickListener(v -> right());
        binding.wrong.setOnClickListener(v -> wrong());


        Log.d(LOG_TAG, String.format("LearningActivity created. Max right words = %d, wrong word shift = %d", maxRightWords, wrongWordShift));

    }

    private void displayWord(int position) {
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
            Log.d(LOG_TAG, "Exit because wordList is null");
            finish();
        } else {
            displayWord(activeWord % wordList.size());
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

        LinkedList<WordData> words = new LinkedList<>((ArrayList<WordData>) data.getSerializableExtra(WORDS));


        /*for (int i = 0; i < data.getIntExtra(WORD_COUNT, 0); i++) {
            words.add(new WordData(
                    data.getStringExtra(WORD_ + i),
                    data.getStringExtra(MEANING_ + i),
                    data.getStringExtra(IMAGE_ + i),
                    data.getLongExtra(DICTIONARY_ID, 0)
            ));
            //TODO: Get image
        }
*/
        return words;
    }
}