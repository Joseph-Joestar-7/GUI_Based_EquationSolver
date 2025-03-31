import javax.swing.*;
import java.awt.*;

public class EquationSolverApp {
    public static void main(String[] args) {
        EquationGUI.setGlobalFont(new Font("JetBrains Mono", Font.BOLD, 18));

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
                if ("Nimbus".equals(info.getName())){
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch(Exception e){
            System.err.println("Nimbus Look and Feel not available. Using default Look and Feel.");
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                EquationGUI.start();
            }
        });
    }
}