package com.example.lilyasnotes;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class Theme extends AppCompatActivity implements Data
{
    final public int id;
    public String title;

    public List<Data> data;
    private SQLiteDatabase database;

    private RecyclerView dataListView;
    private ThemeRecyclerViewAdapter adapter;
    private ImageView dataListBackground;
    private NestedScrollView backgroundScrollView;
    private int selectedViewPosition;

    private ImageButton addButton;
    private GifImageView addSplash;
    private ImageButton deleteButton;
    private GifImageView deleteSplash;
    private ImageButton themeButton;
    private GifImageView themeSplash;
    private ImageButton exitButton;
    private GifImageView exitSplash;

    private EditText searchBar;
    private Map<Integer, Boolean> hiddenData;
    private boolean isSearching;

    private List<Decoration> decorations;
    private RelativeLayout actionBarLayout;


    public Theme(int id, Context context)
    {
        this.id = id;

        Cursor titleCursor = SQLiteDatabaseAdapter.getDatabase(context).rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_TITLE +
                " FROM " + SQLiteDatabaseAdapter.THEME +
                " WHERE " + SQLiteDatabaseAdapter.THEME_ID + " = " + id, null);
        if (titleCursor != null && titleCursor.moveToFirst())
        {
            title = titleCursor.getString(titleCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TITLE));
            titleCursor.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        database = SQLiteDatabaseAdapter.getDatabase(this);
        data = new ArrayList<>();
        selectedViewPosition = 0;
        decorations = new ArrayList<>();
        isSearching = false;
        hiddenData = new HashMap<>();


        // Loading data from DB
        Map<Integer, Data> dataMap = new HashMap<>();

        Cursor dataCursor = database.rawQuery("SELECT " +
                SQLiteDatabaseAdapter.THEME_INTO_THEME_IN_ID + ", " +
                SQLiteDatabaseAdapter.THEME_INTO_INDEX +
                " FROM " + SQLiteDatabaseAdapter.THEME_INTO +
                " WHERE " + SQLiteDatabaseAdapter.THEME_INTO_THEME_ID + " = " + id, null);
        if (dataCursor != null)
        {
            while (dataCursor.moveToNext())
                dataMap.put(
                        dataCursor.getInt(dataCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_INDEX)),
                        new Theme(dataCursor.getInt(dataCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_THEME_IN_ID)), this)
                );
            dataCursor.close();
        }

        dataCursor = database.rawQuery("SELECT " +
                SQLiteDatabaseAdapter.THEME_TEXT_NOTE + ", " +
                SQLiteDatabaseAdapter.THEME_TEXT_INDEX +
                " FROM " + SQLiteDatabaseAdapter.THEME_TEXT +
                " WHERE " + SQLiteDatabaseAdapter.THEME_TEXT_THEME_ID + " = " + id, null);
        if (dataCursor != null)
        {
            while (dataCursor.moveToNext())
                dataMap.put(
                        dataCursor.getInt(dataCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TEXT_INDEX)),
                        new Note(dataCursor.getString(dataCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TEXT_NOTE)))
                );
            dataCursor.close();
        }

        dataCursor = database.rawQuery("SELECT " +
                SQLiteDatabaseAdapter.THEME_IMAGE_DATA + ", " +
                SQLiteDatabaseAdapter.THEME_IMAGE_INDEX +
                " FROM " + SQLiteDatabaseAdapter.THEME_IMAGE +
                " WHERE " + SQLiteDatabaseAdapter.THEME_IMAGE_THEME_ID + " = " + id, null);
        if (dataCursor != null)
        {
            while (dataCursor.moveToNext())
                dataMap.put(
                        dataCursor.getInt(dataCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TEXT_INDEX)),
                        new Image(dataCursor.getBlob(dataCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_IMAGE_DATA)))
                );
            dataCursor.close();
        }

        for (int i = 0; i < dataMap.size(); i++)
        {
            data.add(dataMap.get(i));
        }


        dataListView = findViewById(R.id.data_list_view);
        dataListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ThemeRecyclerViewAdapter(data, dataListView);
        dataListView.setAdapter(adapter);

        dataListBackground = findViewById(R.id.data_list_background);
    }


    public static class ThemeViewHolder extends DataViewHolder
    {
        TextView title;
        RelativeLayout titleFrame;
        ImageView mark;
        RelativeLayout basement;

        boolean isSelected;


        @SuppressLint("DiscouragedApi")
        public ThemeViewHolder(@NonNull View itemView)
        {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            titleFrame = itemView.findViewById(R.id.title_frame);
            mark = itemView.findViewById(R.id.mark);
            basement = itemView.findViewById(R.id.basement);

            isSelected = false;
        }
    }


    public static class ThemeViewDialog extends DialogFragment
    {

        RelativeLayout basement;
        int position;
        EditText title;
        OnDialogClosedListener onDialogClosedListener;

        public ThemeViewDialog(int position)
        {
            this.position = position;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.theme_view_dialog_fragment, null);

            Cursor cursor;
            title = view.findViewById(R.id.title);
            if (position != -1)
            {
                cursor = SQLiteDatabaseAdapter.getDatabase(view.getContext()).rawQuery("SELECT " + SQLiteDatabaseAdapter.THEME_TITLE +
                        " FROM " + SQLiteDatabaseAdapter.THEME +
                        " WHERE " + SQLiteDatabaseAdapter.THEME_ID + " = " + MainRecyclerViewAdapter.themes.get(position).id, null);
                if (cursor != null && cursor.moveToFirst())
                {
                    title.setText(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TITLE)));
                    cursor.close();
                }
            }


            basement = view.findViewById(R.id.basement);
            final String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(view.getContext());
            if (appTheme != null)
            {
                basement.setBackgroundColor(
                        appTheme.equals("light") ?
                                view.getContext().getColor(R.color.lightThemeBackground) :
                                view.getContext().getColor(R.color.darkThemeBackground));
                title.setTextColor(
                        appTheme.equals("light") ?
                                view.getContext().getColor(R.color.black) :
                                view.getContext().getColor(R.color.white));
            }

            builder.setView(view);
            return builder.create();
        }

        @Override
        public void onDismiss(@NonNull DialogInterface dialog)
        {
            super.onDismiss(dialog);
            notifyDialogClosed(title.getText().toString());
        }

        public void setOnDialogClosedListener(OnDialogClosedListener onDialogClosedListener)
        {
            this.onDialogClosedListener = onDialogClosedListener;
        }

        private void notifyDialogClosed(String result)
        {
            if (onDialogClosedListener != null)
            {
                onDialogClosedListener.onDialogClosed(result);
            }
        }
    }
}
