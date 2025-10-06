package com.prism.components.terminal;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.prism.components.toolbar.TerminalToolbar;
import com.prism.managers.TerminalManager;
import com.prism.utils.ResourceUtil;

public class TerminalTabbedPane extends JTabbedPane {
    public TerminalTabbedPane() {
        setFocusable(false);
        setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
        //setTabPlacement(JTabbedPane.RIGHT);
    }

    public void addTerminalTab(Terminal terminal, int type, int id) {
        addTab((type == 1 ? "Powershell #" : "Command Prompt #") + id, terminal);

        addFeaturesToTab(terminal);
    }

    public void removeTerminalTab(Terminal terminal) {
        int index = findIndexByTerminal(terminal);

        if (index != -1) {
            removeTabAt(index);
        }
    }

    public void redirectUserToTab(Terminal terminal) {
        int index = findIndexByTerminal(terminal);

        if (index != -1) {
            setSelectedIndex(index);
        }
    }

    public int findIndexByTextArea(Terminal terminal) {
        int index = indexOfComponent(terminal);

        return index;
    }

    public int findIndexByTerminal(Terminal terminal) {
        for (int i = 0; i < getTabCount(); i++) {
            Terminal terminalIndexed = (Terminal) getComponentAt(i);

            if (terminalIndexed == terminal) {
                return i;
            }
        }

        return -1;
    }

    public void closeTabByIndex(int index, boolean... openNewTerminalIfAllTabsAreClosed) {
        if (index < 0 || index >= getTabCount()) {
            return;
        }

        removeTabAt(index);

        TerminalManager.terminals.remove(index);

        if (openNewTerminalIfAllTabsAreClosed.length == 1 || openNewTerminalIfAllTabsAreClosed[0]) {
            openNewTerminalIfAllTabsAreClosed();
        }
    }

    public Terminal getCurrentTerminal() {
        return getTerminalFromIndex(getSelectedIndex());
    }

    public Terminal getTerminalFromIndex(int index) {
        if (index < 0 || index >= getTabCount()) {
            return null;
        }

        return TerminalManager.terminals.get(index);
    }

    public void openNewTerminalIfAllTabsAreClosed() {
        if (getTabCount() == 0) {
            TerminalManager.newTerminal(TerminalToolbar.comboboxTerminalShell.getSelectedIndex());
        }
    }

    public void addFeaturesToTab(Terminal terminal) {
        int index = findIndexByTerminal(terminal);

        if (index != -1) {
            addFeaturesToTab(index, terminal.shell == 1 ? ResourceUtil.getIcon("icons/powershell.png") : ResourceUtil.getIcon("icons/cmd_prompt.gif"));
        }
    }

    public void addFeaturesToTab(int index, Icon icon) {
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setOpaque(false);
        tabPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel tabTitle = new JLabel(getTitleAt(index), icon, JLabel.LEFT);
        tabTitle.setIconTextGap(5);

        JButton closeButton = new JButton("  ✕");
        closeButton.setPreferredSize(new Dimension(17, 17));
        closeButton.setFocusable(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = indexOfTabComponent(tabPanel);

                closeTabByIndex(index, true);
            }
        });

        tabPanel.add(tabTitle, BorderLayout.WEST);
        tabPanel.add(closeButton, BorderLayout.EAST);

        setTabComponentAt(index, tabPanel);
    }
}
