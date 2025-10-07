package com.prism.components.frames;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import com.prism.components.extended.JExtendedTextField;
import com.prism.managers.FileManager;

public class ConfigurationDialog extends JFrame {

    private JPanel contentPane;
    private JTree configTree;
    private JPanel rightPanel;
    private CardLayout cardLayout;

    // Configuration panels
    private GeneralPanel generalPanel;
    private EditorPanel editorPanel;
    private LanguagePanel languagePanel;

    // Buttons
    private JButton okButton;
    private JButton cancelButton;
    private JButton applyButton;

    public ConfigurationDialog() {
        initializeUI();
        setupTreeSelectionListener();

        setVisible(true);
    }

    private void initializeUI() {
        setTitle("Configuration Settings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        createLeftPanel();
        createRightPanel();
        createBottomPanel();
    }

    private void createLeftPanel() {
        // Create tree nodes
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Configuration");
        DefaultMutableTreeNode generalNode = new DefaultMutableTreeNode("General");
        DefaultMutableTreeNode editorNode = new DefaultMutableTreeNode("Editor");
        DefaultMutableTreeNode languageNode = new DefaultMutableTreeNode("Language");

        // Add children to Editor node
        DefaultMutableTreeNode syntaxHighlightingNode = new DefaultMutableTreeNode("Syntax Highlighting");
        DefaultMutableTreeNode languagesNode = new DefaultMutableTreeNode("Languages");

        // Add children to Languages node
        DefaultMutableTreeNode cNode = new DefaultMutableTreeNode("C");
        DefaultMutableTreeNode cppNode = new DefaultMutableTreeNode("C++");
        DefaultMutableTreeNode javaNode = new DefaultMutableTreeNode("Java");
        DefaultMutableTreeNode pythonNode = new DefaultMutableTreeNode("Python");
        DefaultMutableTreeNode javascriptNode = new DefaultMutableTreeNode("JavaScript");

        // Build the tree hierarchy
        languagesNode.add(cNode);
        languagesNode.add(cppNode);
        languagesNode.add(javaNode);
        languagesNode.add(pythonNode);
        languagesNode.add(javascriptNode);

        editorNode.add(syntaxHighlightingNode);
        editorNode.add(languagesNode);

        root.add(generalNode);
        root.add(editorNode);
        root.add(languageNode);

        // Create tree
        configTree = new JTree(root);
        configTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        configTree.setCellRenderer(new NullIconTreeCellRenderer());
        configTree.setRootVisible(true);
        configTree.setShowsRootHandles(true);
        configTree.setFocusable(false);

        // Expand all nodes
        for (int i = 0; i < configTree.getRowCount(); i++) {
            configTree.expandRow(i);
        }

        JScrollPane treeScrollPane = new JScrollPane(configTree);
        treeScrollPane.setPreferredSize(new Dimension(200, 0));
        treeScrollPane.setBorder(BorderFactory.createTitledBorder("Categories"));

        contentPane.add(treeScrollPane, BorderLayout.WEST);
    }

    private void createRightPanel() {
        rightPanel = new JPanel();
        cardLayout = new CardLayout();
        rightPanel.setLayout(cardLayout);
        rightPanel.setBorder(BorderFactory.createTitledBorder("General"));

        // Create all configuration panels
        generalPanel = new GeneralPanel();
        editorPanel = new EditorPanel();
        languagePanel = new LanguagePanel();

        // New panels for editor children
        SyntaxHighlightingPanel syntaxHighlightingPanel = new SyntaxHighlightingPanel();
        CPanel cPanel = new CPanel();
        CppPanel cppPanel = new CppPanel();
        JavaPanel javaPanel = new JavaPanel();
        // Add similar for Python, JavaScript...

        // Add all panels to card layout with their names
        rightPanel.add(generalPanel, "General");
        rightPanel.add(editorPanel, "Editor");
        rightPanel.add(languagePanel, "Language");

        // Add the new panels
        rightPanel.add(syntaxHighlightingPanel, "Syntax Highlighting");
        rightPanel.add(cPanel, "C");
        rightPanel.add(cppPanel, "C++");
        rightPanel.add(javaPanel, "Java");
        // Add similar for Python, JavaScript...

        contentPane.add(rightPanel, BorderLayout.CENTER);
    }

    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        applyButton = new JButton("Apply");

        // Add action listeners
        okButton.addActionListener(new ButtonListener());
        cancelButton.addActionListener(new ButtonListener());
        applyButton.addActionListener(new ButtonListener());

        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
        bottomPanel.add(applyButton);

        contentPane.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupTreeSelectionListener() {
        configTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) configTree.getLastSelectedPathComponent();

            if (node == null) {
                return;
            }

            String nodeName = node.toString();
            cardLayout.show(rightPanel, nodeName);
        });
    }

    public class NullIconTreeCellRenderer extends DefaultTreeCellRenderer {

        public NullIconTreeCellRenderer() {
            setLeafIcon(null);
            setOpenIcon(null);
            setClosedIcon(null);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        }
    }

    // Button action listener
    private class ButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == okButton) {
                applyAllChanges();
                dispose();
            } else if (e.getSource() == cancelButton) {
                dispose();
            } else if (e.getSource() == applyButton) {
                applyAllChanges();
            }
        }

        private void applyAllChanges() {
            Component[] components = rightPanel.getComponents();
            for (Component comp : components) {
                if (comp.isVisible()) {
                    if (comp instanceof GeneralPanel) {
                        ((GeneralPanel) comp).applyChanges();
                    } else if (comp instanceof EditorPanel) {
                        ((EditorPanel) comp).applyChanges();
                    } else if (comp instanceof SyntaxHighlightingPanel) {
                        ((SyntaxHighlightingPanel) comp).applyChanges();
                    }
                    break;
                }
            }
        }
    }

    // General Configuration Panel
    private class GeneralPanel extends JPanel {

        private JExtendedTextField defaultDirPath;
        private JButton defaultDirPathBrowse;
        private JComboBox defaultShell;
        private JCheckBox warnWhenOpeningLargeFile;
        private JSpinner maxFileSize;

        public GeneralPanel() {
            initializeUI();
        }

        private void initializeUI() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel mainPanel = new JPanel(new GridLayout(10, 1, 0, 0));

            // Default directory
            JPanel defaultDirtPathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            defaultDirPath = new JExtendedTextField(30);
            defaultDirPath.setText(FileManager.getDirectory().getAbsolutePath());
            defaultDirPath.setEnabled(false);
            defaultDirPathBrowse = new JButton("Browse...");
            defaultDirPathBrowse.setFocusable(false);

            defaultDirtPathPanel.add(new JLabel("Default directory:"));
            defaultDirtPathPanel.add(defaultDirPath);
            defaultDirtPathPanel.add(defaultDirPathBrowse);

            // Default shell
            JPanel defaultShellPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            String[] terminalShells = {"Command Prompt", "PowerShell"};
            defaultShell = new JComboBox<>(terminalShells);
            defaultShell.setFocusable(false);

            Dimension preferredSize = defaultShell.getPreferredSize();
            preferredSize.width += 20;
            defaultShell.setPreferredSize(preferredSize);

            defaultShellPanel.add(new JLabel("Default console shell: "));
            defaultShellPanel.add(defaultShell);

            // Max File Size
            JPanel warnWhenOpeningLargeFilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            warnWhenOpeningLargeFile = new JCheckBox("Warn when opening file size over (MB): ");
            warnWhenOpeningLargeFile.setFocusable(false);
            maxFileSize = new JSpinner(new SpinnerNumberModel(10, 1, 500, 1));

            preferredSize = maxFileSize.getPreferredSize();
            preferredSize.width += 20;
            maxFileSize.setPreferredSize(preferredSize);

            warnWhenOpeningLargeFilePanel.add(warnWhenOpeningLargeFile);   
            warnWhenOpeningLargeFilePanel.add(maxFileSize);  

            // End
            mainPanel.add(defaultDirtPathPanel);
            mainPanel.add(defaultShellPanel);
            mainPanel.add(warnWhenOpeningLargeFilePanel);

            add(mainPanel, BorderLayout.NORTH);

            defaultDirPathBrowse.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int result = chooser.showOpenDialog(GeneralPanel.this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    defaultDirPath.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            });
        }

        public void applyChanges() {
            System.out.println("Applying General settings:");
            
        }
    }

    private class EditorPanel extends JPanel {

        private JCheckBox lineNumbersCheckBox;
        private JCheckBox wordWrapCheckBox;
        private JSpinner tabSizeSpinner;
        private JComboBox<String> fontCombo;
        private JSpinner fontSizeSpinner;

        public EditorPanel() {
            initializeUI();
        }

        private void initializeUI() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel mainPanel = new JPanel(new GridLayout(6, 1, 5, 5));

            // Line numbers
            JPanel lineNumbersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            lineNumbersCheckBox = new JCheckBox("Show line numbers");
            lineNumbersPanel.add(lineNumbersCheckBox);

            // Word wrap
            JPanel wordWrapPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            wordWrapCheckBox = new JCheckBox("Enable word wrap");
            wordWrapPanel.add(wordWrapCheckBox);

            // Tab size
            JPanel tabSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            tabSizePanel.add(new JLabel("Tab size:"));
            tabSizeSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 8, 1));
            tabSizePanel.add(tabSizeSpinner);

            // Font family
            JPanel fontPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            fontPanel.add(new JLabel("Font:"));
            fontCombo = new JComboBox<>(new String[]{"Monospaced", "Consolas", "Courier New", "Arial"});
            fontPanel.add(fontCombo);

            // Font size
            JPanel fontSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            fontSizePanel.add(new JLabel("Font size:"));
            fontSizeSpinner = new JSpinner(new SpinnerNumberModel(12, 8, 24, 1));
            fontSizePanel.add(fontSizeSpinner);

            // Syntax highlighting
            JPanel syntaxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            syntaxPanel.add(new JLabel("Syntax theme:"));
            JComboBox<String> syntaxCombo = new JComboBox<>(new String[]{"Default", "Dark", "Solarized", "Monokai"});
            syntaxPanel.add(syntaxCombo);

            mainPanel.add(lineNumbersPanel);
            mainPanel.add(wordWrapPanel);
            mainPanel.add(tabSizePanel);
            mainPanel.add(fontPanel);
            mainPanel.add(fontSizePanel);
            mainPanel.add(syntaxPanel);

            add(mainPanel, BorderLayout.NORTH);
        }

        public void applyChanges() {
            System.out.println("Applying Editor settings:");
            System.out.println("Line numbers: " + lineNumbersCheckBox.isSelected());
            System.out.println("Word wrap: " + wordWrapCheckBox.isSelected());
            System.out.println("Tab size: " + tabSizeSpinner.getValue());
            System.out.println("Font: " + fontCombo.getSelectedItem());
        }
    }

    private class LanguagePanel extends JPanel {

        private JComboBox<String> languageCombo;
        private JCheckBox spellCheckCheckBox;
        private JCheckBox autoCompleteCheckBox;
        private JRadioButton usEnglishRadio;
        private JRadioButton ukEnglishRadio;

        public LanguagePanel() {
            initializeUI();
        }

        private void initializeUI() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel mainPanel = new JPanel(new GridLayout(5, 1, 5, 5));

            // Interface language
            JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            languagePanel.add(new JLabel("Interface Language:"));
            languageCombo = new JComboBox<>(new String[]{"English", "Spanish", "French", "German", "Japanese"});
            languagePanel.add(languageCombo);

            // Spell check
            JPanel spellCheckPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            spellCheckCheckBox = new JCheckBox("Enable spell checking");
            spellCheckPanel.add(spellCheckCheckBox);

            // Auto-complete
            JPanel autoCompletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            autoCompleteCheckBox = new JCheckBox("Enable auto-completion");
            autoCompletePanel.add(autoCompleteCheckBox);

            // English variant
            JPanel englishVariantPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            englishVariantPanel.add(new JLabel("English Variant:"));
            ButtonGroup englishGroup = new ButtonGroup();
            usEnglishRadio = new JRadioButton("US English", true);
            ukEnglishRadio = new JRadioButton("UK English");
            englishGroup.add(usEnglishRadio);
            englishGroup.add(ukEnglishRadio);
            englishVariantPanel.add(usEnglishRadio);
            englishVariantPanel.add(ukEnglishRadio);

            // Regional format
            JPanel regionalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            regionalPanel.add(new JLabel("Regional Format:"));
            JComboBox<String> regionalCombo = new JComboBox<>(new String[]{"System Default", "US", "UK", "European", "Asian"});
            regionalPanel.add(regionalCombo);

            mainPanel.add(languagePanel);
            mainPanel.add(spellCheckPanel);
            mainPanel.add(autoCompletePanel);
            mainPanel.add(englishVariantPanel);
            mainPanel.add(regionalPanel);

            add(mainPanel, BorderLayout.NORTH);
        }

        public void applyChanges() {
            System.out.println("Applying Language settings:");
            System.out.println("Language: " + languageCombo.getSelectedItem());
            System.out.println("Spell check: " + spellCheckCheckBox.isSelected());
            System.out.println("Auto-complete: " + autoCompleteCheckBox.isSelected());
            System.out.println("English variant: " + (usEnglishRadio.isSelected() ? "US" : "UK"));
        }
    }

    // Syntax Highlighting Panel
    private class SyntaxHighlightingPanel extends JPanel {

        private JComboBox<String> fontCombo;
        private JSpinner fontSizeSpinner;
        private JButton fontBrowseButton;
        private JButton bgColorButton;
        private JButton fgColorButton;
        private JComboBox<String> elementCombo;
        private JButton elementFontButton;
        private JTextArea previewArea;
        private JColorChooser colorChooser;

        public SyntaxHighlightingPanel() {
            initializeUI();
        }

        private void initializeUI() {
            setLayout(new BorderLayout(10, 10));
            setBorder(new EmptyBorder(10, 10, 10, 10));

            // Create main panels
            JPanel settingsPanel = createSettingsPanel();
            JPanel previewPanel = createPreviewPanel();

            // Add panels to main layout
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, settingsPanel, previewPanel);
            splitPane.setResizeWeight(0.6);
            splitPane.setDividerLocation(0.6);

            add(splitPane, BorderLayout.CENTER);
        }

        private JPanel createSettingsPanel() {
            JPanel panel = new JPanel(new BorderLayout(5, 5));

            // Description label
            JLabel descLabel = new JLabel("<html>You can fine-tune the fonts and colors used in the editor here, but consider using the User Interface panel before starting customizations.</html>");
            descLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
            panel.add(descLabel, BorderLayout.NORTH);

            // Main settings
            JPanel mainSettings = new JPanel(new GridLayout(0, 1, 5, 5));

            // Global font settings
            JPanel globalFontPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            globalFontPanel.add(new JLabel("Font:"));
            fontCombo = new JComboBox<>(new String[]{"Consolas", "Monospaced", "Courier New", "Source Code Pro", "Fira Code"});
            fontCombo.setSelectedItem("Consolas");
            fontSizeSpinner = new JSpinner(new SpinnerNumberModel(13, 8, 24, 1));
            fontBrowseButton = new JButton("Browse...");

            globalFontPanel.add(fontCombo);
            globalFontPanel.add(fontSizeSpinner);
            globalFontPanel.add(fontBrowseButton);

            // Background color
            JPanel bgColorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bgColorPanel.add(new JLabel("Background:"));
            bgColorButton = new JButton("r=255,g=255,b=255");
            bgColorButton.setBackground(Color.WHITE);
            bgColorButton.setOpaque(true);
            bgColorButton.setBorderPainted(true);
            bgColorPanel.add(bgColorButton);

            // Fonts and Colors section
            JLabel fontsColorsLabel = new JLabel("Fonts and Colors:");
            fontsColorsLabel.setFont(fontsColorsLabel.getFont().deriveFont(Font.BOLD));

            // Element-specific settings
            JPanel elementPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            elementPanel.add(new JLabel("Font:"));
            elementCombo = new JComboBox<>(new String[]{
                "End-of-line Comment",
                "Multiline Comment",
                "Documentation Comment",
                "Comment Keyword",
                "Comment Markup",
                "Keyword",
                "Keyword 2",
                "Function"
            });
            elementFontButton = new JButton("Consolas 13");
            elementFontButton.setBackground(Color.WHITE);
            elementFontButton.setOpaque(true);
            elementFontButton.setBorderPainted(true);

            elementPanel.add(elementCombo);
            elementPanel.add(elementFontButton);

            // Add action listeners
            //fontBrowseButton.addActionListener(e -> browseFont());
            bgColorButton.addActionListener(e -> chooseBackgroundColor());
            elementFontButton.addActionListener(e -> chooseElementFont());
            elementCombo.addActionListener(e -> updateElementButton());

            // Add all components to main settings
            mainSettings.add(globalFontPanel);
            mainSettings.add(bgColorPanel);
            mainSettings.add(fontsColorsLabel);
            mainSettings.add(elementPanel);

            panel.add(mainSettings, BorderLayout.CENTER);
            return panel;
        }

        private JPanel createPreviewPanel() {
            JPanel panel = new JPanel(new BorderLayout(5, 5));

            // Preview label
            JLabel previewLabel = new JLabel("Preview:");
            previewLabel.setFont(previewLabel.getFont().deriveFont(Font.BOLD));

            // Sample text label
            JLabel sampleLabel = new JLabel("Sample Text: Java");

            // Preview text area
            previewArea = new JTextArea();
            previewArea.setText(
                    "/**\n"
                    + " * This is about <code>ClassName</code>.\n"
                    + " * @author author\n"
                    + " */\n"
                    + "public class ClassName<E> implements InterfaceName<String> {\n"
                    + "    enum Color { RED, GREEN, BLUE };\n"
                    + "    /* This comment may span multiple lines. */\n"
                    + "    static Object staticField;\n"
                    + "    // This comment may span only this line\n"
                    + "}"
            );
            previewArea.setFont(new Font("Consolas", Font.PLAIN, 13));
            previewArea.setBackground(Color.WHITE);
            previewArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(previewArea);
            scrollPane.setPreferredSize(new Dimension(300, 200));

            // Layout for labels
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            labelPanel.add(previewLabel);
            labelPanel.add(Box.createHorizontalStrut(20));
            labelPanel.add(sampleLabel);

            panel.add(labelPanel, BorderLayout.NORTH);
            panel.add(scrollPane, BorderLayout.CENTER);

            return panel;
        }

        private void chooseBackgroundColor() {
            Color color = JColorChooser.showDialog(this, "Choose Background Color", bgColorButton.getBackground());
            if (color != null) {
                bgColorButton.setBackground(color);
                bgColorButton.setText(String.format("r=%d,g=%d,b=%d", color.getRed(), color.getGreen(), color.getBlue()));
                previewArea.setBackground(color);
            }
        }

        private void chooseElementFont() {
            // For simplicity, using color chooser for element font color
            Color color = JColorChooser.showDialog(this, "Choose Element Color", elementFontButton.getBackground());
            if (color != null) {
                elementFontButton.setBackground(color);
                elementFontButton.setForeground(getContrastColor(color));
                updatePreviewStyling();
            }
        }

        private void updateElementButton() {
            // Update button text based on selected element
            String element = (String) elementCombo.getSelectedItem();
            elementFontButton.setText("Consolas 13");
            // You could load saved settings for each element here
        }

        private void updatePreviewStyling() {
            // This would apply the actual syntax highlighting to the preview
            // For now, we just update the global font and background
            String fontName = (String) fontCombo.getSelectedItem();
            int fontSize = (Integer) fontSizeSpinner.getValue();
            previewArea.setFont(new Font(fontName, Font.PLAIN, fontSize));
            previewArea.setBackground(bgColorButton.getBackground());
        }

        private Color getContrastColor(Color color) {
            // Calculate contrasting color for text
            double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
            return luminance > 0.5 ? Color.BLACK : Color.WHITE;
        }

        public void applyChanges() {
            System.out.println("Applying Syntax Highlighting settings:");
            System.out.println("Font: " + fontCombo.getSelectedItem() + " " + fontSizeSpinner.getValue());
            System.out.println("Background: " + bgColorButton.getText());
            // Save individual element settings here
        }
    }

    private class CPanel extends JPanel {

        public CPanel() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel mainPanel = new JPanel(new GridLayout(3, 1, 5, 5));

            JCheckBox c11Standard = new JCheckBox("Enable C11 standard");
            JCheckBox showWarnings = new JCheckBox("Show all warnings");
            JSpinner indentSpinner = new JSpinner(new SpinnerNumberModel(4, 2, 8, 1));

            JPanel indentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            indentPanel.add(new JLabel("Indentation:"));
            indentPanel.add(indentSpinner);

            mainPanel.add(c11Standard);
            mainPanel.add(showWarnings);
            mainPanel.add(indentPanel);

            add(mainPanel, BorderLayout.NORTH);
        }

        public void applyChanges() {
            System.out.println("Applying C language settings");
        }
    }

    private class CppPanel extends JPanel {

        public CppPanel() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel mainPanel = new JPanel(new GridLayout(4, 1, 5, 5));

            JCheckBox cpp17Standard = new JCheckBox("Enable C++17 standard");
            JCheckBox cpp20Standard = new JCheckBox("Enable C++20 features");
            JCheckBox smartPointers = new JCheckBox("Smart pointer highlighting");
            JCheckBox templateHighlighting = new JCheckBox("Enhanced template highlighting");

            mainPanel.add(cpp17Standard);
            mainPanel.add(cpp20Standard);
            mainPanel.add(smartPointers);
            mainPanel.add(templateHighlighting);

            add(mainPanel, BorderLayout.NORTH);
        }

        public void applyChanges() {
            System.out.println("Applying C++ language settings");
        }
    }

    private class JavaPanel extends JPanel {

        public JavaPanel() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 10, 10, 10));

            JPanel mainPanel = new JPanel(new GridLayout(3, 1, 5, 5));

            JCheckBox springSupport = new JCheckBox("Spring Framework support");
            JCheckBox lombokSupport = new JCheckBox("Lombok support");
            JCheckBox annotationProcessing = new JCheckBox("Annotation processing");

            mainPanel.add(springSupport);
            mainPanel.add(lombokSupport);
            mainPanel.add(annotationProcessing);

            add(mainPanel, BorderLayout.NORTH);
        }

        public void applyChanges() {
            System.out.println("Applying Java language settings");
        }
    }
}
