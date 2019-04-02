import java.net.*;
import java.io.*;
import java.util.*;


public class Client  {
	// for I/O streams
	private ObjectInputStream sInput;		// to read from the socket
	private ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;
	
	// the server, the port and the username
	private String username;
	private int port;
	private static Scanner scan;
	private static ArrayList<ClientThread> clients;

	// Constructor for Client
	Client(int port, String username) {
		this.port = port;
		this.username = username;
	}
	
	public static void printClientListToGUI() {
		for (int i = 0; i < clients.size(); i++) {
			ClientThread ct = clients.get(i);
			ClientGUI.printToUsersScreen(ct.username);
		}
	}

	public boolean start() {
		// try to connect to the server
		try {
			socket = new Socket("localhost", port);
		} 
		// if it failed not much I can so
		catch(Exception ec) {
			display("Error connectiong to server:" + ec);
			return false;
		}
		
		String msg = "Connection accepted from port: " + socket.getPort();
		display(msg);
	
		/* Creating both Data Stream */
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch (IOException eIO) {
			display("Exception creating new Input/output Streams: " + eIO);
			return false;
		}

		// creates the Thread to listen from the server 
		new GetMessageFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		try
		{
			sOutput.writeObject(username);
		}
		catch (IOException eIO) {
			display("Exception doing login : " + eIO);
			disconnect();
			return false;
		}
		// success we inform the caller that it worked
		return true;
	}

	// Send message to the GUI
	private void display(String msg) {
			ClientGUI.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	}
	
	// This will send a message to the server
	void sendMessage(MessageType msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}

	// Disconnect and close streams and socket
	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} 
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} 
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} 
	}
	
	public static void main(String[] args) {
		// default values
		int portNumber = 1212;
		String userName = "Anonymous";
		
		// create the Client object
		Client client = new Client( portNumber, userName);
		
		// Try to connect to server
		if(!client.start())
			return;
		
		scan = new Scanner(System.in);

		// Inf loop. At least until logout/disconnect
		while(true) {
			clients = Server.getClientList();
			ClientGUI.clearUsersScreen();
			printClientListToGUI();
			// read message from user
			String msg = scan.nextLine();
			// logout if message is LOGOUT
			if(msg.equalsIgnoreCase("LOGOUT")) {
				client.sendMessage(new MessageType(MessageType.logout, "", null));
				// break to do the disconnect
				break;
			}
			else {				// default to ordinary message
				client.sendMessage(new MessageType(MessageType.msg, msg, null));
			}
		}
		// broke out of the loop, which means we want to disconnect
		client.disconnect();	
	}

	// This method gets a message from server and display them in the clients text field
	class GetMessageFromServer extends Thread {
		// Need the run method to fire off thread and start read messages from server
		public void run() {
			while(true) {
				try {
					Object msg = sInput.readObject();
					//ClientGUI.append(msg);
					if(msg instanceof String) {
						System.out.println("Instance of String");	// debug
						ClientGUI.append(msg.toString());
					}
					else {	// This is an encrypted message
						// decrypt
						System.out.println("instanceof decrypt");	// debug
						ArrayList<Long> l = (ArrayList<Long>) msg;
						String s = ClientThread.decryptMessage(l);
						ClientGUI.append(s);
					}
				}
				catch(IOException e) {
					display("Connection to server has closed");
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				}
			}
		}
	} // end thread
}	// End client class
