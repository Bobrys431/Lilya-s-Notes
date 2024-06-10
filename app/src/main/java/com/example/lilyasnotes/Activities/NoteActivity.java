package com.example.lilyasnotes.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.lilyasnotes.Data.DTO.Note;
import com.example.lilyasnotes.DatabaseManagement.NoteManager;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.EmergentWidgets.NoteEmergentWidget;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.EmergentWidgets.EmergentWidget;

public class NoteActivity extends AppCompatActivity {

    public Note note;

    private EmergentWidget emergentWidget;

    private RelativeLayout basement;
    private EditText text;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        note = new Note(getIntent().getIntExtra("noteId", -1), this);

        SQLiteDatabaseAdapter.printTable(null, this);

        buildEmergentWidget();
        buildBasement();
        buildTextEntering();

        emergentWidget.getThemeButton().changeAllViewsByAppTheme();
    }

    private void buildEmergentWidget() {
        emergentWidget = new NoteEmergentWidget(this);
        emergentWidget.setup();
    }

    private void buildBasement() {
        basement = findViewById(R.id.basement);
    }

    private void buildTextEntering() {
        text = findViewById(R.id.text);
        text.setTypeface(ResourcesCompat.getFont(this, R.font.advent_pro_bold));
        text.setText(note.text);

        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                NoteManager.setText(note.id, charSequence.toString());
                note.text = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public EmergentWidget getEmergentWidget() {
        return emergentWidget;
    }

    public RelativeLayout getBasement() {
        return basement;
    }

    public EditText getEditText() {
        return text;
    }
}
