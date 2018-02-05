package com.aspire.etl.flowmonitor.table_sort;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;



public class SortManager implements TableModelListener {

//    final static Icon upIcon = new UpDownArrow(0);

//    final static Icon downIcon = new UpDownArrow(1);

    private JTable table;

    private TableModel dataModel;

    private int sortColumn;

    private Row rows[];

    private boolean ascending;

    private int sortableColumns[];

    public SortManager(JTable jtable) {

        rows = null;

        ascending = true;

        sortableColumns = null;

        table = jtable;

        int i=0;

        int length=jtable.getModel().getColumnCount();

        final int[] columns=new int[length];

        for(;i<length;i++){

            columns[i]=i;

        }

        sortableColumns=columns;

        initialize();

    }

    public SortManager(JTable jtable, int i) {

        rows = null;

        ascending = true;

        sortableColumns = null;

        table = jtable;

        sortColumn = i;

        initialize();

    }

    public SortManager(JTable jtable, int ai[]) {

        this(jtable, ai[0]);

        sortableColumns = (int[]) ai.clone();

    }

    public void initialize() {

        dataModel = table.getModel();

        ((AbstractTableModel) dataModel).addTableModelListener(this);

        addMouseListener(table);

        JTableHeader jtableheader = table.getTableHeader();

        jtableheader.setDefaultRenderer(createHeaderRenderer());

        if (table.getRowCount() > 0) {

            reinitialize();

        }

    }

    protected TableCellRenderer createHeaderRenderer() {

        DefaultTableCellRenderer defaultHeaderRenderer = new SortHeaderRenderer();

        defaultHeaderRenderer.setHorizontalAlignment(0);

        defaultHeaderRenderer.setHorizontalTextPosition(2);

        return defaultHeaderRenderer;

    }

    public void reinitialize() {

        rows = null;

        if (table.getRowCount() > 0) {

            rows = new Row[table.getRowCount()];

            for (int i = 0; i < rows.length; i++) {
                rows[i] = new Row();

                rows[i].index = i;

            }



            if (columnIsSortable(sortColumn)) {

                sort();

            }

        }

    }

    private boolean columnIsSortable(int i) {

        if (rows != null) {

            if (sortableColumns != null) {

                for (int j = 0; j < sortableColumns.length; j++) {

                    if (i == sortableColumns[j]) {

                        return true;

                    }

                }



            } else {

                return true;

            }

        }

        return false;

    }

    public void addMouseListener(final JTable table) {

        table.getTableHeader().addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent mouseevent) {

                int i = table.columnAtPoint(mouseevent.getPoint());

                int j = table.convertColumnIndexToModel(i);

                if (!columnIsSortable(j)) {

                    return;

                }

                if (j == sortColumn) {

                    ascending = !ascending;

                } else {

                    ascending = true;

                    sortColumn = j;

                }

                sort();

            }

        });

    }

    public void sort() {

        if (rows == null) {

            return;

        } else {

            ((AbstractTableModel) dataModel).removeTableModelListener(this);

            Arrays.sort(rows);

            resetData();

            ((AbstractTableModel) dataModel).fireTableDataChanged();

            ((AbstractTableModel) dataModel).addTableModelListener(this);

            table.revalidate();

            table.repaint();

            return;

        }

    }

    public void resetData() {

        Vector data = new Vector(dataModel.getRowCount());

        int i = 0;

        for(; i < dataModel.getRowCount(); i++){

            int j=0;

            final Vector vv = new Vector(dataModel.getColumnCount());

            for(; j < dataModel.getColumnCount(); j++){

                vv.add(dataModel.getValueAt(i,j));

            }

            data.add(vv);

        }

        i=0;

        for(;i<rows.length;i++){

            if(rows[i].index!=i){

                int j=0;

                final Vector vv=(Vector)data.get(rows[i].index);

                for(;j<dataModel.getColumnCount();j++){

                    dataModel.setValueAt(vv.get(j),i,j);

                }

            }

        }

    }

    public void tableChanged(TableModelEvent tablemodelevent) {

        reinitialize();

    }

    private class SortHeaderRenderer extends DefaultTableCellRenderer {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable jtable,

                Object obj, boolean flag, boolean flag1, int i, int j) {

            if (jtable != null) {

                JTableHeader jtableheader = jtable.getTableHeader();

                if (jtableheader != null) {

                    setForeground(jtableheader.getForeground());

                    setBackground(jtableheader.getBackground());

                    setFont(jtableheader.getFont());

                }

            }

            setText(obj != null ? obj.toString() : "");

//           int k = jtable.convertColumnIndexToModel(j);

//            if (k == sortColumn) {
//
//                setIcon(ascending ? SortManager.upIcon : SortManager.downIcon);
//
//            } else {
//
//                setIcon(null);
//
//            }

            setBorder(UIManager.getBorder("TableHeader.cellBorder"));

            return this;

        }

    }

    private class Row implements Comparable {
    	
    	public int index;
    	
        private Row() {

        }

        public int compareTo(Object obj) 
        {

            Row row = (Row) obj;

            java.text.Collator cnCollator = java.text.Collator.getInstance(Locale.getDefault());

            Object obj1 = dataModel.getValueAt(index, sortColumn);

            Object obj2 = dataModel.getValueAt(row.index, sortColumn);

            if (ascending) 
            {

            	if (!(obj1 instanceof Comparable)) {

            		return -1;

            	}

            	else if (!(obj2 instanceof Comparable)) {

            		return 1;

            	} 
            	else {
            		
            		return cnCollator.compare(obj2,obj1);
            		
            	}

            }
            else
            {

	         	if (!(obj1 instanceof Comparable)) {
	
		             return 1;
		
		         }
		
	         	else if (!(obj2 instanceof Comparable)) {
		
		             return -1;
		
		         } else {
		
		             return cnCollator.compare(obj1,obj2);
		
		         }
            }		
		    
        }
    }
}
