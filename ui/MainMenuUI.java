package ui;

import javax.swing.*;

import util.WindowState;

import java.awt.*;

public class MainMenuUI extends JFrame {

    public MainMenuUI() {

        setTitle("POS System");
        setSize(WindowState.width, WindowState.height);
        if (WindowState.x != -1) {
            setLocation(WindowState.x, WindowState.y);
        } else {
            setLocationRelativeTo(null);
        }

        WindowState.track(this);

        setLayout(new GridLayout(7, 1));

        JButton btnAdd = new JButton("เพิ่มสินค้า");
        JButton btnSell = new JButton("ขายสินค้า");
        JButton btnList = new JButton("รายการสินค้า");
        JButton btnHistory = new JButton("ประวัติการขาย");
        JButton btnReport = new JButton("รายงานยอดขาย");
        JButton btnStockHistory = new JButton("ประวัติ Stock");

        add(btnAdd);
        add(btnSell);
        add(btnList);
        add(btnHistory);
        add(btnReport);
        add(btnStockHistory);

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

        btnStockHistory.addActionListener(e -> {
            new StockHistoryUI();
            dispose();
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}
