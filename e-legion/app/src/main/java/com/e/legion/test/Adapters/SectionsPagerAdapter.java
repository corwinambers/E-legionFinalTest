package com.e.legion.test.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.e.legion.test.QuizFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = SectionsPagerAdapter.class.getSimpleName();
    private int mQuestionsCount;

    public SectionsPagerAdapter(FragmentManager fm, int questionsCount) {
        super(fm);
        this.mQuestionsCount = questionsCount;
    }

    @Override
    public Fragment getItem(int position) {
        return QuizFragment.create(position);
    }

    @Override
    public int getCount() {
        return mQuestionsCount;
    }
}