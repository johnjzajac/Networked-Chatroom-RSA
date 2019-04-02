import java.io.*;
import java.net.*;
import java.util.*;

// This creates the server portion
public class Server {
	// an ArrayList to keep the list of the Client
	static ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	private ServerGUI s;
	private static int clientId;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned of to stop the server
	private boolean serverOnline;
	ClientThread ct;
	
	public Server(int port, ServerGUI s) {
		this.s = s;
		// the port
		this.port = port;
	}
	
	public void start() {
		serverOnline = true;
		// create socket and wait for connections
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections
			while(serverOnline) 
			{
				// format message saying we are waiting
				printMessage("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!serverOnline)
					break;
				ClientThread t = new ClientThread(socket, this, clients);  // make a thread of it
				clients.add(t);									// save it in the ArrayList
				s.clearUsersScreen();
				printClientListToGUI();
				t.start();
			}
			try {
				serverSocket.close();
				for(int i = 0; i < clients.size(); ++i) {
					ClientThread tc = clients.get(i);
					try {
					// close data streams
					tc.input.close();
					tc.output.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
					}
				}
			}
			catch(Exception e) {
				printMessage("Exception closing the server and clients: " + e);
			}
		}
		catch (IOException e) {
            String ms = (" Exception on new ServerSocket: " + e + "\n");
            printMessage(ms);
		}
	}		
    /*
     * For the GUI to stop the server
     */
	@SuppressWarnings("resource")
	protected void stop() {
		serverOnline = false;
	
		try {
			new Socket("localhost", port);
		} catch(Exception e) { }
	}

	
	public void printMessage(String str) {
		s.printToServerInfo(str);
	}
	
	public void printClientListToGUI() {
		for (int i = 0; i < clients.size(); i++) {
			ct = clients.get(i);
			s.printToUsersScreen(ct.username);
		}
	}

	// Removes a client when they hit the logoff key
	void remove(int id) {
		// clear users screen so we can place new users - the logoff one
		s.clearUsersScreen();
		// scan the array list until we found the Id
		for(int i = 0; i < clients.size(); ++i) {
			ct = clients.get(i);
			// found it
			if(ct.id == id) {
				clients.remove(i);
				printClientListToGUI();
				return;
			}
		}
	}
	
	// Sends message to all users, such as a user disconnecting
	// This is called from clientThread class
	public void sendMessageToAllUsers(String str) 
	{	
		ClientThread ct;
		s.printToMessageScreen(str);     // append in the room window

		for(int i = clients.size(); --i >= 0;) {
			ct = clients.get(i);
		
			// try to write to the Client if it fails remove it from the list
			if(!ct.writeMsg(str)) {
				clients.remove(i);
				printMessage(ct.username + " has disconnected from the server!");
			}
		}
	}
	
	// Sends message to one user
	// This is called from clientThread class
	public void sendMessageToOneUsers(String name, ArrayList<Long> encrypt) 
	{	
		ClientThread ct;
		
		for(int i = clients.size(); --i >= 0;) {
			ct = clients.get(i);
			
			// try to write to the Client if it fails remove it from the list
			if(!ct.writeEn(encrypt)) {
				clients.remove(i);
				printMessage(ct.username + " has disconnected from the server!");
			}
		}
	}

	// Getters and setters
	public static int getClientId() {
		return clientId;
	}
	public static int setClientId(int clientId) {
		Server.clientId= clientId;
		return clientId;
	}
	
	
	public static ArrayList<ClientThread> getClientList() {
		return clients;
	}
	
	public static ArrayList<ClientThread> setClientList(ArrayList<ClientThread> l) {
		Server.clients = l;
		return clients;
	}
}	// End of server class