package org.example.scelldemo.controls.factory;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.scelldemo.controls.FlatIconicButton;
import org.example.scelldemo.controls.ScellWrapper;
import org.example.scelldemo.controls.helper.ButtonsManager;
import org.example.scelldemo.controls.helper.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ButtonsFactory {
    private static final HashMap<String, String> openButtonsIconsToFilesMap = new HashMap<String, String>() {{
        put("dollar.png", "Formats.xlsx");
        put("bar_chart.png", "Travel Budget.xlsx");
        put("flag_01.png", "Example1.xlsx");
        put("line_chart.png", "Climate data for Barcelona.xlsx");
    }};

    private final ScellWrapper scell;
    private final ButtonsManager buttonsManager;
    private final Stage primaryStage;
    private final FileChooser fileChooser;
    private final ClassLoader resourceLoader;

    public ButtonsFactory(ScellWrapper scell, Stage primaryStage, ClassLoader resourceLoader) {
        this.scell = scell;
        this.primaryStage = primaryStage;
        this.resourceLoader = resourceLoader;

        this.buttonsManager = new ButtonsManager();
        this.fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter =
                new FileChooser.ExtensionFilter("SpreadSheet files (*.xlsx)", "*.xlsx");
        this.fileChooser.getExtensionFilters().add(extensionFilter);
    }

    public Button createCreateNewButton() {
        Button result = new FlatIconicButton("icons/add.png");
        result.setOnAction(actionEvent -> this.scell.createNew());
        this.buttonsManager.add(result);
        return result;
    }

    public Button createOpenButton() {
        Button result = new FlatIconicButton("icons/upload.png");
        result.setOnAction(actionEvent -> {
            this.fileChooser.setTitle("Open file");
            this.buttonsManager.setDisable(true);
            this.scell.loadSpreadsheet(this.fileChooser.showOpenDialog(this.primaryStage)).thenRun(() ->
                    this.buttonsManager.setDisable(false));
        });
        this.buttonsManager.add(result);
        return result;
    }

    public Button createUndoButton() {
        Button result = new FlatIconicButton("icons/undo.png");
        result.setOnAction(actionEvent -> this.scell.undo());
        this.buttonsManager.add(result);
        return result;
    }

    public Button createRedoButton() {
        Button result = new FlatIconicButton("icons/redo.png");
        result.setOnAction(actionEvent -> this.scell.redo());
        this.buttonsManager.add(result);
        return result;
    }

    public Button createSaveButton() {
        Button result = new FlatIconicButton("icons/save.png");
        result.setOnAction(actionEvent -> {
            this.buttonsManager.setDisable(true);
            this.fileChooser.setTitle("Save file");
            this.fileChooser.setInitialFileName("default.xlsx");
            this.scell.saveAs(this.fileChooser.showSaveDialog(this.primaryStage)).whenComplete((e, throwable) ->
                    this.buttonsManager.setDisable(false));
        });
        this.buttonsManager.add(result);
        return result;
    }

    public Button createAboutButton() {
        Button result = new FlatIconicButton("icons/help.png");
        result.setOnAction(actionEvent -> this.showAboutDialog());
        return result;
    }

    public Pane createSearchControlsPanel() {
        TextField patternInput = new TextField();
        patternInput.setStyle("-fx-background-radius: 0;");
        patternInput.setMaxHeight(0.01);
        FlatIconicButton searchForwardButton = new FlatIconicButton("icons/looking_glass.png");
        this.buttonsManager.add(searchForwardButton);

        HBox result = new HBox();
        result.setAlignment(Pos.CENTER_RIGHT);
        result.setSpacing(5);
        result.getChildren().addAll(patternInput, searchForwardButton);
        this.scell.getScellControlFuture().thenAccept(node -> {
            if (node instanceof Label) {
                return;
            }

            this.scell.addSearchActions(patternInput, searchForwardButton, this.primaryStage.getScene().getWindow());
        });

        return result;
    }

    public List<Button> createOpenPreloadedFilesButtons() {
        List<Button> result = new ArrayList<>();
        openButtonsIconsToFilesMap.forEach((iconFileName, xlsxFileName) -> {
            Button openFileButton = new FlatIconicButton("icons/" + iconFileName);
            openFileButton.setOnAction(e -> this.loadPreset("presets/" + xlsxFileName));
            this.buttonsManager.add(openFileButton);
            result.add(openFileButton);
        });

        return result;
    }

    private void showAboutDialog() {
        Alert aboutDialog = new Alert(Alert.AlertType.INFORMATION);
        aboutDialog.setTitle("About");
        aboutDialog.initOwner(this.primaryStage.getScene().getWindow());
        aboutDialog.setHeaderText(this.scell.getApiVersionsInfo());
        aboutDialog.setContentText(this.scell.getPlatformVersionsInfo());
        aboutDialog.showAndWait();
    }

    private void loadPreset(String resourcePath) {
        this.buttonsManager.setDisable(true);
        try (InputStream stream = this.resourceLoader.getResourceAsStream(resourcePath)) {
            Utils.withErrorHandling(
                    this.scell.loadSpreadsheet(stream, resourcePath),
                    this.primaryStage.getScene().getWindow())
                    .whenComplete((unused, throwable) -> this.buttonsManager.setDisable(false));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
