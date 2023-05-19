package ru.vladsa.wordteacher;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import ru.vladsa.wordteacher.databinding.ActivityMainBinding;
import ru.vladsa.wordteacher.dictionaries.DictionaryAdapter;
import ru.vladsa.wordteacher.dictionaries.DictionaryData;
import ru.vladsa.wordteacher.dictionaries.DictionaryRepository;
import ru.vladsa.wordteacher.words.WordData;
import ru.vladsa.wordteacher.words.WordRepository;

public class MainActivity extends AppCompatActivity {
    public static final String WORD_COUNT = "Word_count";
    public static final String DICTIONARY_ID = "Dictionary_id";
    public static final String DICTIONARY = "Dictionary";
    public static final String WORDS = "Words";
    public static final String IS_NEW_DICTIONARY = "Is_new_dict";

    public static final String LOG_TAG = "Log_debug_1";

    private ActivityMainBinding binding;

    private volatile WordRepository wordRepository;
    private volatile DictionaryRepository dictionaryRepository;

    DictionaryAdapter.Listener listener = new DictionaryAdapter.Listener() {

        @Override
        public void onPositionClicked(int position) {
            Log.d(LOG_TAG, "Starting dictionary Editing at " + position);

            Intent intent = new Intent(MainActivity.this, DictionaryEditActivity.class);

            intent.putExtra(IS_NEW_DICTIONARY, false);
            intent.putExtra(DICTIONARY, dictionaryRepository.getDictionaries().get(position));
            putWordListToExtra(intent, wordRepository.getDictionaryWords(
                    dictionaryRepository.getDictionaries().get(position).getId()));

            activityDictionaryEditLauncher.launch(intent);
        }

        @Override
        public void onLongClicked(int position) {

        }
    };


    private final DictionaryAdapter adapter = new DictionaryAdapter(listener);


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

    public int getWordCount(long dictionaryID) {
        List<WordData> data = wordRepository.getDictionaryWords(dictionaryID);
        return data.size();
    }


    private void addDictionary() {
        Log.d(LOG_TAG, "Adding dictionary...");
        Intent intent = new Intent(MainActivity.this, DictionaryEditActivity.class);
        intent.putExtra(IS_NEW_DICTIONARY, true);
        intent.putExtra(DICTIONARY_ID, Long.valueOf(dictionaryRepository.getAllDictionariesCount() + 1));
        intent.putExtra(WORDS, new ArrayList<WordData>());

        activityDictionaryEditLauncher.launch(intent);
    }

    private void start() {
        Log.d(LOG_TAG, "Starting learning...");

        ArrayList<DictionaryData> dictionaries = (ArrayList<DictionaryData>) adapter.getDictionaries();

        for (int i = 0; i < dictionaries.size(); i++) {
            if (dictionaries.get(i).getValue() != dictionaryRepository.getDictionaries().get(i).getValue()) {
                dictionaryRepository.updateDictionary(dictionaries.get(i));
            }
        }

        ArrayList<WordData> words = new ArrayList<>();

        for (DictionaryData dictionary : dictionaries) {
            if (dictionary.getValue()) {
                words.addAll(wordRepository.getDictionaryWords(dictionary.getId()));
            }
        }


        if (words.isEmpty()) {
            Toast.makeText(this, R.string.no_dictionary_selected, Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "No dictionary selected. LearningActivity not started.");
        } else {
            Intent intent = new Intent(MainActivity.this, LearningActivity.class);
            putWordListToExtra(intent, words);

            startActivity(intent);
            Log.d(LOG_TAG, "Learning started");
        }



    }

    private void putWordListToExtra(Intent intent, List<WordData> wordList) {
        intent.putExtra(WORD_COUNT, wordList.size());

        intent.putExtra(WORDS, (ArrayList<WordData>) wordList);

        /*for (int i = 0; i < wordList.size(); i++) {
            WordData word = wordList.get(i);

            intent.putExtra(WORD_ + i, word.getWord());
            intent.putExtra(MEANING_ + i, word.getMeaning());
            intent.putExtra(IMAGE_ + i, word.getImage());
            intent.putExtra(ID_ + i, word.getId());
            //TODO: Transfer image
        }*/

    }

    private final ActivityResultLauncher<Intent> activityDictionaryEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data = result.getData();

                    if (data != null) {
                        boolean isNewDictionary = data.getBooleanExtra(IS_NEW_DICTIONARY, false);

                        /*DictionaryData dictionary = new DictionaryData(
                          data.getStringExtra(DICTIONARY_NAME),
                          data.getLongExtra(WORD_COUNT, 0),
                          data.getBooleanExtra(DICTIONARY_VALUE, false)
                        );*/

                        DictionaryData dictionary = (DictionaryData) data.getSerializableExtra(DICTIONARY);
                        List<WordData> words = (ArrayList<WordData>) data.getSerializableExtra(WORDS);

                        for (WordData word :words) {
                            if (wordRepository.getWordsFromId(word.getId()).size() == 0) {
                                wordRepository.addWord(word);
                            } else {
                                wordRepository.updateWord(word);
                            }
                        }


                        /*for (int i = 0; i < data.getIntExtra(WORD_COUNT, 0); i++) {
                            WordData word = new WordData(
                                    data.getStringExtra(WORD_ + i),
                                    data.getStringExtra(MEANING_ + i),
                                    data.getStringExtra(IMAGE_ + i),
                                    data.getLongExtra(DICTIONARY_ID, 0)
                            );
                            wordRepository.addWord(word);
                        }*/

                        if (isNewDictionary) {
                            dictionaryRepository.addDictionary(dictionary);
                        } else {
                            dictionaryRepository.updateDictionary(dictionary);
                        }

                        adapter.setData(dictionaryRepository.getDictionaries());

                    }
                }
            });

}