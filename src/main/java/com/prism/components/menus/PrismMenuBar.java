package com.prism.components.menus;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import com.prism.Prism;
import com.prism.components.extended.JClosableComponent;
import com.prism.components.files.PrismFile;
import com.prism.components.frames.AboutPrism;
import com.prism.components.frames.ConfigurationDialog;
import com.prism.components.frames.EditToolFrame;
import com.prism.components.frames.NewToolFrame;
import com.prism.components.frames.WarningDialog;
import com.prism.components.terminal.Terminal;
import com.prism.components.textarea.TextArea;
import com.prism.managers.FileManager;
import com.prism.managers.ToolsManager; // Import the manager
import com.prism.components.definition.Tool; // Import the Tool definition
import com.prism.utils.ResourceUtil;

public class PrismMenuBar extends JMenuBar {

    public Prism prism = Prism.getInstance();

    // Existing Menu Items
    JMenuItem menuItemNewFile;
    JMenuItem menuItemOpenFile;
    JMenuItem menuItemOpenFolder;
    JMenuItem menuItemSave;
    JMenuItem menuItemSaveAs;
    JMenuItem menuItemSaveAll;
    JMenuItem menuItemCloseApp;
    JMenuItem menuItemUndo;
    JMenuItem menuItemRedo;
    JMenuItem menuItemCut;
    JMenuItem menuItemCopy;
    JMenuItem menuItemPaste;
    JMenuItem menuItemDelete;
    JMenuItem menuItemSelectAll;
    JMenuItem menuItemOptions;
    JMenuItem menuItemSidebar;
    JMenuItem menuItemLowerSidebar;
    JMenuItem menuItemNewTool;
    JMenuItem menuItemHelp;
    JMenuItem menuItemAbout;

    // --- New Menu Items ---
    // File Menu
    JMenuItem menuItemCloseFile;
    JMenuItem menuItemCloseAll;

    // Edit Menu
    JMenuItem menuItemFind;
    JMenuItem menuItemReplace;

    // View Menu
    JMenuItem menuItemToggleFullScreen;

    // Tools Menu
    JMenuItem menuItemRunTool;
    JMenuItem menuItemManageTools;

    // Custom tools list components
    private JMenu toolsMenu;
    private JSeparator toolMenuSeparator;

    // Go Menu
    JMenuItem menuItemGoToLine;
    JMenuItem menuItemNextTab;
    JMenuItem menuItemPreviousTab;

    // Help Menu
    JMenuItem menuItemCheckForUpdates;
    // ----------------------

    public PrismMenuBar() {
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

        menuItemOpenFile = createMenuItem("Open File", ResourceUtil.getIcon("icons/file_open.png"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
        menuItemOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.openFile();
            }
        });

