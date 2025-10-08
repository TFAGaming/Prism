package com.prism.components.files;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.prism.utils.ResourceUtil;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CodeOutlineTree extends JTree {

    public RSyntaxTextArea textArea = null;

    public CodeOutlineTree() {
        // Initialize the JTree with a default model, but defer the tree population
        super(new DefaultTreeModel(new DefaultMutableTreeNode("Outline")));

        setRootVisible(true);
        setShowsRootHandles(true);
        setFocusable(false);

        setCellRenderer(new CodeOutlineTreeRenderer());

        this.setRootVisible(false); // Hide the artificial "Outline" root node

        // Add listeners for navigation immediately
        addTreeNavigationListener();
    }

    /**
     * Sets the RSyntaxTextArea instance to be used for fold data and
     * navigation. This also triggers an immediate update of the tree structure.
     *
     * @param textArea The RSyntaxTextArea instance.
     */
    public void setSyntaxTextArea(RSyntaxTextArea textArea) {
        this.textArea = textArea;
        updateTree();
    }

    /**
     * Clears the current tree and rebuilds it based on the current folds in the
     * attached RSyntaxTextArea.
     */
    public void updateTree() {
        if (textArea == null) {
            // Cannot update if the editor component hasn't been set yet.
            System.err.println("Cannot update tree: RSyntaxTextArea is not attached.");
            return;
        }

        FoldManager fm = textArea.getFoldManager();

        // The root node of our JTree outline
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Outline");
        DefaultTreeModel model = new DefaultTreeModel(root);

        // Iterate over all top-level folds (index 0 to count-1)
        int topLevelFoldCount = fm.getFoldCount();
        for (int i = 0; i < topLevelFoldCount; i++) {
            Fold fold = fm.getFold(i);

            // Add the top-level fold node
            DefaultMutableTreeNode node = createNodeFromFold(fold);
            root.add(node);
        }

        this.setModel(model);

        // Expand all nodes by default for a complete view
        for (int i = 0; i < this.getRowCount(); i++) {
            this.expandRow(i);
        }
    }

    /**
     * Recursively creates DefaultMutableTreeNode structure from a Fold
     * hierarchy. Uses fold.getChildCount() and fold.getChild(i) for nested
     * folds.
     *
     * * @param fold The current Fold object.
     * @return A DefaultMutableTreeNode representing the fold.
     */
    private DefaultMutableTreeNode createNodeFromFold(Fold fold) {
        // The tree node will display the fold's header text and store the Fold object itself.
        String title = getFoldTitle(fold);
        FoldWrapper wrapper = new FoldWrapper(title, fold);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(wrapper);

        // Recursively process child folds using the correct iteration API
        int childCount = fold.getChildCount();
        for (int i = 0; i < childCount; i++) {
            Fold childFold = fold.getChild(i);
            if (childFold.getHasChildFolds()) {
                node.add(createNodeFromFold(childFold));
            }
        }

        return node;
    }

    /**
     * Generates a readable title for the fold in the JTree.
     */
    private String getFoldTitle(Fold fold) {
        // RSyntaxTextArea often sets the fold text (e.g., "public class MyClass {")
        String title = "";

        try {
            int startLine = fold.getStartLine();
            // Get the offsets for the entire starting line
            int lineStartOffset = textArea.getLineStartOffset(startLine);
            int lineEndOffset = textArea.getLineEndOffset(startLine);

            int length = lineEndOffset - lineStartOffset;

            if (length > 0) {
                // Extract the text for the line where the fold starts
                title = textArea.getText(lineStartOffset, length);
            }

        } catch (BadLocationException e) {
            // If text cannot be retrieved (e.g., offsets are invalid)
            System.err.println("Error extracting text for fold title: " + e.getMessage());
        }

        // Clean up the title (remove leading/trailing whitespace)
        title = title.trim();

        // Fallback: show line number if no specific fold text is set
        // Line numbers are 0-based in the model, so add 1 for display
        if (title.isEmpty()) {
            // Line numbers are 0-based in the model, so add 1 for display
            return "Line " + (fold.getStartLine() + 1);
        }

        // Truncate long titles for better tree visibility
        if (title.length() > 50) {
            title = title.substring(0, 47) + "...";
        }

        return title;
    }

    /**
     * Adds a MouseListener to handle double-clicks on the tree nodes to
     * navigate the editor.
     */
    private void addTreeNavigationListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Only act on double-clicks
                if (e.getClickCount() == 2) {
                    TreePath selPath = getPathForLocation(e.getX(), e.getY());
                    if (selPath != null) {
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();

                        // Check if the node's user object is a Fold
                        Object userObj = selectedNode.getUserObject();
                        if (userObj instanceof FoldWrapper) {
                            Fold fold = ((FoldWrapper) userObj).getFold();

                            try {
                                // 1. Ensure the fold is expanded (if it was collapsed)
                                if (fold.isCollapsed()) {
                                    fold.setCollapsed(false);
                                }

                                // 2. Calculate the offset (start of the fold's line)
                                int offset = textArea.getLineStartOffset(fold.getStartLine());

                                // 3. Set the caret position and scroll to it
                                textArea.setCaretPosition(offset);

                                // Optional: Request focus back to the editor
                                textArea.requestFocusInWindow();
                            } catch (Exception ex) {
                                System.err.println("Error navigating to fold: " + ex.getMessage());
                                // Just print the error, don't use alert() or confirm()
                            }
                        }
                    }
                }
            }
        });
    }

    private static class CodeOutlineTreeRenderer extends DefaultTreeCellRenderer {

        private final Icon mainParentIcon = ResourceUtil.getIcon("icons/outline2.gif");
        private final Icon nestedParentIcon = ResourceUtil.getIcon("icons/outline3.gif");
        private final Icon leafNodeIcon = ResourceUtil.getIcon("icons/outline3.gif");

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {

            // Call the super method first to handle default rendering (text, selection)
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            // Logic only applies to nodes that are actually DefaultMutableTreeNode and wrap a FoldWrapper
            if (value instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object userObject = node.getUserObject();

                if (userObject instanceof FoldWrapper) {
                    Fold fold = ((FoldWrapper) userObject).getFold();

                    // Check node depth. Since root is hidden, the top-level folds are at level 1.
                    int depth = node.getLevel();

                    if (depth == 1) {
                        // 1. Icon for the first main parent (top-level class or method)
                        setIcon(mainParentIcon);
                    } else if (fold.getChildCount() > 0) {
                        // 2. Icon for each parent inside the main one (nested folds that have children)
                        // Note: We use the same icon for open/closed state on the tree's expansion state,
                        // but the icon itself is based on its folding structure (having children).
                        setIcon(nestedParentIcon);
                    } else {
                        // 3. Icon for the rest of the children (folds that have no children / are leaf folds)
                        setIcon(leafNodeIcon);
                    }
                } else {
                    // Fallback for the root node if it was visible
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
