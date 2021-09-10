package sample;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class UserHandler extends Thread {

    private ArrayList<UserHandler> connectionList;
    private TextArea activityWindow;

    private Socket connection;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private String name;


    public UserHandler(Socket socket, ArrayList<UserHandler> connectionList, TextArea activityWindow) throws IOException {
        this.connection = socket;;
        this.ois = new ObjectInputStream(socket.getInputStream());
        this.oos = new ObjectOutputStream(socket.getOutputStream());
        this.connectionList = connectionList;
        this.activityWindow = activityWindow;
    }

    @Override
    public void run() {
        try{
            String message;
            setUserName();
            while(true){
                message = getIncomingMessage();
                sendMessageToClients(this.name+": "+message);
            }

        } catch (IOException e) {
            e.printStackTrace(); //from getUserName()
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); //from getUserName()
        }

    }

    private String getIncomingMessage() throws ClassNotFoundException, IOException {
        String message = (String)this.ois.readObject();
        return message;
    }

    private void sendMessageToClients(String message) throws IOException {
        for(UserHandler userConnection : this.connectionList) {
            if(userConnection.getSocket()!=this.connection){
                userConnection.write(message);
            }
        }
    }

    private void setUserName() throws IOException, ClassNotFoundException {
        this.name = (String)ois.readObject();
        this.activityWindow.appendText(this.name+ " has successfully connected to the server.");
        oos.writeObject(this.name + ", you have successfully connected to the server.\n");
    }

    public Socket getSocket(){
        return this.connection;
    }

    public String getUserName(){
        return this.name;
    }

    public void write(String message) throws IOException {
        this.oos.writeObject(message); //consider handling the error here
    }
    
}
