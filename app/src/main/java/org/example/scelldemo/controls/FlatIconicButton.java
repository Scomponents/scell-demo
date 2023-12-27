package org.example.scelldemo.controls;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FlatIconicButton extends Button {
    private static final int WIDTH = 30;
    private static final int HEIGHT = 30;
    private static final String STYLE =
                    "-fx-background-color: #3498db; "
                    + "-fx-padding: 5px 5px; "
                    + "-fx-background-radius: 0; "
                    + "-fx-background-color: rgba(0, 0, 0, 0);";
    private static final String STYLE_HIGHLIGHTED =
                    "-fx-background-color: #3498db; "
                    + "-fx-padding: 5px 5px; "
                    + "-fx-background-radius: 0; "
                    + "-fx-background-color: rgba(255, 255, 255, 0.3);";

    public FlatIconicButton(String iconPath) {
        Image image = new Image(iconPath);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(WIDTH);
        imageView.setFitWidth(HEIGHT);
        super.setGraphic(imageView);
        super.setStyle(STYLE);
        super.setOnMouseEntered(e -> super.setStyle(STYLE_HIGHLIGHTED));
        super.setOnMouseExited(e -> super.setStyle(STYLE));
    }
}
