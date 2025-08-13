package se.su.inlupp;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Gui extends Application {

  private boolean placingLocation = false;
  private final int nodeRadius = 10;
  private final int connectionWidth = 2;
  private HashMap<Circle, Location> locations = new HashMap<>();
  private ArrayList<Circle> selectedPlaces = new ArrayList<>();
  Graph<Location> graph;
  Pane mapHolder;
  Button findPath;
  Button showConnection;
  Button newPlace;
  Button newConnection;
  Button changeConnection;

  public void start(Stage stage) {
    graph = new ListGraph<>();
    stage.setResizable(false);

    ButtonHandler buttonHandler = new ButtonHandler();

    BorderPane root = new BorderPane();
    VBox barHolder = new VBox();
    mapHolder = new Pane();

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

    Image customImage;
    Image skyrimMap = new Image("file:../assets/maps/templates/skyrimMap.png");
    Image borderlands2Map = new Image("file:../assets/maps/templates/borderlands2Map.png");

    HashMap<MenuItem, Image> templates = new HashMap<MenuItem, Image>();
    templates.putIfAbsent(skyrim, skyrimMap);
    templates.putIfAbsent(borderlands2, borderlands2Map);


    menu.getItems().addAll(maps, open, save, saveImage, exit);
    maps.getItems().addAll(customMap, skyrim, borderlands2);
    menuBar.getMenus().add(menu);

    //function for menu items
    for(MenuItem m : templates.keySet() ) {
      m.setOnAction(event -> {
        mapHolder.setBackground(new Background(new BackgroundImage(templates.get(m), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        mapHolder.setPrefSize(templates.get(m).getWidth(), templates.get(m).getHeight());
        stage.sizeToScene();
      });
    }
    customMap.setOnAction(event -> {
      FileChooser fileChooser = new FileChooser();
      File file = fileChooser.showOpenDialog(stage);
      if(file == null) return;
      Image image = new Image(file.toURI().toString());
      if (image.isError()) return;
      mapHolder.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
      mapHolder.setPrefSize(image.getWidth(), image.getHeight());
      stage.sizeToScene();
    });
    saveImage.setOnAction(event -> {
      try{
        WritableImage image = mapHolder.snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ImageIO.write(bufferedImage, "png", new File("capture.png"));
      } catch(IOException e){
        Alert alert = new Alert(Alert.AlertType.ERROR, "IO exception");
        alert.showAndWait();
      }



    });


    //toolbar setup
    ToolBar toolBar = new ToolBar();
    findPath = new Button("Find Path");
    showConnection = new Button("Show Connection");
    newPlace = new Button("New Place");
    newConnection = new Button("New Connection");
    changeConnection = new Button("Change Connection");
    toolBar.getItems().addAll(findPath, showConnection, newPlace, newConnection, changeConnection);

    findPath.setOnAction(buttonHandler);
    showConnection.setOnAction(buttonHandler);
    newPlace.setOnAction(buttonHandler);
    newConnection.setOnAction(buttonHandler);
    changeConnection.setOnAction(buttonHandler);

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


    //sätt ut plats på klickade kordinater om new place är vald
    mapHolder.setOnMouseClicked(event -> {
      if(placingLocation) {
        mapHolder.setCursor(Cursor.DEFAULT);
        placingLocation = false;
        newPlace.setDisable(false);

        TextInputDialog nameInput = new TextInputDialog();
        nameInput.setTitle("Name Input");
        nameInput.setContentText("Enter a name for the place");
        nameInput.showAndWait();
        if(nameInput.getResult().isEmpty()){
          error("No name entered");
          return;
        }

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
  }

  private class ButtonHandler implements EventHandler<ActionEvent>{
    public void handle(ActionEvent event) {

      //new place
      if (event.getSource() == newPlace){
          mapHolder.setCursor(Cursor.CROSSHAIR);
          placingLocation = true;
          newPlace.setDisable(true);

      }

      //new connection
      if(event.getSource() == newConnection){
        if(!checkSelectedPlaces()) return;
        ConnectionDialog connectionDialog = new ConnectionDialog();
        connectionDialog.showAndWait();
        if(connectionDialog.getResult() == null) return;
        String[] split = connectionDialog.getResult().split(";");
        connectPlaces(split[0], Integer.parseInt(split[1]));
        unselectAll();
      }

      //Change connection
      if(event.getSource() == changeConnection){
        if(!checkSelectedPlaces()) return;
        String connectionName = graph.getEdgeBetween(locations.get(selectedPlaces.get(0)), locations.get(selectedPlaces.get(1))).getName();
        ConnectionDialog connectionDialog = new ConnectionDialog(connectionName);
        connectionDialog.showAndWait();
        if(connectionDialog.getResult() == null) return;
        graph.setConnectionWeight(locations.get(selectedPlaces.get(0)), locations.get(selectedPlaces.get(1)), Integer.parseInt(connectionDialog.getResult()));
        unselectAll();
      }

      if(event.getSource() == showConnection){
        if(!checkSelectedPlaces()) return;
        Edge<Location> connection = graph.getEdgeBetween(locations.get(selectedPlaces.get(0)), locations.get(selectedPlaces.get(1)));
        ConnectionDialog connectionDialog = new ConnectionDialog(locations.get(selectedPlaces.get(0)).toString(),connection);
        connectionDialog.showAndWait();
        unselectAll();
      }

      if(event.getSource() == findPath){
        if(!checkSelectedPlaces()) return;
        List<Edge<Location>> path = graph.getPath(locations.get(selectedPlaces.get(0)), locations.get(selectedPlaces.get(1)));
        String pathString = "";
        int totalTime = 0;
        Alert pathAlert = new Alert(Alert.AlertType.INFORMATION);
        pathAlert.setHeaderText("The path from " + locations.get(selectedPlaces.get(0)).toString() + " to " + locations.get(selectedPlaces.get(1)).toString() + ":");
        for(Edge<Location> edge : path){
          totalTime += edge.getWeight();
          pathString += edge.toString() + "\n";
        }
        pathAlert.setContentText(pathString + "\nTotal time: " + totalTime);
        pathAlert.showAndWait();
        unselectAll();
      }
    }
  }
  private class menuHandler implements EventHandler<ActionEvent>{
    public void handle(ActionEvent event) {

    }
  }

  private void connectPlaces(String name, int weight){
    Location place1 = locations.get(selectedPlaces.get(0));
    Location place2 = locations.get(selectedPlaces.get(1));

    graph.connect(locations.get(selectedPlaces.get(0)), locations.get(selectedPlaces.get(1)), name, weight);

    Line connection = new Line(place1.getXPos(), place1.getYPos(), place2.getXPos(), place2.getYPos());
    connection.setStrokeWidth(connectionWidth);
    mapHolder.getChildren().add(connection);
    connection.toBack();
  }

  private void unselectAll(){
    for(Circle c : selectedPlaces){
      c.setFill(Color.BLUE);
    }
    selectedPlaces.clear();
  }
  private boolean checkSelectedPlaces() {
    if(selectedPlaces.size() < 2) {
      error("Please select at least two places");
      return false;
    }
    return true;
  }
  private void error(String message){
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setContentText(message);
    alert.showAndWait();
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
