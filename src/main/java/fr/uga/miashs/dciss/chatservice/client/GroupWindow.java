package fr.uga.miashs.dciss.chatservice.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.awt.event.ActionEvent;

public class GroupWindow extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//GroupWindow frame = new GroupWindow();
					//frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GroupWindow(int client_id, ClientMsg client) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);//я поменяла EXIT_ON_CLOSE на DISPOSE_ON_CLOSE чтобы вся программа не закрывалась, а только окно
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.WEST);
		
		JPanel panel_3 = new JPanel();
		contentPane.add(panel_3, BorderLayout.EAST);
		
		JPanel panel_main = new JPanel();
		contentPane.add(panel_main, BorderLayout.CENTER);
		panel_main.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_10 = new JPanel();
		panel_main.add(panel_10, BorderLayout.WEST);
		
		JPanel panel_4 = new JPanel();
		panel_main.add(panel_4, BorderLayout.NORTH);
		panel_4.setLayout(new GridLayout(0, 4, 0, 0));
		
		JPanel panel_7 = new JPanel();
		panel_4.add(panel_7);
		
		JLabel lbl_gr_choice = new JLabel("Choose a group :");
		lbl_gr_choice.setForeground(new Color(0, 128, 128));
		panel_7.add(lbl_gr_choice);
		
		JPanel panel_8 = new JPanel();
		panel_4.add(panel_8);
		
		textField = new JTextField();
		panel_8.add(textField);
		textField.setColumns(10);
		
		JPanel panel_9 = new JPanel();
		panel_4.add(panel_9);
		
		JButton btn_gr_change = new JButton("Change");
		btn_gr_change.setForeground(new Color(0, 128, 128));
		panel_9.add(btn_gr_change);
		
		JPanel panel_11 = new JPanel();
		panel_4.add(panel_11);
		
		JButton btnNewButton_1 = new JButton("Delete");
		btnNewButton_1.setForeground(new Color(128, 0, 0));
		panel_11.add(btnNewButton_1);
		
		JPanel panel_5 = new JPanel();
		panel_main.add(panel_5, BorderLayout.SOUTH);
		panel_5.setLayout(new GridLayout(0, 3, 0, 0));
		
		JPanel panel_12 = new JPanel();
		panel_12.setBounds(new Rectangle(30, 30, 30, 30));
		panel_12.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_5.add(panel_12);
		
		JButton btnNewButton = new JButton("Create a group :");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				ByteBuffer buffer = ByteBuffer.allocate(10);//création buffer  pour packet
				System.out.println("allocation buffer");
				buffer.put((byte) 1);//paquetage type un
				System.out.println("type de premier byte");
//				buffer.rewind(); // ← ВОТ ЭТО ВАЖНО
				
				String nb = textField_1.getText();
				int nbMembres = Integer.parseInt(nb);
				int[] idMembre = new int[nbMembres];
				
				for(int i = 0; i < nbMembres; i++) {//rajout de id des membres que l'on veut rajouter dans le groupe
				//pour l'instant sans users	
				}
				
				byte[] data = new byte[nbMembres];
				
				//-------test avec id = 1
				buffer.putInt(nbMembres);
				System.out.println("ajout de nombre de membres");
				
				buffer.putInt(1);
				System.out.println("rajout de un pour le premier id du premier membre");
				buffer.rewind(); // ← ВОТ ЭТО ВАЖНО
				buffer.get(data);
				client.sendPacket(0, data);
			}
		});
		btnNewButton.setForeground(new Color(0, 128, 0));
		panel_12.add(btnNewButton);
		
		JPanel panel_13 = new JPanel();
		panel_13.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_13.setBounds(new Rectangle(30, 30, 30, 30));
		panel_5.add(panel_13);
		panel_13.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel lblNewLabel = new JLabel("Nmb of members");
		lblNewLabel.setForeground(new Color(0, 128, 128));
		panel_13.add(lblNewLabel);
		
		textField_1 = new JTextField();
		panel_13.add(textField_1);
		textField_1.setColumns(10);
		
		JPanel panel_15 = new JPanel();
		panel_5.add(panel_15);
		
		JTextArea textArea_nbMb = new JTextArea();
		textArea_nbMb.setBackground(new Color(245, 255, 250));
		
		
		JButton btnNewButton_2 = new JButton("Refresh groups");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//montre les groupes dans "textArea"
				ByteBuffer buffer = ByteBuffer.allocate(5);//création buffer  pour packet
				System.out.println("allocation buffer");
				buffer.put((byte) 4);//paquetage type un
				System.out.println("type de premier byte");
				
				byte[] data = new byte[1];
				buffer.rewind(); // ← ВОТ ЭТО ВАЖНО
				buffer.get(data);
				String groups;
			}
		});
		btnNewButton_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNewButton_2.setForeground(new Color(0, 128, 128));
		panel_15.add(btnNewButton_2);
		
		JPanel panel_6 = new JPanel();
		panel_main.add(panel_6, BorderLayout.CENTER);
		panel_6.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_6.add(scrollPane);
		
		scrollPane.setViewportView(textArea_nbMb);
		
		JPanel panel_14 = new JPanel();
		panel_6.add(panel_14, BorderLayout.NORTH);
		
		JLabel lbl_your_groups = new JLabel("Your groups");
		lbl_your_groups.setFont(new Font("Tahoma", Font.BOLD, 11));
		lbl_your_groups.setForeground(new Color(0, 128, 128));
		panel_14.add(lbl_your_groups);
	}

}
