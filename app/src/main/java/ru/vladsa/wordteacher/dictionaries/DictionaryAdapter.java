package ru.vladsa.wordteacher.dictionaries;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.vladsa.wordteacher.databinding.ItemDictionaryBinding;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.ViewHolder> {

    private static final List<DictionaryData> data = new ArrayList<>();

    public interface OnDictionaryClickListener {
        void onDictionaryClick(ViewHolder holder);
    }

    private final OnDictionaryClickListener clickListener;

    public DictionaryAdapter(OnDictionaryClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemDictionaryBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
                .getRoot());
    }


    public void setData(List<DictionaryData> newData) {
        data.clear();
        data.addAll(newData);

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // holder.bind(filterData.get(position));
        holder.bind(data.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onDictionaryClick(holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void removeItemByPosition(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDictionaryBinding itemDictionaryBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemDictionaryBinding = ItemDictionaryBinding.bind(itemView);
        }

        public void bind(DictionaryData dictionaryData) {
            itemDictionaryBinding.name.setText(dictionaryData.getName());
            itemDictionaryBinding.count.setText(dictionaryData.getWordCount());
            itemDictionaryBinding.value.setChecked(dictionaryData.getValue());
        }
    }
}
