module se233.projectadpro {
    requires javafx.controls;
    requires javafx.fxml;

    opens se233.projectadpro to javafx.fxml;
    exports se233.projectadpro;

    opens  se233.projectadpro.controller to javafx.fxml;
    exports se233.projectadpro.controller;
}