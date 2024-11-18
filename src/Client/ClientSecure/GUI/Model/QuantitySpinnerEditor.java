package Client.ClientSecure.GUI.Model;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class QuantitySpinnerEditor extends AbstractCellEditor implements TableCellEditor {
    private final JSpinner spinner;
    private final Integer value;

    public QuantitySpinnerEditor(int quantityColumn) {
        this.value = quantityColumn;
        spinner = new JSpinner();
        spinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
    }

    @Override
    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        int stockQuantity = (int) table.getValueAt(row, this.value);
        spinner.setModel(new SpinnerNumberModel(0, 0, stockQuantity, 1));
        spinner.setValue(value);
        return spinner;
    }
}