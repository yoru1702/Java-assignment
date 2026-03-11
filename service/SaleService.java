package service;

import model.*;
import util.CSVUtil;

import java.text.SimpleDateFormat;
import java.util.*;

public class SaleService {

    private final String FILE = "sales.csv";

    public void saveSale(List<CartItem> cart) {

        List<String> lines = new ArrayList<>();
        lines.add("datetime,productId,name,qty,price,total");

        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm")
                .format(new Date());

        for (CartItem item : cart) {
            SaleRecord s = new SaleRecord(
                    time,
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQty(),
                    item.getProduct().getPrice(),
                    item.getTotal()
            );

            lines.add(s.toCSV());
        }

        CSVUtil.write(FILE, lines);
    }
}
