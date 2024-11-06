package Client.GUI;

import Client.Controller.ClientInterface;
import Common.Model.Entities.Book;
import Common.Model.Entities.CaddyItem;
import Client.GUI.Model.*;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class MainWindow extends JFrame implements PurchaseInterface {
    private ClientInterface client;
    private JPanel mainPanel;
    private JTextField lastnameField;
    private JTextField firstnameField;
    private JCheckBox newClientCheckBox;
    private JButton connectionBtn;
    private JPanel connectionPanel;
    private JPanel purchasePanel;
    private JTable booksTable;
    private JTextField idBookField;
    private JTextField authorField;
    private JTextField subjectField;
    private JTextField titleField;
    private JTextField isbnField;
    private JTextField publishYearField;
    private JButton searchButton;
    private JButton resetButton;
    private JTabbedPane connectedPanel;
    private JPanel caddyPanel;
    private JTable caddyTable;
    private JButton buyButton;
    private JButton cancelButton;

    public MainWindow() {
        this.setTitle("Book purchase");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();

        this.connectionBtn.addActionListener(e -> {
            this.client.connection(this.lastnameField.getText(), this.firstnameField.getText(), this.newClientCheckBox.isSelected());
        });

        this.searchButton.addActionListener(e -> {
            Integer idBook = idBookField.getText().isEmpty() ? null : Integer.parseInt(idBookField.getText());
            String author = authorField.getText().isEmpty() ? null : authorField.getText();
            String subject = subjectField.getText().isEmpty() ? null : subjectField.getText();
            String title = titleField.getText().isEmpty() ? null : titleField.getText();
            String isbn = isbnField.getText().isEmpty() ? null : isbnField.getText();
            Integer publishYear = publishYearField.getText().isEmpty() ? null : Integer.parseInt(publishYearField.getText());
            this.client.retrieveBooks(idBook, author, subject, title, isbn, publishYear);
        });

        this.resetButton.addActionListener(e -> {
            this.clearField();
            this.client.retrieveBooks(null, null, null, null, null, null);
        });
    }

    private void clearField() {
        idBookField.setText("");
        authorField.setText("");
        subjectField.setText("");
        titleField.setText("");
        isbnField.setText("");
        publishYearField.setText("");
    }

    @Override
    public void setController(ClientInterface controller) {
        this.client = controller;
    }

    @Override
    public void display() {
        this.setVisible(true);
    }

    @Override
    public void displayConnectionMenu() {
        this.connectionPanel.setVisible(true);
        this.connectedPanel.setVisible(false);
    }

    @Override
    public void displayConnectedPanel() {
        this.connectionPanel.setVisible(false);
        this.connectedPanel.setVisible(true);
    }

    @Override
    public void displayMessage(String message, Boolean isError) {
        if (isError) {
            JOptionPane.showMessageDialog(mainPanel, message, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(mainPanel, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void displayBooks(ArrayList<Book> books) {
        String[] columnNames = {"Id", "Author", "Subject", "Title", "ISBN", "PageCount", "StockQuantity", "Price", "PublishYear", "Quantity to add", "Action"};
        ArrayList<Integer> editableCells = new ArrayList<>();
        editableCells.add(9);
        editableCells.add(10);
        EditableTableModel model = new EditableTableModel(columnNames, 0, editableCells);

        for (Book book : books) {
            Object[] rowData = {
                    book.getId(),
                    book.getAuthorLastName(),
                    book.getSubjectName(),
                    book.getTitle(),
                    book.getIsbn(),
                    book.getPageCount(),
                    book.getStockQuantity(),
                    book.getPrice(),
                    book.getPublishYear(),
                    0,
                    "Add"
            };
            model.addRow(rowData);
        }
        booksTable.setModel(model);
        booksTable.getTableHeader().setReorderingAllowed(false);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.getColumn("Quantity to add").setCellEditor(new QuantitySpinnerEditor(6));
        booksTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        booksTable.getColumn("Action").setCellEditor(new AddButtonEditor(new JCheckBox(), this.client));
    }

    static class QuantitySpinnerEditor extends AbstractCellEditor implements TableCellEditor {
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

    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Add" : value.toString());
            setText((value == null) ? "Remove" : value.toString());
            return this;
        }
    }

    class AddButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;
        private final ClientInterface client;

        public AddButtonEditor(JCheckBox checkBox, ClientInterface client) {
            super(checkBox);
            this.client = client;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "Add" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int row = booksTable.getEditingRow();
                if (row != -1) {
                    int bookId = (int) booksTable.getValueAt(row, 0);
                    int quantity = (int) booksTable.getValueAt(row, 9);
                    this.client.addToCaddy(bookId, quantity);
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

    class RemoveButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;
        private final ClientInterface client;

        public RemoveButtonEditor(JCheckBox checkBox, ClientInterface client) {
            super(checkBox);
            this.client = client;
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
                int row = caddyTable.getEditingRow();
                if (row != -1) {
                    int bookId = (int) caddyTable.getValueAt(row, 0);
                    int quantity = (int) caddyTable.getValueAt(row, 2);
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

    @Override
    public void displayCaddy(ArrayList<CaddyItem> items) {
        String[] columnNames = {"Book id", "Quantity", "Quantity to remove", "Action"};

        ArrayList<Integer> editableCells = new ArrayList<>();
        editableCells.add(2);
        editableCells.add(3);
        EditableTableModel model = new EditableTableModel(columnNames, 0, editableCells);

        for (CaddyItem item : items) {
            Object[] rowData = {
                    item.getBookId(),
                    item.getQuantity(),
                    0,
                    "Remove"
            };
            model.addRow(rowData);
        }

        caddyTable.setModel(model);
        caddyTable.getTableHeader().setReorderingAllowed(false);
        caddyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        caddyTable.getColumn("Quantity to remove").setCellEditor(new QuantitySpinnerEditor(1));
        caddyTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        caddyTable.getColumn("Action").setCellEditor(new RemoveButtonEditor(new JCheckBox(), this.client));
    }
}
