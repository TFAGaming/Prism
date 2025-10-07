package com.prism.components.extended;

import javax.swing.*;
import java.awt.*;

public class JGradientPanel extends JPanel {
    public enum Direction {
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP,
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        DIAGONAL_TOP_LEFT,
        DIAGONAL_BOTTOM_LEFT
    }
    
    private Color color1;
    private Color color2;
    private Direction direction;
    
    public JGradientPanel(Color color1, Color color2, Direction direction) {
        this.color1 = color1;
        this.color2 = color2;
        this.direction = direction;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        GradientPaint gradient = createGradient();
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        super.paintComponent(g);
    }
    
    private GradientPaint createGradient() {
        switch (direction) {
            case TOP_TO_BOTTOM:
                return new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                
            case BOTTOM_TO_TOP:
                return new GradientPaint(0, getHeight(), color1, 0, 0, color2);
                
            case LEFT_TO_RIGHT:
                return new GradientPaint(0, 0, color1, getWidth(), 0, color2);
                
            case RIGHT_TO_LEFT:
                return new GradientPaint(getWidth(), 0, color1, 0, 0, color2);
                
            case DIAGONAL_TOP_LEFT:
                return new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                
            case DIAGONAL_BOTTOM_LEFT:
                return new GradientPaint(0, getHeight(), color1, getWidth(), 0, color2);
                
            default:
                return new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        }
    }
    
    // Setters for dynamic changes
    public void setColors(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
        repaint();
    }
    
    public void setDirection(Direction direction) {
        this.direction = direction;
        repaint();
    }
}