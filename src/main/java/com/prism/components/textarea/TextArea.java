package com.prism.components.textarea;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

import javax.swing.JList;
import javax.swing.text.Document;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;

import com.prism.Prism;
import com.prism.config.Config;
import com.prism.managers.AutocompleteManager;
import com.prism.utils.Languages;

public class TextArea extends RSyntaxTextArea {

    public Prism prism = Prism.getInstance();

    public TextArea() {
        super();

        setAnimateBracketMatching(false);
        setShowMatchedBracketPopup(prism.config.getBoolean(Config.Key.SHOW_MATCHED_BRACKET_POPUP, true));
        setCodeFoldingEnabled(prism.config.getBoolean(Config.Key.CODE_FOLDING_ENABLED, true));
        setAntiAliasingEnabled(prism.config.getBoolean(Config.Key.ANTI_ALIASING_ENABLED, true));
        setTabSize(prism.config.getInt(Config.Key.TAB_SIZE, 4));
        setTabsEmulated(true);
        setAutoIndentEnabled(prism.config.getBoolean(Config.Key.AUTO_INDENT_ENABLED, true));
        setCloseCurlyBraces(prism.config.getBoolean(Config.Key.CLOSE_CURLY_BRACES, true));
        setCloseMarkupTags(prism.config.getBoolean(Config.Key.CLOSE_MARKUP_TAGS, true));
        setBracketMatchingEnabled(prism.config.getBoolean(Config.Key.BRACKET_MATCHING_ENABLED, true));
        setMarkOccurrences(prism.config.getBoolean(Config.Key.MARK_OCCURRENCES, true));
        setFadeCurrentLineHighlight(prism.config.getBoolean(Config.Key.FADE_CURRENT_LINE_HIGHLIGHT, true));
        setHighlightCurrentLine(prism.config.getBoolean(Config.Key.HIGHLIGHT_CURRENT_LINE, true));
        setLineWrap(prism.config.getBoolean(Config.Key.WORD_WRAP_ENABLED, false));
        setWrapStyleWord(prism.config.getBoolean(Config.Key.WORD_WRAP_STYLE_WORD, true));

        Font font = getFont();

        setFont(font.deriveFont((float) prism.config.getInt(Config.Key.TEXTAREA_ZOOM, 12)));
    }

