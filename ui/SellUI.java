package ui;

import model.*;
import service.*;
import util.WindowState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class SellUI extends JFrame {

    private ProductService productService = new ProductService();
    private SaleService saleService = new SaleService();

    private List<CartItem> cart = new ArrayList<>();

    private DefaultTableModel model;
    private JLabel lblTotal = new JLabel("0");

    public SellUI() {

        setTitle("ขายสินค้า");
        setSize(WindowState.width, WindowState.height);
        if (WindowState.x != -1) {
            setLocation(WindowState.x, WindowState.y);
        } else {
            setLocationRelativeTo(null);
        }

        WindowState.track(this);

        model = new DefaultTableModel(
                new String[]{"ID", "Name", "Qty", "Price", "Total"}, 0);

        JTable table = new JTable(model);

        JTextField txtId = new JTextField();
        JTextField txtQty = new JTextField();

        JButton btnAdd = new JButton("เพิ่ม");
        JButton btnConfirm = new JButton("ยืนยันการขาย");
        JButton btnBack = new JButton("ย้อนกลับ");

        JPanel top = new JPanel(new GridLayout(2, 4));
        top.add(new JLabel("รหัส"));
        top.add(txtId);
        top.add(new JLabel("จำนวน"));
        top.add(txtQty);
        top.add(btnAdd);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(new JLabel("รวมทั้งหมด: "));
        bottom.add(lblTotal);
        bottom.add(btnConfirm);
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {

            Product p = productService.findById(txtId.getText());

            if (p == null) {
                JOptionPane.showMessageDialog(this,
                        "ไม่พบสินค้า! ไปเพิ่มสินค้าได้");
                return;
            }

            int qty = Integer.parseInt(txtQty.getText());

            cart.add(new CartItem(p, qty));
            model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    qty,
                    p.getPrice(),
                    p.getPrice() * qty
            });

            updateTotal();
        });

        btnConfirm.addActionListener(e -> {
            saleService.saveSale(cart);
            JOptionPane.showMessageDialog(this, "บันทึกการขายแล้ว");
            cart.clear();
            model.setRowCount(0);
            updateTotal();
        });

        btnBack.addActionListener(e -> {
            new MainMenuUI();
            dispose();
        });

        setVisible(true);
    }

    private void updateTotal() {
        double sum = 0;
        for (CartItem c : cart)
            sum += c.getTotal();

        lblTotal.setText("" + sum);
    }
}
