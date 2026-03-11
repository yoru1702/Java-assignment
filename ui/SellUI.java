package ui;

import model.*;
import service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class SellUI extends JFrame {

    private ProductService productService = new ProductService();
    private SaleService saleService = new SaleService();
    private List<CartItem> cart = new ArrayList<>();

    private DefaultTableModel productModel;
    private DefaultTableModel cartModel;

    private JLabel lblTotal = new JLabel("0.00");
    private JTextField txtId = new JTextField();
    private JTextField txtQty = new JTextField("1");

    public SellUI() {

        setTitle("ระบบขายสินค้า (POS System)");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ===============================
        // CENTER: ตารางสินค้า และ ตะกร้า
        // ===============================
        // ตารางสินค้าในร้าน (ซ้าย)
        productModel = new DefaultTableModel(new String[] { "ID", "ชื่อสินค้า", "ราคา", "คงเหลือ" }, 0);
        JTable productTable = new JTable(productModel);
        productTable.setRowHeight(25);
        productTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 12));

        // ตารางตะกร้าสินค้า (ขวา)
        cartModel = new DefaultTableModel(new String[] { "ID", "ชื่อสินค้า", "จำนวน", "ราคา", "รวมเงิน" }, 0);
        JTable cartTable = new JTable(cartModel);
        cartTable.setRowHeight(25);
        cartTable.setSelectionBackground(new Color(255, 234, 167));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createTitledPanel("สินค้าในสต็อก (ดับเบิลคลิกเพื่อเลือก)", new JScrollPane(productTable)),
                createTitledPanel("ตะกร้าสินค้า (ดับเบิลคลิกเพื่อลบ)", new JScrollPane(cartTable)));
        splitPane.setDividerLocation(450);

        // ===============================
        // TOP PANEL
        // ===============================

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        top.setBackground(new Color(236, 240, 241));
        top.setBorder(BorderFactory.createTitledBorder(" ค้นหาและเพิ่มสินค้า "));

        txtId.setPreferredSize(new Dimension(150, 30));
        txtQty.setPreferredSize(new Dimension(80, 30));
        txtQty.setHorizontalAlignment(JTextField.CENTER);

        JButton btnAdd = new JButton("เพิ่มลงตะกร้า");
        btnAdd.setBackground(new Color(52, 152, 219));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setPreferredSize(new Dimension(120, 30));

        top.add(new JLabel("รหัสสินค้า:"));
        top.add(txtId);
        top.add(new JLabel("จำนวน:"));
        top.add(txtQty);
        top.add(btnAdd);

        // ===============================
        // BOTTOM PANEL: สรุปยอดเงิน
        // ===============================
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // ยอดรวมเด่นๆ
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setOpaque(false);
        JLabel lblText = new JLabel("ยอดรวมสุทธิ: ");
        lblText.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTotal.setFont(new Font("Tahoma", Font.BOLD, 35));
        lblTotal.setForeground(new Color(192, 57, 43));
        totalPanel.add(lblText);
        totalPanel.add(lblTotal);
        totalPanel.add(new JLabel(" บาท "));

        // ปุ่มคำสั่ง
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnConfirm = new JButton("ยืนยันการขาย (F10)");
        btnConfirm.setPreferredSize(new Dimension(180, 50));
        btnConfirm.setBackground(new Color(39, 174, 96));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(new Font("Tahoma", Font.BOLD, 16));

        JButton btnBack = new JButton("ย้อนกลับ");
        btnBack.setPreferredSize(new Dimension(100, 50));

        actionPanel.add(btnBack);
        actionPanel.add(btnConfirm);

        bottom.add(totalPanel, BorderLayout.NORTH);
        bottom.add(actionPanel, BorderLayout.SOUTH);

        // วางลงหน้าจอ
        add(top, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // ===============================
        // EVENTS / LOGIC
        // ===============================

        // 1. เลือกจากตารางซ้าย
        productTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = productTable.getSelectedRow();
                if (row != -1) {
                    txtId.setText(productTable.getValueAt(row, 0).toString());
                    if (e.getClickCount() == 2)
                        btnAdd.doClick();
                }
            }
        });
        // 2. ลบจากตารางขวา (ตะกร้า)
        cartTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = cartTable.getSelectedRow();
                    if (row != -1) {
                        cart.remove(row);
                        cartModel.removeRow(row);
                        updateTotal();
                    }
                }
            }
        });
        // 3. เพิ่มสินค้า
        btnAdd.addActionListener(e -> {
            String id = txtId.getText().trim();
            String qtyStr = txtQty.getText().trim();
            if (id.isEmpty() || qtyStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "กรุณาระบุรหัสและจำนวนสินค้า");
                return;
            }
            try {
                int qty = Integer.parseInt(txtQty.getText());
                if (qty <= 0)
                    throw new NumberFormatException();

                Product p = productService.findById(id);
                if (p == null) {
                    handleProductNotFound(txtId.getText(), qty);
                    return;
                }
                if (qty > p.getStock()) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "สินค้า " + p.getName() + " เหลือเพียง " + p.getStock() + " ชิ้น\nขายเท่าที่มีหรือไม่?",
                            "Stock ไม่พอ", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION)
                        qty = p.getStock();
                    else
                        return;
                }
                addProductToCart(p, qty);
                txtId.setText("");
                txtQty.setText("1");
                txtId.requestFocus();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "จำนวนต้องเป็นตัวเลขที่มากกว่า 0");
            }
        });
        // 4. ยืนยันการขาย
        btnConfirm.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ไม่มีสินค้าในตะกร้า");
                return;
            }
            for (CartItem item : cart) {
                Product p = item.getProduct();
                p.setStock(p.getStock() - item.getQty());
                productService.updateProduct(p);
            }
            saleService.saveSale(cart);
            JOptionPane.showMessageDialog(this, "ขายสินค้าสำเร็จ!");

            cart.clear();
            cartModel.setRowCount(0);
            updateTotal();
            loadData();
        });
        btnBack.addActionListener(e -> {
            new MainMenuUI();
            dispose();
        });
        loadData();
        setVisible(true);
    }

    private void addProductToCart(Product p, int qty) {
        boolean found = false;
        for (int i = 0; i < cart.size(); i++) {
            CartItem c = cart.get(i);
            if (c.getProduct().getId().equals(p.getId())) {
                int newQty = c.getQty() + qty;
                if (newQty > p.getStock()) {
                    JOptionPane.showMessageDialog(this, "จำนวนรวมเกิน Stock ที่มี");
                    return;
                }
                c.setQty(newQty);
                cartModel.setValueAt(newQty, i, 2);
                cartModel.setValueAt(String.format("%.2f", newQty * p.getPrice()), i, 4);
                found = true;
                break;
            }
        }
        if (!found) {
            cart.add(new CartItem(p, qty));
            cartModel.addRow(new Object[] { p.getId(), p.getName(), qty, p.getPrice(), (p.getPrice() * qty) });
        }
        updateTotal();
    }

    private void handleProductNotFound(String id, int qty) { 
    Object[] options = { "เพิ่มใหม่", "กรอกเอง", "ยกเลิก" };
    int choice = JOptionPane.showOptionDialog(this, "ไม่พบรหัสสินค้า: " + id, "ไม่พบสินค้า",
            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
    if (choice == 0) {
        new ProductFormUI(null);
        dispose();
    } else if (choice == 1) {
        // --- ส่วนที่กรอกเอง ---
        String name = JOptionPane.showInputDialog(this, "ชื่อสินค้า (เฉพาะบิล)");
        if (name == null || name.trim().isEmpty()) return;

        String priceStr = JOptionPane.showInputDialog(this, "ราคาสินค้า");
        if (priceStr == null) return;

        try {
            double price = Double.parseDouble(priceStr);
            Product temp = new Product(id, name, price, qty);
            cart.add(new CartItem(temp, qty));
            cartModel.addRow(new Object[] { id, name, qty, price, price * qty });
            
            updateTotal();
            txtId.setText("");
            txtQty.setText("1");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ราคาต้องเป็นตัวเลขเท่านั้น");
        }
    }
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

    private JPanel createTitledPanel(String title, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), title));
        panel.add(component);
        return panel;
    }
}
