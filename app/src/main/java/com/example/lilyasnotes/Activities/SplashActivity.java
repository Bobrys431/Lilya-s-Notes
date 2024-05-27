package com.example.lilyasnotes.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.example.lilyasnotes.DatabaseManagement.NoteManager;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.DatabaseManagement.ThemeIntoManager;
import com.example.lilyasnotes.DatabaseManagement.ThemeManager;
import com.example.lilyasnotes.DatabaseManagement.ThemeNoteManager;
import com.example.lilyasnotes.DatabaseManagement.ThemesManager;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.Utilities.ActionsSimulator;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ThemeManager.build(this);
        ThemesManager.build(this);
        ThemeIntoManager.build(this);
        NoteManager.build(this);
        ThemeNoteManager.build(this);

        ActionsSimulator.build(this);

        if (SQLiteDatabaseAdapter.getCurrentAppTheme(this) == null)
            { SQLiteDatabaseAdapter.getDatabase(this).execSQL("INSERT INTO " + SQLiteDatabaseAdapter.ADDITIONAL_DATA + "(" + SQLiteDatabaseAdapter.APP_THEME + ") VALUES ('LIGHT')"); }

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.splashScreenBackground));

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 5000);
    }
}