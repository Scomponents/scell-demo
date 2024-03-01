module org.example.scelldemo {
    requires org.apache.logging.log4j;
    requires javafx.controls;
    requires javafx.fxml;
    requires api.interfaces.core;
    requires api.interfaces.ui;
//    requires static api.impl.core;
//    requires static api.impl.ui;

    exports org.example.scelldemo;
    opens org.example.scelldemo to javafx.fxml;
}
