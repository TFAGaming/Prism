package com.prism.components.textarea;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fife.ui.rtextarea.RTextScrollPane;

import com.prism.Prism;
import com.prism.components.files.PrismFile;
import com.prism.managers.FileManager;
import com.prism.managers.TextAreaManager;

public class TextAreaTabbedPane extends JTabbedPane {

    public Prism prism = Prism.getInstance();

    public TextAreaTabbedPane() {
        super();

        setFocusable(false);
        setTabLayoutPolicy(SCROLL_TAB_LAYOUT);

        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                PrismFile file = getCurrentFile();

                if (file != null) {
                    FileManager.updateTitle(file);
                }
            }
        });
    }

    public void addTextAreaTab(PrismFile file) {
        RTextScrollPane scrollPane = new RTextScrollPane(file.getTextArea());

        TextAreaManager.setGutter(scrollPane);

        file.setScrollPane(scrollPane);

        addTab(file.getFileName(), scrollPane);

        addFeaturesToTab(file);
    }

    public void removeTextAreaTab(TextArea textArea) {
        int index = findIndexByTextArea(textArea);

        if (index != -1) {
            removeTabAt(index);
        }
    }

    public void removeTextAreaTab(PrismFile file) {
        int index = findIndexByPrismFile(file);

        if (index != -1) {
            removeTabAt(index);
        }
    }

    public void redirectUserToTab(PrismFile file) {
        int index = findIndexByPrismFile(file);

        if (index != -1) {
            setSelectedIndex(index);
        }
    }

    public int findIndexByTextArea(TextArea textArea) {
        int index = indexOfComponent(textArea);

        return index;
    }

    public int findIndexByPrismFile(PrismFile file) {
        for (int i = 0; i < getTabCount(); i++) {
            RTextScrollPane scrollPane = (RTextScrollPane) getComponentAt(i);
            TextArea textArea = (TextArea) scrollPane.getViewport().getView();

            if (textArea == file.getTextArea()) {
                return i;
            }
        }

        return -1;
    }

    public void closeAllTabs() {
        int size = FileManager.files.size();

        for (int index = size - 1; index >= 0; index--) {
            closeTabByIndex(index, true);
        }
    }

    public void closeTabByIndex(int index, boolean... openNewFileIfAllTabsAreClosed) {
        if (index < 0 || index >= getTabCount()) {
            return;
        }

        PrismFile fileIndex = getFileFromIndex(index);

        if (!fileIndex.isSaved()) {
            int response = JOptionPane.showConfirmDialog(Prism.getInstance(),
                    "This file is marked with new changes, do you want to save it?", "File Changes",
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                FileManager.saveFile();
            } else if (response == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        removeTabAt(index);

        FileManager.files.remove(index);

        if (openNewFileIfAllTabsAreClosed.length == 1 && openNewFileIfAllTabsAreClosed[0]) {
            openNewFileIfAllTabsAreClosed();
        }

        if (getTabCount() > 0) {
            PrismFile file = getCurrentFile();

            FileManager.updateTitle(file);
        }

        prism.bookmarks.updateTreeData(TextAreaManager.getBookmarksOfAllFiles());
    }

    public void updateTitle(PrismFile file) {
        int index = findIndexByPrismFile(file);

        if (index != -1) {
            JPanel tabPanel = (JPanel) getTabComponentAt(index);

            if (tabPanel != null && tabPanel.getComponent(0) instanceof JLabel) {
                JLabel tabTitle = (JLabel) tabPanel.getComponent(0);
                tabTitle.setText(file.getFileName() + (!file.isSaved() ? "*" : ""));
            }
        }
    }

    public PrismFile getCurrentFile() {
        return getFileFromIndex(getSelectedIndex());
    }

    public PrismFile getFileFromIndex(int index) {
        if (index < 0 || index >= getTabCount()) {
            return null;
        }

        return FileManager.files.get(index);
    }

    public void openNewFileIfAllTabsAreClosed() {
        if (getTabCount() == 0) {
            FileManager.openNewFile();
        }
    }

    public void addFeaturesToTab(PrismFile file) {
        int index = findIndexByPrismFile(file);

        if (index != -1) {
            addFeaturesToTab(index, file.getIcon());
        }
    }

    public void addFeaturesToTab(int index, Icon icon) {
        JPanel tabPanel = new JPanel(new BorderLayout());
        tabPanel.setOpaque(false);
        tabPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel tabTitle = new JLabel(getTitleAt(index), icon, JLabel.LEFT);
        tabTitle.setIconTextGap(5);
        tabTitle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (SwingUtilities.isRightMouseButton(event)) {
                    JPopupMenu contextMenu = new JPopupMenu();

                    JMenuItem closeItem = new JMenuItem("Close");
                    closeItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int index = indexOfTabComponent(tabPanel);

                            closeTabByIndex(index, true);
                        }
                    });

                    JMenuItem closeAllItem = new JMenuItem("Close All");
                    closeAllItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            closeAllTabs();
                        }
                    });

                    JMenuItem copyPathItem = new JMenuItem("Copy Path");
                    copyPathItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int tabIndex = indexOfTabComponent(tabPanel);
                            PrismFile prismFile = FileManager.files.get(tabIndex);

                            if (prismFile == null) {
                                return;
                            }

                            File file = prismFile.getFile();

                            if (file == null) {
                                return;
                            }

                            copyToClipboard(file.getAbsolutePath());
                        }
                    });

                    contextMenu.add(closeItem);
                    contextMenu.add(closeAllItem);
                    contextMenu.addSeparator();
                    contextMenu.add(copyPathItem);

                    Point point = SwingUtilities.convertPoint(event.getComponent(), event.getPoint(), tabPanel);
                    contextMenu.show(tabPanel, point.x, point.y);
                } else if (SwingUtilities.isLeftMouseButton(event)) {
                    setSelectedIndex(indexOfTabComponent(tabPanel));
                }
            }
        });

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

    public void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }
}
