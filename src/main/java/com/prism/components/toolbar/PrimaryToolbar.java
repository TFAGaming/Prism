package com.prism.components.toolbar;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import com.prism.Prism;
import com.prism.components.files.PrismFile;
import com.prism.components.frames.TextDiffer;
import com.prism.managers.FileManager;
import com.prism.managers.TextAreaManager;
import com.prism.utils.ResourceUtil;

public class PrimaryToolbar extends JToolBar {
    public Prism prism = Prism.getInstance();

    public JButton buttonNewFile;
    public JButton buttonFileOpen;
    public JButton buttonFolder;
    public JButton buttonSave;
    public JButton buttonSaveAll;
    public JButton buttonCopy;
    public JButton buttonPaste;
    public JButton buttonCut;
    public JButton buttonUndo;
    public JButton buttonRedo;
    public JButton buttonZoomIn;
    public JButton buttonZoomOut;
    public JButton buttonRefreshTextArea;
    public JButton buttonSortTabs;
    public JButton buttonTextDiff;

    public PrimaryToolbar() {
        setFloatable(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(new EmptyBorder(5, 5, 5, 0));

        buttonNewFile = createButton(ResourceUtil.getIcon("icons/new_file.png"), "New File");
        buttonNewFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.openNewFile();
            }
        });

        buttonFileOpen = createButton(ResourceUtil.getIcon("icons/file_open.png"), "New File");
        buttonFileOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.openFile();
            }
        });

        buttonFolder = createButton(ResourceUtil.getIcon("icons/folder_open.png"), "Open Folder");
        buttonFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.openFolder();
            }
        });

        buttonSave = createButton(ResourceUtil.getIcon("icons/save.gif"), "Save");
        buttonSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrismFile file = Prism.getInstance().textAreaTabbedPane.getCurrentFile();

                FileManager.saveFile(file);
            }
        });

        buttonSaveAll = createButton(ResourceUtil.getIcon("icons/saveall.gif"), "Save All");
        buttonSaveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.saveAllFiles();
            }
        });

        buttonCopy = createButton(ResourceUtil.getIcon("icons/copy.gif"), "Copy");
        buttonCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                if (textArea != null) {
                    textArea.copy();
                }
            }
        });

        buttonPaste = createButton(ResourceUtil.getIcon("icons/paste.gif"), "Paste");
        buttonPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                if (textArea != null) {
                    textArea.paste();
                }
            }
        });

        buttonCut = createButton(ResourceUtil.getIcon("icons/cut.gif"), "Cut");
        buttonCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                if (textArea != null) {
                    textArea.cut();
                }
            }
        });

        buttonUndo = createButton(ResourceUtil.getIcon("icons/undo.gif"), "Undo Edit");
        buttonUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                if (textArea != null && textArea.canUndo()) {
                    textArea.undoLastAction();
                }
            }
        });

        buttonRedo = createButton(ResourceUtil.getIcon("icons/redo.gif"), "Redo Edit");
        buttonRedo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                if (textArea != null && textArea.canRedo()) {
                    textArea.redoLastAction();
                }
            }
        });

        buttonZoomIn = createButton(ResourceUtil.getIcon("icons/zoom_in.png"), "Zoom In");
        buttonZoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextAreaManager.zoomIn();
            }
        });

        buttonZoomOut = createButton(ResourceUtil.getIcon("icons/zoom_out.png"), "Zoom Out");
        buttonZoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextAreaManager.zoomOut();
            }
        });

        buttonRefreshTextArea = createButton(ResourceUtil.getIcon("icons/refresh_txt_area.gif"), "Refresh All Tabs");
        buttonRefreshTextArea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        buttonSortTabs = createButton(ResourceUtil.getIcon("icons/sort_tabs.gif"), "Sort Tabs Alphabetically");
        buttonSortTabs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        buttonTextDiff = createButton(ResourceUtil.getIcon("icons/textdiff.png"), "Text Difference");
        buttonTextDiff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrismFile prismFile = prism.textAreaTabbedPane.getCurrentFile();
                com.prism.components.textarea.TextArea textArea = prismFile.getTextArea();
                File file = prismFile.getFile();

                if (file == null) {
                    return;
                }

                String oldText = FileManager.getOldText(file);

                if (oldText == null) {
                    return;
                }

                if (textArea != null) {
                    new TextDiffer(file, oldText, file, textArea.getText());
                }
            }
        });

        add(buttonNewFile);
        add(Box.createRigidArea(new Dimension(4, 0)));
        add(buttonFileOpen);
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
        add(buttonTextDiff);

        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
    }

    public void updateToolbar() {
        PrismFile prismFile = prism.textAreaTabbedPane.getCurrentFile();

        if (prismFile == null) {
            return;
        }
        
        com.prism.components.textarea.TextArea textArea = prismFile.getTextArea();

        if (textArea != null) {
            buttonRedo.setEnabled(textArea.canRedo());
            buttonUndo.setEnabled(textArea.canUndo());

            buttonSave.setEnabled(!prismFile.isSaved());
        }
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
