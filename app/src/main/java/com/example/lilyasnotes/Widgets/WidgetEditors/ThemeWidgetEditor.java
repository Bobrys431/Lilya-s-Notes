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

import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class ThemeWidgetEditor extends Dialog {

    private EditText title;

    public ThemeWidgetEditor(@NonNull Context Context) {
        super(Context);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    public String getTitle() {
        return title.getText().toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_theme);

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
    }
}
