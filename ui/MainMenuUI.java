package ui;

import javax.swing.*;

import service.ProductService;

import java.awt.*;

public class MainMenuUI extends JFrame {
    private ProductService productService = new ProductService();
    private ReportUI reportUI = new ReportUI();

    public MainMenuUI() {

        setTitle("MiniMart - Dashboard");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        // ==========================================
        // 1. SIDEBAR (เมนูทางซ้าย)
        // ==========================================
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, 700));
        sidebar.setBackground(new Color(44, 62, 80)); // สี Dark Slate
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // หัวข้อ Sidebar
        JLabel lblLogo = new JLabel("🛒 POS SYSTEM");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Tahoma", Font.BOLD, 22));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblLogo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40))); // ระยะห่าง

        // สร้างปุ่มเมนู
        JButton btnAdd = createMenuButton("เพิ่มสินค้าใหม่");
        JButton btnSell = createMenuButton("ขายสินค้า (POS)");
        JButton btnList = createMenuButton("คลังสินค้า");
        JButton btnHistory = createMenuButton("ประวัติการขาย");
        JButton btnReport = createMenuButton("สรุปยอดขาย");
        JButton btnExit = createMenuButton("ปิดโปรแกรม");

        sidebar.add(btnSell);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnAdd);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnList);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnHistory);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnReport);
        sidebar.add(Box.createVerticalGlue()); // ดันปุ่มล่างสุดลงไป
        sidebar.add(btnExit);

        // ==========================================
        // 2. MAIN CONTENT (พื้นที่แสดง Dashboard)
        // ==========================================
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(236, 240, 241));

        // Header ของ Content
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(Color.WHITE);
        JLabel lblWelcome = new JLabel("ยินดีต้อนรับสู่ระบบจัดการร้านค้า");
        lblWelcome.setFont(new Font("Tahoma", Font.BOLD, 18));
        header.add(lblWelcome);
        mainContent.add(header, BorderLayout.NORTH);

        // Dashboard Cards ()
        JPanel dashboardBody = new JPanel(new GridLayout(1, 3, 20, 20));
        dashboardBody.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        dashboardBody.setOpaque(false);

        dashboardBody
                .add(createStatCard("จำนวนสินค้าในคลัง", getTotalProduct() + " รายการ", "📦", new Color(52, 152, 219)));
        dashboardBody.add(createStatCard("ยอดขายวันนี้", String.format("%,.2f ฿", getToDaySale()), "💰",
                new Color(46, 204, 113)));
        dashboardBody.add(
                createStatCard("ยอดขายสะสม", String.format("%,.2f ฿", getTotalSale()), "📈", new Color(155, 89, 182)));

        mainContent.add(dashboardBody, BorderLayout.CENTER);

        // ==========================================
        // 3. ASSEMBLY & ACTIONS
        // ==========================================
        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);

        // Event Listeners
        btnSell.addActionListener(e -> {
            new SellUI();
            dispose();
        });
        btnList.addActionListener(e -> {
            new ProductListUI();
            dispose();
        });
        btnAdd.addActionListener(e -> {
            new ProductFormUI(null);
            dispose();
        });
        btnHistory.addActionListener(e -> {
            new HistoryUI();
            dispose();
        });
        btnReport.addActionListener(e -> {
            new ReportUI();
            dispose();
        });
        btnExit.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(230, 45));
        btn.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btn.setFocusPainted(false);
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

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

    private JPanel createStatCard(String title, String value, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        // ใส่เส้นขอบนุ่มๆ และเน้นแถบสีด้านซ้าย
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1),BorderFactory.createMatteBorder(0, 10, 0, 0, color)));

        // ส่วนของ Text (Title & Value)
        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblTitle.setForeground(new Color(127, 140, 141)); // สีเทาเข้ม

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Tahoma", Font.BOLD, 24));
        lblValue.setForeground(new Color(44, 62, 80)); // สีเกือบดำ

        textPanel.add(lblTitle);
        textPanel.add(lblValue);

        // ส่วนของ Icon ด้านขวา
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 45)); // ใช้ Font ที่รองรับ Emoji
        lblIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        lblIcon.setOpaque(false);
        lblIcon.setForeground(color.brighter());

        card.add(textPanel, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }

    private int getTotalProduct() { // ดึงรายการสินค้ามาทั้งหมด
        return productService.getAll().size();
    }

    private double getTotalSale() {
        double total = 0;
        java.util.List<String[]> data = util.CSVUtil.read("sales.csv");

        for (String[] row : data) {
            total += Double.parseDouble(row[5]);
        }
        return total;
    }

    private double getToDaySale() {
        double total = 0;
        java.util.List<String[]> data = util.CSVUtil.read("sales.csv");
        java.time.LocalDate now = java.time.LocalDate.now();
        for (String[] row : data) {
            String dateStr = row[0].split(" ")[0];
            String[] parts = dateStr.split("-");

            int yearBE = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            java.time.LocalDate saleDateCE = java.time.LocalDate.of(yearBE - 543, month, day);
            if (saleDateCE.isEqual(now)) {
                total += Double.parseDouble(row[5]); // บวกยอดรวมจากคอลัมน์ index 5
            }
        }
        return total;
    }

}