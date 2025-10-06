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

import com.prism.managers.FileManager;
import com.prism.utils.Languages;
import com.prism.utils.ResourceUtil;

public class FileExplorer extends JTree {
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
        setModel(createTreeModel(rootDirectory));
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
