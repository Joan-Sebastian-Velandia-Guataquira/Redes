/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 *
 * @author PC
 */
public class ImagenesT extends DefaultTableCellRenderer{

    @Override
    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus, int row, int column) 
    {
        if(obj instanceof JLabel){
            JLabel lbl= (JLabel)obj;
            return lbl;
        }
        return super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column); //To change body of generated methods, choose Tools | Templates.
    }
    
}
