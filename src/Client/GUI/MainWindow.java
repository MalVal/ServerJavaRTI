package Client.GUI;

import Client.Controller.ClientInterface;
import Common.Model.Entities.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
            this.client.retreiveBooks(idBook, author, subject, title, isbn, publishYear);
        });

        this.resetButton.addActionListener(e -> {
            this.clearField();
            this.client.retreiveBooks(null, null, null, null, null, null);
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
        this.purchasePanel.setVisible(false);
    }

    @Override
    public void displayPurchaseMenu() {
        this.connectionPanel.setVisible(false);
        this.purchasePanel.setVisible(true);
    }

    @Override
    public void displayMessage(String message, Boolean isError) {
        if(isError) {
            JOptionPane.showMessageDialog(mainPanel, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(mainPanel, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void displayBooks(ArrayList<Book> books) {

        String[] columnNames = {"Id", "Author", "Subject", "Title", "ISBN", "PageCount", "StockQuantity", "Price", "PublishYear"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Book book : books) {
            Object[] rowData = {book.getId(), book.getAuthorLastName(), book.getSubjectName(), book.getTitle(), book.getIsbn(), book.getPageCount(), book.getStockQuantity(), book.getPrice(), book.getPublishYear()};
            model.addRow(rowData);
        }

        booksTable.setModel(model);
    }
}
