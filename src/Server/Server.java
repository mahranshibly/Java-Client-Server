package Server;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JButton;

import Server.serverListen.WorkerRunnable;


public class Server {
	
	private serverGui serverFrame;
	
	private serverListen listenThread;
	
	private final static int SERVERPORT = 5555; 
	
	private HashMap<String, WorkerRunnable> users;

	public Server() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					serverFrame = new serverGui();
					serverFrame.setVisible(true);
					users = new HashMap<String, WorkerRunnable>();
					startButton();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				stopListen();
			}
		});
	}


	
	public void startButton() {
		JButton start = serverFrame.getStartBtn();
		start.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(start.getText().equals("Start")) {
					serverFrame.setMsgBoxText("");
					start.setText("Close");
					startListen();
				} else {
					start.setText("Start");
					serverFrame.setMsgBoxText("");
					stopListen();
				}
			}
		});
	}

	
	public void startListen() {
		listenThread = new serverListen(SERVERPORT, serverFrame, users);
		new Thread(listenThread).start();
	}
	
	
	public void stopListen() {
		if(this.listenThread != null)
			this.listenThread.stop();
	}

	
	public static void main(String[] args) {
		@SuppressWarnings("unused")
		Server srv = new Server();
	}
}
