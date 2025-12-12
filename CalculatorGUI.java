import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import com.formdev.flatlaf.FlatLightLaf;

public class CalculatorGUI extends JFrame {

    private JTextField num1Field, num2Field;
    private JComboBox<String> operatorBox;
    private JLabel resultLabel;
    private JTextArea historyArea;

    private Calculator calc;

    public CalculatorGUI() {

        setTitle("Modern RMI Calculator");
        setSize(700, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        connectToServer();
        buildUI();
    }

    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            calc = (Calculator) registry.lookup("CalcService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to connect to server.\nStart the server first.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buildUI() {

        setLayout(new BorderLayout(12, 12));

        JPanel left = new JPanel();
        left.setLayout(new GridBagLayout());
        left.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("RMI Calculator");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        left.add(title, c);

        // Number 1
        c.gridwidth = 1; c.gridx = 0; c.gridy = 1;
        left.add(new JLabel("Number 1"), c);

        num1Field = new JTextField();
        c.gridx = 1;
        left.add(num1Field, c);

        // Operator
        c.gridx = 0; c.gridy = 2;
        left.add(new JLabel("Operator"), c);

        operatorBox = new JComboBox<>(new String[]{"+", "-", "*", "/"});
        c.gridx = 1;
        left.add(operatorBox, c);

        // Number 2
        c.gridx = 0; c.gridy = 3;
        left.add(new JLabel("Number 2"), c);

        num2Field = new JTextField();
        c.gridx = 1;
        left.add(num2Field, c);

        // Result
        resultLabel = new JLabel("Result: —");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        c.gridx = 0; c.gridy = 4; c.gridwidth = 2;
        left.add(resultLabel, c);

        // Button
        JButton calcBtn = new JButton("Calculate");
        calcBtn.addActionListener(e -> calculate());
        calcBtn.setPreferredSize(new Dimension(140, 40));
        c.gridy = 5; c.gridwidth = 2;
        left.add(calcBtn, c);

        add(left, BorderLayout.WEST);

        // History Panel
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setBorder(BorderFactory.createTitledBorder("History"));

        add(scroll, BorderLayout.CENTER);
    }

    private void calculate() {
        try {
            int a = Integer.parseInt(num1Field.getText());
            int b = Integer.parseInt(num2Field.getText());
            String op = operatorBox.getSelectedItem().toString();

            double result = switch (op) {
                case "+" -> calc.add(a, b);
                case "-" -> calc.subtract(a, b);
                case "*" -> calc.multiply(a, b);
                case "/" -> calc.divide(a, b);
                default -> 0;
            };

            resultLabel.setText("Result: " + result);
            historyArea.append(a + " " + op + " " + b + " = " + result + "\n");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        FlatLightLaf.setup(); // ⬅ modern UI theme
        SwingUtilities.invokeLater(() -> new CalculatorGUI().setVisible(true));
    }
}
