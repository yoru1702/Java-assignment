package ui;

import model.Product;
import service.ProductService;
import util.WindowState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StockHistoryUI extends JFrame {

    private ProductService service = new ProductService();
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DefaultTableModel tableModel;

    public StockHistoryUI() {

        setTitle("ประวัติการเพิ่ม Stock");
        setSize(WindowState.width, WindowState.height);
        if (WindowState.x != -1) {
            setLocation(WindowState.x, WindowState.y);
        } else {
            setLocationRelativeTo(null);
        }
        WindowState.track(this);
        setLayout(new BorderLayout(5, 5));

        // ── Panel กรอง ──
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField txtSearch = new JTextField(15);
        txtSearch.setToolTipText("วัน: 11/3/2026 | เดือน: 3/2026 | ปี: 2026");
        JButton btnSearch = new JButton("ค้นหา");
        JButton btnAll    = new JButton("ทั้งหมด");

        filterPanel.add(new JLabel("ค้นหา:"));
        filterPanel.add(txtSearch);
        filterPanel.add(btnSearch);
        filterPanel.add(btnAll);

        // ── ตาราง ──
        String[] columns = {"รหัสสินค้า", "ชื่อสินค้า", "ราคาขาย", "ราคาทุน", "Stock", "เวลานำเข้า"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        loadTable(service.getAll());

        // ── ปุ่มล่าง ──
        JButton btnBack = new JButton("ย้อนกลับ");
        btnBack.addActionListener(e -> {
            new MainMenuUI();
            dispose();
        });

        // ── Action ──
        btnSearch.addActionListener(e -> {
            String value = txtSearch.getText().trim();
            if (value.isEmpty()) {
                JOptionPane.showMessageDialog(this, "กรุณากรอกค่าที่ต้องการค้นหา");
                return;
            }
            filterTable(value);
        });

        // กด Enter ในกล่องค้นหาได้เลย
        txtSearch.addActionListener(e -> {
            String value = txtSearch.getText().trim();
            if (!value.isEmpty()) filterTable(value);
        });

        btnAll.addActionListener(e -> {
            txtSearch.setText("");
            loadTable(service.getAll());
        });

        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnBack, BorderLayout.SOUTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void filterTable(String value) {
        List<Product> all = service.getAll();
        List<Product> filtered = new ArrayList<>();

        // ตรวจรูปแบบจากจำนวน "/" ที่กรอก
        int slashCount = value.length() - value.replace("/", "").length();

        if (slashCount == 2) {
            // วัน: d/M/yyyy
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d/M/yyyy");
            for (Product p : all) {
                if (p.getImportedAt() != null &&
                        p.getImportedAt().format(fmt).equals(value)) {
                    filtered.add(p);
                }
            }
        } else if (slashCount == 1) {
            // เดือน: M/yyyy
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M/yyyy");
            for (Product p : all) {
                if (p.getImportedAt() != null &&
                        p.getImportedAt().format(fmt).equals(value)) {
                    filtered.add(p);
                }
            }
        } else if (slashCount == 0) {
            // ปี: yyyy
            for (Product p : all) {
                if (p.getImportedAt() != null &&
                        String.valueOf(p.getImportedAt().getYear()).equals(value)) {
                    filtered.add(p);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "รูปแบบไม่ถูกต้อง\nวัน: 11/3/2026\nเดือน: 3/2026\nปี: 2026",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadTable(filtered);
        if (filtered.isEmpty())
            JOptionPane.showMessageDialog(this, "ไม่พบข้อมูล: " + value);
    }

    private void loadTable(List<Product> list) {
        tableModel.setRowCount(0);
        for (Product p : list) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    String.format("%.2f", p.getPrice()),
                    String.format("%.2f", p.getCostPrice()),
                    p.getStock(),
                    p.getImportedAt() != null ? p.getImportedAt().format(FORMATTER) : "-"
            });
        }
    }
}