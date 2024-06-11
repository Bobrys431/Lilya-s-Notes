package com.example.lilyasnotes.Buttons.ThemeButtons;

import android.view.Window;

import com.example.lilyasnotes.Activities.ThemeActivity;
import com.example.lilyasnotes.Data.ViewHolders.DataView;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;

public class ThemeThemeButton extends ThemeButton {

    private final ThemeActivity activity;

    public ThemeThemeButton(ThemeActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void changeAllViewsByAppTheme() {
        String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        changeWindowSettingsByAppTheme(appTheme);
        changeActionBarLayoutByAppTheme(appTheme);
        changeDataViewsByAppTheme();
        changeSearchBarByAppTheme();
        changeEmergentWidgetByAppTheme();
        changeUndoEraseWidgetByAppTheme();
    }

    private void changeWindowSettingsByAppTheme(String appTheme) {
        Window window = activity.getWindow();

        window.setStatusBarColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground));

        window.getDecorView().setBackgroundColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeAdditionalColor :
                        R.color.darkThemeAdditionalColor));

        window.setNavigationBarColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeBackground :
                        R.color.darkThemeBackground));
    }

    private void changeActionBarLayoutByAppTheme(String appTheme) {
        activity.getActionBarLayout().setBackgroundColor(activity.getColor(
                appTheme.equals("light") ?
                        R.color.lightThemeAdditionalColor :
                        R.color.darkThemeAdditionalColor));
    }

    private void changeDataViewsByAppTheme() {
        for (int i = 0; i < activity.getAdapter().getItemCount(); i++) {
            DataView holder = (DataView) activity.getDataListView().findViewHolderForAdapterPosition(i);
            if (holder != null) holder.changeColorByAppTheme();
        }
    }

    private void changeSearchBarByAppTheme() {
        activity.getSearchBar().changeColorByAppTheme();
    }

    private void changeEmergentWidgetByAppTheme() {
        activity.getEmergentWidget().changeByAppTheme();
    }

    private void changeUndoEraseWidgetByAppTheme() {
        activity.getUndoEraseWidget().changeColorByAppTheme();
    }
}
