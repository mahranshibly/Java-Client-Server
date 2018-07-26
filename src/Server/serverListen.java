package Server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
 
public class serverListen implements Runnable{
 
	private int serverPort; 
	 
	private ServerSocket serverSocket = null;
	 
	private boolean isStopped    = false;
	 
	private serverGui serverFrame;
	 
	private HashMap<String, WorkerRunnable> usersList;

	 
	public serverListen(int port, serverGui serverFrame, HashMap<String, WorkerRunnable> usersList){
		this.serverPort = port;
		this.serverFrame = serverFrame;
		this.usersList = usersList;
	}

	 
	public void run(){
		try {
			this.serverSocket = new ServerSocket(this.serverPort);

		} catch (IOException e) {
			serverFrame.setMsgBoxText("Cannot listen on this port.\n" + e.getMessage());
			isStopped = true;
		}   

		while(!isStopped()){
			Socket clientSocket = null;  // socket created by accept
			try {
				clientSocket = this.serverSocket.accept(); // wait for a client to connect
				new WorkerRunnable(clientSocket).start();

			} catch (IOException e) {
				if(isStopped()) {
					return;
				}
				throw new RuntimeException(
						"Error accepting client connection", e);    //Accept failed
			}
		}   

	}

	 
	private synchronized boolean isStopped() {
		return this.isStopped;
	}

	 
	public synchronized void stop(){
		this.isStopped = true;
		try {
			synchronized(this.usersList)
			{
				for (Map.Entry<String, WorkerRunnable> entry : this.usersList.entrySet()) {
					WorkerRunnable socketThread = entry.getValue();
					socketThread.out.println("<disconnect><" + entry.getKey() + ">");
					socketThread.out.flush();

					socketThread.clientSocket.close();
				}
				this.usersList.clear();
			}
			if(!this.serverSocket.isClosed())
				this.serverSocket.close();
			this.serverFrame.setMsgBoxText("Server is closed");

		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}

	}


	 
	class WorkerRunnable extends Thread{

		 
		protected Socket clientSocket;
		public PrintWriter out;
		private BufferedReader in;
		private int typeOfMsg = 0;

		
		public WorkerRunnable(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}

		public StringTokenizer splitMsg(String msg) {
			StringTokenizer st = new StringTokenizer(msg, "<>");
			return st;
		}

		
		public void sendToAll(String msg, String from, boolean showUSer) {
			for (Map.Entry<String, WorkerRunnable> entry : usersList.entrySet()) {
				if(showUSer)
				{
					sendToUser(from, entry.getKey(), msg);
				} else
					entry.getValue().out.println(msg);
				entry.getValue().out.flush();
			}
		}

		public void sendToUser(String from, String to, String Msg)
		{
			synchronized (usersList) {
				if(usersList.containsKey(to)) // if username exists
				{
					WorkerRunnable userThread = usersList.get(to);
					userThread.out.println("<set_msg><" + from + "><" + Msg + ">");
					userThread.out.flush();
				}
				else
				{
					this.out.println("<userNameIsNotOnline><" + to + ">");
					this.out.flush();
				}
			}
		}

		
		public void disconnect(String userName) {       
			try {
				synchronized (usersList) {
					usersList.remove(userName);
				}
				new Thread(new Runnable() {

					@Override
					public void run() {
						String msg = "";
						if(typeOfMsg == 1)
							msg = "system:" + "<msg><client "+ userName +" leaved>\n";
						sendToAll(msg + "client " + userName + " leaved", "", false);
					}
				}).start();

				serverFrame.setMsgBoxText("client " + userName +" leaved");
				this.clientSocket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		public void showAllUsers(String userName) {
			synchronized (usersList) {
				WorkerRunnable userThread = usersList.get(userName);
				String usersListMsg = "";
				for (Map.Entry<String, WorkerRunnable> entry : usersList.entrySet()) {
					usersListMsg += entry.getKey() + ",";
				}
				if(usersListMsg.length() > 0)
					usersListMsg = usersListMsg.substring(0, usersListMsg.length()-1);
				
				userThread.out.println("<get_users><" + usersListMsg + ">");
				userThread.out.flush();
			}
		}

		
		public void userConnect(String userName) {
			if(userName == null) return;

			synchronized (usersList) {
				if(!usersList.containsValue(userName)) // if username not exists
				{
					this.out.println("<connected><" + userName + ">");
					usersList.put(userName, this);
					this.out.flush();

					new Thread(new Runnable() {

						@Override
						public void run() {
							String msg = "";
							if(typeOfMsg == 1)
								msg = "system:" + "<msg><client "+ userName +" entered>\n";
							sendToAll(msg + "client " + userName + " entered", "", false);
						}
					}).start();

					serverFrame.setMsgBoxText("client " + userName +" entered");
				}
				else {
					this.out.println("<userNameExists><" + userName + "><" + typeOfMsg + ">");
					this.out.flush();
				}
			}
		}

		
		@Override
		public void run() {
			try {
				out = new PrintWriter(this.clientSocket.getOutputStream());
				in = new BufferedReader(
						new InputStreamReader(this.clientSocket.getInputStream()));
				String msg;
				while (!isStopped() && (msg = in.readLine()) != null) {
					StringTokenizer m = this.splitMsg(msg);
					if(m != null)
					{
						if(msg.indexOf("<connect>") > -1) // user connected
						{
							m.nextToken();
							String userName = m.nextToken();
							this.typeOfMsg = Integer.parseInt(m.nextToken());
							userConnect(userName);
						}
						if(msg.indexOf("<disconnect>") > -1)
						{
							m.nextToken();
							String userName = m.nextToken();
							this.disconnect(userName);
						}
						if(msg.indexOf("<set_msg>") > -1)
						{
							m.nextToken();
							String from = m.nextToken();
							String to = m.nextToken();
							String msgText = m.nextToken();
							sendToUser(from, to, msgText);
						}
						if(msg.indexOf("<set_msg_all>") > -1)
						{
							m.nextToken();
							String from = m.nextToken();
							String msgText = m.nextToken();
							sendToAll(msgText, from, true);
						}
						if(msg.indexOf("<get_users>") > -1)
						{
							m.nextToken();
							String userName = m.nextToken();
							showAllUsers(userName);
						}
					}
				}

			} catch (IOException e) {
			}
		}
	}
}
