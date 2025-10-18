package com.prism.components.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;

import com.prism.Prism;
import com.prism.components.definition.Tool;
import com.prism.components.extended.JExtendedTextField;
import com.prism.managers.FileManager;
import com.prism.managers.ToolsManager;
import com.prism.utils.ResourceUtil;

public class EditToolFrame extends JFrame {
    public Prism prism = Prism.getInstance();

    private JExtendedTextField nameField, descriptionField, directoryField, shortcutField;
    private final DefaultListModel<String> argumentsModel = new DefaultListModel<>();
    private JList<String> argumentsList;

    public Tool tool;

    public EditToolFrame(Tool tool) {
        this.tool = tool;

        for (int i = 0; i < tool.getArguments().size(); i++) {
            String arg = tool.getArguments().get(i);

            if (arg != null && !arg.trim().isEmpty()) {
                argumentsModel.addElement(arg.trim());
            }
        }

        setTitle("Edit a Tool (ID: " + tool.getId().toString() + ")");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setResizable(false);

        setIconImage(ResourceUtil.getIcon("icons/Prism.png").getImage());

        init();

        this.nameField.setText(tool.getName());
        this.descriptionField.setText(tool.getDescription());
        this.directoryField.setText(tool.getDirectory().getAbsolutePath());
        this.shortcutField.setText(tool.getShortcut());

        setVisible(true);
    }

    public void init() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JExtendedTextField(60);
        nameField.setPlaceholder("The tool name.");

        descriptionField = new JExtendedTextField(60);
        descriptionField.setPlaceholder("The tool description; A small explanation of this tool.");

        directoryField = new JExtendedTextField(55);
        directoryField.setPlaceholder("The directory where the terminal should start, default: Project directory");
        directoryField.setEditable(false);

        if (FileManager.getDirectory() != null) {
            directoryField.setText(FileManager.getDirectory().getAbsolutePath());
        }

        JButton browseDirectory = new JButton("Browse...");
        browseDirectory.setFocusable(true);

