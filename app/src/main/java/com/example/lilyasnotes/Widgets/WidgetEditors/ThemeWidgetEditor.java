package com.example.lilyasnotes.Widgets.WidgetEditors;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.Database.ThemeManager;
import com.example.lilyasnotes.R;

public class ThemeWidgetEditor extends Dialog {

    private final int id;

    private EditText title;

    public ThemeWidgetEditor(@NonNull Context context, int id) {
        super(context);
        this.id = id;
    }

    public ThemeWidgetEditor(@NonNull Context context) {
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
        if (id != -1)
            title.setText(ThemeManager.getTitle(id));
    }
}
