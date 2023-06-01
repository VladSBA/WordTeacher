package ru.vladsa.wordteacher;

import static ru.vladsa.wordteacher.DictionaryEditActivity.GETTING_IMAGE;
import static ru.vladsa.wordteacher.DictionaryEditActivity.IMAGE_DIR;
import static ru.vladsa.wordteacher.SettingsActivity.APP_PREFERENCES;
import static ru.vladsa.wordteacher.dictionaries.DictionaryAdapter.ID_DELETE;
import static ru.vladsa.wordteacher.dictionaries.DictionaryAdapter.ID_EXPORT;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.vladsa.wordteacher.databinding.ActivityMainBinding;
import ru.vladsa.wordteacher.dictionaries.Dictionary;
import ru.vladsa.wordteacher.dictionaries.DictionaryAdapter;
import ru.vladsa.wordteacher.dictionaries.DictionaryData;
import ru.vladsa.wordteacher.dictionaries.DictionaryRepository;
import ru.vladsa.wordteacher.words.Word;
import ru.vladsa.wordteacher.words.WordData;
import ru.vladsa.wordteacher.words.WordRepository;

public class MainActivity extends AppCompatActivity {
    public static final String WORD_COUNT = "Word_count";
    public static final String DICTIONARY_ID = "Dictionary_id";
    public static final String DICTIONARY = "Dictionary";
    public static final String WORDS = "Words";
    public static final String DELETED_WORDS = "Deleted_words";
    public static final String IS_NEW_DICTIONARY = "Is_new_dict";

    public static final String MIME_TYPE_WTD = "application/wtd";

    private DictionaryData exportingDictionary = null;

    private static final int PERMISSION_REQUEST_CODE = 67;

    public static final String LOG_TAG = "Log_WT_M";

    private SharedPreferences preferences;

    private final String LAST_DICTIONARY_ID = "last_dictionary_id";
    private long lastDictionaryId;

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


    private DictionaryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.start.setOnClickListener(view -> start());
        binding.addDictionary.setOnClickListener(view -> addDictionary());

        wordRepository = WordRepository.getInstance(this);
        dictionaryRepository = DictionaryRepository.getInstance(this);

        adapter = new DictionaryAdapter(listener, this.getString(R.string.word_count));

        binding.container.setAdapter(adapter);

        adapter.setData(dictionaryRepository.getDictionaries());

        registerForContextMenu(binding.container);

        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        lastDictionaryId = preferences.getLong(LAST_DICTIONARY_ID, 0);

