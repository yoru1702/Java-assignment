package ui;

import model.Product;
import service.ProductService;
import util.WindowState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ProductFormUI extends JFrame {

    private ProductService service = new ProductService();
    private Product editProduct;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ProductFormUI(Product product) {
        this.editProduct = product;

        setTitle(product == null ? "เพิ่มสินค้าใหม่" : "แก้ไขข้อมูลสินค้า");
        setSize(WindowState.width, WindowState.height);
        if (WindowState.x != -1) {
            setLocation(WindowState.x, WindowState.y);
        } else {
            setLocationRelativeTo(null);
        }

        WindowState.track(this);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. Header Panel ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 62, 80)); // สี Dark Slate เดียวกับ Sidebar
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel(product == null ? " เพิ่มสินค้าใหม่" : " แก้ไขข้อมูลสินค้า");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. Form Panel (GridBagLayout) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // สร้าง TextFields
        JTextField txtId = createStyledTextField();
        JTextField txtName = createStyledTextField();
        JTextField txtPrice = createStyledTextField();
        JTextField txtCostPrice = createStyledTextField();
        JTextField txtStock = createStyledTextField();
        JTextField txtImportedAt = createStyledTextField();
        txtImportedAt.setText(LocalDateTime.now().format(FORMATTER));

        if (product != null) {
            txtId.setText(product.getId());
            txtId.setEnabled(false);
            txtId.setBackground(new Color(245, 245, 245));
            txtName.setText(product.getName());
            txtPrice.setText(String.valueOf(product.getPrice()));
            txtCostPrice.setText(String.valueOf(product.getCostPrice()));
            txtStock.setText(String.valueOf(product.getStock()));
            txtImportedAt.setText(product.getImportedAt() != null ? product.getImportedAt().format(FORMATTER) : "");
        }

        // เพิ่มคอมโพเนนต์ลงใน Grid
        addFormField(formPanel, "รหัสสินค้า:", txtId, gbc, 0);
        addFormField(formPanel, "ชื่อสินค้า:", txtName, gbc, 1);
        addFormField(formPanel, "ราคาขาย (บาท):", txtPrice, gbc, 2);
        addFormField(formPanel, "ราคาทุน (บาท):", txtCostPrice, gbc, 3);
        addFormField(formPanel, "จำนวนในคลัง (Stock):", txtStock, gbc, 4);
        addFormField(formPanel, "เวลานำเข้า (วว/ดด/ปปปป นน:นน):", txtImportedAt, gbc, 5);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);

        // --- 3. Footer Panel (Buttons) ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footerPanel.setBackground(new Color(245, 245, 245));

        JButton btnSave = new JButton("บันทึกข้อมูล");
        styleButton(btnSave, new Color(46, 204, 113)); // สีเขียว

        JButton btnBack = new JButton("ย้อนกลับ");
        styleButton(btnBack, new Color(149, 165, 166)); // สีเทา

        footerPanel.add(btnBack);
        footerPanel.add(btnSave);
        add(footerPanel, BorderLayout.SOUTH);

        // --- 4. Logic ---
        btnSave.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                String name = txtName.getText().trim();
                if(id.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "กรุณากรอกรหัสและชื่อสินค้า", "คำเตือน", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                LocalDateTime importedAt = LocalDateTime.parse(txtImportedAt.getText().trim(), FORMATTER);
                List<Product> list = service.getAll();

                if (editProduct == null) {
                    list.add(new Product(id, name, Double.parseDouble(txtPrice.getText()), 
                         Double.parseDouble(txtCostPrice.getText()), Integer.parseInt(txtStock.getText()), importedAt));
                } else {
                    for (Product p : list) {
                        if (p.getId().equals(editProduct.getId())) {
                            p.setName(name);
                            p.setPrice(Double.parseDouble(txtPrice.getText()));
                            p.setCostPrice(Double.parseDouble(txtCostPrice.getText()));
                            p.setStock(Integer.parseInt(txtStock.getText()));
                            p.setImportedAt(importedAt);
                        }
                    }
                }

                service.saveAll(list);
                JOptionPane.showMessageDialog(this, "บันทึกข้อมูลเรียบร้อยแล้ว", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
                new MainMenuUI();
                dispose();

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "รูปแบบวันที่ไม่ถูกต้อง (dd/MM/yyyy HH:mm)", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "กรุณากรอกตัวเลขในช่องราคาและสต็อก", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> {
            new MainMenuUI();
            dispose();
        });

        setVisible(true);
    }

    private void addFormField(JPanel panel, String labelText, JTextField textField, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Tahoma", Font.BOLD, 13));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(textField, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Tahoma", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void styleButton(JButton btn, Color color) {
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Tahoma", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}