package sample;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class LoginController {

    private double xOffset;
    private double yOffset;

    @FXML
    private AnchorPane root;
    @FXML
    private TextField serverAddressTF;
    @FXML
    private TextField portNumberTF;
    @FXML
    private TextField displayNameTF;
    @FXML
    private Button connectButton;

    @FXML
    public void initialize(){
        Platform.runLater( () -> root.requestFocus() ); //Don't request to focus on anything immediately on run.
    }

    @FXML
    private void handleLoginAttempt() {

        try {
            switchWindow(serverAddressTF.getText(), portNumberTF.getText(), displayNameTF.getText()); //issue is all stemming from here
        } catch (IOException e) {
            connectButton.setText("Failed connection, try again");
        } catch (Exception e) {
            connectButton.setText("Check input, try again after");
        }

    }

    public void switchWindow(String address, String port, String name) throws IOException {

        FXMLLoader l = new FXMLLoader(getClass().getResource("Inbox.fxml"));
        Parent root = l.load(); //load fxml
        InboxController isc = l.getController(); //get controller so we can call methods on it
        isc.setSocket(address, port);
        isc.beginCommunication();
        isc.initDisplayName(name);
        Main.stage.setScene(new Scene(root)); //switch scenes


    }

    public void closeLogin(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void minimizeLogin(MouseEvent mouseEvent) {
        Main.stage.setIconified(true); //when maximizing the native system programs handle that so we don't need to worry
    }

    //Code for dragging undecorated window START
    public void rootMousePressed(MouseEvent mouseEvent) {
        this.xOffset = mouseEvent.getSceneX();
        this.yOffset = mouseEvent.getSceneY();
    }

    public void rootMouseDragged(){

        this.root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Main.stage.setX(event.getScreenX()-xOffset);
                Main.stage.setY(event.getScreenY()-yOffset);
            }
        });
    }
    //Code for dragging undecorated window END

}