        menuItemOpenFolder = createMenuItem("Open Folder", ResourceUtil.getIcon("icons/folder_open.png"),
                null, null);
        menuItemOpenFolder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.openFolder();
            }
        });

        menuItemSave = createMenuItem("Save", ResourceUtil.getIcon("icons/save.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        menuItemSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.saveFile();
            }
        });

        menuItemSaveAs = createMenuItem("Save As", ResourceUtil.getIcon("icons/saveas.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
        menuItemSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.saveAsFile();
            }
        });

        menuItemSaveAll = createMenuItem("Save All", ResourceUtil.getIcon("icons/saveall.gif"), null,
                null);
        menuItemSaveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.saveAllFiles();
            }
        });

        menuItemCloseFile = createMenuItem("Close File", ResourceUtil.getIcon("icons/close.png"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));
        menuItemCloseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //prism.textAreaTabbedPane.closeCurrentFile();
            }
        });

        menuItemCloseAll = createMenuItem("Close All", null, null, null);
        menuItemCloseAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Assuming a method exists to close all tabs
                //prism.textAreaTabbedPane.closeAllFiles();
            }
        });

        menuItemCloseApp = createMenuItem("Exit", null, null,
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
        fileMenu.add(menuItemCloseFile); // New
        fileMenu.add(menuItemCloseAll); // New
        fileMenu.addSeparator();
        fileMenu.add(menuItemCloseApp);

        /*
         * Edit menu
         */
        JMenu editMenu = new JMenu("Edit");

        menuItemUndo = createMenuItem("Undo", ResourceUtil.getIcon("icons/undo.gif"), null,
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

        menuItemRedo = createMenuItem("Redo", ResourceUtil.getIcon("icons/redo.gif"), null,
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

        menuItemCut = createMenuItem("Cut", ResourceUtil.getIcon("icons/cut.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
        menuItemCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                textArea.cut();
            }
        });

        menuItemCopy = createMenuItem("Copy", ResourceUtil.getIcon("icons/copy.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
        menuItemCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                textArea.copy();
            }
        });

        menuItemPaste = createMenuItem("Paste", ResourceUtil.getIcon("icons/paste.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
        menuItemPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                textArea.paste();
            }
        });

        menuItemDelete = createMenuItem("Delete", ResourceUtil.getIcon("icons/trash.gif"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menuItemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                textArea.replaceSelection("");
            }
        });

        menuItemSelectAll = createMenuItem("Select All", null, null,
                KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
        menuItemSelectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

                textArea.selectAll();
            }
        });

        menuItemFind = createMenuItem("Find", ResourceUtil.getIcon("icons/find.png"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK));
        menuItemFind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Placeholder for opening a Find dialog or panel
                System.out.println("Opening Find dialog/panel...");
            }
        });

        menuItemReplace = createMenuItem("Replace...", ResourceUtil.getIcon("icons/replace.png"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
        menuItemReplace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Placeholder for opening a Replace dialog
                System.out.println("Opening Replace dialog...");
            }
        });

        menuItemOptions = createMenuItem("Options", null, null, null);
        menuItemOptions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ConfigurationDialog();
            }
        ;
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
        editMenu.add(menuItemFind); // New
        editMenu.add(menuItemReplace); // New
        editMenu.addSeparator();
        editMenu.add(menuItemOptions);

        /*
         * View menu
         */
        JMenu viewMenu = new JMenu("View");

        menuItemSidebar = createMenuItem("Sidebar", ResourceUtil.getIcon("icons/sidebar.gif"), null, null);
        menuItemSidebar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prism.addBackComponent(JClosableComponent.ComponentType.SIDEBAR);
            }
        });

        menuItemLowerSidebar = createMenuItem("Lower Sidebar",
                ResourceUtil.getIcon("icons/lower_sidebar.gif"), null, null);
        menuItemLowerSidebar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prism.addBackComponent(JClosableComponent.ComponentType.LOWER_SIDEBAR);
            }
        });

        menuItemToggleFullScreen = createMenuItem("Toggle Full Screen", ResourceUtil.getIcon("icons/fullscreen.png"), null,
                KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
        menuItemToggleFullScreen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //prism.toggleFullScreen();
            }
        });

        viewMenu.add(menuItemSidebar);
        viewMenu.add(menuItemLowerSidebar);
        viewMenu.addSeparator();
        viewMenu.add(menuItemToggleFullScreen); // New

        /*
         * Tools menu
         */
        toolsMenu = new JMenu("Tools");

        menuItemNewTool = createMenuItem("New Tool", null, null, null);
        menuItemNewTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NewToolFrame();
            }
        });

        menuItemManageTools = createMenuItem("Manage Tools...", null, null, null);
        menuItemManageTools.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Tool> allTools = ToolsManager.getAllTools();

                if (allTools.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            prism,
                            "No tools have been defined yet. Please create a new tool first.",
                            "Manage Tools",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }

                String[] toolNames = allTools.stream()
                        .map(Tool::getName)
                        .toArray(String[]::new);

                String selectedToolName = (String) JOptionPane.showInputDialog(
                        prism,
                        "Select a tool to edit:",
                        "Manage Tools",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        toolNames,
                        toolNames[0]
                );

                if (selectedToolName != null) {
                    Tool selectedTool = allTools.stream()
                            .filter(tool -> tool.getName().equals(selectedToolName))
                            .findFirst()
                            .orElse(null);

                    if (selectedTool != null) {
                        new EditToolFrame(selectedTool);
                    } else {
                        JOptionPane.showMessageDialog(
                                prism,
                                "Error finding tool: " + selectedToolName,
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        });
        // ----------------------------

        toolsMenu.add(menuItemNewTool);
        toolsMenu.add(menuItemManageTools); // New

        toolMenuSeparator = new JSeparator();
        toolsMenu.add(toolMenuSeparator);

        refreshToolsMenu();

        /*
         * Go menu
         */
        JMenu goMenu = new JMenu("Go");

        // --- New Go Menu Items ---
        menuItemGoToLine = createMenuItem("Go to Line...", null, null,
                KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));
        menuItemGoToLine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Placeholder for bringing up a Go to Line input box/dialog
                //prism.textAreaTabbedPane.getCurrentFile().getTextArea().focusOnGoToLineInput();
            }
        });

        menuItemNextTab = createMenuItem("Next Tab", null, null,
                KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, KeyEvent.CTRL_DOWN_MASK));
        menuItemNextTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Placeholder for switching to the next tab
                //prism.textAreaTabbedPane.switchToNextTab();
            }
        });

        menuItemPreviousTab = createMenuItem("Previous Tab", null, null,
                KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, KeyEvent.CTRL_DOWN_MASK));
        menuItemPreviousTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Placeholder for switching to the previous tab
                //prism.textAreaTabbedPane.switchToPreviousTab();
            }
        });
        // -------------------------

        goMenu.add(menuItemGoToLine); // New
        goMenu.addSeparator();
        goMenu.add(menuItemNextTab); // New
        goMenu.add(menuItemPreviousTab); // New

        /*
         * Help menu
         */
        JMenu helpMenu = new JMenu("Help");

        menuItemHelp = createMenuItem("Help?", ResourceUtil.getIcon("icons/help.gif"),
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

        // --- New Help Menu Item ---
        menuItemCheckForUpdates = createMenuItem("Check for Updates...", null, null, null);
        menuItemCheckForUpdates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Placeholder for logic to check for updates
                //JOptionPane.showMessageDialog(prism, "Checking for updates...", "Update", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        menuItemAbout = createMenuItem("About Prism", ResourceUtil.getIcon("icons/information.png"),
                null,
                null);
        menuItemAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AboutPrism();
            }
        });

        helpMenu.add(menuItemHelp);
        helpMenu.add(menuItemCheckForUpdates);
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

    public void refreshToolsMenu() {
        int initialItemCount = toolsMenu.getMenuComponentCount();
        int separatorIndex = -1;

        for (int i = 0; i < initialItemCount; i++) {
            if (toolsMenu.getMenuComponent(i) == toolMenuSeparator) {
                separatorIndex = i;
                break;
            }
        }

        if (separatorIndex != -1) {
            for (int i = initialItemCount - 1; i > separatorIndex; i--) {
                toolsMenu.remove(i);
            }
        }

        for (Tool tool : ToolsManager.getAllTools()) {
            JMenuItem toolItem = createMenuItem(
                    tool.getName(),
                    null,
                    tool.getDescription().isEmpty() ? null : tool.getDescription(),
                    null
            );

            toolItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Terminal terminal = prism.terminalTabbedPane.getCurrentTerminal();

                    if (terminal == null) {
                        JOptionPane.showMessageDialog(prism, "Something went wrong; Unable to get the current terminal.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    terminal.executeTool(tool);
                }
            });

            toolsMenu.add(toolItem);
        }

        toolsMenu.revalidate();
        toolsMenu.repaint();
    }

    public void updateMenuBar() {
        PrismFile prismFile = prism.textAreaTabbedPane.getCurrentFile();

        if (prismFile == null) {
            // Disable all file/edit actions when no file is open
            menuItemSave.setEnabled(false);
            menuItemSaveAs.setEnabled(false);
            menuItemCloseFile.setEnabled(false); // New
            menuItemUndo.setEnabled(false);
            menuItemRedo.setEnabled(false);
            menuItemCut.setEnabled(false);
            menuItemCopy.setEnabled(false);
            menuItemPaste.setEnabled(false);
            menuItemDelete.setEnabled(false);
            menuItemSelectAll.setEnabled(false);
            menuItemFind.setEnabled(false); // New
            menuItemReplace.setEnabled(false); // New
            menuItemGoToLine.setEnabled(false); // New
            return;
        }

        com.prism.components.textarea.TextArea textArea = prismFile.getTextArea();

        if (textArea != null) {
            menuItemRedo.setEnabled(textArea.canRedo());
            menuItemUndo.setEnabled(textArea.canUndo());

            menuItemSave.setEnabled(!prismFile.isSaved());

            // General text area functions are enabled if a text area exists
            menuItemSaveAs.setEnabled(true);
            menuItemCloseFile.setEnabled(true); // New
            menuItemCut.setEnabled(true);
            menuItemCopy.setEnabled(true);
            menuItemPaste.setEnabled(true);
            menuItemDelete.setEnabled(true);
            menuItemSelectAll.setEnabled(true);
            menuItemFind.setEnabled(true); // New
            menuItemReplace.setEnabled(true); // New
            menuItemGoToLine.setEnabled(true); // New
        }
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
