package com.e.legion.test;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.androidquiz.R;
import com.e.legion.test.database.AnswersDB;
import com.e.legion.test.database.DatabaseHandler;
import com.e.legion.test.database.QuestionsDB;
import com.e.legion.test.helpers.PreferencesHelper;

public class QuizFragment extends Fragment {

    private static final String TAG = QuizFragment.class.getSimpleName();
    private static final String ARG_PAGE = "ARG_PAGE";
    private static final int DELAY_BETWEEN_NEXT_QUESTION = 500;

    private TextView mQuestionTextView;
    private ListView mListView;
    private SimpleCursorAdapter mQuestionsAdapter;
    private DatabaseHandler mSQLHandler;
    private PreferencesHelper mPreferencesHelper;
    private int mPosition;
    private int[] mAnswers;
    private String mSelectionQuestions;
    private String mSelectionAnswers;

    public static QuizFragment create(int pageNumber) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public QuizFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt(ARG_PAGE) + 1;
        Log.i(TAG, "mPosition = " + mPosition);
        mPreferencesHelper = new PreferencesHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        initResources(view);
        setQuiz();

        return view;
    }

    private void initResources(View view) {
        mQuestionTextView = (TextView) view.findViewById(R.id.fragment_question_text_view);
        mListView = (ListView) view.findViewById(R.id.fragment_list);
        mSQLHandler = new DatabaseHandler(getActivity());
        mSelectionQuestions = QuestionsDB._ID + "=?";
        mSelectionAnswers = AnswersDB.COL_QUESTION_ID + "=?";
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setQuiz() {
        String[] selectionArgs = {String.valueOf(mPosition)};

        mQuestionsAdapter = new SimpleCursorAdapter(getActivity(), R.layout.fragment_quiz,
                mSQLHandler.readTableQuery(QuestionsDB.TABLE_NAME,
                        null, mSelectionQuestions, selectionArgs, null, null, null),
                new String[]{QuestionsDB.COL_QUESTION},
                new int[]{R.id.list_item_text_view}, 0);

        SimpleCursorAdapter answersAdapter = new SimpleCursorAdapter(getActivity(), R.layout.main_list_item,
                mSQLHandler.readTableQuery(AnswersDB.TABLE_NAME,
                        null, mSelectionAnswers, selectionArgs, null, null, null),
                new String[]{AnswersDB.COL_ANSWER},
                new int[]{R.id.list_item_text_view}, 0);

        setQuestion();
        setTrueAndFalseAnswers();

        mListView.setAdapter(answersAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView answers = (TextView) view.findViewById(R.id.list_item_text_view);

                if (mAnswers[position] == 1) {
                    answers.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_green_600));
                    mListView.setOnItemClickListener(null);

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mPreferencesHelper.getInt(QuizActivity.SP_QUESTION_POSITION) == 0) {
                                ((QuizActivity) getActivity()).getViewPager().setCurrentItem(QuizActivity.START_QUESTION_POSITION);
                            } else {
                                ((QuizActivity) getActivity()).getViewPager().setCurrentItem(mPreferencesHelper.getInt(QuizActivity.SP_QUESTION_POSITION));
                            }
                        }
                    }, DELAY_BETWEEN_NEXT_QUESTION);


                } else {
                    answers.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_red_800));
                }
            }
        });
    }

    private void setQuestion() {
        Cursor cursor = mQuestionsAdapter.getCursor();
        cursor.moveToFirst();
        mQuestionTextView.setText(cursor.getString(cursor.getColumnIndex(QuestionsDB.COL_QUESTION)));
    }

    private void setTrueAndFalseAnswers() {
        String[] columns = {AnswersDB.COL_TRUE_FALSE};
        String[] selectionArgs = {String.valueOf(mPosition)};
        Cursor cursor = mSQLHandler.readTableQuery(AnswersDB.TABLE_NAME,
                columns, mSelectionAnswers, selectionArgs, null, null, null);

        if (mAnswers == null) {
            mAnswers = new int[cursor.getCount()];
        }

        while (cursor.moveToNext()) {
            mAnswers[cursor.getPosition()] =
                    cursor.getInt(cursor.getColumnIndex(AnswersDB.COL_TRUE_FALSE));
        }
    }
}