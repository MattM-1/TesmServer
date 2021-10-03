package userConnections;
import javafx.scene.control.TextArea;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class IncomingConnectionHandlerThread extends Thread {
    private ServerSocket ss;
    private List<Connection> connections;
    private TextArea activityWindow;
    private BlockingQueue<Message> messages;
    private String poisonPill;

    public IncomingConnectionHandlerThread(int port, TextArea activityWindow) throws IOException {
        this.ss=new ServerSocket(port);
        this.ss.setSoTimeout(1000); //This allows our thread to check if it has been interrupted each second.
        this.activityWindow=activityWindow;
        this.connections=Collections.synchronizedList(new ArrayList<>()); //returns a concurrently safe list
        this.messages=new LinkedBlockingQueue<>();
        this.poisonPill=this.getRandomString();
    }

    @Override
    public void run() {
        MessageWriterThread writer=
                new MessageWriterThread(this.messages, this.connections, this.poisonPill, this.hashCode());
        writer.start();
        while(true){
            try {
                Socket s=this.ss.accept(); //blocks here for 1 second, throws exception, throws ste, and repeats again.
                Connection c=this.establishConnection(s);
                new MessageReaderThread(this.messages, c).start();
                this.connections.add(c);
                this.activityWindow.appendText(c.toString()+" has connected.\n");
            } catch (SocketTimeoutException ste){
                if(Thread.interrupted()){
                    synchronized (this.connections){
                        for(Connection c:this.connections){
                            if(!c.isClosed()){
                                try {
                                    c.writeMessage("Server: The server is shutting down and, thus, " +
                                            "is no longer distributing messages.");
                                } catch (IOException e) {
                                    c.close();
                                }
                            }
                        }
                    }
                    this.messages.add(new Message(this.poisonPill, "Server", this.hashCode()));
                    return;
                }
            } catch (IOException|ClassNotFoundException e){
                this.activityWindow.appendText("Failed connection attempt");
            }
        }
    }

    private String getIPAddressFromSocket(Socket s) {
        SocketAddress sa=s.getRemoteSocketAddress();
        if (sa instanceof InetSocketAddress) {
            InetAddress inetAddress=((InetSocketAddress)sa).getAddress();
            if (inetAddress instanceof Inet4Address)
                return inetAddress.toString();
            else if (inetAddress instanceof Inet6Address)
                return inetAddress.toString();
            else
                return "not an IP address";
        } else {
            return "Not an IP socket.";
        }
    }

    private Connection establishConnection(Socket s) throws IOException, ClassNotFoundException {
        ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
        ObjectOutputStream oos =new ObjectOutputStream(s.getOutputStream());
        String name=(String)ois.readObject();
        oos.writeObject(name + ", you have successfully connected to the server.\n");
        String IP=this.getIPAddressFromSocket(s);
        return new Connection(s, name, IP, ois, oos);
    }

    private String getRandomString() {
        byte[] array=new byte[8];
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }
}
