package com.prism.components.files;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.prism.Prism;
import com.prism.config.Config;
import com.prism.managers.FileManager;
import com.prism.utils.Languages;
import com.prism.utils.ResourceUtil;

public class FileExplorer extends JTree {

    public Prism prism = Prism.getInstance();

    private FileSystemView fsv = FileSystemView.getFileSystemView();
    private File rootDirectory;

    public FileExplorer(File rootDirectory) {
        super();

        this.rootDirectory = rootDirectory;

        initializeTree();
    }

    private void initializeTree() {
        setModel(createTreeModel(rootDirectory));

        setRootVisible(true);
        setShowsRootHandles(true);
        setFocusable(true);

        setCellRenderer(new FileTreeCellRenderer());

        addTreeExpansionListener(new FileTreeExpansionListener());
        addTreeSelectionListener(new FileTreeSelectionDirectoryListener());

        addMouseListener(new RightClickMouseListener());
        addMouseListener(new LeftClickMouseListener());

        setDragEnabled(true);
        setDropMode(DropMode.ON);
        setTransferHandler(new FileTransferHandler());

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                TreePath path = getPathForLocation(e.getX(), e.getY());

                if (path != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    if (node.getUserObject() instanceof File) {
                        // Show hand cursor for any file system item (both files and directories)
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        return;
                    }
                }

                // Default cursor when not over a file system item
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private TreeModel createTreeModel(File rootFile) {
        DefaultMutableTreeNode root = createNode(rootFile);

        loadChildren(root);

        return new DefaultTreeModel(root);
    }

    private DefaultMutableTreeNode createNode(File file) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);

