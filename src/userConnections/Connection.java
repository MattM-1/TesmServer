package userConnections;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//Implements closeable, so this Connection can be automatically closed after try-with-resources block
public final class Connection implements Closeable {
    private final Socket s;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;
    private final String name;
    private final String IPAddress;

    Connection(Socket s, String name, String IPAddress, ObjectInputStream ois, ObjectOutputStream oos) {
        this.s=s;
        this.ois=ois;
        this.oos=oos;
        this.name=name;
        this.IPAddress=IPAddress;
    }

    public void writeMessage(String s) throws IOException {
        synchronized(this.oos) {
            this.oos.writeObject(s);
        }
    }

    public Message readMessage() throws IOException, ClassNotFoundException {
        synchronized (this.ois){
            String message = (String)this.ois.readObject();
            return new Message(message, this.name, this.hashCode()); //The hashcode will be unique for each connection.
        }
    }

    public synchronized boolean isClosed(){
        return this.s.isClosed();
    }

    @Override
    public synchronized void close() {
        if(!s.isClosed()) {
            try {
                this.s.close(); //This should implicitly close its associated streams and release system resources.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getName(){
        return this.name;
    }

    public String getIP(){
        return this.IPAddress;
    }

    @Override
    public String toString(){
        return this.getName()+this.getIP();
    }
}
