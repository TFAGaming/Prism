package com.prism;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

import com.prism.components.extended.JClosableComponent;
import com.prism.components.extended.JClosableComponent.ComponentType;
import com.prism.components.files.CodeOutline;
import com.prism.components.files.FileExplorer;
import com.prism.components.files.PrismFile;
import com.prism.components.frames.ErrorDialog;
import com.prism.components.frames.LoadingFrame;
import com.prism.components.frames.WarningDialog;
import com.prism.components.menus.PrismMenuBar;
import com.prism.components.menus.SearchAndReplace;
import com.prism.components.panels.PluginsPanel;
import com.prism.components.sidebar.LowerSidebar;
import com.prism.components.sidebar.Sidebar;
import com.prism.components.tables.Bookmarks;
import com.prism.components.tables.TasksList;
import com.prism.components.terminal.TerminalTabbedPane;
import com.prism.components.textarea.TextArea;
import com.prism.components.textarea.TextAreaTabbedPane;
import com.prism.components.toolbar.BookmarksToolbar;
import com.prism.components.toolbar.PrimaryToolbar;
import com.prism.components.toolbar.TerminalToolbar;
import com.prism.components.toolbar.TasksToolbar;
import com.prism.config.Config;
import com.prism.managers.FileManager;
import com.prism.managers.TextAreaManager;
import com.prism.managers.ToolsManager;
import com.prism.plugins.PluginLoader;
import com.prism.utils.Languages;
import com.prism.utils.ResourceUtil;

public class Prism extends JFrame {

    public static Prism instance;

    public Config config;
    public PluginLoader pluginLoader;

    public LoadingFrame loadingFrame;

    public PrismMenuBar menuBar;
    public PrimaryToolbar primaryToolbar;

    public TextAreaTabbedPane textAreaTabbedPane;
    public TerminalTabbedPane terminalTabbedPane;
    public FileExplorer fileExplorer;
    public CodeOutline codeOutline;
    public Bookmarks bookmarks;
    public TasksList toolsList;

    public LowerSidebar lowerSidebar;
    public JLabel lowerSidebarHeader;

    public Sidebar sidebar;
    public JLabel sidebarHeader;

    public PluginsPanel pluginsPanel;
    public JPanel searchAndReplaceAndStatusBarPanel;
    public SearchAndReplace searchAndReplace;
    public JPanel statusBarPanel;

    public JSplitPane primarySplitPane;
    public JSplitPane secondarySplitPane;

    public JClosableComponent lowerSidebarClosableComponent;
    public JClosableComponent sidebarClosableComponent;

    public List<JClosableComponent> removedComponents = new ArrayList<JClosableComponent>();

