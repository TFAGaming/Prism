package com.prism.components.files;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JTree;
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
        setFocusable(false);

        setCellRenderer(new FileTreeCellRenderer());

        addTreeExpansionListener(new FileTreeExpansionListener());
        addTreeSelectionListener(new FileTreeSelectionListener());

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

    private class FileTreeSelectionListener implements TreeSelectionListener {

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            if (node != null && node.getUserObject() instanceof File) {
                File selectedFile = (File) node.getUserObject();

                if (selectedFile.isFile()) {
                    FileManager.openFile(selectedFile);
                } else {
                    loadChildren(node);
                }
            }
        }
    }

    public void refresh() {
        // Save expanded paths
        TreePath[] expandedPaths = getExpandedPaths();

        // Remember the selected path too
        TreePath selectedPath = getSelectionPath();

        // Rebuild model
        setModel(createTreeModel(rootDirectory));

        // Restore expanded paths
        if (expandedPaths != null) {
            for (TreePath path : expandedPaths) {
                expandPathIfExists(path);
            }
        }

        // Restore selection if possible
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
        if (oldPath == null) return;

        Object[] oldComponents = oldPath.getPath();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();

        java.util.List<Object> newPathList = new java.util.ArrayList<>();
        newPathList.add(root);

        DefaultMutableTreeNode currentNode = root;

        // Rebuild path by matching File objects
        for (int i = 1; i < oldComponents.length; i++) {
            DefaultMutableTreeNode oldNode = (DefaultMutableTreeNode) oldComponents[i];
            File oldFile = (File) oldNode.getUserObject();

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

            if (!found) break;
        }

        TreePath newPath = new TreePath(newPathList.toArray());
        expandPath(newPath);
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

        if (name == null || name.trim().isEmpty()) return; // Cancel or blank

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
        if (target == null || !target.exists() || target.isDirectory()) return;
        showRenameDialog(target);
    }

    public void renameFolder(File target) {
        if (target == null || !target.exists() || !target.isDirectory()) return;
        showRenameDialog(target);
    }

    private void showRenameDialog(File target) {
        String newName = JOptionPane.showInputDialog(
                prism,
                "Enter a new name:",
                target.getName()
        );

        if (newName == null || newName.trim().isEmpty()) return;

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
            if (result != null) return result;
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
        if (!Languages.isSupported(file)) {
            return getSystemFileIcon(file);
        }

        return Languages.getIcon(file);
    }

    public Icon getFolderIcon(boolean expanded) {
        return ResourceUtil.getIcon(!expanded ? "icons/folder.png" : "icons/folder_open.png");
    }
}
