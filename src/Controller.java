import userConnections.*;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class Controller {
    private double xOffset;
    private double yOffset;
    @FXML
    private AnchorPane root;
    @FXML
    private TextField portNumber;
    @FXML
    private Button launchButton;
    @FXML
    private TextArea activityWindow;

    public void initialize(){
        Platform.runLater( () -> root.requestFocus() ); //No immediate focus on any nodes
        activityWindow.setEditable(false); //cannot edit the text inside
        activityWindow.setFocusTraversable(false); //is not traversable with tab
        activityWindow.setWrapText(true);
    }

    @FXML
    private void launchServer(){
        try {
            HandleIncomingUserConnections huc = new HandleIncomingUserConnections(Integer.parseInt(portNumber.getText()), this.activityWindow);
            huc.start();
            this.launchButton.setDisable(true);
            this.activityWindow.appendText("The server is now accepting incoming connections on port # "+portNumber.getText()+".\n");
            this.portNumber.clear();
        } catch (IOException e) {
            activityWindow.appendText("Error when launching server socket.\n");
        } catch (Exception e) {
            activityWindow.appendText("Try checking if port # input is correct.\n");
        }
    }

    @FXML
    public void shutDownServer(){
        System.exit(0);
    }

    @FXML
    public void closeWindow(MouseEvent mouseEvent) {
        System.exit(0);
    }

    @FXML
    public void minimizeWindow(MouseEvent mouseEvent) {
        Main.stage.setIconified(true); //when maximizing the native system programs handle that so we don't need to worry
    }

    @FXML
    public void rootMousePressed(MouseEvent mouseEvent) {
        this.xOffset = mouseEvent.getSceneX();
        this.yOffset = mouseEvent.getSceneY();
    }

    @FXML
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
