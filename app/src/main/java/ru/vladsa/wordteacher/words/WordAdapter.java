package ru.vladsa.wordteacher.words;

import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import ru.vladsa.wordteacher.MainActivity;
import ru.vladsa.wordteacher.databinding.ItemWordBinding;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {
    private static ItemWordBinding lastWordBinding;
    private static int lastWordPosition = -1;

    private static final String LOG_TAG = MainActivity.LOG_TAG + " (WordAdapter)";

    public interface Listener {

        void onPositionClicked(int position);

        void onLongClicked(int position);

        Bitmap onImageButtonClicked(int position);
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
        if (lastWordPosition != -1 && lastWordBinding != null) {
            WordData word = words.get(lastWordPosition);

            word.setWord(lastWordBinding.word.getText().toString());
            word.setMeaning(lastWordBinding.meaning.getText().toString());

            words.remove(lastWordPosition);
            words.add(lastWordPosition, word);

            //TODO: Set image
        }
    }

    public void setWords(List<WordData> newData) {
        words.clear();
        words.addAll(newData);

        notifyDataSetChanged();
    }

    public List<WordData> getWords() {
        return words;
    }

    @Override
    public void onBindViewHolder(@NonNull WordAdapter.ViewHolder holder, int position) {
        Log.d(LOG_TAG, "Binding...");
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


    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener, View.OnFocusChangeListener, TextWatcher {
        private final ItemWordBinding wordBinding;
        private final WeakReference<Listener> listenerRef;

        public ViewHolder(@NonNull View itemView, Listener listener) {
            super(itemView);
            wordBinding = ItemWordBinding.bind(itemView);
            listenerRef = new WeakReference<>(listener);
            lastWordBinding = wordBinding;

            itemView.setOnClickListener(this);
            wordBinding.word.setOnClickListener(this);
            wordBinding.meaning.setOnClickListener(this);
            wordBinding.image.setOnClickListener(this);

            wordBinding.word.setOnFocusChangeListener(this);
            wordBinding.meaning.setOnFocusChangeListener(this);

        }

        public void bind(WordData word) {
            wordBinding.word.setText(word.getWord());
            wordBinding.meaning.setText(word.getMeaning());
            //TODO: set Image

            /*wordBinding.word.setOnFocusChangeListener((v, hasFocus) -> {
                Log.d(LOG_TAG, String.format("Element %s's focus has been changed", word));
            });*/
        }

        private void setWords() {
            int position = getAdapterPosition();
            lastWordPosition = position;
            WordData word = words.get(position);

            word.setWord(wordBinding.word.getText().toString());
            word.setMeaning(wordBinding.meaning.getText().toString());

            words.remove(position);
            words.add(position, word);

            //TODO: Set image
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == wordBinding.image.getId()) {
                wordBinding.image.setImageBitmap(listenerRef.get().onImageButtonClicked(getAdapterPosition()));
            }

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            //TODO: Handle long click and return "true"

            listenerRef.get().onLongClicked(getAdapterPosition());
            return false;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            setWords();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            setWords();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setWords();
        }

        @Override
        public void afterTextChanged(Editable s) {
            setWords();
        }

    }
}
