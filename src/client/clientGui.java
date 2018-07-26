package client;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JDesktopPane;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
 
public class clientGui extends JFrame {
 
	private static final long serialVersionUID = -7608126090829634528L;

	
	private JPanel contentPane;
	private JTextField nameField;
	private JTextField txtLocalhost;
	private JTextField curNameField;
	private JTextField msgField;
	private JTextArea msgBox;
	private JButton btnConnect, btnSend, btnShowOnline;
	private JCheckBox typeOfMsgs;

	
	public clientGui() {
		initialize();
	}
	 
	private void initialize() {
	setTitle("Chat - Client");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(100, 100, 730, 382);
	contentPane = new JPanel();
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	contentPane.setLayout(new BorderLayout(0, 0));
	setContentPane(contentPane);
	
	JDesktopPane desktopPane = new JDesktopPane();
	desktopPane.setBackground(Color.WHITE);
	contentPane.add(desktopPane, BorderLayout.CENTER);
	
	btnConnect = new JButton("Connect");
	btnConnect.setFont(new Font("Arial", Font.BOLD, 13));
	btnConnect.setForeground(Color.BLACK);
	btnConnect.setBounds(10, 11, 107, 33);
	desktopPane.add(btnConnect);
	
	JLabel lblNewLabel = new JLabel("Name:");
	lblNewLabel.setFont(new Font("Arial", Font.BOLD, 13));
	lblNewLabel.setBounds(124, 20, 46, 14);
	desktopPane.add(lblNewLabel);
	
	nameField = new JTextField();
	nameField.setHorizontalAlignment(SwingConstants.CENTER);
	nameField.setText("unnamed");
	nameField.setFont(new Font("Arial", Font.BOLD, 13));
	nameField.setBounds(180, 11, 100, 33);
	desktopPane.add(nameField);
	nameField.setColumns(10);
	
	JLabel lblAddress = new JLabel("Address:");
	lblAddress.setFont(new Font("Arial", Font.BOLD, 13));
	lblAddress.setBounds(290, 20, 62, 14);
	desktopPane.add(lblAddress);
	
	txtLocalhost = new JTextField();
	txtLocalhost.setText("localhost");
	txtLocalhost.setHorizontalAlignment(SwingConstants.CENTER);
	txtLocalhost.setFont(new Font("Arial", Font.BOLD, 13));
	txtLocalhost.setColumns(10);
	txtLocalhost.setBounds(349, 11, 100, 33);
	desktopPane.add(txtLocalhost);
	
	btnShowOnline = new JButton("Show Online");
	btnShowOnline.setForeground(Color.BLACK);
	btnShowOnline.setFont(new Font("Arial", Font.BOLD, 13));
	btnShowOnline.setBounds(459, 11, 130, 33);
	desktopPane.add(btnShowOnline);
	
	JButton btnClear = new JButton("Clear");
	btnClear.setForeground(Color.BLACK);
	btnClear.setFont(new Font("Arial", Font.BOLD, 13));
	btnClear.setBounds(599, 11, 72, 33);
	btnClear.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			setMsgBoxText("");
		}
	});
	
	desktopPane.add(btnClear);
	
	typeOfMsgs = new JCheckBox("");
	typeOfMsgs.setBackground(Color.WHITE);
	typeOfMsgs.setBounds(677, 11, 21, 33);
	desktopPane.add(typeOfMsgs);
	
	JScrollPane scrollPane = new JScrollPane();
	scrollPane.setViewportBorder(UIManager.getBorder("EditorPane.border"));
	scrollPane.setBounds(10, 55, 684, 226);
	scrollPane.getViewport().setBackground(Color.WHITE);
	
	msgBox = new JTextArea();
	msgBox.setFont(new Font("Arial", Font.PLAIN, 12));
	msgBox.setEditable(false);
	msgBox.setLineWrap(true);
	msgBox.setWrapStyleWord(true);
	scrollPane.setViewportView(msgBox);
	
	desktopPane.add(scrollPane);
	
	curNameField = new JTextField();
	curNameField.setText("all");
	curNameField.setHorizontalAlignment(SwingConstants.CENTER);
	curNameField.setFont(new Font("Arial", Font.BOLD, 13));
	curNameField.setColumns(10);
	curNameField.setBounds(10, 290, 107, 33);
	desktopPane.add(curNameField);
	
	msgField = new JTextField();
	msgField.setHorizontalAlignment(SwingConstants.LEFT);
	msgField.setFont(new Font("Arial", Font.BOLD, 13));
	msgField.setColumns(10);
	msgField.setBounds(129, 290, 477, 33);
	msgField.setBorder(BorderFactory.createCompoundBorder(
			msgField.getBorder(), 
	        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
	desktopPane.add(msgField);
	
	btnSend = new JButton("Send");
	btnSend.setEnabled(false);
	btnSend.setForeground(Color.BLACK);
	btnSend.setFont(new Font("Arial", Font.BOLD, 13));
	btnSend.setBounds(616, 290, 78, 33);
	desktopPane.add(btnSend);
}
	 
	public void setMsgBoxText(String text) {
		if(text != null && !text.isEmpty())
			this.msgBox.append(text + "\n");
		else
			this.msgBox.setText("");
	}
	 
	public JButton getConnectBtn() {
		return this.btnConnect;
	}
	 
	public JTextField getIp() {
		return this.txtLocalhost;
	}
	
	public JTextField getUserName() {
		return this.nameField;
	}
	
	public JTextField getMsgField() {
		return this.msgField;
	}
	
	public JButton getSendBtn() {
		return this.btnSend;
	}
	
	public String getCurName() {
		return this.curNameField.getText();
	}
	
	public JButton getOnlineBtn() {
		return this.btnShowOnline;
	}
	
	public int typeOfMsgs() {
		return this.typeOfMsgs.isSelected() ? 1 : 0;
	}
}
