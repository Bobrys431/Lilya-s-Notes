package com.example.lilyasnotes.Data.ViewHolders;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.example.lilyasnotes.Activities.AbstractActivity;
import com.example.lilyasnotes.Buttons.AddButton;
import com.example.lilyasnotes.Buttons.EraseButton;
import com.example.lilyasnotes.DatabaseManagement.SQLiteDatabaseAdapter;
import com.example.lilyasnotes.R;
import com.example.lilyasnotes.Utilities.Tools;

public class FooterViewHolder extends AbstractViewHolder {

    private final AddButton addButton;
    private final EraseButton eraseButton;

    private final RelativeLayout itemsManagementFrame;
    private final RelativeLayout addButtonFrame;
    private final ImageView addIcon;
    private final ImageView addMark;
    private final RelativeLayout deleteButtonFrame;
    private final ImageView deleteIcon;
    private final ImageView deleteMark;

    public FooterViewHolder(@NonNull View itemView) {
        super(itemView);

        itemsManagementFrame = itemView.findViewById(R.id.items_management_frame);
        addButtonFrame = itemView.findViewById(R.id.add_button_frame);
        addIcon = itemView.findViewById(R.id.add_icon);
        addMark = itemView.findViewById(R.id.add_mark);
        deleteButtonFrame = itemView.findViewById(R.id.delete_button_frame);
        deleteIcon = itemView.findViewById(R.id.delete_icon);
        deleteMark = itemView.findViewById(R.id.delete_mark);

        addButton = new AddButton(addButtonFrame);
        eraseButton = new EraseButton(deleteButtonFrame);
    }

    @Override
    public void setup(int id, AbstractActivity activity) {
        this.activity = activity;

        setupAddButton();
        setupEraseButton();

        changeColorByAppTheme();
    }

    private void setupAddButton() {
        addButton.setup(activity, (view) -> {
            ValueAnimator markDown = ValueAnimator.ofInt(addButtonFrame.getHeight(), (int) (Tools.getDensity(activity) * 78.75f));
            markDown.setDuration(100);
            markDown.addUpdateListener(valueAnimator -> {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) addButtonFrame.getLayoutParams();
                params.height = (int) valueAnimator.getAnimatedValue();
                addButtonFrame.setLayoutParams(params);
            });

            ValueAnimator markUp = ValueAnimator.ofInt((int) (Tools.getDensity(activity) * 78.75f), (int) (Tools.getDensity(activity) * 56.25f));
            markUp.setDuration(100);
            markUp.addUpdateListener(valueAnimator -> {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) addButtonFrame.getLayoutParams();
                params.height = (int) valueAnimator.getAnimatedValue();
                addButtonFrame.setLayoutParams(params);
            });

            markDown.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {}

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    markUp.start();
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {}

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {}
            });
            markDown.start();
        });
    }

    private void setupEraseButton() {
        eraseButton.setup(activity, (view) -> {
            if (activity.eraseMode) {
                ValueAnimator markDown = ValueAnimator.ofInt(deleteButtonFrame.getHeight(), (int) (Tools.getDensity(activity) * 78.75f));
                markDown.setDuration(100);
                markDown.addUpdateListener(valueAnimator -> {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) deleteButtonFrame.getLayoutParams();
                    params.height = (int) valueAnimator.getAnimatedValue();
                    deleteButtonFrame.setLayoutParams(params);
                });
                markDown.start();

            } else {
                ValueAnimator markUp = ValueAnimator.ofInt((int) (Tools.getDensity(activity) * 78.75f), (int) (Tools.getDensity(activity) * 56.25f));
                markUp.setDuration(100);
                markUp.addUpdateListener(valueAnimator -> {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) deleteButtonFrame.getLayoutParams();
                    params.height = (int) valueAnimator.getAnimatedValue();
                    deleteButtonFrame.setLayoutParams(params);
                });
                markUp.start();
            }
        });
    }

    @Override
    public void changeColorByAppTheme() {
        final String appTheme = SQLiteDatabaseAdapter.getCurrentAppTheme(activity);

        changeItemsManagementFrameColor(appTheme);
        changeAddButtonFrameColor(appTheme);
        changeAddIconColor(appTheme);
        changeAddMarkColor(appTheme);
        changeDeleteButtonFrameColor(appTheme);
        changeDeleteIconColor(appTheme);
        changeDeleteMarkColor(appTheme);
    }

    @SuppressLint("DiscouragedApi")
    private void changeItemsManagementFrameColor(String appTheme) {
        itemsManagementFrame.setBackgroundResource(activity.getResources().getIdentifier(
                "items_management_frame_" + appTheme,
                "drawable",
                activity.getPackageName()
        ));
    }

    @SuppressLint("DiscouragedApi")
    private void changeAddButtonFrameColor(String appTheme) {
        addButtonFrame.setBackgroundResource(activity.getResources().getIdentifier(
                "items_management_button_" + appTheme,
                "drawable",
                activity.getPackageName()
        ));
    }

    @SuppressLint("DiscouragedApi")
    private void changeAddIconColor(String appTheme) {
        addIcon.setBackgroundResource(activity.getResources().getIdentifier(
                "add_" + appTheme,
                "drawable",
                activity.getPackageName()
        ));
    }

    @SuppressLint("DiscouragedApi")
    private void changeAddMarkColor(String appTheme ) {
        addMark.setBackgroundResource(activity.getResources().getIdentifier(
                "mark_down_" + appTheme,
                "drawable",
                activity.getPackageName()
        ));
    }

    @SuppressLint("DiscouragedApi")
    private void changeDeleteButtonFrameColor(String appTheme) {
        deleteButtonFrame.setBackgroundResource(activity.getResources().getIdentifier(
                "items_management_button_" + appTheme,
                "drawable",
                activity.getPackageName()
        ));
    }

    @SuppressLint("DiscouragedApi")
    private void changeDeleteIconColor(String appTheme) {
        deleteIcon.setBackgroundResource(activity.getResources().getIdentifier(
                "delete_" + appTheme,
                "drawable",
                activity.getPackageName()
        ));
    }

    @SuppressLint("DiscouragedApi")
    private void changeDeleteMarkColor(String appTheme) {
        deleteMark.setBackgroundResource(activity.getResources().getIdentifier(
                "mark_down_" + appTheme,
                "drawable",
                activity.getPackageName()
        ));
    }
}
