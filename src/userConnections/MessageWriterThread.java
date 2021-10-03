package userConnections;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageWriterThread extends Thread {
    private final BlockingQueue<Message> messages;
    private final List<Connection> connections;
    private final String poisonPill;
    private final int serverHashCode;

    MessageWriterThread(BlockingQueue<Message> messages, List<Connection> connections, String poisonPill, int serverHashCode){
        this.messages=messages;
        this.connections=connections;
        this.poisonPill=poisonPill;
        this.serverHashCode=serverHashCode;
    }

    @Override
    public void run() {
        while(true){
            try {
                Message m=this.messages.poll(100, TimeUnit.MILLISECONDS);
                if(m==null) {
                    continue;
                } else if (m.getSenderHash()==this.serverHashCode && m.getMessage().equals(this.poisonPill)) {
                    this.closeAllOpenConnections();
                    return;
                } else {
                    this.sendToAllConnections(m);
                }
            } catch (InterruptedException ie) {
                //The thread should be shut down immediately. This is not a proper shutdown and shouldn't occur naturally;
                //A proper shut down would be done by submitting a poisonPill to the BlockingQueue allowing all messages
                //in the BlockingQueue to be sent first.
                return;
            } finally {
                //The finally block will execute even after a return statement.
                this.closeAllOpenConnections();
            }
        }
    }

    private void sendToAllConnections(Message m) {
        //Java docs states that it is imperative to manually synchronize the concurrent list while iterating over it.
        synchronized (this.connections){
            for(Connection c:this.connections) {
                if(!c.isClosed()){
                    if(c.hashCode()!=m.getSenderHash()){
                        try {
                            c.writeMessage(m.getSenderName()+": "+m.getMessage());
                        } catch (IOException e) {
                            c.close();
                        }
                    }
                }
            }
        }
    }

    private void closeAllOpenConnections(){
        //Java docs states that it is imperative to manually synchronize the concurrent list while iterating over it.
        synchronized (this.connections) {
            for (Connection c:this.connections) if (!c.isClosed()) c.close();
        }
    }
}