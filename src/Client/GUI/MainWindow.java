package Client.GUI;

import Client.Interfaces.ClientInterface;
import Client.GUI.Model.*;
import Common.Model.Entities.Book;
import Common.Model.Entities.CaddyItem;

import javax.swing.*;
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
    private JLabel amountLabel;

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

        this.cancelButton.addActionListener(e -> {
            this.client.cancelCaddy();
        });

        this.buyButton.addActionListener(e -> {
            this.client.payCaddy();
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
        booksTable.getColumn("Action").setCellEditor(new AddButtonEditor(new JCheckBox(), this.client, this.booksTable));
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
        caddyTable.getColumn("Action").setCellEditor(new RemoveButtonEditor(new JCheckBox(), this.client, this.caddyTable));
    }

    @Override
    public void clearCaddy() {
        String[] columnNames = {"Book id", "Quantity", "Quantity to remove", "Action"};
        EditableTableModel emptyModel = new EditableTableModel(columnNames, 0, new ArrayList<>());
        caddyTable.setModel(emptyModel);
    }

    @Override
    public void setTotalAmount(Double total) {
        amountLabel.setText(String.format("%.2f", total));
    }
}
