package org.example.scelldemo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.scelldemo.controls.ScellWrapper;
import org.example.scelldemo.controls.factory.ButtonsFactory;
import org.example.scelldemo.controls.factory.ToolbarsFactory;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        boolean addExtraControls = this.getCommandLineParams().contains("--desktop");

        BorderPane root = new BorderPane();
        root.setVisible(false);

        ScellWrapper scell = new ScellWrapper(!addExtraControls);

        scell.getScellControlFuture().thenAccept(scellControl -> {
            root.setCenter(scellControl);
            root.setStyle("-fx-background-color: #28a87d;");
            root.setVisible(true);
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
}
