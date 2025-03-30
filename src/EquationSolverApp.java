import javax.swing.*;
import java.awt.*;

public class EquationSolverApp {
    public static void main(String[] args) {
        // Set a global font for all UI components.
        EquationGUI.setGlobalFont(new Font("JetBrains Mono", Font.BOLD, 18));

        // Use Nimbus look and feel if available.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
                if ("Nimbus".equals(info.getName())){
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch(Exception e){
            // If Nimbus is not available, fallback to default.
        }

        // Launch the GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                EquationGUI.start();
            }
        });
    }
}