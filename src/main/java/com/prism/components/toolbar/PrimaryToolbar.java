package com.prism.components.toolbar;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.prism.Prism;
import com.prism.components.files.PrismFile;
import com.prism.managers.FileManager;
import com.prism.managers.TextAreaManager;
import com.prism.utils.ResourceUtil;

public class PrimaryToolbar extends JToolBar {
    public PrimaryToolbar(Prism prism) {
        setFloatable(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(new EmptyBorder(5, 5, 5, 0));

        JButton buttonNewFile = createButton(ResourceUtil.getIcon("icons/new_file.png"), "New File");
        buttonNewFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.openNewFile();
            }
        });

        JButton buttonFolder = createButton(ResourceUtil.getIcon("icons/folder_open.png"), "Open Folder");
        buttonFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.openFolder();
            }
        });

        JButton buttonSave = createButton(ResourceUtil.getIcon("icons/save.gif"), "Save");
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrismFile file = Prism.getInstance().textAreaTabbedPane.getCurrentFile();

                FileManager.saveFile(file);
            }
        });

        JButton buttonSaveAll = createButton(ResourceUtil.getIcon("icons/saveall.gif"), "Save All");
        buttonSaveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.saveAllFiles();
            }
        });

        JButton buttonCopy = createButton(ResourceUtil.getIcon("icons/copy.gif"), "Copy");
        buttonCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                if (textArea != null) {
                    textArea.copy();
                }
            }
        });

        JButton buttonPaste = createButton(ResourceUtil.getIcon("icons/paste.gif"), "Paste");
        buttonPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                if (textArea != null) {
                    textArea.paste();
                }
            }
        });

        JButton buttonCut = createButton(ResourceUtil.getIcon("icons/cut.gif"), "Cut");
        buttonCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                if (textArea != null) {
                    textArea.cut();
                }
            }
        });

        JButton buttonUndo = createButton(ResourceUtil.getIcon("icons/undo.gif"), "Undo Edit");
        buttonUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                if (textArea != null && textArea.canUndo()) {
                    textArea.undoLastAction();
                }
            }
        });

        JButton buttonRedo = createButton(ResourceUtil.getIcon("icons/redo.gif"), "Redo Edit");
        buttonRedo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                if (textArea != null && textArea.canRedo()) {
                    textArea.redoLastAction();
                }
            }
        });

        JButton buttonZoomIn = createButton(ResourceUtil.getIcon("icons/zoom_in.png"), "Zoom In");
        buttonZoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextAreaManager.zoomIn();
            }
        });

        JButton buttonZoomOut = createButton(ResourceUtil.getIcon("icons/zoom_out.png"), "Zoom Out");
        buttonZoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextAreaManager.zoomOut();
            }
        });

        JButton buttonRefreshTextArea = createButton(ResourceUtil.getIcon("icons/refresh_txt_area.gif"), "Refresh All Tabs");
        buttonRefreshTextArea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });

        JButton buttonSortTabs = createButton(ResourceUtil.getIcon("icons/sort_tabs.gif"), "Sort Tabs Alphabetically");
        buttonSortTabs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });

        JButton buttonCloseAllTabs = createButton(ResourceUtil.getIcon("icons/close_all_tabs.gif"), "Close All Tabs");
        buttonCloseAllTabs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        });

        add(buttonNewFile);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonFolder);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonSave);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonSaveAll);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonCut);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonCopy);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonPaste);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonUndo);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonRedo);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonZoomIn);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonZoomOut);

        add(Box.createRigidArea(new Dimension(4, 0)));
        addSeparator(new Dimension(4, 20));
        add(Box.createRigidArea(new Dimension(4, 0)));

        add(buttonRefreshTextArea);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonSortTabs);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonCloseAllTabs);

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

    private ImageIcon getIcon(String iconName) {
        ImageIcon icon = new ImageIcon("resources/" + iconName);

        return icon;
    }
}