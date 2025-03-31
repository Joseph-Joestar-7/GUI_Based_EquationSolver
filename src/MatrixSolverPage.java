import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MatrixSolverPage extends JFrame {
    private JTextField nField;             
    private JButton createFieldsButton;    
    private JPanel matrixPanel;            
    private JTextField[][] aFields;        
    private JTextField[] bFields;         
    private JButton solveButton;      
    private JButton backButton;           
    private JTextArea outputArea;         
    private JScrollPane matrixScrollPane; 
    private JComboBox<String> methodBox;   

    public MatrixSolverPage() {
        setTitle("Matrix Equation Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Enter the number of equations (n):"));
        nField = new JTextField(5);
        topPanel.add(nField);
        createFieldsButton = new JButton("Create Matrix Fields");
        topPanel.add(createFieldsButton);
        add(topPanel, BorderLayout.NORTH);

        matrixPanel = new JPanel(new GridBagLayout());
        matrixScrollPane = new JScrollPane(matrixPanel);
        matrixScrollPane.setPreferredSize(new Dimension(720, 300));
        add(matrixScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        methodBox = new JComboBox<>(new String[] {"Gaussian", "Jacobi", "Seidel"});
        buttonPanel.add(new JLabel("Select Method:"));
        buttonPanel.add(methodBox);
        
        solveButton = new JButton("Solve");
        solveButton.setEnabled(false);
        buttonPanel.add(solveButton);
        
        backButton = new JButton("Back");
        buttonPanel.add(backButton);
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        outputArea = new JTextArea(15, 60);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        bottomPanel.add(outputScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        createFieldsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createInputFields();
            }
        });

        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solveMatrixEquation();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new MainPage();
            }
        });

        setVisible(true);
    }

    private void createInputFields() {
        outputArea.setText("");
        matrixPanel.removeAll();
        int n;
        try {
            n = Integer.parseInt(nField.getText());
            if (n <= 0) {
                outputArea.setText("Please enter a positive integer for n.");
                return;
            }
        } catch (NumberFormatException ex) {
            outputArea.setText("Invalid n. Please enter a positive integer.");
            return;
        }

        aFields = new JTextField[n][n];
        bFields = new JTextField[n];

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                gbc.gridx = j * 2;
                gbc.gridy = i;
                matrixPanel.add(new JLabel("A(" + (i + 1) + "," + (j + 1) + "):"), gbc);
                gbc.gridx = j * 2 + 1;
                aFields[i][j] = new JTextField(5);
                matrixPanel.add(aFields[i][j], gbc);
            }
        }

        for (int i = 0; i < n; i++) {
            gbc.gridx = n * 2;
            gbc.gridy = i;
            matrixPanel.add(new JLabel("B(" + (i + 1) + "):"), gbc);
            gbc.gridx = n * 2 + 1;
            bFields[i] = new JTextField(5);
            matrixPanel.add(bFields[i], gbc);
        }
        matrixPanel.revalidate();
        matrixPanel.repaint();
        solveButton.setEnabled(true);
    }


    private void solveMatrixEquation() {
        int n;
        try {
            n = Integer.parseInt(nField.getText());
        } catch (NumberFormatException ex) {
            outputArea.setText("Invalid n.");
            return;
        }
        
        StringBuilder matrixBuilder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            StringBuilder rowBuilder = new StringBuilder();
            for (int j = 0; j < n; j++) {
                String value = aFields[i][j].getText().trim();
                if (value.isEmpty()) {
                    outputArea.setText("Please fill all matrix fields.");
                    return;
                }
                rowBuilder.append(value);
                if (j < n - 1) {
                    rowBuilder.append(" ");
                }
            }
            matrixBuilder.append(rowBuilder);
            if (i < n - 1) {
                matrixBuilder.append(";");
            }
        }
        
        StringBuilder vectorBuilder = new StringBuilder();
        for (int i = 0; i < n; i++) {
            String value = bFields[i].getText().trim();
            if (value.isEmpty()) {
                outputArea.setText("Please fill all vector fields.");
                return;
            }
            vectorBuilder.append(value);
            if (i < n - 1) {
                vectorBuilder.append(" ");
            }
        }
        
        String input = matrixBuilder.toString() + "\n" + vectorBuilder.toString();
        
        try {
            Equation eq = EquationFactory.createEquation("matrix", input);
            if (!(eq instanceof MatrixEquation)) {
                outputArea.setText("Parsed equation is not a MatrixEquation.");
                return;
            }
            MatrixEquation matrixEq = (MatrixEquation) eq;
            System.out.println(matrixEq.toString());
            String method = (String) methodBox.getSelectedItem();
            EquationResult result;
            if (method.equalsIgnoreCase("Gaussian")) {
                result = matrixEq.solveGaussian();
            } else if (method.equalsIgnoreCase("Jacobi")) {
                result = matrixEq.solveJacobi();
            } else if (method.equalsIgnoreCase("Seidel")) {
                result = matrixEq.solveSeidel();
            } else {
                result = new EquationResult("Unknown solving method.");
            }
            outputArea.setText(result.toString());
        } catch (EquationParseException ex) {
            outputArea.setText("Parse Error: " + ex.getMessage());
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
        }
    }
}
