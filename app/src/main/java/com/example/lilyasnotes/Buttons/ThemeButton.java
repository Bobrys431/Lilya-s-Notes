package com.example.lilyasnotes.Buttons;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Data.ViewHolders.AbstractViewHolder;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class ThemeButton {

    AbstractActivity activity;

    public ThemeButton(AbstractActivity activity) {
        this.activity = activity;
    }

    public void changeAppTheme() {
        SQLiteDatabaseAdapter.getDatabase(activity).execSQL("UPDATE " + SQLiteDatabaseAdapter.ADDITIONAL_DATA + " SET " + SQLiteDatabaseAdapter.APP_THEME + " = CASE WHEN " + SQLiteDatabaseAdapter.APP_THEME + " = 'LIGHT' THEN 'DARK' ELSE 'LIGHT' END");
        changeByAppTheme();
    }

    public void changeByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        changeWindowColorByAppTheme(appTheme);
        changeActionBarColorByAppTheme(appTheme);
        changeStatusBarColorByAppTheme(appTheme);
        changeAllViewHoldersColorByAppTheme();
        changeSearchBarColorByAppTheme();
        changeEmergentWidgetByAppTheme();
        changeUndoEraseWidgetColorByAppTheme();
    }

    private void changeWindowColorByAppTheme(String appTheme) {
        activity.getWindow().setBackgroundDrawableResource(
                appTheme.equals("light") ?
                        R.color.lightThemeActiveColor :
                        R.color.darkThemeActiveColor);
    }

    private void changeActionBarColorByAppTheme(String appTheme) {
        activity.getActionBarLayout().setBackgroundResource(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground);
    }

    private void changeStatusBarColorByAppTheme(String appTheme) {
        activity.getWindow().setStatusBarColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground));
    }

    private void changeAllViewHoldersColorByAppTheme() {
        for (int i = 0; i < activity.getAdapter().getItemCount(); i++) {
            AbstractViewHolder holder = (AbstractViewHolder) activity.getDataListView().findViewHolderForAdapterPosition(i);

            if (holder != null) {
                holder.changeColorByAppTheme();
            }
        }
    }

    private void changeSearchBarColorByAppTheme() {
        activity.getSearchBar().changeColorByAppTheme();
    }

    private void changeEmergentWidgetByAppTheme() {
        activity.getEmergentWidget().changeByAppTheme();
    }

    private void changeUndoEraseWidgetColorByAppTheme() {
        activity.getUndoEraseWidget().changeColorByAppTheme();
    }
}