    public Prism(String[] args) {
        Prism.instance = this;

        setTitle("Prism");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setIconImage(ResourceUtil.getIcon("icons/Prism.png").getImage());

        setSystemLookAndFeel();

        File file = new File("config.properties");
        config = new Config(file);

        try {
            config.load();
        } catch (Exception e) {
            ErrorDialog.showErrorDialog(this, e);
        }

        pluginLoader = new PluginLoader(new File("plugins"));
        pluginLoader.loadPlugins();

        loadingFrame = new LoadingFrame();
        loadingFrame.setVisible(true);

        if (config.getBoolean(Config.Key.WINDOW_EXTENDED, true)) {
            setExtendedState(MAXIMIZED_BOTH);
        } else {
            setExtendedState(NORMAL);

            setSize(new Dimension(config.getInt(Config.Key.WINDOW_WIDTH, 900),
                    config.getInt(Config.Key.WINDOW_HEIGHT, 600)));
            setLocation(config.getInt(Config.Key.WINDOW_POSITION_X, (int) getLocation().getX()),
                    config.getInt(Config.Key.WINDOW_POSITION_Y, (int) getLocation().getY()));
        }

        if (config.getString(Config.Key.DIRECTORY_PATH) == null) {
            config.set(Config.Key.DIRECTORY_PATH, System.getProperty("user.home"));
        }

        FileManager.setDirectory(new File(config.getString(Config.Key.DIRECTORY_PATH)));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if ((getExtendedState() & MAXIMIZED_BOTH) == MAXIMIZED_BOTH) {
                    config.set(Config.Key.WINDOW_EXTENDED, true);

                } else {
                    config.set(Config.Key.WINDOW_EXTENDED, false);
                }

                if (!config.getBoolean(Config.Key.WINDOW_EXTENDED, true)) {
                    Dimension size = getSize();

                    config.set(Config.Key.WINDOW_WIDTH, size.width);
                    config.set(Config.Key.WINDOW_HEIGHT, size.height);

                    config.set(Config.Key.WINDOW_POSITION_X, (int) getLocation().getX());
                    config.set(Config.Key.WINDOW_POSITION_Y, (int) getLocation().getY());
                }

                String[] recentFilePaths = {};

                for (PrismFile pf : FileManager.files) {
                    if (pf.getPath() == null) {
                        continue;
                    }

                    String[] newArray = Arrays.copyOf(recentFilePaths, recentFilePaths.length + 1);

                    newArray[newArray.length - 1] = pf.getPath();

                    recentFilePaths = newArray;
                }

                config.set(Config.Key.RECENT_OPENED_FILES, recentFilePaths);

                config.set(Config.Key.PRIMARY_SPLITPANE_DIVIDER_LOCATION, primarySplitPane.getDividerLocation());
                config.set(Config.Key.SECONDARY_SPLITPANE_DIVIDER_LOCATION, secondarySplitPane.getDividerLocation());

                config.set(Config.Key.SIDEBAR_CLOSABLE_COMPONENT_OPENED, sidebarClosableComponent.isClosed());
                config.set(Config.Key.LOWER_SIDEBAR_CLOSABLE_COMPONENT_OPENED, lowerSidebarClosableComponent.isClosed());
            }
        });

        menuBar = new PrismMenuBar();
        setJMenuBar(menuBar);

        primaryToolbar = new PrimaryToolbar();
        add(primaryToolbar, BorderLayout.NORTH);

        // Text Area Tabbed pane
        textAreaTabbedPane = new TextAreaTabbedPane();

        File directory = new File(config.getString(Config.Key.DIRECTORY_PATH));
        if (!directory.exists() || !directory.isDirectory()) {
            directory = new File(System.getProperty("user.home"));
            config.set(Config.Key.DIRECTORY_PATH, directory.getAbsolutePath());
        }

        // Lower tabbed pane
        // - Terminal
        JPanel terminalArea = new JPanel(new BorderLayout());

        terminalTabbedPane = new TerminalTabbedPane();

        terminalArea.add(new TerminalToolbar(this), BorderLayout.NORTH);
        terminalArea.add(terminalTabbedPane, BorderLayout.CENTER);

        // - Bookmarks
        JPanel bookmarksArea = new JPanel(new BorderLayout());

        bookmarks = new Bookmarks();

        bookmarksArea.add(new BookmarksToolbar(this), BorderLayout.NORTH);
        bookmarksArea.add(bookmarks, BorderLayout.CENTER);

        // - Tasks
        JPanel tasksArea = new JPanel(new BorderLayout());

        toolsList = new TasksList();

        tasksArea.add(new TasksToolbar(this), BorderLayout.NORTH);
        tasksArea.add(toolsList, BorderLayout.CENTER);

        // Secondary Split pane
        lowerSidebarHeader = new JLabel("Console");
        lowerSidebar = new LowerSidebar(lowerSidebarHeader, terminalArea, bookmarksArea, tasksArea);

        lowerSidebarClosableComponent = new JClosableComponent(JClosableComponent.ComponentType.LOWER_SIDEBAR,
                lowerSidebarHeader, lowerSidebar);

        if (config.getBoolean(Config.Key.LOWER_SIDEBAR_CLOSABLE_COMPONENT_OPENED, false)) {
            lowerSidebarClosableComponent.closeComponent();
        }

        secondarySplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textAreaTabbedPane,
                lowerSidebarClosableComponent);
        secondarySplitPane.setDividerLocation(config.getInt(Config.Key.SECONDARY_SPLITPANE_DIVIDER_LOCATION, 300));
        secondarySplitPane.setResizeWeight(0.3);

        // File Explorer, Code outline, Plugins
        fileExplorer = new FileExplorer(directory);
        codeOutline = new CodeOutline();
        pluginsPanel = new PluginsPanel(pluginLoader.getPlugins());

        // Primary Split pane
        sidebarHeader = new JLabel("File Explorer");
        sidebar = new Sidebar(sidebarHeader, fileExplorer, codeOutline, pluginsPanel);

        sidebarClosableComponent = new JClosableComponent(JClosableComponent.ComponentType.SIDEBAR, sidebarHeader,
                sidebar);

        primarySplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebarClosableComponent, secondarySplitPane);
        primarySplitPane.setDividerLocation(config.getInt(Config.Key.PRIMARY_SPLITPANE_DIVIDER_LOCATION, 250));
        primarySplitPane.setResizeWeight(0.3);

        if (config.getBoolean(Config.Key.SIDEBAR_CLOSABLE_COMPONENT_OPENED, false)) {
            sidebarClosableComponent.closeComponent();
        }

        add(primarySplitPane);

        // idk what to comment abt this
        textAreaTabbedPane.openNewFileIfAllTabsAreClosed();
        terminalTabbedPane.openNewTerminalIfAllTabsAreClosed();

        // Find & Replace and Status Bar
        searchAndReplaceAndStatusBarPanel = new JPanel(new BorderLayout());

        // Find & Replace
        searchAndReplace = new SearchAndReplace();

        // Status bar
        statusBarPanel = new JPanel(new BorderLayout());

        JLabel statusBarLabel = new JLabel("Getting ready...");
        statusBarLabel.setBorder(new EmptyBorder(5, 5, 5, 0));

        statusBarPanel.add(statusBarLabel, BorderLayout.WEST);

        // Adding both
        searchAndReplaceAndStatusBarPanel.add(searchAndReplace, BorderLayout.NORTH);
        searchAndReplaceAndStatusBarPanel.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.CENTER);
        searchAndReplaceAndStatusBarPanel.add(statusBarPanel, BorderLayout.SOUTH);

        add(searchAndReplaceAndStatusBarPanel, BorderLayout.SOUTH);

        // End
        if (config.getBoolean(Config.Key.OPEN_RECENT_FILES, true)) {
            FileManager.openRecentFiles();
        }

        bookmarks.updateTreeData(TextAreaManager.getBookmarksOfAllFiles());

        ToolsManager.loadTools();
    }

    public static Prism getInstance() {
        return instance;
    }

    public static String getVersion() {
        return "1.0.0-build-16.10.2025";
    }

    public void setSystemLookAndFeel() {
        try {
            try {
                UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                WarningDialog.showWarningDialog(this, e);
            }

            SwingUtilities.updateComponentTreeUI(this);
        } catch (UnsupportedLookAndFeelException e) {
            WarningDialog.showWarningDialog(this, e);
        }
    }

    public void updateStatusBar() {
        if (statusBarPanel == null) {
            return;
        }

        PrismFile file = textAreaTabbedPane.getCurrentFile();
        TextArea textArea = file.getTextArea();

        if (textArea == null) {
            return;
        }

        int caretPosition = textArea.getCaretPosition();
        int lineNumber = 0;
        int column = 0;

        try {
            lineNumber = textArea.getLineOfOffset(caretPosition) + 1;
            int lineStartOffset = textArea.getLineStartOffset(lineNumber - 1);
            column = caretPosition - lineStartOffset;
        } catch (BadLocationException e) {

        }

        String language = "Plain Text";

        String path = file.getPath();

        if (path != null) {
            language = Languages.getFullName(file.getFile());
        }

        String newLabelText = language + " | Length: " + textArea.getText().trim().length() + ", Lines: "
                + textArea.getText().trim().split("\n").length + " | Line: " + lineNumber + ", Column: " + column
                + " | Zoom: " + config.getInt(Config.Key.TEXTAREA_ZOOM, 12) + ", Encoding: UTF-8 | Path: "
                + (path == null ? "Not defined" : path);

        for (Component component : statusBarPanel.getComponents()) {
            if (component instanceof JLabel) {
                ((JLabel) component).setText(newLabelText);

                break;
            }
        }
    }

    public void addBackComponent(ComponentType type) {
        if (!removedComponents.isEmpty()) {
            for (int i = 0; i < removedComponents.size(); i++) {
                JClosableComponent component = removedComponents.get(i);

                if (component.getType() != type) {
                    continue;
                }

                switch (type) {
                    case LOWER_SIDEBAR:
                        removedComponents.remove(i);

                        secondarySplitPane.add(component);
                        secondarySplitPane.revalidate();
                        secondarySplitPane.repaint();

                        secondarySplitPane.setDividerSize(5);
                        secondarySplitPane.setDividerLocation(
                                config.getInt(Config.Key.SECONDARY_SPLITPANE_DIVIDER_LOCATION, 300));
                        secondarySplitPane.setResizeWeight(0.3);
                        break;
                    case SIDEBAR:
                        removedComponents.remove(i);

                        primarySplitPane.add(component);
                        primarySplitPane.revalidate();
                        primarySplitPane.repaint();

                        primarySplitPane.setDividerSize(5);
                        primarySplitPane
                                .setDividerLocation(config.getInt(Config.Key.PRIMARY_SPLITPANE_DIVIDER_LOCATION, 250));
                        primarySplitPane.setResizeWeight(0.3);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public static void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                WarningDialog.showWarningDialog(getInstance(), e);
            }
        }).start();
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            Prism prism = new Prism(args);

            // just to make it look cool
            Random random = new Random();
            int randomNumber = random.nextInt(3) + 2;

            setTimeout(() -> {
                if (prism.loadingFrame.isDisplayable()) {
                    prism.loadingFrame.dispose();
                }

                prism.setVisible(true);

                prism.updateStatusBar();
            }, randomNumber * 1000);
        });
    }
}
