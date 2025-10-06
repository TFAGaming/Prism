package com.prism.components.toolbar;

import static javax.swing.JOptionPane.*;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.util.List;

import com.prism.Prism;
import com.prism.components.terminal.Terminal;
import com.prism.managers.TerminalManager;
import com.prism.utils.ResourceUtil;

public class TerminalToolbar extends JToolBar {

    public static JComboBox comboboxTerminalShell;

    public TerminalToolbar(Prism prism) {
        setFloatable(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(new EmptyBorder(5, 5, 5, 0));

        JButton buttonNewTerminal = createButton(ResourceUtil.getIcon("icons/new.gif"), "New Terminal");
        buttonNewTerminal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TerminalManager.newTerminal(comboboxTerminalShell.getSelectedIndex());
            }
        });

        JButton buttonClearOutput = createButton(ResourceUtil.getIcon("icons/erase.gif"), "Clear Output");
        buttonClearOutput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Terminal terminal = prism.terminalTabbedPane.getCurrentTerminal();

                if (terminal == null) {
                    JOptionPane.showMessageDialog(prism, "Something went wrong; Unable to get the current terminal.",
                            "Error",
                            ERROR_MESSAGE);
                    return;
                }

                terminal.clearTerminal();
            }
        });

        JButton buttonProcessRestart = createButton(ResourceUtil.getIcon("icons/process_restart.gif"), "Restart Process");
        buttonProcessRestart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Terminal terminal = prism.terminalTabbedPane.getCurrentTerminal();

                if (terminal == null) {
                    JOptionPane.showMessageDialog(prism, "Something went wrong; Unable to get the current terminal.",
                            "Error",
                            ERROR_MESSAGE);
                    return;
                }

                terminal.restartProcess();
            }
        });

        JButton buttonProcessStop = createButton(ResourceUtil.getIcon("icons/process_stop.gif"), "Stop Process");
        buttonProcessStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Terminal terminal = prism.terminalTabbedPane.getCurrentTerminal();

                if (terminal == null) {
                    JOptionPane.showMessageDialog(prism, "Something went wrong; Unable to get the current terminal.",
                            "Error",
                            ERROR_MESSAGE);
                    return;
                }

                terminal.closeProcess();
            }
        });

        JButton buttonProcessStopAll = createButton(ResourceUtil.getIcon("icons/process_stop_all.gif"), "Stop All Processes");
        buttonProcessStopAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TerminalManager.stopAllProcesses();
            }
        });

        JButton buttonCopy = createButton(ResourceUtil.getIcon("icons/copy.gif"), "Copy Output");
        buttonCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Terminal terminal = prism.terminalTabbedPane.getCurrentTerminal();

                if (terminal == null) {
                    JOptionPane.showMessageDialog(prism, "Something went wrong; Unable to get the current terminal.",
                            "Error",
                            ERROR_MESSAGE);
                    return;
                }

                terminal.terminalArea.selectAll();
                terminal.terminalArea.copy();
                terminal.terminalArea.select(0, 0);
            }
        });

        JButton buttonRunPreviousCommand = createButton(ResourceUtil.getIcon("icons/process_run_previous.gif"),
                "Run Previous Command");
        buttonRunPreviousCommand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Terminal terminal = prism.terminalTabbedPane.getCurrentTerminal();

                if (terminal == null) {
                    JOptionPane.showMessageDialog(prism, "Something went wrong; Unable to get the current terminal.",
                            "Error",
                            ERROR_MESSAGE);
                    return;
                }

                terminal.runPreviousCommand();
            }
        });

        JButton buttonExecutedCommandsHistory = createButton(ResourceUtil.getIcon("icons/executed_commands_history.gif"),
                "View Commands History");
        buttonExecutedCommandsHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Terminal terminal = prism.terminalTabbedPane.getCurrentTerminal();

                if (terminal == null) {
                    JOptionPane.showMessageDialog(prism, "Something went wrong; Unable to get the current terminal.",
                            "Error",
                            ERROR_MESSAGE);
                    return;
                }

                List<String> commands = terminal.getCommands();

                if (commands.size() == 0) {
                    showMessageDialog(prism, "The commands history for this terminal is empty.",
                            "Terminal Commands History",
                            WARNING_MESSAGE);

                    return;
                }

                //new TerminalCommandsHistory(terminal.getCommands());
            }
        });

        String[] terminalShells = {"Command Prompt", "PowerShell"};
        comboboxTerminalShell = new JComboBox<>(terminalShells);
        comboboxTerminalShell.setFocusable(false);
        Dimension preferredSize = comboboxTerminalShell.getPreferredSize();
        preferredSize.width += 20;
        comboboxTerminalShell.setPreferredSize(preferredSize);

        add(buttonNewTerminal);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonClearOutput);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonProcessRestart);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonProcessStop);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonProcessStopAll);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonCopy);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonRunPreviousCommand);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonExecutedCommandsHistory);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(comboboxTerminalShell);

        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
    }

    private JButton createButton(ImageIcon buttonIcon, String tooltip) {
        JButton button = new JButton();

        if (tooltip != null) {
            button.setToolTipText(tooltip);
        }

        button.setPreferredSize(new Dimension(20, 20));

        Image scaledImage = buttonIcon.getImage().getScaledInstance(16, 16, Image.SCALE_FAST);
        button.setIcon(new ImageIcon(scaledImage));

        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
}
