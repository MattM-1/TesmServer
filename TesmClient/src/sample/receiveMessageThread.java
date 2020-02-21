package sample;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class receiveMessageThread extends Thread {

    private ObjectInputStream ois;
    private TextArea t;

    public receiveMessageThread(Socket socket, TextArea t) throws IOException {
        this.ois = new ObjectInputStream(socket.getInputStream());
        this.t = t;
    }


    @Override
    public void run() {
        while(true){
            String s = getServerMessage();
            t.appendText(s+"\n");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getServerMessage() {
        String s = "-1";
        try {
            s = (String)ois.readObject();
        } catch (IOException e) {
            t.appendText("Error with I/O streams, closing the app");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            System.exit(1); //for now
        } catch (ClassNotFoundException e) {
            t.appendText("Error with I/O streams, closing the app");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            System.exit(1); //for now
        }
        return s;
    }


}
