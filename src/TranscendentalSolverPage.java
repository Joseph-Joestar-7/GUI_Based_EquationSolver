import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TranscendentalSolverPage extends JFrame {
    private JTextField equationField, xMinField, xMaxField, stepField, tolField;
    private JButton solveButton, backButton, plotGraphButton;
    private JComboBox<String> methodBox;
    private JTextArea outputArea;

    public TranscendentalSolverPage() {
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
        equationField = new JTextField("3*x^3 + 10*x^2 + 10*x + 7", 25);
        gbc.gridx = 1;
        topPanel.add(equationField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("x_min:"), gbc);
        xMinField = new JTextField("-10", 10);
        gbc.gridx = 1;
        topPanel.add(xMinField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        topPanel.add(new JLabel("x_max:"), gbc);
        xMaxField = new JTextField("10", 10);
        gbc.gridx = 1;
        topPanel.add(xMaxField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        topPanel.add(new JLabel("Step:"), gbc);
        stepField = new JTextField("0.1", 10);
        gbc.gridx = 1;
        topPanel.add(stepField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        topPanel.add(new JLabel("Tolerance:"), gbc);
        tolField = new JTextField("1e-6", 10);
        gbc.gridx = 1;
        topPanel.add(tolField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        topPanel.add(new JLabel("Select Method:"), gbc);
        methodBox = new JComboBox<>(new String[] {"Bisection", "Regula Falsi", "Newton-Raphson"});
        gbc.gridx = 1;
        topPanel.add(methodBox, gbc);

        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        solveButton = new JButton("Solve");
        backButton = new JButton("Back");
        plotGraphButton = new JButton("Plot Graph");
        buttonPanel.add(solveButton);
        buttonPanel.add(plotGraphButton);
        buttonPanel.add(backButton);
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        outputArea = new JTextArea(15, 60);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.CENTER);

        solveButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                solveTranscendentalEquation();
            }
        });

        plotGraphButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String expr = equationField.getText().trim();
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
        });

        backButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                dispose();
                new MainPage(); 
            }
        });

        setVisible(true);
    }

    private void solveTranscendentalEquation() {
        String expr = equationField.getText().trim();
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
            if (method.equalsIgnoreCase("Bisection")) {
                result = tEq.solveBisection();
            } else if (method.equalsIgnoreCase("Regula Falsi")) {
                result = tEq.solveRegulaFalsi();
            } else if (method.equalsIgnoreCase("Newton-Raphson")) {
                result = tEq.solveNewtonRaphson();
            } else {
                result = new EquationResult("Unknown method selected.");
            }
            outputArea.setText(result.toString());
        } catch (EquationParseException ex) {
            outputArea.setText("Parse Error: " + ex.getMessage());
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }
}
