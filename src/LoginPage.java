import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {
    public LoginPage() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        JTextField emailField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        JPasswordField passwordField = new JPasswordField(15);
        
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        
        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        panel.add(buttonPanel);
        
        loginButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                if(email.isEmpty() || password.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please enter both email and password.");
                    return;
                }
                try (Connection conn = DBConnection.getConnection()){
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?");
                    ps.setString(1, email);
                    ps.setString(2, password);
                    ResultSet rs = ps.executeQuery();
                    if(rs.next()){
                        int userID = rs.getInt("id");
                        String username = rs.getString("username");
                        JOptionPane.showMessageDialog(null, "Login successful!");
                        dispose();
                        new MainPage(username, userID); 
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid email or password.");
                    }
                    rs.close();
                    ps.close();
                } catch (SQLException ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                }
            }
        });
        
        registerButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegisterPage();
            }
        });
        
        add(panel);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        new LoginPage();
    }
}
