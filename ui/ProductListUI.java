package ui;

import model.Product;
import service.ProductService;
import util.WindowState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductListUI extends JFrame {

    private ProductService service = new ProductService();
    private DefaultTableModel model;

    public ProductListUI() {

        setTitle("รายการสินค้า");
        setSize(WindowState.width, WindowState.height);
        if (WindowState.x != -1) {
            setLocation(WindowState.x, WindowState.y);
        } else {
            setLocationRelativeTo(null);
        }

        WindowState.track(this);

        model = new DefaultTableModel(
                new String[]{"ID", "Name", "Price", "Stock"}, 0);

        JTable table = new JTable(model);

        loadData();

        JButton btnEdit = new JButton("แก้ไข");
        JButton btnDelete = new JButton("ลบ");
        JButton btnBack = new JButton("ย้อนกลับ");

        JPanel bottom = new JPanel();
        bottom.add(btnEdit);
        bottom.add(btnDelete);
        bottom.add(btnBack);

        add(new JScrollPane(table));
        add(bottom, BorderLayout.SOUTH);

        btnDelete.addActionListener(e -> {

            int row = table.getSelectedRow();
            if (row < 0) return;

            String id = model.getValueAt(row, 0).toString();

            List<Product> list = service.getAll();
            list.removeIf(p -> p.getId().equals(id));

            service.saveAll(list);

            loadData();
        });

        btnEdit.addActionListener(e -> {

            int row = table.getSelectedRow();
            if (row < 0) return;

            String id = model.getValueAt(row, 0).toString();
            Product p = service.findById(id);

            new ProductFormUI(p);
            dispose();
        });

        btnBack.addActionListener(e -> {
            new MainMenuUI();
            dispose();
        });

        setVisible(true);
    }

    private void loadData() {

        model.setRowCount(0);

        for (Product p : service.getAll()) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    p.getStock()
            });
        }
    }
}
