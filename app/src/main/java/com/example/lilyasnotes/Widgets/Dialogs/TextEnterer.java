package com.example.lilyasnotes.Widgets.Dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class TextEnterer extends DialogFragment {

    private EditText text;
    private DialogInterface.OnDismissListener onDismissListener;

    private String startText;

    public TextEnterer() {
    }

    public TextEnterer(String text) {
        startText = text;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.get_title_widget, null);

        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(view.getContext());

        text = view.findViewById(R.id.text);
        text.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.advent_pro_bold));
        text.setTextColor(view.getContext().getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, view.getContext().getTheme()));
        text.setText(startText);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { /* **NOTHING** */ }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 20) {
                    Toast.makeText(getContext(), "Максимальна довжина - 20 символів", Toast.LENGTH_SHORT).show();
                    text.setText(charSequence.subSequence(0, 20));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { /* **NOTHING** */ }
        });

        TextView title = view.findViewById(R.id.title);
        title.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.advent_pro_bold));
        title.setTextColor(view.getContext().getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, view.getContext().getTheme()));

        RelativeLayout basement = view.findViewById(R.id.basement);
        basement.setBackgroundColor(view.getContext().getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, view.getContext().getTheme()));


        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        onDismissListener.onDismiss(dialog);
    }

    public String getText() {
        return text.getText().toString();
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }
}