        return node;
    }

    private void loadChildren(DefaultMutableTreeNode parentNode) {
        File parentFile = (File) parentNode.getUserObject();

        if (!parentFile.isDirectory()) {
            return;
        }

        File[] files = parentFile.listFiles();
        if (files == null) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        Arrays.sort(files, (f1, f2) -> {
            if (f1.isDirectory() && !f2.isDirectory()) {
                return -1;
            }
            if (!f1.isDirectory() && f2.isDirectory()) {
                return 1;
            }
            return f1.getName().compareToIgnoreCase(f2.getName());
        });

        Map<File, DefaultMutableTreeNode> existingChildren = new HashMap<>();
        for (int i = 0; i < parentNode.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);
            File childFile = (File) childNode.getUserObject();
            existingChildren.put(childFile, childNode);
        }

        Set<File> seen = new HashSet<>();

        int index = 0;
        for (File file : files) {
            DefaultMutableTreeNode childNode = existingChildren.get(file);
            if (childNode == null) {
                childNode = createNode(file);
                parentNode.insert(childNode, index);
            } else {
                if (parentNode.getIndex(childNode) != index) {
                    parentNode.insert(childNode, index);
                }
            }
            seen.add(file);
            index++;
        }

        for (Iterator<Map.Entry<File, DefaultMutableTreeNode>> it = existingChildren.entrySet().iterator(); it.hasNext();) {
            Map.Entry<File, DefaultMutableTreeNode> entry = it.next();
            if (!seen.contains(entry.getKey())) {
                parentNode.remove(entry.getValue());
            }
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private class FileTreeCellRenderer extends DefaultTreeCellRenderer {

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();

                if (userObject instanceof File) {
                    File file = (File) userObject;

                    setText(file.getName());

                    if (file.isDirectory()) {
                        setIcon(getFolderIcon(expanded));
                    } else {
                        setIcon(getFileIcon(file));
                    }
                }
            }

            return this;
        }
    }

    private class FileTreeExpansionListener implements TreeExpansionListener {

        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            TreePath path = event.getPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

            loadChildren(node);

            ((DefaultTreeModel) getModel()).nodeStructureChanged(node);
        }

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
            // Nothing to do when collapsed
        }
    }

    private class FileTreeSelectionDirectoryListener implements TreeSelectionListener {

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            if (node != null && node.getUserObject() instanceof File) {
                File selectedFile = (File) node.getUserObject();

                if (selectedFile.isDirectory()) {
                    loadChildren(node);
                }
            }
        }
    }

    private class RightClickMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent event) {
            if (SwingUtilities.isRightMouseButton(event)) {
                TreePath path = getPathForLocation(event.getX(), event.getY());

                if (path != null) {
                    setSelectionPath(path);

                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                    File file = (File) selectedNode.getUserObject();

                    if (file.isDirectory()) {
                        createFolderContextMenu(file).show(prism.fileExplorer, event.getX(), event.getY());
                    } else if (file.isFile()) {
                        createFileContextMenu(file).show(prism.fileExplorer, event.getX(), event.getY());
                    }
                }
            }
        }
    }

    private class LeftClickMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent event) {
            if (SwingUtilities.isLeftMouseButton(event)) {
                TreePath path = getPathForLocation(event.getX(), event.getY());

                if (path != null) {
                    setSelectionPath(path);

                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                    File file = (File) selectedNode.getUserObject();

                    if (file.isFile()) {
                        FileManager.openFile(file);
                    }
                }
            }
        }
    }

    private class FileTransferHandler extends TransferHandler {

        // Define the actions supported (COPY and MOVE)
        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        // Prepare the data to be dragged (export)
        @Override
        protected Transferable createTransferable(JComponent c) {
            JTree tree = (JTree) c;
            // Get all selected file nodes
            TreePath[] paths = tree.getSelectionPaths();

            if (paths == null || paths.length == 0) {
                return null;
            }

            List<File> filesToTransfer = new ArrayList<>();
            for (TreePath path : paths) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();
                if (userObject instanceof File) {
                    filesToTransfer.add((File) userObject);
                }
            }

            if (filesToTransfer.isEmpty()) {
                return null;
            }

            // Use the standard DataFlavor for a list of files
            return new Transferable() {
                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    return new DataFlavor[]{DataFlavor.javaFileListFlavor};
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return flavor.equals(DataFlavor.javaFileListFlavor);
                }

                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                    if (!isDataFlavorSupported(flavor)) {
                        throw new UnsupportedFlavorException(flavor);
                    }
                    return filesToTransfer;
                }
            };
        }

        // Check if data can be dropped (import)
        @Override
        public boolean canImport(TransferSupport support) {
            if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }

            JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
            TreePath path = dropLocation.getPath();

            if (path == null) {
                // Cannot drop outside the tree structure
                return false;
            }

            DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object userObject = targetNode.getUserObject();

            if (userObject instanceof File) {
                File targetFile = (File) userObject;

                // Allow dropping on a directory, or on a file (to get its parent directory)
                if (targetFile.isDirectory() || targetFile.isFile()) {
                    // Prevent dropping a folder into itself or its descendant
                    try {
                        @SuppressWarnings("unchecked")
                        List<File> draggedFiles = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                        for (File draggedFile : draggedFiles) {
                            File dropDir = getDropDirectory(targetFile);

                            // Check if the dragged file is an ancestor of the target directory
                            if (draggedFile.isDirectory() && dropDir.toPath().startsWith(draggedFile.toPath())) {
                                return false; // Cannot drop an ancestor into a descendant
                            }

                            // Prevent dropping a file to its own current directory
                            if (dropDir.equals(draggedFile.getParentFile())) {
                                return false;
                            }
                        }
                    } catch (Exception e) {
                        return true;
                    }

                    return true;
                }
            }

            return false;
        }

        // Perform the drop action (import)
        @Override
        public boolean importData(TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
            TreePath path = dropLocation.getPath();
            DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            File targetFile = (File) targetNode.getUserObject();

            // Determine the actual destination directory based on the user's request
            File destinationDir = getDropDirectory(targetFile);

            try {
                @SuppressWarnings("unchecked")
                List<File> filesToMove = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                boolean success = true;
                for (File sourceFile : filesToMove) {
                    File newFile = new File(destinationDir, sourceFile.getName());

                    // Move the file (rename is a move on the same file system)
                    if (!sourceFile.renameTo(newFile)) {
                        // Fallback to copy/delete if rename fails (e.g., cross-filesystem move)
                        try {
                            Files.move(sourceFile.toPath(), newFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                            success = false;
                            break;
                        }
                    }
                }

                if (success) {
                    prism.fileExplorer.refresh();
                    return true;
                } else {
                    return false;
                }

            } catch (Exception e) {
                return false;
            }
        }

        private File getDropDirectory(File targetFile) {
            if (targetFile.isDirectory()) {
                return targetFile;
            } else {
                return targetFile.getParentFile();
            }
        }
    }

    public void refresh() {
        TreePath[] expandedPaths = getExpandedPaths();

        TreePath selectedPath = getSelectionPath();

        setModel(createTreeModel(rootDirectory));

        if (expandedPaths != null) {
            for (TreePath path : expandedPaths) {
                expandPathIfExists(path);
            }
        }

        if (selectedPath != null) {
            expandPathIfExists(selectedPath);
            setSelectionPath(selectedPath);
        }
    }

    private TreePath[] getExpandedPaths() {
        java.util.List<TreePath> expanded = new java.util.ArrayList<>();

        for (int i = 0; i < getRowCount(); i++) {
            TreePath path = getPathForRow(i);

            if (isExpanded(path)) {
                expanded.add(path);
            }
        }

        return expanded.toArray(new TreePath[0]);
    }

    private void expandPathIfExists(TreePath oldPath) {
        if (oldPath == null) {
            return;
        }

        Object[] oldComponents = oldPath.getPath();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
        DefaultTreeModel model = (DefaultTreeModel) getModel(); // Get model once

        java.util.List<Object> newPathList = new java.util.ArrayList<>();
        newPathList.add(root);

        DefaultMutableTreeNode currentNode = root;

        for (int i = 1; i < oldComponents.length; i++) {
            DefaultMutableTreeNode oldNode = (DefaultMutableTreeNode) oldComponents[i];
            File oldFile = (File) oldNode.getUserObject();

            loadChildren(currentNode);

            model.nodeStructureChanged(currentNode);

            boolean found = false;
            for (int j = 0; j < currentNode.getChildCount(); j++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) currentNode.getChildAt(j);
                File childFile = (File) child.getUserObject();

                if (childFile.equals(oldFile)) {
                    currentNode = child;
                    newPathList.add(child);
                    found = true;
                    break;
                }
            }

            if (!found) {
                return;
            }
        }

        if (newPathList.size() == oldComponents.length) {
            TreePath newPath = new TreePath(newPathList.toArray());
            expandPath(newPath);
        }
    }

    private JPopupMenu createFolderContextMenu(File file) {
        JPopupMenu folderContextMenu = new JPopupMenu();

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.openFile(file);
            }
        });

        JMenuItem moveItem = new JMenuItem("Move");
        moveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveFile(file);
            }
        });

        //folderContextMenu.add(openItem);
        folderContextMenu.add(moveItem);
        folderContextMenu.addSeparator();

        JMenuItem copyPathItem = new JMenuItem("Copy Path");
        copyPathItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(file.getAbsolutePath());
            }
        });

        folderContextMenu.add(copyPathItem);
        folderContextMenu.addSeparator();

        JMenuItem createFileItem = new JMenuItem("New File");
        createFileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFile(false);
            }
        });

        JMenuItem createFolderItem = new JMenuItem("New Folder");
        createFolderItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFile(true);
            }
        });

        JMenuItem renameItem = new JMenuItem("Rename...");
        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renameFolder(file);
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteFile(file);
            }
        });

        if (FileManager.getDirectory() != null && file.getAbsolutePath().equalsIgnoreCase(FileManager.getDirectory().getAbsolutePath())) {
            moveItem.setEnabled(false);
            renameItem.setEnabled(false);
            deleteItem.setEnabled(false);
        }

        folderContextMenu.add(createFileItem);
        folderContextMenu.add(createFolderItem);
        folderContextMenu.addSeparator();
        folderContextMenu.add(renameItem);
        folderContextMenu.add(deleteItem);

        return folderContextMenu;
    }

    private JPopupMenu createFileContextMenu(File file) {
        JPopupMenu fileContextMenu = new JPopupMenu();

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileManager.openFile(file);
            }
        });

        JMenuItem moveItem = new JMenuItem("Move");
        moveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveFile(file);
            }
        });

        fileContextMenu.add(openItem);
        fileContextMenu.add(moveItem);
        fileContextMenu.addSeparator();

        JMenuItem copyPathItem = new JMenuItem("Copy Path");
        copyPathItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copyToClipboard(file.getAbsolutePath());
            }
        });

        fileContextMenu.add(copyPathItem);
        fileContextMenu.addSeparator();

        JMenuItem renameItem = new JMenuItem("Rename...");
        renameItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renameFile(file);
            }
        });

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteFile(file);
            }
        });

        fileContextMenu.add(renameItem);
        fileContextMenu.add(deleteItem);
        fileContextMenu.addSeparator();

        JMenuItem propertiesItem = new JMenuItem("Properties");
        propertiesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFilePropertiesDialog(file);
            }
        });

        fileContextMenu.add(propertiesItem);

        return fileContextMenu;
    }

    public void newFile(boolean isFolder) {
        File selected = getSelectedFile();
        File parentDir = (selected != null && selected.isDirectory()) ? selected : rootDirectory;

        String type = isFolder ? "folder" : "file";
        String name = JOptionPane.showInputDialog(
                prism,
                "Enter a name for the new " + type + ":",
                "Create New " + (isFolder ? "Folder" : "File"),
                JOptionPane.PLAIN_MESSAGE
        );

        if (name == null || name.trim().isEmpty()) {
            return; // Cancel or blank
        }
        File newFile = new File(parentDir, name.trim());
        if (newFile.exists()) {
            JOptionPane.showMessageDialog(prism,
                    "A file or folder with that name already exists.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean created = isFolder ? newFile.mkdir() : newFile.createNewFile();
            if (created) {
                refreshNode(parentDir);
            } else {
                JOptionPane.showMessageDialog(prism,
                        "Failed to create the new " + type + ".",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(prism,
                    "An error occurred: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void renameFile(File target) {
        if (target == null || !target.exists() || target.isDirectory()) {
            return;
        }
        showRenameDialog(target);
    }

    public void renameFolder(File target) {
        if (target == null || !target.exists() || !target.isDirectory()) {
            return;
        }
        showRenameDialog(target);
    }

    public void deleteFile(File target) {
        int confirm = JOptionPane.showConfirmDialog(
                prism,
                "Are you sure you want to delete \"" + target.getName() + "\"?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = deleteRecursively(target);
        if (!success) {
            JOptionPane.showMessageDialog(prism,
                    "Failed to delete: " + target.getName(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            File parentDir = target.getParentFile();
            refreshNode(parentDir);
        }
    }

    public void moveFile(File target) {
        if (target == null || !target.exists()) {
            JOptionPane.showMessageDialog(prism,
                    "No valid file or folder selected.",
                    "Move",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Gather all directories starting from root
        java.util.List<File> directories = new java.util.ArrayList<>();
        collectDirectories(rootDirectory, directories);

        if (directories.isEmpty()) {
            JOptionPane.showMessageDialog(prism,
                    "No target directories found.",
                    "Move",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create combo box with directory options
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFocusable(true);
        for (File dir : directories) {
            comboBox.addItem(dir.getAbsolutePath());
        }

        // Default selection = target's parent folder
        File parentDir = target.getParentFile();
        comboBox.setSelectedItem(parentDir.getAbsolutePath());

        // Show dialog
        int result = JOptionPane.showConfirmDialog(
                prism,
                comboBox,
                "Move \"" + target.getName() + "\" to:",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        File selectedDir = new File((String) comboBox.getSelectedItem());
        if (!selectedDir.exists() || !selectedDir.isDirectory()) {
            JOptionPane.showMessageDialog(prism,
                    "Invalid destination directory.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedDir.equals(parentDir)) {
            // Same directory — no move needed
            return;
        }

        File newFile = new File(selectedDir, target.getName());
        if (newFile.exists()) {
            JOptionPane.showMessageDialog(prism,
                    "A file or folder with that name already exists in the destination.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = target.renameTo(newFile);
        if (!success) {
            JOptionPane.showMessageDialog(prism,
                    "Failed to move the file/folder.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            refreshNode(parentDir);
            refreshNode(selectedDir);
        }
    }

    public static void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    private void collectDirectories(File parent, java.util.List<File> directories) {
        if (parent == null || !parent.isDirectory()) {
            return;
        }

        directories.add(parent);

        File[] children = parent.listFiles(File::isDirectory);
        if (children != null) {
            for (File child : children) {
                collectDirectories(child, directories);
            }
        }
    }

    private boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] contents = file.listFiles();
            if (contents != null) {
                for (File child : contents) {
                    if (!deleteRecursively(child)) {
                        return false;
                    }
                }
            }
        }

        return file.delete();
    }

    private void showRenameDialog(File target) {
        String newName = JOptionPane.showInputDialog(
                prism,
                "Enter a new name:",
                target.getName()
        );

        if (newName == null || newName.trim().isEmpty()) {
            return;
        }

        File parent = target.getParentFile();
        File newFile = new File(parent, newName.trim());

        if (newFile.exists()) {
            JOptionPane.showMessageDialog(prism,
                    "A file or folder with that name already exists.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = target.renameTo(newFile);
        if (success) {
            refreshNode(parent);
        } else {
            JOptionPane.showMessageDialog(prism,
                    "Failed to rename.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showFilePropertiesDialog(File file) {
        Path path = file.toPath();
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            return;
        }

        BasicFileAttributes attrs = null;
        try {
            attrs = Files.readAttributes(path, BasicFileAttributes.class);
        } catch (IOException e) {
            System.err.println("Could not read file attributes: " + e.getMessage());
        }

        String humanReadableSize = formatFileSize(file.length());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
        String creationTime = (attrs != null)
                ? dateFormat.format(new Date(attrs.creationTime().toMillis()))
                : "N/A";
        String lastModifiedTime = (attrs != null)
                ? dateFormat.format(new Date(attrs.lastModifiedTime().toMillis()))
                : dateFormat.format(new Date(file.lastModified())); // Fallback to old API

        StringBuilder message = new StringBuilder();
        message.append("Name: ").append(file.getName()).append("\n");
        message.append("Path: ").append(file.getAbsolutePath()).append("\n");
        message.append("").append("\n"); // Separator for better readability
        message.append("Size: ").append(humanReadableSize).append(" (").append(file.length()).append(" bytes)").append("\n");

        message.append("Created: ").append(creationTime).append("\n");
        message.append("Modified: ").append(lastModifiedTime).append("\n");
        message.append("").append("\n");

        message.append("Readable: ").append(Files.isReadable(path) ? "Yes" : "No").append("\n");
        message.append("Writable: ").append(Files.isWritable(path) ? "Yes" : "No").append("\n");
        message.append("Executable: ").append(Files.isExecutable(path) ? "Yes" : "No").append("\n");

        JOptionPane.showMessageDialog(
                prism,
                message.toString(),
                "Properties for: " + file.getName(),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String formatFileSize(long bytes) {
        if (bytes <= 0) {
            return "0 bytes";
        }
        final String[] units = new String[]{"bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(bytes / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void refreshNode(File directory) {
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

        DefaultMutableTreeNode node = findNode(root, directory);
        if (node != null) {
            loadChildren(node);
            model.nodeStructureChanged(node);
        } else {
            refresh();
        }
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode parent, File target) {
        File nodeFile = (File) parent.getUserObject();
        if (nodeFile.equals(target)) {
            return parent;
        }

        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
            DefaultMutableTreeNode result = findNode(child, target);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void setRootDirectory(File newRootDirectory) {
        this.rootDirectory = newRootDirectory;
        refresh();
    }

    // Get the currently selected file
    public File getSelectedFile() {
        TreePath selectionPath = getSelectionPath();
        if (selectionPath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
            if (node.getUserObject() instanceof File) {
                return (File) node.getUserObject();
            }
        }
        return null;
    }

    public Icon getSystemFileIcon(File file) {
        return fsv.getSystemIcon(file);
    }

    public Icon getFileIcon(File file) {
        if (prism.config.getBoolean(Config.Key.FILE_EXPLORER_USE_SYSTEM_ICONS, true)) {
            return getSystemFileIcon(file);
        }

        if (!Languages.isSupported(file)) {
            return getSystemFileIcon(file);
        }

        return Languages.getIcon(file);
    }

    public Icon getFolderIcon(boolean expanded) {
        return ResourceUtil.getIcon(!expanded ? "icons/folder.png" : "icons/folder_open.png");
    }
}
