module untitled {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;

    opens org.example to javafx.fxml;
    opens org.example.viewController to javafx.fxml;
    opens org.example.model;
    opens org.example.model.usuarios;
    opens org.example.model.pelicula;
    opens org.example.model.compra;
    opens org.example.model.compra.estadoCompra;
    opens org.example.model.compra.estrategiaReembolso;
    opens org.example.model.consumibleComposite;
    opens org.example.model.funcionDecorator;
    opens org.example.model.salaPrototype;
    opens org.example.model.observer;
    opens org.example.model.tarjeta;
    opens org.example.model.promocion;
    opens org.example.model.reporteAdapter;

    exports org.example;
    exports org.example.controller;
    exports org.example.services;
    exports org.example.exceptions;
    exports org.example.model;
    exports org.example.model.usuarios;
    exports org.example.model.pelicula;
    exports org.example.model.compra;
    exports org.example.model.compra.estadoCompra;
    exports org.example.model.compra.estrategiaReembolso;
    exports org.example.model.consumibleComposite;
    exports org.example.model.funcionDecorator;
    exports org.example.model.salaPrototype;
    exports org.example.model.observer;
    exports org.example.model.tarjeta;
    exports org.example.model.promocion;
    exports org.example.model.reporteAdapter;
}
