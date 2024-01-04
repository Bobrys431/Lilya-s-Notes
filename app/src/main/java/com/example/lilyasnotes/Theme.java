package com.example.lilyasnotes;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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


    public Theme(int id)
    {
        this.id = id;
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
                        new Theme(dataCursor.getInt(dataCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_THEME_IN_ID)))
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
        dataListView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            int lastSelectedView = 0;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                // Background translation
                float translationY = dataListBackground.getTranslationY() - dy * 0.5f;
                dataListBackground.setTranslationY(translationY);

                float scaleFactor = 1 - 0.2f * translationY / dataListBackground.getHeight();
                dataListBackground.setScaleX(scaleFactor);
                dataListBackground.setScaleY(scaleFactor);

                // Elements cursor
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) dataListView.getLayoutManager();
                if (linearLayoutManager != null)
                {
                    selectedViewPosition =
                            (linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1) ?
                                    linearLayoutManager.findLastCompletelyVisibleItemPosition() :
                                    linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                }

                if (linearLayoutManager != null)
                {
                    for (int i = 0; i < adapter.getItemCount(); i++)
                    {
                        ThemeViewHolder themeViewHolder = (ThemeViewHolder) dataListView.findViewHolderForAdapterPosition(i);
                        View view = linearLayoutManager.findViewByPosition(i);
                        if (themeViewHolder != null && view != null)
                        {
                            float targetAlpha = (i == selectedViewPosition) ? 0.85f : 0.35f;
                            ObjectAnimator.ofFloat(view, "alpha", view.getAlpha(), targetAlpha).setDuration(400).start();

                            float targetScale = (i == selectedViewPosition) ? 1.0f : 0.7f;
                            ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), targetScale).setDuration(400).start();
                            ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), targetScale).setDuration(400).start();

                            if (!isSearching)
                            {
                                float targetTranslation = (i == selectedViewPosition) ? 0f : -150f;
                                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(themeViewHolder.translationFrame, "translationX", themeViewHolder.translationFrame.getTranslationX(), targetTranslation);
                                objectAnimator.setInterpolator(new DecelerateInterpolator());
                                objectAnimator.setDuration(200).start();
                            }

                            if (i == selectedViewPosition && lastSelectedView != selectedViewPosition)
                            {
                                int qQ = selectedViewPosition;
                                new Handler().postDelayed(() -> {
                                    if (qQ == selectedViewPosition)
                                    {
                                        Animation animation = AnimationUtils.loadAnimation(Theme.this, R.anim.shake);
                                        view.startAnimation(animation);
                                    }
                                }, 350);
                            }

                            themeViewHolder.isSelected = i == selectedViewPosition;
                        }
                    }
                    lastSelectedView = selectedViewPosition;
                }
            }
        });
    }


    public static class ThemeViewHolder extends DataViewHolder
    {
        TextView title;
        TextView subthemesTitles;
        ImageButton translationUp;
        ImageButton translationDown;
        RelativeLayout titleFrame;
        RelativeLayout dataFrame;
        RelativeLayout translationFrame;
        RelativeLayout basement;

        boolean isSelected;
        List<Decoration> decorations;


        @SuppressLint("DiscouragedApi")
        public ThemeViewHolder(@NonNull View itemView)
        {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            subthemesTitles = itemView.findViewById(R.id.subthemes_titles);
            translationUp = itemView.findViewById(R.id.translation_up);
            translationDown = itemView.findViewById(R.id.translation_down);
            titleFrame = itemView.findViewById(R.id.title_frame);
            dataFrame = itemView.findViewById(R.id.data_frame);
            translationFrame = itemView.findViewById(R.id.translation_frame);
            basement = itemView.findViewById(R.id.basement);

            isSelected = false;
            decorations = new ArrayList<>();

            Random random = new Random();
            Handler handler = new Handler();

            final String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(itemView.getContext());
            handler.post(() ->
            {
                for (int i = 0; i < 15; i++)
                {
                    ImageView decoration = new ImageView(itemView.getContext());

                    int leftM = random.nextInt(619);
                    int topM = random.nextInt(618);
                    int decorationType = random.nextInt(8) + 1;

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.width = 120;
                    params.height = 120;
                    params.leftMargin = leftM;
                    params.topMargin = topM;
                    decoration.setZ(0);
                    decoration.setRotation(random.nextInt(360));
                    decoration.setAlpha(0.75f);

                    decoration.setImageResource(itemView.getContext().getResources().getIdentifier(
                            "decoration_" + decorationType + "_" + appTheme,
                            "drawable",
                            itemView.getContext().getPackageName()));

                    dataFrame.addView(decoration, params);

                    decoration.setId(View.generateViewId());
                    decorations.add(new Decoration(decoration, decorationType));
                }
            });
            handler.post(() ->
            {
                for (int i = 0; i < 5; i++)
                {
                    ImageView decoration = new ImageView(itemView.getContext());

                    int leftM = random.nextInt(468);
                    int topM = random.nextInt(194);
                    int decorationType = random.nextInt(8) + 1;

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.width = 120;
                    params.height = 120;
                    params.leftMargin = leftM;
                    params.topMargin = topM;
                    decoration.setZ(0);
                    decoration.setRotation(random.nextInt(360));
                    decoration.setAlpha(0.75f);

                    decoration.setImageResource(itemView.getContext().getResources().getIdentifier(
                            "decoration_" + decorationType + "_" + appTheme,
                            "drawable",
                            itemView.getContext().getPackageName()));

                    titleFrame.addView(decoration, params);

                    decoration.setId(View.generateViewId());
                    decorations.add(new Decoration(decoration, decorationType));
                }
            });

            title = itemView.findViewById(R.id.title);
            titleFrame = itemView.findViewById(R.id.title_frame);
            basement = itemView.findViewById(R.id.basement);
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
