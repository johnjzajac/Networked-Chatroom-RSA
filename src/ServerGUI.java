/*
 * Chance Potter - cpotte4
 * Nick Abbasi - nabbasi3
 * John Zajac - jzajac4
 * 
 * Program 5 for CS342 at UIC Fall semester of 2017
 * 
 * Sources:
 * Notes from professor Troy and help from TA's
 * https://stackoverflow.com/questions/19201605/stop-jtextarea-from-expanding
 * https://stackoverflow.com/questions/9103226/getting-user-input-by-jtextfield-in-java
 * 
 */
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;

// Creates the GUI window for the server
public class ServerGUI implements ActionListener {
	
	/*** Variables ***/
	private JFrame f;
	// Create the objects for The GUI
	private JButton start;
	private JTextField portNumber;
	private JTextArea userMessages, serverInfo, users;
	// JTextArea for the chat room and the events
	private Server server;
	private JMenuBar menubar;
	private JMenu menu;
	private JMenuItem aboutbox;
	private JMenuItem helpbox;
	/*****************/
	
	// These three methods allow a message to be printed to the chat screen, connection screen, or user screen
	void printToMessageScreen(String str) {
		userMessages.append(str + "\n");
	}
	void printToServerInfo(String str) {
		serverInfo.append(str + "\n");
	}
	void printToUsersScreen(String str) {
		users.append(str + "\n");
	}
	// Clear screen of users 
	void clearUsersScreen() {
		users.setText("");
	}
	
	// server constructor that receive the port to listen to for connection as parameter
	ServerGUI(int port) {
		// Create JFrame and give it a title
		f = new JFrame("Server Window");
		f.setLayout(new BorderLayout());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		
		// Create a panel that holds the port number
		JPanel infoPanel = new JPanel();
		infoPanel.add(new JLabel("Port number: "));
		portNumber = new JTextField("  " + port);
		infoPanel.add(portNumber);
		// Create a button to start up to server or disconnect the server on command.
		start = new JButton("Start");
		start.addActionListener(this);
		infoPanel.add(start);
		
		// Create a panel that holds all the users
		JPanel usersPanel = new JPanel(new BorderLayout());
		users = new JTextArea();
		users.setEditable(false);
		TitledBorder border = new TitledBorder("Users");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);
        usersPanel.setBorder(border);
        usersPanel.add(users);
		
		// the event and chat room
		JPanel messages = new JPanel();
		messages.setLayout(new GridLayout(2,1));
		userMessages = new JTextArea();
        userMessages.setLineWrap(true);
		printToMessageScreen("User Messages: \n");
		userMessages.setEditable(false);
		JScrollPane scroll = new JScrollPane(userMessages);
		messages.add(scroll);
		serverInfo = new JTextArea();
		serverInfo.setLineWrap(true);
		printToServerInfo("Server Information:\n");
		serverInfo.setEditable(false);
		JScrollPane scroll2 = new JScrollPane(serverInfo);
		messages.add(scroll2);	
		
		TitledBorder border2 = new TitledBorder("Messages");
        border2.setTitleJustification(TitledBorder.CENTER);
        border2.setTitlePosition(TitledBorder.TOP);
        messages.setBorder(border2);
		
		// Add panels to frame
		f.add(infoPanel, BorderLayout.NORTH);
		f.add(messages, BorderLayout.CENTER);
		f.add(usersPanel, BorderLayout.WEST);
		usersPanel.setPreferredSize(new Dimension(200, 200));
		f.add(usersPanel, BorderLayout.WEST);
		f.pack();
		
		// menubar init
		menubar = new JMenuBar();
		menu = new JMenu("File");
		aboutbox = new JMenuItem("About");
		helpbox = new JMenuItem("Help");
		class aboutaction implements ActionListener {
			public void actionPerformed (ActionEvent e) {
				JOptionPane.showMessageDialog(f.getComponent(0), 
						"Networked Chatroom with RSA Encryption/Decryption\n"
						+ "Chance Potter: cpotte4 \n"
						+ "Nicholas Abbasi: nabbasi3 \n"
						+ "John Zajac: jzajac4 ");
			}
		}
		aboutbox.addActionListener(new aboutaction());
		menu.add(aboutbox);
		
		class helpaction implements ActionListener {
			public void actionPerformed (ActionEvent e) {
				JOptionPane.showMessageDialog(f.getComponent(0), 
						"To use the chatroom, please create a username, then login to the chatroom\n"
						+ "Once you have succesffully logged in, choose who you would like to send\n"
						+ "your message to (either select all users or a specific client).\n"
						+ "Once selected, type out your message and send away.\n"
						+ "To leave the chatroom, hit the logout button at the bottom.");
			}
		}
		helpbox.addActionListener(new helpaction());
		menu.add(helpbox);
		
		menubar.add(menu);
		f.setJMenuBar(menubar);
		
		f.setSize(600, 600);
		f.setVisible(true);
		f.setResizable(true);
	}		
	
	// start or stop where clicked
	public void actionPerformed(ActionEvent e) {
		// if running we have to stop
		if(server != null) {
			server.stop();
			server = null;
			portNumber.setEditable(true);
			start.setText("Start");
			return;
		}
		// Get port number from JTextField
		int port = Integer.parseInt(portNumber.getText().trim());
		// ceate a new Server
		server = new Server(port, this);
		// Fire off a thread for the server
		new Run().start();
		start.setText("Stop");
		// port number has been established. DO NOT CHANGE
		portNumber.setEditable(false);
	}
	
	// Main - Start the GUI and give it a port number
	// Make it random so it may not already be in use
	public static void main(String[] arg) {
		new ServerGUI(1212);
	}

	// Create a thread for the server
	class Run extends Thread {
		public void run() {
			server.start();
			start.setText("Start");
			portNumber.setEditable(true);
			printToServerInfo("Connection to server has been lost!");
			server = null;
		}
	}
}


