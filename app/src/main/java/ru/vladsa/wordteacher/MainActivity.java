package ru.vladsa.wordteacher;

import static ru.vladsa.wordteacher.DictionaryEditActivity.GETTING_IMAGE;
import static ru.vladsa.wordteacher.dictionaries.DictionaryAdapter.ID_DELETE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
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
    public static final String DELETED_WORDS = "Deleted_words";
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
            Log.d(LOG_TAG, "Setting position " + position);
            adapter.setPosition(position);

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

        registerForContextMenu(binding.container);

        Log.d(LOG_TAG, "MainActivity has been created");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = -1;

        try {
            position = adapter.getPosition();
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }

        if (item.getItemId() == ID_DELETE) {
            deleteDictionary(dictionaryRepository.getDictionaries().get(position));

            adapter.removeItemByPosition(position);
        }

        return super.onContextItemSelected(item);
    }

    private void deleteDictionary(DictionaryData dictionary) {
        Log.d(LOG_TAG, String.format("Deleting dictionary %s...", dictionary));

        ArrayList<WordData> words = (ArrayList<WordData>) wordRepository.getDictionaryWords(dictionary.getId());

        for (WordData word :words) {
            Log.d(LOG_TAG, String.format("Deleting word %s...", word));

            if (word.getImage() != null && !word.getImage().isEmpty() && !word.getImage().equals(GETTING_IMAGE)) {
                Log.d(LOG_TAG, "Deleting image...");
                File file = new File(word.getImage());

                if (file.delete()) {
                    Log.d(LOG_TAG, "Image has deleted");
                } else {
                    Log.d(LOG_TAG, "Image has not deleted");
                }
            }

            wordRepository.removeByPosition(word);
        }

        dictionaryRepository.removeByPosition(dictionary);

        Log.d(LOG_TAG, "Dictionary has deleted.");
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

    }

    private final ActivityResultLauncher<Intent> activityDictionaryEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data = result.getData();

                    if (data != null) {
                        boolean isNewDictionary = data.getBooleanExtra(IS_NEW_DICTIONARY, false);

                        DictionaryData dictionary = (DictionaryData) data.getSerializableExtra(DICTIONARY);
                        List<WordData> words = (ArrayList<WordData>) data.getSerializableExtra(WORDS);
                        List<WordData> deletedWords = (ArrayList<WordData>) data.getSerializableExtra(DELETED_WORDS);

                        for (WordData word :words) {
                            if (wordRepository.getWordsFromId(word.getId()).size() == 0) {
                                wordRepository.addWord(word);
                            } else {
                                wordRepository.updateWord(word);
                            }
                        }

                        for (WordData word: deletedWords) {
                            if (word != null && wordRepository.getWordsFromId(word.getId()).size() > 0) {
                                wordRepository.removeByPosition(word);
                            }
                        }

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