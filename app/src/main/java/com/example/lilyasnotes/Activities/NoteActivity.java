package com.example.lilyasnotes.Activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lilyasnotes.Data.DTO.Note;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.Widgets.EmergentWidget;

public class NoteActivity extends AppCompatActivity {

    public Note note;

    private EmergentWidget emergentWidget;

    private ImageView background;
    private EditText text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        note = new Note(getIntent().getIntExtra("noteId", -1), this);

        SQLiteDatabaseAdapter.printTable(null, this);

        setupScrollableBackground();
        setupTextEntering();
        setupEmergentWidget();

        emergentWidget.getThemeButton().changeByAppTheme();
    }

    private void setupScrollableBackground() {

    }

    private void setupTextEntering() {

    }

    private void setupEmergentWidget() {

    }
}
