import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {
    //this.es provides access to IncomingConnectionHandlerThread thread for JavaFX shutdown process.
    private final ExecutorService es = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.initStyle(StageStyle.UNDECORATED); //this is to remove the native stage
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Server.fxml"));
        Parent root = fxmlLoader.load();
        Controller serverController = fxmlLoader.getController();
        serverController.setExecutorService(this.es);
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void stop() {
        System.out.println("Inside stop method now");
        this.es.shutdownNow(); //this will cause a thread interrupt and our threads will terminate after this.
    }
}
