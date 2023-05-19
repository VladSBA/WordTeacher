package ru.vladsa.wordteacher;

import static ru.vladsa.wordteacher.MainActivity.DICTIONARY;
import static ru.vladsa.wordteacher.MainActivity.DICTIONARY_ID;
import static ru.vladsa.wordteacher.MainActivity.IS_NEW_DICTIONARY;
import static ru.vladsa.wordteacher.MainActivity.WORDS;
import static ru.vladsa.wordteacher.MainActivity.WORD_COUNT;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import ru.vladsa.wordteacher.databinding.ActivityDictionaryEditBinding;
import ru.vladsa.wordteacher.dictionaries.DictionaryData;
import ru.vladsa.wordteacher.words.WordAdapter;
import ru.vladsa.wordteacher.words.WordData;

public class DictionaryEditActivity extends AppCompatActivity {
    public final static int RESULT_CODE = 685;
    private DictionaryData dictionary;

    private ArrayList<WordData> words;
    private boolean isNewDictionary;
    private long dictionaryId;

    private static final String LOG_TAG = MainActivity.LOG_TAG + " (DEActivity)";
    private final WordAdapter.Listener listener = new WordAdapter.Listener() {
        @Override
        public void onPositionClicked(int position) {

        }

        @Override
        public void onLongClicked(int position) {

        }
    };
    private final WordAdapter adapter = new WordAdapter(listener);


    private ActivityDictionaryEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDictionaryEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.add.setOnClickListener(view -> addWord());
        binding.confirm.setOnClickListener(view -> confirm());

        Intent data = getIntent();

        words = getWordsFromExtra(data);
        adapter.clearLastWord();
        isNewDictionary = data.getBooleanExtra(IS_NEW_DICTIONARY, false);
        dictionaryId = data.getLongExtra(DICTIONARY_ID, 0);
        if (!isNewDictionary) {
            dictionary = (DictionaryData) data.getSerializableExtra(DICTIONARY);
        } else {
            dictionary = new DictionaryData("", 0, false);
        }

        binding.name.setText(dictionary.getName());

        adapter.setWords(words);
        binding.container.setAdapter(adapter);

        Log.d(LOG_TAG, "DictionaryEditActivity has been created");

    }

    private ArrayList<WordData> getWordsFromExtra(Intent data) {
        ArrayList<WordData> words = new ArrayList<>();
        words.addAll((ArrayList<WordData>) data.getSerializableExtra(WORDS));

        /*int word_count = data.getIntExtra(WORD_COUNT, 0);

        for (int i = 0; i < word_count; i++) {
            words.add(new WordData(
                    data.getStringExtra(WORD_ + i),
                    data.getStringExtra(MEANING_ + i),
                    data.getStringExtra(IMAGE_ + i),
                    data.getLongExtra(DICTIONARY_ID, 0)
            ));
            words.get(i).setId(data.getLongExtra(ID_, 0));
            //TODO: Get image
        }*/

        return words;
    }

    private void confirm() {
        Log.d(LOG_TAG, "Try to confirm");

        words.clear();
        adapter.updateWords();
        words.addAll(adapter.getWords());
        dictionary.setName(binding.name.getText().toString());
        dictionary.setWordCount(words.size());

        if (checkFields()) {
            Intent data = new Intent();

            data.putExtra(IS_NEW_DICTIONARY, isNewDictionary);
            data.putExtra(WORD_COUNT, words.size());
            data.putExtra(DICTIONARY, dictionary);
            data.putExtra(WORDS, words);
            //TODO: Transfer images

            /*
            data.putExtra(DICTIONARY_NAME, dictionary.getName());
            data.putExtra(DICTIONARY_ID, dictionary.getId());
            data.putExtra(DICTIONARY_VALUE, dictionary.getValue());

            for (int i = 0; i < words.size(); i++) {
                WordData word = words.get(i);
                data.putExtra(WORD_ + i, word.getWord());
                data.putExtra(MEANING_ + i, word.getMeaning());
                data.putExtra(DICTIONARY_ID + i, dictionary.getId());
            }*/


            Log.d(LOG_TAG, "Finishing DictionaryEditActivity...");

            setResult(RESULT_CODE, data);
            this.finish();

            Log.d(LOG_TAG, "DictionaryEditActivity finished");
        }
        
    }

    private boolean checkFields() {
        Log.d(LOG_TAG, "Checking Fields");

        if (binding.name.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.name_is_empty, Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "Dictionary name is empty");
            return false;
        }

        for (WordData word : words) {
            if(word.getWord().isEmpty() && (word.getMeaning().isEmpty() || word.getImage().isEmpty())) {
                Toast.makeText(this, R.string.not_fields_filled, Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, "Not all required fields are field");
                return false;
            }
        }

        Log.d(LOG_TAG, "All required fields are field");
        return true;
    }

    private void addWord() {
        if (isNewDictionary) {
            words.add(new WordData("", "", null, dictionaryId));
        } else {
            words.add(new WordData("", "", null, dictionary.getId()));
        }

        //TODO: add image
        adapter.setWords(words);

        Log.d(LOG_TAG, "Word added");

    }

}