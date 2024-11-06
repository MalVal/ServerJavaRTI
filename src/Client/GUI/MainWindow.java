package Client.GUI;

import Client.Controller.ClientInterface;
import Common.Model.Entities.Book;
import Common.Model.Entities.CaddyItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        NonEditableTableModel model = new NonEditableTableModel(columnNames, 0);

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
        booksTable.revalidate();
        booksTable.repaint();
        booksTable.getTableHeader().setReorderingAllowed(false);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.getColumn("Quantity to add").setCellEditor(new SpinnerEditor());
        booksTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        booksTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this.client));
    }

    static class NonEditableTableModel extends DefaultTableModel {
        public NonEditableTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 9 || column == 10;
        }
    }

    static class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private final JSpinner spinner = new JSpinner();

        public SpinnerEditor() {
            spinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
        }

        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            int stockQuantity = (int) table.getValueAt(row, 6);
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
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;
        private final ClientInterface client;

        public ButtonEditor(JCheckBox checkBox, ClientInterface client) {
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
                    if (row >= 0 && row < booksTable.getRowCount()) {
                        int bookId = (int) booksTable.getValueAt(row, 0);
                        int quantity = (int) booksTable.getValueAt(row, 9);
                        this.client.addToCaddy(bookId, quantity);
                    }
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

        String[] columnNames = {"Id", "Book id", "Quantity", "Quantity to remove", "Action"};
        NonEditableTableModel model = new NonEditableTableModel(columnNames, 0);

        for (CaddyItem item : items) {
            Object[] rowData = {
                    item.getId(),
                    item.getBookId(),
                    item.getQuantity(),
                    0,
                    "Remove"
            };
            model.addRow(rowData);
        }

        caddyTable.setModel(model);
        caddyTable.revalidate();
        caddyTable.repaint();
        caddyTable.getTableHeader().setReorderingAllowed(false);
        caddyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        caddyTable.getColumn("Quantity").setCellEditor(new SpinnerEditor());
        caddyTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        caddyTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this.client));
    }
}
