package com.example.lilyasnotes.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
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

    private RelativeLayout textFrame;
    private EditText text;
    private ImageView backgroundImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        note = new Note(getIntent().getIntExtra("noteId", -1), this);

        SQLiteDatabaseAdapter.printTable(null, this);

        buildEmergentWidget();
        buildTextEntering();
        buildScrollingBackground();

        emergentWidget.getThemeButton().changeAllViewsByAppTheme();
    }

    private void buildEmergentWidget() {
        emergentWidget = new NoteEmergentWidget(this);
        emergentWidget.setup();
    }

    private void buildTextEntering() {
        textFrame = findViewById(R.id.text_frame);

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

    protected void buildScrollingBackground() {
        backgroundImage = findViewById(R.id.background_image);

        text.setOnScrollChangeListener((view, i, i1, i2, i3) -> {
            float translationY = backgroundImage.getTranslationY() - (i1 - i3) * 0.5f;
            backgroundImage.setTranslationY(translationY);

            float scaleFactor = 1 - 0.2f * translationY / backgroundImage.getHeight();
            backgroundImage.setScaleX(scaleFactor);
            backgroundImage.setScaleY(scaleFactor);
        });
    }

    public EmergentWidget getEmergentWidget() {
        return emergentWidget;
    }

    public RelativeLayout getTextFrame() {
        return textFrame;
    }

    public EditText getEditText() {
        return text;
    }
}
