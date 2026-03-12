package ui;

import util.CSVUtil;
import util.WindowState;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        setTitle("รายงานสรุปยอดขาย");
        setSize(950, 650); // ปรับขนาดให้สมดุล
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE); // พื้นหลังขาวสะอาด
        setLayout(new BorderLayout());

        // --- TOP: Minimal Header ---
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(new Color(44, 62, 80));
        topPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("Dashboard รายงานยอดขาย");
        title.setFont(new Font("Tahoma", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        topPanel.add(title, BorderLayout.NORTH);

        // Filter Buttons ในรูปแบบ Minimal
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterBar.setOpaque(false);
        
        String[] filters = {"ทั้งหมด", "วันนี้", "สัปดาห์นี้", "เดือนนี้", "ปีนี้"};
        for (String f : filters) {
            JButton btn = createFilterButton(f);
            btn.addActionListener(e -> refreshReport(f));
            filterBar.add(btn);
        }
        topPanel.add(filterBar, BorderLayout.SOUTH);

        // --- CENTER: Modern Table ---
        String[] columns = {"วันที่-เวลา", "รหัสสินค้า", "ชื่อสินค้า", "จำนวน", "ราคา", "รวมเงิน"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(model);
        table.setRowHeight(35); // เพิ่มความสูงให้ดูไม่อึดอัด
        table.setShowGrid(false); // ปิดเส้นตารางแนวตั้ง
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Tahoma", Font.PLAIN, 14));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(20, 25, 10, 25));
        centerWrapper.add(scroll, BorderLayout.CENTER);

        // --- BOTTOM: Summary Card & Back ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 25, 20, 25));

        // ยอดรวมแบบ Card เล็กๆ
        JPanel summaryCard = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        summaryCard.setBackground(new Color(245, 245, 245));
        summaryCard.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        JLabel lblText = new JLabel("รวมรายได้สุทธิ:");
        lblText.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblTotal.setFont(new Font("Tahoma", Font.BOLD, 28));
        lblTotal.setForeground(new Color(39, 174, 96));
        
        summaryCard.add(lblText);
        summaryCard.add(lblTotal);
        summaryCard.add(new JLabel(" บาท"));

        JButton btnBack = new JButton("ย้อนกลับ");
        btnBack.setFont(new Font("Tahoma", Font.PLAIN, 14));
        btnBack.setPreferredSize(new Dimension(100, 40));
        btnBack.addActionListener(e -> {
            new MainMenuUI();
            dispose();
        });

        bottomPanel.add(btnBack, BorderLayout.WEST);
        bottomPanel.add(summaryCard, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshReport("ทั้งหมด");
        setVisible(true);
    }

    private JButton createFilterButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Tahoma", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(52, 73, 94));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 110, 120)),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover Effect แบบง่าย
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 152, 219));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 73, 94));
            }
        });
        return btn;
    }

    private void refreshReport(String filterType) {
        model.setRowCount(0);
        double sum = 0;
        List<String[]> data = CSVUtil.read("sales.csv");
        LocalDate now = LocalDate.now();

        for (String[] row : data) {
            try {
                String dateStr = row[0].split(" ")[0];
                String[] parts = dateStr.split("-");
                int yearCE = Integer.parseInt(parts[0]) - 543; // แปลง พ.ศ. เป็น ค.ศ.
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                
                LocalDate saleDate = LocalDate.of(yearCE, month, day);
                boolean isMatch = false;

                switch (filterType) {
                    case "ทั้งหมด": isMatch = true; break;
                    case "วันนี้": isMatch = saleDate.isEqual(now); break;
                    case "สัปดาห์นี้":
                        long daysBetween = ChronoUnit.DAYS.between(saleDate, now);
                        isMatch = (daysBetween >= 0 && daysBetween <= 7);
                        break;
                    case "เดือนนี้":
                        isMatch = (saleDate.getMonth() == now.getMonth() && saleDate.getYear() == now.getYear());
                        break;
                    case "ปีนี้":
                        isMatch = (saleDate.getYear() == now.getYear());
                        break;
                }

                if (isMatch) {
                    model.addRow(row);
                    sum += Double.parseDouble(row[5]);
                }
            } catch (Exception ex) {}
        }
        lblTotal.setText(String.format("%,.2f", sum));
    }
}