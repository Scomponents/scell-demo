package org.example.scelldemo.controls.factory;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public final class ToolbarsFactory {
    private final ButtonsFactory buttonsFactory;
    private final boolean addExtraControls;

    public ToolbarsFactory(ButtonsFactory buttonsFactory, boolean addExtraControls) {
        this.buttonsFactory = buttonsFactory;
        this.addExtraControls = addExtraControls;
    }

    public Pane createTopToolbar() {
        HBox toolbar = new HBox();
        toolbar.setSpacing(5);
        toolbar.getChildren().add(this.buttonsFactory.createCreateNewButton());
        if (this.addExtraControls) {
            toolbar.getChildren().add(this.buttonsFactory.createOpenButton());
            toolbar.getChildren().add(this.buttonsFactory.createUndoButton());
            toolbar.getChildren().add(this.buttonsFactory.createRedoButton());
            toolbar.getChildren().add(this.buttonsFactory.createSaveButton());
        }

        BorderPane result = new BorderPane();
        result.setStyle("-fx-padding: 5px 5px;");
        result.setLeft(toolbar);
        result.setRight(this.buttonsFactory.createAboutButton());
        result.setCenter(this.buttonsFactory.createSearchControlsPanel());

        return result;
    }

    public Pane createLeftToolbar() {
        BorderPane result = new BorderPane();
        result.setStyle("-fx-padding: 5px 5px;");

        VBox presets = new VBox();
        presets.setSpacing(5);
        this.buttonsFactory.createOpenPreloadedFilesButtons().forEach(button -> presets.getChildren().add(button));
        result.setTop(presets);

        Image image = new Image("icons/logotype_256.png");
        ImageView logotype = new ImageView(image);
        logotype.setFitWidth(40);
        logotype.setFitHeight(40);
        logotype.setStyle("-fx-translate-y: -10;");
        result.setBottom(logotype);

        return result;
    }
}
