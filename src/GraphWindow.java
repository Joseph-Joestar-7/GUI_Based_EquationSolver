import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GraphWindow extends JFrame {
    public GraphWindow(Equation equation) {
        setTitle("Graph of Equation");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GraphPanel graphPanel = new GraphPanel(equation);
        add(graphPanel);

        JButton exportButton = new JButton("Export Graph");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphPanel.exportToImage(); 
            }
        });

        add(exportButton, "South");

        setLocationRelativeTo(null);
    }
}
