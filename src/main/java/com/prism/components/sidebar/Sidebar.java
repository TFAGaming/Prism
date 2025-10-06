package com.prism.components.sidebar;

import java.awt.BorderLayout;
import java.awt.Label;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.prism.components.files.FileExplorer;
import com.prism.utils.ResourceUtil;

public class Sidebar extends JTabbedPane {
    public Sidebar(FileExplorer fileExplorer) {
        super(JTabbedPane.BOTTOM);

        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setFocusable(false);

        addFileExplorer(fileExplorer);
        addSearch();
        addPlugins();
    }

    private void addFileExplorer(FileExplorer fileExplorer) {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new Label("File Explorer"), BorderLayout.NORTH);
        panel.add(new JScrollPane(fileExplorer), BorderLayout.CENTER);

        addTab("File Explorer", ResourceUtil.getIcon("icons/tree_explorer.gif"), panel);
    }

    private void addSearch() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new Label("Find File"), BorderLayout.NORTH);

        addTab("Find File", ResourceUtil.getIcon("icons/search_text.png"), panel);
    }

    private void addPlugins() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new Label("Plugins"), BorderLayout.NORTH);

        addTab("Plugins", ResourceUtil.getIcon("icons/plugin.gif"), panel);
    }
}
