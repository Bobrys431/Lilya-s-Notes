package com.example.lilyasnotes.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Database.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class Tools {
    public static float getDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getDataLength(Context context, int id) {
        SQLiteDatabase database = SQLiteDatabaseAdapter.getDatabase(context);

        Cursor themeCount = database.rawQuery("SELECT COUNT(" + SQLiteDatabaseAdapter.THEME_INTO_ID + ") AS count" +
                " FROM " + SQLiteDatabaseAdapter.THEME_INTO +
                " WHERE " + SQLiteDatabaseAdapter.THEME_INTO_THEME_ID + " = " + id, null);

        Cursor noteCount = database.rawQuery("SELECT COUNT(" + SQLiteDatabaseAdapter.THEME_NOTE_ID + ") AS count" +
                " FROM " + SQLiteDatabaseAdapter.THEME_NOTE +
                " WHERE " + SQLiteDatabaseAdapter.THEME_NOTE_THEME_ID + " = " + id, null);

        int count = 0;
        if (themeCount != null && noteCount != null) {
            if (themeCount.moveToFirst()) {
                count += themeCount.getInt(themeCount.getColumnIndexOrThrow("count"));
            }
            if (noteCount.moveToFirst()) {
                count += noteCount.getInt(noteCount.getColumnIndexOrThrow("count"));
            }
            themeCount.close();
            noteCount.close();
        }
        return count;
    }

    public static int[] getNoteWrapParams(ThemeActivity activity, String title, String note) {
        int[] params = new int[2];

        RelativeLayout titleFrame = activity.findViewById(R.id.title_frame_wrap);
        TextView titleView = activity.findViewById(R.id.title_wrap);

        titleView.setText(title);
        params[0] = titleFrame.getWidth();

        RelativeLayout noteFrame = activity.findViewById(R.id.note_frame_wrap);
        TextView noteView = activity.findViewById(R.id.note_wrap);

        noteView.setText(note);
        params[1] = noteFrame.getHeight();

        return params;
    }
}
