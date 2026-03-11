package ui;

import model.Product;
import service.ProductService;
import util.WindowState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class ProductListUI extends JFrame {

    private ProductService service = new ProductService();
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtSearch = new JTextField();

    public ProductListUI() {
        setTitle("จัดการรายการสินค้า");
        setSize(WindowState.width, WindowState.height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // --- TOP PANEL: Search & Title ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        topPanel.setBackground(new Color(44, 62, 80));

        JLabel lblTitle = new JLabel("รายการสินค้าทั้งหมด");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JPanel searchBar = new JPanel(new BorderLayout(5, 5));
        searchBar.setOpaque(false);
        txtSearch.setPreferredSize(new Dimension(250, 30));
        searchBar.add(new JLabel("ค้นหา: "), BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        ((JLabel)searchBar.getComponent(0)).setForeground(Color.WHITE);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(searchBar, BorderLayout.EAST);

        // --- CENTER: Table ---
        model = new DefaultTableModel(
                new String[]{"ID", "ชื่อสินค้า", "ราคาสินค้า", "คงเหลือในคลัง"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Tahoma", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        
        // ระบบค้นหา (Filter)
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        loadData();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // --- BOTTOM: Buttons ---
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottom.setBackground(new Color(236, 240, 241));

        JButton btnEdit = createStyledButton("แก้ไขสินค้า", new Color(52, 152, 219));
        JButton btnDelete = createStyledButton("ลบสินค้า", new Color(231, 76, 60));
        JButton btnBack = createStyledButton("ย้อนกลับ", new Color(149, 165, 166));

        bottom.add(btnBack);
        bottom.add(btnEdit);
        bottom.add(btnDelete);

        // --- Layout Setting ---
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        // --- Events ---
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกสินค้าที่ต้องการลบ");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "คุณต้องการลบสินค้านี้ใช่หรือไม่?", "ยืนยันการลบ", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String id = table.getValueAt(row, 0).toString();
                List<Product> list = service.getAll();
                list.removeIf(p -> p.getId().equals(id));
                service.saveAll(list);
                loadData();
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกสินค้าที่ต้องการแก้ไข");
                return;
            }
            String id = table.getValueAt(row, 0).toString();
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
                    String.format("%,.2f", p.getPrice()),
                    p.getStock()
            });
        }
    }

    private JButton createStyledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Tahoma", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(120, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

}