package com.prism.components.extended;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.Document;

public class JExtendedTextField extends JTextField {
    private String placeholder;

    public JExtendedTextField() {
    }

    public JExtendedTextField(
            final Document pDoc,
            final String pText,
            final int pColumns) {
        super(pDoc, pText, pColumns);
    }

    public JExtendedTextField(final int pColumns) {
        super(pColumns);
    }

    public JExtendedTextField(final String pText) {
        super(pText);
    }

    public JExtendedTextField(final String pText, final int pColumns) {
        super(pText, pColumns);
    }

    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        if (placeholder == null || placeholder.length() == 0 || getText().length() > 0) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        g.drawString(placeholder, getInsets().left, pG.getFontMetrics()
                .getMaxAscent() + getInsets().top);
    }

    public void setPlaceholder(final String s) {
        placeholder = s;
    }
}
