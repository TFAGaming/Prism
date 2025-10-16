package com.prism.components.terminal;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;

import com.prism.Prism;
import com.prism.components.definition.Tool;
import com.prism.components.extended.JExtendedTextField;

public class Terminal extends JPanel {

    public Prism prism = Prism.getInstance();

    private JTextField dirPathLabel;

    private String currentDirectory = "";
    private Process currentProcess;
    private BufferedWriter processWriter;
    private Thread processOutputThread;

    private List<String> commands = new ArrayList<>();
    // commandsIndex now points to the command being displayed/edited
    // -1 means the current, un-executed command line
    private int commandsIndex = -1;

    private boolean awaitingInput = false;

    public int shell = 0;
    public JTextPane terminalArea;
    public JExtendedTextField commandTextField;
    public JComboBox<String> comboBoxCommands;

    // Default styles for ANSI parsing
    private Style currentStyle;
    private static final Color DEFAULT_FOREGROUND = Color.decode("#000000"); // Black
    private static final Color DEFAULT_BACKGROUND = Color.decode("#FFFFFF"); // White

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
        terminalArea.setBackground(DEFAULT_BACKGROUND);
        terminalArea.setForeground(DEFAULT_FOREGROUND);
        terminalArea.setCaretColor(DEFAULT_FOREGROUND);
        terminalArea.setEditable(false);

        // Initialize the default style
        currentStyle = terminalArea.addStyle("default", null);
        StyleConstants.setForeground(currentStyle, DEFAULT_FOREGROUND);
        StyleConstants.setBackground(currentStyle, DEFAULT_BACKGROUND);
        StyleConstants.setBold(currentStyle, false);

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
                // Reset history index if typing occurs
                if (commandsIndex != -1) {
                    commandsIndex = -1;
                }

                char keyChar = event.getKeyChar();

