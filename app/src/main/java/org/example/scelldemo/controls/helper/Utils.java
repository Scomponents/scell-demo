package org.example.scelldemo.controls.helper;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CompletableFuture;

public final class Utils {
    public static CompletableFuture<Void> withErrorHandling(CompletableFuture<Void> source, Window parent) {
        return source.whenCompleteAsync((unused, throwable) -> {
            if ((throwable != null) && !(throwable.getCause() instanceof NullPointerException)) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);

                Alert alert = new Alert(Alert.AlertType.ERROR, throwable.getMessage(), ButtonType.CLOSE);
                alert.initOwner(parent);
                TextArea textArea = new TextArea(sw.toString());
                textArea.setEditable(false);
                alert.getDialogPane().setExpandableContent(textArea);
                alert.setResizable(true);
                alert.showAndWait();
            }
        }, Platform::runLater);
    }
}
