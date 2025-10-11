package com.prism.components.sidebar;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.prism.components.panels.MathPanel;
import com.prism.utils.ResourceUtil;

public class LowerSidebar extends JTabbedPane {

    public LowerSidebar(JLabel header, JPanel terminalArea, JPanel bookmarksArea, JPanel tasksArea) {
        super(JTabbedPane.BOTTOM);

        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setFocusable(false);

        addTasks(tasksArea);
        addTerminalArea(terminalArea);
        addBookmarks(bookmarksArea);
        addMath();
        addErrorLogger();

        setSelectedIndex(1);

        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = getSelectedIndex();

                switch (index) {
                    case 0:
                        header.setText("Tasks");
                        break;
                    case 1:
                        header.setText("Console");
                        break;
                    case 2:
                        header.setText("Math");
                        break;
                    case 3:
                        header.setText("Bookmarks");
                        break;
                    case 4:
                        header.setText("Error Logs");
                        break;
                    
                }
            }
        });
    }

    private void addTasks(JPanel tasksArea) {
        addTab("Tasks", ResourceUtil.getIcon("icons/task_marked.gif"), tasksArea);
    }

    private void addTerminalArea(JPanel terminalArea) {
        addTab("Console", ResourceUtil.getIcon("icons/console.gif"), terminalArea);
    }

    private void addMath() {
        addTab("Math", ResourceUtil.getIcon("icons/math.gif"), new MathPanel());
    }

    private void addBookmarks(JPanel bookmarksArea) {
        addTab("Bookmarks", ResourceUtil.getIcon("icons/bookmark_nav.gif"), bookmarksArea);
    }

    private void addErrorLogger() {
        addTab("Error Logs", ResourceUtil.getIcon("icons/error_log.gif"), null);
    }
}
