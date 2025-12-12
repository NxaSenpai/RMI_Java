import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) throws Exception {
        FlatLightLaf.setup();
        UIManager.setLookAndFeel(new FlatLightLaf());

        new CalculatorGUI().setVisible(true);
    }
}

