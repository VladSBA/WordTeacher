package ru.vladsa.wordteacher;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import ru.vladsa.wordteacher.databinding.ActivityMainBinding;
import ru.vladsa.wordteacher.dictionaries.DictionaryAdapter;
import ru.vladsa.wordteacher.dictionaries.DictionaryData;
import ru.vladsa.wordteacher.dictionaries.DictionaryRepository;
import ru.vladsa.wordteacher.words.WordData;
import ru.vladsa.wordteacher.words.WordRepository;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<DictionaryData> data = new ArrayList<>();

    private ActivityMainBinding binding;

    private volatile WordRepository wordRepository;
    private volatile DictionaryRepository dictionaryRepository;

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


        adapter.setData(data);

    }


    DictionaryAdapter.OnDictionaryClickListener clickListener = new DictionaryAdapter.OnDictionaryClickListener() {

        @Override
        public void onDictionaryClick(DictionaryAdapter.ViewHolder holder) {
            Intent intent = new Intent(MainActivity.this, DictionaryEditActivity.class);
            intent.putExtra(DictionaryEditActivity.DICTIONARY_ID, holder.getAdapterPosition());

            activityDictionaryEditLauncher.launch(intent);

        }
    };


    private final DictionaryAdapter adapter = new DictionaryAdapter(clickListener);


    private void addDictionary() {
        Intent intent = new Intent(MainActivity.this, DictionaryEditActivity.class);
        intent.putExtra(DictionaryEditActivity.DICTIONARY_ID, -1);

        activityDictionaryEditLauncher.launch(intent);
    }

    private void start() {
        //TODO: Start learning
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