package com.prism.components.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.prism.Prism;
import com.prism.components.frames.WarningDialog;

public class MathPanel extends JPanel implements ActionListener {

    private JTextField display;
    private StringBuilder currentExpression = new StringBuilder();
    private boolean equalsPressed = false;

    private ScriptEngine engine;

    // --- Constructor ---
    public MathPanel() {
        // Initialize the script engine
        ScriptEngineManager manager = new ScriptEngineManager();
        // We use the JavaScript engine for its powerful Math capabilities
        engine = manager.getEngineByName("JavaScript");

        // Use a BorderLayout for the main panel: Display at North, Buttons at Center/South
        setLayout(new BorderLayout());

        // Suggest a height, but the internal components will primarily determine the actual size
        setPreferredSize(new Dimension(getWidth(), 300));

        // 1. Display Field
        display = new JTextField("0");
        display.setEditable(true); // Allow manual editing for a true terminal/input feel
        display.setFont(new Font("Monospaced", Font.BOLD, 22));
        display.setHorizontalAlignment(JTextField.RIGHT);

        // Wrap the display in a scroll pane if you expect long expressions
        JScrollPane scrollPane = new JScrollPane(display);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.NORTH);

        // 2. Buttons Panel
        JPanel buttonPanel = new JPanel();
        // A 6x5 grid for a scientific layout will keep buttons compact.
        buttonPanel.setLayout(new GridLayout(6, 5, 3, 3));

        String[] buttonLabels = {
            "C", "DEL", "(", ")", "/",
            "sin", "cos", "tan", "sqrt", "*",
            "log", "ln", "pow", "!", "-", // '!' (factorial) and 'pow' (power) will need special handling
            "7", "8", "9", "mod", "+", // 'mod' needs to be replaced with '%' for JS
            "4", "5", "6", "pi", "=", // 'pi' needs to be replaced with Math.PI
            "1", "2", "3", "0", "."
        };

        // Customize button colors for visual separation
        Color opColor = new Color(255, 165, 0); // Orange for operators
        Color funcColor = new Color(135, 206, 250); // Light blue for functions
        Color clearColor = new Color(255, 99, 71); // Tomato for clear
        Color equalsColor = new Color(60, 179, 113); // Medium sea green for equals

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFocusable(true);
            button.setFont(new Font("Monospaced", Font.PLAIN, 16));
            button.addActionListener(this);

            // Apply custom colors
            if (label.matches("[+\\-*/=!]")) {
                button.setBackground(opColor);
            } else if (label.matches("C|DEL")) {
                button.setBackground(clearColor);
            } else if (label.matches("sin|cos|tan|log|ln|sqrt|pow|mod|pi")) {
                button.setBackground(funcColor);
            } else if (label.equals("=")) {
                button.setBackground(equalsColor);
                button.setForeground(Color.WHITE);
            }

            buttonPanel.add(button);
        }

        // Add the button panel to the CENTER (which fills the remaining space)
        add(buttonPanel, BorderLayout.CENTER);
    }

    // --- Action Handling ---
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        String currentText = display.getText();

        if (command.equals("C")) {
            currentExpression.setLength(0);
            display.setText("0");
            equalsPressed = false;
        } else if (command.equals("DEL")) {
            if (currentText.length() > 0 && !currentText.equals("0")) {
                // Adjusting the display text deletion
                display.setText(currentText.substring(0, currentText.length() - 1));
                if (display.getText().isEmpty()) {
                    display.setText("0");
                }
            }
            if (currentExpression.length() > 0) {
                // We're just trimming the expression string builder here
                currentExpression.setLength(currentExpression.length() - 1);
            }
            equalsPressed = false;
        } else if (command.equals("=")) {
            if (!equalsPressed) {
                try {
                    // Get the expression from the display (allowing manual edits)
                    String expression = display.getText();

                    String result = evaluateExpression(expression);

                    display.setText(result);
                    currentExpression.setLength(0);
                    currentExpression.append(result);
                    equalsPressed = true;
                } catch (Exception ex) {
                    display.setText("Error: " + ex.getMessage());
                    currentExpression.setLength(0);
                    equalsPressed = true;
                }
            }
        } else {
            // All other buttons (numbers, operators, functions)
            if (equalsPressed) {
                // Clear the display/expression when starting a new calculation
                display.setText("");
                currentExpression.setLength(0);
                equalsPressed = false;
            }

            String appendText = command;

            // 1. Handle function and constant buttons to ensure JS compatibility
            if (command.matches("sin|cos|tan|sqrt|log|ln")) {
                // JS requires 'Math.' prefix for most functions, and 'log' is natural log (ln)
                // Use 'Math.log10' for base-10 log if 'log' is meant. 
                // Using 'Math.sin(' etc., assuming standard JS implementation.
                appendText = "Math." + command + "(";
            } else if (command.equals("pow")) {
                // JS power function is Math.pow(base, exponent)
                appendText = "Math.pow(";
            } else if (command.equals("mod")) {
                // JS modulo operator is '%'
                appendText = "%";
            } else if (command.equals("pi")) {
                // JS constant for Pi is Math.PI
                appendText = "Math.PI";
            } else if (command.equals("!")) {
                // Factorial is not native to JS, will need manual implementation or error handling
                // For simplicity, we'll treat it as an unsupported operation for this example.
                // A production calculator would need a separate function for this.
                // For now, let's treat it as an unsupported operator and see what the user inputs
            }

            // 2. Clear '0' if the first input is a number/function
            if (currentText.equals("0") && !command.matches("[+\\-*/()]")) {
                display.setText(appendText);
                currentExpression.setLength(0); // Clear expression builder
            } else {
                display.setText(currentText + appendText);
            }

            currentExpression.append(appendText);
        }
    }

    private String evaluateExpression(String expression) {
        try {
            expression = expression.replace("--", "+");

            Object result = engine.eval(expression);

            if (result instanceof Number) {
                double dResult = ((Number) result).doubleValue();

                if (dResult == (long) dResult) {
                    return String.format("%d", (long) dResult);
                } else {
                    return String.valueOf(dResult);
                }
            }

            return String.valueOf(result);

        } catch (ScriptException e) {
            new WarningDialog(Prism.getInstance(), e);
        }

        return "Error";
    }
}
