
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SplashScreenW extends JFrame {
    public SplashScreenW() {
        setTitle("Equation Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("EQUATION SOLVER", SwingConstants.CENTER);
        titleLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.CENTER);

        JButton tryItNowButton = new JButton("Try It Now");
        tryItNowButton.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(tryItNowButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        tryItNowButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage();
            }
        });

        add(panel);
        setVisible(true);
    }
}