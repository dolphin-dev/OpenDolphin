/*
 * RegisteredDiagnosisTransferHandler.java
 * Copyright (C) 2007 Digital Globe, Inc. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package open.dolphin.order;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import open.dolphin.infomodel.RegisteredDiagnosisModel;
import open.dolphin.table.ObjectTableModel;


/**
 * RegisteredDiagnosisTransferHandler
 *
 * @author Minagawa,Kazushi
 *
 */
public class RegisteredDiagnosisTransferHandler extends TransferHandler {
    
    private static final long serialVersionUID = 4871088750931696219L;
    
    private DataFlavor registeredDiagnosisFlavor = RegisteredDiagnosisTransferable.registeredDiagnosisFlavor;
    
    private JTable sourceTable;
    private boolean shouldRemove;
    private int fromIndex;
    private int toIndex;
    private DiagnosisTablePanel editor;
    
    public RegisteredDiagnosisTransferHandler(DiagnosisTablePanel editor) {
        this.editor = editor;
    }
    
    protected Transferable createTransferable(JComponent c) {
        sourceTable = (JTable) c;
        ObjectTableModel tableModel = (ObjectTableModel) sourceTable.getModel();
        fromIndex = sourceTable.getSelectedRow();
        RegisteredDiagnosisModel dragItem = (RegisteredDiagnosisModel) tableModel.getObject(fromIndex);
        return dragItem != null ? new RegisteredDiagnosisTransferable(dragItem) : null;
    }
    
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
    
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(c, t.getTransferDataFlavors())) {
            try {
                RegisteredDiagnosisModel dropItem = (RegisteredDiagnosisModel) t.getTransferData(registeredDiagnosisFlavor);
                JTable dropTable = (JTable) c;
                ObjectTableModel tableModel = (ObjectTableModel) dropTable.getModel();
                toIndex = dropTable.getSelectedRow();
                shouldRemove = dropTable == sourceTable ? true : false;
                if (shouldRemove) {
                    tableModel.moveRow(fromIndex, toIndex);
                    editor.reconstractDiagnosis();
                } 
                sourceTable.getSelectionModel().setSelectionInterval(toIndex, toIndex);
                return true;
                
            } catch (Exception ioe) {
            }
        }
        
        return false;
    }
    
    protected void exportDone(JComponent c, Transferable data, int action) {
        if (action == MOVE && shouldRemove) {
        }
        shouldRemove = false;
        fromIndex = -1;
        toIndex = -1;
    }
    
    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        JTable dropTable = (JTable) c;
        ObjectTableModel tableModel = (ObjectTableModel) dropTable.getModel();
        if (tableModel.getObject(dropTable.getSelectedRow()) != null) {
            for (int i = 0; i < flavors.length; i++) {
                if (registeredDiagnosisFlavor.equals(flavors[i])) {
                    return true;
                }
            }
        }
        return false;
    }
}
