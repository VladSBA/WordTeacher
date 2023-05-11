package ru.vladsa.wordteacher.dictionaries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

import ru.vladsa.wordteacher.MainActivity;
import ru.vladsa.wordteacher.R;
import ru.vladsa.wordteacher.databinding.ActivityDictionaryEditBinding;
import ru.vladsa.wordteacher.dictionaries.words.WordAdapter;
import ru.vladsa.wordteacher.dictionaries.words.WordData;

public class DictionaryEditActivity extends AppCompatActivity {
    public final static String DICTIONARY_ID = "DICT_ID";
    public final static String DICTIONARY_NAME = "DICT_NAME";
    public final static String WORD_COUNT = "WORD_C";
    public final static String WORD_ID = "WORD_";
    public final static String MEANING_ID = "MEANING_";
    public final static String IMAGE_ID = "IMAGE_";
    public final static int RESULT_CODE = 685;

    private final ArrayList<WordData> words = new ArrayList<>();
    private int dictionaryID = -1;
    private String dictionaryName = "";

    private ActivityDictionaryEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDictionaryEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dictionaryID = getIntent().getIntExtra(DICTIONARY_ID, -1);

        binding.container.setAdapter(adapter);

        binding.add.setOnClickListener(view -> addWord());
        binding.confirm.setOnClickListener(view -> confirm());

    }

    WordAdapter.OnWordDataClickListener clickListener = new WordAdapter.OnWordDataClickListener() {
        @Override
        public void onWordClick(WordAdapter.ViewHolder holder) {

        }
    };


    private final WordAdapter adapter = new WordAdapter(clickListener);


    private void confirm() {
        if (checkFields()) {
            Intent intent = new Intent();
            intent.putExtra(DICTIONARY_ID, dictionaryID);
            intent.putExtra(DICTIONARY_NAME, dictionaryID);
            intent.putExtra(WORD_COUNT, Integer.toString(words.size()));

            for (int i = 0; i < words.size(); i++) {
                intent.putExtra(WORD_ID + i, words.get(i).getWord());
                intent.putExtra(MEANING_ID + i, words.get(i).getMeaning());
                intent.putExtra(IMAGE_ID + i, words.get(i).getImage());
            }

            setResult(RESULT_CODE);

            this.finish(); 
        }
        
    }

    private boolean checkFields() {
        if (binding.name.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.name_is_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        for (WordData word : words) {
            if(word.getWord().isEmpty() || word.getMeaning().isEmpty())
                Toast.makeText(this, R.string.not_fields_filled, Toast.LENGTH_SHORT).show();
                return false;
        }

        return true;
    }

    private void addWord() {
        words.add(new WordData("", "", null, 0));
    }

    //TODO: Saving words

}