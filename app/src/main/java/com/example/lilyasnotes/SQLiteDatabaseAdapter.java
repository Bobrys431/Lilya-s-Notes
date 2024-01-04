package com.example.lilyasnotes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteDatabaseAdapter extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Notes";
    private static final int DATABASE_VERSION = 5;

    public static final String ADDITIONAL_DATA = "additional_data";
    public static final String APP_THEME = "app_theme";

    public static final String THEME = "theme";
    public static final String THEME_ID = THEME + "_id";
    public static final String THEME_TITLE = THEME + "_title";

    public static final String THEMES = "themes";
    public static final String THEMES_ID = THEMES + "_id";
    public static final String THEMES_THEME_ID = THEME_ID;
    public static final String THEMES_THEME_INDEX = THEME + "_index";

    public static final String THEME_TEXT = "theme_text";
    public static final String THEME_TEXT_ID = THEME_TEXT +  "_id";
    public static final String THEME_TEXT_THEME_ID = THEME_ID;
    public static final String THEME_TEXT_INDEX = "text_index";
    public static final String THEME_TEXT_NOTE = "note";

    public static final String THEME_IMAGE = "theme_image";
    public static final String THEME_IMAGE_ID = THEME_IMAGE + "_id";
    public static final String THEME_IMAGE_THEME_ID = THEME_ID;
    public static final String THEME_IMAGE_INDEX = "image_index";
    public static final String THEME_IMAGE_DATA = "data";

    public static final String THEME_INTO = "theme_into";
    public static final String THEME_INTO_ID = THEME_INTO + "_id";
    public static final String THEME_INTO_THEME_ID = THEME_ID;
    public static final String THEME_INTO_INDEX = THEME + "_index";
    public static final String THEME_INTO_THEME_IN_ID = THEME + "_in_id";

    public SQLiteDatabaseAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON");

        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s(" +
                "%s TEXT NOT NULL" +
                ")", ADDITIONAL_DATA, APP_THEME));

        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s(" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT" +
                ")", THEME, THEME_ID, THEME_TITLE));

        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s(" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s INTEGER NOT NULL, " +
                "%s INTEGER NOT NULL, " +
                "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE" +
                ")", THEMES, THEMES_ID, THEMES_THEME_ID, THEMES_THEME_INDEX,
                THEMES_THEME_ID, THEME, THEME_ID));

        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s(" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s INTEGER NOT NULL, " +
                "%s INTEGER NOT NULL, " +
                "%s TEXT, " +
                "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE" +
                ")", THEME_TEXT, THEME_TEXT_ID, THEME_TEXT_THEME_ID, THEME_TEXT_INDEX, THEME_TEXT_NOTE,
                THEME_TEXT_THEME_ID, THEME, THEME_ID));

        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s(" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s INTEGER NOT NULL, " +
                "%s INTEGER NOT NULL, " +
                "%s BLOB, " +
                "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE" +
                ")", THEME_IMAGE, THEME_IMAGE_ID, THEME_IMAGE_THEME_ID, THEME_IMAGE_INDEX, THEME_IMAGE_DATA,
                THEME_IMAGE_THEME_ID, THEME, THEME_ID));

        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s(" +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s INTEGER NOT NULL, " +
                "%s INTEGER NOT NULL, " +
                "%s INTEGER NOT NULL, " +
                "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE, " +
                "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE" +
                ")", THEME_INTO, THEME_INTO_ID, THEME_INTO_THEME_ID, THEME_INTO_INDEX, THEME_INTO_THEME_IN_ID,
                THEME_INTO_THEME_ID, THEME, THEME_ID,
                THEME_INTO_THEME_IN_ID, THEME, THEME_ID));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ADDITIONAL_DATA);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + THEME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + THEMES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + THEME_TEXT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + THEME_IMAGE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + THEME_INTO);

        onCreate(sqLiteDatabase);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        return new SQLiteDatabaseAdapter(context).getWritableDatabase();
    }

    public static void printTable(String tableName, Context context) {
        if (tableName != null) {
            Cursor printCursor;
            System.out.println(".");

            switch (tableName) {
                case ADDITIONAL_DATA:
                    printCursor = getDatabase(context).rawQuery("SELECT * FROM " + ADDITIONAL_DATA, null);
                    System.out.println(ADDITIONAL_DATA + ":");
                    System.out.println(APP_THEME);
                    if (printCursor != null) {
                        while (printCursor.moveToNext()) {
                            System.out.println(printCursor.getString(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.APP_THEME)));
                        }
                        printCursor.close();
                    }
                    break;
                case THEME:
                    printCursor = getDatabase(context).rawQuery("SELECT * FROM " + THEME + " ORDER BY " + THEME_ID, null);
                    System.out.println(THEME + ":");
                    System.out.println(THEME_ID + "\t" + THEME_TITLE);
                    if (printCursor != null) {
                        while (printCursor.moveToNext()) {
                            System.out.println(
                                    printCursor.getInt(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_ID)) + "\t" +
                                            printCursor.getString(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TITLE))
                            );
                        }
                        printCursor.close();
                    }
                    break;
                case THEMES:
                    printCursor = getDatabase(context).rawQuery("SELECT * FROM " + THEMES + " ORDER BY " + THEMES_ID, null);
                    System.out.println(THEMES + ":");
                    System.out.println(THEMES_ID + "\t" + THEMES_THEME_ID + "\t" + THEMES_THEME_INDEX);
                    if (printCursor != null) {
                        while (printCursor.moveToNext()) {
                            System.out.println(
                                    printCursor.getInt(printCursor.getColumnIndexOrThrow(THEMES_ID)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(THEMES_THEME_ID)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(THEMES_THEME_INDEX))
                            );
                        }
                        printCursor.close();
                    }
                    break;
                case THEME_TEXT:
                    printCursor = getDatabase(context).rawQuery("SELECT * FROM " + THEME_TEXT + " ORDER BY " + THEME_TEXT_ID, null);
                    System.out.println(THEME_TEXT + ":");
                    System.out.println(THEME_TEXT_ID + "\t" + THEME_TEXT_THEME_ID + "\t" + THEME_TEXT_INDEX + "\t" + THEME_TEXT_NOTE);
                    if (printCursor != null) {
                        while (printCursor.moveToNext()) {
                            System.out.println(
                                    printCursor.getInt(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TEXT_ID)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TEXT_THEME_ID)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TEXT_INDEX)) + "\t" +
                                            printCursor.getString(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_TEXT_NOTE))
                            );
                        }
                        printCursor.close();
                    }
                    break;
                case THEME_IMAGE:
                    printCursor = getDatabase(context).rawQuery("SELECT * FROM " + THEME_IMAGE + " ORDER BY " + THEME_IMAGE_ID, null);
                    System.out.println(THEME_IMAGE + ":");
                    System.out.println(THEME_IMAGE_ID + "\t" + THEME_IMAGE_THEME_ID + "\t" + THEME_IMAGE_INDEX);
                    if (printCursor != null) {
                        while (printCursor.moveToNext()) {
                            System.out.println(
                                    printCursor.getInt(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_IMAGE_ID)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_IMAGE_THEME_ID)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_IMAGE_INDEX))
                            );
                        }
                        printCursor.close();
                    }
                    break;
                case THEME_INTO:
                    printCursor = getDatabase(context).rawQuery("SELECT * FROM " + THEME_INTO + " ORDER BY " + THEME_INTO_ID, null);
                    System.out.println(THEME_INTO + ":");
                    System.out.println(THEME_INTO_ID + "\t" + THEME_INTO_THEME_ID + "\t" + THEME_INTO_INDEX + "\t" + THEME_INTO_THEME_IN_ID);
                    if (printCursor != null) {
                        while (printCursor.moveToNext()) {
                            System.out.println(
                                    printCursor.getInt(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_ID)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_THEME_ID)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_INDEX)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(SQLiteDatabaseAdapter.THEME_INTO_THEME_IN_ID))
                            );
                        }
                        printCursor.close();
                    }
                    break;
            }
        } else {
            printTable(ADDITIONAL_DATA, context);
            printTable(THEME, context);
            printTable(THEMES, context);
            printTable(THEME_TEXT, context);
            printTable(THEME_IMAGE, context);
            printTable(THEME_INTO, context);
            System.out.println(".");
        }
    }

    public static void deleteTheme(int themeId, Context context, boolean isIntoThemes) {
        Cursor themesInto;
        int themeIndex = -1;
        int inThemeId = -1;
        SQLiteDatabase database = getDatabase(context);

        if (isIntoThemes) {
            Cursor findThemeIndex = database.rawQuery("SELECT " + THEMES_THEME_INDEX +
                    " FROM " + THEMES +
                    " WHERE " + THEMES_THEME_ID + " = " + themeId, null);
            if (findThemeIndex != null && findThemeIndex.moveToFirst()) {
                themeIndex = findThemeIndex.getInt(findThemeIndex.getColumnIndexOrThrow(THEMES_THEME_INDEX));
                findThemeIndex.close();
            }
        } else {
            Cursor findThemeIndex = database.rawQuery("SELECT " +
                    THEME_INTO_THEME_ID + ", " +
                    THEME_INTO_INDEX +
                    " FROM " + THEME_INTO +
                    " WHERE " + THEME_INTO_THEME_IN_ID + " = " + themeId, null);
            if (findThemeIndex != null && findThemeIndex.moveToFirst()) {
                inThemeId = findThemeIndex.getInt(findThemeIndex.getColumnIndexOrThrow(THEME_INTO_THEME_ID));
                themeIndex = findThemeIndex.getInt(findThemeIndex.getColumnIndexOrThrow(THEME_INTO_INDEX));
                findThemeIndex.close();
            }
        }

        themesInto = database.rawQuery("SELECT " + THEME_INTO_THEME_IN_ID +
                " FROM " + THEME_INTO +
                " WHERE " + THEME_INTO_THEME_ID + " = " + themeId +
                " ORDER BY " + THEME_INTO_THEME_IN_ID + " ASC", null);

        if (themesInto != null) {
            while (themesInto.moveToNext())
                deleteTheme(themesInto.getInt(themesInto.getColumnIndexOrThrow(THEME_INTO_THEME_IN_ID)), context, false);
            themesInto.close();
        }

        if (isIntoThemes) {
            database.execSQL("DELETE FROM " + THEMES +
                    " WHERE " + THEMES_THEME_ID + " = " + themeId);

            database.execSQL("UPDATE " + THEMES +
                    " SET " + THEMES_THEME_INDEX + " = " + THEMES_THEME_INDEX + " - 1" +
                    " WHERE " + THEMES_THEME_INDEX + " > " + themeIndex);
        } else {
            database.execSQL("DELETE FROM " + THEME_INTO +
                    " WHERE " + THEME_INTO_THEME_IN_ID + " = " + themeId);

            database.execSQL("UPDATE " + THEME_INTO +
                    " SET " + THEME_INTO_INDEX + " = " + THEME_INTO_INDEX + " - 1" +
                    " WHERE " + THEME_INTO_THEME_ID + " = " + inThemeId + " AND " + THEME_INTO_INDEX + " > " + themeIndex);
            database.execSQL("UPDATE " + THEME_IMAGE +
                    " SET " + THEME_IMAGE_INDEX + " = " + THEME_IMAGE_INDEX + " - 1" +
                    " WHERE " + THEME_IMAGE_THEME_ID + " = " + inThemeId + " AND " + THEME_IMAGE_INDEX + " > " + themeIndex);
            database.execSQL("UPDATE " + THEME_TEXT +
                    " SET " + THEME_TEXT_INDEX + " = " + THEME_TEXT_INDEX + " - 1" +
                    " WHERE " + THEME_TEXT_THEME_ID + " = " + inThemeId + " AND " + THEME_TEXT_INDEX + " > " + themeIndex);
        }

        database.execSQL("DELETE FROM " + THEME_TEXT +
                " WHERE " + THEME_TEXT_THEME_ID + " = " + themeId);

        database.execSQL("DELETE FROM " + THEME_IMAGE +
                " WHERE " + THEME_IMAGE_THEME_ID + " = " + themeId);

        database.execSQL("DELETE FROM " + THEME +
                " WHERE " + THEME_ID + " = " + themeId);
    }

    public static String getCurrentAppTheme(Context context) {
        SQLiteDatabase database = getDatabase(context);
        Cursor cursor = database.rawQuery("SELECT " + APP_THEME + " FROM " + ADDITIONAL_DATA, null);
        String result = null;
        if (cursor != null && cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(APP_THEME)).toLowerCase();
            cursor.close();
        }
        return result;
    }
}