    public void addSyntaxHighlighting() {
        SyntaxScheme scheme = getSyntaxScheme();

        scheme.getStyle(Token.ANNOTATION).foreground = getConfigSyntaxHighlightingTokenColor(Config.Key.ANNOTATION);
        scheme.getStyle(Token.RESERVED_WORD).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.RESERVED_WORD, "#990099");
        scheme.getStyle(Token.RESERVED_WORD_2).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.RESERVED_WORD, "#990099");

        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.STRING_DOUBLE_QUOTE, "#009933");
        scheme.getStyle(Token.LITERAL_CHAR).foreground = getConfigSyntaxHighlightingTokenColor(Config.Key.CHARACTER, "#009933");
        scheme.getStyle(Token.LITERAL_BACKQUOTE).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.BACKQUOTE, "#009933");

        scheme.getStyle(Token.LITERAL_BOOLEAN).foreground = getConfigSyntaxHighlightingTokenColor(Config.Key.BOOLEAN, "#3300FF");

        scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.NUMBER_INTEGER_DECIMAL, "#FF6633");
        scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.NUMBER_FLOAT, "#FF6633");
        scheme.getStyle(Token.LITERAL_NUMBER_HEXADECIMAL).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.NUMBER_HEXADECIMAL, "#FF6633");

        scheme.getStyle(Token.REGEX).foreground = getConfigSyntaxHighlightingTokenColor(Config.Key.REGULAR_EXPRESSION, "#df1d1d");

        scheme.getStyle(Token.COMMENT_MULTILINE).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.MULTI_LINE_COMMENT, "#999999");
        scheme.getStyle(Token.COMMENT_DOCUMENTATION).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.DOCUMENTATION_COMMENT, "#999999");
        scheme.getStyle(Token.COMMENT_EOL).foreground = getConfigSyntaxHighlightingTokenColor(Config.Key.EOL_COMMENT, "#999999");

        scheme.getStyle(Token.SEPARATOR).foreground = getConfigSyntaxHighlightingTokenColor(Config.Key.SEPERATOR);
        scheme.getStyle(Token.OPERATOR).foreground = getConfigSyntaxHighlightingTokenColor(Config.Key.OPERATOR);
        scheme.getStyle(Token.IDENTIFIER).foreground = getConfigSyntaxHighlightingTokenColor(Config.Key.IDENTIFIER);
        scheme.getStyle(Token.VARIABLE).foreground = getConfigSyntaxHighlightingTokenColor(Config.Key.VARIABLE);
        scheme.getStyle(Token.FUNCTION).foreground = getConfigSyntaxHighlightingTokenColor(Config.Key.FUNCTION, "#006666");
        scheme.getStyle(Token.PREPROCESSOR).foreground = getConfigSyntaxHighlightingTokenColor(Config.Key.PREPROCESSOR);

        // HTML / XML related
        scheme.getStyle(Token.MARKUP_CDATA).foreground = getConfigSyntaxHighlightingTokenColor(Config.Key.MARKUP_CDATA);
        scheme.getStyle(Token.MARKUP_COMMENT).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.MARKUP_COMMENT);
        scheme.getStyle(Token.MARKUP_DTD).foreground = getConfigSyntaxHighlightingTokenColor(Config.Key.MARKUP_DTD);
        // scheme.getStyle(Token.MARKUP_ENTITY_REFERENCE).foreground = Color.BLUE;
        // scheme.getStyle(Token.MARKUP_PROCESSING_INSTRUCTION).foreground = Color.BLUE;
        scheme.getStyle(Token.MARKUP_TAG_ATTRIBUTE).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.MARKUP_TAG_ATTRIBUTE);
        scheme.getStyle(Token.MARKUP_TAG_ATTRIBUTE_VALUE).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.MARKUP_TAG_ATTRIBUTE_VALUE);
        scheme.getStyle(Token.MARKUP_TAG_DELIMITER).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.MARKUP_TAG_DELIMITER);
        scheme.getStyle(Token.MARKUP_TAG_NAME).foreground = getConfigSyntaxHighlightingTokenColor(
                Config.Key.MARKUP_TAG_NAME);

        setSyntaxScheme(scheme);
    }

    public void addAutocomplete(File file) {
        try {
            DefaultCompletionProvider provider = new DefaultCompletionProvider();

            if (prism.config.getBoolean(Config.Key.AUTOCOMPLETE_AUTO_POPUP_ENABLED, true)) {
                provider.setAutoActivationRules(true, ".");
            }

            AutocompleteManager.getBasic(provider, Languages.getFullName(file));

            AutocompleteManager.getShorthand(provider, Languages.getFullName(file));

            if (provider != null) {
                AutoCompletion ac = new AutoCompletion(provider);

                if (prism.config.getBoolean(Config.Key.AUTOCOMPLETE_AUTO_POPUP_ENABLED, true)) {
                    ac.setAutoActivationEnabled(true);
                    ac.setAutoActivationDelay(500);
                }

                ac.setListCellRenderer(new CompletionCellRenderer() {
                    @Override
                    protected void prepareForOtherCompletion(JList<?> list,
                            Completion c, int index, boolean selected, boolean hasFocus) {
                        super.prepareForOtherCompletion(list, c, index, selected, hasFocus);

                        setIcon(super.getIcon());
                    }
                });

                ac.install(this);
            }
        } catch (Exception e) {

        }
    }

    private Color getConfigSyntaxHighlightingTokenColor(Config.Key key) {
        return getConfigSyntaxHighlightingTokenColor(key, "#000000");
    }

    private Color getConfigSyntaxHighlightingTokenColor(Config.Key key, String defaultValue) {
        String hexColor = Prism.getInstance().config.getString(key, defaultValue);

        try {
            return Color.decode(hexColor);
        } catch (NumberFormatException e) {
            return Color.BLACK;
        }
    }

    public void setCursorOnLine(int line) {
        Document doc = getDocument();
        try {
            int lineStartOffset = doc.getDefaultRootElement().getElement(line).getStartOffset();
            setCaretPosition(lineStartOffset);
            requestFocusInWindow();
        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }
}
