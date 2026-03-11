package ui;

import javax.swing.*;
import java.awt.*;

public class MainMenuUI extends JFrame {

    public MainMenuUI() {

        setTitle("POS System");
        setSize(400, 500);
        setLayout(new GridLayout(7, 1));

        JButton btnAdd = new JButton("เพิ่มสินค้า");
        JButton btnSell = new JButton("ขายสินค้า");
        JButton btnList = new JButton("รายการสินค้า");
        JButton btnHistory = new JButton("ประวัติการขาย");
        JButton btnReport = new JButton("รายงานยอดขาย");

        add(btnAdd);
        add(btnSell);
        add(btnList);
        add(btnHistory);
        add(btnReport);

        btnSell.addActionListener(e -> {
            new SellUI();
            dispose();
        });

        btnList.addActionListener(e -> {
            new ProductListUI();
            dispose();
        });

        btnAdd.addActionListener(e -> {
            new ProductFormUI(null);
            dispose();
        });

        btnHistory.addActionListener(e -> {
            new HistoryUI();
            dispose();
        });

        btnReport.addActionListener(e -> {
            new ReportUI();
            dispose();
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}
