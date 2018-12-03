package com.e.legion.test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquiz.R;
import com.e.legion.test.helpers.PreferencesHelper;
import com.e.legion.test.adapters.SectionsPagerAdapter;
import com.e.legion.test.database.DatabaseHandler;
import com.e.legion.test.database.ProofsDB;
import com.e.legion.test.database.QuestionsDB;

public class QuizActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = QuizActivity.class.getSimpleName();

    private final static String SP_QUESTIONS_COUNT = "SP_QUESTIONS_COUNT";
    public final static String SP_QUESTION_POSITION = "SP_QUESTION_POSITION";
    public final static int START_QUESTION_POSITION = 1;
    private static final int VIEW_PAGER_DEFAULT_OFF_SCREEN_LIMIT = 2;

    private DatabaseHandler mSQLHandler;
    private PreferencesHelper mPreferencesHelper;
    private SpannableString mSpannableString;
    private ViewPager mViewPager;
    private String mSelectionProofs;
    private EditText mPage;
    private AlertDialog mAlertDialog;
    private int mQuestionPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//      TODO make layouts for Tablets 10"
//      TODO Remove all SQL logic from QuizActivity & QuizFragment to correspond module
//      TODO make whole code review

        initResources();
        setProof(mQuestionPosition);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.activity_main_menu);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                mPreferencesHelper.getInt(SP_QUESTIONS_COUNT));

        mViewPager.setAdapter(sectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(VIEW_PAGER_DEFAULT_OFF_SCREEN_LIMIT);
        mViewPager.setCurrentItem(mPreferencesHelper.getInt(SP_QUESTION_POSITION) - 1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mQuestionPosition = position + 1;
                setProof(position + 1);
                mPreferencesHelper.setInt(SP_QUESTION_POSITION, position + 1);
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        findViewById(R.id.drawer_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrl(getString(R.string.drawer_quiz_link));
            }
        });
    }

    private void initResources() {
        ((NavigationView) findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
        mViewPager = (ViewPager) findViewById(R.id.container);
        LayoutInflater li = LayoutInflater.from(QuizActivity.this);
        final View view = li.inflate(R.layout.main_dialog_search, null);
        mPage = (EditText) view.findViewById(R.id.quiz_edit_text_add_tag);
        mAlertDialog = new AlertDialog.Builder(QuizActivity.this)
                .setView(view)
                //Setting to null. We'll override the onclick
                .setPositiveButton(getString(R.string.menu_dialog_page_selection_positive_button), null)
                .setNegativeButton(getString(R.string.menu_dialog_page_selection_negative_button), null)
                .create();
        mSQLHandler = new DatabaseHandler(this);
        mPreferencesHelper = new PreferencesHelper(this);
        mSelectionProofs = ProofsDB.COL_QUESTION_ID + "=?";
        mQuestionPosition = mPreferencesHelper.getInt(SP_QUESTION_POSITION, START_QUESTION_POSITION);
        if (mPreferencesHelper.getInt(SP_QUESTIONS_COUNT) == 0) {
            mPreferencesHelper.setInt(SP_QUESTIONS_COUNT,
                    mSQLHandler.readTable(QuestionsDB.TABLE_NAME).getCount());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_item_page_count).setTitle(String.valueOf(mQuestionPosition) +
                getResources().getString(R.string.menu_page_count) +
                String.valueOf(mPreferencesHelper.getInt(SP_QUESTIONS_COUNT)));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_proof:
                AlertDialog alertDialogProof = new AlertDialog.Builder(QuizActivity.this)
                        .setPositiveButton(android.R.string.ok, null)
                        .setMessage(mSpannableString)
                        .create();

                alertDialogProof.show();

                // Make the textView clickable. Must be called after show()
                ((TextView) alertDialogProof.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

                return true;

            case R.id.menu_item_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.menu_share_link));
                shareIntent.setType("text/plain");
                startActivity(shareIntent);

                return true;

            case R.id.menu_item_page_count:
                mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button positiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                setPage();

                            }
                        });
                    }
                });

                mAlertDialog.getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                mPage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            setPage();
                            return true;
                        }
                        return false;
                    }
                });

                mAlertDialog.show();
                return true;

            default:
                return onOptionsItemSelected(item);
        }
    }

    private void setPage() {
        String page = mPage.getText().toString();
        mPage.setText("");

        if (page.trim().isEmpty() ||
                Integer.valueOf(page) > mPreferencesHelper.getInt(SP_QUESTIONS_COUNT) ||
                Integer.valueOf(page) < START_QUESTION_POSITION) {
            Toast.makeText(QuizActivity.this, getString(R.string.page_selection_error), Toast.LENGTH_SHORT).show();
        } else {
            mViewPager.setCurrentItem(Integer.valueOf(page) - 1);
        }
        mAlertDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_forum:
                openUrl(getString(R.string.drawer_forum_link));
                return true;
            case R.id.nav_feedback:
                Intent feedbackIntent = new Intent();
                feedbackIntent.setAction(Intent.ACTION_SENDTO);
                feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                feedbackIntent.setData(Uri.parse("mailto:" + getString(R.string.drawer_feedback_e_mail)));

                try {
                    startActivity(feedbackIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(QuizActivity.this, getString(R.string.drawer_feedback_e_mail_error), Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.nav_about:
                AlertDialog d = new AlertDialog.Builder(QuizActivity.this)
                        .setPositiveButton(android.R.string.ok, null)
                        .setTitle(getString(R.string.drawer_references_version) + getVersionNameOfApp())
                        .setMessage(getString(R.string.drawer_references))
                        .create();
                d.show();
                return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setProof(int position) {
        String[] selectionArgs = {String.valueOf(position)};
        SimpleCursorAdapter proofsAdapter = new SimpleCursorAdapter(QuizActivity.this, R.layout.activity_main,
                mSQLHandler.readTableQuery(ProofsDB.TABLE_NAME,
                        null, mSelectionProofs, selectionArgs, null, null, null),
                new String[]{ProofsDB.COL_PROOF},
                null, 0);

        Cursor cursor = proofsAdapter.getCursor();
        cursor.moveToFirst();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cursor.getString(cursor.getColumnIndex(ProofsDB.COL_PROOF)));
        stringBuilder.append(System.getProperty("line.separator"));
        while (cursor.moveToNext()) {
            stringBuilder.append(System.getProperty("line.separator"));
            stringBuilder.append(cursor.getString(cursor.getColumnIndex(ProofsDB.COL_PROOF)));
            stringBuilder.append(System.getProperty("line.separator"));
        }
        mSpannableString = new SpannableString(stringBuilder);
        Linkify.addLinks(mSpannableString, Linkify.ALL);
    }

    private void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private String getVersionNameOfApp() {
        String versionName = "";
        try {
            versionName = QuizActivity.this.getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public ViewPager getViewPager() {
        if (mViewPager == null) {
            mViewPager = (ViewPager) findViewById(R.id.container);
        }
        return mViewPager;
    }
}