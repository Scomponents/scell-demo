package org.example.scelldemo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.scelldemo.controls.ScellWrapper;
import org.example.scelldemo.controls.factory.ButtonsFactory;
import org.example.scelldemo.controls.factory.ToolbarsFactory;
import org.example.scelldemo.controls.helper.FsXlsxWatcher;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        List<String> cmdArgs = this.getCommandLineParams();
        boolean addExtraControls = cmdArgs.contains("--desktop");

        BorderPane root = new BorderPane();
        root.setVisible(false);

        ScellWrapper scell = new ScellWrapper(!addExtraControls);

        scell.getScellControlFuture().thenAccept(scellControl -> {
            root.setCenter(scellControl);
            root.setStyle("-fx-background-color: #28a87d;");
            root.setVisible(true);

            watchForAutoOpen(scell, cmdArgs);
        });

        ButtonsFactory buttonsFactory = new ButtonsFactory(scell, primaryStage, App.class.getClassLoader());
        ToolbarsFactory toolbarsFactory = new ToolbarsFactory(buttonsFactory, addExtraControls);

        root.setTop(toolbarsFactory.createTopToolbar());
        root.setLeft(toolbarsFactory.createLeftToolbar());

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private List<String> getCommandLineParams() {
        Parameters parameters = this.getParameters();
        if (parameters == null) {
            return new ArrayList<>();
        }

        return parameters.getRaw();
    }

    private void watchForAutoOpen(ScellWrapper scell, List<String> args) {
        Path watchDirPath = args.stream().filter(arg -> arg.startsWith("path="))
                .map(s -> Path.of(s.replace("path=", "")))
                .findFirst().orElse(null);

        if (watchDirPath != null && Files.exists(watchDirPath)) {
            CompletableFuture.runAsync(() -> FsXlsxWatcher.watchDirectory(watchDirPath, newFile -> {
                scell.loadSpreadsheet(newFile.toFile());
            }));
        }
    }
}
