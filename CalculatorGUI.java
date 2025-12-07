import javax.swing.*;
import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CalculatorGUI extends JFrame {

    private JTextField num1Field;
    private JTextField num2Field;
    private JLabel resultLabel;
    private JComboBox<String> operatorBox;
    private JTextArea historyArea;
    private Calculator calc;

    public CalculatorGUI() {

        setTitle("Modern RMI Calculator");
        setSize(450, 420);
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

        // ===== MAIN PANEL (card-like container) =====
        JPanel mainPanel = new RoundedPanel(25);
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BorderLayout(15, 15));

        add(mainPanel);

        // ===== TOP INPUT AREA =====
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Number 1
        c.gridx = 0; c.gridy = 0;
        inputPanel.add(label("Number 1:"), c);

        num1Field = textField();
        c.gridx = 1;
        inputPanel.add(num1Field, c);

        // Row 2: Operator
        c.gridx = 0; c.gridy = 1;
        inputPanel.add(label("Operator:"), c);

        operatorBox = new JComboBox<>(new String[]{"+", "-", "*", "/"});
        operatorBox.setFont(new Font("Arial", Font.PLAIN, 16));
        c.gridx = 1;
        inputPanel.add(operatorBox, c);

        // Row 3: Number 2
        c.gridx = 0; c.gridy = 2;
        inputPanel.add(label("Number 2:"), c);

        num2Field = textField();
        c.gridx = 1;
        inputPanel.add(num2Field, c);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // ===== RESULT LABEL =====
        resultLabel = new JLabel("Result: ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        resultLabel.setForeground(new Color(50, 50, 50));
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        mainPanel.add(resultLabel, BorderLayout.CENTER);

        // ===== BUTTON =====
        JButton calcBtn = new JButton("Calculate");
        calcBtn.setFont(new Font("Arial", Font.BOLD, 16));
        calcBtn.setBackground(new Color(0, 122, 255));
        calcBtn.setForeground(Color.WHITE);
        calcBtn.setFocusPainted(false);
        calcBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        calcBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        calcBtn.addActionListener(e -> calculate());

        mainPanel.add(calcBtn, BorderLayout.SOUTH);

        // ===== HISTORY PANEL BELOW MAIN PANEL =====

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        historyArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setBorder(BorderFactory.createTitledBorder("History"));

        add(historyScroll, BorderLayout.SOUTH);
    }

    private JLabel label(String txt) {
        JLabel lbl = new JLabel(txt);
        lbl.setFont(new Font("Arial", Font.PLAIN, 16));
        return lbl;
    }

    private JTextField textField() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial", Font.PLAIN, 16));
        return tf;
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
                case "/" -> {
                    double r = calc.divide(a, b);
                    if (Double.isNaN(r)) {
                        JOptionPane.showMessageDialog(this,
                                "Cannot divide by zero.",
                                "Math Error",
                                JOptionPane.WARNING_MESSAGE);
                        yield Double.NaN;
                    }
                    yield r;
                }
                default -> 0;
            };

            if (!Double.isNaN(result))
                resultLabel.setText("Result: " + result);

            historyArea.append(a + " " + op + " " + b + " = " + result + "\n");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "RMI Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== CUSTOM ROUNDED PANEL =====
    class RoundedPanel extends JPanel {
        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorGUI().setVisible(true));
    }
}