        Log.d(LOG_TAG, "MainActivity has been created");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.settings) {
            openSettings();
        } else if (itemId == R.id.import_dictionary) {
            importDictionary();
        }

        return super.onOptionsItemSelected(item);
    }

    private void importDictionary() {
        Intent dictionaryPickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        dictionaryPickIntent.setType("*/*");

        //TODO: cast type to MIME_TYPE_WTD

        dictionaryPickLauncher.launch(dictionaryPickIntent);
    }

    private void openSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);

        Log.d(LOG_TAG, "SettingsActivity was starting...");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position;

        try {
            position = adapter.getPosition();
        } catch (Exception e) {
            Log.d(LOG_TAG, e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }

        DictionaryData dictionary = dictionaryRepository.getDictionaries().get(position);

        switch (item.getItemId()) {
            case ID_EXPORT:
                exportDictionary(dictionary, true);
                break;
            case ID_DELETE:
                deleteDictionary(dictionary);
                adapter.removeItemByPosition(position);
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void saveDictionaryOld(Dictionary dictionary) {
        String fileName = dictionary.getName() + ".wtd";

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        File file = new File(dir, fileName);

        if (dir.canWrite()) {
            try {
                FileOutputStream fos = null;
                ObjectOutputStream oos = null;

                try {
                    fos = new FileOutputStream(file);
                    oos = new ObjectOutputStream(fos);

                    String message;

                    if (dictionary.save(oos)) {
                        message = String.format(this.getString(R.string.dictionary__name__has_been_saved), dictionary.getName());
                    } else {
                        message = String.format(this.getString(R.string.dictionary__name__has_not_been_saved), dictionary.getName());
                    }

                    exportingDictionary = null;

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                } finally {
                    if (fos != null) fos.close();
                    if (oos != null) oos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void exportDictionary(DictionaryData dictionary, boolean isNewTry) {
        exportingDictionary = dictionary;

        ArrayList<WordData> wordDataList = new ArrayList<>(wordRepository.getDictionaryWords(dictionary.getId()));
        ArrayList<Word> words = new ArrayList<Word>();

        for (WordData data : wordDataList) {
            words.add(new Word(data));
        }

        Dictionary dict = new Dictionary(dictionary.getName(), words);

        Log.d(LOG_TAG, String.format("Exporting dictionary %s...  isNewTry = %b", dict, isNewTry));

        //TODO: Save dictionary in other thread

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(LOG_TAG, "New variant.");

            String fileName = dictionary.getName() + ".wtd";

            ContentValues cv = new ContentValues();
            cv.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            cv.put(MediaStore.Downloads.MIME_TYPE, MIME_TYPE_WTD);
            cv.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            ContentResolver cr = this.getContentResolver();

            Uri uri = cr.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv);

            try {
                OutputStream os = null;
                ObjectOutputStream oos = null;

                try {
                    os = cr.openOutputStream(uri);
                    oos = new ObjectOutputStream(os);

                    String message;

                    if (dict.save(oos)) {
                        message = String.format(this.getString(R.string.dictionary__name__has_been_saved), dictionary.getName());
                    } else {
                        message = String.format(getString(R.string.dictionary__name__has_not_been_saved), dictionary.getName());
                    }

                    exportingDictionary = null;

                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                    exportingDictionary = null;

                } finally {
                    if (os != null) os.close();
                    if (oos != null) oos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {

            if (isNewTry) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d(LOG_TAG, "Old variant");

                    saveDictionaryOld(dict);

                } else {
                    Log.d(LOG_TAG, "Requesting permission...");

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_CODE);
                }
            } else {
                saveDictionaryOld(dict);
            }

        }

        Log.d(LOG_TAG, "Saving ended");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && Objects.equals(permissions[0], Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportDictionary(exportingDictionary, false);

                Log.d(LOG_TAG, "Write external permission received");
            } else {

                Toast.makeText(this, this.getString(R.string.permission_not_received), Toast.LENGTH_SHORT).show();

                Log.d(LOG_TAG, "Write external permission not received");
            }
        }

    }

    private void deleteDictionary(DictionaryData dictionary) {
        Log.d(LOG_TAG, String.format("Deleting dictionary %s...", dictionary));

        ArrayList<WordData> words = new ArrayList<>();
        words.addAll(wordRepository.getDictionaryWords(dictionary.getId()));

        for (WordData word : words) {
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

        intent.putExtra(DICTIONARY_ID, lastDictionaryId + 1);

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

    private final ActivityResultLauncher<Intent> dictionaryPickLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data = result.getData();

                    if (data != null && result.getResultCode() == RESULT_OK) {
                        Log.d(LOG_TAG, "Getting data...");

                        Dictionary dictionary = null;

                        Uri uri = null;
                        if (result.getData() != null) {
                            uri = result.getData().getData();
                        }

                        Log.d(LOG_TAG, String.format("Uri = %s, URI = %s.", uri, uri));

                        try {
                            InputStream is = null;
                            ObjectInputStream ois = null;

                            try {
                                is = getContentResolver().openInputStream(uri);
                                ois = new ObjectInputStream(is);

                                dictionary = Dictionary.load(ois);

                            } finally {
                                if (is != null) is.close();
                                if (ois != null) ois.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (dictionary != null) {
                            unpackDictionary(dictionary);
                        } else {
                            Toast.makeText(MainActivity.this, R.string.file_extension_is_invalid, Toast.LENGTH_SHORT).show();

                            Log.w(LOG_TAG, "File extension is invalid");
                        }

                        Log.d(LOG_TAG, "Data received");

                    } else {
                        Log.d(LOG_TAG, "Data error");
                    }
                }
            });

    private void unpackDictionary(Dictionary dictionary) {
        DictionaryData dictionaryData = new DictionaryData(dictionary.getName(), dictionary.getWordCount(), false);
        dictionaryRepository.addDictionary(dictionaryData);

        ArrayList<Word> words = (ArrayList<Word>) dictionary.getWords();
        ArrayList<WordData> wordsDataList = new ArrayList<>();

        Log.d(LOG_TAG, "Dictionary adding...");

        lastDictionaryId = preferences.getLong(LAST_DICTIONARY_ID, 0);
        lastDictionaryId++;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(LAST_DICTIONARY_ID, lastDictionaryId);
        editor.apply();

        Log.d(LOG_TAG, "Dictionary has added. LDI = " + lastDictionaryId);

        for (Word word : words) {
            Bitmap bitmap = word.getImage();

            WordData wordData = new WordData(word.getWord(), word.getMeaning(), null, lastDictionaryId);

            if (bitmap != null) {
                Log.d(LOG_TAG, "Saving bitmap...");
                int position = -1;

                String fileName = String.format("%s_image_", bitmap.hashCode());

                for (int i = 0; ; i++) {
                    String name = fileName + i + ".png";
                    try {
                        FileOutputStream fos = null;

                        File dir = getDir(IMAGE_DIR, MODE_PRIVATE);
                        File file = new File(dir.getAbsoluteFile() + "/" + name);

                        try {
                            fos = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                            wordData.setImage(file.getAbsolutePath());

                        } finally {
                            if (fos != null) fos.close();
                        }

                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

            wordsDataList.add(wordData);

        }

        wordRepository.addWords(wordsDataList);

        adapter.setData(dictionaryRepository.getDictionaries());

        Log.d(LOG_TAG, String.format("Word data list %s has created.", wordsDataList));
    }

    private final ActivityResultLauncher<Intent> activityDictionaryEditLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent data = result.getData();

                    if (data != null) {
                        Log.d(LOG_TAG, "Getting data...");

                        boolean isNewDictionary = data.getBooleanExtra(IS_NEW_DICTIONARY, false);

                        DictionaryData dictionary = (DictionaryData) data.getSerializableExtra(DICTIONARY);
                        ArrayList<WordData> words = (ArrayList<WordData>) data.getSerializableExtra(WORDS);
                        ArrayList<WordData> deletedWords = (ArrayList<WordData>) data.getSerializableExtra(DELETED_WORDS);

                        if (isNewDictionary) {
                            Log.d(LOG_TAG, "Dictionary adding...");

                            dictionaryRepository.addDictionary(dictionary);

                            lastDictionaryId = preferences.getLong(LAST_DICTIONARY_ID, 0);
                            lastDictionaryId++;

                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putLong(LAST_DICTIONARY_ID, lastDictionaryId);
                            editor.apply();

                            Log.d(LOG_TAG, "Dictionary has added. LDI = " + lastDictionaryId);
                        } else {
                            dictionaryRepository.updateDictionary(dictionary);
                            Log.d(LOG_TAG, "Dictionary has updated.");
                        }

                        Log.d(LOG_TAG, String.format("Deleting words %s...", deletedWords));

                        for (WordData word : deletedWords) {
                            if (word != null && wordRepository.getWordsFromId(word.getId()).size() > 0) {
                                wordRepository.removeByPosition(word);
                            }
                        }

                        Log.d(LOG_TAG, String.format("Getting words %s...", words));

                        for (WordData word : words) {
                            if (wordRepository.getWordsFromId(word.getId()).size() == 0) {
                                wordRepository.addWord(word);
                            } else {
                                wordRepository.updateWord(word);
                            }
                        }

                        adapter.setData(dictionaryRepository.getDictionaries());

                    }

                    Log.d(LOG_TAG, "Data received.");

                }
            });

}