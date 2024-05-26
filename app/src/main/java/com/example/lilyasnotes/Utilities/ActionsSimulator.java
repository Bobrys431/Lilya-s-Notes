package com.example.lilyasnotes.Utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.lilyasnotes.Database.NoteManager;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.Database.ThemeIntoManager;
import com.example.lilyasnotes.Database.ThemeManager;
import com.example.lilyasnotes.Database.ThemeNoteManager;
import com.example.lilyasnotes.Database.ThemesManager;

public class ActionsSimulator {
    private static SQLiteDatabase database;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public static void build(Context context) {
        ActionsSimulator.context = context;
        database = SQLiteDatabaseAdapter.getDatabase(context);
    }


    public static void clearDB() {
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.ADDITIONAL_DATA);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEME);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.NOTE);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEMES);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEME_NOTE);
        database.execSQL("DELETE FROM " + SQLiteDatabaseAdapter.THEME_INTO);
    }

    public static int[] createThemes(int count) {
        int[] idList = new int[count];

        for (int i = 0; i < count; i++) {
            ThemeManager.addNewTheme("Тема №" + (i + 1));
            ThemesManager.addConnection(ThemeManager.getLastThemeId());

            System.out.println("Created in themes theme(" + ThemeManager.getLastThemeId() + ") - " + (i + 1));
            idList[i] = ThemeManager.getLastThemeId();
        }

        return idList;
    }

    public static int[] createThemes(int count, int id) {
        int[] idList = new int[count];

        for (int i = 0; i < count; i++) {
            ThemeManager.addNewTheme("Тема №" + (i + 1));
            ThemeIntoManager.addConnection(id, ThemeManager.getLastThemeId());

            System.out.println("Created in theme(" + id + ") theme(" + ThemeManager.getLastThemeId() + ") - " + (i + 1));
            idList[i] = ThemeManager.getLastThemeId();
        }

        return idList;
    }

    public static int[] createNotes(int count, int id) {
        int[] idList = new int[count];

        for (int i = 0; i < count; i++) {

            StringBuilder text = new StringBuilder();
            for (int j = 0; j < i; j++) {
                text.append("Слово№").append(j + 1).append(" ");
            }

            NoteManager.addNewNote("Нот №" + (i + 1), text.toString());
            ThemeNoteManager.addConnection(id, NoteManager.getLastNoteId());

            System.out.println("Created in theme(" + id + ") note(" + NoteManager.getLastNoteId() + ") - " + (i + 1));
            idList[i] = NoteManager.getLastNoteId();
        }

        return idList;
    }


    public static void firstAction() {
        int[] ids = createThemes(3);

        for (int id : ids) {
            createProfileIn(3, id, 3);
        }

        SQLiteDatabaseAdapter.printTable(null, context);
    }

    public static void secondAction() {
        int[] ids = createThemes(5);

        for (int id : ids) {
            createProfileIn(5, id, 3);
        }

        SQLiteDatabaseAdapter.printTable(null, context);
    }

    public static void thirdAction() {
        int[] ids = createThemes(1);

        for (int id : ids) {
            createProfileIn(12, id, 1);
        }

        SQLiteDatabaseAdapter.printTable(null, context);
    }

    private static void createProfileIn(int count, int id, int depth) {
        if (depth == 0) return;

        int newDepth = depth - 1;
        int[] ids = createThemes(count, id);
        createNotes(count, id);

        for (int inId : ids) {
            createProfileIn(count, inId, newDepth);
        }
    }
}
