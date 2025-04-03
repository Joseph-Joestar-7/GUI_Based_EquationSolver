import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterPage extends JFrame {
    public RegisterPage() {
        setTitle("Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        JTextField usernameField = new JTextField(15);
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        JTextField emailField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        JPasswordField passwordField = new JPasswordField(15);
        
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");
        
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        panel.add(buttonPanel);
        
        registerButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                    return;
                }
                try (Connection conn = DBConnection.getConnection()){
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
                    ps.setString(1, email);
                    ResultSet rs = ps.executeQuery();
                    if(rs.next()){
                        JOptionPane.showMessageDialog(null, "User already exists. Please login.");
                        return;
                    }
                    rs.close();
                    ps.close();
                    
                    ps = conn.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
                    ps.setString(1, username);
                    ps.setString(2, email);
                    ps.setString(3, password); 
                    int rows = ps.executeUpdate();
                    if(rows > 0){
                        JOptionPane.showMessageDialog(null, "Registration successful. Please login.");
                        dispose();
                        new LoginPage();
                    } else {
                        JOptionPane.showMessageDialog(null, "Registration failed. Please try again.");
                    }
                    ps.close();
                } catch (SQLException ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                }
            }
        });
        
        backButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage();
            }
        });
        
        add(panel);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        new RegisterPage();
    }
}
