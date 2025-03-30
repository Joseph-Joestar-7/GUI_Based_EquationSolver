import javax.swing.*;



import java.awt.*;
import java.awt.event.*;

public class MatrixSolverPage extends JFrame {
    private JTextField nField;             // Field for entering number of equations n
    private JButton createFieldsButton;    // Button to create input fields
    private JPanel matrixPanel;            // Panel for the matrix input fields
    private JTextField[][] aFields;        // 2D array for matrix A coefficients
    private JTextField[] bFields;          // 1D array for vector B constants
    private JButton solveButton;           // Solve button
    private JButton backButton;            // Back button
    private JTextArea outputArea;          // Output area to display result
    private JScrollPane matrixScrollPane;  // Scroll pane for matrixPanel
    private JComboBox<String> methodBox;   // Dropdown to select solving method

    public MatrixSolverPage() {
        setTitle("Matrix Equation Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top panel: Enter number of equations n and create fields.
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Enter the number of equations (n):"));
        nField = new JTextField(5);
        topPanel.add(nField);
        createFieldsButton = new JButton("Create Matrix Fields");
        topPanel.add(createFieldsButton);
        add(topPanel, BorderLayout.NORTH);

        // Center panel: Matrix input fields.
        matrixPanel = new JPanel(new GridBagLayout());
        matrixScrollPane = new JScrollPane(matrixPanel);
        matrixScrollPane.setPreferredSize(new Dimension(720, 300));
        add(matrixScrollPane, BorderLayout.CENTER);

        // Bottom panel: Method selection, Solve and Back buttons, and output area.
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Dropdown for selecting solving method.
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

        // Action: Create input fields when requested.
        createFieldsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createInputFields();
            }
        });

        // Action: Solve button calls EquationFactory and then the appropriate solving method.
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solveMatrixEquation();
            }
        });

        // Back button returns to MainPage.
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new MainPage();
            }
        });

        setVisible(true);
    }

    /**
     * Dynamically creates input fields for an n×n matrix A and an n×1 vector B.
     */
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

        // Create input fields for matrix A.
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

        // Create input fields for vector B.
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

    /**
     * Collects the input, creates a two-line string, calls EquationFactory,
     * casts to MatrixEquation, then calls the solving method based on the selected method.
     */
    private void solveMatrixEquation() {
        int n;
        try {
            n = Integer.parseInt(nField.getText());
        } catch (NumberFormatException ex) {
            outputArea.setText("Invalid n.");
            return;
        }
        
        // Build the first line (matrix A) and second line (vector B).
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
        
        // Combine into a two-line input.
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
