package Server;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JDesktopPane;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
 
public class serverGui extends JFrame {
 
	private static final long serialVersionUID = 8305345318885925787L;
 
	private JPanel contentPane;
	private JButton btnStart;
	private JTextArea msgBox;
 
	public serverGui() {
		setTitle("Chat - Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBackground(Color.WHITE);
		contentPane.add(desktopPane, BorderLayout.CENTER);
		
		btnStart = new JButton("Start");
		btnStart.setForeground(Color.BLACK);
		btnStart.setFont(new Font("Arial", Font.BOLD, 13));
		btnStart.setBounds(10, 11, 100, 33);
		desktopPane.add(btnStart);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(UIManager.getBorder("EditorPane.border"));
		scrollPane.setBounds(10, 55, 404, 186);
		scrollPane.getViewport().setBackground(Color.WHITE);
		
		msgBox = new JTextArea();
		msgBox.setFont(new Font("Arial", Font.PLAIN, 12));
		msgBox.setEditable(false);
		msgBox.setLineWrap(true);
		msgBox.setWrapStyleWord(true);
		scrollPane.setViewportView(msgBox);
		
		desktopPane.add(scrollPane);
	}
 
	
	public void setMsgBoxText(String text) {
		if(text != null && !text.isEmpty())
			this.msgBox.append(text + "\n");
		else
			this.msgBox.setText("");
	}
	
	 
	public JButton getStartBtn() {
		return this.btnStart;
	}

}
