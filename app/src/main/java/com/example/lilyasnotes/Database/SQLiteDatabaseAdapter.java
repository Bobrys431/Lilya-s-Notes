package com.example.lilyasnotes.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteDatabaseAdapter extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Notes";
    private static final int DATABASE_VERSION = 6;

    public static final String ADDITIONAL_DATA = "additional_data";
    public static final String APP_THEME = "app_theme";

    public static final String THEME = "theme";
    public static final String THEME_ID = THEME + "_id";
    public static final String THEME_TITLE = THEME + "_title";

    public static final String NOTE = "note";
    public static final String NOTE_ID = NOTE + "_id";
    public static final String NOTE_TITLE = NOTE + "_title";
    public static final String NOTE_TEXT = NOTE + "_text";

    public static final String THEMES = "themes";
    public static final String THEMES_ID = THEMES + "_id";
    public static final String THEMES_THEME_ID = THEME_ID;
    public static final String THEMES_THEME_INDEX = THEME + "_index";

    public static final String THEME_NOTE = "theme_note";
    public static final String THEME_NOTE_ID = THEME_NOTE + "_id";
    public static final String THEME_NOTE_THEME_ID = THEME_ID;
    public static final String THEME_NOTE_INDEX = NOTE + "_index";
    public static final String THEME_NOTE_IN_ID = NOTE + "_in_id";

    public static final String THEME_INTO = "theme_into";
    public static final String THEME_INTO_ID = THEME_INTO + "_id";
    public static final String THEME_INTO_THEME_ID = THEME_ID;
    public static final String THEME_INTO_INDEX = THEME + "_index";
    public static final String THEME_INTO_IN_ID = THEME + "_in_id";

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
                "%s TEXT, " +
                "%s TEXT" +
                ")", NOTE, NOTE_ID, NOTE_TITLE, NOTE_TEXT));

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
                        "%s INTEGER NOT NULL, " +
                        "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE, " +
                        "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE" +
                        ")", THEME_NOTE, THEME_NOTE_ID, THEME_NOTE_THEME_ID, THEME_NOTE_INDEX, THEME_NOTE_IN_ID,
                THEME_NOTE_THEME_ID, THEME, THEME_ID,
                THEME_NOTE_IN_ID, NOTE, NOTE_ID));

        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s(" +
                        "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "%s INTEGER NOT NULL, " +
                        "%s INTEGER NOT NULL, " +
                        "%s INTEGER NOT NULL, " +
                        "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE, " +
                        "FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE CASCADE" +
                        ")", THEME_INTO, THEME_INTO_ID, THEME_INTO_THEME_ID, THEME_INTO_INDEX, THEME_INTO_IN_ID,
                THEME_INTO_THEME_ID, THEME, THEME_ID,
                THEME_INTO_IN_ID, THEME, THEME_ID));
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ADDITIONAL_DATA);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + THEME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOTE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + THEMES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + THEME_NOTE);
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
                            System.out.println(printCursor.getString(printCursor.getColumnIndexOrThrow(APP_THEME)));
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
                                    printCursor.getInt(printCursor.getColumnIndexOrThrow(THEME_ID)) + "\t" +
                                    printCursor.getString(printCursor.getColumnIndexOrThrow(THEME_TITLE))
                            );
                        }
                        printCursor.close();
                    }
                    break;
                case NOTE:
                    printCursor = getDatabase(context).rawQuery("SELECT * FROM " + NOTE + " ORDER BY " + NOTE_ID, null);
                    System.out.println(NOTE + ":");
                    System.out.println(NOTE_ID + "\t" + NOTE_TITLE + "\t " + NOTE_TEXT);
                    if (printCursor != null) {
                        while (printCursor.moveToNext()) {
                            System.out.println(
                                    printCursor.getInt(printCursor.getColumnIndexOrThrow(NOTE_ID)) + "\t" +
                                            printCursor.getString(printCursor.getColumnIndexOrThrow(NOTE_TITLE)) + "\t" +
                                            printCursor.getString(printCursor.getColumnIndexOrThrow(NOTE_TEXT))
                            );
                        }
                        printCursor.close();
                    }
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
                case THEME_NOTE:
                    printCursor = getDatabase(context).rawQuery("SELECT * FROM " + THEME_NOTE + " ORDER BY " + THEME_NOTE_ID, null);
                    System.out.println(THEME_NOTE + ":");
                    System.out.println(THEME_NOTE_ID + "\t" + THEME_NOTE_THEME_ID + "\t" + THEME_NOTE_INDEX + "\t" + THEME_NOTE_IN_ID);
                    if (printCursor != null) {
                        while (printCursor.moveToNext()) {
                            System.out.println(
                                    printCursor.getInt(printCursor.getColumnIndexOrThrow(THEME_NOTE_ID)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(THEME_NOTE_THEME_ID)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(THEME_NOTE_INDEX)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(THEME_NOTE_IN_ID))
                            );
                        }
                        printCursor.close();
                    }
                    break;
                case THEME_INTO:
                    printCursor = getDatabase(context).rawQuery("SELECT * FROM " + THEME_INTO + " ORDER BY " + THEME_INTO_ID, null);
                    System.out.println(THEME_INTO + ":");
                    System.out.println(THEME_INTO_ID + "\t" + THEME_INTO_THEME_ID + "\t" + THEME_INTO_INDEX + "\t" + THEME_INTO_IN_ID);
                    if (printCursor != null) {
                        while (printCursor.moveToNext()) {
                            System.out.println(
                                    printCursor.getInt(printCursor.getColumnIndexOrThrow(THEME_INTO_ID)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(THEME_INTO_THEME_ID)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(THEME_INTO_INDEX)) + "\t" +
                                            printCursor.getInt(printCursor.getColumnIndexOrThrow(THEME_INTO_IN_ID))
                            );
                        }
                        printCursor.close();
                    }
                    break;
            }
        } else {
            printTable(ADDITIONAL_DATA, context);
            printTable(THEME, context);
            printTable(NOTE, context);
            printTable(THEMES, context);
            printTable(THEME_NOTE, context);
            printTable(THEME_INTO, context);
            System.out.println(".");
        }
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
