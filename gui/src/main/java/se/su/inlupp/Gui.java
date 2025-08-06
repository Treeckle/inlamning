package se.su.inlupp;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Gui extends Application {

  public void start(Stage stage) {
    Graph<String> graph = new ListGraph<String>();
    /*String javaVersion = System.getProperty("java.version");
    String javafxVersion = System.getProperty("javafx.version");
    Label label =
        new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");

    VBox root = new VBox(30, label);
    root.setAlignment(Pos.CENTER);
    Scene scene = new Scene(root, 640, 480);
    stage.setScene(scene);
    stage.show();*/

    stage.setTitle("Test title <3 uwu o3o owo");
    Pane root = new Pane();
    Label nameLabel = new Label("Name: ");
    TextField nameField = new TextField();
    Button addButton = new Button("Add");
    Label output = new Label();


    root.getChildren().add(nameLabel);
    root.getChildren().add(nameField);
    root.getChildren().add(addButton);
    root.getChildren().add(output);

    output.textProperty().bind(nameField.textProperty());

    addButton.setOnAction(e -> {
      String name = nameField.getText();
      output.setText(name);
    });
    /*addButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent actionEvent) {
        String name = nameField.getText();
        output.setText("Hej " + name + "!" );
      }
    });*/


    Scene scene = new Scene(root, 300, 300);

    nameField.setPrefWidth(50);
    nameLabel.relocate(0, 100);
    nameField.relocate(100, 100);
    addButton.relocate(200, 100);
    output.relocate(150, 200);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
