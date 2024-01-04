package com.example.lilyasnotes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmDialog extends DialogFragment {
    RelativeLayout basement;
    TextView title;

    final private String titleText;

    ConfirmDialogListener listener;

    public ConfirmDialog(String titleText) {
        this.titleText = titleText;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.confirm_dialog_fragment, null);

        title = view.findViewById(R.id.title);
        title.setText(titleText);

        basement = view.findViewById(R.id.basement);
        final String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(view.getContext());
        basement.setBackgroundColor(appTheme.equals("light") ? view.getContext().getColor(R.color.lightThemeBackground) : view.getContext().getColor(R.color.darkThemeBackground));
        title.setTextColor(appTheme.equals("light") ? view.getContext().getColor(R.color.black) : view.getContext().getColor(R.color.white));

        builder
                .setView(view)
                .setPositiveButton("Так", (dialogInterface, i) -> listener.isConfirmed(true))
                .setNegativeButton("Ні", (dialogInterface, i) -> listener.isConfirmed(false));

        AlertDialog dialog = builder.create();

        dialog.getWindow().getDecorView().findViewById(android.R.id.content).setBackgroundColor(
                appTheme.equals("light") ?
                        view.getContext().getColor(R.color.lightThemeBackground) :
                        view.getContext().getColor(R.color.darkThemeBackground)
        );

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
                    appTheme.equals("light") ?
                            view.getContext().getColor(R.color.lightThemeBackground) :
                            view.getContext().getColor(R.color.darkThemeBackground)
            );
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    appTheme.equals("light") ?
                            view.getContext().getColor(R.color.black) :
                            view.getContext().getColor(R.color.white)
            );

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(
                    appTheme.equals("light") ?
                            view.getContext().getColor(R.color.lightThemeBackground) :
                            view.getContext().getColor(R.color.darkThemeBackground)
            );
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    appTheme.equals("light") ?
                            view.getContext().getColor(R.color.black) :
                            view.getContext().getColor(R.color.white)
            );
        });

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ConfirmDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement ConfirmDialogListener");
        }
    }

    public interface ConfirmDialogListener {
        void isConfirmed(boolean isConfirmed);
    }
}
