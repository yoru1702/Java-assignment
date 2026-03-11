package service;

import model.*;
import util.CSVUtil;

import java.text.SimpleDateFormat;
import java.util.*;

public class SaleService {

    private final String FILE = "sales.csv";

    public void saveSale(List<CartItem> cart) {

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

            CSVUtil.append(FILE, s.toCSV());
        }
    }
}