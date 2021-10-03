import javafx.event.Event;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import userConnections.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class Controller {
    private double xOffset;
    private double yOffset;
    private ExecutorService es;
    @FXML
    private AnchorPane root;
    @FXML
    private TextField portNumber;
    @FXML
    private Button launchButton;
    @FXML
    private TextArea activityWindow;

    public void initialize() {
        Platform.runLater( () -> root.requestFocus() ); //No immediate focus on any nodes
        activityWindow.setEditable(false); //cannot edit the text inside
        activityWindow.setFocusTraversable(false); //is not traversable with command+tab
        activityWindow.setWrapText(true); //wraps text so that words aren't cut in half at the border of the window
    }

    private Stage getStageFromEvent(Event event) {
        Node node = (Node)event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        return stage;
    }

    public void setExecutorService(ExecutorService es){
        this.es=es;
    }

    //This calls stop(), which is Overridden in the Main.java to stop threads and properly close resources.
    private void shutDown() {
        Platform.exit();
    }

    @FXML
    private void launchServer() {
        try {
            es.execute(new IncomingConnectionHandlerThread(Integer.parseInt(portNumber.getText()), this.activityWindow));
            this.launchButton.setDisable(true);
            this.activityWindow.appendText(
                    "The server is now accepting incoming connections on port # "+
                            portNumber.getText()+
                            ".\n");
            this.portNumber.clear();
        } catch (IOException e) {
            activityWindow.appendText("Error when launching server socket.\n");
        } catch (Exception e) {
            activityWindow.appendText("Try checking if port # input is correct.\n");
        }
    }

    @FXML
    private void shutDownServer() {
        shutDown();
    } //This method is separate from closeWindow() in case it is desired to perform different UI operations.

    @FXML
    private void closeWindow() {
        shutDown();
    } //This method is separate from shutDownServer() in case it is desired to perform different UI operations.

    @FXML
    private void minimizeWindow(MouseEvent event) {
        Stage stage = getStageFromEvent(event);
        stage.setIconified(true);
    } //the native system programs handles maximizing

    @FXML
    private void rootMousePressed(MouseEvent mouseEvent) {
        this.xOffset = mouseEvent.getSceneX();
        this.yOffset = mouseEvent.getSceneY();
    }

    @FXML
    private void rootMouseDragged() {
        this.root.setOnMouseDragged(event -> {
            Stage stage = getStageFromEvent(event);
            stage.setX(event.getScreenX()-xOffset);
            stage.setY(event.getScreenY()-yOffset);
        });
    }
}
