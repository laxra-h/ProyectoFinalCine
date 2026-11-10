module untitled {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires org.junit.jupiter.api;

    opens org.example to javafx.fxml;
    opens org.example.viewController to javafx.fxml;
    opens org.example.model;

    exports org.example;
    opens org.example.model.usuarios;
    opens org.example.model.pelicula;
    opens org.example.model.compra;
}