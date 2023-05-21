package ru.vladsa.wordteacher;

import static ru.vladsa.wordteacher.MainActivity.DICTIONARY;
import static ru.vladsa.wordteacher.MainActivity.DICTIONARY_ID;
import static ru.vladsa.wordteacher.MainActivity.IS_NEW_DICTIONARY;
import static ru.vladsa.wordteacher.MainActivity.WORDS;
import static ru.vladsa.wordteacher.MainActivity.WORD_COUNT;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import ru.vladsa.wordteacher.databinding.ActivityDictionaryEditBinding;
import ru.vladsa.wordteacher.dictionaries.DictionaryData;
import ru.vladsa.wordteacher.words.WordAdapter;
import ru.vladsa.wordteacher.words.WordData;

public class DictionaryEditActivity extends AppCompatActivity {
    public final static int RESULT_CODE = 685;
    public final static String GETTING_IMAGE = "Getting image";
    public final static String IMAGE_DIR = "images";
    private DictionaryData dictionary;

    private LinkedList<WordData> words;
    private boolean isNewDictionary;
    private long dictionaryId;
    private int lastPosition = -1;
    private Uri selectedImage;

    private static final String LOG_TAG = MainActivity.LOG_TAG + " (DEActivity)";
    private final WordAdapter.Listener listener = new WordAdapter.Listener() {
        @Override
        public void onPositionClicked(int position) {

        }

        @Override
        public void onLongClicked(int position) {

        }

        @Override
        public void onImageButtonClicked(int position) {
            getBitmap(position);
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

    private LinkedList<WordData> getWordsFromExtra(Intent data) {
        LinkedList<WordData> words = new LinkedList<>();
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

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(LOG_TAG, "Picking image...");
                    Bitmap bitmap;
                    if (result.getResultCode() == RESULT_OK) {
                        selectedImage = result.getData().getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                            saveBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    //TODO: Set result
                }
            }
    );

    private void saveBitmap(Bitmap bitmap) {
        Log.d(LOG_TAG, "Saving bitmap...");
        int position = -1;

        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).getImage().equals(GETTING_IMAGE)) {
                position = i;
            }
        }

        WordData word = words.get(position);

        String fileName = String.format("%s_image_", bitmap.hashCode());

        for (int i = 0;; i++){
            String name = fileName + i + ".png";
            try {
                FileOutputStream fos = null;

                File oldImage = new File(word.getImage());
                if (oldImage.delete()) {
                    Log.d(LOG_TAG, "Old image had deleted");
                } else {
                    Log.d(LOG_TAG, "Old image had not deleted");
                }
                File dir = getDir(IMAGE_DIR, MODE_PRIVATE);
                File file = new File(dir.getAbsoluteFile() + "/" + name);

                FileInputStream fis = null;
                try {
                    fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                    word.setImage(file.getAbsolutePath());

                } finally {
                    if (fos != null) fos.close();
                }

                break;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        words.remove(position);
        words.add(position, word);

        adapter.setWords(words);

        //TODO: Save image
    }

    private void getBitmap(int position) {
        Log.d(LOG_TAG, String.format("Getting bitmap at position %s...", position));

        WordData word = words.get(position);
        word.setImage(GETTING_IMAGE);
        words.remove(position);
        words.add(position, word);


        Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
        photoPickIntent.setType("image/*");
        lastPosition = position;
        pickImageLauncher.launch(photoPickIntent);



    }

}