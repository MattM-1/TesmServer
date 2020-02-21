package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class InboxController {

    @FXML
    private AnchorPane root;
    @FXML
    private TextArea inputTA;
    @FXML
    private TextArea outputTA;
    @FXML
    private Button sendButton;

    private double xOffset;
    private double yOffset;

    private String name;
    private Socket socket;
    private ObjectOutputStream oos;
    private receiveMessageThread rmt;

    public void setSocket(String address, String port) throws IOException {
        this.socket = new Socket(address, Integer.parseInt(port));
    }

    public void beginCommunication() throws IOException {
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.rmt = new receiveMessageThread(this.socket, this.outputTA);
        rmt.start();
    }

    public void initDisplayName(String name) throws IOException {
        this.name = name;
        this.oos.writeObject(this.name);
    }

    public void setName(String name){
        this.name = name;
    }

    @FXML
    public void initialize(){

        Platform.runLater( () -> root.requestFocus() ); //No immediate focus on any nodes

        outputTA.setEditable(false); //cannot edit the text inside
        outputTA.setFocusTraversable(false); //is not traversable with tab
        outputTA.setWrapText(true);

        inputTA.setWrapText(true);
        inputTA.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER)){
                    String s = inputTA.getText();
                    s = s.substring(0,s.length()-1);
                    outputTA.appendText(name+": "+s+"\n"); //removed "\n" because this will enter and create "\n" before reaching this code and will result us in two spaces with the "\n" included in the code
                    try {
                        oos.writeObject(s);
                    } catch (IOException e) {
                        //if there was an error sending the message display that in the text
                        //make the send button flash red and don't do anything with the text
                        e.printStackTrace();
                    }
                    inputTA.clear();
                }
            }
        });

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String s = inputTA.getText();
                outputTA.appendText(name+": "+s+"\n");
                try {
                    oos.writeObject(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inputTA.clear();
            }
        });

    }

    public void closeWindow(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void minimizeWindow(MouseEvent mouseEvent) {
        Main.stage.setIconified(true); //when maximizing the native system programs handle that so we don't need to worry
    }

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

}
