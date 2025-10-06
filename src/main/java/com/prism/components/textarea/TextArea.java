package com.prism.components.textarea;

import java.awt.Font;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.prism.Prism;
import com.prism.config.Config;

public class TextArea extends RSyntaxTextArea {

    public TextArea() {
        super();

        Prism prism = Prism.getInstance();

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
