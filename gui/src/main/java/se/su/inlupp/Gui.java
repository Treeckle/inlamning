package se.su.inlupp;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Gui extends Application {

  public void start(Stage stage) {
    Graph<String> graph = new ListGraph<String>();
    stage.setResizable(false);

    /*String javaVersion = System.getProperty("java.version");
    String javafxVersion = System.getProperty("javafx.version");
    Label label =
        new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");

    VBox root = new VBox(30, label);
    root.setAlignment(Pos.CENTER);
    Scene scene = new Scene(root, 640, 480);
    stage.setScene(scene);
    stage.show();*/


    BorderPane root = new BorderPane();
    VBox barHolder = new VBox();
    Pane mapHolder = new Pane();
    //menu setup
    MenuBar menuBar = new MenuBar();
    Menu menu = new Menu("File");
    Menu maps = new Menu("New Map");
    MenuItem open = new MenuItem("Open");
    MenuItem save = new MenuItem("Save");
    MenuItem saveImage = new MenuItem("Save Image");
    MenuItem exit = new MenuItem("Exit");

    MenuItem customMap = new MenuItem("Custom Map");
    MenuItem skyrim = new MenuItem("Skyrim");
    MenuItem borderlands2 = new MenuItem("Borderlands 2");

    Image skyrimMap = new Image("file:../assets/maps/templates/skyrimMap.png");
    Image borderlands2Map = new Image("file:../assets/maps/templates/borderlands2Map.png");

    HashMap<MenuItem, Image> templates = new HashMap<MenuItem, Image>();
    templates.putIfAbsent(skyrim, skyrimMap);
    templates.putIfAbsent(borderlands2, borderlands2Map);


    menu.getItems().addAll(maps, open, save, saveImage, exit);
    maps.getItems().addAll(skyrim, borderlands2);
    menuBar.getMenus().add(menu);

    //function for menu buttons

    for(MenuItem m : templates.keySet() ) {
      m.setOnAction(event -> {
        mapHolder.setBackground(new Background(new BackgroundImage(templates.get(m), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        mapHolder.setPrefSize(templates.get(m).getWidth(), templates.get(m).getHeight());
        System.out.println("joe mama");
        stage.sizeToScene();
      });
    }


    //toolbar setup
    ToolBar toolBar = new ToolBar();
    Button findPath = new Button("Find Path");
    Button showConnection = new Button("Show Connection");
    Button newPlace = new Button("New Place");
    Button newConnection = new Button("New Connection");
    Button changeConnection = new Button("Change Connection");
    toolBar.getItems().addAll(findPath, showConnection, newPlace, newConnection, changeConnection);

    //footer setup
    HBox footer = new HBox();
    Label action = new Label("Actions from the user will potentially be displayed here");

    //set up the full borderPane
    barHolder.getChildren().addAll(menuBar, toolBar);
    root.setTop(barHolder);
    root.setCenter(mapHolder);
    root.setBottom(action);
    stage.setTitle("Test title <3 uwu o3o owo");
    Scene scene = new Scene(root);
    stage.sizeToScene();
    stage.setScene(scene);
    stage.show();
    System.out.println("Working directory: " + System.getProperty("user.dir"));



  }

  public static void main(String[] args) {
    launch(args);
  }
}
