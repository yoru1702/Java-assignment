package ui;

import model.Product;
import service.ProductService;
import util.WindowState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StockHistoryUI extends JFrame {

    private ProductService service = new ProductService();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private DefaultTableModel tableModel;
    private JLabel lblResultCount = new JLabel("แสดงทั้งหมด");

    public StockHistoryUI() {
        setTitle("ประวัติการเพิ่มและนำเข้า Stock");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 1. Header & Filter Panel ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(44, 62, 80)); // Dark Slate
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("ประวัติการนำเข้าสินค้า");
        title.setFont(new Font("Tahoma", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        topPanel.add(title, BorderLayout.WEST);

        // ส่วนค้นหา (Right Side of Header)
        JPanel searchBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchBox.setOpaque(false);
        
        JTextField txtSearch = new JTextField(12);
        txtSearch.setPreferredSize(new Dimension(150, 30));
        txtSearch.setToolTipText("ตัวอย่าง: 11/3/2026 หรือ 3/2026 หรือ 2026");

        JButton btnSearch = createIconButton("ค้นหา", new Color(52, 152, 219));
        JButton btnAll = createIconButton("ทั้งหมด", new Color(149, 165, 166));

        searchBox.add(new JLabel("กรองวันที่: "));
        ((JLabel)searchBox.getComponent(0)).setForeground(Color.WHITE);
        searchBox.add(txtSearch);
        searchBox.add(btnSearch);
        searchBox.add(btnAll);
        
        topPanel.add(searchBox, BorderLayout.EAST);

        // --- 2. Table Section ---
        String[] columns = {"รหัสสินค้า", "ชื่อสินค้า", "ราคาขาย", "ราคาทุน", "Stock ล่าสุด", "เวลานำเข้าล่าสุด"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Tahoma", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        // --- 3. Bottom Panel ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 20, 15, 20));

        lblResultCount.setFont(new Font("Tahoma", Font.ITALIC, 13));
        bottomPanel.add(lblResultCount, BorderLayout.WEST);

        JButton btnBack = new JButton("กลับหน้าหลัก");
        btnBack.setPreferredSize(new Dimension(140, 40));
        btnBack.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnBack.addActionListener(e -> {
            new MainMenuUI();
            dispose();
        });
        bottomPanel.add(btnBack, BorderLayout.EAST);

        // --- 4. Logic & Actions ---
        btnSearch.addActionListener(e -> {
            String val = txtSearch.getText().trim();
            if (!val.isEmpty()) filterTable(val);
        });

        txtSearch.addActionListener(e -> btnSearch.doClick());

        btnAll.addActionListener(e -> {
            txtSearch.setText("");
            loadTable(service.getAll());
            lblResultCount.setText("แสดงรายการทั้งหมด");
        });

        // Assembly
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadTable(service.getAll());
        setVisible(true);
    }

    private void filterTable(String value) {
        List<Product> all = service.getAll();
        List<Product> filtered = new ArrayList<>();
        int slashCount = value.length() - value.replace("/", "").length();

        try {
            if (slashCount == 2) { // d/M/yyyy
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d/M/yyyy");
                for (Product p : all) {
                    if (p.getImportedAt() != null && p.getImportedAt().format(fmt).equals(value)) filtered.add(p);
                }
            } else if (slashCount == 1) { // M/yyyy
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("M/yyyy");
                for (Product p : all) {
                    if (p.getImportedAt() != null && p.getImportedAt().format(fmt).equals(value)) filtered.add(p);
                }
            } else { // yyyy
                for (Product p : all) {
                    if (p.getImportedAt() != null && String.valueOf(p.getImportedAt().getYear()).equals(value)) filtered.add(p);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "รูปแบบวันที่ไม่ถูกต้อง", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadTable(filtered);
        lblResultCount.setText("พบข้อมูลทั้งหมด " + filtered.size() + " รายการ จากการค้นหา: " + value);
    }

    private void loadTable(List<Product> list) {
        tableModel.setRowCount(0);
        for (Product p : list) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(),
                    String.format("%,.2f", p.getPrice()),
                    String.format("%,.2f", p.getCostPrice()),
                    p.getStock(),
                    p.getImportedAt() != null ? p.getImportedAt().format(FORMATTER) : "-"
            });
        }
    }

    private JButton createIconButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}