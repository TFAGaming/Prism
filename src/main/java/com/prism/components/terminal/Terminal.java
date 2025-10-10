package com.prism.components.terminal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;

import javax.swing.text.*;

import com.prism.Prism;
import com.prism.components.extended.JExtendedTextField;

public class Terminal extends JPanel {

    private Prism prism = Prism.getInstance();
    private JTextField dirPathLabel;

    private String currentDirectory = "";
    private Process currentProcess;
    private BufferedWriter processWriter;
    private Thread processOutputThread;

    private List<String> commands = new ArrayList<>();
    private int commandsIndex = -1;

    private boolean awaitingInput = false;

    public int shell = 0;
    public JTextPane terminalArea;
    public JExtendedTextField commandTextField;
    public JComboBox comboBoxCommands;

    public Terminal(File directory, int shell) {
        this.shell = shell;

        if (directory == null) {
            currentDirectory = System.getProperty("user.dir");
        } else {
            currentDirectory = directory.getAbsolutePath();
        }

        setLayout(new BorderLayout());

        terminalArea = new JTextPane();
        terminalArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        terminalArea.setBackground(Color.decode("#FFFFFF"));
        terminalArea.setForeground(Color.decode("#000000"));
        terminalArea.setCaretColor(Color.decode("#000000"));
        terminalArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(terminalArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel secondaryPanel = new JPanel();
        secondaryPanel.setLayout(new BorderLayout());
        secondaryPanel.setBorder(new EmptyBorder(5, 1, 5, 1));

        commandTextField = new JExtendedTextField(16);
        commandTextField.setPlaceholder("Type a command or send an input for an active process.");

        commandTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent event) {
                char keyChar = event.getKeyChar();

                if (keyChar == KeyEvent.VK_ENTER) {
                    String command = commandTextField.getText();

                    if (!awaitingInput && isAlive()) {
                        appendToTerminal(command + "\n", Color.decode("#000000"));

                        try {
                            commandTextField.setText("");

                            processWriter.write(command + "\n");
                            processWriter.flush();
                        } catch (IOException ex) {

                        }
                    } else if (!awaitingInput && !isAlive()) {
                        commandTextField.setText("");

                        appendToTerminal(command + "\n", Color.decode("#000000"));

                        executeCommand(command);
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent event) {
                if (commands.isEmpty()) {
                    return;
                }

                int keyCode = event.getKeyCode();

                if (keyCode == KeyEvent.VK_UP) {
                    commandTextField.setText(commands.get(commandsIndex));

                    commandsIndex--;

                    if (commandsIndex < 0) {
                        commandsIndex = commands.size() - 1;
                    }
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    commandTextField.setText(commands.get(commandsIndex));

                    commandsIndex++;

                    if (commandsIndex > (commands.size() - 1)) {
                        commandsIndex = 0;
                    }
                }
            }
        });

        secondaryPanel.add(commandTextField, BorderLayout.CENTER);

        comboBoxCommands = new JComboBox<>(new String[] { "No commands..." });
        comboBoxCommands.setBorder(new EmptyBorder(0, 5, 0, 5));
        comboBoxCommands.setFocusable(false);
        comboBoxCommands.setEnabled(false);
        Dimension preferredSize = comboBoxCommands.getPreferredSize();
        preferredSize.width += 20;
        comboBoxCommands.setPreferredSize(preferredSize);
        comboBoxCommands.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) comboBoxCommands.getSelectedItem();

