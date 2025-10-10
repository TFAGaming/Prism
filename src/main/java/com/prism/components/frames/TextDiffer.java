package com.prism.components.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.ChangeDelta;
import com.github.difflib.patch.DeleteDelta;
import com.github.difflib.patch.InsertDelta;
import com.github.difflib.patch.Patch;
import com.prism.components.textarea.TextArea;
import com.prism.utils.Languages;
import com.prism.utils.ResourceUtil;

public class TextDiffer extends JFrame {

    private TextArea oldTextArea;
    private TextArea newTextArea;

    private final Highlighter.HighlightPainter removedPainter =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 190, 190)); // Light Red

    private final Highlighter.HighlightPainter addedPainter =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(190, 255, 190)); // Light Green
            
    private final Highlighter.HighlightPainter changedPainter =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 255, 150)); // Light Yellow

    public TextDiffer(File oldFile, String oldText, File file, String text) {
        setTitle("Text Comparison");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        setIconImage(ResourceUtil.getIcon("icons/Prism.png").getImage());
        
        oldTextArea = createTextArea(oldFile, oldText);
        newTextArea = createTextArea(file, text);
        
        RTextScrollPane oldScrollPane = new RTextScrollPane(oldTextArea);
        oldScrollPane.setBorder(new EmptyBorder(2, 2, 2, 2));

        RTextScrollPane newScrollPane = new RTextScrollPane(newTextArea);
        newScrollPane.setBorder(new EmptyBorder(2, 2, 2, 2));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(oldScrollPane, BorderLayout.CENTER);
        leftPanel.setBorder(BorderFactory.createTitledBorder("Original"));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(newScrollPane, BorderLayout.CENTER);
        rightPanel.setBorder(BorderFactory.createTitledBorder("New Changes"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(new EmptyBorder(0, 5, 0, 5));
        
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.5));
        
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(splitPane, BorderLayout.CENTER);
        
        compareAndHighlight(oldText, text);
        
        setVisible(true);
    }

    private TextArea createTextArea(File file, String text) {
        TextArea textArea = new TextArea();

        textArea.setText(text);

        textArea.setSyntaxEditingStyle(Languages.getHighlighter(file));
        textArea.addSyntaxHighlighting();
        textArea.setEditable(false);
        textArea.setHighlightCurrentLine(false);

        return textArea;
    }

    private void compareAndHighlight(String oldText, String newText) {
        oldTextArea.getHighlighter().removeAllHighlights();
        newTextArea.getHighlighter().removeAllHighlights();

        List<String> oldLines = Arrays.asList(oldText.split("\n"));
        List<String> newLines = Arrays.asList(newText.split("\n"));

        try {
            Patch<String> patch = DiffUtils.diff(oldLines, newLines);

            for (AbstractDelta<String> delta : patch.getDeltas()) {
                
                int originalStart = delta.getSource().getPosition();
                int originalSize = delta.getSource().getLines().size();
                
                int revisedStart = delta.getTarget().getPosition();
                int revisedSize = delta.getTarget().getLines().size();
                
                if (delta instanceof DeleteDelta) {
                    highlightLineRange(oldTextArea, originalStart, originalStart + originalSize - 1, removedPainter);
                } else if (delta instanceof InsertDelta) {
                    highlightLineRange(newTextArea, revisedStart, revisedStart + revisedSize - 1, addedPainter);
                } else if (delta instanceof ChangeDelta) {
                    highlightLineRange(oldTextArea, originalStart, originalStart + originalSize - 1, changedPainter);
                    highlightLineRange(newTextArea, revisedStart, revisedStart + revisedSize - 1, changedPainter);
                }
            }
        } catch (Exception e) {
            System.err.println("FATAL ERROR: Could not perform diff using java-diff-utils. " +
                               "Ensure the 'java-diff-utils' Maven dependency is configured. Error: " + e.getMessage());
        }
    }

    private void highlightLineRange(RSyntaxTextArea textArea, int startLineIndex, int endLineIndex, Highlighter.HighlightPainter painter) {
        try {
            int startOffset = textArea.getLineStartOffset(startLineIndex);
            int endOffset;
            
            if (endLineIndex + 1 < textArea.getLineCount()) {
                endOffset = textArea.getLineStartOffset(endLineIndex + 1);
            } else {
                endOffset = textArea.getDocument().getLength();
            }

            if (endOffset > startOffset) {
                textArea.getHighlighter().addHighlight(startOffset, endOffset, painter);
            }
        } catch (BadLocationException e) {
            System.err.println("Error highlighting line range: " + e.getMessage());
        }
    }
}
