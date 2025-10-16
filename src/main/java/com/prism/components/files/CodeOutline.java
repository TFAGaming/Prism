package com.prism.components.files;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;

import com.prism.Prism;
import com.prism.config.Config;
import com.prism.utils.ResourceUtil;

public class CodeOutline extends JTree {

    public RSyntaxTextArea textArea = null;

    public CodeOutline() {
        super(new DefaultTreeModel(new DefaultMutableTreeNode("Outline")));

        setRootVisible(true);
        setShowsRootHandles(true);
        setFocusable(true);

        setCellRenderer(new CodeOutlineTreeRenderer());

        this.setRootVisible(false);

        addTreeNavigationListener();
    }

    public void setSyntaxTextArea(RSyntaxTextArea textArea) {
        this.textArea = textArea;
        updateTree();
    }

    public void updateTree() {
        if (textArea == null) {
            return;
        }

        FoldManager fm = textArea.getFoldManager();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Outline");
        DefaultTreeModel model = new DefaultTreeModel(root);

        int topLevelFoldCount = fm.getFoldCount();
        for (int i = 0; i < topLevelFoldCount; i++) {
            Fold fold = fm.getFold(i);

            DefaultMutableTreeNode node = createNodeFromFold(fold);

            if (node == null) {
                continue;
            }

            root.add(node);
        }

        this.setModel(model);

        for (int i = 0; i < this.getRowCount(); i++) {
            this.expandRow(i);
        }
    }

    private DefaultMutableTreeNode createNodeFromFold(Fold fold) {
        String title = getFoldTitle(fold);

        if (Prism.getInstance().config.getBoolean(Config.Key.CODE_OUTLINE_IGNORE_COMMENTS, true) && (title.startsWith("//") || title.startsWith("/*") || title.startsWith("/**"))) {
            return null;
        }

        FoldWrapper wrapper = new FoldWrapper(title, fold);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(wrapper);

        int childCount = fold.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Fold childFold = fold.getChild(i);

            DefaultMutableTreeNode childNode = createNodeFromFold(childFold);

            if (childNode != null) {
                node.add(childNode);
            }
        }

        return node;
    }

    private String getFoldTitle(Fold fold) {
        String title = "";

        try {
            int startLine = fold.getStartLine();
            int lineStartOffset = textArea.getLineStartOffset(startLine);
            int lineEndOffset = textArea.getLineEndOffset(startLine);

            int length = lineEndOffset - lineStartOffset;

            if (length > 0) {
                title = textArea.getText(lineStartOffset, length);
            }

        } catch (BadLocationException e) {
        }

        title = title.trim();

        if (title.isEmpty()) {
            return "<line " + (fold.getStartLine() + 1) + ">";
        }

        if (title.length() > 50) {
            title = title.substring(0, 47) + "...";
        }

        return title;
    }

    private void addTreeNavigationListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // if (e.getClickCount() == 2) {
                TreePath selPath = getPathForLocation(e.getX(), e.getY());
                if (selPath != null) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();

                    Object userObj = selectedNode.getUserObject();
                    if (userObj instanceof FoldWrapper) {
                        Fold fold = ((FoldWrapper) userObj).getFold();

                        try {
                            if (fold.isCollapsed()) {
                                fold.setCollapsed(false);
                            }

                            int offset = textArea.getLineStartOffset(fold.getStartLine());

                            textArea.setCaretPosition(offset);

                            textArea.requestFocusInWindow();
                        } catch (Exception ex) {

                        }
                    }
                }
                // }
            }
        });
    }

    private static class CodeOutlineTreeRenderer extends DefaultTreeCellRenderer {

        private final Icon mainParentIcon = ResourceUtil.getIcon("icons/outline2.gif");
        private final Icon nestedParentIcon = ResourceUtil.getIcon("icons/outline3.gif");
        private final Icon leafNodeIcon = ResourceUtil.getIcon("icons/outline1.gif");

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();

                if (userObject instanceof FoldWrapper) {
                    Fold fold = ((FoldWrapper) userObject).getFold();

                    int depth = node.getLevel();

                    if (depth == 1) {
                        setIcon(mainParentIcon);
                    } else if (fold.getChildCount() > 0) {
                        setIcon(nestedParentIcon);
                    } else {
                        setIcon(leafNodeIcon);
                    }
                } else {
                    setIcon(null);
                }
            }
            return this;
        }
    }

    private static class FoldWrapper {

        private final String title;
        private final Fold fold;

        public FoldWrapper(String title, Fold fold) {
            this.title = title;
            this.fold = fold;
        }

        public Fold getFold() {
            return fold;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
