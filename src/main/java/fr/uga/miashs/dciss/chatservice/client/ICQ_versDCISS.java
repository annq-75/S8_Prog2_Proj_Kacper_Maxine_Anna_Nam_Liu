package fr.uga.miashs.dciss.chatservice.client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import java.awt.Rectangle;
import java.net.UnknownHostException;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;

public class ICQ_versDCISS {

	private JFrame frame;
	private ClientMsg client;
	private JTextArea textArea_msgs;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ICQ_versDCISS window = new ICQ_versDCISS();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ICQ_versDCISS() {
	    try {
	        initialize();
	    } catch (UnknownHostException e) {
	        e.printStackTrace();
	        // show err:
	        JOptionPane.showMessageDialog(null, "Connection to server is impossible.");
	    }
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws UnknownHostException 
	 */
	private void initialize() throws UnknownHostException {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client = new ClientMsg("localhost", 1666); 
		//Un objet ClientMsg est créé, qui se connecte au serveur à l’adresse localhost 
		//(c’est-à-dire sur le même ordinateur) et au port 1666.
		
		
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JPanel panel_6 = new JPanel();
		panel.add(panel_6);
		
		JLabel lblNewLabel = new JLabel("Your number");
		lblNewLabel.setForeground(new Color(0, 128, 128));
		panel_6.add(lblNewLabel);
		
		JPanel panel_showid = new JPanel();
		panel_6.add(panel_showid);
		
		JTextArea textArea_showid = new JTextArea();
		textArea_showid.setBackground(UIManager.getColor("CheckBox.background"));
		panel_showid.add(textArea_showid);
		
		//start session
		client.startSession();
		textArea_showid.setText("" + client.getIdentifier());
		
		JPanel panel_7 = new JPanel();
		panel.add(panel_7);
		panel_7.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblNewLabel_1 = new JLabel("Send to (number)");
		lblNewLabel_1.setHorizontalTextPosition(SwingConstants.CENTER);
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setForeground(new Color(0, 128, 128));
		panel_7.add(lblNewLabel_1);
		
		JPanel panel_nickshow = new JPanel();
		panel_7.add(panel_nickshow);
		
		JTextArea textArea_SendTO = new JTextArea();
		textArea_SendTO.setColumns(1);
		textArea_SendTO.setBackground(UIManager.getColor("CheckBox.light"));
		panel_nickshow.add(textArea_SendTO);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.WEST);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_10 = new JPanel();
		panel_1.add(panel_10);
		panel_10.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lbl_online = new JLabel("     Online     ");
		lbl_online.setForeground(new Color(46, 139, 87));
		lbl_online.setBounds(new Rectangle(50, 50, 50, 50));
		lbl_online.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_10.add(lbl_online);
		
		JPanel panel_for_list_online = new JPanel();
		panel_10.add(panel_for_list_online);
		panel_for_list_online.setLayout(new BorderLayout(0, 0));
		
		JList list_online = new JList();
		panel_for_list_online.add(list_online);
		
		JPanel panel_11 = new JPanel();
		panel_1.add(panel_11);
		panel_11.setLayout(new GridLayout(0, 1, 0, 0));
		
		JLabel lblNewLabel_3 = new JLabel("     Off line     ");
		lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_3.setBounds(new Rectangle(50, 50, 50, 50));
		lblNewLabel_3.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		lblNewLabel_3.setForeground(new Color(128, 0, 0));
		panel_11.add(lblNewLabel_3);
		
		JPanel panel_13 = new JPanel();
		panel_11.add(panel_13);
		panel_13.setLayout(new BorderLayout(0, 0));
		
		JList list_1 = new JList();
		panel_13.add(list_1);
		
		JPanel panel_2 = new JPanel();
		frame.getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));
		
		JPanel panel_8 = new JPanel();
		panel_8.setBounds(new Rectangle(50, 50, 50, 50));
		panel_8.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_2.add(panel_8);
		
		//JTextArea textArea_msgs = new JTextArea();
		//textArea_msgs = new JTextArea();
		//textArea_msgs.setBounds(new Rectangle(70, 70, 70, 70));
		//panel_2.add(textArea_msgs);
		
		textArea_msgs = new JTextArea(10, 30); // strs and clmns
		textArea_msgs.setLineWrap(true);
		textArea_msgs.setWrapStyleWord(true);
		textArea_msgs.setEditable(false); // user have not to change the messages that are comming
		JScrollPane scrollPane = new JScrollPane(textArea_msgs);
		panel_2.add(scrollPane);
		
		/*client.addMessageListener(p -> {
		    String message = p.srcId + " says to " + p.destId + ": " + new String(p.data) + "\n";
		    EventQueue.invokeLater(() -> textArea_msgs.append(message)); 
		    //EventQueue.invokeLater() est utilisé pour mettre à jour 
		    //l’interface utilisateur dans le thread de l’interface graphique (UI).
		//------------------------------------
		});*/
		
		client.addMessageListener(p -> {
		    String msg = p.srcId + " says: " + new String(p.data) + "\n";
		    EventQueue.invokeLater(() -> textArea_msgs.append(msg));
		});
		
		JPanel panel_3 = new JPanel();
		frame.getContentPane().add(panel_3, BorderLayout.SOUTH);
		panel_3.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(new Rectangle(70, 70, 70, 70));
		panel_4.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_3.add(panel_4);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.X_AXIS));
		
		JButton btn_send = new JButton("Send");
		btn_send.setForeground(new Color(0, 128, 128));
		panel_4.add(btn_send);
		

		
		JLabel labelMessage = new JLabel("     Message:");
		labelMessage.setForeground(new Color(0, 128, 128));
		panel_4.add(labelMessage);
		
		JPanel panel_16 = new JPanel();
		panel_3.add(panel_16);
		panel_16.setLayout(new BorderLayout(0, 0));
		
		JTextArea textArea_yourmsg = new JTextArea();
		textArea_yourmsg.setBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(127, 255, 212), new Color(0, 128, 128)));
		panel_16.add(textArea_yourmsg);
		
		btn_send.addActionListener(e -> {
		    try {
		        // Supposons que l’utilisateur saisit l’ID du destinataire dans le textArea_SendTO.
		        int dest = Integer.parseInt(textArea_SendTO.getText().trim());
		        String msg = textArea_yourmsg.getText();

		        client.sendPacket(dest, msg.getBytes());
		        textArea_yourmsg.setText(""); // permet de nettoyer le champ de saisie
		    } catch (NumberFormatException ex) {
		        textArea_msgs.append("Invalid recipient ID\n");
		    }
		});
		
		JPanel panel_17 = new JPanel();
		panel_3.add(panel_17);
		
		JPanel panel_5 = new JPanel();
		frame.getContentPane().add(panel_5, BorderLayout.EAST);
		
		JPanel panel_14 = new JPanel();
		panel_5.add(panel_14);
		
		JPanel panel_15 = new JPanel();
		panel_5.add(panel_15);
		
		//close session correct
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent e) {
		        client.closeSession();
		    }
		});
	}

}
