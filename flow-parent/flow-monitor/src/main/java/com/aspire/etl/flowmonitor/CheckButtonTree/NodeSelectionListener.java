
/**
 * <p>Title: </p>
 * <p>Description:
 *
 * </p>
 * <p>Copyright: Copyright (c) 2000-2006 ASPire Technologies, Inc. </p>
 * <p>Company: ASPIRE</p>
 * @Create Author  luoqi
 * @Created Date:  2006年5月17日, 下午5:04
 * @Update Author
 * @Update Date
 * @version 1.0
 */
package com.aspire.etl.flowmonitor.CheckButtonTree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;


public class NodeSelectionListener extends MouseAdapter {
    JTree tree;
    public NodeSelectionListener(JTree tree) {
        this.tree = tree;
    }
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int row = tree.getRowForLocation(x, y);
        TreePath  path = tree.getPathForRow(row);
        //TreePath  path = tree.getSelectionPath();
        if (path != null) {  
        	DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
//            boolean isSelected = ! (node.isSelected());
//            node.setSelected(isSelected);
//            if (node.getSelectionMode() == ButtonNode.DIG_IN_SELECTION) {
//                if ( isSelected) {
//                    tree.expandPath(path);
//                } else {
//                    tree.collapsePath(path);
//                }
//            }
            ((DefaultTreeModel) tree.getModel()).nodeChanged(node);
            // I need revalidate if node is root.  but why?
            if (row == 0) {
                tree.revalidate();
                tree.repaint();
            }
        }
    }
}
