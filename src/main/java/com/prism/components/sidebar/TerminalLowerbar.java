package com.prism.components.sidebar;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.prism.components.tables.Bookmarks;
import com.prism.utils.ResourceUtil;

public class TerminalLowerbar extends JTabbedPane {
    public TerminalLowerbar(JPanel terminalArea, Bookmarks bookmarks) {
        super(JTabbedPane.BOTTOM);

        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setFocusable(false);

        addTasks();
        addTerminalArea(terminalArea);
        addTools();
        addMath();
        addBookmarks(bookmarks);
        addErrorLogger();

        setSelectedIndex(1);
    }

    private void addTasks() {
        addTab("Tasks", ResourceUtil.getIcon("icons/task_marked.gif"), null);
    }

    private void addTerminalArea(JPanel terminalArea) {
        addTab("Console", ResourceUtil.getIcon("icons/console.gif"), terminalArea);
    }

    private void addTools() {
        addTab("Tools", ResourceUtil.getIcon("icons/tools.gif"), null);
    }

    private void addMath() {
        addTab("Math", ResourceUtil.getIcon("icons/math.gif"), null);
    }

    private void addBookmarks(Bookmarks bookmarks) {
        addTab("Bookmarks", ResourceUtil.getIcon("icons/bookmark_nav.gif"), bookmarks);
    }

    private void addErrorLogger() {
        addTab("Error Logs", ResourceUtil.getIcon("icons/error_log.gif"), null);
    }
}
