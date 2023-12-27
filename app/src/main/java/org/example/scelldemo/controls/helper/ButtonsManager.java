package org.example.scelldemo.controls.helper;

import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;

public final class ButtonsManager {
    private final List<Button> buttons = new ArrayList<>();

    public void add(Button button) {
        this.buttons.add(button);
    }

    public void setDisable(boolean value) {
        this.buttons.forEach(button -> button.setDisable(value));
    }
}