        shortcutField = new JExtendedTextField(60);
        shortcutField.setPlaceholder(
                "The tool name's shortcut, a small and memorable shortcut to quickly execute this tool.");

        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Name*:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        fieldsPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        fieldsPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        fieldsPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        fieldsPanel.add(new JLabel("Directory:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(directoryField, gbc);
        gbc.gridx = 2;
        fieldsPanel.add(browseDirectory, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        fieldsPanel.add(new JLabel("Shortcut:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        fieldsPanel.add(shortcutField, gbc);

        // Variables

        String variables = """
                Variables:
                - {{curent_file_name}}: The current and selected file name (with extension)
                - {{curent_file_name_no_extension}}: The current and selected file name (without extension)
                - {{current_file}}: The current and selected file path
                - {{current_dir}}: The directory path from File Explorer
                                """;

        JTextPane variablesPane = new JTextPane();
        variablesPane.setCaretColor(variablesPane.getBackground());
        variablesPane.setEditable(false);
        variablesPane.setBackground(getBackground());
        variablesPane.setText(variables);

        gbc.gridx = 0;
        gbc.gridy = 5;
        fieldsPanel.add(variablesPane, gbc);

        // Command Line Arguments
        JPanel argumentsPanel = new JPanel(new BorderLayout());
        argumentsPanel.setBorder(BorderFactory.createTitledBorder("Command Line Arguments:"));

        //argumentsModel = new DefaultListModel<>();
        argumentsList = new JList<>(argumentsModel);
        argumentsList.setFocusable(true);
        argumentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(argumentsList);

        argumentsPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons for arguments
        JPanel argButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        addBtn.setFocusable(true);
        JButton removeBtn = new JButton("Remove");
        removeBtn.setFocusable(true);
        JButton modifyBtn = new JButton("Modify");
        modifyBtn.setFocusable(true);
        JButton upBtn = new JButton("Move Up");
        upBtn.setFocusable(true);
        JButton downBtn = new JButton("Move Down");
        downBtn.setFocusable(true);

        argButtonsPanel.add(addBtn);
        argButtonsPanel.add(removeBtn);
        argButtonsPanel.add(modifyBtn);
        argButtonsPanel.add(upBtn);
        argButtonsPanel.add(downBtn);

        argumentsPanel.add(argButtonsPanel, BorderLayout.SOUTH);

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.add(fieldsPanel, BorderLayout.NORTH);
        mainContent.add(argumentsPanel, BorderLayout.CENTER);

        mainPanel.add(mainContent, BorderLayout.CENTER);

        // Bottom OK/Cancel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateBtn = new JButton("Update");
        updateBtn.setPreferredSize(new Dimension(80, 25));
        updateBtn.setFocusable(true);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setPreferredSize(new Dimension(80, 25));
        cancelBtn.setFocusable(true);
        bottomPanel.add(updateBtn);
        bottomPanel.add(cancelBtn);

        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // ---- Event Handlers ----
        browseDirectory.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Select Tool Directory");
            fc.setCurrentDirectory(FileManager.getDirectory());
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                directoryField.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });

        addBtn.addActionListener(e -> {
            String arg = JOptionPane.showInputDialog(this, "Enter the command argument:", "New Argument",
                    JOptionPane.PLAIN_MESSAGE);
            if (arg != null && !arg.trim().isEmpty()) {
                argumentsModel.addElement(arg.trim());
            }
        });

        removeBtn.addActionListener(e -> {
            int idx = argumentsList.getSelectedIndex();
            if (idx != -1) {
                argumentsModel.remove(idx);
            }
        });

        modifyBtn.addActionListener(e -> {
            int idx = argumentsList.getSelectedIndex();
            if (idx != -1) {
                String current = argumentsModel.get(idx);

                String newArg = (String) JOptionPane.showInputDialog(
                        this,
                        "Update the command argument:",
                        "Update Argument",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        current);

                if (newArg != null && !newArg.trim().isEmpty()) {
                    argumentsModel.set(idx, newArg.trim());
                }
            }
        });

        upBtn.addActionListener(e -> {
            int idx = argumentsList.getSelectedIndex();
            if (idx > 0) {
                String element = argumentsModel.remove(idx);
                argumentsModel.add(idx - 1, element);
                argumentsList.setSelectedIndex(idx - 1);
            }
        });

        downBtn.addActionListener(e -> {
            int idx = argumentsList.getSelectedIndex();
            if (idx != -1 && idx < argumentsModel.size() - 1) {
                String element = argumentsModel.remove(idx);
                argumentsModel.add(idx + 1, element);
                argumentsList.setSelectedIndex(idx + 1);
            }
        });

        updateBtn.addActionListener(e -> {
            if (nameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "The tool name cannot be an empty string.", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            File directory = new File(directoryField.getText());

            if (!(directory.exists() && directory.isDirectory())) {
                JOptionPane.showMessageDialog(this, "The directory is not valid.", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!shortcutField.getText().isEmpty() && shortcutField.getText().contains(" ")) {
                JOptionPane.showMessageDialog(this, "The shortcut cannot have spaces.", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (argumentsModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, "The command line argumments is empty.", "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ToolsManager.isNameUnique(nameField.getText())) {
                JOptionPane.showMessageDialog(this, "The tool name must be unique.", "Name Already Exist",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!shortcutField.getText().isEmpty() && !ToolsManager.isShortcutUnique(shortcutField.getText())) {
                JOptionPane.showMessageDialog(this, "The shortcut must be unique.", "Shortcut Already Exist",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            tool.setName(nameField.getText());
            tool.setDescription(descriptionField.getText());
            tool.setDirectory(directory);
            tool.setShortcut(shortcutField.getText());
            tool.setArguments(new ArrayList<>());

            for (int i = 0; i < argumentsModel.size(); i++) {
                tool.addArgument(argumentsModel.get(i));
            }

            ToolsManager.updateTool(tool);

            if (isDisplayable()) {
                dispose();
            }
        });

        cancelBtn.addActionListener(e -> dispose());
    }
}
