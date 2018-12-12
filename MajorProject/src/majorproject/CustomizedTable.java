/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package majorproject;

import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


enum BORDERTYPE {

    EMPTY, LINE
}
/**
 *
 * @author suraj
 */
public class CustomizedTable{

    public void createTable(JTable table, DefaultTableModel model1) {
        table.setModel(model1);
    }

    public void addColumn(Object value, DefaultTableModel model1) {
        model1.addColumn(value);
    }

    public void addRows(Object[] value, DefaultTableModel model1) {
        model1.addRow(value);
    }

    public void resizeColumn(JTable table, int index, int size) {
        table.getColumnModel().getColumn(index).setPreferredWidth(size);
        //table.getColumnModel().getColumn(1).setCellRenderer(new CustomRenderer(0, 0, Color.GRAY));
    }

    public void setBoldHeader(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            //table.getColumnModel().getColumn(i).setCellRenderer(new CustomRenderer(0, 0, Color.GRAY));
            table.getColumnModel().getColumn(i).setHeaderRenderer(new CustomRenderer(0, 0, Color.GRAY, BORDERTYPE.LINE));
        }
//        table.getColumnModel().getColumn(0).set
//        trialBalance.jTable1.getCellRenderer(0,0).getTableCellRendererComponent(trialBalance.jTable1,new String[]{"b"}, true,true,1,1).setBackground(Color.GRAY);
    }

    public void setLightHeader(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(new CustomRenderer(0, 0, Color.WHITE, BORDERTYPE.LINE));
        }
    }

    public void setLightLinedHeader(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(new CustomRenderer(0, 0, Color.lightGray, BORDERTYPE.LINE));
        }
    }

    public void setEmptyHeader(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(new CustomRenderer(0, 0, Color.WHITE, BORDERTYPE.EMPTY));
        }
    }
    public void setEmptyLineHeader(JTable table){
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(new CustomRenderer(0, 0, Color.WHITE, BORDERTYPE.LINE));
        }
    }

    public void setBorderType(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new CustomRenderer(0, 0, Color.WHITE, BORDERTYPE.LINE));
        }
    }
    public void setLightBody(JTable table){
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new CustomRenderer(0, 0, Color.WHITE, BORDERTYPE.LINE));
        }
    }

}

class CustomRenderer extends DefaultTableCellRenderer {

    private int rowIndex, columnIndex;
    private Color color;
    private BORDERTYPE border;

    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, columnIndex);
        switch (border) {
            case EMPTY:
                this.setBorder(BorderFactory.createEmptyBorder());
                break;
            case LINE:
                this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                break;
        }
        this.setBackground(color);
        // this.setText(value.toString());
        return this;
    }

    public CustomRenderer(int row, int column, Color clr, BORDERTYPE b) {
        rowIndex = row;
        columnIndex = column;
        color = clr;
        border = b;
    }
}
