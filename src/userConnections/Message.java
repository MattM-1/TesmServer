package userConnections;

public class Message {
    private final String message;
    private final String senderName;
    private final int senderHash;

    Message(String message, String senderName, int senderHash){
        this.message=message;
        this.senderName=senderName;
        this.senderHash=senderHash;
    }

    public String getMessage(){
        return this.message;
    }

    public int getSenderHash(){
        return this.senderHash;
    }

    public String getSenderName() { return this.senderName; }
}
