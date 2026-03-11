package ui;

import model.Product;
import service.ProductService;
import util.WindowState;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductFormUI extends JFrame {

    private ProductService service = new ProductService();
    private Product editProduct;

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
        setLayout(new GridLayout(5, 2));

        JTextField txtId = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtPrice = new JTextField();
        JTextField txtStock = new JTextField();

        if (product != null) {
            txtId.setText(product.getId());
            txtId.setEnabled(false);
            txtName.setText(product.getName());
            txtPrice.setText("" + product.getPrice());
            txtStock.setText("" + product.getStock());
        }

        JButton btnSave = new JButton("บันทึก");
        JButton btnBack = new JButton("ย้อนกลับ");

        add(new JLabel("รหัสสินค้า"));
        add(txtId);
        add(new JLabel("ชื่อ"));
        add(txtName);
        add(new JLabel("ราคา"));
        add(txtPrice);
        add(new JLabel("Stock"));
        add(txtStock);
        add(btnSave);
        add(btnBack);

        btnSave.addActionListener(e -> {

            List<Product> list = service.getAll();

            if (editProduct == null) {
                list.add(new Product(
                        txtId.getText(),
                        txtName.getText(),
                        Double.parseDouble(txtPrice.getText()),
                        Integer.parseInt(txtStock.getText())
                ));
            } else {
                for (Product p : list) {
                    if (p.getId().equals(editProduct.getId())) {
                        p.setName(txtName.getText());
                        p.setPrice(Double.parseDouble(txtPrice.getText()));
                        p.setStock(Integer.parseInt(txtStock.getText()));
                    }
                }
            }

            service.saveAll(list);

            JOptionPane.showMessageDialog(this, "บันทึกแล้ว");
            new MainMenuUI();
            dispose();
        });

        btnBack.addActionListener(e -> {
            new MainMenuUI();
            dispose();
        });

        setVisible(true);
    }
}
