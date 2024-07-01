package com.example.lilyasnotes.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lilyasnotes.R;
import com.example.lilyasnotes.Utilities.Console;

public class ConsoleActivity extends AppCompatActivity {

    public final static int THEMES_TYPE = -1;
    public final static int THEME_TYPE = 0;
    public final static int NOTE_TYPE = 1;

    private int type;
    private int id;

    private EditText commandsHistory;
    private EditText inputLine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);

        type = getIntent().getIntExtra("type", -2);
        id = getIntent().getIntExtra("id", -2);

        buildCommandsHistory();
        buildInputLine();
        buildStatusBar();
    }

    private void buildCommandsHistory() {
        commandsHistory = findViewById(R.id.commands_history);
        commandsHistory.setText(Console.getCommandsHistory());
    }

    private void buildInputLine() {
        inputLine = findViewById(R.id.input_line);

        inputLine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().contains("*")) {
                    Console.executeCommandLine(type, id);
                    inputLine.setText("");
                    commandsHistory.setText(Console.getCommandsHistory());
                } else {
                    Console.setCommandLine(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void buildStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.black));
    }
}
