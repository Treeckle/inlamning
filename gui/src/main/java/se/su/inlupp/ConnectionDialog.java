//Prog2 VT2025, Inlämningsuppgift, del 2
//Grupp 361
//Jamal Cabanos jaca9541
package se.su.inlupp;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.Optional;

public class ConnectionDialog extends Dialog<String> {
    private TextField nameField = new TextField();
    private TextField timeField = new TextField();
    private Label nameLabel = new Label("Name:");
    private Label timeLabel = new Label("Time:");
    boolean changingConnection = false;

    public ConnectionDialog(String from, Edge<Location> connection/*, String connectionName, int time*/){
        super();
        setTitle("Connection Dialog");
        this.setHeaderText("Connection between  " + from +" and " + connection.getName());
        nameField.setText(connection.getName());
        nameField.setEditable(false);
        timeField.setText(Integer.toString(connection.getWeight()));
        timeField.setEditable(false);
        createLayout();
        finishDialog();

    }
    public ConnectionDialog(String connectionName) {

        super();
        this.setTitle("Connection Dialog");
        this.setHeaderText("Configure Connection");
        changeConnection(connectionName);
        createLayout();
        finishDialog();

    }
    public ConnectionDialog(){
        this(null);
    }

    private void createLayout(){
        GridPane dialogGrid = new GridPane();
        dialogGrid.setHgap(10);
        dialogGrid.setVgap(10);
        dialogGrid.add(nameLabel, 0, 0);
        dialogGrid.add(nameField, 1, 0);
        dialogGrid.add(timeLabel, 0, 1);
        dialogGrid.add(timeField, 1, 1);
        getDialogPane().setContent(dialogGrid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }
    private void finishDialog(){

        setResultConverter(e ->{
            if(e == ButtonType.OK){
                if(!timeIsInteger() || timeField.getText().isEmpty()) return null;
                if(changingConnection){
                    return timeField.getText();
                }
                if(nameField.getText().isEmpty())return null;
                return nameField.getText() + ";" + timeField.getText();
            }
            return null;
        });
    }

    private boolean timeIsInteger(){
        try{
            int time = Integer.parseInt(timeField.getText());
            return time >= 0;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private void changeConnection(String name){
        if(name == null) return;
        changingConnection = true;
        nameField.setText(name);
        nameField.setEditable(false);

    }



}
