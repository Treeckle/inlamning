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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.util.*;

public class Gui extends Application {

  private boolean placingLocation = false;
  private final int nodeRadius = 10;
  private final int connectionWidth = 2;
  private HashMap<Circle, Location> locations = new HashMap<>();
  private ArrayList<Circle> selectedPlaces = new ArrayList<>();

  public void start(Stage stage) {
    Graph<Location> graph = new ListGraph<Location>();
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
    maps.getItems().addAll(customMap, skyrim, borderlands2);
    menuBar.getMenus().add(menu);

    //function for menu buttons

    for(MenuItem m : templates.keySet() ) {
      m.setOnAction(event -> {
        mapHolder.setBackground(new Background(new BackgroundImage(templates.get(m), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        mapHolder.setPrefSize(templates.get(m).getWidth(), templates.get(m).getHeight());
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

    //addPlace

    newPlace.setOnAction(event -> {
      placingLocation = true;
    });

    //sätt ut plats på klickade kordinater
    mapHolder.setOnMouseClicked(event -> {
      if(placingLocation) {
        placingLocation = false;
        TextInputDialog nameInput = new TextInputDialog();
        nameInput.setTitle("Name Input");
        nameInput.setContentText("Enter a name for the place");
        Optional<String> result = nameInput.showAndWait();
        if(result.isEmpty()) return;
        String name = nameInput.getResult();
        Location place = new Location(name, event.getX(), event.getY());
        graph.add(place);
        System.out.println(graph.getNodes());
        Circle circle = new Circle(event.getX(), event.getY(), nodeRadius, Color.BLUE);
        locations.putIfAbsent(circle, place);

        circle.setOnMouseClicked(mouseEvent -> {
        selectCircle(circle);
        });
        mapHolder.getChildren().add(circle);
      }
    });

        newConnection.setOnAction(event -> {
          if(selectedPlaces.size() < 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Please select at least two places");
            alert.showAndWait();
            return;
          }
          Location place1 = locations.get(selectedPlaces.get(0));
          Location place2 = locations.get(selectedPlaces.get(1));

          String name = "joe";

          int weight = 5;
          graph.connect(locations.get(selectedPlaces.get(0)), locations.get(selectedPlaces.get(1)), name, weight);

          Line connection = new Line(place1.getXPos(), place1.getYPos(), place2.getXPos(), place2.getYPos());
          connection.setStrokeWidth(connectionWidth);
          mapHolder.getChildren().add(connection);
          connection.toBack();

        });
  }
  private void selectCircle(Circle c){
    if (selectedPlaces.contains(c)) {
      c.setFill(Color.BLUE);
      selectedPlaces.remove(c);
    }
    else if(selectedPlaces.size()<2) {
      c.setFill(Color.RED);
      selectedPlaces.add(c);
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
