package ru.vladsa.wordteacher.words;

import static ru.vladsa.wordteacher.DictionaryEditActivity.GETTING_IMAGE;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import ru.vladsa.wordteacher.MainActivity;
import ru.vladsa.wordteacher.R;
import ru.vladsa.wordteacher.databinding.ItemWordBinding;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {
    private static final String LOG_TAG = MainActivity.LOG_TAG + " (WordAdapter)";
    public static final int ID_DELETE_IMAGE = 1;
    public static final int ID_DELETE = 2;

    private static ItemWordBinding lastWordBinding;
    private static int lastWordPosition = -1;

    private int position;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public interface Listener {

        void onPositionClicked(int position);

        void onLongClicked(int position);

        void onImageButtonClicked(int position);

        void onCreatedContextMenu(int position);
    }

    private final Listener listener;

    private static final List<WordData> words = new LinkedList<>();

    public WordAdapter(Listener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                ItemWordBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false)
                        .getRoot(),
                listener);
    }

    public void clearLastWord() {
        lastWordPosition = -1;
        lastWordBinding = null;
    }

    public void updateWords() {
        Log.d(LOG_TAG, String.format("Updating words %s at LWP = %d...", words, lastWordPosition));
        if (lastWordPosition != -1 && lastWordBinding != null) {
            WordData word = words.get(lastWordPosition);

            word.setWord(lastWordBinding.word.getText().toString());
            word.setMeaning(lastWordBinding.meaning.getText().toString());

            words.remove(lastWordPosition);
            words.add(lastWordPosition, word);
        }

        Log.d(LOG_TAG, String.format("Updated words: %s", words));
    }

    public void setWords(List<WordData> newData) {
        words.clear();
        words.addAll(newData);

        notifyDataSetChanged();
    }

    public List<WordData> getWords() {
        Log.d(LOG_TAG, String.format("Getting words: %s", words));
        return words;
    }

    @Override
    public void onBindViewHolder(@NonNull WordAdapter.ViewHolder holder, int position) {
        Log.d(LOG_TAG, String.format("Binding... at %s from %s ap position %d", words.get(position), words, position));
        holder.bind(words.get(position));

    }

    @Override
    public int getItemCount() {
        // return filterData.size();
        return words.size();
    }

    public void removeItemByPosition(int position) {
        words.remove(position);

        notifyItemRemoved(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener, View.OnFocusChangeListener, View.OnCreateContextMenuListener {
        private final ItemWordBinding wordBinding;
        private final WeakReference<Listener> listenerRef;

        public ViewHolder(@NonNull View itemView, Listener listener) {
            super(itemView);
            wordBinding = ItemWordBinding.bind(itemView);
            listenerRef = new WeakReference<>(listener);

            itemView.setOnClickListener(this);
            wordBinding.word.setOnClickListener(this);
            wordBinding.meaning.setOnClickListener(this);
            wordBinding.image.setOnClickListener(this);

            wordBinding.word.setOnFocusChangeListener(this);
            wordBinding.meaning.setOnFocusChangeListener(this);

            itemView.setOnCreateContextMenuListener(this);
            wordBinding.image.setOnCreateContextMenuListener(this);

        }

        public void bind(WordData word) {
            wordBinding.word.setText(word.getWord());
            wordBinding.meaning.setText(word.getMeaning());

            if (word.getImage() != null && !word.getImage().equals("null") && !word.getImage().isEmpty() && !word.getImage().equals(GETTING_IMAGE)) {
                Bitmap bitmap;

                try {
                    FileInputStream fis = new FileInputStream(word.getImage());
                    bitmap = BitmapFactory.decodeStream(fis);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                wordBinding.image.setImageBitmap(bitmap);
            } else {
                wordBinding.image.setImageDrawable(null);
            }

        }

        private void updateWords(boolean hasFocus) {
            Log.d(LOG_TAG, String.format("Updating words %s, at position %d, LWP = %d", words, getAdapterPosition(), lastWordPosition));

            if (lastWordPosition != -1 && lastWordBinding != null) {
                WordData word = words.get(lastWordPosition);

                word.setWord(lastWordBinding.word.getText().toString());
                word.setMeaning(lastWordBinding.meaning.getText().toString());

                words.remove(lastWordPosition);
                words.add(lastWordPosition, word);

                lastWordPosition = -1;
                lastWordBinding = null;
            }

            int position = getAdapterPosition();

            if (position >= 0) {
                if (hasFocus) {
                    lastWordPosition = position;
                    lastWordBinding = wordBinding;
                }

                WordData word = words.get(position);

                word.setWord(wordBinding.word.getText().toString());
                word.setMeaning(wordBinding.meaning.getText().toString());

                words.remove(position);
                words.add(position, word);
            }

            Log.d(LOG_TAG, String.format("Updated words %s, at position %d, LWP = %d", words, getAdapterPosition(), lastWordPosition));
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == wordBinding.image.getId()) {
                listenerRef.get().onImageButtonClicked(getAdapterPosition());
            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            Log.d(LOG_TAG, "LongClick at position " + getAdapterPosition());
            listenerRef.get().onLongClicked(getAdapterPosition());

            return true;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            updateWords(hasFocus);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if (v.getId() == itemView.getId()) {
                Log.d(LOG_TAG, "Creating context menu at position " + getAdapterPosition());
                menu.add(Menu.NONE, ID_DELETE_IMAGE, Menu.NONE, R.string.delete_image);
                menu.add(Menu.NONE, ID_DELETE, Menu.NONE, R.string.delete);
            }

            listenerRef.get().onCreatedContextMenu(getAdapterPosition());

        }
    }
}
