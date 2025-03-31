import javax.swing.*;
import java.awt.*;

public class EquationGUI {
    public static void setGlobalFont(Font font) {
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TitledBorder.font", font);
        UIManager.put("OptionPane.font", font);
        UIManager.put("TabbedPane.font", font);
        UIManager.put("List.font", font);
    }
    public static void start() {
        new SplashScreenW();
    }
}