                commandTextField.setText(selectedItem);
            }
        });

        secondaryPanel.add(comboBoxCommands, BorderLayout.EAST);

        dirPathLabel = new JTextField();
        dirPathLabel.setText(">>");
        dirPathLabel.setEnabled(false);
        dirPathLabel.setBorder(new EmptyBorder(0, 5, 0, 5));

        secondaryPanel.add(dirPathLabel, BorderLayout.WEST);

        add(secondaryPanel, BorderLayout.SOUTH);

        appendPrompt();

        switch (shell) {
            case 0:
                appendToTerminal("Command Prompt, ", Color.decode("#000000"));
                break;
            case 1:
                appendToTerminal("Windows PowerShell, ", Color.decode("#000000"));
                break;
            default:
                appendToTerminal("Command Prompt, ", Color.decode("#000000"));
        }

        appendToTerminal("Path: " + (currentDirectory == null ? "(None)" : currentDirectory) + "\n", Color.decode("#000000"));
        appendPrompt();

        terminalArea.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            private void showPopupMenu(MouseEvent e) {
                /*
                TerminalPopupMenu menu = new TerminalPopupMenu(scriptor);

                menu.show(e.getComponent(), e.getX(), e.getY());
                 */
            }

        });

        terminalArea.getCaret()
                .setVisible(false);
    }

    public boolean isAlive() {
        return currentProcess == null ? false : currentProcess.isAlive();
    }

    public void executeCommand(String command) {
        commands.add(command);
        commandsIndex = commands.size() - 1;

        if (!comboBoxCommands.isEnabled()) {
            comboBoxCommands.setEnabled(true);
        }

        comboBoxCommands.setModel(new DefaultComboBoxModel<>(commands.toArray()));
        comboBoxCommands.setSelectedIndex(commands.size() - 1);

        if (command.trim().startsWith("exit")) {
            appendPrompt();
        } else if (command.trim().startsWith("cls") || command.trim().startsWith("clear")) {
            clearTerminal();
        } else if (command.trim().startsWith("cd")) {
            changeDirectory(command);
        } else if (command.trim().startsWith("path")) {
            appendToTerminal("Process directory path: " + currentDirectory + "\n", Color.decode("#000000"));
            appendPrompt();
        } else {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder();

                switch (shell) {
                    case 0:
                        processBuilder.command("cmd.exe", "/c", command);
                        break;
                    case 1:
                        processBuilder.command("powershell.exe", "-Command", command);
                        break;
                    default:
                        processBuilder.command("cmd.exe", "/c", command);
                }

                processBuilder.directory(new File(currentDirectory));

                processBuilder.redirectErrorStream(true);
                currentProcess = processBuilder.start();

                processWriter = new BufferedWriter(
                        new OutputStreamWriter(currentProcess.getOutputStream(), Charset.forName("Cp850")));

                processOutputThread = new Thread(() -> {
                    try (BufferedReader processReader = new BufferedReader(
                            new InputStreamReader(currentProcess.getInputStream(), Charset.forName("Cp850")))) {
                        int character;
                        StringBuilder buffer = new StringBuilder();

                        while ((character = processReader.read()) != -1) {
                            buffer.append((char) character);
                            if (character == '\n' || character == '\r') {
                                final String outputLine = buffer.toString();

                                buffer.setLength(0);

                                SwingUtilities.invokeLater(() -> appendToTerminal(outputLine, Color.decode("#000000")));
                            } else {
                                final String partialOutput = buffer.toString();

                                SwingUtilities.invokeLater(() -> appendToTerminal(partialOutput, Color.decode("#000000")));

                                buffer.setLength(0);
                            }
                        }
                    } catch (IOException ex) {

                    }
                });

                processOutputThread.start();

                new Thread(() -> {
                    try {
                        currentProcess.waitFor();
                        SwingUtilities.invokeLater(() -> {
                            appendToTerminal("\n", Color.decode("#000000"));
                            appendPrompt();
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();

            } catch (IOException ex) {
                appendToTerminal("Error: Unable to execute command\n", Color.RED, true);
            }
        }
    }

    private void changeDirectory(String command) {
        String[] parts = command.trim().split("\\s+");
        if (parts.length < 2) {
            appendToTerminal("Missing arguments.\n", Color.decode("#000000"));
            return;
        }

        List<String> list = new ArrayList<String>();

        for (int i = 1; i < parts.length; i++) {
            list.add(parts[i]);
        }

        String newPath = String.join(" ", list).trim();
        File dir;

        if (newPath.equals("..")) {
            dir = new File(currentDirectory).getParentFile();
        } else {
            dir = new File(currentDirectory, newPath);
        }

        if (dir != null && dir.exists() && dir.isDirectory()) {
            currentDirectory = dir.getAbsolutePath();

            appendToTerminal("Changed directory path to: " + currentDirectory + "\n", Color.decode("#000000"));
            appendPrompt();
        } else {
            appendToTerminal("No such directory: " + newPath + "\n", Color.decode("#000000"));
            appendPrompt();
        }
    }

    public void changeDirectoryWithoutCommandString(String path) {
        File dir = new File(path);

        if (dir != null && dir.exists() && dir.isDirectory()) {
            currentDirectory = dir.getAbsolutePath();
        }
    }

    private void appendToTerminal(String text, Color color, boolean... details) {
        StyledDocument doc = terminalArea.getStyledDocument();
        Style style = terminalArea.addStyle("style", null);
        StyleConstants.setForeground(style, color);

        if (details.length > 0 && details[0]) {
            StyleConstants.setBold(style, true);
        }

        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {

        }
    }

    private void appendPrompt() {
        appendToTerminal("terminal@prism~ ", Color.decode("#808080"));
    }

    public void closeProcess() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroy();
            currentProcess = null;
            awaitingInput = false;
        }
    }

    public void restartProcess() {
        if (commands.size() == 0) {
            showMessageDialog(prism, "The commands history for this terminal is empty.", "Terminal Commands History",
                    WARNING_MESSAGE);

            return;
        }

        closeProcess();

        appendToTerminal(commands.get(commands.size() - 1) + "\n", Color.decode("#000000"));

        executeCommand(commands.get(commands.size() - 1));
    }

    public void runPreviousCommand() {
        if (commands.size() == 0) {
            showMessageDialog(prism, "The commands history for this terminal is empty.", "Terminal Commands History",
                    WARNING_MESSAGE);

            return;
        }

        closeProcess();

        appendToTerminal(commands.get(commands.size() - 1) + "\n", Color.decode("#000000"));

        executeCommand(commands.get(commands.size() - 1));
    }

    public void setAwaitingInput(boolean awaiting) {
        this.awaitingInput = awaiting;
    }

    public void clearTerminal() {
        closeProcess();

        terminalArea.setText("");

        appendPrompt();
    }

    public List<String> getCommands() {
        return this.commands;
    }

}
