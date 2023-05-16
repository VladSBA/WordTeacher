package ru.vladsa.wordteacher;

import static ru.vladsa.wordteacher.LearningActivity.DICTIONARY_ID_;
import static ru.vladsa.wordteacher.LearningActivity.IMAGE_;
import static ru.vladsa.wordteacher.LearningActivity.MEANING_;
import static ru.vladsa.wordteacher.LearningActivity.WORD_;
import static ru.vladsa.wordteacher.LearningActivity.WORD_COUNT;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.vladsa.wordteacher.databinding.ActivityMainBinding;
import ru.vladsa.wordteacher.dictionaries.DictionaryAdapter;
import ru.vladsa.wordteacher.dictionaries.DictionaryData;
import ru.vladsa.wordteacher.dictionaries.DictionaryRepository;
import ru.vladsa.wordteacher.words.WordData;
import ru.vladsa.wordteacher.words.WordRepository;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = "Log_debug_1";

    private ActivityMainBinding binding;

    private volatile WordRepository wordRepository;
    private volatile DictionaryRepository dictionaryRepository;

    DictionaryAdapter.OnDictionaryClickListener clickListener = new DictionaryAdapter.OnDictionaryClickListener() {

        @Override
        public void onDictionaryClick(DictionaryAdapter.ViewHolder holder) {
            Intent intent = new Intent(MainActivity.this, DictionaryEditActivity.class);
            intent.putExtra(DictionaryEditActivity.DICTIONARY_ID, holder.getAdapterPosition());

            activityDictionaryEditLauncher.launch(intent);

        }
    };


    private final DictionaryAdapter adapter = new DictionaryAdapter(clickListener);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.start.setOnClickListener(view -> start());
        binding.addDictionary.setOnClickListener(view -> addDictionary());

        wordRepository = WordRepository.getInstance(this);
        dictionaryRepository = DictionaryRepository.getInstance(this);

        binding.container.setAdapter(adapter);

        adapter.setData(dictionaryRepository.getDictionaries());

        Log.d(LOG_TAG, "MainActivity has been created");

    }


    private void addDictionary() {
        Intent intent = new Intent(MainActivity.this, DictionaryEditActivity.class);
        intent.putExtra(DictionaryEditActivity.DICTIONARY_ID, -1);

        activityDictionaryEditLauncher.launch(intent);
    }

    private void start() {
        Log.d(LOG_TAG, "Starting learning");

        ArrayList<WordData> exampleWordData = new ArrayList<>();
        exampleWordData.add(new WordData ("Example 1", "Example meaning 1", null, 0));
        exampleWordData.add(new WordData ("Example 2", "Example meaning 2", null, 0));
        exampleWordData.add(new WordData ("Example 3", "Example meaning 3", null, 0));

        Intent intent = new Intent(MainActivity.this, LearningActivity.class);
        insertWordList(intent, exampleWordData);

        startActivity(intent);

        Log.d(LOG_TAG, "Learning started");

    }

    private void insertWordList(Intent intent, List<WordData> wordList) {
        intent.putExtra(WORD_COUNT, wordList.size());

        for (int i = 0; i < wordList.size(); i++) {
            WordData word = wordList.get(i);

            intent.putExtra(WORD_ + i, word.getWord());
            intent.putExtra(MEANING_ + i, word.getMeaning());
            intent.putExtra(IMAGE_ + i, word.getImage());
            //TODO: Put image to extra
            intent.putExtra(DICTIONARY_ID_ + i, word.getDictionaryID());
        }

    }

    private final ActivityResultLauncher<Intent> activityDictionaryEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent intent = result.getData();

                    if (intent != null) {
                        DictionaryData dictionary = new DictionaryData(
                                intent.getStringExtra(DictionaryEditActivity.DICTIONARY_NAME),
                                false);

                        for (int i = 0; i < intent.getIntExtra(DictionaryEditActivity.WORD_COUNT, 0); i++) {
                            WordData word = new WordData(
                                    intent.getStringExtra(DictionaryEditActivity.WORD_ID),
                                    intent.getStringExtra(DictionaryEditActivity.MEANING_ID),
                                    intent.getStringExtra(DictionaryEditActivity.IMAGE_ID),
                                    intent.getIntExtra(DictionaryEditActivity.DICTIONARY_ID, 0)
                            );

                            wordRepository.addWord(word);
                        }

                        dictionaryRepository.addDictionary(dictionary);
                    }
                }
            });

}