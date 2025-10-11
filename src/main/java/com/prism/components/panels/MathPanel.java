package com.prism.components.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout; // Used for a basic RPN/Shunting-yard-like calculation
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class MathPanel extends JPanel implements ActionListener {

    private JTextField display;
    private StringBuilder currentExpression = new StringBuilder(); // The full expression string
    private boolean equalsPressed = false;

    // --- Constructor ---
    public MathPanel() {
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
        // A 5x5 grid for a scientific layout will keep buttons compact.
        buttonPanel.setLayout(new GridLayout(6, 5, 3, 3)); // 6 rows, 5 columns, with small gaps

        String[] buttonLabels = {
            "C", "DEL", "(", ")", "/",
            "sin", "cos", "tan", "sqrt", "*",
            "log", "ln", "pow", "!", "-",
            "7", "8", "9", "mod", "+",
            "4", "5", "6", "pi", "=",
            "1", "2", "3", "0", "."
        };
        
        // Customize button colors for visual separation
        Color opColor = new Color(255, 165, 0); // Orange for operators
        Color funcColor = new Color(135, 206, 250); // Light blue for functions
        Color clearColor = new Color(255, 99, 71); // Tomato for clear
        Color equalsColor = new Color(60, 179, 113); // Medium sea green for equals

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFocusable(false);
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
            if (currentText.length() > 0) {
                display.setText(currentText.substring(0, currentText.length() - 1));
            }
            if (currentExpression.length() > 0) {
                currentExpression.setLength(currentExpression.length() - 1);
            }
        } else if (command.equals("=")) {
            if (!equalsPressed) {
                try {
                    // Get the final expression (which might include manual edits)
                    String expression = display.getText();
                    
                    // --- THE CORE EVALUATION PART ---
                    String result = evaluateExpression(expression); 
                    // --------------------------------
                    
                    display.setText(result);
                    currentExpression.setLength(0);
                    currentExpression.append(result);
                    equalsPressed = true;
                } catch (Exception ex) {
                    display.setText("Error");
                    currentExpression.setLength(0);
                    equalsPressed = true;
                }
            }
        } else {
            // All other buttons (numbers, operators, functions)
            if (equalsPressed) {
                display.setText("");
                currentExpression.setLength(0);
                equalsPressed = false;
            }
            
            String appendText = command;
            
            // Handle function and constant buttons to ensure proper formatting
            if (command.matches("sin|cos|tan|log|ln|sqrt|pow|!|mod")) {
                appendText = command + "(";
            } else if (command.equals("pi")) {
                appendText = String.valueOf(Math.PI);
            }
            
            // Append the new text
            display.setText(currentText.equals("0") && command.matches("[0-9]") ? command : currentText + appendText);
            currentExpression.append(appendText);
        }
    }
    
    /**
     * Placeholder for actual expression evaluation. 
     * In a real application, you would use a robust mathematical parser library.
     */
    private String evaluateExpression(String expression) throws Exception {
        // --- WARNING: Simplified Evaluation Logic ---
        // This is a minimal example and does NOT correctly handle operator precedence, 
        // nested functions, or complex expressions like a real calculator.
        // It mainly handles basic arithmetic and replaces function names with their values.

        // 1. Basic Function Replacement (e.g., sin(30) -> 0.5)
        expression = expression.replaceAll("pi", String.valueOf(Math.PI));
        
        // This is where a real parsing library (like JEP) is essential.
        // We'll just try to calculate a single basic operation for simplicity:
        
        if (expression.contains("+")) {
            String[] parts = expression.split("\\+");
            if (parts.length == 2) {
                double result = Double.parseDouble(parts[0]) + Double.parseDouble(parts[1]);
                return String.valueOf(result);
            }
        }
        // ... add checks for -, *, /

        // If it's a number, just return it
        try {
            Double.parseDouble(expression);
            return expression;
        } catch (NumberFormatException e) {
            throw new Exception("Complex expression requires a real parser.");
        }
    }
}