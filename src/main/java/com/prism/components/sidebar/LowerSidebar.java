package com.prism.components.sidebar;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.prism.utils.ResourceUtil;

public class LowerSidebar extends JTabbedPane {
    public LowerSidebar(JPanel terminalArea, JPanel bookmarksArea, JPanel tasksArea) {
        super(JTabbedPane.BOTTOM);

        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setFocusable(false);

        addTasks(tasksArea);
        addTerminalArea(terminalArea);
        addMath();
        addBookmarks(bookmarksArea);
        addErrorLogger();

        setSelectedIndex(1);
    }

    private void addTasks(JPanel tasksArea) {
        addTab("Tasks", ResourceUtil.getIcon("icons/task_marked.gif"), tasksArea);
    }

    private void addTerminalArea(JPanel terminalArea) {
        addTab("Console", ResourceUtil.getIcon("icons/console.gif"), terminalArea);
    }

    private void addMath() {
        addTab("Math", ResourceUtil.getIcon("icons/math.gif"), null);
    }

    private void addBookmarks(JPanel bookmarksArea) {
        addTab("Bookmarks", ResourceUtil.getIcon("icons/bookmark_nav.gif"), bookmarksArea);
    }

    private void addErrorLogger() {
        addTab("Error Logs", ResourceUtil.getIcon("icons/error_log.gif"), null);
    }
}
