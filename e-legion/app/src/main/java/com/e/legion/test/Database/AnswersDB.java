package com.e.legion.test.database;

import android.provider.BaseColumns;

public class AnswersDB implements BaseColumns {
    public static final String TABLE_NAME = "answers";
    public static final String COL_QUESTION_ID = "question_id";
    public static final String COL_ANSWER = "answer";
    public static final String COL_TRUE_FALSE = "true_false";
}