package service;

import model.Product;
import util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ProductService {

    private final String FILE = "products.csv";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();

        for (String[] row : CSVUtil.read(FILE)) {
            
            if (row.length < 6) continue; // ข้าม row ที่ข้อมูลไม่ครบ

            LocalDateTime importedAt = null;
            try {
                importedAt = LocalDateTime.parse(row[5], FORMATTER);
            } catch (Exception ignored) {}

            list.add(new Product(
                     row[0],
                    row[1],
                    Double.parseDouble(row[2]),  // price
                    Double.parseDouble(row[3]),  // costPrice
                    Integer.parseInt(row[4]),    // stock
                    importedAt                   // row[5]                  // เวลานำเข้า
            ));
        }
        return list;
    }

    public Product findById(String id) {
        for (Product p : getAll()) {
            if (p.getId().equals(id))
                return p;
        }
        return null;
    }

    public void saveAll(List<Product> list) {
        List<String> lines = new ArrayList<>();
        lines.add("id,name,price,costPrice,stock,importedAt"); // header ใหม่

        for (Product p : list) {
            lines.add(p.toCSV());
        }

        CSVUtil.write(FILE, lines);
    }
}