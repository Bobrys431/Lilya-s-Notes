package com.example.lilyasnotes.Buttons.ButtonManagers;

import com.example.lilyasnotes.Buttons.DTO.Button;

import java.util.ArrayList;
import java.util.List;

public abstract class ButtonsManager {

    protected List<Button> buttons;

    protected ButtonsManager() {
        buttons = new ArrayList<>();
    }

    public void hide(Class<? extends Button> buttonType) {
        boolean isButtonFounded = false;

        for (Button button : buttons) {
            if (buttonType.isInstance(button) && button.isActive()) {
                button.hide();
                isButtonFounded = true;
                continue;
            }

            if (isButtonFounded) {
                button.up();
            }
        }
    }

    public void show(Class<? extends Button> buttonType) {
        boolean isButtonFounded = false;

        for (Button button : buttons) {
            if (buttonType.isInstance(button) && !button.isActive()) {
                button.show();
                isButtonFounded = true;
                continue;
            }

            if (isButtonFounded) {
                button.down();
            }
        }
    }

    public abstract void updateButtonsDisplay();

    public abstract void addAndSetupButtonsByType(Class<? extends Button>... buttonsTypes);

    public List<Button> getButtons() {
        return buttons;
    }
}