package com.prism.components.menus;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.prism.Prism;
import com.prism.components.extended.JClosableComponent;
import com.prism.components.frames.AboutPrism;
import com.prism.components.frames.ConfigurationDialog;
import com.prism.components.frames.EditToolFrame;
import com.prism.components.frames.WarningDialog;
import com.prism.components.textarea.TextArea;
import com.prism.managers.FileManager;
import com.prism.utils.ResourceUtil;

public class PrismMenuBar extends JMenuBar {
    public PrismMenuBar(Prism prism) {
        /*
         * File menu
         */
        JMenu fileMenu = new JMenu("File");

        JMenuItem menuItemNewFile = createMenuItem("New File", ResourceUtil.getIcon("icons/new_file.png"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        menuItemNewFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.openNewFile();
            }
        });

        JMenuItem menuItemOpenFile = createMenuItem("Open File", ResourceUtil.getIcon("icons/file_open.png"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        menuItemOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.openFile();
            }
        });

        JMenuItem menuItemOpenFolder = createMenuItem("Open Folder", ResourceUtil.getIcon("icons/folder_open.png"),
                null, null);
        menuItemOpenFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.openFolder();
            }
        });

        JMenuItem menuItemSave = createMenuItem("Save", ResourceUtil.getIcon("icons/save.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        menuItemSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.saveFile();
            }
        });

        JMenuItem menuItemSaveAs = createMenuItem("Save As", ResourceUtil.getIcon("icons/saveas.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
        menuItemSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.saveAsFile();
            }
        });

        JMenuItem menuItemSaveAll = createMenuItem("Save All", ResourceUtil.getIcon("icons/saveall.gif"), null,
                null);
        menuItemSaveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.saveAllFiles();
            }
        });

        JMenuItem menuItemCloseApp = createMenuItem("Exit", null, null,
                null);
        menuItemCloseApp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (prism.isDisplayable()) {
                    prism.dispose();
                    System.exit(0);
                }
            }
        });

        fileMenu.add(menuItemNewFile);
        fileMenu.addSeparator();
        fileMenu.add(menuItemOpenFile);
        fileMenu.add(menuItemOpenFolder);
        fileMenu.addSeparator();
        fileMenu.add(menuItemSave);
        fileMenu.add(menuItemSaveAs);
        fileMenu.add(menuItemSaveAll);
        fileMenu.addSeparator();
        fileMenu.add(menuItemCloseApp);

        /*
         * Edit menu
         */

        JMenu editMenu = new JMenu("Edit");

        JMenuItem menuItemUndo = createMenuItem("Undo", ResourceUtil.getIcon("icons/undo.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK));
        menuItemUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                if (textArea.canUndo()) {
                    textArea.undoLastAction();
                }
            }
        });

        JMenuItem menuItemRedo = createMenuItem("Redo", ResourceUtil.getIcon("icons/redo.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK));
        menuItemRedo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                if (textArea.canRedo()) {
                    textArea.redoLastAction();
                }
            }
        });

        JMenuItem menuItemCut = createMenuItem("Cut", ResourceUtil.getIcon("icons/cut.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        menuItemCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                textArea.cut();
            }
        });

        JMenuItem menuItemCopy = createMenuItem("Copy", ResourceUtil.getIcon("icons/copy.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        menuItemCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                textArea.copy();
            }
        });

        JMenuItem menuItemPaste = createMenuItem("Paste", ResourceUtil.getIcon("icons/paste.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        menuItemPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                textArea.paste();
            }
        });

        JMenuItem menuItemDelete = createMenuItem("Delete", ResourceUtil.getIcon("icons/trash.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menuItemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                textArea.replaceSelection("");
            }
        });

        JMenuItem menuItemSelectAll = createMenuItem("Select All", null, null,
                KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        menuItemSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                textArea.selectAll();
            }
        });

        JMenuItem menuItemOptions = createMenuItem("Options", null, null, null);
        menuItemOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ConfigurationDialog();
            };
        });

        editMenu.add(menuItemUndo);
        editMenu.add(menuItemRedo);
        editMenu.addSeparator();
        editMenu.add(menuItemCut);
        editMenu.add(menuItemCopy);
        editMenu.add(menuItemPaste);
        editMenu.add(menuItemDelete);
        editMenu.add(menuItemSelectAll);
        editMenu.addSeparator();
        editMenu.add(menuItemOptions);

        /*
         * View menu
         */

        JMenu viewMenu = new JMenu("View");

        JMenuItem menuItemSidebar = createMenuItem("Sidebar", ResourceUtil.getIcon("icons/sidebar.gif"), null, null);
        menuItemSidebar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prism.addBackComponent(JClosableComponent.ComponentType.SIDEBAR);
            }
        });

        JMenuItem menuItemLowerSidebar = createMenuItem("Lower Sidebar",
                ResourceUtil.getIcon("icons/lower_sidebar.gif"), null, null);
        menuItemLowerSidebar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prism.addBackComponent(JClosableComponent.ComponentType.LOWER_SIDEBAR);
            }
        });

        viewMenu.add(menuItemSidebar);
        viewMenu.add(menuItemLowerSidebar);

        /*
         * Tools menu
         */

        JMenu toolsMenu = new JMenu("Tools");

        JMenuItem menuItemNewTool = createMenuItem("New Tool", null, null, null);
        menuItemNewTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EditToolFrame();
            }
        });

        toolsMenu.add(menuItemNewTool);

        /*
         * Go menu
         */

        JMenu goMenu = new JMenu("Go");

        /*
         * Help menu
         */

        JMenu helpMenu = new JMenu("Help");

        JMenuItem menuItemHelp = createMenuItem("Help?", ResourceUtil.getIcon("icons/help.gif"),
                null,
                null);
        menuItemHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI("https://www.google.com"));
                    } catch (IOException | URISyntaxException err) {
                        WarningDialog.showWarningDialog(prism, err);
                    }
                } else {
                    JOptionPane.showMessageDialog(prism,
                            "Faild to open Help URL.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JMenuItem menuItemAbout = createMenuItem("About Prism", ResourceUtil.getIcon("icons/information.png"),
                null,
                null);
        menuItemAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AboutPrism();
            }
        });

        helpMenu.add(menuItemHelp);
        helpMenu.addSeparator();
        helpMenu.add(menuItemAbout);

        /*
         * End
         */

        add(fileMenu);
        add(editMenu);
        add(viewMenu);
        add(toolsMenu);
        add(goMenu);
        add(helpMenu);
    }

    private JMenuItem createMenuItem(String text, ImageIcon menuItemIcon, String tooltip, KeyStroke accelerator) {
        JMenuItem menuItem = new JMenuItem(text);

        if (tooltip != null) {
            menuItem.setToolTipText(tooltip);
        }

        if (accelerator != null) {
            menuItem.setAccelerator(accelerator);
        }

        if (menuItemIcon != null) {
            menuItem.setIcon(menuItemIcon);
        }

        return menuItem;
    }
}
