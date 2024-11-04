package Client.GUI;

import javax.swing.*;

public class MainWindow extends JFrame implements PurchaseInterface {
    private JPanel mainPanel;
    private JTextField lastnameField;
    private JTextField firstnameField;
    private JCheckBox newClientCheckBox;
    private JButton connectionBtn;
    private JPanel connectionPanel;
    private JPanel purchasePanel;

    public MainWindow() {
        this.setTitle("Book purchase");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
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
    public void displayMessage(String message, Boolean isError) {
        if(isError) {
            JOptionPane.showMessageDialog(mainPanel, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(mainPanel, message, "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
