//Prog2 VT2025, Inlämningsuppgift, del 2
//Grupp 361
//Jamal Cabanos jaca9541
package se.su.inlupp;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Gui extends Application {

  private boolean placingLocation = false;
  private boolean changes = false;
  private final int nodeRadius = 10;
  private final int connectionWidth = 2;
  private HashMap<Circle, Location> locations = new HashMap<>();
  private ArrayList<Circle> selectedPlaces = new ArrayList<>();
  private ArrayList<Pair<Location, Location>> edges = new ArrayList<>();
  private Graph<Location> graph;
  private Pane mapHolder;
  private ToolBar toolBar;
  private Button findPath;
  private Button showConnection;
  private Button newPlace;
  private Button newConnection;
  private Button changeConnection;

  private String fileName = null;
  private String backgroundImagePath = null;


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
    MenuItem saveAs = new MenuItem("Save As");
    MenuItem saveImage = new MenuItem("Save Image");
    MenuItem exit = new MenuItem("Exit");

    MenuItem customMap = new MenuItem("Custom Map");
    MenuItem skyrim = new MenuItem("Skyrim");
    MenuItem borderlands2 = new MenuItem("Borderlands 2");

    Image customImage;
    Image skyrimMap = new Image("file:assets/maps/templates/skyrimMap.png");
    Image borderlands2Map = new Image("file:assets/maps/templates/borderlands2Map.png");

    HashMap<MenuItem, Image> templates = new HashMap<MenuItem, Image>();
    templates.putIfAbsent(skyrim, skyrimMap);
    templates.putIfAbsent(borderlands2, borderlands2Map);


    menu.getItems().addAll(maps, open, save, saveAs, saveImage, exit);
    maps.getItems().addAll(customMap, skyrim, borderlands2);
    menuBar.getMenus().add(menu);

    stage.setOnCloseRequest(event -> {
      event.consume();
      exitProgram();
    });
    //function for menu items
    for(MenuItem m : templates.keySet() ) {
      m.setOnAction(event -> {
        unsavedChanges();
        reset();
        backgroundImagePath = templates.get(m).getUrl();
        mapHolder.setBackground(new Background(new BackgroundImage(templates.get(m), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        mapHolder.setPrefSize(templates.get(m).getWidth(), templates.get(m).getHeight());
        stage.sizeToScene();
        for(Node n : toolBar.getItems()) {
          if (n instanceof Button b) {
            b.setDisable(false);
          }
        }
      });
    }
    customMap.setOnAction(event -> {
      unsavedChanges();
      reset();
      FileChooser fileChooser = new FileChooser();
      File file = fileChooser.showOpenDialog(stage);
      if(file == null) return;
      Image image = new Image(file.toURI().toString());
      if (image.isError()) return;
      mapHolder.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
      mapHolder.setPrefSize(image.getWidth(), image.getHeight());
      stage.sizeToScene();
      for(Node n : toolBar.getItems()) {
        if (n instanceof Button b) {
          b.setDisable(false);
        }
      }
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

    save.setOnAction(event -> {
      save();
    });
    saveAs.setOnAction(event -> {
      saveAs();
    });

    open.setOnAction(event -> {
      FileChooser fileChooser = new FileChooser();
      FileInputStream fileInputStream;
      File file = fileChooser.showOpenDialog(stage);
      if(file == null) return;
      if(file.getName().endsWith(".graph")) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Selected file is not a graph file.");
      }
      unsavedChanges();
      try {
        fileInputStream = new FileInputStream(file.getAbsolutePath());
        ObjectInputStream ois = new ObjectInputStream(fileInputStream);
        reset();
        Image background = new Image((String) ois.readObject());
        graph = (ListGraph<Location>) ois.readObject();
        edges = (ArrayList<Pair<Location, Location>>) ois.readObject();
        mapHolder.setBackground(new Background(new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        mapHolder.setPrefSize(background.getWidth(), background.getHeight());
        for(Location l : graph.getNodes()) {
          Circle c = new Circle(l.getXPos(), l.getYPos(), nodeRadius, Color.BLUE);
          locations.putIfAbsent(c, l);
          Label label = new Label(l.toString());
          label.setFont(Font.font("System", FontWeight.BOLD, 14));
          label.relocate(l.getXPos()-nodeRadius, l.getYPos()+nodeRadius);

          c.setOnMouseClicked(mouseEvent -> {
            selectCircle(c);
          });
          mapHolder.getChildren().addAll(c, label);
          label.toBack();

        }
        for(Pair<Location, Location> pair : edges) {
          Line l = new Line(pair.getKey().getXPos(), pair.getKey().getYPos(), pair.getValue().getXPos(), pair.getValue().getYPos());
          l.setStrokeWidth(connectionWidth);
          mapHolder.getChildren().add(l);
        }
        for(Node n : toolBar.getItems()) {
          if (n instanceof Button b) {
            b.setDisable(false);
          }
        }
        stage.sizeToScene();

      }catch (ClassNotFoundException e) {
        System.err.println("Class not found");
      }catch (FileNotFoundException e) {
        System.err.println("File could not be opened");

      }catch (IOException e) {
        System.err.println("IO Exception occured");
      }

    });

    exit.setOnAction(event -> {
      stage.fireEvent(
              new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)
      );
    });


    //toolbar setup
    toolBar = new ToolBar();
    findPath = new Button("Find Path");
    showConnection = new Button("Show Connection");
    newPlace = new Button("New Place");
    newConnection = new Button("New Connection");
    changeConnection = new Button("Change Connection");
    toolBar.getItems().addAll(findPath, showConnection, newPlace, newConnection, changeConnection);
    for(Node n : toolBar.getItems()){
      if(n instanceof Button b){
        b.setDisable(true);
      }
    }

    findPath.setOnAction(buttonHandler);
    showConnection.setOnAction(buttonHandler);
    newPlace.setOnAction(buttonHandler);
    newConnection.setOnAction(buttonHandler);
    changeConnection.setOnAction(buttonHandler);

    //footer setup
    HBox footer = new HBox();
    Label action = new Label("Actions from the user will potentially be displayed here");

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
        nameInput.setHeaderText("New Place");
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
        Label label = new Label(name);
        label.setFont(Font.font("System", FontWeight.BOLD, 14));
        label.relocate(event.getX()-nodeRadius, event.getY()+nodeRadius);

        circle.setOnMouseClicked(mouseEvent -> {
        selectCircle(circle);
        });
        mapHolder.getChildren().addAll(circle, label);
        changes = true;
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
        changes = true;
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
        changes = true;
      }

      if(event.getSource() == showConnection){
        if(!checkSelectedPlaces()) return;
        Edge<Location> connection = graph.getEdgeBetween(locations.get(selectedPlaces.get(0)), locations.get(selectedPlaces.get(1)));
        ConnectionDialog connectionDialog = new ConnectionDialog(locations.get(selectedPlaces.get(0)).toString(),connection);
        connectionDialog.showAndWait();
        unselectAll();
      }

      if(event.getSource() == findPath) {
        if (!checkSelectedPlaces()) return;
        List<Edge<Location>> path = graph.getPath(locations.get(selectedPlaces.get(0)), locations.get(selectedPlaces.get(1)));
        String pathString = "";
        int totalTime = 0;
        Alert pathAlert = new Alert(Alert.AlertType.INFORMATION);
        pathAlert.setHeaderText("The path from " + locations.get(selectedPlaces.get(0)).toString() + " to " + locations.get(selectedPlaces.get(1)).toString() + ":");
        for (Edge<Location> edge : path) {
          totalTime += edge.getWeight();
          pathString += edge.toString() + "\n";
        }
        pathAlert.setContentText(pathString + "\nTotal time: " + totalTime);
        pathAlert.showAndWait();
        unselectAll();
      }
    }
  }


  private void connectPlaces(String name, int weight){
    Location place1 = locations.get(selectedPlaces.get(0));
    Location place2 = locations.get(selectedPlaces.get(1));

    graph.connect(locations.get(selectedPlaces.get(0)), locations.get(selectedPlaces.get(1)), name, weight);
    edges.add(new Pair<>(place1, place2));
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

  private void unsavedChanges(){
    if (!changes) return;
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setHeaderText("You have unsaved changes, would you like to save changes?");
    System.out.println(alert.showAndWait());
    if (alert.getResult() == ButtonType.OK){
      save();
    }
  }

  private void save(){

      if (fileName == null) {
        pickSaveLocation();
      }

      try{
      FileOutputStream file = new FileOutputStream(fileName);
      ObjectOutputStream oos = new ObjectOutputStream(file);
      unselectAll();

      oos.writeObject(backgroundImagePath);
      oos.writeObject(graph);
      oos.writeObject(edges);
      //oos.writeObject(savedLocations);
    }catch (FileNotFoundException e){
      System.err.println("File could not be found");
    }catch (IOException e){
      System.err.println("IOException occured");
    }
  }

  private void pickSaveLocation(){
    FileChooser filechooser = new FileChooser();
    File file = filechooser.showSaveDialog(mapHolder.getScene().getWindow());
    if (file == null){
      throw new IllegalStateException("Save failed");
    }
    if(!file.getName().endsWith(".graph")){
      String[] name = file.getAbsolutePath().split("\\.");
      file = new File(name[0] + ".graph");
    }
    fileName = file.getAbsolutePath();
  }

  private void saveAs(){
    pickSaveLocation();
    save();
  }
  private void exitProgram(){
    unsavedChanges();
    System.exit(0);
  }

  public void reset(){
    graph = new ListGraph<Location>();
    edges.clear();
    selectedPlaces.clear();
    changes = false;
    mapHolder.getChildren().clear();
    fileName = null;
    backgroundImagePath = null;

  }
  public static void main(String[] args) {
    launch(args);
  }


}