                if (keyChar == KeyEvent.VK_ENTER) {
                    event.consume();
                    String command = commandTextField.getText().trim();

                    if (command.isEmpty()) {
                        appendToTerminal("\n", DEFAULT_FOREGROUND);
                        appendPrompt();
                        return;
                    }

                    commandTextField.setText("");

                    // Append command to terminal first (always default color/style for user input)
                    appendToTerminal(command + "\n", DEFAULT_FOREGROUND);

                    if (isAlive()) {
                        // Active process -> treat as input
                        try {
                            processWriter.write(command + "\n");
                            processWriter.flush();
                        } catch (IOException ex) {
                            appendToTerminal("Error sending input to process.\n", Color.RED, true);
                        }
                    } else if (!isAlive()) {
                        // No active process -> execute as a new command
                        executeCommand(command);
                    }
                    // Reset history index after execution
                    commandsIndex = -1;
                }
            }

            @Override
            public void keyPressed(KeyEvent event) {
                if (commands.isEmpty()) {
                    return;
                }

                int keyCode = event.getKeyCode();

                if (keyCode == KeyEvent.VK_UP) {
                    event.consume();

                    if (commandsIndex == -1) {
                        commandsIndex = commands.size() - 1;
                    } else {
                        commandsIndex--;
                        if (commandsIndex < 0) {
                            commandsIndex = commands.size() - 1;
                        }
                    }
                    commandTextField.setText(commands.get(commandsIndex));
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    event.consume();

                    if (commandsIndex != -1) {
                        commandsIndex++;
                        if (commandsIndex > (commands.size() - 1)) {
                            commandsIndex = -1;
                            commandTextField.setText("");
                        } else {
                            commandTextField.setText(commands.get(commandsIndex));
                        }
                    }
                }
            }
        });

        secondaryPanel.add(commandTextField, BorderLayout.CENTER);

        comboBoxCommands = new JComboBox<>(new String[]{"No commands..."});
        comboBoxCommands.setBorder(new EmptyBorder(0, 5, 0, 5));
        comboBoxCommands.setFocusable(true);
        comboBoxCommands.setEnabled(false);
        Dimension preferredSize = comboBoxCommands.getPreferredSize();
        preferredSize.width += 20;
        comboBoxCommands.setPreferredSize(preferredSize);
        comboBoxCommands.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) comboBoxCommands.getSelectedItem();
                if (selectedItem != null && !selectedItem.equals("No commands...")) {
                    commandTextField.setText(selectedItem);
                }
            }
        });

        secondaryPanel.add(comboBoxCommands, BorderLayout.EAST);

        dirPathLabel = new JTextField();
        updateDirPathLabel();
        dirPathLabel.setEnabled(false);
        dirPathLabel.setEditable(false);
        dirPathLabel.setBorder(new EmptyBorder(0, 5, 0, 5));

        secondaryPanel.add(dirPathLabel, BorderLayout.WEST);

        add(secondaryPanel, BorderLayout.SOUTH);

        appendPrompt();

        String shellName;
        switch (shell) {
            case 0:
                shellName = "Command Prompt";
                break;
            case 1:
                shellName = "Windows PowerShell";
                break;
            default:
                shellName = "Command Prompt";
        }

        appendToTerminal(shellName + ", ", DEFAULT_FOREGROUND);

        appendToTerminal("Path: " + (currentDirectory == null ? "(None)" : currentDirectory) + "\n", DEFAULT_FOREGROUND);
        appendPrompt();

        terminalArea.addMouseListener(new MouseListener() {
            // (Mouse Listener methods remain the same)
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e);
            
                }}

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e);
            
                }}

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
                /* TerminalPopupMenu menu = new TerminalPopupMenu(scriptor); menu.show(e.getComponent(), e.getX(), e.getY()); */
            }

        });

        terminalArea.getCaret().setVisible(false);
    }

    public boolean isAlive() {
        return currentProcess != null && currentProcess.isAlive();
    }

    public void executeCommand(String command) {
        if (command.trim().isEmpty()) {
            appendPrompt();
            return;
        }

        commands.add(command);
        commandsIndex = -1;

        if (!comboBoxCommands.isEnabled()) {
            comboBoxCommands.setEnabled(true);
        }

        comboBoxCommands.setModel(new DefaultComboBoxModel<>(commands.toArray(new String[0])));
        comboBoxCommands.setSelectedIndex(commands.size() - 1);

        commandTextField.setText("");

        // Internal commands
        if (command.trim().startsWith("cls") || command.trim().startsWith("clear")) {
            clearTerminal();
        } else if (command.trim().startsWith("cd")) {
            changeDirectory(command);
        } else if (command.trim().startsWith("path")) {
            appendToTerminal("Process directory path: " + currentDirectory + "\n", DEFAULT_FOREGROUND);
            appendPrompt();
        } else if (command.trim().startsWith("exit")) {
            // For internal exit, just print prompt
            appendPrompt();
        } else {
            // External command execution
            try {
                ProcessBuilder processBuilder = new ProcessBuilder();

                String[] commandArray;
                switch (shell) {
                    case 0: // Command Prompt
                        // Use /c to terminate after command, allowing external exit handling
                        commandArray = new String[]{"cmd.exe", "/c", command};
                        break;
                    case 1: // PowerShell
                        commandArray = new String[]{"powershell.exe", "-Command", command};
                        break;
                    default:
                        commandArray = new String[]{"cmd.exe", "/c", command};
                }

                processBuilder.command(commandArray);
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

                            // Check for line ending or a large buffer size
                            if (character == '\n' || character == '\r' || buffer.length() > 1024) {
                                final String outputLine = buffer.toString();
                                buffer.setLength(0);

                                SwingUtilities.invokeLater(() -> {
                                    // Use the new parser for output
                                    parseAndAppendToTerminal(outputLine);
                                });
                            }
                        }

                        // Handle any remaining text in the buffer 
                        if (buffer.length() > 0) {
                            final String outputLine = buffer.toString();
                            SwingUtilities.invokeLater(() -> {
                                parseAndAppendToTerminal(outputLine);
                            });
                        }

                    } catch (IOException ex) {
                        if (!currentProcess.isAlive()) {
                            return;
                        }
                        SwingUtilities.invokeLater(() -> {
                            appendToTerminal("Error reading process output: " + ex.getMessage() + "\n", Color.RED, true);
                        });
                    }
                });

                processOutputThread.start();

                // Wait for the process to finish and report exit code
                new Thread(() -> {
                    try {
                        int exitCode = currentProcess.waitFor();

                        try {
                            if (processWriter != null) {
                                processWriter.close();
                            }
                        } catch (IOException ignored) {
                        }

                        SwingUtilities.invokeLater(() -> {
                            // Print process exit status
                            String statusMessage;
                            Color statusColor;

                            if (exitCode == 0) {
                                statusMessage = "\n[Process finished successfully (Exit Code: 0)]\n";
                                statusColor = new Color(0, 150, 0); // Dark Green
                            } else {
                                statusMessage = "\n[Process finished with error (Exit Code: " + exitCode + ")]\n";
                                statusColor = Color.RED;
                            }

                            appendToTerminal(statusMessage, statusColor, true);
                            appendPrompt();
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        SwingUtilities.invokeLater(() -> {
                            appendToTerminal("\n[Process interrupted.]\n", Color.RED, true);
                            appendPrompt();
                        });
                    }
                }).start();

            } catch (IOException ex) {
                appendToTerminal("Error: Unable to start command (" + ex.getMessage() + ")\n", Color.RED, true);
                appendPrompt();
            }
        }
    }

    // --- NEW FEATURE: ANSI Color and Style Parsing ---
    private void parseAndAppendToTerminal(String text) {
        StyledDocument doc = terminalArea.getStyledDocument();
        int startIndex = 0;
        int ansiCodeIndex;

        // ANSI escape sequence start: ESC [
        final String ANSI_START = "\u001B[";

        while (startIndex < text.length()) {
            ansiCodeIndex = text.indexOf(ANSI_START, startIndex);

            if (ansiCodeIndex == -1) {
                // No more ANSI codes, append remaining text with current style
                String segment = text.substring(startIndex);
                try {
                    doc.insertString(doc.getLength(), segment, currentStyle);
                } catch (BadLocationException e) {
                }
                break;
            }

            // 1. Append text segment before the ANSI code
            if (ansiCodeIndex > startIndex) {
                String segment = text.substring(startIndex, ansiCodeIndex);
                try {
                    doc.insertString(doc.getLength(), segment, currentStyle);
                } catch (BadLocationException e) {
                }
            }

            // 2. Find the end of the ANSI sequence (SGR codes end with 'm')
            int ansiEndIndex = text.indexOf('m', ansiCodeIndex);
            if (ansiEndIndex == -1) {
                // Incomplete ANSI sequence, treat the rest as regular text
                String segment = text.substring(ansiCodeIndex);
                try {
                    doc.insertString(doc.getLength(), segment, currentStyle);
                } catch (BadLocationException e) {
                }
                break;
            }

            // 3. Parse and apply the ANSI code
            String code = text.substring(ansiCodeIndex + ANSI_START.length(), ansiEndIndex);
            applyAnsiCode(code);

            // 4. Update the start index for the next segment
            startIndex = ansiEndIndex + 1;
        }

        // Auto-scroll to bottom
        terminalArea.setCaretPosition(doc.getLength());
    }

    private void applyAnsiCode(String code) {
        // SGR (Select Graphic Rendition) codes are separated by semicolons
        String[] codes = code.split(";");

        // Create a new style based on the current style for modification
        Style newStyle = terminalArea.addStyle(null, currentStyle);

        for (String c : codes) {
            try {
                int sgrCode = Integer.parseInt(c);

                if (sgrCode == 0) {
                    // Reset all attributes
                    newStyle = terminalArea.addStyle("reset", null);
                    StyleConstants.setForeground(newStyle, DEFAULT_FOREGROUND);
                    StyleConstants.setBackground(newStyle, DEFAULT_BACKGROUND);
                    StyleConstants.setBold(newStyle, false);
                } else if (sgrCode == 1) {
                    // Bold/Bright
                    StyleConstants.setBold(newStyle, true);
                } else if (sgrCode == 22) {
                    // Not bold (reset intensity)
                    StyleConstants.setBold(newStyle, false);
                } else if (sgrCode >= 30 && sgrCode <= 37) {
                    // Foreground color (Standard 8 colors)
                    StyleConstants.setForeground(newStyle, getStyleForAnsiCode(sgrCode));
                } else if (sgrCode >= 40 && sgrCode <= 47) {
                    // Background color (Standard 8 colors)
                    StyleConstants.setBackground(newStyle, getStyleForAnsiCode(sgrCode - 10)); // Map background codes to foreground codes
                } else if (sgrCode >= 90 && sgrCode <= 97) {
                    // Bright Foreground color (High-intensity 8 colors)
                    StyleConstants.setForeground(newStyle, getStyleForAnsiCode(sgrCode - 60)); // Map 9x codes to 3x bright codes
                    StyleConstants.setBold(newStyle, true); // Often bright is just bolded standard color
                }
                // (More complex codes like 38;5 and 48;5 for 256 colors are ignored for simplicity)

            } catch (NumberFormatException e) {
                // Ignore non-numeric or invalid codes
            }
        }
        currentStyle = newStyle; // Update the current active style
    }

    // Maps standard ANSI foreground codes (30-37) to Java Colors
    private Color getStyleForAnsiCode(int code) {
        switch (code) {
            case 30:
                return Color.BLACK;
            case 31:
                return Color.RED;
            case 32:
                return Color.GREEN;
            case 33:
                return Color.YELLOW;
            case 34:
                return Color.BLUE;
            case 35:
                return Color.MAGENTA;
            case 36:
                return Color.CYAN;
            case 37:
                return Color.WHITE;
            default:
                return DEFAULT_FOREGROUND;
        }
    }
    // --- END NEW FEATURE ---

    private void updateDirPathLabel() {
        // ... (unchanged)
        String baseName;
        try {
            File dir = new File(currentDirectory);
            baseName = dir.getName();
            if (baseName.isEmpty() && dir.isAbsolute()) {
                baseName = dir.getAbsolutePath();
            }
        } catch (Exception e) {
            baseName = currentDirectory;
        }

        dirPathLabel.setText(">> " + baseName + " >");
    }

    private void changeDirectory(String command) {
        // ... (unchanged)
        String[] parts = command.trim().split("\\s+");
        if (parts.length < 2) {
            appendToTerminal("Missing directory argument.\n", DEFAULT_FOREGROUND);
            appendPrompt();
            return;
        }

        StringBuilder newPathBuilder = new StringBuilder();
        for (int i = 1; i < parts.length; i++) {
            if (i > 1) {
                newPathBuilder.append(" ");
            }
            newPathBuilder.append(parts[i]);
        }
        String newPath = newPathBuilder.toString().trim();

        if (newPath.startsWith("\"") && newPath.endsWith("\"")) {
            newPath = newPath.substring(1, newPath.length() - 1);
        }

        File dir;

        if (newPath.equals("..")) {
            File currentFile = new File(currentDirectory);
            dir = currentFile.getParentFile();
            if (dir == null) {
                dir = currentFile;
            }
        } else if (newPath.equals(".")) {
            dir = new File(currentDirectory);
        } else {
            File absoluteDir = new File(newPath);
            if (absoluteDir.exists() && absoluteDir.isDirectory()) {
                dir = absoluteDir;
            } else {
                dir = new File(currentDirectory, newPath);
            }
        }

        if (dir != null && dir.exists() && dir.isDirectory()) {
            currentDirectory = dir.getAbsolutePath();
            updateDirPathLabel();
            appendToTerminal("Changed directory path to: " + currentDirectory + "\n", DEFAULT_FOREGROUND);
            appendPrompt();
        } else {
            appendToTerminal("No such directory or invalid path: " + newPath + "\n", DEFAULT_FOREGROUND);
            appendPrompt();
        }
    }

    public void changeDirectoryWithoutCommandString(String path) {
        // ... (unchanged)
        File dir = new File(path);

        if (dir != null && dir.exists() && dir.isDirectory()) {
            currentDirectory = dir.getAbsolutePath();
            SwingUtilities.invokeLater(this::updateDirPathLabel);
        }
    }

    // Helper method to apply basic non-ANSI styles (for prompts/errors)
    private void appendToTerminal(String text, Color color, boolean... details) {
        StyledDocument doc = terminalArea.getStyledDocument();

        Style basicStyle = terminalArea.addStyle("basic_" + color.getRGB() + "_" + (details.length > 0 && details[0]), null);
        StyleConstants.setForeground(basicStyle, color);
        StyleConstants.setBackground(basicStyle, DEFAULT_BACKGROUND);

        if (details.length > 0 && details[0]) {
            StyleConstants.setBold(basicStyle, true);
        }

        try {
            doc.insertString(doc.getLength(), text, basicStyle);
            terminalArea.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
        }
    }

    private void appendPrompt() {
        appendToTerminal("terminal@prism~ ", Color.decode("#808080"));
        // Reset current style to default after prompt (important for user input/next command output)
        currentStyle = terminalArea.addStyle("default", null);
        StyleConstants.setForeground(currentStyle, DEFAULT_FOREGROUND);
        StyleConstants.setBackground(currentStyle, DEFAULT_BACKGROUND);
        StyleConstants.setBold(currentStyle, false);
    }

    public void closeProcess() {
        // ... (unchanged)
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroyForcibly();
            currentProcess = null;
            awaitingInput = false;
            try {
                if (processWriter != null) {
                    processWriter.close();
                }
            } catch (IOException ignored) {
            }
            if (processOutputThread != null && processOutputThread.isAlive()) {
                processOutputThread.interrupt();
            }

            SwingUtilities.invokeLater(() -> {
                appendToTerminal("\n[Process terminated by user.]\n", Color.RED, true);
                appendPrompt();
            });
        }
    }

    public void restartProcess() {
        // ... (unchanged)
        if (commands.size() == 0) {
            showMessageDialog(prism, "The commands history for this terminal is empty.", "Terminal Commands History",
                    WARNING_MESSAGE);
            return;
        }

        closeProcess();

        String lastCommand = commands.get(commands.size() - 1);

        appendToTerminal(lastCommand + "\n", DEFAULT_FOREGROUND);

        executeCommand(lastCommand);
    }

    public void runPreviousCommand() {
        // ... (unchanged)
        if (commands.size() == 0) {
            showMessageDialog(prism, "The commands history for this terminal is empty.", "Terminal Commands History",
                    WARNING_MESSAGE);
            return;
        }

        closeProcess();

        String lastCommand = commands.get(commands.size() - 1);

        appendToTerminal(lastCommand + "\n", DEFAULT_FOREGROUND);

        executeCommand(lastCommand);
    }

    public void executeTool(Tool tool) {
        closeProcess();

        appendToTerminal("Executing tool: " + tool.getName() + "\n", DEFAULT_FOREGROUND);

        for (String argument : tool.getArguments()) {
            executeCommand(argument);
        }
    }

    public void setAwaitingInput(boolean awaiting) {
        this.awaitingInput = awaiting;
    }

    public void clearTerminal() {
        // ... (unchanged)
        closeProcess();

        terminalArea.setText("");

        appendPrompt();
    }

    public List<String> getCommands() {
        return this.commands;
    }

}
