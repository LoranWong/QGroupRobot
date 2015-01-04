package com.jackwong025.qgrouprobot.View;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Window.Type;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;

import java.awt.TextArea;

import javax.swing.JTextArea;

import com.jackwong025.qgrouprobot.Controller.DAO;
import com.jackwong025.qgrouprobot.Controller.RobotController;
import com.jackwong025.qgrouprobot.Model.User;

import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.awt.Toolkit;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField qq_number;
	private JTextField qq_password;
	private static JTextArea textArea;
	private RobotController robotController = null;
	private JTextField qgroup_number;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")));

		setResizable(false);
		setTitle("QGroupRobot -By JackWong ");
		setType(Type.POPUP);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 606, 737);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel LableNewLable = new JLabel("QQ账号:");
		LableNewLable.setBounds(14, 16, 84, 18);
		contentPane.add(LableNewLable);
		
		JLabel lblQqpass = new JLabel("密码:");
		lblQqpass.setBounds(205, 16, 47, 18);
		contentPane.add(lblQqpass);
		
		qq_number = new JTextField();
		qq_number.setText("1819832462");
		qq_number.setBounds(88, 13, 99, 24);
		contentPane.add(qq_number);
		qq_number.setColumns(10);
		
		qq_password = new JTextField();
		qq_password.setText("hzb199483");
		qq_password.setBounds(252, 13, 141, 24);
		contentPane.add(qq_password);
		qq_password.setColumns(10);
		
		JButton btnStart = new JButton("start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showMessage("登录中...");
				robotController = new RobotController(MainFrame.this, qq_number.getText(), qq_password.getText(),qgroup_number.getText());
				qq_password.setText("");
			}
		});
		btnStart.setBounds(473, 43, 113, 27);
		contentPane.add(btnStart);
		

		textArea = new JTextArea();
		textArea.setForeground(Color.BLACK);
		textArea.setFont(new Font("幼圆", Font.PLAIN, 15));
		textArea.setBounds(14, 74, 572, 555);
		textArea.setLineWrap(true);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(14, 74, 572, 555);
		contentPane.add(scrollPane);
		
		JLabel lblQq = new JLabel("QQ群号码:");
		lblQq.setBounds(14, 47, 84, 18);
		contentPane.add(lblQq);
		
		qgroup_number = new JTextField();
		qgroup_number.setText("192866846");
		qgroup_number.setColumns(10);
		qgroup_number.setBounds(88, 44, 99, 24);
		contentPane.add(qgroup_number);
	}
	
	public void showMessage(String msg) {
		textArea.setText(textArea.getText()+msg+"\r\n");
	}
}
