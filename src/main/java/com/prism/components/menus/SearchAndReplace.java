package com.prism.components.menus;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.prism.Prism;
import com.prism.components.extended.JExtendedTextField;
import com.prism.components.frames.ErrorDialog;
import com.prism.components.frames.WarningDialog;
import com.prism.utils.ResourceUtil;

public class SearchAndReplace extends JPanel {
    private final  JExtendedTextField searchField;
    private final  JExtendedTextField replaceField;
    private final  JCheckBox caseSensitiveCheck;
    private final  JCheckBox wholeWordCheck;
    private final  JCheckBox regexCheck;
    private final JCheckBox replaceCheck;
    private final JButton upButton;
    private final JButton downButton;
    private JButton replaceButton;
    private int currentMatchIndex = -1;
    private java.util.List<Integer> matchPositions;

    public SearchAndReplace() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(new EmptyBorder(5, 0, 0, 0));

        searchField = new JExtendedTextField(25);
        searchField.setPlaceholder("Search (↓↑ for history)");

        add(new JLabel("  "));
        add(searchField);

        // Options
        caseSensitiveCheck = new JCheckBox("Case Sensitive");
        caseSensitiveCheck.setFocusable(false);

        wholeWordCheck = new JCheckBox("Whole Word");
        wholeWordCheck.setFocusable(false);

        regexCheck = new JCheckBox("Regex");
        regexCheck.setFocusable(false);

        add(regexCheck);
        add(wholeWordCheck);
        add(caseSensitiveCheck);

        // Replace panel
        replaceCheck = new JCheckBox();
        replaceCheck.setFocusable(false);
        replaceCheck.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                boolean isEnabled = e.getStateChange() == ItemEvent.SELECTED;

                replaceField.setEnabled(isEnabled);
                replaceButton.setEnabled(isEnabled);
            }
        });

        replaceField = new JExtendedTextField(15);
        replaceField.setPlaceholder("Replace with...");
        replaceField.setEnabled(false);

        add(replaceCheck);
        add(replaceField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        upButton = createButton(ResourceUtil.getIcon("icons/up.png"), "Up");
        downButton = createButton(ResourceUtil.getIcon("icons/down.png"), "Down");
        replaceButton = createButton(ResourceUtil.getIcon("icons/check_mark.gif"), "Replace");
        replaceButton.setEnabled(false);

        buttonPanel.add(upButton);
        buttonPanel.add(downButton);
        buttonPanel.add(replaceButton);

        add(buttonPanel);

        upButton.addActionListener((e) -> findNextMatch(true));
        downButton.addActionListener((e) -> findNextMatch(false));
        replaceButton.addActionListener((e) -> replaceMatch());
        searchField.addActionListener((e) -> findMatches());
    }

    private JButton createButton(ImageIcon buttonIcon, String tooltip) {
        JButton button = new JButton();

        if (tooltip != null) {
            button.setToolTipText(tooltip);
        }

        button.setPreferredSize(new Dimension(20, 20));

        Image scaledImage = buttonIcon.getImage().getScaledInstance(16, 16, Image.SCALE_FAST);
        button.setIcon(new ImageIcon(scaledImage));

        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void findMatches() {
        com.prism.components.textarea.TextArea textArea = Prism.getInstance().textAreaTabbedPane.getCurrentFile().getTextArea();

        String searchText = searchField.getText();

        if (searchText.length() == 0) {
            JOptionPane.showMessageDialog(this, "You cannot search an empty string!", "Warning", JOptionPane.WARNING_MESSAGE);

            return;
        }

        String text = textArea.getText();
        matchPositions = new java.util.ArrayList<>();
        currentMatchIndex = -1;

        if (searchText.isEmpty()) {
            return;
        }

        int flags = caseSensitiveCheck.isSelected() ? 0 : Pattern.CASE_INSENSITIVE;
        Pattern pattern;

        if (regexCheck.isSelected()) {
            pattern = Pattern.compile(searchText, flags);
        } else {
            String escapedText = Pattern.quote(searchText);
            if (wholeWordCheck.isSelected()) {
                escapedText = "\\b" + escapedText + "\\b";
            }
            pattern = Pattern.compile(escapedText, flags);
        }

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            matchPositions.add(matcher.start());
        }

        if (!matchPositions.isEmpty()) {
            currentMatchIndex = 0;
            highlightMatch();
        }
    }

    private void findNextMatch(boolean isUp) {
        if (matchPositions == null || matchPositions.isEmpty()) {
            return;
        }

        if (isUp) {
            currentMatchIndex = (currentMatchIndex - 1 + matchPositions.size()) % matchPositions.size();
        } else {
            currentMatchIndex = (currentMatchIndex + 1) % matchPositions.size();
        }

        highlightMatch();
    }

    private void replaceMatch() {
        com.prism.components.textarea.TextArea textArea = Prism.getInstance().textAreaTabbedPane.getCurrentFile().getTextArea();

        if (currentMatchIndex == -1 || matchPositions.isEmpty()) {
            return;
        }

        int position = matchPositions.get(currentMatchIndex);
        String searchText = searchField.getText();
        String replaceText = replaceField.getText();
        
        try {
            int matchEnd = position + (regexCheck.isSelected() ? getMatchLength(position) : searchText.length());
            textArea.replaceRange(replaceText, position, matchEnd);

            findMatches();
        } catch (Exception ex) {
            WarningDialog.showWarningDialog(Prism.getInstance(), ex);
        }
    }

    private int getMatchLength(int position) {
        com.prism.components.textarea.TextArea textArea = Prism.getInstance().textAreaTabbedPane.getCurrentFile().getTextArea();

        String searchText = searchField.getText();
        String text = textArea.getText().substring(position);
        Pattern pattern = Pattern.compile(searchText, caseSensitiveCheck.isSelected() ? 0 : Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.end();
        }
        return searchText.length();
    }

    private void highlightMatch() {
        com.prism.components.textarea.TextArea textArea = Prism.getInstance().textAreaTabbedPane.getCurrentFile().getTextArea();

        if (currentMatchIndex == -1 || matchPositions.isEmpty()) {
            return;
        }

        int position = matchPositions.get(currentMatchIndex);

        textArea.requestFocus();
        textArea.select(position, position + searchField.getText().length());
    }
}