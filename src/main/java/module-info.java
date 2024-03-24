module org.moldidev {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens org.moldidev.main to javafx.fxml;
    exports org.moldidev.main;
    exports org.moldidev.controller;
    opens org.moldidev.controller to javafx.fxml;
}