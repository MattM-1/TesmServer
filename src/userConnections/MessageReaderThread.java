package userConnections;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class MessageReaderThread extends Thread {
    private final Connection c;
    private final BlockingQueue<Message> messages;

    MessageReaderThread(BlockingQueue<Message> messages, Connection c){
        this.messages=messages;
        this.c=c;
    }

    @Override
    public void run() {
        while(true){
            try {
                Message m=this.c.readMessage();
                this.messages.add(m);
            } catch (IOException|ClassNotFoundException e) {
                if(!this.c.isClosed()){
                    this.c.close();
                }
                return;
            }
        }
    }
}