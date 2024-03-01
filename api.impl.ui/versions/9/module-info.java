module api.impl.ui {
    requires jdk.unsupported;

    requires transitive api.impl.core;
    requires transitive api.interfaces.core;
    requires transitive api.interfaces.ui;
    requires transitive java.desktop;

    exports com.intechcore.scell.common;
    exports com.intechcore.scell.common.awt;
    exports com.intechcore.scell.common.fx.control.helper;
    exports com.intechcore.scell.integration.css;
    exports com.intechcore.scell.integration.font;
    exports com.intechcore.scell.integration.input;
    exports com.intechcore.scomponents.scell.api.impl.ui;
    exports com.intechcore.scomponents.scell.api.impl.ui.events;
    exports com.intechcore.scomponents.scell.api.init;
    exports intechcore.scell.common.fx.behavior;
    exports intechcore.scell.common.fx.control;
    exports intechcore.scell.common.fx.helper;

    provides com.intechcore.scomponents.scell.api.init.UiModule with
        com.intechcore.scomponents.scell.api.init.ScellFxModule;

}
