import java.awt.EventQueue;
import java.awt.Font;
import java.sql.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

public class display implements ActionListener{
	 // JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.ibm.db2.jcc.DB2Driverr";  
	   static final String DB_URL = "jdbc:db2://localhost:50000/UOB60";

	   //  Database credentials
	   static final String USER = "devuser";
	   static final String PASS = "ACI@2018";

	private JFrame frame;
	private JTextField textField0, textField1, textField2, textField3, textField4, textField5, textField6, textField7, textField8;
	private JTextArea textArea, resultArea;
	private JButton b;
	private JButton button, execute;
	private JComboBox combodomains;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					display window = new display();
					window.frame.setVisible(true);
					window.frame.setExtendedState(window.frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public display() {
		initialize();
	}
	public void actionPerformed(ActionEvent e)
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs;
		java.util.Date date= new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());
		   
		if(e.getSource()==b) {
			      String sql = "UPDATE \n" + 
			      		" SONEDBA.PROPERTIES \n" + 
			      		" SET \n" + 
			      		" \"VALUE\" = '"+ textField3.getText() +"'," + 
			      		" PRIORITY = " + textField7.getText() + " , " +
			      		" APPLICATION = '" + textField8.getText() + "'," + 
			      		" \"UPDATEDTTM \"  = '" + timestamp + "'," +
			      		" \"UPDATEUSER \" = 'Tool' , " +
			      		" \"COREFLAG \" = 1 \n" +
			      		" WHERE \n"
			      		+ " DOMAIN = '" +  combodomains.getSelectedItem() + "' and"+
			      		" SUBDOMAIN = '"+ textField1.getText() +"' and" +
			      		" \"KEY\" = '"+ textField2.getText() +"' and" + 
			      		" \"LABEL\" = '"+ textField6.getText() +"'" ;
			      textArea.setText(sql);
		}
		
