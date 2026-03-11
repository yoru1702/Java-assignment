package ui;

import util.CSVUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class HistoryUI extends JFrame {

    public HistoryUI() {

        setTitle("ประวัติการขาย");
        setSize(700, 400);

        DefaultTableModel model =
                new DefaultTableModel(
                        new String[]{"เวลา", "ID", "ชื่อ", "Qty", "ราคา", "รวม"}, 0);

        JTable table = new JTable(model);

        for (String[] row : CSVUtil.read("sales.csv")) {
            model.addRow(row);
        }

        JButton btnBack = new JButton("ย้อนกลับ");

        btnBack.addActionListener(e -> {
            new MainMenuUI();
            dispose();
        });

        add(new JScrollPane(table));
        add(btnBack, BorderLayout.SOUTH);

        setVisible(true);
    }
}
