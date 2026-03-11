package ui;

import util.CSVUtil;

import javax.swing.*;
import java.awt.*;

public class ReportUI extends JFrame {

    public ReportUI() {

        setTitle("รายงานยอดขาย");
        setSize(400, 200);

        double sum = 0;

        for (String[] row : CSVUtil.read("sales.csv")) {
            sum += Double.parseDouble(row[5]);
        }

        JLabel lbl = new JLabel("ยอดขายรวมทั้งหมด = " + sum);

        JButton btnBack = new JButton("ย้อนกลับ");

        btnBack.addActionListener(e -> {
            new MainMenuUI();
            dispose();
        });

        setLayout(new BorderLayout());

        add(lbl, BorderLayout.CENTER);
        add(btnBack, BorderLayout.SOUTH);

        setVisible(true);
    }
}
