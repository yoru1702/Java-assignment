package ui;

import model.*;
import service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SellUI extends JFrame {

    private ProductService productService = new ProductService();
    private SaleService saleService = new SaleService();
    private List<CartItem> cart = new ArrayList<>();

    // ตารางสินค้าในร้าน
    private DefaultTableModel productModel;

    // ตารางตะกร้า
    private DefaultTableModel cartModel;

    private JLabel lblTotal = new JLabel("0");

    private JTextField txtId = new JTextField();
    private JTextField txtQty = new JTextField();

    public SellUI() {

        setTitle("ขายสินค้า");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===============================
        // ตารางสินค้าในร้าน (ซ้าย)
        // ===============================

        productModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Price", "Stock" }, 0);

        JTable productTable = new JTable(productModel);
        productTable.setRowHeight(25);

        JScrollPane productScroll = new JScrollPane(productTable);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("สินค้าในร้าน"));
        leftPanel.add(productScroll);
        loadData();

        
        // ===============================
        // ตารางตะกร้า (ขวา)
        // ===============================

        cartModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Qty", "Price", "Total" }, 0);

        JTable cartTable = new JTable(cartModel);
        cartTable.setRowHeight(25);

        JScrollPane cartScroll = new JScrollPane(cartTable);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("ตะกร้าสินค้า"));
        rightPanel.add(cartScroll);

        // ===============================
        // แบ่งหน้าจอ
        // ===============================

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel,
                rightPanel);

        splitPane.setDividerLocation(450);

        add(splitPane, BorderLayout.CENTER);

        // ===============================
        // TOP PANEL
        // ===============================

        JPanel top = new JPanel(new GridLayout(1, 6, 10, 10));
        top.setBorder(BorderFactory.createTitledBorder("เพิ่มสินค้า"));

        JButton btnAdd = new JButton("เพิ่มสินค้า");

        top.add(new JLabel("รหัสสินค้า"));
        top.add(txtId);

        top.add(new JLabel("จำนวน"));
        top.add(txtQty);

        top.add(new JLabel());
        top.add(btnAdd);

        add(top, BorderLayout.NORTH);

        // ===============================
        // BOTTOM PANEL
        // ===============================

        JPanel bottom = new JPanel();

        JButton btnConfirm = new JButton("ยืนยันการขาย");
        JButton btnBack = new JButton("ย้อนกลับ");

        bottom.add(new JLabel("รวมทั้งหมด: "));
        bottom.add(lblTotal);
        bottom.add(btnConfirm);
        bottom.add(btnBack);

        add(bottom, BorderLayout.SOUTH);

        // ===============================
        // ADD PRODUCT
        // ===============================

        // กดเลือกจากตารางสินค้าซ้ายมือ //
        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = productTable.getSelectedRow();
                if (row != -1) {
                    // คลิกแล้วเอารหัสมาใส่ในช่อง txtId
                    String id = productTable.getValueAt(row, 0).toString();
                    txtId.setText(id);
                    
                    // ถ้า Double Click ให้กดปุ่ม Add ให้เลย
                    if (e.getClickCount() == 2) {
                        btnAdd.doClick();
                    }
                }
            }
        });

        btnAdd.addActionListener(e -> {
            // ตรวจสอบข้อมูลการกรอก //
            if (txtId.getText().isEmpty() || txtQty.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "กรุณากรอกข้อมูลให้ครบ");
                return;
            }
            int qty;
            try {
                qty = Integer.parseInt(txtQty.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "จำนวนต้องเป็นตัวเลข");
                return;
            }

            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "จำนวนต้องมากกว่า 0");
                return;
            }

            Product p = productService.findById(txtId.getText());

            if (p == null) {
                Object[] options = { "เพิ่มสินค้าใหม่", "กรอกสินค้าเฉพาะบิล", "ยกเลิก" };
                int choice = JOptionPane.showOptionDialog(this, "ไม่พบสินค้าในระบบ", "สินค้าไม่พบ",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

                // กรณี 1 ไปเพิ่มสินค้า
                if (choice == 0) {
                    new ProductFormUI(null);
                    dispose();
                }
                // กรณี 2 กรอกสินค้าเอง
                else if (choice == 1) {

                    String id = txtId.getText();
                    String name = JOptionPane.showInputDialog("ชื่อสินค้า");

                    String priceStr = JOptionPane.showInputDialog("ราคาสินค้า");

                    double price = Double.parseDouble(priceStr);

                    Product temp = new Product(id, name, price, qty);

                    CartItem item = new CartItem(temp, qty);

                    cart.add(item);

                    cartModel.addRow(new Object[] {
                            id,
                            name,
                            qty,
                            price,
                            price * qty
                    });

                    updateTotal();
                }
                return;
            }

            // ===============================
            // ตรวจสอบ STOCK
            // ===============================

            if (qty > p.getStock()) {

                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "สินค้าเหลือเพียง " + p.getStock() + " ชิ้น\nต้องการขายเท่าที่มีหรือไม่?",
                        "Stock ไม่พอ",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    qty = p.getStock();
                } else {
                    return;
                }
            }

            // ===============================
            // รวมสินค้าซ้ำ
            // ===============================

            boolean found = false;

            for (int i = 0; i < cart.size(); i++) {

                CartItem c = cart.get(i);

                if (c.getProduct().getId().equals(p.getId())) {

                    int newQty = c.getQty() + qty;

                    if (newQty > p.getStock()) {

                        JOptionPane.showMessageDialog(
                                this,
                                "จำนวนรวมเกิน Stock ที่มี");

                        return;
                    }

                    c.setQty(newQty);

                    cartModel.setValueAt(newQty, i, 2);
                    cartModel.setValueAt(newQty * p.getPrice(), i, 4);

                    found = true;
                    break;
                }
            }

            // ===============================
            // ถ้าไม่ซ้ำ
            // ===============================

            if (!found) {

                CartItem item = new CartItem(p, qty);

                cart.add(item);

                cartModel.addRow(new Object[] {
                        p.getId(),
                        p.getName(),
                        qty,
                        p.getPrice(),
                        p.getPrice() * qty
                });
            }

            updateTotal();

            txtId.setText("");
            txtQty.setText("");

        });

        // ===============================
        // ยืนยันการขาย
        // ===============================
        btnConfirm.addActionListener(e -> {

            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ยังไม่มีสินค้าในรายการ");
                return;
            }

            // ตัด STOCK
            for (CartItem item : cart) {

                Product p = item.getProduct();

                int newStock = p.getStock() - item.getQty();

                p.setStock(newStock);

                productService.updateProduct(p);
            }

            // บันทึกการขาย
            saleService.saveSale(cart);

            JOptionPane.showMessageDialog(this, "ขายสินค้าสำเร็จ");

            cart.clear();
            cartModel.setRowCount(0);
            updateTotal();
            loadData();
        });

        // ===============================
        // BACK
        // ===============================
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

    private void loadData() {

        productModel.setRowCount(0);

        for (Product p : productService.getAll()) {

            productModel.addRow(new Object[] {
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    p.getStock()
            });
        }
    }
}
