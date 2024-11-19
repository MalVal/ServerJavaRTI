package Client.GUI.Model;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class EditableTableModel extends DefaultTableModel {
    private final ArrayList<Integer> editableCells;

    public EditableTableModel(Object[] columnNames, int rowCount, ArrayList<Integer> editableCells) {
        super(columnNames, rowCount);
        this.editableCells = editableCells;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return editableCells.contains(column);
    }
}