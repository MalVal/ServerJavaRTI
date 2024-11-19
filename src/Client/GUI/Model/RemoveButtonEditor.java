package Client.GUI.Model;

import Client.Interfaces.ClientInterface;

import javax.swing.*;
import java.awt.*;

public class RemoveButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private String label;
    private boolean isPushed;
    private final ClientInterface client;
    private final JTable table;

    public RemoveButtonEditor(JCheckBox checkBox, ClientInterface client, JTable table) {
        super(checkBox);
        this.client = client;
        this.table = table;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        label = (value == null) ? "Remove" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            int row = table.getEditingRow();
            if (row != -1) {
                int bookId = (int) table.getValueAt(row, 0);
                int quantity = (int) table.getValueAt(row, 2);
                this.client.removeFromCaddy(bookId, quantity);
            }
        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}