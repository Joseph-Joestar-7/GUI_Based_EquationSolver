import javax.swing.*;


public class GraphWindow extends JFrame {
    public GraphWindow(Equation equation) {
        setTitle("Graph of Equation");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(new GraphPanel(equation));
        setLocationRelativeTo(null);
    }
}
