package fr.uga.miashs.dciss.chatservice.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;

import java.awt.GridLayout;
import java.awt.Dimension;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import fr.uga.miashs.dciss.chatservice.server.GroupMsg;

public class GroupWindow extends JFrame {

	private final ClientMsg client;
	private JTextField textField_nmb_mem;
	private JTextArea textArea;
	private JTextField textField;

	public GroupWindow(ClientMsg client) {
		this.client = client;
		setSize(800, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		initUI(); // выносим UI в отдельный метод
		registerGroupListener(); // подключаем listener
	}

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */

	private void initUI() {
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);

		JLabel lblNewLabel_1 = new JLabel("Your number");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNewLabel_1.setForeground(new Color(0, 128, 128));
		panel.add(lblNewLabel_1);

		JPanel panel_showid = new JPanel();
		panel.add(panel_showid);

		JTextArea textArea_showid = new JTextArea();
		textArea_showid.setForeground(new Color(47, 79, 79));
		// textArea_showid.setText("0");

		textArea_showid.setText("" + client.getIdentifier());// write id

		textArea_showid.setPreferredSize(new Dimension(18, 22));
		textArea_showid.setMinimumSize(new Dimension(18, 22));
		textArea_showid.setBackground(new Color(245, 255, 250));
		panel_showid.add(textArea_showid);

		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.SOUTH);

		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.WEST);

		JPanel panel_3 = new JPanel();
		contentPane.add(panel_3, BorderLayout.EAST);

		JPanel panel_main = new JPanel();
		panel_main.setBounds(new Rectangle(30, 30, 30, 30));
		panel_main.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		contentPane.add(panel_main, BorderLayout.CENTER);
		panel_main.setLayout(new BorderLayout(0, 0));

		JPanel panel_10 = new JPanel();
		panel_main.add(panel_10, BorderLayout.WEST);

		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_main.add(panel_4, BorderLayout.NORTH);
		panel_4.setLayout(new GridLayout(0, 4, 0, 0));

		JPanel panel_7 = new JPanel();
		panel_7.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_7.setBounds(new Rectangle(30, 30, 30, 30));
		panel_4.add(panel_7);

		JLabel lbl_gr_choice = new JLabel("Choose a group :");
		lbl_gr_choice.setForeground(new Color(0, 128, 128));
		panel_7.add(lbl_gr_choice);

		JPanel panel_8 = new JPanel();
		panel_8.setBounds(new Rectangle(30, 30, 30, 30));
		panel_8.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_4.add(panel_8);

		textField = new JTextField();
		panel_8.add(textField);
		textField.setColumns(10);

		JPanel panel_9 = new JPanel();
		panel_9.setBounds(new Rectangle(30, 30, 30, 30));
		panel_9.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_4.add(panel_9);

		JButton btn_gr_change = new JButton("Change");
		btn_gr_change.setForeground(new Color(0, 128, 128));
		panel_9.add(btn_gr_change);

		JPanel panel_11 = new JPanel();
		panel_11.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_11.setBounds(new Rectangle(30, 30, 30, 30));
		panel_4.add(panel_11);

		JButton btnNewButton_1 = new JButton("Delete");
		btnNewButton_1.setForeground(new Color(128, 0, 0));
		panel_11.add(btnNewButton_1);

		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_main.add(panel_5, BorderLayout.SOUTH);
		panel_5.setLayout(new GridLayout(0, 3, 0, 0));

		JPanel panel_12 = new JPanel();
		panel_12.setBounds(new Rectangle(30, 30, 30, 30));
		panel_12.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_5.add(panel_12);

		JButton btnCreate = new JButton("Create group");
		btnCreate.addActionListener(e -> {
			try {
				int nb = Integer.parseInt(textField_nmb_mem.getText());
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bos);
				dos.writeByte(1); // type = create
				dos.writeInt(nb);
				for (int i = 0; i < nb; i++) {
					dos.writeInt(client.getIdentifier());
				}
				dos.flush();
				client.sendPacket(0, bos.toByteArray());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		btnCreate.setForeground(new Color(0, 128, 0));
		panel_12.add(btnCreate);

		JPanel panel_13 = new JPanel();
		panel_13.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_13.setBounds(new Rectangle(30, 30, 30, 30));
		panel_5.add(panel_13);
		panel_13.setLayout(new GridLayout(0, 2, 0, 0));

		JLabel lblNewLabel = new JLabel("Nmb of members");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(new Rectangle(30, 30, 30, 30));
		lblNewLabel.setForeground(new Color(0, 128, 128));
		panel_13.add(lblNewLabel);

		textField_nmb_mem = new JTextField();
		textField_nmb_mem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panel_13.add(textField_nmb_mem);
		textField_nmb_mem.setColumns(10);

		JPanel panel_15 = new JPanel();
		panel_15.setBounds(new Rectangle(30, 30, 30, 30));
		panel_15.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_5.add(panel_15);

		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(e -> {
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put((byte) 4); // type = refresh
			client.sendPacket(0, buffer.array());
		});

		btnRefresh.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnRefresh.setForeground(new Color(0, 128, 128));
		panel_15.add(btnRefresh);

		JPanel panel_6 = new JPanel();
		panel_main.add(panel_6, BorderLayout.CENTER);
		panel_6.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel_6.add(scrollPane);

		textArea = new JTextArea();
		textArea.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		textArea.setBackground(new Color(245, 255, 250));
		scrollPane.setViewportView(textArea);

		JPanel panel_14 = new JPanel();
		panel_6.add(panel_14, BorderLayout.NORTH);

		JLabel lbl_your_groups = new JLabel("Your groups");
		lbl_your_groups.setFont(new Font("Tahoma", Font.BOLD, 11));
		lbl_your_groups.setForeground(new Color(0, 128, 128));
		panel_14.add(lbl_your_groups);

		JPanel panel_16 = new JPanel();
		panel_main.add(panel_16, BorderLayout.EAST);
	}

	private void registerGroupListener() {
		client.addMessageListener(p -> {
			try {
				if (p.data.length > 0 && p.data[0] == 4) {
					String msg = new String(p.data, 1, p.data.length - 1);
					updateGroupListFromString(msg);
				}
			} catch (Exception e) {
				System.err.println("Ошибка при чтении ответа сервера");
			}
		});
	}

	public void updateGroupListFromString(String data) {
		SwingUtilities.invokeLater(() -> {
			textArea.setText("");
			if (data == null || data.isEmpty())
				return;
			for (String id : data.split(",")) {
				if (!id.isBlank()) {
					textArea.append("Group ID: " + id + "\n");
				}
			}
		});
	}

}
