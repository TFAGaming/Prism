package com.prism.components.frames;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.prism.Prism;
import com.prism.config.Config;
import com.prism.utils.ResourceUtil;

public class ConfigurationDialog extends JFrame {
    public Prism prism = Prism.getInstance();

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

        setResizable(false);

        setIconImage(ResourceUtil.getIcon("icons/Prism.png").getImage());

        contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
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
        DefaultMutableTreeNode autocompleteNode = new DefaultMutableTreeNode("Autocomplete");
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
        editorNode.add(autocompleteNode);
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
        AutocompletePanel autocompletePanel = new AutocompletePanel();
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
        rightPanel.add(autocompletePanel, "Autocomplete");
        rightPanel.add(cPanel, "C");
        rightPanel.add(cppPanel, "C++");
        rightPanel.add(javaPanel, "Java");
        // Add similar for Python, JavaScript...

        contentPane.add(rightPanel, BorderLayout.CENTER);
    }

    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        okButton = new JButton("OK");
        okButton.setFocusable(false);
        okButton.setPreferredSize(new Dimension(80, 25));
        cancelButton = new JButton("Cancel");
        cancelButton.setFocusable(false);
        cancelButton.setPreferredSize(new Dimension(80, 25));

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);

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

    private JPanel newJPanelLeftLayout(JComponent... components) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        if (!(components[components.length - 1] == null)) {
            panel.setBorder(new EmptyBorder(5, 5, 0, 0));
        }

        for (JComponent component : components) {
            if (component == null) {
                continue;
            }

            component.setAlignmentX(Component.LEFT_ALIGNMENT);
            component.setMaximumSize(component.getPreferredSize());
            component.setFocusable(false);

            panel.add(component);
            panel.add(Box.createRigidArea(new Dimension(5, 0)));
        }

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    // General Configuration Panel
    private class GeneralPanel extends JPanel {

        public GeneralPanel() {
            initializeUI();
        }

        private void initializeUI() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(5, 5, 5, 5));

            // 1
            JCheckBox checkBox1 = new JCheckBox("Check for Updates");
            checkBox1.setFocusable(false);
            checkBox1.setSelected(prism.config.getBoolean(Config.Key.CHECK_FOR_UPDATES, false));
            checkBox1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.CHECK_FOR_UPDATES, checkBox1.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox1));

            // 2
            JCheckBox checkBox2 = new JCheckBox("Warn before opening files larger than (MB): ");
            JSpinner maxFileSize = new JSpinner(
                    new SpinnerNumberModel(prism.config.getInt(Config.Key.MAX_FILE_SIZE_FOR_WARNING, 10), 1, 500, 1));
            checkBox2.setFocusable(false);
            checkBox2.setSelected(prism.config.getBoolean(Config.Key.WARN_BEFORE_OPENING_LARGE_FILES, true));
            maxFileSize.setFocusable(false);
            maxFileSize.setEnabled(prism.config.getBoolean(Config.Key.WARN_BEFORE_OPENING_LARGE_FILES, true));
            checkBox2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.WARN_BEFORE_OPENING_LARGE_FILES, checkBox2.isSelected());

                    maxFileSize.setEnabled(checkBox2.isSelected());
                }
            });
            maxFileSize.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    prism.config.set(Config.Key.MAX_FILE_SIZE_FOR_WARNING, (int) maxFileSize.getValue());
                }
            });

            add(newJPanelLeftLayout(checkBox2, maxFileSize));

            // 3
            JCheckBox checkBox3 = new JCheckBox("Use system icons for File explorer");
            checkBox3.setFocusable(false);
            checkBox3.setSelected(prism.config.getBoolean(Config.Key.FILE_EXPLORER_USE_SYSTEM_ICONS, true));
            checkBox3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.FILE_EXPLORER_USE_SYSTEM_ICONS, checkBox3.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox3));
        }
    }

    private class EditorPanel extends JPanel {

        public EditorPanel() {
            initializeUI();
        }

        private void initializeUI() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(5, 5, 5, 5));

            // 0
            JCheckBox checkBox = new JCheckBox("Show line numbers");
            checkBox.setFocusable(false);
            checkBox.setSelected(prism.config.getBoolean(Config.Key.SHOW_LINE_NUMBERS, true));
            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.SHOW_LINE_NUMBERS, checkBox.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox));

            // 0_1
            JCheckBox checkBox0_1 = new JCheckBox("Open recent files on startup");
            checkBox0_1.setFocusable(false);
            checkBox0_1.setSelected(prism.config.getBoolean(Config.Key.OPEN_RECENT_FILES, true));
            checkBox0_1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.OPEN_RECENT_FILES, checkBox0_1.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox0_1));

            // 1
            JCheckBox checkBox1 = new JCheckBox("Anti-Aliasing");
            checkBox1.setFocusable(false);
            checkBox1.setSelected(prism.config.getBoolean(Config.Key.ANTI_ALIASING_ENABLED, true));
            checkBox1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.ANTI_ALIASING_ENABLED, checkBox1.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox1));

            // 2
            JCheckBox checkBox2 = new JCheckBox("Auto-Indent, Tab size: ");
            JSpinner autoIndentTabSize = new JSpinner(
                    new SpinnerNumberModel(prism.config.getInt(Config.Key.TAB_SIZE, 4), 1, 8, 1));
            checkBox2.setFocusable(false);
            checkBox2.setSelected(prism.config.getBoolean(Config.Key.AUTO_INDENT_ENABLED, true));
            autoIndentTabSize.setFocusable(false);
            autoIndentTabSize.setEnabled(prism.config.getBoolean(Config.Key.AUTO_INDENT_ENABLED, true));
            checkBox2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.AUTO_INDENT_ENABLED, checkBox2.isSelected());

                    autoIndentTabSize.setEnabled(checkBox2.isSelected());
                }
            });
            autoIndentTabSize.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    prism.config.set(Config.Key.TAB_SIZE, (int) autoIndentTabSize.getValue());
                }
            });

            add(newJPanelLeftLayout(checkBox2, autoIndentTabSize));

            // 3
            JCheckBox checkBox3 = new JCheckBox("Close curly braces");
            checkBox3.setFocusable(false);
            checkBox3.setSelected(prism.config.getBoolean(Config.Key.CLOSE_CURLY_BRACES, true));
            checkBox3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.CLOSE_CURLY_BRACES, checkBox3.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox3));

            // 4
            JCheckBox checkBox4 = new JCheckBox("Close markup tags");
            checkBox4.setFocusable(false);
            checkBox4.setSelected(prism.config.getBoolean(Config.Key.CLOSE_MARKUP_TAGS, true));
            checkBox4.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.CLOSE_MARKUP_TAGS, checkBox4.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox4));

            // 5
            JCheckBox checkBox5 = new JCheckBox("Bookmarks");
            checkBox5.setFocusable(false);
            checkBox5.setSelected(prism.config.getBoolean(Config.Key.BOOK_MARKS, true));
            checkBox5.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.BOOK_MARKS, checkBox5.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox5));

            // 6
            JCheckBox checkBox6 = new JCheckBox("Bracket matching");
            checkBox6.setFocusable(false);
            checkBox6.setSelected(prism.config.getBoolean(Config.Key.BRACKET_MATCHING_ENABLED, true));
            checkBox6.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.BRACKET_MATCHING_ENABLED, checkBox6.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox6));

            // 7
            JCheckBox checkBox7 = new JCheckBox("Mark occurences");
            checkBox7.setFocusable(false);
            checkBox7.setSelected(prism.config.getBoolean(Config.Key.MARK_OCCURRENCES, true));
            checkBox7.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.MARK_OCCURRENCES, checkBox7.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox7));

            // 8
            JCheckBox checkBox8 = new JCheckBox("Fade current line highlight");
            checkBox8.setFocusable(false);
            checkBox8.setSelected(prism.config.getBoolean(Config.Key.FADE_CURRENT_LINE_HIGHLIGHT, true));
            checkBox8.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.FADE_CURRENT_LINE_HIGHLIGHT, checkBox8.isSelected());
                }
            });

            // 9
            JCheckBox checkBox9 = new JCheckBox("Highlight current line");
            checkBox9.setFocusable(false);
            checkBox9.setSelected(prism.config.getBoolean(Config.Key.HIGHLIGHT_CURRENT_LINE, true));
            checkBox9.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.HIGHLIGHT_CURRENT_LINE, checkBox9.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox9));

            // 10
            JCheckBox checkBox10 = new JCheckBox("Word wrap");
            checkBox10.setFocusable(false);
            checkBox10.setSelected(prism.config.getBoolean(Config.Key.WORD_WRAP_ENABLED, false));
            checkBox10.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.WORD_WRAP_ENABLED, checkBox10.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox10));

            // 11
            JCheckBox checkBox11 = new JCheckBox("Word wrap style");
            checkBox11.setFocusable(false);
            checkBox11.setSelected(prism.config.getBoolean(Config.Key.WORD_WRAP_STYLE_WORD, true));
            checkBox11.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.WORD_WRAP_STYLE_WORD, checkBox11.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox11));

            // 12
            JCheckBox checkBox12 = new JCheckBox("Code folding");
            checkBox12.setFocusable(false);
            checkBox12.setSelected(prism.config.getBoolean(Config.Key.CODE_FOLDING_ENABLED, true));
            checkBox12.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.CODE_FOLDING_ENABLED, checkBox12.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox12));

            // 13
            JCheckBox checkBox13 = new JCheckBox("Show matched bracket popup");
            checkBox13.setFocusable(false);
            checkBox13.setSelected(prism.config.getBoolean(Config.Key.SHOW_MATCHED_BRACKET_POPUP, true));
            checkBox13.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.SHOW_MATCHED_BRACKET_POPUP, checkBox13.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox13));
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
            setBorder(new EmptyBorder(5, 5, 5, 5));

            JPanel mainPanel = new JPanel(new GridLayout(5, 1, 5, 5));

            // Interface language
            JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            languagePanel.add(new JLabel("Interface Language:"));
            languageCombo = new JComboBox<>(new String[] { "English", "Spanish", "French", "German", "Japanese" });
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
            JComboBox<String> regionalCombo = new JComboBox<>(
                    new String[] { "System Default", "US", "UK", "European", "Asian" });
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
        private Map<String, Config.Key> tokenKeyMap;
        private JComboBox<String> tokenComboBox;
        private JButton colorButton;
        private com.prism.components.textarea.TextArea previewTextArea;

        public SyntaxHighlightingPanel() {
            initializeTokenMap();
            initializeUI();
        }

        private void initializeTokenMap() {
            tokenKeyMap = new LinkedHashMap<>();
            tokenKeyMap.put("Annotation", Config.Key.ANNOTATION);
            tokenKeyMap.put("Reserved Word", Config.Key.RESERVED_WORD);
            tokenKeyMap.put("String Double Quote", Config.Key.STRING_DOUBLE_QUOTE);
            tokenKeyMap.put("Character", Config.Key.CHARACTER);
            tokenKeyMap.put("Backquote", Config.Key.BACKQUOTE);
            tokenKeyMap.put("Boolean", Config.Key.BOOLEAN);
            tokenKeyMap.put("Number Integer/Decimal", Config.Key.NUMBER_INTEGER_DECIMAL);
            tokenKeyMap.put("Number Float", Config.Key.NUMBER_FLOAT);
            tokenKeyMap.put("Number Hexadecimal", Config.Key.NUMBER_HEXADECIMAL);
            tokenKeyMap.put("Regular Expression", Config.Key.REGULAR_EXPRESSION);
            tokenKeyMap.put("Multi-line Comment", Config.Key.MULTI_LINE_COMMENT);
            tokenKeyMap.put("Documentation Comment", Config.Key.DOCUMENTATION_COMMENT);
            tokenKeyMap.put("EOL Comment", Config.Key.EOL_COMMENT);
            tokenKeyMap.put("Seperator", Config.Key.SEPERATOR);
            tokenKeyMap.put("Operator", Config.Key.OPERATOR);
            tokenKeyMap.put("Identifier", Config.Key.IDENTIFIER);
            tokenKeyMap.put("Variable", Config.Key.VARIABLE);
            tokenKeyMap.put("Function", Config.Key.FUNCTION);
            tokenKeyMap.put("Preprocessor", Config.Key.PREPROCESSOR);
            tokenKeyMap.put("Markup CData", Config.Key.MARKUP_CDATA);
            tokenKeyMap.put("Markup Comment", Config.Key.MARKUP_COMMENT);
            tokenKeyMap.put("Markup DTD", Config.Key.MARKUP_DTD);
            tokenKeyMap.put("Markup Tag Attribute", Config.Key.MARKUP_TAG_ATTRIBUTE);
            tokenKeyMap.put("Markup Tag Attribute Value", Config.Key.MARKUP_TAG_ATTRIBUTE_VALUE);
            tokenKeyMap.put("Markup Tag Delimiter", Config.Key.MARKUP_TAG_DELIMITER);
            tokenKeyMap.put("Markup Tag Name", Config.Key.MARKUP_TAG_NAME);
        }

        private void initializeUI() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(5, 5, 5, 5));

            String[] tokens = tokenKeyMap.keySet().toArray(new String[0]);

            tokenComboBox = new JComboBox<>(tokens);
            colorButton = new JButton(getDefaultColor(Config.Key.ANNOTATION));

            updateColorButton();

            tokenComboBox.addActionListener(e -> updateColorButton());
            colorButton.addActionListener(e -> chooseColor());

            colorButton.setOpaque(true);
            colorButton.setForeground(Color.decode(getDefaultColor(Config.Key.ANNOTATION)));

            add(newJPanelLeftLayout(new JLabel("Token: "), tokenComboBox));
            add(newJPanelLeftLayout(new JLabel("Color: "), colorButton));

            add(newJPanelLeftLayout(new JLabel("Preview Text Area:")));

            previewTextArea = new com.prism.components.textarea.TextArea();
            previewTextArea.setEditable(false);
            previewTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
            previewTextArea.addSyntaxHighlighting();
            previewTextArea.setText("""
                    package com.example.tokens;

                    import java.io.Serializable;
                    import java.util.List;
                    import static java.lang.Math.PI;

                    public class TokenExample implements Serializable {

                        private static final int MAX_VALUE = 100;
                        protected String name = "Java";
                        public volatile boolean active = true;

                        public TokenExample() {
                            super();
                        }

                        public static void main(String[] args) {
                            int x = 10;
                            double y = 20.5;
                            char grade = 'A';
                            boolean isValid = true;
                            String message = "Hello, World!";

                            if (x < y && isValid) {
                                System.out.println(message + " The value of PI is: " + PI);
                            } else {
                                for (int i = 0; i < 5; i++) {
                                    switch (i) {
                                        case 0:
                                            System.out.println("Case zero");
                                            break;
                                        case 1:
                                            System.out.println("Case one");
                                            break;
                                        default:
                                            System.out.println("Other case");
                                    }
                                }
                            }

                            try {
                                List<Integer> numbers = null;
                                int first = numbers.get(0); // This will throw NullPointerException
                            } catch (NullPointerException e) {
                                System.err.println("Caught an exception: " + e.getMessage());
                            } finally {
                                System.out.println("Finally block executed.");
                            }

                            long bigNumber = 1234567890123L;
                            float smallFloat = 3.14f;
                            short s = 50;
                            byte b = 127;
                        }

                        public final void processData(int data) throws IllegalArgumentException {
                            if (data > MAX_VALUE) {
                                throw new IllegalArgumentException("Data exceeds max value.");
                            }
                            synchronized (this) {
                                // Some synchronized operation
                            }
                        }

                        enum Status {
                            PENDING, COMPLETE, FAILED
                        }
                    }
                                        """.trim());

            add(newJPanelLeftLayout(new RTextScrollPane(previewTextArea)));
        }

        private void updateColorButton() {
            String selectedToken = (String) tokenComboBox.getSelectedItem();
            Config.Key configKey = tokenKeyMap.get(selectedToken);

            if (configKey != null) {
                String hexColor = prism.config.getString(configKey, getDefaultColor(configKey));
                Color color = hexToColor(hexColor);

                colorButton.setForeground(color);
                colorButton.setText(hexColor);

                if (previewTextArea != null) {
                    previewTextArea.addSyntaxHighlighting();
                }
            }
        }

        private String getDefaultColor(Config.Key configKey) {
            switch (configKey) {
                case RESERVED_WORD:
                    return "#990099";
                case STRING_DOUBLE_QUOTE:
                case CHARACTER:
                case BACKQUOTE:
                    return "#009933";
                case BOOLEAN:
                    return "#3300FF";
                case NUMBER_INTEGER_DECIMAL:
                case NUMBER_FLOAT:
                case NUMBER_HEXADECIMAL:
                    return "#FF6633";
                case REGULAR_EXPRESSION:
                    return "#DF1D1D";
                case MULTI_LINE_COMMENT:
                case DOCUMENTATION_COMMENT:
                case EOL_COMMENT:
                    return "#999999";
                case FUNCTION:
                    return "#006666";
                default:
                    return "#000000";
            }
        }

        private void chooseColor() {
            String selectedToken = (String) tokenComboBox.getSelectedItem();
            Config.Key configKey = tokenKeyMap.get(selectedToken);

            if (configKey != null) {
                Color currentColor = colorButton.getForeground();
                Color selectedColor = JColorChooser.showDialog(
                        prism,
                        "Choose Color for " + selectedToken,
                        currentColor);

                if (selectedColor != null) {
                    String hexColor = colorToHex(selectedColor);
                    prism.config.set(configKey, hexColor);

                    colorButton.setForeground(selectedColor);
                    colorButton.setText(hexColor);

                    if (previewTextArea != null) {
                        previewTextArea.addSyntaxHighlighting();
                    }
                }
            }
        }

        private String colorToHex(Color color) {
            return String.format("#%02x%02x%02x",
                    color.getRed(),
                    color.getGreen(),
                    color.getBlue()).toUpperCase();
        }

        private Color hexToColor(String hex) {
            try {
                return Color.decode(hex);
            } catch (NumberFormatException e) {
                return Color.WHITE;
            }
        }
    }

    // General Configuration Panel
    private class AutocompletePanel extends JPanel {

        public AutocompletePanel() {
            initializeUI();
        }

        private void initializeUI() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(5, 5, 5, 5));

            // 1
            JCheckBox checkBox1 = new JCheckBox("Enable Autocomplete");
            checkBox1.setFocusable(false);
            checkBox1.setSelected(prism.config.getBoolean(Config.Key.AUTOCOMPLETE_ENABLED, true));
            checkBox1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.AUTOCOMPLETE_ENABLED, checkBox1.isSelected());
                }
            });

            add(newJPanelLeftLayout(checkBox1));

            // 2
            JCheckBox checkBox2 = new JCheckBox("Autocomplete automatic popup menu");
            checkBox2.setFocusable(false);
            checkBox2.setSelected(prism.config.getBoolean(Config.Key.AUTOCOMPLETE_AUTO_POPUP_ENABLED, true));
            checkBox2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    prism.config.set(Config.Key.AUTOCOMPLETE_AUTO_POPUP_ENABLED, checkBox2.isSelected());
                }
            });
            add(newJPanelLeftLayout(checkBox2));
        }
    }

    private class CPanel extends JPanel {

        public CPanel() {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(5, 5, 5, 5));

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
            setBorder(new EmptyBorder(5, 5, 5, 5));

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
            setBorder(new EmptyBorder(5, 5, 5, 5));

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
