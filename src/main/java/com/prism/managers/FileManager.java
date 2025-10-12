package com.prism.managers;

import java.awt.Desktop;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.prism.Prism;
import com.prism.components.files.PrismFile;
import com.prism.components.frames.ErrorDialog;
import com.prism.components.textarea.TextArea;
import com.prism.components.textarea.TextAreaTabbedPane.ImageViewerContainer;
import com.prism.config.Config;
import com.prism.utils.Extensions;
import com.prism.utils.Languages;

public class FileManager {

    public static Prism prism = Prism.getInstance();

    public static File directory = null;
    public static HashMap<String, String> textDiffCache = new HashMap<>();
    public static List<PrismFile> files = new ArrayList<>();

    public static JFileChooser fileChooser = new JFileChooser();

    static {
        fileChooser.setCurrentDirectory(directory);

        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.setMultiSelectionEnabled(false);
    }

    public static File getDirectory() {
        return directory;
    }

    public static void setDirectory(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            JOptionPane.showMessageDialog(prism,
                    "Unable to set the directory; Received a null object or the directory is a file.", "Error",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        directory = dir;

        prism.config.set(Config.Key.DIRECTORY_PATH, directory.getAbsolutePath());

        if (prism.fileExplorer != null) {
            prism.fileExplorer.setModel(null);
        }

        fileChooser.setCurrentDirectory(directory);

        if (prism.fileExplorer != null) {
            prism.fileExplorer.setRootDirectory(directory);
        }
    }

    public static void openNewFile() {
        TextArea textArea = new TextArea();

        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        textArea.addSyntaxHighlighting();

        PrismFile prismFile = new PrismFile(null, textArea);

        files.add(prismFile);
        prism.textAreaTabbedPane.addTextAreaTab(prismFile);

        addListenersToTextArea(textArea, prismFile);

        prism.textAreaTabbedPane.redirectUserToTab(prismFile);
    }

    public static void openFile() {
        fileChooser.setDialogTitle("Open File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int response = fileChooser.showOpenDialog(prism);

        if (response == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            openFile(selectedFile);
        }
    }

    public static void openFolder() {
        fileChooser.setDialogTitle("Open Folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int response = fileChooser.showOpenDialog(prism);

        if (response == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = fileChooser.getSelectedFile();

            openFolder(selectedFolder);
        }
    }

    public static void openFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            JOptionPane.showMessageDialog(prism,
                    "Unable to load the file; Received a null object or the file is a directory.", "Error",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        if (!Languages.isSupported(file)) {
            int response = JOptionPane.showConfirmDialog(prism,
                    "This file extension is not supported by Prism, would you like to open it by its default application?",
                    "Unsupported File", JOptionPane.YES_NO_CANCEL_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {

                }

                return;
            } else if (response == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        if (prism.config.getBoolean(Config.Key.WARN_BEFORE_OPENING_LARGE_FILES, true)) {
            int size = getFileSizeInMBExact(file);
            int maxSize = prism.config.getInt(Config.Key.MAX_FILE_SIZE_FOR_WARNING, 10);

            if (size >= maxSize) {
                int confirm = JOptionPane.showConfirmDialog(
                        prism,
                        "Are you sure you want to open \"" + file.getName() + "\"? The file is too large to open.",
                        "Large File",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }

        for (int i = 0; i < files.size(); i++) {
            PrismFile prismFile = files.get(i);

            if (prismFile.getPath() != null && prismFile.getPath().equals(file.getAbsolutePath())) {
                prism.textAreaTabbedPane.redirectUserToTab(prismFile);

                return;
            }
        }

        boolean isImage = Extensions.isImageFormat(file);

        if (!isImage) {
            TextArea textArea = new TextArea();

            textArea.setSyntaxEditingStyle(Languages.getHighlighter(file));
            textArea.addSyntaxHighlighting();

            if (prism.config.getBoolean(Config.Key.AUTOCOMPLETE_ENABLED, true)) {
                textArea.addAutocomplete(file);
            }

            PrismFile prismFile = new PrismFile(file, textArea);

            files.add(prismFile);
            prism.textAreaTabbedPane.addTextAreaTab(prismFile);

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));

                textArea.read(reader, null);

                reader.close();

                RTextScrollPane scrollPane = (RTextScrollPane) SwingUtilities.getAncestorOfClass(RTextScrollPane.class,
                        textArea);

                SwingUtilities.invokeLater(() -> {
                    JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
                    verticalScrollBar.setValue(verticalScrollBar.getMinimum());
                });
            } catch (Exception e) {
                ErrorDialog.showErrorDialog(prism, e);
            }

            addListenersToTextArea(textArea, prismFile);

            prism.textAreaTabbedPane.redirectUserToTab(prismFile);

            prism.bookmarks.updateTreeData(TextAreaManager.getBookmarksOfAllFiles());

            if (files.size() == 2) {
                PrismFile firstFile = files.get(0);

                if (firstFile.getPath() == null && firstFile.getTextArea().getText().trim().isEmpty()) {
                    prism.textAreaTabbedPane.closeTabByIndex(0);
                }
            }

            if (!textDiffCache.containsKey(file.getAbsolutePath())) {
                textDiffCache.put(file.getAbsolutePath(), textArea.getText());
            }
        } else {
            ImageViewerContainer viewer = prism.textAreaTabbedPane.new ImageViewerContainer(file.getPath());

            PrismFile prismFile = new PrismFile(file, viewer);

            files.add(prismFile);
            prism.textAreaTabbedPane.addImageViewerTab(prismFile);

            prism.textAreaTabbedPane.redirectUserToTab(prismFile);
        }
    }

    public static void openRecentFiles() {
        String[] recentFilePaths = prism.config.getStringArray(Config.Key.RECENT_OPENED_FILES);

        for (String path : recentFilePaths) {
            File file = new File(path);

            if (file.exists() && file.isFile()) {
                openFile(file);
            }
        }
    }

    private static void addListenersToTextArea(TextArea textArea, PrismFile prismFile) {
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                prism.updateStatusBar();

                prism.codeOutline.updateTree();
                prism.primaryToolbar.updateToolbar();
                prism.menuBar.updateMenuBar();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                prism.updateStatusBar();

                prism.codeOutline.updateTree();
                prism.primaryToolbar.updateToolbar();
                prism.menuBar.updateMenuBar();
            }

            @Override
            public void keyTyped(KeyEvent e) {
                prism.updateStatusBar();

                prism.codeOutline.updateTree();
                prism.primaryToolbar.updateToolbar();
                prism.menuBar.updateMenuBar();
            }
        });

        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                prism.updateStatusBar();
            }
        });

        textArea.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0) {
                    if (e.getWheelRotation() < 0) {
                        TextAreaManager.zoomIn();
                    } else if (e.getWheelRotation() > 0) {
                        TextAreaManager.zoomOut();
                    }
                } else {
                    e.getComponent().getParent().dispatchEvent(e);
                }
            }
        });

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {

            }

            @Override
            public void removeUpdate(DocumentEvent e) {

            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                prismFile.setSaved(false);

                updateTitle(prismFile);

                prism.updateStatusBar();
            }
        });
    }

    public static void openFolder(File folder) {
        if (folder == null || !folder.exists() || folder.isFile()) {
            JOptionPane.showMessageDialog(prism,
                    "Unable to load the folder; Received a null object or the folder is a file.", "Error",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        setDirectory(folder);
    }

    public static void saveFile() {
        PrismFile file = prism.textAreaTabbedPane.getCurrentFile();

        saveFile(file);
    }

    public static void saveFile(PrismFile file) {
        String string = file.getTextArea().getText();
        File selectedFile = null;

        if (file.getFile() == null) {
            fileChooser.setDialogTitle("Save File");
            int reponse = fileChooser.showSaveDialog(prism);

            if (reponse == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();

                file.setFile(selectedFile);
            } else {
                return;
            }
        } else {
            selectedFile = file.getFile();
        }

        if (selectedFile != null) {
            try {
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(selectedFile), StandardCharsets.UTF_8));
                writer.write(string);
                writer.close();

                file.setSaved(true);

                updateTitle(file);
            } catch (IOException e) {

            }
        }
    }

    public static void saveAsFile() {
        PrismFile file = prism.textAreaTabbedPane.getCurrentFile();

        saveAsFile(file);
    }

    public static void saveAsFile(PrismFile file) {
        String string = file.getTextArea().getText();
        File selectedFile = null;

        fileChooser.setDialogTitle("Save As");
        int reponse = fileChooser.showSaveDialog(prism);

        if (reponse == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();

            file.setFile(selectedFile);
        } else {
            return;
        }

        if (selectedFile != null) {
            try {
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(selectedFile), StandardCharsets.UTF_8));
                writer.write(string);
                writer.close();

                file.setSaved(true);

                if (file.getPath() != null && !file.getPath().equals(selectedFile.getPath())) {
                    file.setFile(selectedFile);
                }

                updateTitle(file);
            } catch (IOException e) {

            }
        }
    }

    public static void saveAllFiles() {
        for (PrismFile file : files) {
            saveFile(file);
        }
    }

    public static void updateTitle(PrismFile file) {
        prism.setTitle("Prism (" + prism.getVersion() + ") - " + file.getFileName() + (!file.isSaved() ? "*" : ""));
        prism.textAreaTabbedPane.updateTitle(file);

        prism.updateStatusBar();

        prism.primaryToolbar.updateToolbar();
        prism.menuBar.updateMenuBar();

        if (prism.codeOutline.textArea != null && prism.codeOutline.textArea.equals(file.getTextArea())) {
            prism.codeOutline.updateTree();
        } else {
            prism.codeOutline.setSyntaxTextArea(file.getTextArea());
        }
    }

    public static int getFileSizeInMBExact(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return -1;
        }

        double fileSizeInBytes = file.length();
        double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);

        return (int) Math.round(fileSizeInMB);
    }

    public static String getOldText(File file) {
        if (!textDiffCache.containsKey(file.getAbsolutePath())) {
            return null;
        }

        return textDiffCache.get(file.getAbsolutePath());
    }
}
