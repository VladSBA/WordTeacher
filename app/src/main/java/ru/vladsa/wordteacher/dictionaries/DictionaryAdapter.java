package ru.vladsa.wordteacher.dictionaries;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import ru.vladsa.wordteacher.MainActivity;
import ru.vladsa.wordteacher.databinding.ItemDictionaryBinding;

public class DictionaryAdapter extends RecyclerView.Adapter<DictionaryAdapter.ViewHolder> {
    private static final String LOG_TAG = MainActivity.LOG_TAG + " (DictionaryAdapter)";
    private static final List<DictionaryData> dictionaries = new ArrayList<>();
    public static final int ID_DELETE = 1;

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public DictionaryAdapter(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {

        void onPositionClicked(int position);

        void onLongClicked(int position);
    }

    private final Listener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                ItemDictionaryBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false)
                        .getRoot(),
                listener);
    }

    public List<DictionaryData> getDictionaries() {
        return dictionaries;
    }

    public void setData(List<DictionaryData> newData) {
        dictionaries.clear();
        dictionaries.addAll(newData);

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // holder.bind(filterData.get(position));
        holder.bind(dictionaries.get(position));
    }

    @Override
    public int getItemCount() {
        return dictionaries.size();
    }

    public void removeItemByPosition(int position) {
        dictionaries.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener,
            View.OnLongClickListener,
            CompoundButton.OnCheckedChangeListener,
            View.OnCreateContextMenuListener {
        private final ItemDictionaryBinding dictionaryBinding;

        private final WeakReference<Listener> listenerRef;

        public ViewHolder(@NonNull View itemView, Listener listener) {
            super(itemView);
            dictionaryBinding = ItemDictionaryBinding.bind(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            dictionaryBinding.value.setOnCheckedChangeListener(this);

            listenerRef = new WeakReference<>(listener);
        }

        public void bind(DictionaryData data) {
            if (!data.getName().isEmpty()) {
                dictionaryBinding.name.setText(data.getName());
            }
            dictionaryBinding.count.setText(String.valueOf(data.getWordCount()));
            dictionaryBinding.value.setChecked(data.getValue());
        }

        private void dictionaryUpdate() {
            int position = getAdapterPosition();
            DictionaryData dictionary = dictionaries.get(position);

            dictionary.setValue(dictionaryBinding.value.isChecked());

            dictionaries.remove(position);
            dictionaries.add(position, dictionary);

        }

        @Override
        public void onClick(View v) {
            dictionaryUpdate();

            listenerRef.get().onPositionClicked(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d(LOG_TAG, "LongClick at position " + getAdapterPosition());
            listenerRef.get().onLongClicked(getAdapterPosition());

            return false;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            dictionaryUpdate();
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            Log.d(LOG_TAG, "Creating context menu at position " + getAdapterPosition());
            menu.add(Menu.NONE, ID_DELETE, Menu.NONE, "Delete");

        }
    }
}
