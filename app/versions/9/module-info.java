module app {
    requires api.interfaces.core;
    requires api.interfaces.ui;

    exports org.example.scelldemo;
    exports org.example.scelldemo.controls;
    exports org.example.scelldemo.controls.factory;
    exports org.example.scelldemo.controls.helper;

}
