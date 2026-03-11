package ui;

import util.CSVUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ReportUI extends JFrame {

    private DefaultTableModel model;
    private JLabel lblTotal = new JLabel("0.00");
    private JTable table;

    public ReportUI() {
        setTitle("📊 รายงานสรุปยอดขาย");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- TOP: Header & Filter Buttons ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(44, 62, 80));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Dashboard รายงานยอดขาย");
        title.setFont(new Font("Tahoma", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        topPanel.add(title, BorderLayout.NORTH);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterBar.setOpaque(false);
        
        String[] filters = {"ทั้งหมด", "วันนี้", "สัปดาห์นี้", "เดือนนี้", "ปีนี้"};
        for (String f : filters) {
            JButton btn = new JButton(f);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> refreshReport(f));
            filterBar.add(btn);
        }
        topPanel.add(filterBar, BorderLayout.SOUTH);

        // --- CENTER: Data Table ---
        String[] columns = {"วันที่-เวลา", "รหัสสินค้า", "ชื่อสินค้า", "จำนวน", "ราคา", "รวมเงิน"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Tahoma", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- BOTTOM: Summary & Back Button ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        bottomPanel.setBackground(new Color(236, 240, 241));

        JPanel summaryBox = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryBox.setOpaque(false);
        JLabel lblText = new JLabel("รวมยอดขายสุทธิ: ");
        lblText.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblTotal.setFont(new Font("Tahoma", Font.BOLD, 32));
        lblTotal.setForeground(new Color(39, 174, 96));
        
        summaryBox.add(lblText);
        summaryBox.add(lblTotal);
        summaryBox.add(new JLabel(" บาท"));

        JButton btnBack = new JButton("⬅ กลับหน้าหลัก");
        btnBack.setPreferredSize(new Dimension(130, 40));
        btnBack.addActionListener(e -> {
            new MainMenuUI();
            dispose();
        });

        bottomPanel.add(btnBack, BorderLayout.WEST);
        bottomPanel.add(summaryBox, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // โหลดข้อมูลครั้งแรก
        refreshReport("ทั้งหมด");
        setVisible(true);
    }

    private void refreshReport(String filterType) {
        model.setRowCount(0);
        double sum = 0;
        List<String[]> data = CSVUtil.read("sales.csv");

        LocalDate now = LocalDate.now(); // วันนี้ (ค.ศ.)
        // ข้อมูลใน CSV เป็น พ.ศ. เช่น 2569-03-11
        // เราจะใช้ความต่าง 543 ปีในการเปรียบเทียบ

        for (String[] row : data) {
            try {
                // row[0] = "2569-03-11 15:46"
                String dateStr = row[0].split(" ")[0]; // "2569-03-11"
                String[] parts = dateStr.split("-");
                int yearBE = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                
                LocalDate saleDateCE = LocalDate.of(yearBE - 543, month, day);
                boolean isMatch = false;

                switch (filterType) {
                    case "ทั้งหมด": isMatch = true; break;
                    case "วันนี้": 
                        if (saleDateCE.isEqual(now)) isMatch = true;
                        break;
                    case "สัปดาห์นี้":
                        long daysBetween = ChronoUnit.DAYS.between(saleDateCE, now);
                        if (daysBetween >= 0 && daysBetween <= 7) isMatch = true;
                        break;
                    case "เดือนนี้":
                        if (saleDateCE.getMonth() == now.getMonth() && saleDateCE.getYear() == now.getYear()) isMatch = true;
                        break;
                    case "ปีนี้":
                        if (saleDateCE.getYear() == now.getYear()) isMatch = true;
                        break;
                }

                if (isMatch) {
                    model.addRow(row);
                    sum += Double.parseDouble(row[5]);
                }
            } catch (Exception ex) {
                // ข้ามบรรทัดที่ header หรือข้อมูลผิดพลาด
            }
        }
        lblTotal.setText(String.format("%,.2f", sum));
    }
}