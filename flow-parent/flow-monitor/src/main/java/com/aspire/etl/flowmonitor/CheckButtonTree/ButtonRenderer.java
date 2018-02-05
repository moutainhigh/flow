
/**
 * <p>Title: </p>
 * <p>Description:
 *
 * </p>
 * <p>Copyright: Copyright (c) 2000-2006 ASPire Technologies, Inc. </p>
 * <p>Company: ASPIRE</p>
 * @Create Author  luoqi
 * @Created Date:  2006年5月17日, 下午5:02
 * @Update Author
 * @Update Date
 * @version 1.0
 */
package com.aspire.etl.flowmonitor.CheckButtonTree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreeCellRenderer;


public class ButtonRenderer extends JPanel implements TreeCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected JButton button;
    
    protected TreeLabel label;
    
    public ButtonRenderer() {
        setLayout(null);
        add(button = new JButton());
        add(label = new TreeLabel());
        button.setPreferredSize(new Dimension(20,15));
        button.setBackground(UIManager.getColor("Tree.textBackground"));
        label.setForeground(UIManager.getColor("Tree.textForeground"));
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean isSelected, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        String stringValue = tree.convertValueToText(value, isSelected,
                expanded, leaf, row, hasFocus);
        setEnabled(tree.isEnabled());
//        button.setSelected(((ButtonNode) value).isSelected());
        label.setFont(tree.getFont());
        label.setText(stringValue);
        label.setSelected(isSelected);
        label.setFocus(hasFocus);
        if (leaf) {
            label.setIcon(UIManager.getIcon(""));
            button.setIcon(new ImageIcon("./images/play.png"));
        } else if (expanded) {
            label.setIcon(UIManager.getIcon("Tree.openIcon"));
            button.setIcon(new ImageIcon("./images/stop.png"));
        } else {
            label.setIcon(UIManager.getIcon("Tree.closedIcon"));
        }
        return this;
    }
    
    public Dimension getPreferredSize() {
        Dimension d_check = button.getPreferredSize();
        Dimension d_label = label.getPreferredSize();
        return new Dimension(d_check.width + d_label.width,(d_check.height < d_label.height ? d_label.height: d_check.height));   
    }
    
    
    public void doLayout() {
        Dimension d_check = button.getPreferredSize();
        Dimension d_label = label.getPreferredSize();
        int y_check = 0;
        int y_label = 0;
        if (d_check.height < d_label.height) {
            y_check = (d_label.height - d_check.height) / 2;
        } else {
            y_label = (d_check.height - d_label.height) / 2;
        }
        button.setLocation(0, y_check);
        button.setBounds(0, y_check, d_check.width, d_check.height);
        label.setLocation(d_check.width, y_label);
        label.setBounds(d_check.width, y_label, d_label.width, d_label.height);
    }
    
    public void setBackground(Color color) {
        if (color instanceof ColorUIResource)
            color = null;
        super.setBackground(color);
    }
    
    public class TreeLabel extends JLabel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		boolean isSelected;
        
        boolean hasFocus;
        
        public TreeLabel() {
        }
        
        public void setBackground(Color color) {
            if (color instanceof ColorUIResource)
                color = null;
            super.setBackground(color);
        }
        
        public void paint(Graphics g) {
            String str;
            if ((str = getText()) != null) {
                if (0 < str.length()) {
                    if (isSelected) {
                        g.setColor(UIManager
                                .getColor("Tree.selectionBackground"));
                    } else {
                        g.setColor(UIManager.getColor("Tree.textBackground"));
                    }
                    Dimension d = getPreferredSize();
                    int imageOffset = 0;
                    Icon currentI = getIcon();
                    if (currentI != null) {
                        imageOffset = currentI.getIconWidth()
                        + Math.max(0, getIconTextGap() - 1);
                    }
                    g.fillRect(imageOffset, 0, d.width - 1 - imageOffset,
                            d.height);
                    if (hasFocus) {
                        g.setColor(UIManager
                                .getColor("Tree.selectionBorderColor"));
                        g.drawRect(imageOffset, 0, d.width - 1 - imageOffset,
                                d.height - 1);
                    }
                }
            }
            super.paint(g);
        }
        
        public Dimension getPreferredSize() {
            Dimension retDimension = super.getPreferredSize();
            if (retDimension != null) {
                retDimension = new Dimension(retDimension.width + 3,
                        retDimension.height);
            }
            return retDimension;
        }
        
        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
        
        public void setFocus(boolean hasFocus) {
            this.hasFocus = hasFocus;
        }
    }
}
