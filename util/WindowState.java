package util;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class WindowState {
    public static int width = 400;
    public static int height = 500;
    public static int x = -1;
    public static int y = -1;

    // เรียกใน constructor ของทุก UI แค่บรรทัดเดียว
    public static void track(JFrame frame) {
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                x = frame.getX();
                y = frame.getY();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                width = frame.getWidth();
                height = frame.getHeight();
            }
        });
    }
}