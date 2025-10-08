package com.prism.components.sidebar;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.prism.Prism;
import com.prism.components.files.CodeOutline;
import com.prism.components.files.FileExplorer;
import com.prism.components.toolbar.FileExplorerToolbar;
import com.prism.utils.ResourceUtil;

public class Sidebar extends JTabbedPane {
    public Sidebar(JLabel header, FileExplorer fileExplorer, CodeOutline codeOutline) {
        super(JTabbedPane.BOTTOM);

        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setFocusable(false);

        addFileExplorer(fileExplorer);
        addOutline(codeOutline);
        addSearch();
        addPlugins();

        setSelectedIndex(0);

        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = getSelectedIndex();

                switch (index) {
                    case 0:
                        header.setText("File Explorer");
                        break;
                    case 1:
                        header.setText("Outline");
                        break;
                    case 2:
                        header.setText("Find File");
                        break;
                    case 3:
                        header.setText("Plugins");
                        break;
                }
            }
        });
    }

    private void addFileExplorer(FileExplorer fileExplorer) {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());

        headerPanel.add(new FileExplorerToolbar(Prism.getInstance()), BorderLayout.NORTH);
        headerPanel.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(fileExplorer);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        addTab("File Explorer", ResourceUtil.getIcon("icons/tree_explorer.gif"), panel);
    }

    private void addOutline(CodeOutline codeOutline) {
        JScrollPane scrollPane = new JScrollPane(codeOutline);

        addTab("Outline", ResourceUtil.getIcon("icons/outline.gif"), scrollPane);
    }

    private void addSearch() {
        JPanel panel = new JPanel(new BorderLayout());

        addTab("Find File", ResourceUtil.getIcon("icons/search_text.png"), panel);
    }

    private void addPlugins() {
        JPanel panel = new JPanel(new BorderLayout());

        addTab("Plugins", ResourceUtil.getIcon("icons/plugin.gif"), panel);
    }
}
