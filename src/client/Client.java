package client;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Client {
	

	private clientGui clientFrame;
	private final static int SERVERPORT = 5555; 
	private PrintWriter out;
	private BufferedReader in;
	private boolean userConnected = false;

	public Client() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					clientFrame = new clientGui();
					clientFrame.setVisible(true);
					connectButton();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	@SuppressWarnings("resource")
	public void clientActions(JButton connect) {
		try {

			String name = clientFrame.getUserName().getText();
			String serverIp = clientFrame.getIp().getText();

			if(name.isEmpty()) {
				JOptionPane.showMessageDialog(clientFrame,
						"Please enter username",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			Socket socket = null;
			try {
				socket = new Socket();
				socket.connect(new InetSocketAddress(serverIp, SERVERPORT), 1000);
			} catch(Exception e) {
				JOptionPane.showMessageDialog(clientFrame,
						"Cannot connect to the server",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));

			int checkBoxType = clientFrame.typeOfMsgs();

			out.println("<connect><" + name + "><" + checkBoxType + ">");

			String serverOutput = "";
			while(serverOutput.indexOf("<connected><" + name + ">") < 0
					&& serverOutput.indexOf("<userNameExists><" + name + ">") < 0)
				serverOutput = in.readLine();

			if(serverOutput.indexOf("<connected><" + name + ">") > -1)
			{
				this.userConnected = true;
				this.clientFrame.getUserName().setEditable(false);

				String msg = "";
				if(checkBoxType == 1)
					msg = "system: <connected>\n";
				this.clientFrame.setMsgBoxText(msg + "got a connection");

				connect.setText("Disconnect");
				clientFrame.getIp().setEnabled(false);
				clientFrame.getSendBtn().setEnabled(true);
				
				showOnlineUsers();

				new WorkerRunnable(socket).start();
			}

			if(serverOutput.indexOf("<userNameExists>") > -1)
			{
				JOptionPane.showMessageDialog(clientFrame,
						"The user name " + name +" is exists",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				socket.close();
			}

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						out.println("<disconnect><" + name + "><" + checkBoxType + ">");
						out.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} catch (Exception e1) {
			JOptionPane.showMessageDialog(clientFrame,
					"There is no server connection yet.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	public void disconnectClient() {
		clientFrame.getConnectBtn().setText("Connect");
		clientFrame.setMsgBoxText("");
		clientFrame.getIp().setEnabled(true);
		clientFrame.getUserName().setEditable(true);
		clientFrame.getSendBtn().setEnabled(false);
		if(this.userConnected) {
			out.println("<disconnect><" + clientFrame.getUserName().getText() + "><" + clientFrame.typeOfMsgs() + ">");
			out.flush();
		}
	}
	
	public void showOnlineUsers() {
		JButton onlineBtn = clientFrame.getOnlineBtn();
		onlineBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String userName = clientFrame.getUserName().getText();
				out.println("<get_users><" + userName + ">");
				out.flush();
			}
		});
	}

	public void sendAction() {
		JButton send = clientFrame.getSendBtn();
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String sendFrom = clientFrame.getUserName().getText();
				String sendTo = clientFrame.getCurName();
				String msg = clientFrame.getMsgField().getText();

				if(msg.isEmpty())
				{
					JOptionPane.showMessageDialog(clientFrame,
							"Please insert message",
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
				else if(sendTo.isEmpty())
				{
					JOptionPane.showMessageDialog(clientFrame,
							"Please insert user name that you want to send him message (or type \"all\" to send to all users in this chat server)",
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					if(sendTo.equals("all"))
						out.println("<set_msg_all><" + sendFrom + "><" + msg + ">");
					else
						out.println("<set_msg><" + sendFrom + "><" + sendTo + "><" + msg + ">");
					out.flush();
				}
			}
		});
	}

	public void connectButton() {
		JButton btnConnect= clientFrame.getConnectBtn();
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(btnConnect.getText().equals("Connect")) {
					clientActions(btnConnect);
					sendAction();
				} else {
					disconnectClient();
				}
			}
		});
	}


	
	class WorkerRunnable extends Thread{

		protected Socket clientSocket = null;

		public WorkerRunnable(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		public StringTokenizer splitMsg(String msg) {
			StringTokenizer st = new StringTokenizer(msg, "<>");
			return st;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			try {
				@SuppressWarnings("unused")
				PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream());
				BufferedReader in = new BufferedReader(
						new InputStreamReader(this.clientSocket.getInputStream()));
				String msg;
				while (userConnected && (msg = in.readLine()) != null) {
					StringTokenizer m = this.splitMsg(msg);
					if(msg.indexOf("<disconnect>") > -1)
					{
						userConnected = false;
						this.clientSocket.close();
						JOptionPane.showMessageDialog(clientFrame,
								"The connection was lost!",
								"Error",
								JOptionPane.ERROR_MESSAGE);
						disconnectClient();
						out.close();
						in.close();
						break;
					}
					else if(msg.indexOf("<userNameIsNotOnline>") > -1 && m != null)
					{
						m.nextToken();
						@SuppressWarnings("unused")
						String userName = m.nextToken();
						JOptionPane.showMessageDialog(clientFrame,
								"The user name that you want to send him message is not online",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					}
					else if(msg.indexOf("<set_msg>") > -1 && m != null)
					{
						m.nextToken();
						String userName = m.nextToken();
						String msgText = m.nextToken();
						
						if(clientFrame.typeOfMsgs() == 1)
							clientFrame.setMsgBoxText("system: <msg><" + userName + ": " + msgText + ">");
						else
							clientFrame.setMsgBoxText(userName + ": " + msgText);
						
						JTextField msgField = clientFrame.getMsgField();
						msgField.setText("");
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								msgField.grabFocus();
								msgField.requestFocus();
							}
						});
					}
					else if(msg.indexOf("<get_users>") > -1 && m != null)
					{
						m.nextToken();
						String usersList = m.nextToken();
						if(clientFrame.typeOfMsgs() == 1)
							clientFrame.setMsgBoxText("system: <get_users><The online users is: " + usersList + ">");
						else
							clientFrame.setMsgBoxText("The online users is: " + usersList);
					}
					else
						clientFrame.setMsgBoxText(msg);
				}

			} catch (IOException e) {

			}
		}

	}
	
	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Client client = new Client();
	}
	
	
}