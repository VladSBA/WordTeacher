package ru.vladsa.wordteacher.words;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.vladsa.wordteacher.databinding.ItemWordBinding;

public class WordAdapterOld extends RecyclerView.Adapter<WordAdapterOld.ViewHolder> {

    public static final String DICTIONARY_ID = "DICT";

    List<WordData> data = new ArrayList<>();

    public interface OnWordDataClickListener {
        void onWordClick(ViewHolder holder);
    }

    private final WordAdapterOld.OnWordDataClickListener clickListener;

    public WordAdapterOld(WordAdapterOld.OnWordDataClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemWordBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
                .getRoot());
    }


    public void setData(List<WordData> newData) {
        data.clear();
        data.addAll(newData);

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull WordAdapterOld.ViewHolder holder, int position) {
        // holder.bind(filterData.get(position));
        holder.bind(data.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onWordClick(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        // return filterData.size();
        return data.size();
    }

    public void removeItemByPosition(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemWordBinding itemWordBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemWordBinding = ItemWordBinding.bind(itemView);
        }

        public void bind(WordData word) {
            itemWordBinding.word.setText(word.getWord());
            itemWordBinding.meaning.setText(word.getMeaning());
            //TODO: set Image
        }
    }
}
