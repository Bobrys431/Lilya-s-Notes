package com.example.lilyasnotes.Widgets.WidgetEditors;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.lilyasnotes.DatabaseManagement.NoteManager;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class NoteWidgetEditor extends Dialog {

    private final int id;

    private EditText title;
    private EditText text;

    public NoteWidgetEditor(@NonNull Context context, int id) {
        super(context);
        this.id = id;
    }

    public NoteWidgetEditor(@NonNull Context context) {
        super(context);
        this.id = -1;
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    public String getTitle() {
        return title.getText().toString();
    }

    public String getText() {
        return text.getText().toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_note);

        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(getContext());

        RelativeLayout basement = findViewById(R.id.basement);
        basement.setBackgroundColor(getContext().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground));

        TextView dialogTitle = findViewById(R.id.widget_title);
        dialogTitle.setTypeface(ResourcesCompat.getFont(getContext(), R.font.advent_pro_bold));
        dialogTitle.setTextColor(getContext().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white));

        title = findViewById(R.id.title);
        title.setTypeface(ResourcesCompat.getFont(getContext(), R.font.advent_pro_bold));
        title.setTextColor(getContext().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white));

        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 45) {
                    Toast.makeText(getContext(), "Максимум символів: 45", Toast.LENGTH_LONG).show();
                    title.setText(charSequence.subSequence(0, 45));
                    title.setEnabled(false);
                    title.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        if (id != -1)
            title.setText(NoteManager.getTitle(id));

        text = findViewById(R.id.text);
        text.setTypeface(ResourcesCompat.getFont(getContext(), R.font.advent_pro_bold));
        text.setTextColor(getContext().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white));
        if (id != -1)
            text.setText(NoteManager.getText(id));
    }
}
