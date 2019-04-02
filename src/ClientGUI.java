import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// This creates the gui for the client window
public class ClientGUI implements ActionListener {

	// JFrame
	JFrame f;
	// create label for text and username
	private JLabel label, usernameLabel;
	// This is the field where users enter their messages
	private JTextField userMessage;
	// to hold the server address an the port number
	private JTextField portNum, userName;
	// Login and logout buttons for client
	private JButton login, logout;
	// This shows all the messages from client and other clients connected to the server
	private static JTextArea messages, users;
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	// the default port number created when clientGUI is called
	private int defaultPort;
	// This is the arraylist of clients
	private static ArrayList<ClientThread> clients;
	// Combo Box
	final JComboBox<String> cb;
	
	static void printToUsersScreen(String str) {
		users.append(str + "\n");
	}
	// Clear screen of users 
	static void clearUsersScreen() {
		users.setText("");
		users.append("hello");
	}

	// Constructor connection receiving a socket number
	ClientGUI(int port) {
		
		f = new JFrame("Client Server");		
		defaultPort = port;
		
		JPanel infoPanel = new JPanel(new GridLayout(4,1));
		// the server name anmd the port number
		JPanel info = new JPanel(new GridLayout(1, 5, 1, 2));
		// the two JTextField with default value for server address and port number
		userName = new JTextField("Anonymous");
		portNum = new JTextField("" + port);
		portNum.setHorizontalAlignment(SwingConstants.RIGHT);
		
		// Create a panel that holds all the users
		JPanel usersPanel = new JPanel(new BorderLayout());
		users = new JTextArea();
		users.setEditable(false);
		TitledBorder border = new TitledBorder("Users");
		border.setTitleJustification(TitledBorder.CENTER);
		border.setTitlePosition(TitledBorder.TOP);
		usersPanel.setBorder(border);
		usersPanel.add(users);

		usernameLabel = new JLabel("User Name: ");
		info.add(usernameLabel);
		info.add(userName);
		info.add(new JLabel("Port Number:  "));
		info.add(portNum);
		info.add(new JLabel(""));
		// adds the Server an port field to the GUI
		infoPanel.add(info);

		// the Label and the TextField
		String[] array = {"all"};
		cb = new JComboBox<String>(array);
	    cb.setVisible(true);
	    infoPanel.add(cb);
		label = new JLabel("Enter your message below", SwingConstants.CENTER);
		infoPanel.add(label);
		userMessage = new JTextField("Hello");
		userMessage.setBackground(Color.WHITE);
		infoPanel.add(userMessage);
		f.add(infoPanel, BorderLayout.NORTH);

		// The CenterPanel which is the chat room
		messages = new JTextArea("Welcome to the Chat room\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(messages));
		messages.setEditable(false);
		f.add(centerPanel, BorderLayout.CENTER);

		// the 2 buttons
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);		// you have to login before being able to logout

		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		f.add(southPanel, BorderLayout.SOUTH);
		
		f.add(usersPanel, BorderLayout.WEST);
		usersPanel.setPreferredSize(new Dimension(200, 200));
		f.add(usersPanel, BorderLayout.WEST);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(600, 600);
		f.setVisible(true);
		userMessage.requestFocus();

	}

	// called by the Client to append text in the TextArea 
	static void append(String str) {
		messages.append(str + "\n");
	}
	
	// called by the GUI is the connection failed
	// we reset our buttons
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		usernameLabel.setText("Enter your username: ");
		userMessage.setText("Hello");
		// reset port number and host name as a construction time
		portNum.setText("" + defaultPort);
		// let the user change them
		portNum.setEditable(false);
		userMessage.removeActionListener(this);
		connected = false;
	}
		
	// button has been clicked
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if(o == logout) {
			client.sendMessage(new MessageType(MessageType.logout, "", null));
			return;
		}
		// coming from the textfield. User wants to send a message
		if(connected) {
			// just have to send the message
			// Encrypt message and then send (as arralist of long)
			System.out.println(cb.getSelectedItem().toString());
			if (cb.getSelectedItem().toString() == "all") {
				System.out.println("all");	// debug
				client.sendMessage(new MessageType(MessageType.msg, userMessage.getText(), null));				
				userMessage.setText("");
				return;
			}
			// else user wants to send private message. If we do this
			// We need to encrypt the message and send to selected user
			else {
				System.out.println("private messagess");	// debug
				ArrayList<Long> l = null;
				String message = userMessage.getText();
				String reciever = cb.getSelectedItem().toString();
				ClientThread ct = null;
				clients = Server.clients;
				System.out.println("" + clients.size());
				for(int i = 0; i < clients.size(); i++) {
					ct = clients.get(i);
					System.out.println("" + ct.getName());
					if(ct.getName() == reciever) {
						break;
					}
				}
				if (ct == null) {
					System.out.println("No such user. How the f**k did that happen!?");
				}
				else {
					l = RSA.encryption(message, ct.getPublicRSA(), ct.getShareRSA());
					client.sendMessage(new MessageType(MessageType.encrypted, "", l));
				}
			}
		}
		
		if(o == login) {
			// ok it is a connection request
			String username = userName.getText().trim();
			// empty username, make client anon
			if(username.length() == 0)
				username = "anon";
			// Get port number from jtextfield and parse it
			String portNumber = portNum.getText().trim();
			int port = Integer.parseInt(portNumber);

			// try creating a new Client with GUI
			client = new Client(port, username);
			// test if we can start the Client
			if(!client.start()) 
				return;
			userMessage.setText("");
			label.setText("Enter your message below");
			connected = true;
			
			// disable login button
			login.setEnabled(false);
			// enable the 2 buttons
			logout.setEnabled(true);
			// disable the Port JTextField
			portNum.setEditable(false);
			// disable username JTextField
			userName.setEditable(false);
			// Action listener for when the user enter a message
			userMessage.addActionListener(this);
		}

	}
	
	public ArrayList<String> getClientNames() {
		ArrayList<String> l = new ArrayList<String>();
		l.add("all");
		for(int i = 0; i < clients.size(); i++) {
			ClientThread ct = clients.get(i);
			l.add(ct.username);
		}
		return l;
	}

	// to start the whole thing the server
	public static void main(String[] args) {
		new ClientGUI(1212);
	}

}