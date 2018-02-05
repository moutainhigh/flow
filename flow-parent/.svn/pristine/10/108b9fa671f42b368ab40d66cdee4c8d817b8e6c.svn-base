package com.aspire.etl.flowdesinger;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

/**
 * 
 * @author wangcaiping
 * @since 20080808
 */
public class WcpCloseIcon implements Icon {
    private int x, y;

    private final int width, height;

    private final Color darkColor;

    private Color currentColor;

    private final BasicStroke stroke;

    public WcpCloseIcon() {
	width = 8;
	height = 8;
	darkColor = new Color(100, 150, 200);
	currentColor = darkColor;
	stroke = new BasicStroke(2.0f);
    }

    public int getIconHeight() {
	return height;
    }

    public int getIconWidth() {
	return width;
    }

    public void paintIcon(Component comp, Graphics g, int x, int y) {
	this.x = x;
	this.y = y;
	Graphics2D g2 = (Graphics2D) g;
	g2.setStroke(stroke);
	g2.setColor(currentColor);
	g2.drawLine(x, y, x + width, y + height);
	g2.drawLine(x, y + height, x + width, y);
    }

    public boolean contains(int _x, int _y) {
	if ((x <= _x) && (x + 10 >= _x) && (y <= _y) && (y + 10 >= _y)) {
	    return true;
	}
	return false;
    }

    public void setLightColor(boolean b) {
	if (b) {
	    this.currentColor = Color.RED;
	} else {
	    this.currentColor = darkColor;
	}
    }
}
