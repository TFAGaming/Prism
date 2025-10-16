package com.prism.components.menus;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import com.prism.Prism;
import com.prism.components.extended.JExtendedTextField;
import com.prism.components.frames.WarningDialog;
import com.prism.utils.ResourceUtil;

public class SearchAndReplace extends JPanel {
    public Prism prism = Prism.getInstance();

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

    // --- History Fields ---
    private final List<String> searchHistory = new ArrayList<>();
    private int historyIndex = -1;
    // ----------------------

    public SearchAndReplace() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBorder(new EmptyBorder(5, 0, 0, 0));

        searchField = new JExtendedTextField(25);
        searchField.setPlaceholder("Search (↓↑ for history)");

        add(new JLabel("  "));
        add(searchField);

        // Options
        caseSensitiveCheck = new JCheckBox("Case Sensitive");
        caseSensitiveCheck.setFocusable(true);

        wholeWordCheck = new JCheckBox("Whole Word");
        wholeWordCheck.setFocusable(true);

        regexCheck = new JCheckBox("Regex");
        regexCheck.setFocusable(true);

        add(regexCheck);
        add(wholeWordCheck);
        add(caseSensitiveCheck);

        // Replace panel
        replaceCheck = new JCheckBox();
        replaceCheck.setFocusable(true);
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

        // --- Setup history navigation for the search field ---
        setupSearchHistory();
        // -----------------------------------------------------
    }
    
    // --- New method to setup key bindings for history ---
    private void setupSearchHistory() {
        InputMap inputMap = searchField.getInputMap(WHEN_FOCUSED);
        ActionMap actionMap = searchField.getActionMap();

        // Key Binding for UP arrow (Previous search term)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "historyUp");
        actionMap.put("historyUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateHistory(true); // true for 'Up' (backwards in history)
            }
        });

        // Key Binding for DOWN arrow (Next search term)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "historyDown");
        actionMap.put("historyDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateHistory(false); // false for 'Down' (forwards in history)
            }
        });
    }

    private void navigateHistory(boolean isUp) {
        if (searchHistory.isEmpty()) {
            return;
        }

        if (historyIndex == -1) {
            // Start history from the last search term if the field is empty or user is navigating history for the first time
            historyIndex = searchHistory.size() - 1;
        } else {
            if (isUp) {
                // Move backwards (up in the history list)
                historyIndex = (historyIndex - 1 + searchHistory.size()) % searchHistory.size();
            } else {
                // Move forwards (down in the history list)
                historyIndex = (historyIndex + 1) % searchHistory.size();
            }
        }

        // Display the selected history item
        searchField.setText(searchHistory.get(historyIndex));
    }
    // --------------------------------------------------

    private JButton createButton(ImageIcon buttonIcon, String tooltip) {
        JButton button = new JButton();

        if (tooltip != null) {
            button.setToolTipText(tooltip);
        }

        button.setPreferredSize(new Dimension(20, 20));

        Image scaledImage = buttonIcon.getImage().getScaledInstance(16, 16, Image.SCALE_FAST);
        button.setIcon(new ImageIcon(scaledImage));

        button.setFocusPainted(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void findMatches() {
        com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

        String searchText = searchField.getText();

        if (searchText.length() == 0) {
            return;
        }

        // --- History Logic: Add unique, non-empty search text to history ---
        if (!searchText.trim().isEmpty() && !searchHistory.contains(searchText)) {
            searchHistory.add(searchText);
            // After a new search, reset history index so next UP starts from the newest entry
            historyIndex = -1;
        }
        // -------------------------------------------------------------------

        String text = textArea.getText();
        matchPositions = new java.util.ArrayList<>();
        currentMatchIndex = -1;

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
        com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

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
            WarningDialog.showWarningDialog(prism, ex);
        }
    }

    private int getMatchLength(int position) {
        com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

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
        com.prism.components.textarea.TextArea textArea = prism.textAreaTabbedPane.getCurrentFile().getTextArea();

        if (currentMatchIndex == -1 || matchPositions.isEmpty()) {
            return;
        }

        int position = matchPositions.get(currentMatchIndex);

        textArea.requestFocus();
        textArea.select(position, position + searchField.getText().length());
    }
}