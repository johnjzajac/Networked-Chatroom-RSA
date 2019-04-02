import java.io.*;
import java.util.ArrayList;

// This helps print out the messages and where they should go
// A simple message from clients or a logout to notify clients and server

// Modify later to change to private messages from one client to another
@SuppressWarnings("serial")
public class MessageType implements Serializable {
	
    static final int msg = 1, logout = 2, encrypted = 3;
    private int type;
    private String message;
    private ArrayList<Long> encryptedMsg;

    // constructor

    MessageType(int type, String message, ArrayList<Long> encrypt) {
        this.type = type;
        this.message = message;
        this.encryptedMsg = encrypt;
    }

    int getType() {
        return type;
    }

    String getMessage() {
        return message;
    }
    
    ArrayList<Long> getEncryptMsg() {
    		return encryptedMsg;
    }
}