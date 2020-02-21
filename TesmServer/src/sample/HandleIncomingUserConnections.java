package sample;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class HandleIncomingUserConnections extends Thread {

    private ServerSocket serverSocket;
    private TextArea activityWindow;
    private ArrayList<UserHandler> connectionList; //package access

    public HandleIncomingUserConnections(int port, TextArea activityWindow) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.activityWindow = activityWindow;
        this.connectionList = new ArrayList<>(); //type inference
    }

    @Override
    public void run() {
        while(true){

            try{
                UserHandler uh = new UserHandler(serverSocket.accept(), this.connectionList, this.activityWindow); //handle the user
                uh.start();
                this.connectionList.add(uh); //add socket connection to the list of connected users
            } catch (IOException e) {
                activityWindow.appendText("Failure on accepting an incoming connection.\n");
            }

        }

    }
}
