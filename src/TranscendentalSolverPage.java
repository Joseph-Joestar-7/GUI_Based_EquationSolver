import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TranscendentalSolverPage extends JFrame {
    private JTextField xMinField, xMaxField, stepField, tolField;
    private JButton solveButton, backButton, plotGraphButton;
    private JComboBox<String> methodBox, equationBox;
    private JTextArea outputArea;
    private int userId; 

    public TranscendentalSolverPage(int userId) {
        this.userId = userId;
        
        setTitle("Transcendental Equation Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("f(x):"), gbc);
        
        String[] historyEquations = fetchEquationHistory();
        equationBox = new JComboBox<>(historyEquations);
        equationBox.setEditable(true);
        equationBox.setPreferredSize(new Dimension(350, 35)); 
        equationBox.setSelectedItem("");
        gbc.gridx = 1;
        topPanel.add(equationBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("x_min:"), gbc);
        xMinField = new JTextField("-10", 20);
        gbc.gridx = 1;
        topPanel.add(xMinField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("x_max:"), gbc);
        xMaxField = new JTextField("10", 20);
        gbc.gridx = 1;
        topPanel.add(xMaxField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        topPanel.add(new JLabel("Step:"), gbc);
        stepField = new JTextField("0.1", 20);
        gbc.gridx = 1;
        topPanel.add(stepField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        topPanel.add(new JLabel("Tolerance:"), gbc);
        tolField = new JTextField("1e-6", 20);
        gbc.gridx = 1;
        topPanel.add(tolField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        topPanel.add(new JLabel("Select Method:"), gbc);
        methodBox = new JComboBox<>(new String[]{"Bisection", "Regula Falsi", "Newton-Raphson"});
        gbc.gridx = 1;
        topPanel.add(methodBox, gbc);

        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        solveButton = new JButton("Solve");
        backButton = new JButton("Back");
        plotGraphButton = new JButton("Plot Graph");
        solveButton.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        backButton.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        plotGraphButton.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        buttonPanel.add(solveButton);
        buttonPanel.add(plotGraphButton);
        buttonPanel.add(backButton);
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        outputArea = new JTextArea(15, 60);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.CENTER);

        solveButton.addActionListener(e -> solveTranscendentalEquation());
        plotGraphButton.addActionListener(e -> plotGraph());
        backButton.addActionListener(e -> {
            dispose();
            new MainPage();
        });

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

    private void saveEquation(String equation) {
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

    private void solveTranscendentalEquation() {
        String expr = (String) equationBox.getSelectedItem();
        if (expr == null || expr.trim().isEmpty()) {
            outputArea.setText("Please enter a function f(x).");
            return;
        }

        String params = xMinField.getText().trim() + " " +
                        xMaxField.getText().trim() + " " +
                        stepField.getText().trim() + " " +
                        tolField.getText().trim();
        String input = expr + "\n" + params;

        try {
            Equation eq = EquationFactory.createEquation("transcendental", input);
            if (!(eq instanceof TranscendentalEquation)) {
                outputArea.setText("Parsed equation is not a TranscendentalEquation.");
                return;
            }

            TranscendentalEquation tEq = (TranscendentalEquation) eq;
            String method = (String) methodBox.getSelectedItem();
            EquationResult result;

            switch (method.toLowerCase()) {
                case "bisection":
                    result = tEq.solveBisection();
                    break;
                case "regula falsi":
                    result = tEq.solveRegulaFalsi();
                    break;
                case "newton-raphson":
                    result = tEq.solveNewtonRaphson();
                    break;
                default:
                    result = new EquationResult("Unknown method selected.");
            }

            outputArea.setText(result.toString());
            saveEquation(expr); 
        } catch (EquationParseException ex) {
            outputArea.setText("Parse Error: " + ex.getMessage());
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }

    private void plotGraph() {
        String expr = (String) equationBox.getSelectedItem();
        if (expr == null || expr.trim().isEmpty()) {
            outputArea.setText("Please enter a function f(x) to plot.");
            return;
        }

        String params = xMinField.getText().trim() + " " +
                        xMaxField.getText().trim() + " " +
                        stepField.getText().trim() + " " +
                        tolField.getText().trim();
        String input = expr + "\n" + params;

        try {
            Equation eq = EquationFactory.createEquation("transcendental", input);
            if (!(eq instanceof TranscendentalEquation)) {
                outputArea.setText("Parsed equation is not a TranscendentalEquation.");
                return;
            }
            GraphWindow graphWindow = new GraphWindow(eq);
            graphWindow.setVisible(true);
        } catch (EquationParseException ex) {
            outputArea.setText("Parse Error: " + ex.getMessage());
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }
}
