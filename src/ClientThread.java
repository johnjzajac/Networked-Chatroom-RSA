import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

// To be able to have clients that can connect to the server, 
// We have to have each client on a thread
// Which means that each client is a thread and each thread holds the clients
// personal info such as unique id and username
class ClientThread extends Thread {
	Socket socket;
	ObjectInputStream input;
	ObjectOutputStream output;
	int id;
	// the Username of the Client
	String username;
	Server s;
	MessageType message;
	private RSA rsa;
	public ArrayList<ClientThread> clientlist;	// This is gonna hold every client in the server
	
	// VAriables for encryption
	private int private_rsa; // d value (half of private key)
	private static int public_rsa; // e value (half of public key)
	private static int shared_rsa; // n value (half of both private and public key)
	

	ClientThread(Socket socket, Server serv, ArrayList<ClientThread> c) {
		this.s = serv;
		// a unique id
		id = Server.setClientId(Server.getClientId() +1);
		this.socket = socket;
		this.clientlist = c;
		
		// Get the keys for this client from the RSA class
		rsa = new RSA();
		private_rsa = rsa.getPrivate_rsa();
		public_rsa = rsa.public_rsa;
		shared_rsa = rsa.shared_rsa;
		
		clientlist = Server.getClientList();
		/*
		ClientThread c = clients.get(1);
		int x = c.getPublicRSA();
		int y = c.getShareRSA();
		*/
		
		try
		{
			// create output first
			output = new ObjectOutputStream(socket.getOutputStream());
			input  = new ObjectInputStream(socket.getInputStream());
			// read the username
			username = (String) input.readObject();
			// Print to server information screen
			s.printMessage(username + " just connected.");
		}
		catch (IOException e) { }
		// have to catch ClassNotFoundException
		// but I read a String, I am sure it will work
		catch (ClassNotFoundException e) {
		}
	}
	
	public int getShareRSA() {
		// TODO Auto-generated method stub
		return shared_rsa;
	}

	public int getPublicRSA() {
		// TODO Auto-generated method stub
		return public_rsa;
	}

	// This will continue to run until a client has logged out
	public void run() {
		// to loop until logout
		boolean serverOnline = true;
		while(serverOnline) {
			// Read String
			try {
				message = (MessageType) input.readObject();
			} catch (IOException e) {
				break;			
			} catch(ClassNotFoundException e2) {
				break;
			}


			// Get the type of message. A login, logout, or a message
			// WE can later change this to possible send a message to a certain individual client
			switch(message.getType()) {

			case MessageType.msg:
				// get Message, determine type and print message
				String mg = message.getMessage();
				s.sendMessageToAllUsers(username + ": " + mg);
				break;
			case MessageType.logout:
				// get Message, determine type and print message
				//String mg = message.getMessage();
				s.sendMessageToAllUsers(username + " has disconnected.");
				serverOnline = false;
				break;
			case MessageType.encrypted:
				ArrayList<Long> l = null;
				s.sendMessageToOneUsers(username, l);
			}
		}
		s.remove(id);
		close();
	}
		
	// try to close everything
	private void close() {
		// try to close the connection
		try {
			// close output stream
			if(output != null) output.close();
		} catch(Exception e) { }
		try {
			// close input stream
			if(input != null) input.close();
			} catch(Exception e) { };
		try {
			//close socket
			if(socket != null) socket.close();
		} catch (Exception e) { }
	}

	// This method will write a string to the output stream
	boolean writeMsg(String msg) {
		// if Client is still connected send the message to it
		if(!socket.isConnected()) {
			close();
			return false;
		}
		// write the message to the stream
		try {
			output.writeObject(msg);
		}
		// if an error occurs, let user know message was not sent
		catch(IOException e) {
			s.printMessage("Error sending message to " + username);
			s.printMessage(e.toString());
		}
		return true;
	}
	
	// This method will write a string to the output stream
		boolean writeEn(ArrayList<Long> msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				output.writeObject(msg);
			}
			// if an error occurs, let user know message was not sent
			catch(IOException e) {
				s.printMessage("Error sending message to " + username);
				s.printMessage(e.toString());
			}
			return true;
		}
		
		public static String decryptMessage(ArrayList<Long> l) {
			String s = RSA.decryption(l,public_rsa , shared_rsa);
			return s;
		}

}