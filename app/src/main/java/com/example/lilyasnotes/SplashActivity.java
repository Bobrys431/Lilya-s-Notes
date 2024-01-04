package com.example.lilyasnotes;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
// import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // SQLiteDatabase database = SQLiteDatabaseAdapter.getDatabase(this);
        /*
        database.execSQL("INSERT INTO " + SQLiteDatabaseAdapter.THEME_INTO + " (" +
                SQLiteDatabaseAdapter.THEME_INTO_THEME_ID + ", " +
                SQLiteDatabaseAdapter.THEME_INTO_INDEX + ", '" +
                SQLiteDatabaseAdapter.THEME_INTO_THEME_IN_ID + "')" +
                "VALUES " +
                "(9, 2, 14), " +
                "(8, 0, 13)");
        /*
        database.execSQL("INSERT INTO " + SQLiteDatabaseAdapter.THEME_TEXT + " (" +
                SQLiteDatabaseAdapter.THEME_TEXT_THEME_ID + ", " +
                SQLiteDatabaseAdapter.THEME_TEXT_INDEX + ", '" +
                SQLiteDatabaseAdapter.THEME_TEXT_NOTE + "')" +
                "VALUES " +
                "(9, 0, 'Hello World!'), " +
                "(9, 1, 'Ліля лох'), " +
                "(13, 0, 'Попа')");
         */
        /*
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.ADDITIONAL_DATA);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEME);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEMES);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEME_TEXT);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEME_IMAGE);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEME_INTO);
         */

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, Main.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}