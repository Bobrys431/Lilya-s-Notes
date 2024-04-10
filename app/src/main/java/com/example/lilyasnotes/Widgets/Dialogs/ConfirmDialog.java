package com.example.lilyasnotes.Widgets.Dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class ConfirmDialog extends DialogFragment {

    private View.OnClickListener onConfirmListener;
    private String title;

    public ConfirmDialog() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.confirm_dialog, null);

        RelativeLayout basement = view.findViewById(R.id.basement);
        TextView titleView = view.findViewById(R.id.title);
        Button confirmButton = view.findViewById(R.id.confirm_button);

        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(view.getContext());

        basement.setBackgroundColor(view.getContext().getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, view.getContext().getTheme()));

        titleView.setText(title);
        titleView.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.advent_pro_bold));
        titleView.setTextColor(view.getContext().getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, view.getContext().getTheme()));

        confirmButton.setTypeface(ResourcesCompat.getFont(view.getContext(), R.font.advent_pro_bold));
        confirmButton.setTextColor(view.getContext().getResources().getColor(
                appTheme.equals("light") ?
                        R.color.black :
                        R.color.white, view.getContext().getTheme()));
        confirmButton.setBackgroundColor(view.getContext().getResources().getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground, view.getContext().getTheme()));
        confirmButton.setOnClickListener(onConfirmListener);

        builder.setView(view);

        return builder.create();
    }

    public void setOnConfirmListener(View.OnClickListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