		if(e.getSource()==button) {
			      String sql = "INSERT INTO SONEDBA.PROPERTIES \n" + 
			      		" (DOMAIN, SUBDOMAIN, \"KEY\", \"VALUE\", \"LABEL\", PRIORITY, APPLICATION, \"UPDATEDTTM \", \"UPDATEUSER \", \"COREFLAG \") \n" + 
			      		"VALUES \n" + 
			      		" ('"+combodomains.getSelectedItem() 
			      		+"', '"+textField1.getText()
			      		+"', '"+textField2.getText()
			      		+"', '"+textField3.getText()
			      		+"', '"+textField6.getText()
			      		+"', "+textField7.getText()
			      		+", '"+textField8.getText()
			      		+"', '" + timestamp 
			      		+"', '" + "Tool"  
			      		+"', 1)" ;
			      textArea.setText(sql);
		}
		if(e.getSource()== execute) {
			try{
			      Class.forName("com.ibm.db2.jcc.DB2Driver");
			      conn = DriverManager.getConnection(DB_URL, USER, PASS);
			      stmt = conn.createStatement();
			      String sql = textArea.getText();
			      //stmt.executeQuery(sql);
			      boolean result = stmt.execute(sql);
			      if(result == false)
			    	  resultArea.setText("SuccessFul");
			      
			   }catch(SQLException se){
			      //Handle errors for JDBC
			      se.printStackTrace();
			      resultArea.setText("SQL Exception Occurred");
			   }catch(Exception ea){
			      //Handle errors for Class.forName
			      ea.printStackTrace();
			      resultArea.setText("Exception Occurred");
			   }finally{
			      //finally block used to close resources
			      try{
			         if(stmt!=null)
			            conn.close();
			      }catch(SQLException se){
			      }// do nothing
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			         se.printStackTrace();
			      }
			   }
			
		}
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		
		frame.setBounds(100, 100, 640, 411);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		/*textField0 = new JTextField();
		textField0.setBounds(160, 27, 100, 20);
		frame.getContentPane().add(textField0);
		textField0.setColumns(10);*/
		combodomains = new JComboBox();
		combodomains.setBounds(160, 27, 100, 20);
		combodomains.addItem("Account");
		combodomains.addItem("Bank");
		combodomains.addItem("ep");
		combodomains.addItem("soa");
		frame.getContentPane().add(combodomains);
		
		textField1 = new JTextField();
		textField1.setBounds(450, 27, 100, 20);
		frame.getContentPane().add(textField1);
		textField1.setColumns(10);
		
		textField2 = new JTextField();
		textField3 = new JTextField();
		textField6 = new JTextField();
		textField7 = new JTextField();
		textField8 = new JTextField();

		
		textField2.setBounds(700, 27, 100, 20);
		textField3.setBounds(950, 27, 100, 20);
		textField8.setBounds(1250, 27, 100, 20);
		textField6.setBounds(175, 100, 100, 20);
		textField7.setBounds(450, 100, 100, 20);
		
		frame.getContentPane().add(textField2);
		frame.getContentPane().add(textField3);
		frame.getContentPane().add(textField6);
		frame.getContentPane().add(textField7);
		frame.getContentPane().add(textField8);
		
		JLabel domain = new JLabel("Enter Domain :");
		domain.setBounds(25, 27, 150, 18);
		domain.setFont(new Font("Arial", Font.BOLD,18));
		frame.getContentPane().add(domain);
		
		JLabel subdomain = new JLabel("Enter Sub-Domain :");
		subdomain.setBounds(275, 27, 175, 18);
		subdomain.setFont(new Font("Arial", Font.BOLD,18));
		frame.getContentPane().add(subdomain);
		
		JLabel key = new JLabel("Enter Key :");
		key.setBounds(575, 27, 150, 18);
		key.setFont(new Font("Arial", Font.BOLD,18));
		frame.getContentPane().add(key);
		
		JLabel value = new JLabel("Enter Value :");
		value.setBounds(825, 27, 150, 18);
		value.setFont(new Font("Arial", Font.BOLD,18));
		frame.getContentPane().add(value);
		
		JLabel application = new JLabel("Enter Application :");
		application.setBounds(1075, 27, 175, 18);
		application.setFont(new Font("Arial", Font.BOLD,18));
		frame.getContentPane().add(application);
		
		JLabel label = new JLabel("Enter Label :");
		label.setBounds(25, 100, 175, 18);
		label.setFont(new Font("Arial", Font.BOLD,18));
		frame.getContentPane().add(label);
		
		JLabel priority = new JLabel("Enter Priority :");
		priority.setBounds(300, 100, 150, 18);
		priority.setFont(new Font("Arial", Font.BOLD,18));
		frame.getContentPane().add(priority);
		
		b=new JButton("Update");
		b.setBounds(92,162,125,50);
		frame.getContentPane().add(b);
		b.addActionListener(this);
		
		b.setFont(new Font("Arial", Font.BOLD,18));
		
		textArea = new JTextArea();
		textArea.setBounds(40, 236, 1500, 200);
		textArea.setFont(new Font("Arial", Font.BOLD,14));
		frame.getContentPane().add(textArea);
		
		button = new JButton("Add");
		button.setBounds(388, 162, 125, 50);
		button.setFont(new Font("Arial", Font.BOLD,18));
		button.addActionListener(this);
		frame.getContentPane().add(button);
		
		execute = new JButton("Execute SQL");
		execute.setBounds(388, 500, 250, 50);
		execute.setFont(new Font("Arial", Font.BOLD,18));
		execute.addActionListener(this);
		frame.getContentPane().add(execute);
		
		resultArea = new JTextArea();
		resultArea.setBounds(240, 600, 500, 200);
		resultArea.setFont(new Font("Arial", Font.BOLD,14));
		frame.getContentPane().add(resultArea);
		
		
		
		
	}
}
