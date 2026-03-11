package ui;

import model.Product;
import service.ProductService;
import util.WindowState;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ProductFormUI extends JFrame {

    private ProductService service = new ProductService();
    private Product editProduct;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ProductFormUI(Product product) {

        this.editProduct = product;

        setTitle(product == null ? "เพิ่มสินค้า" : "แก้ไขสินค้า");
        setSize(WindowState.width, WindowState.height);
        if (WindowState.x != -1) {
            setLocation(WindowState.x, WindowState.y);
        } else {
            setLocationRelativeTo(null);
        }

        WindowState.track(this);
        setLayout(new GridLayout(7, 2, 5, 5)); // เพิ่มแถวสำหรับ field ใหม่

        JTextField txtId = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtPrice = new JTextField();
        JTextField txtCostPrice = new JTextField();
        JTextField txtStock = new JTextField();
        JTextField txtImportedAt = new JTextField(
                LocalDateTime.now().format(FORMATTER) // default = เวลาปัจจุบัน
        );

        if (product != null) {
            txtId.setText(product.getId());
            txtId.setEnabled(false);
            txtName.setText(product.getName());
            txtPrice.setText(String.valueOf(product.getPrice()));
            txtCostPrice.setText(String.valueOf(product.getCostPrice()));
            txtStock.setText(String.valueOf(product.getStock()));
            txtImportedAt.setText(product.getImportedAt() != null
                    ? product.getImportedAt().format(FORMATTER) : "");
        }

        JButton btnSave = new JButton("บันทึก");
        JButton btnBack = new JButton("ย้อนกลับ");

        add(new JLabel("รหัสสินค้า"));
        add(txtId);
        add(new JLabel("ชื่อ"));
        add(txtName);
        add(new JLabel("ราคาขาย"));
        add(txtPrice);
        add(new JLabel("ราคาทุน"));
        add(txtCostPrice);
        add(new JLabel("Stock"));
        add(txtStock);
        add(new JLabel("เวลานำเข้า (dd/MM/yyyy HH:mm)"));
        add(txtImportedAt);
        add(btnSave);
        add(btnBack);

        btnSave.addActionListener(e -> {
            try {
                LocalDateTime importedAt = LocalDateTime.parse(
                        txtImportedAt.getText().trim(), FORMATTER);

                List<Product> list = service.getAll();

                if (editProduct == null) {
                    list.add(new Product(
                            txtId.getText(),
                            txtName.getText(),
                            Double.parseDouble(txtPrice.getText()),
                            Double.parseDouble(txtCostPrice.getText()),
                            Integer.parseInt(txtStock.getText()),
                            importedAt
                    ));
                } else {
                    for (Product p : list) {
                        if (p.getId().equals(editProduct.getId())) {
                            p.setName(txtName.getText());
                            p.setPrice(Double.parseDouble(txtPrice.getText()));
                            p.setCostPrice(Double.parseDouble(txtCostPrice.getText()));
                            p.setStock(Integer.parseInt(txtStock.getText()));
                            p.setImportedAt(importedAt);
                        }
                    }
                }

                service.saveAll(list);
                JOptionPane.showMessageDialog(this, "บันทึกแล้ว");
                new MainMenuUI();
                dispose();

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "รูปแบบวันที่ไม่ถูกต้อง\nกรุณากรอก dd/MM/yyyy HH:mm\nเช่น 11/03/2026 14:30",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "กรุณากรอกราคาและ Stock เป็นตัวเลข",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> {
            new MainMenuUI();
            dispose();
        });

        setVisible(true);
    }
}