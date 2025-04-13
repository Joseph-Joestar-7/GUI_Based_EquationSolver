import javax.swing.*;
import java.awt.*;    
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SolverPage extends JFrame {
    public int userId; 
    public String equationType;
    
    public SolverPage(String equationType, int userId) {
        this.equationType = equationType;
        this.userId = userId;
        
        setTitle(equationType + " Equation Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel promptLabel = new JLabel("Enter the " + equationType + " equation:");
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        promptLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        panel.add(promptLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] historyEquations = fetchEquationHistory();
        JComboBox<String> equationInput = new JComboBox<>(historyEquations);
        equationInput.setEditable(true);
        equationInput.setMaximumSize(new Dimension(500, 30));

        equationInput.setSelectedItem("");
        panel.add(equationInput);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));

        JButton solveButton = new JButton("Solve");
        solveButton.setFont(new Font("JetBrains Mono", Font.BOLD, 16));
        buttonPanel.add(solveButton);

        JButton plotGraphButton = new JButton("Plot Graph");
        plotGraphButton.setFont(new Font("JetBrains Mono", Font.BOLD, 16));
        buttonPanel.add(plotGraphButton);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("JetBrains Mono", Font.BOLD, 16));
        buttonPanel.add(backButton);

        panel.add(buttonPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel resultLabel = new JLabel("Result:");
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setFont(new Font("JetBrains Mono", Font.BOLD, 18));
        panel.add(resultLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea resultArea = new JTextArea(5, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane);

        solveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String input = ((String) equationInput.getSelectedItem()).trim();
                if (input == null || input.isEmpty()) {
                    JOptionPane.showMessageDialog(SolverPage.this, "Please enter an equation.");
                    return;
                }
                saveEquationHistory(userId, input);
                try {
                    Equation eq = EquationFactory.createEquation(equationType, input);
                    EquationResult result = eq.solve();
                    resultArea.setText(result.toString());
                } catch (EquationParseException ex) {
                    resultArea.setText("Error: " + ex.getMessage());
                } catch (Exception ex) {
                    resultArea.setText("An unexpected error occurred: " + ex.getMessage());
                }
            }
        });

        plotGraphButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String input = ((String) equationInput.getSelectedItem()).trim();
                try {
                    if ("Matrix".equalsIgnoreCase(equationType)) {
                        JOptionPane.showMessageDialog(SolverPage.this,
                                "Graphing is not supported for this type",
                                "Not Supported",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    Equation eq = EquationFactory.createEquation(equationType, input);
                    GraphWindow graphWindow = new GraphWindow(eq);
                    graphWindow.setVisible(true);
                } catch (EquationParseException ex) {
                    JOptionPane.showMessageDialog(SolverPage.this,
                            "Error: " + ex.getMessage(),
                            "Parse Error",
                            JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SolverPage.this,
                            "An unexpected error occurred: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new MainPage();
            }
        });

        add(panel);
        setVisible(true);
    }

    private String[] fetchEquationHistory() {
        List<String> history = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()){
            PreparedStatement ps = conn.prepareStatement(
                "SELECT DISTINCT equation FROM history WHERE user_id = ? ORDER BY id DESC LIMIT 10"
            );
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                history.add(rs.getString("equation"));
            }
            rs.close();
            ps.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return history.toArray(new String[0]);
    }

    private void saveEquationHistory(int userId, String equation) {
        try (Connection conn = DBConnection.getConnection()){
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT 1 FROM history WHERE user_id = ? AND equation = ?"
            );
            checkStmt.setInt(1, userId);
            checkStmt.setString(2, equation);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                rs.close();
                checkStmt.close();
                return;
            }
            rs.close();
            checkStmt.close();
            
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO history (user_id, equation) VALUES (?, ?)"
            );
            ps.setInt(1, userId);
            ps.setString(2, equation);
            ps.executeUpdate();
            ps.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }
}
