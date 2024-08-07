package com.example.lilyasnotes.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.lilyasnotes.DatabaseManagement.NoteManager;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.DatabaseManagement.ThemeIntoManager;
import com.example.lilyasnotes.DatabaseManagement.ThemeManager;
import com.example.lilyasnotes.DatabaseManagement.ThemeNoteManager;
import com.example.lilyasnotes.DatabaseManagement.ThemesManager;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.Utilities.Console;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        Runnable runnable = () -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        };

        ThemeManager.build(this);
        ThemesManager.build(this);
        ThemeIntoManager.build(this);
        NoteManager.build(this);
        ThemeNoteManager.build(this);

        Console.build(this);

        if (SQLiteDatabaseAdapter.getCurrentAppTheme(this) == null)
            { SQLiteDatabaseAdapter.getDatabase(this).execSQL("INSERT INTO " + SQLiteDatabaseAdapter.ADDITIONAL_DATA + "(" + SQLiteDatabaseAdapter.APP_THEME + ") VALUES ('LIGHT')"); }

        ImageButton consoleButton = findViewById(R.id.console_button);
        consoleButton.setOnClickListener(view -> {
            handler.removeCallbacks(runnable);
            Intent intent = new Intent(this, ConsoleActivity.class);
            intent.putExtra("type", ConsoleActivity.THEMES_TYPE);
            intent.putExtra("id", ConsoleActivity.THEMES_TYPE);
            this.startActivity(intent);
        });

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.splashScreenBackground));

        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility
                (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        handler.postDelayed(runnable, 5000);
    }
}