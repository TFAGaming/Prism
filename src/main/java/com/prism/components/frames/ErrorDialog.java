package com.prism.components.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.prism.Prism;

public class ErrorDialog extends JDialog {
    public Prism prism = Prism.getInstance();

    public ErrorDialog(Frame parent, Throwable throwable) {
        this(parent, "Something went terribly wrong. To avoid further damage, please restart the application.<br>If the issue still persists, re-install the application and report the issue via GitHub.", throwable);
    }

    public ErrorDialog(Frame parent, String message, Throwable throwable) {
        super(parent, "Unresolved Error", true);

        setSize(200, 200);
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Main error message
        JLabel label = new JLabel("<html><b>An unexpected error occurred:</b><br>" + message + "</html>");
        label.setBorder(new EmptyBorder(5, 5, 5, 5));
        label.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
        add(label, BorderLayout.NORTH);

        // Stack trace area
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        JTextArea textArea = new JTextArea(sw.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        scrollPane.setVisible(false);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton detailsButton = new JButton("Show Details >>");
        detailsButton.setFocusable(false);
        JButton copyButton = new JButton("Copy to Clipboard");
        copyButton.setFocusable(false);
        JButton closeButton = new JButton("Close");
        closeButton.setFocusable(false);

        buttonPanel.add(copyButton);
        buttonPanel.add(detailsButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        add(scrollPane, BorderLayout.CENTER);

        // Button actions
        detailsButton.addActionListener((ActionEvent e) -> {
            boolean visible = scrollPane.isVisible();
            scrollPane.setVisible(!visible);
            detailsButton.setText(visible ? "Hide Details >>" : "Show Details <<");
            pack();
        });

        copyButton.addActionListener((ActionEvent e) -> {
            Toolkit.getDefaultToolkit()
                   .getSystemClipboard()
                   .setContents(new StringSelection(sw.toString()), null);

            copyButton.setText("Copied!");
            
            Prism.setTimeout(() -> {
                if (this.isDisplayable()) {
                    copyButton.setText("Copy to Clipboard");
                }
            }, 3 * 1000);
        });

        closeButton.addActionListener((ActionEvent e) -> dispose());

        pack();
        setLocationRelativeTo(parent);
    }

    public static void showErrorDialog(Frame parent, String message, Throwable throwable) {
        ErrorDialog dialog = new ErrorDialog(parent, message, throwable);
        dialog.setVisible(true);
    }

    public static void showErrorDialog(Frame parent, Throwable throwable) {
        ErrorDialog dialog = new ErrorDialog(parent, throwable);
        dialog.setVisible(true);
    }
}
