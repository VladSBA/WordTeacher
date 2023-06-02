package ru.vladsa.wordteacher;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import ru.vladsa.wordteacher.databinding.FragmentResultBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultFragment extends Fragment {
    private static final String LOG_TAG = MainActivity.LOG_TAG + "_ResFrag";
    private FragmentResultBinding binding;

    private static final String ARG_TIME = "time";
    private static final String ARG_WORDS_LEARNED = "words_learned";
    private static final String ARG_RIGHT_ANSWERS = "right_answers";
    private static final String ARG_WRONG_ANSWERS = "wrong_answers";

    private int time;
    private int wordsLearned;
    private int rightAnswers;
    private int wrongAnswers;

    public ResultFragment() {
        // Required empty public constructor
    }

    public static ResultFragment newInstance(int time, int wordsLearned, int rightAnswers, int wrongAnswers) {
        ResultFragment fragment = new ResultFragment();

        Bundle args = new Bundle();

        args.putInt(ARG_TIME, time);
        args.putInt(ARG_WORDS_LEARNED, wordsLearned);
        args.putInt(ARG_RIGHT_ANSWERS, rightAnswers);
        args.putInt(ARG_WRONG_ANSWERS, wrongAnswers);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Creating fragment...");

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            time = getArguments().getInt(ARG_TIME);
            wordsLearned = getArguments().getInt(ARG_WORDS_LEARNED);
            rightAnswers = getArguments().getInt(ARG_RIGHT_ANSWERS);
            wrongAnswers = getArguments().getInt(ARG_WRONG_ANSWERS);
        }

        Log.d(LOG_TAG, "Fragment created.");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "Creating view...");

        binding = FragmentResultBinding.inflate(inflater, container, false);

        binding.time.setText(String.format((String) (this.getString(R.string.time__int_)), time));
        binding.wordsLearned.setText(String.format((String) (this.getString(R.string.words_learned__int_)), wordsLearned));
        binding.rightAnswers.setText(String.format((String) (this.getString(R.string.right_answers__int_)), rightAnswers));
        binding.wrongAnswers.setText(String.format((String) (this.getString(R.string.wrong_answers__int_)), wrongAnswers));

        binding.finishButton.setOnClickListener(v -> finishLearning());

        Log.d(LOG_TAG, "View created.");

        return binding.getRoot();
    }

    private void finishLearning() {
        Log.d(LOG_TAG, "Finishing learning...");

        FragmentActivity activity = getActivity();

        if (activity != null) {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commit();

            activity.finish();

            Log.d(LOG_TAG, "Learning finished.");
        } else {
            Log.d(LOG_TAG, "Activity is empty.");
        }


    }
}